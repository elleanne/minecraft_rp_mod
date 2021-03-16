package mlh.goofygoofies.minecraft_rp;

import java.util.Iterator;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;

public class EnderChestManager implements Listener {
    /**
     * Disallows crafting ender chests upon construction.
     * As an event listener, this class disallows placing items in an ender chest that aren't gold nuggets/ingots/blocks.
     */
    public EnderChestManager() {
        Iterator<Recipe> it = Bukkit.recipeIterator();
        Recipe recipe;
        while (it.hasNext()) {
            recipe = it.next();
            if (recipe.getResult().getType() == Material.ENDER_CHEST) {
                it.remove();
            }
        }
    }

    /**
     * Disallows placing items in an ender chest that aren't gold nuggets/ingots/blocks. Removing non-gold items from an ender chest is still allowed.
     * @param event Event when the player clicks inside an inventory view
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {
        InventoryType inventory = event.getClickedInventory().getType();
        InventoryAction action = event.getAction();
        
        if ((// Quick inventory swap (shift + click)
                inventory == InventoryType.PLAYER &&
                action == InventoryAction.MOVE_TO_OTHER_INVENTORY &&
                // event.getWhoClicked().getOpenInventory().getTopInventory().getType() == InventoryType.ENDER_CHEST
                event.getInventory().getType() == InventoryType.ENDER_CHEST
            )||
            (// Dropping an item into the ender chest
                inventory == InventoryType.ENDER_CHEST &&
                isDeposit(action)
            )) {
            if (!isCurrency(event.getCursor())) {
                event.setCancelled(true);
            }
        }
    }

    /**
     * Disallows placing items in an ender chest that aren't gold nuggets/ingots/blocks via stack splitting (click and drag).
     * @param event Event when the player drags inside an inventory view
     */
    @EventHandler(ignoreCancelled = true)
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST && !isCurrency(event.getOldCursor())) {
            event.setCancelled(true);
        }
    }

    /**
     * Whether an item stack contains valid currency
     * @param stack Stack to analyze
     * @return True if valid currency, false otherwise
     */
    boolean isCurrency(ItemStack stack) {
        Material type = stack.getType();
        return (type == Material.GOLD_BLOCK ||
                type == Material.GOLD_INGOT ||
                type == Material.GOLD_NUGGET);
    }

    /**
     * Whether an InventoryAction is the player depositing items into the top inventory
     * @param action InventoryAction taken by the player
     * @return True if a depositing action, false otherwise
     */
    boolean isDeposit(InventoryAction action) {
        return (action == InventoryAction.HOTBAR_SWAP ||
                action == InventoryAction.PLACE_ALL ||
                action == InventoryAction.PLACE_ONE ||
                action == InventoryAction.PLACE_SOME ||
                action == InventoryAction.SWAP_WITH_CURSOR);
    }
}
