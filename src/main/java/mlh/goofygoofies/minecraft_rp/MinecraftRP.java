package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;


public class MinecraftRP extends JavaPlugin  implements Listener {
    Map<Integer, String> playersJobsList = new HashMap<>();

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
    public void healthRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity()  instanceof Player)) return;
        Player player = (Player) event.getEntity();
        event.setCancelled(true);
        if ((Math.round(player.getHealth() * 100.0) / 100.0) > 10) return;
        Bukkit.getScheduler().scheduleAsyncDelayedTask(this, new Runnable() {
            public void run() {
                if (((Math.round(player.getHealth() * 100.0) / 100.0) < 10) && player.getHealth() > 0)
                    player.setHealth(player.getHealth() + 0.5);
            }
        }, 140L);
        return;
    }


    /* Assign default job 'Citizen' to all new players */
    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        playersJobsList.put(event.getPlayer().getEntityId(), "Civilian");
        event.getPlayer().sendMessage(ChatColor.BLUE + "You are now a civilian");
    }

    /* Deletes the stored job of a player when he disconnects */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        playersJobsList.remove(event.getPlayer().getEntityId());
    }


    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        switch (commandLabel) {

            // Commands for policemen

            case "jail": {
                Player player = (Player) sender;
                if (playersJobsList.get(player.getEntityId()) != "Policeman"){
                    sender.sendMessage(ChatColor.RED + "You do not have the rights to use this command");
                    return true;
                }
                Policeman policeman = new Policeman(sender);
                boolean res = policeman.jail(args);
                return res;
            }

            case "inspect": {
                Player player = (Player) sender;
                if (playersJobsList.get(player.getEntityId()) != "Policeman"){
                    sender.sendMessage(ChatColor.RED + "You do not have the rights to use this command");
                    return true;
                }
                Policeman policeman = new Policeman(sender);
                boolean res = policeman.inspect(args);
                return res;
            }

            // Commands usable by all players

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

            case "ID": {
                allPlayers player = new allPlayers(sender);
                boolean res = player.showID();
                return res;
            }

            // Doctor commands

            case "heal": {
                Player player = (Player) sender;
                if (playersJobsList.get(player.getEntityId()) != "Doctor"){
                    sender.sendMessage(ChatColor.RED + "You do not have the rights to use this command");
                    return true;
                }
                Doctor doc = new Doctor(sender);
                boolean res = doc.heal(args);
                return res;
            }

            // Command to change your job

            case "job": {
                Jobs jobChanger = new Jobs(sender);
                boolean res = jobChanger.jobChange(args);
                if (!res) return false;
                switch (args[0]) {
                    case "Policeman": {
                        Player player = (Player) sender;
                        playersJobsList.replace(player.getEntityId(), "Policeman");
                        sender.sendMessage("You are now a policeman!");
                        return true;
                    }
                    case "Doctor": {
                        Player player = (Player) sender;
                        playersJobsList.replace(player.getEntityId(), "Doctor");
                        sender.sendMessage("You are now a doctor!");
                        return true;
                    }
                    case "Judge": {
                        Player player = (Player) sender;
                        playersJobsList.replace(player.getEntityId(), "Judge");
                        sender.sendMessage("You are now a judge!");
                        return true;
                    }
                    default:
                        sender.sendMessage("Invalid Job Name. Please use Policeman, Judge or Doctor");
                        return false;
                }
            }
        }
        return false;
    }

}
