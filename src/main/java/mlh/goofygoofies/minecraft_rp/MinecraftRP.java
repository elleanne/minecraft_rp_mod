package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.World;


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

public class MinecraftRP extends JavaPlugin {
    @Override
    public void onEnable() {
        super.getLogger().info("MinecraftRP plugin enabled.");
        getCommand("jail").setExecutor(this);
        System.out.println("Up and running yay");
    }
    
    @Override
    public void onDisable() {
        Bukkit.getLogger().info("MinecraftRP plugin disabled.");
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
        if (commandLabel.equalsIgnoreCase("jail"))
        {
           // TODO Check if the player using this command is a policeman
           if (Bukkit.getPlayerExact(args[0]) != null){
               Player jailed_player = Bukkit.getPlayerExact(args[0]);
               Location jail_location = new Location(Bukkit.getServer().getWorld("world"), 0, 0, 30);
               jailed_player.teleport(jail_location);
               Integer time = 10000;
               if (args.length>1){
                   try{time = Integer.parseInt(args[1]);}
                   catch(Exception e){sender.sendMessage("Invalid time flag");}
               }
               jailed_player_waiter jailed_player_waiting = new jailed_player_waiter(jailed_player, time);
               jailed_player_waiting.run();
               return true;
           }
            return false;
        }
        return true;
    }

}
