# Minecraft Medieval Roleplay Mod
A roleplay Bukkit plugin where players decide the story. Check out our [Devpost page](https://devpost.com/software/minecraft-medieval-roleplay-mod) to see demo videos!

##  Commands
###  Jobs
- `/job`: change your job to doctor, guard, or judge
- `/jail` (guard): put a player in jail for n minutes
- `/inspect` (guard); check a nearby player's inventory for illegal items
- `/heal` (doctor): completely heal a player

### Land plots
 - `/claim`: claim land 9 blocks at a time
 - `/selfheal`: heal up to 50 percent of your health on your own claimed land
 - `/unclaim`: unclaim land you own (9 blocks at a time)

### Economy
 - `/transfer_money`: transfer some amount of gold to another player
 - `/check_land`: check the land you own
 - `/transfer_land`: sell your land to another player for gold
 - `transfer_itemFor$`: give another player an item for gold
 - `/addShopItem`: add an item from your inventory to the shop
 - `/removeShopItem`: remove an item you listed from the market
 - `/checkForItemToBuy`: check if there is an item for sale in the market
 - `/checkMarketItems`: list all items for sale at the market
 - `/sendMessageToSeller`: send a message to a seller that you want to buy an item

## Playing without a Minecraft account
Don't have a Minecraft account and don't feel like spending USD $26.95 just to try out a hackathon project? Well you're in luck! Many Minecraft mod development toolchains have a system in place to bypass Minecraft's authentication servers (for testing purposes). You can play in singleplayer, but you can only connect to multiplayer servers that have `online-mode=false` in their `server.properties` file (also known as "offline" or "cracked" servers).

First, grab a copy of the Fabric example mod:
```shell
git clone https://github.com/FabricMC/fabric-example-mod.git
```

You might want to rename the directory to avoid confusion about what you're using it for:
```shell
mv fabric-example-mod minecraft-client
```

Go inside the the new directory:
```shell
cd minecraft-client
```

**macOS/\*nix**
```shell
./gradlew runClient
```
If you want to use a custom username:
```shell
./gradlew runClient --args="--username yournamehere"
```
If you get a permissions error, mark the file executable:
```shell
sudo chmod +x ./gradlew
```

**Windows**
```powershell
.\gradlew.bat runClient
```

If you want to use a custom username:
```shell
.\gradlew.bat runClient --args="--username yournamehere"
```

The first run will take a while as Gradle (a build automation tool) downloads and patches Minecraft. Subsequent launches will be faster.

## Server setup
1. [Install Java version 8](https://www.java.com/en/download/) or newer, if you haven't already
1. Download the latest [plugin release](https://github.com/eliboss/minecraft_rp_mod/releases)
1. Unzip the file into the directory you'll use to hold server files
1. Download [Spigot for Minecraft 1.16.5](https://getbukkit.org/download/spigot) into the server directory (other Bukkit servers like Paper work fine, we just tested with Spigot)
1. Download [SkinsRestorer](https://github.com/SkinsRestorer/SkinsRestorerX/releases) and drop the JAR into the `plugins` folder in the server directory
1. Run `spigot-1.16.5.jar` once (either by double-clicking or running `java -jar spigot-1.16.5.jar` in the terminal)
1. Wait for Spigot to generate `eula.txt` in the server folder (it won't do much else)
1. Edit `eula.txt` so that it says `eula=true`  
1. **Optional:** In `server.properties`, set `online-mode=true` to false if you want to allow people without Minecraft accounts to join the server
1. Run `spigot-1.16.5.jar` again
1. Once the server console window opens, you can join the server with a Minecraft client by connecting to `localhost`

## Building the plugin
First, clone the repository (or download and unzip or whatever) and move your terminal window inside the repository
```shell
git clone https://github.com/eliboss/minecraft_rp_mod.git
cd minecraft_rp_mod
```

**macOS/\*nix**
```shell
./gradlew build
```
If you get a permissions error, mark the file executable:
```shell
sudo chmod +x ./gradlew
```

**Windows**
```powershell
.\gradlew.bat build
```

The first run will take a while as Gradle downloads and sets itself up. Subsequent runs will be much faster.

The output plugin JAR is in `build/libs`. Copy this into the `plugins` folder of your Bukkit server to install/update the plugin.
