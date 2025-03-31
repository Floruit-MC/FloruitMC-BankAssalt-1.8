package com.hanielcota.floruitbankassalt.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
@RequiredArgsConstructor
public class EconomyService {

    private final JavaPlugin plugin;
    private Economy economy;

    public boolean setupEconomy() {
        if (plugin.getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }

        RegisteredServiceProvider<Economy> rsp = plugin.getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }

        economy = rsp.getProvider();
        return economy != null;
    }

    public boolean hasFunds(Player player, double amount) {
        if (economy == null) {
            return false;
        }
        return economy.has(player, amount);
    }

    public void withdraw(Player player, double amount) {
        if (economy == null) {
            return;
        }
        economy.withdrawPlayer(player, amount);
    }

    public void deposit(Player player, double amount) {
        if (economy == null) {
            return;
        }
        economy.depositPlayer(player, amount);
    }
}