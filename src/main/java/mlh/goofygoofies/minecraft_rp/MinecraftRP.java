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
        switch(commandLabel){
            case "jail":
                Policeman policeman = new Policeman(sender);
                policeman.jail(args);
                return true;
        }

        return true;
    }

}
