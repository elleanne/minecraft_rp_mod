package mlh.goofygoofies.minecraft_rp;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DoctorCommands implements CommandExecutor {
    public CommandSender sender;
    private static Map<UUID, String> playersJobsList;

    public DoctorCommands(Map<UUID, String> playersJobsList) {
        DoctorCommands.playersJobsList = playersJobsList;
    }

    public boolean heal(String[] args){
        //Check if command's arguments are invalid
        if (args.length != 1) return false;

        if (Bukkit.getPlayerExact(args[0]) != null) {
            Player playerHealed = Bukkit.getPlayerExact(args[0]);
            if (playerHealed == null) {
                sender.sendMessage("Invalid Player Name");
                return false;
            }

            Player Sender = (Player) sender;
            // if the player is too far from the sender, displays an error message to the sender
            if (playerHealed.getLocation().distance(Sender.getLocation()) > 100) {
                sender.sendMessage("Player too far...");
                return true;
            }
            playerHealed.setHealth(20);
            sender.sendMessage(playerHealed.getName() + " is getting healed... He starts to feel better");
            playerHealed.sendMessage("You start feeling better...");
            return true;
        }

        sender.sendMessage("Invalid Player Name");
        return false;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        // Doesn't work for ConsoleCommandSender/BlockCommandSender
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command only supports being run by a player");
            return true;
        }

        // Check job
        Player player = (Player) sender;
        if (playersJobsList.get(player.getUniqueId()) != "doctor") {
            sender.sendMessage(ChatColor.RED + "You do not have the rights to use this command");
            return true;
        }

        if (label.equalsIgnoreCase("heal")) {
            return heal(args);
        }

        return false;
    }

}
