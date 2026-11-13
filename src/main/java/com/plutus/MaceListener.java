package com.plutus;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;

public class MaceListener implements Listener {

    private final PlutusMacePlugin plugin;
    private final AbilityManager abilityManager;
    private boolean isInternalDamage = false;

    public MaceListener(PlutusMacePlugin plugin) {
        this.plugin = plugin;
        this.abilityManager = new AbilityManager(plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        if (plugin.getPackServer() != null) {
            // Small delay to ensure player is fully loaded
            new org.bukkit.scheduler.BukkitRunnable() {
                @Override
                public void run() {
                    plugin.getPackServer().sendPack(event.getPlayer());
                }
            }.runTaskLater(plugin, 20L);
        }
    }

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInMainHand();

        if (plugin.isPlutusMace(item)) {
            event.setCancelled(true);
            abilityManager.handleAbility(player, player.isSneaking());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onAttack(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player attacker)) return;

        ItemStack item = attacker.getInventory().getItemInMainHand();
        if (plugin.isPlutusMace(item)) {
            abilityManager.handleBloodToll(attacker);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onVictimDamage(EntityDamageEvent event) {
        if (isInternalDamage) return;
        if (!(event.getEntity() instanceof Player victim)) return;

        // Damage sharing logic
        isInternalDamage = true;
        try {
            abilityManager.handleDamageSharing(victim, event.getFinalDamage());
        } finally {
            isInternalDamage = false;
        }
    }
}
