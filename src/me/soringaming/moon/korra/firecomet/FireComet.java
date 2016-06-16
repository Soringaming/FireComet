package me.soringaming.moon.korra.firecomet;
 
import java.util.logging.Level;
 
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.util.Vector;
 
import com.projectkorra.projectkorra.ProjectKorra;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.ability.FireAbility;
import com.projectkorra.projectkorra.util.ParticleEffect;
 
public class FireComet extends FireAbility implements AddonAbility{
   
    private Player player;
    private double r;
    private double t;
    private Location start;
    private Location loc;
    private Vector dir;
    private Permission perm;
   
    public FireComet(Player player) {
        super(player);
        this.player = player;
        this.loc = player.getLocation();
        this.dir = player.getEyeLocation().getDirection().normalize().multiply(1);
        this.start = player.getLocation();
        this.t = 0;
        this.r = 1.5;
       
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
       
    }
   
    @Override
    public String getDescription() {
        return getVersion() + " Developed By " + getAuthor() + ":\nA Test Ability";
       
    }
   
    public void doChargeParicles() {
        t = t + Math.PI / 32;
        double x = r * Math.sin(t);
        double y = 0;
        double z = r * Math.cos(t);
        ParticleEffect.FLAME.display(player.getLocation(), 0.1F, 0.1F, 0.1F, 0.05F, 2);
       
    }
 
    @Override
    public String getAuthor() {
        return "Soringaming & Moon243"; //Moon was here :P
    }
 
    @Override
    public String getVersion() {
        return "v1.0";
    }
 
    @Override
    public void load() {
        ProjectKorra.plugin.getServer().getLogger().log(Level.INFO, getName() + " " + getVersion() + " Developed By: " + getAuthor() + " Has Been Enabled");
        ProjectKorra.plugin.getServer().getPluginManager().registerEvents(new FireCometListener(), ProjectKorra.plugin);   
        perm = new Permission("bending.ability.FireComet");
        perm.setDefault(PermissionDefault.TRUE);
        ProjectKorra.plugin.getServer().getPluginManager().addPermission(perm);
    }
 
    @Override
    public void stop() {
        ProjectKorra.plugin.getServer().getLogger().log(Level.INFO, getName() + " " + getVersion() + " Developed By: " + getAuthor() + " Has Been Disabled");
        ProjectKorra.plugin.getServer().getPluginManager().removePermission(perm);
        super.remove();
    }
 
}