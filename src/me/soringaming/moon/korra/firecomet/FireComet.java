package me.soringaming.moon.korra.firecomet;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
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
		this.dir = player.getLocation().getDirection().normalize().multiply(1.5);
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
			if (!player.isSneaking() && startTime + chargeTime < System.currentTimeMillis()) {
				start = player.getLocation();
				loc = player.getEyeLocation();
				loc2 = player.getEyeLocation();
				Charged = true;
			}
			if (player.isSneaking() && startTime + chargeTime > System.currentTimeMillis()) {
				doChargeParticles();
				start = player.getLocation();
				loc = player.getEyeLocation();
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
				remove();
				return;
			}
			if (isWater(loc.getBlock())) {
				remove();
				return;
			}
			if (loc.distance(start) > 40) {
				remove();
				return;
			}

			for (Entity e : GeneralMethods.getEntitiesAroundPoint(loc, 3)) {
				if (e instanceof LivingEntity && e.getEntityId() != player.getEntityId()) {
					DamageHandler.damageEntity(e, 8, this);
				}
				if (player.isSneaking()) {
					this.player.getEyeLocation();
					this.dir = player.getLocation().getDirection().normalize().multiply(1.1);
					this.start = player.getEyeLocation();
				}

			}

		}

	}

	@Override
	public String getDescription() {
		return getVersion() + " Developed By " + getAuthor() + ":\nA Test Ability";

	}

	public void doChargeParticles() {
		t = t + Math.PI / 32;
		CurrentPLoc = player.getLocation();
		if (r <= 2) {
			r += 0.5;
		} else {
			r = 0.5;
		}
		double x = r * Math.sin(t);
		double y = 0.2;
		double z = r * Math.cos(t);
		CurrentPLoc.add(x, y, z);
		ParticleEffect.FLAME.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0F, 50);
		CurrentPLoc.subtract(x, y, z);
		
		double x2 = r * Math.cos(t);
		double y2 = 0.2;
		double z2 = r * Math.sin(t);
		CurrentPLoc.add(x2, y2, z2);
		ParticleEffect.FLAME.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0F, 50);
		CurrentPLoc.subtract(x2, y2, z2);

	}

	public void doPlayerChargedParticles() {
		if (r != 1.5) {
			r = 1.5;
		}
		t = t + Math.PI / 64;
		CurrentPLoc = player.getLocation();
		double x = r * Math.sin(t);
		double y = particleHeight;
		double z = r * Math.cos(t);
		CurrentPLoc.add(x, y, z);
		ParticleEffect.FLAME.display(CurrentPLoc, 0.1F, 0.1F, 0.1F, 0.05F, 5);
		CurrentPLoc.subtract(x, y, z);
	}

	public void doBallChargedParticles() {
		Location Currentloc = GeneralMethods.getTargetedLocation(player, 10);
		loc = GeneralMethods.getTargetedLocation(player, 10);
		t = t + Math.PI / 24;
		ParticleEffect.FLAME.display(loc, 2F, 2F, 2F, 0.0005F, 150);
		ParticleEffect.LAVA.display(loc, 2F, 2F, 2F, 0.0005F, 15);
		double r2 = 5;
		double x = r2 * Math.cos(t);
		double y = r2 * Math.sin(t);
		double z = r2 * Math.sin(t);
		Currentloc.add(x, y, z);
		ParticleEffect.SMOKE.display(Currentloc, 0.1F, 0.1F, 0.1F, 0.1F, 50);
		Currentloc.subtract(x, y, z);

		double x2 = r2 * Math.cos(t);
		double y2 = r2 * Math.cos(t);
		double z2 = r2 * Math.sin(t);
		Currentloc.subtract(x2, y2, z2);
		ParticleEffect.SMOKE.display(Currentloc, 0.1F, 0.1F, 0.1F, 0.1F, 50);
		Currentloc.add(x2, y2, z2);
	}
	
	public void doBallThrowParticles() {
		Location Currentloc = loc;
		t = t + Math.PI / 2;
		ParticleEffect.FLAME.display(loc, 0.5F, 0.5F, 0.5F, 0.0F, 50);
		double r2 = 2;
		double x = r2 * Math.cos(t);
		double y = 0;
		double z = r2 * Math.sin(t);
		Currentloc.add(x, y, z);
		ParticleEffect.SMOKE.display(Currentloc, 0.1F, 0.1F, 0.1F, 0.005F, 50);
		Currentloc.subtract(x, y, z);

		double x2 = r2 * Math.sin(t);
		double y2 = 0;
		double z2 = r2 * Math.cos(t);
		Currentloc.add(x2, y2, z2);
		ParticleEffect.SMOKE.display(Currentloc, 0.1F, 0.1F, 0.1F, 0.005F, 50);
		Currentloc.subtract(x2, y2, z2);
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