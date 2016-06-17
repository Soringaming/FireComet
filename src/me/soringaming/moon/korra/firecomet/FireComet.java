package me.soringaming.moon.korra.firecomet;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.Vector;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.Ability;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.util.DamageHandler;
import com.projectkorra.projectkorra.util.ParticleEffect;
import com.projectkorra.projectkorra.util.TempBlock;

public class FireComet extends FireAbility implements AddonAbility {

	private Player player;
	private double r;
	private double t;
	private Location CurrentPLoc;
	private Location start;
	private Location loc;
	private Vector dir;
	private Permission perm;
	BendingPlayer bp;
	private boolean Charged;
	private long chargeTime;
	private long startTime;
	private double particleHeight;
	private Location loc2;

	private static final ConcurrentHashMap<Entity, Entity> instances = new ConcurrentHashMap<Entity, Entity>();

	public FireComet(Player player) {
		super(player);
		this.player = player;
		bp = BendingPlayer.getBendingPlayer(player.getName());
		this.loc = player.getLocation();
		this.dir = player.getEyeLocation().getDirection().normalize().multiply(1);
		this.start = player.getLocation();
		this.startTime = System.currentTimeMillis();
		this.chargeTime = 5000;
		this.t = 0;
		this.r = 1.5;
		start();

	}

	@Override
	public long getCooldown() {
		return 0;
	}

	@Override
	public Location getLocation() {
		return loc;
	}

	@Override
	public String getName() {
		return "FireComet";
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return true;
	}

	@Override
	public void progress() {
		this.dir = player.getLocation().getDirection().normalize().multiply(1.5).add(new Vector(0, -0.2, 0));
		for (Entity e : GeneralMethods.getEntitiesAroundPoint(loc, 1)) {
			if (e.getEntityId() == player.getEntityId()) {
				instances.put(e, e);
			}
		}
		if (player.isDead() || !player.isOnline()) {
			this.start = player.getEyeLocation();
			remove();
			return;

		}
		if (!Charged) {
			if (!player.isSneaking() && startTime + chargeTime > System.currentTimeMillis()) {
				remove();
				return;
			}
			if (bp.isAvatarState()) {
				Charged = true;
			}
			if (!player.isSneaking() && startTime + chargeTime < System.currentTimeMillis()) {
				start = player.getLocation().add(new Vector(0, 6, 0));
				loc = player.getEyeLocation().add(new Vector(0, 6, 0));
				loc2 = player.getEyeLocation();
				Charged = true;
			}
			if (player.isSneaking() && startTime + chargeTime > System.currentTimeMillis()) {
				doChargeParticles();
				start = player.getLocation().add(new Vector(0, 6, 0));
				loc = player.getEyeLocation().add(new Vector(0, 6, 0));
				loc2 = player.getEyeLocation();
			}
			if (player.isSneaking() && startTime + chargeTime < System.currentTimeMillis()) {
				doPlayerChargedParticles();
				doBallChargedParticles();

			}
		} else {
			bp.addCooldown((Ability) this);
			loc.add(dir);
			loc2.add(dir);
			doBallThrowParticles();

			if (GeneralMethods.isSolid(loc.getBlock())) {
				doExplosion();
			}
			if (isWater(loc.getBlock())) {
				doExplosion();
			}
			if (loc.distance(start) > 40) {
				doExplosion();
				remove();
				return;
			}

			for (Entity e : GeneralMethods.getEntitiesAroundPoint(loc, 3)) {
				if (bp.isAvatarState()) {
					if (e instanceof LivingEntity && e.getEntityId() != player.getEntityId()) {
						DamageHandler.damageEntity(e, 40, this);
					} else {
						if (e instanceof LivingEntity && e.getEntityId() != player.getEntityId()) {
							DamageHandler.damageEntity(e, 8, this);
						}
					}
					if (player.isSneaking()) {
						this.player.getEyeLocation();
						this.dir = player.getLocation().getDirection().normalize().multiply(1.1);
						this.start = player.getEyeLocation();
					}

				}
			}

		}

	}

	@Override
	public String getDescription() {
		return getVersion() + " Developed By " + getAuthor()
				+ ":\nHold Shift Until You See The Comet Form Infront Of You. As You Are Holding Shift, Fire Will Surround You. Doing Damage To Any Entity That Comes In Contact With It. The Comet Will Destroy Any Block It Comes In Contact With (Except Bedrock and Barriers) You Can Enable In The Config If The Explosions Do Tile Drops, Or If They Regenerate. It Also Sets Lava As It Goes Into Stone. ";
	}

	public void doChargeParticles() {
		t = t + Math.PI / 8;
		CurrentPLoc = player.getLocation();
		double x = r * Math.sin(t);
		double y = 0.3;
		double z = r * Math.cos(t);
		CurrentPLoc.add(x, y, z);
		ParticleEffect.FLAME.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0.2F, 10);
		CurrentPLoc.subtract(x, y, z);

		double x2 = r * Math.sin(t);
		double y2 = 0.3;
		double z2 = r * Math.cos(t);
		CurrentPLoc.add(x2, y2, z2);
		ParticleEffect.FLAME.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0.2F, 10);
		CurrentPLoc.subtract(x2, y2, z2);
		for (Entity e : GeneralMethods.getEntitiesAroundPoint(loc, 3.5)) {
			if (e instanceof LivingEntity && e.getEntityId() != player.getEntityId()) {
				DamageHandler.damageEntity(e, 4, this);
				e.setFireTicks(1000);
			}
		}
	}

	public void doPlayerChargedParticles() {
		if (r != 1.5) {
			r = 1.5;
		}
		t = t + Math.PI / 8;
		CurrentPLoc = player.getLocation();
		double x = r * Math.sin(t);
		double y = particleHeight;
		double z = r * Math.cos(t);
		CurrentPLoc.add(x, y, z);
		ParticleEffect.FLAME.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0.2F, 10);
		ParticleEffect.SMOKE.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0.2F, 10);
		ParticleEffect.LAVA.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0.2F, 1);
		CurrentPLoc.subtract(x, y, z);

		double x2 = r * Math.cos(t);
		double y2 = particleHeight;
		double z2 = r * Math.sin(t);
		CurrentPLoc.add(x2, y2, z2);
		ParticleEffect.FLAME.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0.2F, 50);
		ParticleEffect.SMOKE.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0.2F, 50);
		ParticleEffect.LAVA.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0.2F, 1);
		CurrentPLoc.subtract(x2, y2, z2);
	}

	public void doBallChargedParticles() {
		Location Currentloc = GeneralMethods.getTargetedLocation(player, 10).add(new Vector(0, 6, 0));
		loc = GeneralMethods.getTargetedLocation(player, 10).add(new Vector(0, 6, 0));
		t = t + Math.PI / 64;
		ParticleEffect.FLAME.display(loc, 1.5F, 1.5F, 1.5F, 0F, 50);
		double r2 = 3;
		double x = r2 * Math.cos(t);
		double y = r2 * Math.sin(t);
		double z = r2 * Math.sin(t);
		Currentloc.add(x, y, z);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.subtract(x, y, z);

		double x2 = r2 * Math.cos(t);
		double y2 = r2 * Math.cos(t);
		double z2 = r2 * Math.sin(t);
		Currentloc.subtract(x2, y2, z2);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.add(x2, y2, z2);

		double x3 = r2 * Math.cos(t);
		double y3 = 0;
		double z3 = r2 * Math.sin(t);
		Currentloc.subtract(x3, y3, z3);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.add(x3, y3, z3);

		// Opposite rotating particles
		double x4 = r2 * Math.sin(t);
		double y4 = r2 * Math.sin(t);
		double z4 = r2 * Math.cos(t);
		Currentloc.add(x4, y4, z4);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.subtract(x4, y4, z4);

		double x5 = r2 * Math.sin(t);
		double y5 = r2 * Math.cos(t);
		double z5 = r2 * Math.cos(t);
		Currentloc.subtract(x5, y5, z5);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.add(x5, y5, z5);

		double x6 = r2 * Math.sin(t);
		double y6 = 0;
		double z6 = r2 * Math.cos(t);
		Currentloc.subtract(x6, y6, z6);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.add(x6, y6, z6);

	}

	public void doBallThrowParticles() {
		Location Currentloc = loc;
		t = t + Math.PI / 2;
		ParticleEffect.FLAME.display(loc, 1.5F, 1.5F, 1.5F, 0.0005F, 50);
		double r2 = 3;
		double x = r2 * Math.cos(t);
		double y = r2 * Math.sin(t);
		double z = r2 * Math.sin(t);
		Currentloc.add(x, y, z);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.subtract(x, y, z);

		double x2 = r2 * Math.cos(t);
		double y2 = r2 * Math.cos(t);
		double z2 = r2 * Math.sin(t);
		Currentloc.subtract(x2, y2, z2);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.add(x2, y2, z2);

		double x3 = r2 * Math.cos(t);
		double y3 = 0;
		double z3 = r2 * Math.sin(t);
		Currentloc.subtract(x3, y3, z3);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.add(x3, y3, z3);

		// Opposite rotating particles
		double x4 = r2 * Math.sin(t);
		double y4 = r2 * Math.sin(t);
		double z4 = r2 * Math.cos(t);
		Currentloc.add(x4, y4, z4);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.subtract(x4, y4, z4);

		double x5 = r2 * Math.sin(t);
		double y5 = r2 * Math.cos(t);
		double z5 = r2 * Math.cos(t);
		Currentloc.subtract(x5, y5, z5);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.add(x5, y5, z5);

		double x6 = r2 * Math.sin(t);
		double y6 = 0;
		double z6 = r2 * Math.cos(t);
		Currentloc.subtract(x6, y6, z6);
		ParticleEffect.SMOKE.display(Currentloc, 0.01F, 0.01F, 0.01F, 0.1F, 40);
		Currentloc.add(x6, y6, z6);
	}

	@SuppressWarnings("deprecation")
	private void doExplosion() {
		ParticleEffect.FLAME.display(loc, 0.1F, 0.1F, 0.1F, 1F, 300);
		ParticleEffect.SMOKE.display(loc, 0.1F, 0.1F, 0.1F, 1.5F, 250);
		ParticleEffect.LARGE_EXPLODE.display(loc, 0.1F, 0.1F, 0.1F, 1.5F, 15);
		ParticleEffect.BLOCK_CRACK.display(
				(ParticleEffect.ParticleData) new ParticleEffect.BlockData(Material.FIRE, (byte) 0), 0.5F, 0.5F, 0.5F,
				.0F, 400, loc, 200);
		player.getWorld().playSound(loc, Sound.EXPLODE, 10, 1);
		for (Block b : GeneralMethods.getBlocksAroundPoint(loc, 3.5)) {
			if (b.getType() != Material.BEDROCK && b.getType() != Material.BARRIER && b.getType() != Material.OBSIDIAN && b.getType() != Material.ENDER_PORTAL_FRAME && b.getType() != Material.ENDER_PORTAL) {
				if (new Random().nextInt(30) == 1) {
					if (new Random().nextInt(100) == 1) {
						if (b.getType() == Material.STONE) {
							new TempBlock(b, Material.LAVA, (byte) 1);

						}
					}
				}
				if (b.getType() == Material.WATER) {
					b.setType(Material.AIR);
					ParticleEffect.CLOUD.display(loc, 0F, 0.5F, 0F, 0.01F, 100);
				}
				if (new Random().nextInt(15) == 1) {
					if (b.getType() != Material.LAVA && b.getType() != Material.WATER && b.getType() != Material.AIR) {
						float x = (float) -2 + (float) (Math.random() * ((2 - -2) + 1));
						float y = (float) -3 + (float) (Math.random() * ((3 - -3) + 1));
						float z = (float) -2 + (float) (Math.random() * ((2 - -2) + 1));

						FallingBlock fb = b.getWorld().spawnFallingBlock(b.getLocation(), b.getType(), b.getData());
						FallingBlock fb2 = b.getWorld().spawnFallingBlock(b.getLocation(), Material.FIRE, (byte) 0);
						fb.setDropItem(false);
						fb.setVelocity(new Vector(x, y, z));
						fb2.setDropItem(false);
						fb2.setVelocity(new Vector(x, y, z));
						if(new Random().nextInt(2) == 1) {
							FallingBlock fb3 = b.getWorld().spawnFallingBlock(b.getLocation(), Material.COAL_BLOCK, (byte) 0);
							fb3.setDropItem(false);
							fb3.setVelocity(new Vector(x, y, z));
						}
					}
				}
				if (b.getType() != Material.LAVA) {
					b.breakNaturally();
				}
			}
		}
	}

	@EventHandler
	private void stopFireDamage(EntityDamageEvent e) {
		if (instances.contains(e.getEntity())) {
			if (e.getCause() == DamageCause.FIRE || e.getCause() == DamageCause.FIRE_TICK) {
				e.setCancelled(true);
			}
		}
	}

	@Override
	public String getAuthor() {
		return "Soringaming & Moon243"; // Moon was here :P
	}

	@Override
	public String getVersion() {
		return "v1.0";
	}

	@Override
	public void load() {
		ProjectKorra.plugin.getServer().getLogger().log(Level.INFO,
				getName() + " " + getVersion() + " Developed By: " + getAuthor() + " Has Been Enabled");
		ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new FireCometListener(), ProjectKorra.plugin);
		perm = new Permission("bending.ability.FireComet");
		perm.setDefault(PermissionDefault.TRUE);
		ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
	}

	@Override
	public void stop() {
		ProjectKorra.plugin.getServer().getLogger().log(Level.INFO,
				getName() + " " + getVersion() + " Developed By: " + getAuthor() + " Has Been Disabled");
		ProjectKorra.plugin.getServer().getPluginManager().removePermission(perm);
		super.remove();
	}

}