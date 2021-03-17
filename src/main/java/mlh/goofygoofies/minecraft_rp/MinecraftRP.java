package mlh.goofygoofies.minecraft_rp;

import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

import static org.bukkit.Bukkit.getServer;

public class MinecraftRP extends JavaPlugin implements Listener {
    private SkinsRestorerAPI skinsRestorerAPI;
    Map<Integer, String> playersJobsList = new HashMap<>();
    final static LandClaims lc = new LandClaims();
    Location BANK_LOCATION;

    @Override
    public void onEnable() {
        BANK_LOCATION = new Location(getServer().getWorlds().get(0), 7000, 86, -5110);
        // SkinsRestorer
        getLogger().info("Loading SkinsRestorer API...");
        skinsRestorerAPI = SkinsRestorerAPI.getApi();
        getLogger().info(skinsRestorerAPI.toString());

        //gives AllPLayers class the plugin's object (used for multi-threading)
        AllPlayersCommands.plugin = this;

        // Commands
        getCommand("job").setExecutor(new JobsCommand(playersJobsList));
        getCommand("heal").setExecutor(new DoctorCommands(playersJobsList));

        AllPlayersCommands ac = new AllPlayersCommands();
        getCommand("me").setExecutor(ac);
        getCommand("id").setExecutor(ac);
        getCommand("it").setExecutor(ac);
        getCommand("roll").setExecutor(ac);
        getCommand("rob").setExecutor(ac);

        GuardCommands gc = new GuardCommands(playersJobsList);
        getCommand("inspect").setExecutor(gc);
        getCommand("jail").setExecutor(gc);
        
        // Land claims
        lc.loadLandClaims();
        getCommand("claim").setExecutor(lc);
        getCommand("selfheal").setExecutor(lc);
        getCommand("unclaim").setExecutor(lc);
        
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

    @EventHandler
    /*
     * If the bank is getting robbed, checks if the player who is robbing it
     * leaves the bank's zone. If he did, aborts the robbery.
     */
    public void robberExitsArea(PlayerMoveEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        if (AllPlayersCommands.robber == null) return;
        if (event.getPlayer().getEntityId() != AllPlayersCommands.robber.getEntityId() ) return;
        //checks if player is leaving the bank area
        if(event.getPlayer().getLocation().distance(BANK_LOCATION) > 50 ){
            event.getPlayer().sendMessage("You are too far from the bank! The robbery failed...");
            AllPlayersCommands.robber = null;
            int old_robbing_task_id = AllPlayersCommands.robbing_task_id;
            AllPlayersCommands.robbing_task_id = 0;
            Bukkit.getServer().getScheduler().cancelTask(old_robbing_task_id);
        }

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
}
