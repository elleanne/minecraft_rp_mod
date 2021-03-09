package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.World;


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
            case "jail": {
                Policeman policeman = new Policeman(sender);
                boolean res = policeman.jail(args);
                return res;
            }

            case "me": {
                allPlayers player = new allPlayers(sender);
                boolean res = player.describeAction(args);
                return res;
            }

            case "it": {
                allPlayers player = new allPlayers(sender);
                boolean res = player.describeEvent(args);
                return res;
            }

            case "roll": {
                allPlayers player = new allPlayers(sender);
                boolean res = player.rollDice();
                return res;
            }

        }

        return true;
    }

}
