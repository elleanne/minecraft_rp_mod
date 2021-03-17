package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.HashMap;

public class ShopChest implements CommandExecutor {
    private final int MARKET_X_TOP = 7100;
    private final int MARKET_Y_TOP = 80;

    HashMap<Object, Object> invi;
    MinecraftRP plugin = MinecraftRP.getInstance();
    Inventory inv;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(! (sender instanceof Player)) {
            sender.sendMessage("You are not a player");
            return true;
        }
        Player p = (Player) sender;

        if(command.getName().equalsIgnoreCase("shop")) {
            ItemStack is = new ItemStack(Material.DIAMOND);
            ItemMeta tiMeta = is.getItemMeta();
            tiMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3Diamond &9- &7(click to purchase)"));
            tiMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&cPrice &7(in gold)"), "5"));
            is.setItemMeta(tiMeta);
            inv = Bukkit.createInventory(null, 9, "Shop");
            inv.setItem(4, is);
            p.openInventory(inv);


        }  if (command.getName().equalsIgnoreCase("checkGoldInPurse")) {
            Integer coins = plugin.goldData.get(p.getUniqueId());
            if(coins != null) {
                p.sendMessage("You have " + coins + " gold.");
            } else {
                p.sendMessage("You have no gold.");
            }
        }
        return false;
    }
}