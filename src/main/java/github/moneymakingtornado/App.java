
package github.moneymakingtornado;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Pose;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.File;
import java.util.Collection;
import java.util.UUID;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;

public class App extends JavaPlugin implements Listener {
    //test
    @Override
    public void onEnable() {
        getLogger().info("VAR Anticheat is enabled! - Version: " + getDescription().getVersion()
                + "\nA machine learning anticheat made by MoneyMakingTornado on Github");
        this.getCommand("snapshot").setExecutor(this);
        // getServer().getPluginManager().registerEvents(new App(), this);
        try {
            FileWriter PlayerLogWriter = new FileWriter("./PlayerLogs/" + "ReadMe.txt");
            String Headers[] = { "Time (milliseconds)", "PlayerName", "PlayerUUID", "PlayerGamemode", "PlayerPing",
                    "PlayerMovementEffected", "PlayerYaw", "PlayerPitch", "PlayerVelocity",
                    "PlayerWalkSpeed", "PlayerVehicle", "PlayerPose", "PlayerFoodLevel", "PlayerHealth",
                    "PlayerRegenRate", "PlayerSaturation", "PlayerItemInUse", "PlayerItemOnCursor",
                    "PlayerAttackCooldown", "PlayerNoDamageTicks", "PlayerGetLastDamage",
                    "PlayerFireTicks", "PlayerExhaustion", "PlayerFreezeTicks" };
            PlayerLogWriter.write("All colums are in the follwoing order:\n");
            for (String str : Headers) {
                PlayerLogWriter.write('"');
                PlayerLogWriter.write(str);
                PlayerLogWriter.write('"');
                PlayerLogWriter.write(",");
            }
            PlayerLogWriter.close();
        } catch (Exception e) {
            getLogger().warning(e.getMessage());
            e.printStackTrace();
        }

        // // If SnapshotForever is true in plugin.yml make run snapshotall()
        if (getConfig().getBoolean("SnapshotForever")) {
            getLogger().info("SnapshotForever is true, running snapshotall()");
            ForeverSnapshot = true;
            SnapshotAll();
        }
    }

    // FileConfiguration config = getConfig();

    // // this will return the boolean you want to get
    // public static Boolean getValue(String value) {
    //     return getConfig().getBoolean(value);
    // }

    // // use this code to set the value
    // public static void setValue(String value, Boolean bool) {
    //     getConfig().set(value, bool);
    // }

    // save the configuration
    public void save() {
        try {
            getConfig().save(new File("path/to/config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void onDisable() {
        getLogger().info("See you again, SpigotMC!");
    }

    public void SnapshotAll() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @Override
            public void run() {
                if (!ForeverSnapshot)
                    return;
                Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
                for (Player player : players) {
                    SnapshotPlayerLog(player);
                }
                SnapshotAll();
            }
        }, 30 * 20L);
    }

    public boolean SnapshotPlayerLog(Player player) {
        try {
            String time = LocalDateTime.now().toString().replace(":", "-");
            String FileName = (player.getName() + " " + time + ".csv");
            String FilePath = ("./PlayerLogs/" + FileName);
            File PlayerLogFile = new File(FilePath);
            PlayerLogFile.createNewFile();

            int TaskID = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    if (!player.isOnline()) return;
                    LogPlayer(player, FileName);
                }
            }, 0L, 1L);

            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                @Override
                public void run() {
                    Bukkit.getServer().getScheduler().cancelTask(TaskID);
                }
            }, 30 * 20L);

            return true;
        } catch (Exception e) {
            getLogger().warning(e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    public void LogPlayer(Player player, String FileName) {
        // Checks
        long Time = System.currentTimeMillis();
        String PlayerName = player.getName();
        UUID PlayerUUID = player.getUniqueId();

        GameMode PlayerGamemode = player.getGameMode();
        int PlayerPing = player.getPing();
        Boolean PlayerMovementEffected = false;
        for (PotionEffect effect : player.getActivePotionEffects()) {
            PotionEffect PotionEffect = player.getPotionEffect(effect.getType());
            if (PotionEffect != null) {
                PlayerMovementEffected = true;
            }
        }

        // Movement
        Float PlayerYaw = player.getLocation().getYaw();
        Float PlayerPitch = player.getLocation().getPitch();
        Vector PlayerVelocity = player.getVelocity();
        Float PlayerWalkSpeed = player.getWalkSpeed();
        Entity PlayerVehicle = player.getVehicle();
        Pose PlayerPose = player.getPose();

        // Health/Hunger/Saturation
        int PlayerFoodLevel = player.getFoodLevel();
        Double PlayerHealth = player.getHealth();
        int PlayerRegenRate = player.getSaturatedRegenRate();
        Float PlayerSaturation = player.getSaturation();

        // Mouse/inventory
        ItemStack PlayerItemInUse = player.getItemInUse();
        ItemStack PlayerItemOnCursor = player.getItemOnCursor();
        Float PlayerAttackCooldown = player.getAttackCooldown();

        // Damge Taken
        int PlayerNoDamageTicks = player.getNoDamageTicks();
        Double PlayerGetLastDamage = player.getLastDamage();

        // Enviroment
        int PlayerFireTicks = player.getFireTicks();
        Float PlayerExhaustion = player.getExhaustion();
        int PlayerFreezeTicks = player.getFreezeTicks();

        // Save all values into a CSV
        try {
            String FilePath = ("./PlayerLogs/" + FileName);
            String Output[] = {
                    String.valueOf(Time),
                    PlayerName,
                    PlayerUUID.toString(),
                    String.valueOf(PlayerGamemode),
                    String.valueOf(PlayerPing),
                    String.valueOf(PlayerMovementEffected),
                    String.valueOf(PlayerYaw),
                    String.valueOf(PlayerPitch),
                    String.valueOf(PlayerVelocity),
                    String.valueOf(PlayerWalkSpeed),
                    String.valueOf(PlayerVehicle),
                    String.valueOf(PlayerPose),
                    String.valueOf(PlayerFoodLevel),
                    String.valueOf(PlayerHealth),
                    String.valueOf(PlayerRegenRate),
                    String.valueOf(PlayerSaturation),
                    String.valueOf(PlayerItemInUse),
                    String.valueOf(PlayerItemOnCursor),
                    String.valueOf(PlayerAttackCooldown),
                    String.valueOf(PlayerNoDamageTicks),
                    String.valueOf(PlayerGetLastDamage),
                    String.valueOf(PlayerFireTicks),
                    String.valueOf(PlayerExhaustion),
                    String.valueOf(PlayerFreezeTicks)
            };
            String contentToAppend = "\n";
            for (String str : Output) {
                contentToAppend = contentToAppend + '"' + str + '"' + ",";
            }
            contentToAppend = contentToAppend.substring(0, contentToAppend.length() - 1);
            FileWriter fw = new FileWriter(FilePath, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(contentToAppend);
            bw.close();
        } catch (Exception e) {
            getLogger().warning(e.getMessage());
            if (e.getMessage().contains("The system cannot find the path specified")) {
                getLogger().info("Making the path for the PlayerLogs folder");
                Boolean success = (new File("./PlayerLogs")).mkdirs();
                if (!success) {
                    getLogger().warning("Failed to make the PlayerLogs folder");
                } else {
                    getLogger().info("Successfully made the PlayerLogs folder");
                }
            }
        }

        // var PlayerSomthing16 = player.getMaxFireTicks();
        // var PlayerSomthing20 = player.getMaximumNoDamageTicks();
        // var PlayerSomthing5 = player.getAbsorptionAmount();
        // var PlayerSomthing12 = player.getRemainingAir();
        // var PlayerSomthing = player.getEquipment();
        // var PlayerSomthing2 = player.getFallDistance();

        // getLogger().info("Wrote data for " + PlayerName);
    }

    boolean ForeverSnapshot = false;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // log cmd and arg
        // System.out.println("sender: " + sender.toString() + "cmd: " + cmd.toString()
        // + "label: " + label.toString() + "args: " + args.toString());
        // System.out.println(cmd.getName(), cmd.);
        if (cmd.getName().equalsIgnoreCase("snapshot")) {
            Collection<? extends Player> players = Bukkit.getServer().getOnlinePlayers();
            for (Player player : players) {
                if (args[0].equalsIgnoreCase(player.getName())) {
                    boolean RecoredPlayer = SnapshotPlayerLog(player);
                    if (RecoredPlayer) {
                        sender.sendMessage("Successfully recorded " + player.getName());
                    } else {
                        sender.sendMessage("Failed to record " + player.getName());
                    }
                    return true;
                }
            }
        }
        if (cmd.getName().equalsIgnoreCase("foreversnapshot")) {
            if (ForeverSnapshot) {
                ForeverSnapshot = false;
                sender.sendMessage("ForeverSnapshot is now false");
            } else {
                ForeverSnapshot = true;
                sender.sendMessage("ForeverSnapshot is now true");
                SnapshotAll();
            }
            return true;
        }
        return false;
    }
}