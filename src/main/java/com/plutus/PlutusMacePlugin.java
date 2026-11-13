package com.plutus;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PlutusMacePlugin extends JavaPlugin {

    private static PlutusMacePlugin instance;
    private NamespacedKey maceKey;
    private final MiniMessage mm = MiniMessage.miniMessage();
    private PackServer packServer;

    @Override
    public void onEnable() {
        instance = this;
        saveDefaultConfig();
        maceKey = new NamespacedKey(this, "plutus_mace");

        // Initialize resource pack server
        if (getConfig().getBoolean("resource-pack.enabled", true)) {
            packServer = new PackServer(this);
            packServer.start();
        }

        // Register command
        Objects.requireNonNull(getCommand("giveplutus")).setExecutor((sender, command, label, args) -> {
            if (!(sender instanceof org.bukkit.entity.Player player)) {
                sender.sendMessage("Players only.");
                return true;
            }
            if (!player.hasPermission("plutus.admin")) {
                player.sendMessage(mm.deserialize("<red>No permission."));
                return true;
            }

            player.getInventory().addItem(createMace());
            player.sendMessage(mm.deserialize(getConfig().getString("weapon-name") + " <gray>given."));
            return true;
        });

        // Register listener
        getServer().getPluginManager().registerEvents(new MaceListener(this), this);

        getLogger().info("PlutusMace enabled successfully!");
    }

    @Override
    public void onDisable() {
        if (packServer != null) {
            packServer.stop();
        }
    }

    public ItemStack createMace() {
        FileConfiguration config = getConfig();
        ItemStack mace = new ItemStack(Material.MACE);
        ItemMeta meta = mace.getItemMeta();

        if (meta != null) {
            meta.displayName(mm.deserialize(config.getString("weapon-name")));

            List<Component> loreLines = new ArrayList<>();
            for (String s : config.getStringList("lore")) {
                loreLines.add(mm.deserialize(s));
            }
            meta.lore(loreLines);

            // Custom model data for the red texture
            meta.setCustomModelData(config.getInt("custom-model-data", 70001));

            // Persistent marker
            meta.getPersistentDataContainer().set(maceKey, PersistentDataType.BYTE, (byte) 1);

            mace.setItemMeta(meta);
        }
        return mace;
    }

    public boolean isPlutusMace(ItemStack item) {
        if (item == null || item.getType() != Material.MACE || !item.hasItemMeta()) return false;
        return item.getItemMeta().getPersistentDataContainer().has(maceKey, PersistentDataType.BYTE);
    }

    public static PlutusMacePlugin getInstance() {
        return instance;
    }

    public NamespacedKey getMaceKey() {
        return maceKey;
    }

    public PackServer getPackServer() {
        return packServer;
    }
}
