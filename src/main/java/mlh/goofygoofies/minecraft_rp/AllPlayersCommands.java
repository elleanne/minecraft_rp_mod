package mlh.goofygoofies.minecraft_rp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AllPlayersCommands implements CommandExecutor, TabCompleter {
    public CommandSender sender;
 
    // Sends a message to all nearby players, describing the action that the sender did (args[0]).
    public boolean describeAction(String[] args){
        if (args.length == 0) {
            return false;
        }
        int distance = 50 * 50;
        String actionDescription = String.join(" ", args);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Player senderPlayer = (Player) sender;
            if (p.getWorld() ==  senderPlayer.getWorld() && p.getLocation().distanceSquared(senderPlayer.getLocation()) <= distance) {
                p.sendMessage(ChatColor.BLUE + sender.getName() + " " + actionDescription);
            }
        }
        return true;
    }

    // Sends a message to all nearby players, describing the event that happened (args[0]).
    public boolean describeEvent(String[] args){
        if (args.length == 0) {
            return false;
        }
        int distance = 50 * 50;
        String eventDescription = String.join(" ", args);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Player senderPlayer = (Player) sender;
            if (p.getWorld() ==  senderPlayer.getWorld() && p.getLocation().distanceSquared(senderPlayer.getLocation()) <= distance) {
                p.sendMessage(ChatColor.GREEN + eventDescription);
            }
        }
        return true;
    }

    //generates a random number between 100 and 0 (inclusive), to determine if an action has succeeded or not.
    public boolean rollDice(){
        Random r = new Random();
        int result = r.nextInt(101);
        int distance = 50 * 50;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Player senderPlayer = (Player) sender;
            if (p.getWorld() ==  senderPlayer.getWorld() && p.getLocation().distanceSquared(senderPlayer.getLocation()) <= distance) {
                p.sendMessage(ChatColor.GRAY + sender.getName() + " rolled the dice and obtained a " + result);
            }
        }
        return true;
    }

    public boolean showID(){
        int distance = 50 * 50;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Player senderPlayer = (Player) sender;
            if (p.getWorld() ==  senderPlayer.getWorld() && p.getLocation().distanceSquared(senderPlayer.getLocation()) <= distance) {
                p.sendMessage(ChatColor.GREEN + "ID indicates: \n Name: " + sender.getName());
            }
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        this.sender = sender;
        // Doesn't work for ConsoleCommandSender/BlockCommandSender
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command only supports being run by a player");
            return true;
        }

        switch (label.toLowerCase()) {
            case "me":
                return describeAction(args);
            case "it":
                return describeEvent(args);
            case "roll":
                return rollDice();
            case "id":
                return showID();
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // None of these commands take arguments so far
        return new ArrayList<String>();
    }
}
