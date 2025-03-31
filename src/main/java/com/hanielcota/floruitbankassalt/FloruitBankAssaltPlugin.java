package com.hanielcota.floruitbankassalt;

import co.aikar.commands.BukkitCommandManager;
import com.hanielcota.floruitbankassalt.commands.BancoCommands;
import com.hanielcota.floruitbankassalt.listener.AssaltoListener;
import com.hanielcota.floruitbankassalt.manager.AssaltoManager;
import com.hanielcota.floruitbankassalt.manager.BancoManager;
import com.hanielcota.floruitbankassalt.services.EconomyService;
import com.hanielcota.floruitbankassalt.storage.BancoStorage;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public class FloruitBankAssaltPlugin extends JavaPlugin {

    private EconomyService economyService;
    private BancoStorage bancoStorage;
    private BancoManager bancoManager;
    private AssaltoManager assaltoManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        if (!setupDependencies()) {
            getLogger().severe("Erro ao iniciar dependências! Desativando plugin...");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        setupCommands();
        setupListeners();
        getLogger().info("FloruitBankAssaltPlugin habilitado com sucesso!");
    }

    @Override
    public void onDisable() {
        if (bancoStorage != null) {
            bancoStorage.close();
        }
        getLogger().info("FloruitBankAssaltPlugin desabilitado!");
    }

    private boolean setupDependencies() {
        economyService = new EconomyService(this);
        if (!economyService.setupEconomy()) {
            return false;
        }

        bancoStorage = new BancoStorage(this);
        bancoStorage.initialize();
        bancoManager = new BancoManager(this, economyService, bancoStorage);
        assaltoManager = new AssaltoManager(this, bancoManager);
        return true;
    }

    private void setupCommands() {
        BukkitCommandManager commandManager = new BukkitCommandManager(this);
        commandManager.registerCommand(new BancoCommands(this, bancoManager, assaltoManager));
    }

    private void setupListeners() {
        getServer().getPluginManager().registerEvents(new AssaltoListener(this, assaltoManager), this);
    }
}