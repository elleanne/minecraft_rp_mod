package mlh.goofygoofies.minecraft_rp;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;

public class ShopListener implements Listener {

    MinecraftRP plugin = MinecraftRP.getInstance();

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {

        Player p = (Player) e.getWhoClicked();

        if (e.getCurrentItem() == null) {
            p.sendMessage("item is null");
            ItemStack item = e.getCursor();
            p.sendMessage("item: " + item.getType());
            if(e.getClickedInventory().getHolder() == null) {
                e.setCurrentItem(new ItemStack(item.getType()));
                e.getCurrentItem().setAmount(item.getAmount());

                ItemMeta tiMeta = e.getCurrentItem().getItemMeta();
                tiMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3" + e.getCurrentItem().getType().toString() +"&9- &7(click to purchase)"));
                tiMeta.setLore(Arrays.asList(ChatColor.translateAlternateColorCodes('&', "&cPrice &7(in gold)"), "5"));
                e.getCurrentItem().setItemMeta(tiMeta);

                p.getInventory().addItem(new ItemStack(Material.GOLD_BLOCK, 5));
                p.getInventory().remove(item);
            }
            return;
        }
        Inventory i = e.getClickedInventory();
        if (!e.getCurrentItem().hasItemMeta()) {
            p.sendMessage("item has no meta");
            p.sendMessage("holder: " + i.getHolder());
            if ( i.getHolder().toString().compareTo(p.getPlayerListName()) == 0 ) {
                e.setCancelled(true);
                i.remove(new ItemStack(e.getCurrentItem().getType()));
                i.addItem(new ItemStack(Material.GOLD_BLOCK, 5));
                InventoryView inv = p.getOpenInventory();
                int numSlots = inv.countSlots();
                Inventory shopInv = inv.getTopInventory();
                shopInv.addItem(new ItemStack(e.getCurrentItem().getType(), 1));
            }
        }

        p.sendMessage("holder: " + i.getHolder());
        if (i.getHolder() == null) { // TODO: find a better way to check the inventory
            e.setCancelled(true);
            Integer coinAmount = plugin.goldData.get(p.getUniqueId());
            if (coinAmount == null) {
                p.sendMessage("You don't have any coins to spend.");
                return;
            }
            Integer price = Integer.parseInt(e.getCurrentItem().getItemMeta().getLore().get(1));
            if (coinAmount < price) {
                p.sendMessage("You don't have enough coins. You only have " + coinAmount);
                return;
            }

            plugin.goldData.put(p.getUniqueId(), coinAmount - price);
            p.sendMessage("You have purchased a " + e.getCurrentItem().getType().name() + " for the price of " + price + ".");
            Material itemMaterial = e.getCurrentItem().getType();
            p.getInventory().addItem(new ItemStack(itemMaterial));
            i.remove(new ItemStack(e.getCurrentItem().getType()));
        }
    }
}
