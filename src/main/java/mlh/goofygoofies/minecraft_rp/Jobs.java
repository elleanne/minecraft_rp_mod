package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class Jobs {
    Location GUARD_SPAWN_LOCATION = new Location(Bukkit.getServer().getWorlds().get(0), 6887, 99, -4883.720);
    Location DOCTOR_SPAWN_LOCATION = new Location(Bukkit.getServer().getWorlds().get(0), 6997, 86, -5110.561);

    public CommandSender sender;

    public Jobs(CommandSender sender){
        this.sender = sender;
    }

    // converts the args list to a single string
    public String argsToSingleString(String[] args){
        String string = "";
        for (String arg : args){
            string += " " + arg;
        }
        return string;
    }

    //changes the job of a player
    public boolean jobChange(String[] args){
        String jobName = args[0];
        switch(jobName.toLowerCase()){
            case "guard":{
                Player p = (Player) sender;
                p.teleport(GUARD_SPAWN_LOCATION);
                return true;
            }

            case "doctor":{
                Player p = (Player) sender;
                p.teleport(DOCTOR_SPAWN_LOCATION);
                return true;
            }

            case "judge":{
                //TODO TP PLAYER TO THE COURT
                return true;
            }

        }

        return false;
    }
}