package mlh.goofygoofies.minecraft_rp;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public class LandClaims implements CommandExecutor, TabCompleter {

    // X_START, Y_START are the coordinates to start eligible land that can be owned at.
    // TODO: Need to make function for admin to change this?
    public final int X_START = 7050;
    public final int Y_START = 50;
    protected String[][] landClaims = new String[500][500];

    /**
     * Read land claims from .csv file when server is launched
     * @return
     */
    public String loadLandClaims() throws IOException {
        String name = "";
        BufferedReader bReader;
        try {
            bReader = new BufferedReader(new FileReader("LandClaims.csv"));

            String row;
            while ((row = bReader.readLine()) != null) {
                String[] data = row.split(",");
                for (int j = 0; j < data.length; j++) {
                    String[] sepData = data[j].split(":");
                    int l = Integer.parseInt(sepData[0]);
                    int m = Integer.parseInt(sepData[1]);
                    landClaims[l][m] = sepData[2];
                    name = sepData[2];
                }
            }
            bReader.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return name;
    }

    /**
     * Save land claims in .csv file when server is closed
     */
    public void saveLandClaims() {
        BufferedWriter br;
        try {
            br = new BufferedWriter(new FileWriter("LandClaims.csv"));
            StringBuilder sb = new StringBuilder();
            // Append strings from array
            for (int i = 0; i < landClaims.length; i++) {
                for (int j = 0; j < landClaims[i].length; j++) {
                    if (landClaims[i][j] != null) {
                        sb.append(i).append(":").append(j).append(":").append(landClaims[i][j]);
                        sb.append(",");
                    }
                }
            }
            br.write(sb.toString());
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * claim land for this player if land is claimable and available
     * @param p
     * @return
     */
    public boolean setClaim(Player p) {
        int xCoor = (int) p.getLocation().getX() - X_START; //
        int yCoor = (int) p.getLocation().getY() - Y_START;

        if (xCoor < 0 || xCoor > 499 || yCoor < 0 || yCoor > 499) {
            p.sendMessage(p.getLocation() + " " + p.getLocation().getX() + " , " + p.getLocation().getY()
                    + " This land cannot be claimed! Try another location.");
            return false;
        } else {
            int countPClaims = 0;
            for (int i = -1; i < 2; i++) { // check that the 9 blocks surrounding chosen block are not claimed yet
                int l = xCoor + i;
                for (int j = -1; j < 2; j++) {
                    int m = yCoor + j;
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
            if (countPClaims == 9) { // if count is 9, all 9 blocks are already claimed by this player
                p.sendMessage(p.getName() + ", all of this land is already yours!");
                return false;
            }
            for (int i = -1; i < 2; i++) { // if any of the 9 blocks are free, and some are only claimed by this player,
                // set all blocks to claimed and set value to player's name
                int l = xCoor + i;
                for (int j = -1; j < 2; j++) {
                    int m = yCoor + j;
                    landClaims[l][m] = p.getPlayerListName();
                }
            }
            p.sendMessage(p.getName() + ", this land is now yours!");
            return true;
        }
    }

    /**
     * Check if the current location is owned by this player
     * @param p
     * @return
     */
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

    /**
     * Check if a given set of coordinates is owned by this player
     * format used is - x:y1,y2,y3 ( i.e.: this.x, y1; this.x, y2; this.x, y3;)
     * Used in transferLand
     * @param p
     * @param x
     * @param yValues
     * @return
     */
    public Integer[] checkValidClaim(Player p, int x, String[] yValues) {
        Integer[] yCoors = new Integer[yValues.length];
        x = x - X_START;
        if (x < 0 || x > 499) return null;
        for (int i = 0; i < yValues.length; i++) {
            int y = Integer.parseInt(yValues[i]) - Y_START;
            if (y < 0 || y > 499) return null;
            if (landClaims[x][y].compareTo(p.getPlayerListName()) != 0) {
                p.sendMessage("You do not own: " + x + ", " + y);
                return null;
            } else {
                yCoors[i] = y;
            }
        }
        return yCoors;
    }

    /**
     * When player is on their land, release the land they are on
     * @param p
     * @return
     */
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
                    int m = yCoor + j;
                    if (landClaims[l][m].compareTo(p.getPlayerListName()) == 0) {
                        landClaims[l][m] = null;
                    }
                }
            }
        }
        return true;
    }

    /**
     * When a player is on their land, heal up to 50% of their own health
     * @param player
     * @return
     */
    public boolean resetHealth(Player player) {
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
        return false;
    }

    /**
     * Checks for all of this player's land, displays hashmap of this player's owned land in chat
     * @param player
     * @return
     */
    public boolean checkLand(Player player) {
        player.sendMessage("looking for your land...");
        String name = player.getPlayerListName();

        HashMap<Integer, ArrayList<Integer>> playerLand = new HashMap<>();
        for (int i = 0; i < landClaims.length; i++) {
            for (int j = 0; j < landClaims[i].length; j++) {

                if (landClaims[i][j] != null) {
                    if (landClaims[i][j].compareTo(name) == 0) {

                        if (playerLand.get(i + X_START) == null) {
                            ArrayList<Integer> tempArray = new ArrayList<>();
                            tempArray.add(j + Y_START);
                            playerLand.put((i + X_START), tempArray);
                        } else {
                            ArrayList<Integer> tempArray = playerLand.get(i + X_START);
                            tempArray.add((j + Y_START));
                            playerLand.put((i + X_START), tempArray);
                        }
                    }
                }
            }
        }
        if (playerLand.size() > 0) {
            player.sendMessage("Your land claims are (FORMAT: x=[y,y,y]): " + playerLand);
            return true;
        } else {
            player.sendMessage("You do not own any land!");
        }
        return false;
    }

    /**
     * Transfers land to receiver in exchange for money sent to player
     * @param player
     * @param receiver
     * @param landToTransfer
     * @param amountToCharge
     * @return
     */
    public boolean tranferLand(Player player, Player receiver, String landToTransfer, String amountToCharge) {
        String checkGold = Market.checkPlayerGold(receiver, Integer.parseInt(amountToCharge));
        if (checkGold == null) {
            player.sendMessage(receiver.getDisplayName() + " does not have enough gold to pay for this land.");
            receiver.sendMessage(receiver.getDisplayName() + ", you do not have enough gold to pay for this land.");
            return false;
        }
        String[] xYValues = landToTransfer.split(";");

        HashMap<Integer, Integer[]> allLandTransferable = new HashMap<>();
        for (String xy : xYValues) {

            String[] splitXY = xy.split("=");
            int x = Integer.parseInt(splitXY[0]);
            String[] yCoors = splitXY[1].split(",");
            // check that input is owned by player and valid ownable land and returns integer array of y coordinates
            Integer[] yCoorsInt = checkValidClaim(player, x, yCoors);
            if (yCoorsInt == null) return false;
            allLandTransferable.put(x, yCoorsInt);
        }
        // if all land is owned by player and receiver has enough gold, transfer land and gold with helper function
        return transferLandHelper(player, receiver, allLandTransferable, Integer.parseInt(amountToCharge), checkGold);
    }

    /**
     * Helper function for transferLand,
     * once all error checking has been done, this function actually does the transfer
     *
     * @param p
     * @param r
     * @param allLandTransferable
     * @param amount
     * @param goldType
     * @return
     */
    private boolean transferLandHelper(Player p, Player r, HashMap<Integer, Integer[]> allLandTransferable, int amount, String goldType) {
        Set<Integer> keysToLand = allLandTransferable.keySet();
        String recieverName = r.getPlayerListName();
        // set land claims to be owned by reciever
        for (int key : keysToLand) {
            Integer[] yCoors = allLandTransferable.get(key);
            for (int j : yCoors) {
                int l = key - X_START;
                int m = j - Y_START;
                if (l >= 0 && l < 500 && m >= 0 && m < 500) {
                    p.sendMessage(landClaims[l][m] + "");
                    landClaims[l][m] = recieverName;
                    p.sendMessage(landClaims[l][m] + "");
                }
            }
        }
        ItemStack[] rItems = r.getInventory().getContents();

        // give sender player (p) money for land
        ItemStack item = new ItemStack(Material.GOLD_INGOT, amount);
        p.getInventory().addItem(item);

        // take ingots from reciever (r) of land
        if (goldType.compareTo("0") != 0) { // goldType is "0" when the amount for the land is 0 (free)
            for (ItemStack is : rItems) {
                if (amount > 0) {
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
        return amount == 0;
    }

    /**
     * Used in MineCraftRP to listen for a player being attacked
     * @param player
     * @return
     */
    public boolean listenForAttacked(Player player) {
        return getClaim(player);
    }

    /** Commands to claim/unclaim land, check claims, heal when on owned land, and transfer land to another player **/
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player; // check that sender is a player
        if (sender instanceof Player) {
            player = (Player) sender;
        } else {
            sender.sendMessage("You must be a player!");
            return false;
        }
        if (cmd.getName().equalsIgnoreCase("claim")) { // claim land
            return setClaim(player);
        } else if (cmd.getName().equalsIgnoreCase("unclaim")) { // unclaim land
            return unclaim(player);
        } else if (cmd.getName().equalsIgnoreCase("selfheal")) { // self heal this player when on land
            // owned by this player
            if (getClaim(player)) {
                return resetHealth(player);
            } else {
                player.sendMessage("You cannot heal yourself when you are not on your land.");
            }
        } else if (cmd.getName().equalsIgnoreCase("transfer_land")) {
            if (args.length <= 0) {
                player.sendMessage("Please use command with the name player to send and a list of the land to transfer inside of '{LIST}'");
            } else if (args.length == 3) {
                Player reciever = Bukkit.getPlayerExact(args[0]);
                if (reciever != null) {
                    player.sendMessage("transferring land...");
                    tranferLand(player, reciever, args[1], args[2]);
                } else {
                    player.sendMessage("Invalid Player Name");
                }
            } else {
                player.sendMessage("Wrong format! You need 3 arguments to use this command");
            }
        } else if (cmd.getName().equalsIgnoreCase("check_land")) {
            return checkLand(player);
        }

        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        // None of these commands take arguments so far
        return new ArrayList<>();
    }
}
