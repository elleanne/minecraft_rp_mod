package Main.java.mlh.goofygoofies.minecraft_rp;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class MinecraftRP extends JavaPlugin {

	LandClaims lc = new LandClaims(); // TODO: need to make singleton

	@Override
	public void onEnable() {
		String name = lc.loadLandClaims();
		getLogger().info( name + " MinecraftRP plugin enabled.");

	}

	@Override
	public void onDisable() {
		lc.saveLandClaims();
		getLogger().info("MinecraftRP plugin disabled.");
	}

	public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {

		Player player = null;
		if (sender instanceof Player) {
			player = (Player) sender;
		} else {
			sender.sendMessage("You must be a player!");
			return false;
		}

		// claim land
		if (cmd.getName().equalsIgnoreCase("claim") && player != null) {
			return lc.setClaim(player);
		} else if (cmd.getName().equalsIgnoreCase("unclaim") && player != null) { // unclaim land
			return lc.unclaim(player);
		} else if (cmd.getName().equalsIgnoreCase("selfheal") && player != null) { // self heal this player when on land owned by this player
			if (lc.getClaim(player)) {
				double health = player.getHealth();
				if (health < (player.getHealthScale() / 2)) {
					health = player.getHealthScale() / 2;
					player.setHealth(health);
					getLogger().info(player.getHealth() + " " + player.getHealthScale());
					player.sendMessage("You partially healed yourself.");
					return true;
				} else {
					player.sendMessage("You already have 50% or more of your health.");
				}
			} else {
				player.sendMessage("You cannot heal yourself when you are not on your land.");
			}
		} 
		return false;
	}
}