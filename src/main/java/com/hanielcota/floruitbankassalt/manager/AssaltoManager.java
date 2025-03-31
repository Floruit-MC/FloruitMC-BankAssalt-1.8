package com.hanielcota.floruitbankassalt.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hanielcota.floruitbankassalt.FloruitBankAssaltPlugin;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public class AssaltoManager {

    private final FloruitBankAssaltPlugin plugin;
    private final BancoManager bancoManager;

    private final Cache<UUID, Long> cooldownCache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .build();

    public void iniciarAssalto(Player player, Runnable callback) {
        if (player == null) {
            if (callback != null) callback.run();
            return;
        }

        UUID uuid = player.getUniqueId();
        Long lastAttempt = cooldownCache.getIfPresent(uuid);

        if (lastAttempt != null) {

            if (callback != null) callback.run();
            return;
        }

        bancoManager.getTotalAsync(total -> {
            double minTotal = plugin.getConfig().getDouble("assalto.min-total", 1000);
            if (total < minTotal) {
                if (callback != null) callback.run();
                return;
            }

            ItemStack tnt = createCustomTNT();
            if (player.getInventory().firstEmpty() == -1) {
                if (callback != null) callback.run();
                return;

            }

            player.getInventory().addItem(tnt);
            cooldownCache.put(uuid, System.currentTimeMillis());
            if (callback != null) callback.run();
        });
    }

    private ItemStack createCustomTNT() {
        ItemStack tnt = new ItemStack(Material.TNT);
        ItemMeta meta = tnt.getItemMeta();
        if (meta == null) {
            return tnt;
        }

        meta.setDisplayName("§c§lTNT de Assalto");
        meta.setLore(List.of("§7Coloque no banco para tentar assaltar!"));
        tnt.setItemMeta(meta);
        return tnt;
    }

    public void processarAssalto(Player player, Runnable callback) {
        if (player == null) {
            if (callback != null) callback.run();
            return;
        }

        double chance = plugin.getConfig().getDouble("assalto.success-chance", 0.3);
        if (Math.random() > chance) {
            if (callback != null) callback.run();
            return;
        }

        bancoManager.getTotalAsync(total -> {
            double stealPercentage = plugin.getConfig().getDouble("assalto.steal-percentage", 0.1);
            double amount = total * stealPercentage;

            bancoManager.getTotalCache().put("TOTAL", total - amount);
            bancoManager.getBancoStorage().updateTotal(-amount);
            bancoManager.getEconomyService().deposit(player, amount);

            if (callback != null) callback.run();
        });
    }
}