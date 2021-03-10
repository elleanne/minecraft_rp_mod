package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.World;


public class MinecraftRP extends JavaPlugin  implements Listener {
    @Override
    public void onEnable() {
        super.getLogger().info("MinecraftRP plugin enabled.");
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        Bukkit.getLogger().info("MinecraftRP plugin disabled.");
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    /*
    Disables normal health regeneration.
    With food, players' health will only regen to 50% of their max health. To heal completely, you'll have
    to visit a doctor and get healed by him/her (no sexism here).
    */
    public void onJoin(EntityRegainHealthEvent event) {
        Player player = (Player) event.getEntity();
        event.setCancelled(true);
        if ((Math.round(player.getHealth() * 100.0) / 100.0) >10 ) return;
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
            public void run() {
                player.sendMessage("Health "+ (Math.round(player.getHealth() * 100.0) / 100.0) );
                if (((Math.round(player.getHealth() * 100.0) / 100.0) < 10) && player.getHealth()>0)
                    player.setHealth(player.getHealth() + 0.5);
            }
        }, 140L);
        return;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        switch(commandLabel){
            case "jail": {
                Policeman policeman = new Policeman(sender);
                boolean res = policeman.jail(args);
                return res;
            }

            case "me": {
                allPlayers player = new allPlayers(sender);
                boolean res = player.describeAction(args);
                return res;
            }

            case "it": {
                allPlayers player = new allPlayers(sender);
                boolean res = player.describeEvent(args);
                return res;
            }

            case "roll": {
                allPlayers player = new allPlayers(sender);
                boolean res = player.rollDice();
                return res;
            }

        }

        return true;
    }

}
