package mlh.goofygoofies.minecraft_rp;

import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftRP extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("MinecraftRP plugin enabled.");
    }
    
    @Override
    public void onDisable() {
        getLogger().info("MinecraftRP plugin disabled.");
    }
}
