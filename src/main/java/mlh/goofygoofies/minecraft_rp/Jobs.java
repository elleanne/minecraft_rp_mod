package mlh.goofygoofies.minecraft_rp;

import org.bukkit.command.CommandSender;

public class Jobs {
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
        switch(jobName){
            case "Policeman":{
                //TODO reskin player
                //TODO TP PLAYER TO POLICE STATION
                //TODO GIVE PLAYER POLICEMAN STUFF (Weapon?)
                return true;
            }

            case "Doctor":{
                //TODO reskin player
                //TODO TP PLAYER TO HOSPITAL
                return true;
            }

            case "Judge":{
                //TODO reskin player
                //TODO TP PLAYER TO THE COURT
                return true;
            }

        }

        return false;
    }
}
