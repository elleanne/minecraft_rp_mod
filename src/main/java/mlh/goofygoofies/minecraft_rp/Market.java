package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class Market implements CommandExecutor, TabCompleter {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        Player player = null; // check that sender is a player
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }

        if (cmd.getName().equalsIgnoreCase("transfer_money") && player != null) { // claim land
            if (args.length >= 2) {
                Player reciever = Bukkit.getPlayerExact(args[0]); // check that reciever is a valid player
                if (reciever != null) {
                    transferMoney(player, reciever, args[1]);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Transfer gold ingots/block from one player to another
     * amountToTransfer is in gold ingots, 1 gold block is worth 4 gold ingots
     * @param sender
     * @param reciever
     * @param amountToTransfer
     */
    private void transferMoney(Player sender, Player reciever, String amountToTransfer) {
        int amount = 0;
        try {
            amount = Integer.parseInt(amountToTransfer);
        } catch (NumberFormatException e) {
            sender.sendMessage("invalid amount to transfer");
            return;
        }
        if (amount < 1) return;
        int amountTemp = amount; // save copy of amount to use after amount is decremented

        ItemStack[] sItems = sender.getInventory().getContents(); // items only in inventory bar
        // search inventory bar for gold ingots and blocks
        for (ItemStack is : sItems) {
            if (amount > 0) { // only enter if amount has not been decremented to 0
                if (is != null) {
                    if ( is.getType() == Material.GOLD_INGOT ) {
                        int count = is.getAmount();
                        if (count > amount) {
                            is.setAmount(count - amount);
                            amount = 0;
                        } else {
                            is.setAmount(0);
                            amount -= count;
                        }
                    } else if (is.getType() == Material.GOLD_BLOCK) {
                        int count = is.getAmount() * 4; // blocks are worth 4 gold ingots
                        if (count > amount) {
                            is.setAmount(count - amount);
                            amount = 0;
                        } else {
                            is.setAmount(0);
                            amount -= count;
                        }
                    }
                }
            }
        }
        if (amount == amountTemp) { // if equal, no gold was found in the sender's inventory
            sender.sendMessage(sender.getDisplayName() + ", you did not have enough gold to send " + reciever.getDisplayName() + " " + amount + " gold.");
            reciever.sendMessage(sender.getDisplayName() + " tried to send you " + amount + " gold, but didn't have enough.");
        }
        if (amount == 0) { // exactly enough gold was found
            ItemStack item = new ItemStack(Material.GOLD_INGOT, amountTemp);
            reciever.getInventory().addItem(item);
        } else if (amount > 0) { // Did not find enough gold in sender's inventory, sending what was found
            sender.sendMessage( "You did not have " + amountTemp + " gold. Sending " + (amountTemp - amount) + " instead." );
            amountTemp = amountTemp - amount;
            ItemStack item = new ItemStack(Material.GOLD_INGOT, amountTemp);
            reciever.getInventory().addItem(item);
        }
        sender.sendMessage( reciever.getDisplayName() + " recieved " + amountTemp + " gold." );
        reciever.sendMessage(sender.getDisplayName() + " sent you " + amountTemp + " gold.");
    }


    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    /**
     * Check if a player has an amount of gold in their inventory
     * @param p
     * @param amountOfGold
     * @return
     */
    public static String checkPlayerGold(Player p, int amountOfGold) {
        if (amountOfGold == 0) return "0";
        ItemStack[] stack = p.getInventory().getContents();
        if (stack == null || stack.length == 0) return null;
        int countOfGold = 0;
        for (int i = 0; i < stack.length; i++) {
            if (stack[i] != null) {
                if (stack[i].getType() == Material.GOLD_INGOT) {
                    if (stack[i].getAmount() >= amountOfGold) {
                        return "ingots";
                    } else if (stack[i].getAmount() > 0) {
                        countOfGold += stack[i].getAmount();
                    }
                } else if (stack[i].getType() == Material.GOLD_BLOCK) {
                    if (stack[i].getAmount() >= amountOfGold / 4) {
                        return "blocks";
                    } else if (stack[i].getAmount() > 0) {
                        countOfGold += stack[i].getAmount() * 4; // a gold block is worth 4 gold ingots
                    }
                }
            }
            if (countOfGold >= amountOfGold) return "both";
        }
        return null;
    }
}
