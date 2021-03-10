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
}
