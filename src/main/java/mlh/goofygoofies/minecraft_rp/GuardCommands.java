package mlh.goofygoofies.minecraft_rp;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

class jailed_player_waiter implements Runnable {
    Player jailed_player;
    Integer time;
    Location OUT_OF_JAIL_LOCATION  = new Location(Bukkit.getServer().getWorlds().get(0), 7161.246, 82, -4846.105);


    public jailed_player_waiter(Player jailed_player, Integer time){
        this.jailed_player = jailed_player;
        this.time = time;
    }

    public void run(){
        try{Thread.sleep(time);}
        catch(Exception e){
            e.getStackTrace();
        }

        jailed_player.teleport(OUT_OF_JAIL_LOCATION);

    }
}

public class GuardCommands implements CommandExecutor {
    Location JAIL_LOCATION = new Location(Bukkit.getServer().getWorlds().get(0), 6818, 81, -5010.538);
    static String [] illegalItemsList = new String[]{
        "SWORD", "BOW", "ARROW", "TNT", "DIAMOND"
    };
    public CommandSender sender;
    private static Map<Integer, String> playersJobsList;

    public GuardCommands(Map<Integer, String> playersJobsList) {
        GuardCommands.playersJobsList = playersJobsList;
    }

    public boolean jail(String[] args){

        // Check if command is valid
        if (args.length == 0) {
            sender.sendMessage("Please indicate a player to jail.");
            return false;
        }

        if (Bukkit.getPlayerExact(args[0]) != null){
            Player jailed_player = Bukkit.getPlayerExact(args[0]);
            if (jailed_player == null) {
                sender.sendMessage("Invalid Player Name");
                return true;
            }
            Player Sender = (Player) sender;
            // if the player is too far from the sender, displays an error message to the sender
            if (jailed_player.getLocation().distance(Sender.getLocation())>100 ) {
                sender.sendMessage("Player too far...");
                return true;
            }

            jailed_player.teleport(JAIL_LOCATION);

            Integer time = 60000; // By default, jails a player for 60000ms == 1minutes

            if (args.length>1){
                try{time = Integer.parseInt(args[1]);}
                catch(Exception e){
                    sender.sendMessage("Invalid time flag");
                    return true;}
            }

            jailed_player.sendMessage(ChatColor.RED + "You have been jailed for " + (time/60000) + " minutes");
            jailed_player_waiter jailed_player_waiting = new jailed_player_waiter(jailed_player, time);
            jailed_player_waiting.run();
            return true;
        }

        return false;
    }

    public boolean inspect(String[] args){
        if (args.length == 0) {
            sender.sendMessage("Please indicate a player to jail. /jail <player name> <duration>");
            return true;
        }

        if (Bukkit.getPlayerExact(args[0]) != null) {
            Player playerToInspect = Bukkit.getPlayerExact(args[0]);
            if (playerToInspect == null) {
                sender.sendMessage("Invalid Player Name");
                return true;
            }
            Player Sender = (Player) sender;
            // if the player is too far from the sender, displays an error message to the sender
            if (playerToInspect.getLocation().distance(Sender.getLocation()) > 100) {
                sender.sendMessage("Player too far...");
                return true;
            }
            ItemStack[] inv = playerToInspect.getInventory().getContents();
            boolean illegalItemsFound = false;
            for (ItemStack item : inv) {
                if (item == null) continue;
                String itemName = item.getType().toString();
                for(String illegalItem : illegalItemsList) {
                    if (itemName.contains(illegalItem)) {
                        if (!illegalItemsFound) {
                            sender.sendMessage(ChatColor.RED + "Illegal items found!");
                            illegalItemsFound = true;
                        }
                        sender.sendMessage(ChatColor.GRAY + "Item: " + itemName.toLowerCase() + " - Qt: " + item.getAmount());
                        break;
                    }
                }
            }
            if (!illegalItemsFound) sender.sendMessage(ChatColor.GREEN + "No illegal items found!");
            return true;
        }
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
        if (playersJobsList.get(player.getEntityId()) != "guard") {
            sender.sendMessage(ChatColor.RED + "You do not have the rights to use this command");
            return true;
        }

        switch (label.toLowerCase()) {
            case "jail":
                return jail(args);
            case "inspect":
                return inspect(args);
        }

        return false;
    }


}
