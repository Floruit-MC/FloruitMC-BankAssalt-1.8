package com.hanielcota.floruitbankassalt.commands;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.Default;
import co.aikar.commands.annotation.Subcommand;
import com.hanielcota.floruitbankassalt.FloruitBankAssaltPlugin;
import com.hanielcota.floruitbankassalt.manager.AssaltoManager;
import com.hanielcota.floruitbankassalt.manager.BancoManager;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

@CommandAlias("banco")
@RequiredArgsConstructor
public class BancoCommands extends BaseCommand {

    private final FloruitBankAssaltPlugin plugin;
    private final BancoManager bancoManager;
    private final AssaltoManager assaltoManager;

    @Default
    @CommandCompletion("depositar|sacar|saldo|total|assaltar")
    public void onDefault(Player player, String[] args) {
        if (player == null) {
            return;
        }
        String message = plugin.getConfig().getString("messages.default", "§e§lFLORUIT MC §f➔ §e✔ Use: /banco <depositar | sacar | saldo | total | assaltar>");
        player.sendMessage(message);
    }

    @Subcommand("depositar")
    @CommandCompletion("100")
    public void onDepositar(Player player, String[] args) {
        if (player == null) {
            return;
        }

        if (args.length < 1) {
            String message = plugin.getConfig().getString("messages.deposit-usage", "§c§lFLORUIT MC §f➔ §c✘ Use: /banco depositar <quantia>");
            player.sendMessage(message);
            return;
        }

        double quantia;
        try {
            quantia = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            String message = plugin.getConfig().getString("messages.invalid-amount", "§c§lFLORUIT MC §f➔ §c✘ Quantia inválida! Use números.");
            player.sendMessage(message);
            return;
        }

        bancoManager.depositar(player, quantia, () -> {
            if (!bancoManager.getEconomyService().hasFunds(player, quantia)) {
                String message = plugin.getConfig().getString("messages.deposit-error", "§c§lFLORUIT MC §f➔ §c✘ Erro ao depositar! Verifique seus fundos ou a quantia.");
                player.sendMessage(message);
                return;
            }

            bancoManager.getSaldoAsync(player.getUniqueId(), saldo -> {
                String message = plugin.getConfig().getString("messages.deposit-success", "§a§lFLORUIT MC §f➔ §a✔ Depositado {amount} com sucesso! Saldo no banco: {balance}")
                        .replace("{amount}", String.valueOf(quantia))
                        .replace("{balance}", String.valueOf(saldo));
                player.sendMessage(message);
            });
        });
    }

    @Subcommand("sacar")
    @CommandCompletion("100")
    public void onSacar(Player player, String[] args) {
        if (player == null) {
            return;
        }

        if (args.length < 1) {
            String message = plugin.getConfig().getString("messages.withdraw-usage", "§c§lFLORUIT MC §f➔ §c✘ Use: /banco sacar <quantia>");
            player.sendMessage(message);
            return;
        }

        double quantia;
        try {
            quantia = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            String message = plugin.getConfig().getString("messages.invalid-amount", "§c§lFLORUIT MC §f➔ §c✘ Quantia inválida! Use números.");
            player.sendMessage(message);
            return;
        }

        bancoManager.sacar(player, quantia, () -> bancoManager.getSaldoAsync(player.getUniqueId(), saldoAtual -> {
            if (saldoAtual < quantia) {
                String message = plugin.getConfig().getString("messages.withdraw-error", "§c§lFLORUIT MC §f➔ §c✘ Erro ao sacar! Verifique seu saldo.");
                player.sendMessage(message);
                return;
            }

            String message = plugin.getConfig().getString("messages.withdraw-success", "§a§lFLORUIT MC §f➔ §a✔ Sacado {amount} com sucesso! Saldo no banco: {balance}")
                    .replace("{amount}", String.valueOf(quantia))
                    .replace("{balance}", String.valueOf(saldoAtual));
            player.sendMessage(message);
        }));
    }

    @Subcommand("saldo")
    public void onSaldo(Player player, String[] args) {
        if (player == null) {
            return;
        }

        bancoManager.getSaldoAsync(player.getUniqueId(), saldo -> {
            String message = plugin.getConfig().getString("messages.balance", "§a§lFLORUIT MC §f➔ §a✔ Seu saldo no banco é: {balance}")
                    .replace("{balance}", String.valueOf(saldo));
            player.sendMessage(message);
        });
    }

    @Subcommand("total")
    public void onTotal(Player player, String[] args) {
        if (player == null) {
            return;
        }

        bancoManager.getTotalAsync(total -> {
            String message = plugin.getConfig().getString("messages.total", "§a§lFLORUIT MC §f➔ §a✔ Total guardado no banco (todos os jogadores): {total}")
                    .replace("{total}", String.valueOf(total));
            player.sendMessage(message);
        });
    }

    @Subcommand("assaltar")
    public void onAssaltar(Player player, String[] args) {
        if (player == null) {
            return;
        }

        assaltoManager.iniciarAssalto(player, () -> {
            String message = plugin.getConfig().getString("messages.assault-start-success", "§a§lFLORUIT MC §f➔ §a✔ TNT de Assalto recebida! Coloque-a no banco.");
            player.sendMessage(message);
        });
    }
}