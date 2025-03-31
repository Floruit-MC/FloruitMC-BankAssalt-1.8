package com.hanielcota.floruitbankassalt.storage;

import com.hanielcota.floruitbankassalt.FloruitBankAssaltPlugin;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class BancoStorage {

    private final FloruitBankAssaltPlugin plugin;
    private HikariDataSource dataSource;

    public void initialize() {
        FileConfiguration config = plugin.getConfig();
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getString("mysql.url", "jdbc:mysql://localhost:3306/banco?useSSL=false"));
        hikariConfig.setUsername(config.getString("mysql.username", "root"));
        hikariConfig.setPassword(config.getString("mysql.password", ""));
        hikariConfig.setDriverClassName("com.mysql.cj.jdbc.Driver");
        hikariConfig.setMaximumPoolSize(config.getInt("mysql.pool-size", 10));
        dataSource = new HikariDataSource(hikariConfig);

        createTable();
    }

    private void createTable() {
        if (dataSource == null) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "CREATE TABLE IF NOT EXISTS banco (" +
                                     "uuid VARCHAR(36) PRIMARY KEY, " +
                                     "saldo DOUBLE NOT NULL DEFAULT 0, " +
                                     "total DOUBLE NOT NULL DEFAULT 0)")) {
                    stmt.executeUpdate();

                    try (PreparedStatement totalStmt = conn.prepareStatement(
                            "INSERT IGNORE INTO banco (uuid, saldo, total) VALUES ('TOTAL', 0, 0)")) {
                        totalStmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    plugin.getLogger().severe("Erro ao criar tabela: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void saveSaldo(UUID uuid, double saldo) {
        if (dataSource == null || uuid == null) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "INSERT INTO banco (uuid, saldo) VALUES (?, ?) ON DUPLICATE KEY UPDATE saldo = ?")) {
                    stmt.setString(1, uuid.toString());
                    stmt.setDouble(2, saldo);
                    stmt.setDouble(3, saldo);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().severe("Erro ao salvar saldo: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void getSaldoAsync(UUID uuid, SaldoCallback callback) {
        if (dataSource == null || uuid == null || callback == null) {
            callback.onResult(0.0);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                double saldo = 0.0;
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("SELECT saldo FROM banco WHERE uuid = ?")) {
                    stmt.setString(1, uuid.toString());
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        saldo = rs.getDouble("saldo");
                    }
                } catch (SQLException e) {
                    plugin.getLogger().severe("Erro ao consultar saldo: " + e.getMessage());
                }

                double finalSaldo = saldo;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        callback.onResult(finalSaldo);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void updateTotal(double amount) {
        if (dataSource == null) {
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(
                             "UPDATE banco SET total = total + ? WHERE uuid = 'TOTAL'")) {
                    stmt.setDouble(1, amount);
                    stmt.executeUpdate();
                } catch (SQLException e) {
                    plugin.getLogger().severe("Erro ao atualizar total: " + e.getMessage());
                }
            }
        }.runTaskAsynchronously(plugin);
    }

    public void getTotalAsync(TotalCallback callback) {
        if (dataSource == null || callback == null) {
            callback.onResult(0.0);
            return;
        }

        new BukkitRunnable() {
            @Override
            public void run() {
                double total = 0.0;
                try (Connection conn = dataSource.getConnection();
                     PreparedStatement stmt = conn.prepareStatement("SELECT total FROM banco WHERE uuid = 'TOTAL'")) {
                    ResultSet rs = stmt.executeQuery();
                    if (rs.next()) {
                        total = rs.getDouble("total");
                    }
                } catch (SQLException e) {
                    plugin.getLogger().severe("Erro ao consultar total: " + e.getMessage());
                }

                double finalTotal = total;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        callback.onResult(finalTotal);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void close() {
        if (dataSource == null) {
            return;
        }
        dataSource.close();
    }

    // Interfaces de Callback
    public interface SaldoCallback {
        void onResult(double saldo);
    }

    public interface TotalCallback {
        void onResult(double total);
    }
}