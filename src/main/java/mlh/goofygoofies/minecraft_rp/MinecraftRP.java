package mlh.goofygoofies.minecraft_rp;

import net.skinsrestorer.api.SkinsRestorerAPI;
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

public class MinecraftRP extends JavaPlugin implements Listener {
    private SkinsRestorerAPI skinsRestorerAPI;
    Map<Integer, String> playersJobsList = new HashMap<>();
    LandClaims lc = new LandClaims(); // TODO: need to make singleton

    @Override
    public void onEnable() {
        // SkinsRestorer
        getLogger().info("Loading SkinsRestorer API...");
        skinsRestorerAPI = SkinsRestorerAPI.getApi();
        getLogger().info(skinsRestorerAPI.toString());

        // Commands
        getCommand("job").setExecutor(new JobsCommand(playersJobsList));
        GuardCommands gc = new GuardCommands(playersJobsList);
        getCommand("inspect").setExecutor(gc);
        getCommand("jail").setExecutor(gc);
        AllPlayersCommands ac = new AllPlayersCommands();
        getCommand("me").setExecutor(ac);
        getCommand("id").setExecutor(ac);
        getCommand("it").setExecutor(ac);
        getCommand("roll").setExecutor(ac);

        String name = lc.loadLandClaims();
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("MinecraftRP plugin enabled.");
    }

    @Override
    public void onDisable() {
        lc.saveLandClaims();
        Bukkit.getLogger().info("MinecraftRP plugin disabled.");
    }

    @SuppressWarnings("deprecation")
    @EventHandler
    /*
     * Disables normal health regeneration. With food, players' health will only
     * regen to 50% of their max health. To heal completely, you'll have to visit a
     * doctor and get healed by him/her (no sexism here).
     */
    public void healthRegen(EntityRegainHealthEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        Player player = (Player) event.getEntity();
        event.setCancelled(true);
        if ((Math.round(player.getHealth() * 100.0) / 100.0) > 10)
            return;
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
        skinsRestorerAPI.removeSkin(event.getPlayer().getName());
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        switch (commandLabel) {

        // Doctor commands

        case "heal": {
            Player player = (Player) sender;
            if (playersJobsList.get(player.getEntityId()) != "Doctor") {
                sender.sendMessage(ChatColor.RED + "You do not have the rights to use this command");
                return true;
            }
            Doctor doc = new Doctor(sender);
            boolean res = doc.heal(args);
            return res;
        }
        }
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }
        
        if (cmd.getName().equalsIgnoreCase("claim") && player != null) { // claim land
            return lc.setClaim(player);
        } else if (cmd.getName().equalsIgnoreCase("unclaim") && player != null) { // unclaim land
            return lc.unclaim(player);
        } else if (cmd.getName().equalsIgnoreCase("selfheal") && player != null) { // self heal this player when on land
                                                                                   // owned by this player
            if (lc.getClaim(player)) {
                double health = player.getHealth();
                if (health < (player.getHealthScale() / 2)) {
                    health = player.getHealthScale() / 2;
                    player.setHealth(health);
                    getLogger().info(player.getHealth() + " " + player.getHealthScale());
                    player.sendMessage("You partially healed yourself.");
                    return true;
                } else {
                    player.sendMessage("You already have 50% or more of your health.");
                }
            } else {
                player.sendMessage("You cannot heal yourself when you are not on your land.");
            }
        }
        return false;
    }
}
