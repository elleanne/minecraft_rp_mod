package mlh.goofygoofies.minecraft_rp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.TimeSkipEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;

public class JobsManager implements CommandExecutor, Listener, Runnable, TabCompleter {
    /** Map of valid jobs and their attributes */
    private static final HashMap<String, Job> jobs = new HashMap<String, Job>();
    static {
        jobs.put("doctor",
                new Job("doctor", new Location(Bukkit.getServer().getWorlds().get(0), 6997, 86, -5110.561), 5));
        jobs.put("guard",
                new Job("guard", new Location(Bukkit.getServer().getWorlds().get(0), 6887, 99, -4883.720), 10));
        jobs.put("judge", new Job("judge", null, 3));
    }
    /**
     * Time of day to pay salaries
     * (https://minecraft.gamepedia.com/Daylight_cycle#24-hour_Minecraft_day)
     */
    public static final long PAYMENT_TIME = 0L;
    private Plugin plugin;
    private static Map<UUID, String> playersJobsList;
    private static final SkinsRestorerAPI skinsRestorerAPI = SkinsRestorerAPI.getApi();
    private int salaryTaskID = -1;

    public JobsManager(Plugin plugin, Map<UUID, String> playersJobsList) {
        JobsManager.playersJobsList = playersJobsList;
        this.plugin = plugin;
        scheduleSalaryPayments(Bukkit.getServer().getWorlds().get(0).getTime());
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Doesn't work for ConsoleCommandSender/BlockCommandSender
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command only supports being run by a player");
            return true;
        }

        Player player = (Player) sender;
        String jobName = args[0].toLowerCase();

        // Reject invalid jobs
        if (!jobs.containsKey(jobName)) {
            return false;
        }

        // Update job
        Job job = jobs.get(jobName);
        playersJobsList.replace(player.getUniqueId(), job.name);
        if (job.spawn != null) {
            player.teleport(job.spawn);
        }
        if (job.skin != null) {
            skinsRestorerAPI.setSkinName(player.getName(), job.skin);
            skinsRestorerAPI.applySkin(new PlayerWrapper(player));
        }
        sender.sendMessage(String.format(ChatColor.BLUE + "You are now a %s!", job.name));

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // Ignore other commands
        if (!command.getName().equalsIgnoreCase("job")) {
            return null;
        }

        // Autocomplete job names
        if (args.length == 1) {
            return new ArrayList<String>(jobs.keySet());
        }

        return null;
    }

    /**
     * Time skips (players skipping the night by sleeping, /time, etc.) do not make
     * scheduled tasks happen any sooner or later, causing daily payments to arrive
     * at the wrong time. This detects time skips and reschedules salary payments
     * accordingly.
     */
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onTimeSkip(TimeSkipEvent event) {
        Server server = Bukkit.getServer();
        long skipAmount = event.getSkipAmount();
        long nextTime = server.getWorlds().get(0).getTime() + skipAmount;
        server.getLogger().info(String.format("%s Time skip by %s ticks.", event.getSkipReason().toString(), skipAmount));

        // Cancel existing payment schedule, if applicable
        if (salaryTaskID != -1) {
            server.getScheduler().cancelTask(salaryTaskID);
        }

        // Reschedule payments to their proper time
        scheduleSalaryPayments(nextTime % 24000L);

        // Pay skipped paychecks
        long paymentsSkipped = (nextTime + PAYMENT_TIME) / 24000L;
        for (int i = 0; i < paymentsSkipped; i++) {
            run();
        }
    }

    /**
     * Pay all players a salary according to their jobs.
     */
    @Override
    public void run() {
        plugin.getLogger().info("Payday!");
        for (Map.Entry<UUID, String> entry : playersJobsList.entrySet()) {
            Job job = jobs.get(entry.getValue());
            if (job == null) {
                continue;
            }
            Player player = Bukkit.getPlayer(entry.getKey());
            player.getEnderChest().addItem(new ItemStack(Material.GOLD_INGOT, job.salary));
            player.sendMessage(String.format("%sYou have been paid %s%sG%s for your work as a %s.", ChatColor.GREEN, ChatColor.GOLD, job.salary, ChatColor.GREEN, job.name));
        }
    }

    /**
     * Schedules daily salary payments, starting at <code>PAYMENT_TIME</code>.<br>
     * <br>
     * <code>currentTime</code> is parameterized so that this method can also be
     * used for time skip rescheduling. <code>onTimeSkip</code> runs before the time
     * skip is executed, so this method needs to be explicitly told what time it
     * will be.
     * 
     * @param currentTime Current daytime on the server (e.g. from
     *                    <code>World.getTime</code>)
     */
    private void scheduleSalaryPayments(long currentTime) {
        Server server = Bukkit.getServer();
        Logger logger = server.getLogger();
        // long currentTime = server.getWorlds().get(0).getTime();
        long timeUntilPay = (24000L + PAYMENT_TIME - currentTime) % 24000L;
        logger.info(
                String.format("The current time is %s, and time until next paycheck is %s", currentTime, timeUntilPay));
        salaryTaskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, this, timeUntilPay, 24000L);
        if (salaryTaskID == -1) {
            logger.warning("Failed to schedule daily salary task!");
        }
    }
}

class Job {
    /** Name of the job stored in the player-job list */
    public String name;
    /** Location to teleport the player to once they take the job (if not null) */
    public Location spawn;
    /** Salary per in-game day */
    public int salary;
    /** Name of the skin to give the player once they take the job (if not null) */
    public String skin;

    /**
     * Constructs a roleplay job
     * 
     * @param name   Name of the job stored in the player-job list
     * @param spawn  Location to teleport the player to once they take the job (if
     *               not null)
     * @param salary Salary paid to the player per full in-game day on the job
     * @param skin   Name of the skin to give the player once they take the job (if
     *               not null)
     */
    public Job(String name, Location spawn, int salary, String skin) {
        this.name = name;
        this.spawn = spawn;
        this.salary = salary;
        this.skin = skin;
    }

    /**
     * Constructs a roleplay job, with the skin name defined by the job name
     * 
     * @param name   Name of the job stored in the player-job list
     * @param spawn  Location to teleport the player to once they take the job (if
     *               not null)
     * @param salary Salary paid to the player per full in-game day on the job
     */
    public Job(String name, Location spawn, int salary) {
        this(name, spawn, salary, name);
    }
}
