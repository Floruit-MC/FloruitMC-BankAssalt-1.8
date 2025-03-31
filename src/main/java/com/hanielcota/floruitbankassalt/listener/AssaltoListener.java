package com.hanielcota.floruitbankassalt.listener;

import com.hanielcota.floruitbankassalt.FloruitBankAssaltPlugin;
import com.hanielcota.floruitbankassalt.manager.AssaltoManager;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

public class AssaltoListener implements Listener {

    private final FloruitBankAssaltPlugin plugin;
    private final AssaltoManager assaltoManager;

    public AssaltoListener(FloruitBankAssaltPlugin plugin, AssaltoManager assaltoManager) {
        this.plugin = plugin;
        this.assaltoManager = assaltoManager;
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!event.getBlock().getType().equals(Material.TNT)) {
            return;
        }

        Player player = event.getPlayer();
        if (player == null) {
            return;
        }

        ItemStack item = event.getItemInHand();
        if (!item.hasItemMeta() || !item.getItemMeta().getDisplayName().equals("§c§lTNT de Assalto")) {
            return;
        }

        event.setCancelled(true);
        Location location = event.getBlock().getLocation();

        location.getWorld().playEffect(location, Effect.SMOKE, 4);
        location.getWorld().playEffect(location, Effect.MOBSPAWNER_FLAMES, 0);
        location.getWorld().playSound(location, Sound.CREEPER_HISS, 1.0f, 1.0f);

        assaltoManager.processarAssalto(player, () -> assaltoManager.getBancoManager().getTotalAsync(total -> {
            String message = plugin.getConfig()
                    .getString("messages.assault-success",
                            "§a§lFLORUIT MC §f➔ §a✔ Assalto bem-sucedido! Novo total no banco: {total}")
                    .replace("{total}", String.valueOf(total));
            player.sendMessage(message);
        }));

        assaltoManager.processarAssalto(player, null);
        String message = plugin.getConfig().getString("messages.assault-failed", "§c§lFLORUIT MC §f➔ §c✘ Assalto falhou!");
        player.sendMessage(message);
    }
}