package mlh.goofygoofies.minecraft_rp;

import org.bukkit.command.CommandSender;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class allPlayers extends Jobs{

    public allPlayers(CommandSender sender) {
        super(sender);
    }

    // Sends a message to all nearby players, describing the action that the sender did (args[0]).
    public boolean describeAction(String[] args){
        if (args.length == 0) {
            return false;
        }
        int d2 = 50 * 50;
        String actionDescription = super.argsToSingleString(args);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Player senderPlayer = (Player) sender;
            if (p.getWorld() ==  senderPlayer.getWorld() && p.getLocation().distanceSquared(senderPlayer.getLocation()) <= d2) {
                p.sendMessage(ChatColor.BLUE + sender.getName() + actionDescription);
            }
        }
        return true;
    }

    // Sends a message to all nearby players, describing the event that happened (args[0]).
    public boolean describeEvent(String[] args){
        if (args.length == 0) {
            return false;
        }
        int d2 = 50 * 50;
        String eventDescription = super.argsToSingleString(args);
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Player senderPlayer = (Player) sender;
            if (p.getWorld() ==  senderPlayer.getWorld() && p.getLocation().distanceSquared(senderPlayer.getLocation()) <= d2) {
                p.sendMessage(ChatColor.GREEN + eventDescription);
            }
        }
        return true;
    }

    //generates a random number between 100 and 0 (inclusive), to determine if an action has succeeded or not.
    public boolean rollDice(){
        Random r = new Random();
        int result = r.nextInt(101);
        int d2 = 50 * 50;
        for (Player p : Bukkit.getServer().getOnlinePlayers()) {
            Player senderPlayer = (Player) sender;
            if (p.getWorld() ==  senderPlayer.getWorld() && p.getLocation().distanceSquared(senderPlayer.getLocation()) <= d2) {
                p.sendMessage(ChatColor.GRAY + sender.getName() + " rolled the dice and obtained a " + result);
            }
        }
        return true;
    }
}
