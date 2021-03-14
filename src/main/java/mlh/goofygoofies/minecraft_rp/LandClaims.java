package mlh.goofygoofies.minecraft_rp;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class LandClaims implements CommandExecutor, TabCompleter {

    // X_START, Y_START are the coordinates to start eligible land that can be owned at.
    // TODO: Need to make function for admin to change this?
    public final int X_START = 7000;
    public final int Y_START = 80;
    protected String[][] landClaims = new String[500][500];

    public String loadLandClaims() {
        String name = "";
        BufferedReader bReader;
        try {
            bReader = new BufferedReader(new FileReader("LandClaims.csv"));

            int i = 0;
            String row;
            while ((row = bReader.readLine()) != null) {
                String[] data = row.split(",");
                for (int j = 0; j < data.length; j++) {
                    String[] sepData = data[j].split(":");
                    int l = Integer.parseInt(sepData[0]);
                    int m = Integer.parseInt(sepData[1]);
                    landClaims[l][m] = sepData[2].toString();
                    name = sepData[2].toString();
                }
                i++;
            }

            bReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return name;
    }

    public void saveLandClaims() {
        BufferedWriter br;
        try {
            br = new BufferedWriter(new FileWriter("LandClaims.csv"));
            StringBuilder sb = new StringBuilder();

            // Append strings from array
            for (int i = 0; i < landClaims.length; i++) {
                for (int j = 0; j < landClaims[i].length; j++) {
                    if (landClaims[i][j] != null) {
                        sb.append(i + ":" + j + ":" + landClaims[i][j]);
                        sb.append(",");

                        // sb.replace(0, sb.length(), "");
                    }
                }

            }
            br.write(sb.toString());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean setClaim(Player p) {
        double xCoor = p.getLocation().getX() - X_START;
        double yCoor = p.getLocation().getY() - Y_START;
        // String id = p.getPlayerListName();

        if (xCoor < 0 || xCoor > 499 || yCoor < 0 || yCoor > 499) {
            p.sendMessage(p.getLocation() + " " + p.getLocation().getX() + " , " + p.getLocation().getY()
                    + " This land cannot be claimed! Try another location.");
            return false;
        } else {
            int countPClaims = 0;
            for (int i = -1; i < 2; i++) { // check that the 9 blocks surronding chosen block are not claimed yet
                int l = (int) xCoor + i;
                for (int j = -1; j < 2; j++) {
                    int m = (int) yCoor + j;
                    if (landClaims[l][m] != null) {
                        if (landClaims[l][m].compareTo(p.getPlayerListName()) == 0) {
                            countPClaims++;
                        } else {
                            p.sendMessage(landClaims[l][m] + ", This land is already claimed! Try another location.");
                            return false;
                        }
                    }
                }
            }
            if (countPClaims == 9) {
                p.sendMessage(p.getName() + ", all of this land is already yours!");
                return false;
            }
            for (int i = -1; i < 2; i++) { // if any of the 9 blocks are free, and some are only claimed by this player,
                                           // set all blocks to claimed and add to player's
                int l = (int) xCoor + i;
                for (int j = -1; j < 2; j++) {
                    int m = (int) yCoor + j;
                    landClaims[l][m] = p.getPlayerListName();
                }
            }
            p.sendMessage(p.getName() + ", this land is now yours!");
            return true;
        }
    }

    public boolean getClaim(Player p) {
        int xCoor = (int) p.getLocation().getX() - X_START;
        int yCoor = (int) p.getLocation().getY() - Y_START;
        if (xCoor < 0 || xCoor > 499 || yCoor < 0 || yCoor > 499) {
            p.sendMessage("You are not inside claimable land.");
            return false;
        } else if (landClaims[xCoor][yCoor].compareTo(p.getPlayerListName()) == 0) {
            return true;
        } else {
            return false;
        }

    }

    public boolean unclaim(Player p) {
        int xCoor = (int) p.getLocation().getX() - X_START;
        int yCoor = (int) p.getLocation().getY() - Y_START;
        if (xCoor < 0 || xCoor > 499 || yCoor < 0 || yCoor > 499) {
            p.sendMessage("You are not inside claimable land.");
            return false;
        } else {
            for (int i = -1; i < 2; i++) { // if any of the 9 blocks are free, and some are only claimed by this player,
                                           // set all blocks to claimed and add to player's
                int l = (int) xCoor + i;
                for (int j = -1; j < 2; j++) {
                    int m = (int) yCoor + j;
                    if (landClaims[l][m].compareTo(p.getPlayerListName()) == 0) {
                        landClaims[l][m] = null;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }
        
        if (cmd.getName().equalsIgnoreCase("claim") && player != null) { // claim land
            return setClaim(player);
        } else if (cmd.getName().equalsIgnoreCase("unclaim") && player != null) { // unclaim land
            return unclaim(player);
        } else if (cmd.getName().equalsIgnoreCase("selfheal") && player != null) { // self heal this player when on land
                                                                                   // owned by this player
            if (getClaim(player)) {
                double health = player.getHealth();
                if (health < (player.getHealthScale() / 2)) {
                    health = player.getHealthScale() / 2;
                    player.setHealth(health);
                    Bukkit.getLogger().info(player.getHealth() + " " + player.getHealthScale());
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

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // None of these commands take arguments so far
        return new ArrayList<String>();
    }
}
