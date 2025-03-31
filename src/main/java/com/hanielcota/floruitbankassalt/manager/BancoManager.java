package com.hanielcota.floruitbankassalt.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.hanielcota.floruitbankassalt.FloruitBankAssaltPlugin;
import com.hanielcota.floruitbankassalt.services.EconomyService;
import com.hanielcota.floruitbankassalt.storage.BancoStorage;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Getter
@RequiredArgsConstructor
public class BancoManager {

    private final FloruitBankAssaltPlugin plugin;
    private final EconomyService economyService;
    private final BancoStorage bancoStorage;

    private final Cache<UUID, Double> saldoCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1000)
            .build();

    private final Cache<String, Double> totalCache = Caffeine.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .maximumSize(1)
            .build();

    public void depositar(Player player, double quantia, Runnable callback) {
        if (player == null || economyService == null || bancoStorage == null || quantia <= 0) {
            runCallback(callback);
            return;
        }

        if (!economyService.hasFunds(player, quantia)) {
            runCallback(callback);
            return;
        }

        economyService.withdraw(player, quantia);
        UUID uuid = player.getUniqueId();

        getSaldoAsync(uuid, saldoAtual -> {
            double novoSaldo = saldoAtual + quantia;
            bancoStorage.saveSaldo(uuid, novoSaldo);
            saldoCache.put(uuid, novoSaldo);

            getTotalAsync(totalAtual -> {
                double novoTotal = totalAtual + quantia;
                bancoStorage.updateTotal(quantia);
                totalCache.put("TOTAL", novoTotal);
                runCallback(callback);
            });
        });
    }

    public void sacar(Player player, double quantia, Runnable callback) {
        if (player == null || economyService == null || bancoStorage == null || quantia <= 0) {
            runCallback(callback);
            return;
        }

        UUID uuid = player.getUniqueId();

        getSaldoAsync(uuid, saldoAtual -> {
            if (saldoAtual < quantia) {
                runCallback(callback);
                return;
            }

            double novoSaldo = saldoAtual - quantia;
            bancoStorage.saveSaldo(uuid, novoSaldo);
            saldoCache.put(uuid, novoSaldo);

            getTotalAsync(totalAtual -> {
                double novoTotal = totalAtual - quantia;
                bancoStorage.updateTotal(-quantia);
                totalCache.put("TOTAL", novoTotal);
                economyService.deposit(player, quantia);
                runCallback(callback);
            });
        });
    }

    public void getSaldoAsync(UUID uuid, BancoStorage.SaldoCallback callback) {
        if (uuid == null) {
            if (callback != null) callback.onResult(0.0);
            return;
        }

        if (callback == null) return;

        Double saldo = saldoCache.getIfPresent(uuid);
        if (saldo != null) {
            callback.onResult(saldo);
            return;
        }

        bancoStorage.getSaldoAsync(uuid, saldoLido -> {
            saldoCache.put(uuid, saldoLido);
            callback.onResult(saldoLido);
        });
    }

    public void getTotalAsync(BancoStorage.TotalCallback callback) {
        if (callback == null) return;

        Double total = totalCache.getIfPresent("TOTAL");
        if (total != null) {
            callback.onResult(total);
            return;
        }

        bancoStorage.getTotalAsync(fetchedTotal -> {
            totalCache.put("TOTAL", fetchedTotal);
            callback.onResult(fetchedTotal);
        });
    }

    private void runCallback(Runnable callback) {
        if (callback != null) callback.run();
    }
}
