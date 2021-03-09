package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

class jailed_player_waiter implements Runnable {
    Player jailed_player;
    Integer time;

    public jailed_player_waiter(Player jailed_player, Integer time){
        this.jailed_player = jailed_player;
        this.time = time;
    }

    public void run(){
        try{Thread.sleep(time);}
        catch(Exception e){
            e.getStackTrace();
        }
        Location out_of_jail_location = new Location(Bukkit.getServer().getWorld("world"), 120, 120, 50);
        jailed_player.teleport(out_of_jail_location);

    }
}

public class Policeman extends Jobs{
    public Policeman(CommandSender sender){
        super(sender);
    }

    public boolean jail(String[] args){
        {
            // TODO Check if the player using this command is a policeman

            // Check if command is valid
            if (args.length == 0) {
                sender.sendMessage("Please indicate a player to jail. /jail <player name> <duration>");
                return true;
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
                //TODO change location of jail
                Location jail_location = new Location(Bukkit.getServer().getWorlds().get(0), 0, 0, 30);
                jailed_player.teleport(jail_location);

                Integer time = 60000; // By default, jails a player for 60000ms == 1minutes

                if (args.length>1){
                    try{time = Integer.parseInt(args[1]);}
                    catch(Exception e){
                        sender.sendMessage("Invalid time flag");
                        return true;}
                }

                jailed_player.sendMessage(ChatColor.RED + "You have been jailed for " + (time/60000) + "minutes");
                jailed_player_waiter jailed_player_waiting = new jailed_player_waiter(jailed_player, time);
                jailed_player_waiting.run();
                return true;
            }

            return false;
        }
    }
}