package mlh.goofygoofies.minecraft_rp;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import net.skinsrestorer.api.PlayerWrapper;
import net.skinsrestorer.api.SkinsRestorerAPI;

public class JobsCommand implements CommandExecutor, TabCompleter {
    /** Map of valid jobs and their attributes */
    private static final HashMap<String, Job> jobs = new HashMap<String, Job>();
    static {
        jobs.put("doctor", new Job("doctor", new Location(Bukkit.getServer().getWorlds().get(0), 6997, 86, -5110.561)));
        jobs.put("guard", new Job("guard", new Location(Bukkit.getServer().getWorlds().get(0), 6887, 99, -4883.720)));
        jobs.put("judge", new Job("judge", null));
    }
    private static Map<Integer, String> playersJobsList;
    private static final SkinsRestorerAPI skinsRestorerAPI = SkinsRestorerAPI.getApi();;

    public JobsCommand(Map<Integer, String> playersJobsList) {
        JobsCommand.playersJobsList = playersJobsList;
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
        playersJobsList.replace(player.getEntityId(), job.name);
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
}

class Job {
    /** Name of the job stored in the player-job list */
    public String name;
    /** Location to teleport the player to once they take the job (if not null) */
    public Location spawn;
    /** Name of the skin to give the player once they take the job (if not null) */
    public String skin;

    /**
     * Constructs a roleplay job
     * @param name Name of the job stored in the player-job list
     * @param spawn Location to teleport the player to once they take the job (if not null)
     * @param skin Name of the skin to give the player once they take the job (if not null)
     */
    public Job(String name, Location spawn, String skin) {
        this.name = name;
        this.spawn = spawn;
        this.skin = skin;
    }

    /**
     * Constructs a roleplay job, with the skin name defined by the job name
     * @param name Name of the job stored in the player-job list
     * @param spawn Location to teleport the player to once they take the job (if not null)
     */
    public Job(String name, Location spawn) {
        this.name = this.skin = name;
        this.spawn = spawn;
    }
}
