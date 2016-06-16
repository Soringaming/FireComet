package me.soringaming.moon.korra.firecomet;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.projectkorra.projectkorra.BendingPlayer;
import com.projectkorra.projectkorra.ability.CoreAbility;

public class FireCometListener implements Listener {
   
    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        Player player = e.getPlayer();
        if(player.isSneaking()) {
            return;
        }
        if(canBend(player)) {
            new FireComet(player);
        }
    }
   
    public boolean canBend(Player p) {
        BendingPlayer bp = BendingPlayer.getBendingPlayer(p.getName());
        if(bp.canBend(CoreAbility.getAbility("FireComet"))) {
            return true;
        }
        return false;
    }
 
}