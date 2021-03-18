package mlh.goofygoofies.minecraft_rp;

import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MinecraftRP extends JavaPlugin implements Listener {
    private SkinsRestorerAPI skinsRestorerAPI;
    Map<UUID, String> playersJobsList = new HashMap<>();
    final static LandClaims lc = new LandClaims();

    @Override
    public void onEnable() {
        // SkinsRestorer
        getLogger().info("Loading SkinsRestorer API...");
        skinsRestorerAPI = SkinsRestorerAPI.getApi();
        getLogger().info(skinsRestorerAPI.toString());

        // Commands
        JobsManager jm = new JobsManager(this, playersJobsList);
        getCommand("job").setExecutor(jm);
        getCommand("heal").setExecutor(new DoctorCommands(playersJobsList));
        AllPlayersCommands ac = new AllPlayersCommands();
        getCommand("me").setExecutor(ac);
        getCommand("id").setExecutor(ac);
        getCommand("it").setExecutor(ac);
        getCommand("roll").setExecutor(ac);
        GuardCommands gc = new GuardCommands(playersJobsList);
        getCommand("inspect").setExecutor(gc);
        getCommand("jail").setExecutor(gc);
        
        // Land claims
        lc.loadLandClaims();
        getCommand("claim").setExecutor(lc);
        getCommand("selfheal").setExecutor(lc);
        getCommand("unclaim").setExecutor(lc);
        
        // Events
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new EnderChestManager(), this);
        pm.registerEvents(jm, this);
        pm.registerEvents(this, this);
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
        playersJobsList.put(event.getPlayer().getUniqueId(), "Civilian");
        event.getPlayer().sendMessage(ChatColor.BLUE + "You are now a civilian");
    }

    /* Deletes the stored job of a player when he disconnects */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        playersJobsList.remove(event.getPlayer().getUniqueId());
        skinsRestorerAPI.removeSkin(event.getPlayer().getName());
    }
}
