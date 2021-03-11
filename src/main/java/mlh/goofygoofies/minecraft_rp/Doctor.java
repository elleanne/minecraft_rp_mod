package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Doctor extends Jobs{

    public Doctor(CommandSender sender) {
        super(sender);
    }

    public boolean heal(String[] args){
        // TODO Check if the player using this command is a Doctor

        //Check if command's arguments are invalid
        if (args.length != 1) return false;

        if (Bukkit.getPlayerExact(args[0]) != null) {
            Player playerHealed = Bukkit.getPlayerExact(args[0]);
            if (playerHealed == null) {
                sender.sendMessage("Invalid Player Name");
                return false;
            }

            Player Sender = (Player) sender;
            // if the player is too far from the sender, displays an error message to the sender
            if (playerHealed.getLocation().distance(Sender.getLocation()) > 100) {
                sender.sendMessage("Player too far...");
                return true;
            }
            playerHealed.setHealth(20);
            sender.sendMessage(playerHealed.getName() + " is getting healed... He starts to feel better");
            playerHealed.sendMessage("You start feeling better...");
            return true;
        }

        sender.sendMessage("Invalid Player Name");
        return false;
    }

}
