package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MarketShop implements CommandExecutor {
    HashMap<Material, ArrayList<String[]>> shopMap = new HashMap<>(); // key is item to sell, ArrayList< String[ sellerName, amountOfItem, cost ] >
    private final int MARKET_X_TOP = 7100;
    private final int MARKET_Y_TOP = 80;
    private final int MARKET_X_BOTTOM = 7200;
    private final int MARKET_Y_BOTTOM = 180;

    public void addShopItem(Material material, String sellerName, int amountOfItem, int cost) {
        if (shopMap.containsKey(material)) {
            ArrayList<String[]> listOfThisItem = shopMap.get(material);
            for (String[] listing: listOfThisItem) {
                if(listing[0].compareTo(sellerName) == 0) {
                    listOfThisItem.remove(listing);
                    int amount = Integer.parseInt(listing[1]);
                    int c = Integer.parseInt(listing[2]);
                    amount += amountOfItem;
                    c += cost;
                    listing[1] = "" + amount;
                    listing[2] = "" + c;
                    listOfThisItem.add(listing);
                    shopMap.put(material, listOfThisItem);
                    return;
                }
            }

            // if this line is reached, seller has not already listed this item. Add new listing.
            String[] listing = {sellerName, ("" + amountOfItem), ("" + cost) };
            listOfThisItem.add(listing);
            shopMap.put(material, listOfThisItem);
        } else {
            // there are no listing for this item from any seller, make a new key!
            ArrayList<String[]> listOfThisItem = new ArrayList<>();
            String[] listing = {sellerName, ("" + amountOfItem), ("" + cost) };
            listOfThisItem.add(listing);
            shopMap.put(material, listOfThisItem);
        }
    }

    public void removeShopItem(Material material, String sellerName, int amountOfItem, int cost) {
        if (shopMap.containsKey(material)) {
            ArrayList<String[]> listOfThisItem = shopMap.get(material);
            for (String[] listing: listOfThisItem) {
                if(listing[0].compareTo(sellerName) == 0) {
                    listOfThisItem.remove(listing);
                    int amount = Integer.parseInt(listing[1]);
                    int c = Integer.parseInt(listing[2]);
                    amount -= amountOfItem;
                    c -= cost;
                    if (amount <= 0 || c <= 0) {
                        shopMap.put(material, listOfThisItem);
                        return;
                    } else {
                        listing[1] = "" + amount;
                        listing[2] = "" + c;
                        listOfThisItem.add(listing);
                        shopMap.put(material, listOfThisItem);
                        return;
                    }
                }
            } // if this line is reached, this seller has not already listed this item.
        }
    }

    public void checkMarketShop(Player player) {
        Set<Material> keys = shopMap.keySet();
        if(keys.size() == 0) {
            player.sendMessage("There is nothing for sale in the market today.");
            return;
        }
        player.sendMessage("Here is what is for sale today: ");
        for (Material key : keys) {
            ArrayList<String[]> sellerItemData = shopMap.get(key);
            for(String[] seller: sellerItemData) {
                player.sendMessage(key + " amount:" + seller[1] + " cost:" + seller[2] + " from:" + seller[0] + "; " );
            }
        }
    }

    public void checkMarketShopForItem(Player player, String item) {
        Material material = Material.getMaterial(item.toUpperCase());
        if (material == null) return;
        if (shopMap.containsKey(material)) {
            ArrayList<String[]> listOfSellers = shopMap.get(material);
            for(String[] seller: listOfSellers) {
                player.sendMessage(item.toUpperCase() + " amount:" + seller[1] + " cost:" + seller[2] + " from:" + seller[0] + "; " );
            }
        } else {
            player.sendMessage(item.toUpperCase() + " is not for sale in the market today.");
        }
    }

    public boolean sendMessageToSeller(String buyerName, String sellerName, String item, String amountOfItem) {
        Player seller = Bukkit.getPlayerExact(sellerName); // check that receiver is a valid player
        if (seller != null) {

            seller.sendMessage(sellerName + ", " + buyerName + " wants to buy " + amountOfItem + " of your item, " + item + ". If you accept, use the transfer command to start the transaction");
            return checkPlayerLocation(seller);


        }
        return false;
    }

    public boolean checkPlayerLocation(Player player) {
        int x = player.getLocation().getBlockX();
        int y = player.getLocation().getBlockY();
        if ( x <= MARKET_X_TOP || x >= MARKET_X_BOTTOM || y <= MARKET_Y_TOP || y >= MARKET_Y_BOTTOM) {
            player.sendMessage(player.getDisplayName() + " is not inside the market.");
            return false;
        }
        return true;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String label, @NotNull String[] args) {
        Player player ; // check that sender is a player
        if (sender instanceof Player) {
            player = (Player) sender;
            if( !checkPlayerLocation(player) ) {
                return false;
            }
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }


        if (cmd.getName().equalsIgnoreCase("addShopItem") ) {
            // Material is, int amountOfItem, int cost
            if (args.length >= 3) {
                Material item = Material.getMaterial(args[0].toUpperCase());
                if (item == null) return false;
                try {
                    int itemAmount = Integer.parseInt(args[1]);
                    int goldAmount = Integer.parseInt(args[2]);
                    addShopItem(item, player.getPlayerListName(), itemAmount, goldAmount);
                    player.sendMessage("Added " + itemAmount + " " + item.name() + " to the shop for " + goldAmount + " gold.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("invalid amount of gold or item to sell");
                    return false;
                }
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("removeShopItem") ) {
            // Material is, int amountOfItem, int cost
            if (args.length >= 3) {
                Material item = Material.getMaterial(args[0].toUpperCase());
                if (item == null) return false;
                try {
                    int itemAmount = Integer.parseInt(args[1]);
                    int goldAmount = Integer.parseInt(args[2]);
                    removeShopItem(item, player.getPlayerListName(), itemAmount, goldAmount);
                    player.sendMessage("Removed " + itemAmount + " " + item.name() + " to the shop for " + goldAmount + " gold.");
                } catch (NumberFormatException e) {
                    sender.sendMessage("invalid amount of gold or item to remove from shop");
                    return false;
                }
                return true;
            }
        } else if (cmd.getName().equalsIgnoreCase("checkForItemToBuy") ) {
            if (args.length == 1) {
                checkMarketShopForItem(player, args[0]);
            } else {
                player.sendMessage("invalid number of arguments. Please add the item to look up.");
            }
        } else if (cmd.getName().equalsIgnoreCase("checkMarketItems") ) {
            checkMarketShop(player);
        } else if( cmd.getName().equalsIgnoreCase("sendMessageToSeller") ) {
            if(args.length == 3) {
                boolean checkSent = sendMessageToSeller(player.getDisplayName(), args[0], args[1], args[2]);
                if (checkSent) player.sendMessage("Your message was sent to " + args[0] + ".");
                else { player.sendMessage("Invalid seller name or the seller is not in the market.");}
            } else {
                player.sendMessage("please include: seller name, item to buy, and amount of the item you want to buy.");
            }
        }
        return false;
    }
}
