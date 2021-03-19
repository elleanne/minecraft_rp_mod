package mlh.goofygoofies.minecraft_rp;

import net.skinsrestorer.api.SkinsRestorerAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.Objects;

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

        //gives AllPLayers class the plugin's object (used for multi-threading)
        AllPlayersCommands.plugin = this;

        // Commands
        JobsManager jm = new JobsManager(this, playersJobsList);
        Objects.requireNonNull(getCommand("job")).setExecutor(jm);
        Objects.requireNonNull(getCommand("heal")).setExecutor(new DoctorCommands(playersJobsList));

      
        AllPlayersCommands ac = new AllPlayersCommands();
        Objects.requireNonNull(getCommand("me")).setExecutor(ac);
        Objects.requireNonNull(getCommand("id")).setExecutor(ac);
        Objects.requireNonNull(getCommand("it")).setExecutor(ac);
        Objects.requireNonNull(getCommand("rob")).setExecutor(ac);
        Objects.requireNonNull(getCommand("roll")).setExecutor(ac);


        GuardCommands gc = new GuardCommands(playersJobsList);
        Objects.requireNonNull(getCommand("inspect")).setExecutor(gc);
        Objects.requireNonNull(getCommand("jail")).setExecutor(gc);

        // Land claims
        try {
            lc.loadLandClaims();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Objects.requireNonNull(getCommand("claim")).setExecutor(lc);
        Objects.requireNonNull(getCommand("selfheal")).setExecutor(lc);
        Objects.requireNonNull(getCommand("unclaim")).setExecutor(lc);
        Objects.requireNonNull(getCommand("check_land")).setExecutor(lc);
        Objects.requireNonNull(getCommand("transfer_land")).setExecutor(lc);

        Market m = new Market();
        Objects.requireNonNull(getCommand("transfer_money")).setExecutor(m);
        Objects.requireNonNull(getCommand("transfer_itemFor$")).setExecutor(m);

        MarketShop mS = new MarketShop();
        Objects.requireNonNull(getCommand("addShopItem")).setExecutor(mS);
        Objects.requireNonNull(getCommand("removeShopItem")).setExecutor(mS);
        Objects.requireNonNull(getCommand("checkForItemToBuy")).setExecutor(mS);
        Objects.requireNonNull(getCommand("checkMarketItems")).setExecutor(mS);
        Objects.requireNonNull(getCommand("sendMessageToSeller")).setExecutor(mS);

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
    }

    @EventHandler
    /*
     * If the ship is getting robbed, checks if the player who is robbing it
     * leaves the ship's zone. If he did, aborts the robbery.
     */
    public void robberExitsArea(PlayerMoveEvent event) {
        if (!(event.getPlayer() instanceof Player))
            return;
        if (AllPlayersCommands.robber == null) return;
        if (event.getPlayer().getEntityId() != AllPlayersCommands.robber.getEntityId() ) return;
        //checks if player is leaving the ship area
        if(event.getPlayer().getLocation().distance(AllPlayersCommands.SHIP_LOCATION) > 50 ){
            event.getPlayer().sendMessage("You are too far from the ship! The robbery failed...");
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
        playersJobsList.put(event.getPlayer().getUniqueId(), "Civilian");
        event.getPlayer().sendMessage(ChatColor.BLUE + "You are now a civilian");
    }

    /* Deletes the stored job of a player when he disconnects */
    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        playersJobsList.remove(event.getPlayer().getUniqueId());
        skinsRestorerAPI.removeSkin(event.getPlayer().getName());
    }

    @EventHandler
    public void onAttack(EntityDamageByEntityEvent event) {
        if (((event.getDamager() instanceof Player)) && ((event.getEntity() instanceof Player))) {
            Player victim = (Player) event.getEntity();
            if (lc.getClaim(victim)) {
                victim.setNoDamageTicks(10);
            }
        }
    }
}
