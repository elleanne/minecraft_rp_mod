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

    /**
     * Commands to transfer money and land
     **/
    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        Player player; // check that sender is a player
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }
        if (cmd.getName().equalsIgnoreCase("transfer_money")) { // claim land
            if (args.length >= 2) {
                Player reciever = Bukkit.getPlayerExact(args[0]); // check that reciever is a valid player
                if (reciever != null) {
                    transferMoney(player, reciever, args[1]);
                }
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("transfer_itemFor$")) {
            if (args.length >= 4) {
                Player receiver = Bukkit.getPlayerExact(args[0]); // check that receiver is a valid player
                if (receiver != null && player != receiver) {
                    tradeItemForGold(player, receiver, args[1], args[2], args[3]);
                    return true;
                } else {
                    player.sendMessage("invalid buyer name");
                    return false;
                }
            } else {
                player.sendMessage("Invalid number of arguments. Should be: </transfer_itemFor$> <buyer> <item to transfer> <amount of item> <amount of gold> ");
                return false;
            }

        }
        return false;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String alias, @NotNull String[] args) {
        return null;
    }

    /**
     * Trade a specified amount of an item for a specified amount of gold
     * sender is seller, receiver is buyer
     * @param sender
     * @param reciever
     * @param item
     * @param itemAmount
     * @param goldAmount
     */
    public void tradeItemForGold(Player sender, Player reciever, String item, String itemAmount, String goldAmount) {
        int intItemAmount;
        int intGoldAmount;
        try {
            intItemAmount = Integer.parseInt(itemAmount);
            intGoldAmount = Integer.parseInt(goldAmount);
        } catch (NumberFormatException e) {
            sender.sendMessage("invalid amount of gold or item to transfer");
            return;
        }

        item = item.toUpperCase(); // sender is seller, receiver is buyer
        item = item.replace(" ", "_"); // in case the player doesn't add the underscore, add it

        Material itemToTrade = Material.getMaterial(item);
        if (itemToTrade == null) { // check that the input for item was valid
            sender.sendMessage("Invalid input item type. Did not send to " + reciever.getDisplayName() + ".");
            return;
        }

        // check that the sender has the items and the buyer has the money
        ItemStack[] senderInv = sender.getInventory().getContents();
        if (checkInventory(senderInv, intItemAmount, itemToTrade)) {
            String hasGold = checkPlayerGold(reciever, intGoldAmount);
            if (hasGold != null) {
                if (intGoldAmount == 0) { // if giving for free, only transfer the item
                    transferItem(sender, reciever, senderInv, itemToTrade, intItemAmount);
                } else {
                    transferItem(sender, reciever, senderInv, itemToTrade, intItemAmount);
                    transferMoney(sender, reciever, goldAmount);
                }
                sender.sendMessage("Don't forget to remove your item from the market if you had it on sale there!");
            }
        }
    }

    /**
     * Transfer gold ingots/block from one player to another
     * amountToTransfer is in gold ingots, 1 gold block is worth 4 gold ingots
     * @param sender
     * @param reciever
     * @param amountToTransfer
     */
    private void transferMoney(Player sender, Player reciever, String amountToTransfer) {
        int amount;
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
                    if (is.getType() == Material.GOLD_INGOT) {
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
            sender.sendMessage("You did not have " + amountTemp + " gold. Sending " + (amountTemp - amount) + " instead.");
            amountTemp = amountTemp - amount;
            ItemStack item = new ItemStack(Material.GOLD_INGOT, amountTemp);
            reciever.getInventory().addItem(item);
        }
        sender.sendMessage(reciever.getDisplayName() + " recieved " + amountTemp + " gold.");
        reciever.sendMessage(sender.getDisplayName() + " sent you " + amountTemp + " gold.");
    }

    /**
     * Transfer an item between players
     * @param sender
     * @param receiver
     * @param senderInv
     * @param item
     * @param amount
     */
    public void transferItem(Player sender, Player receiver, ItemStack[] senderInv, Material item, int amount) {
        // sender is seller, receiver is buyer
        ItemStack thisItem = new ItemStack(item, amount); // send buyer the item
        receiver.getInventory().addItem(thisItem);

        for (ItemStack is : senderInv) {
            if (is != null && amount > 0) {
                if (is.getType() == item) {
                    int itemQuant = is.getAmount();
                    if (itemQuant >= amount) {
                        is.setAmount(itemQuant - amount);
                        amount = 0;
                    } else {
                        is.setAmount(0);
                        amount -= itemQuant;
                    }
                }
            }
        }
    }

    /**
     * Check if a player's inventory has an item in it
     * @param inventory
     * @param amount
     * @param item
     * @return
     */
    public boolean checkInventory(ItemStack[] inventory, int amount, Material item) {
        int count = 0;
        for (ItemStack is : inventory) {
            if (is != null && amount > count) {
                if (is.getType() == item) {
                    int itemQuant = is.getAmount();
                    if (itemQuant >= amount) {
                        return true;
                    } else {
                        count += itemQuant;
                    }
                }
            }
        }
        if (count >= amount) return true;
        return false;
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
        if (stack.length == 0) return null;
        int countOfGold = 0;
        for (ItemStack itemStack : stack) {
            if (itemStack != null) {
                if (itemStack.getType() == Material.GOLD_INGOT) {
                    if (itemStack.getAmount() >= amountOfGold) {
                        return "ingots";
                    } else if (itemStack.getAmount() > 0) {
                        countOfGold += itemStack.getAmount();
                    }
                } else if (itemStack.getType() == Material.GOLD_BLOCK) {
                    if (itemStack.getAmount() >= amountOfGold / 4) {
                        return "blocks";
                    } else if (itemStack.getAmount() > 0) {
                        countOfGold += itemStack.getAmount() * 4; // a gold block is worth 4 gold ingots
                    }
                }
            }
            if (countOfGold >= amountOfGold) return "both";
        }
        return null;
    }
}
