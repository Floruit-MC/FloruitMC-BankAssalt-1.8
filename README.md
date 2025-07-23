# 🏦 FloruitBankAssalt

O **FloruitBankAssalt** é um plugin completo e dinâmico de economia bancária para servidores Minecraft, ideal para servidores com foco em roleplay, factions ou survival com economia. Ele permite que jogadores depositem, saquem, visualizem seus saldos e até mesmo assaltem o banco, promovendo eventos intensos e estratégicos!

---

## 📦 Funcionalidades

* 💰 Sistema bancário pessoal por jogador.
* 🧾 Comandos simples e diretos para saque, depósito, saldo e total global.
* 💣 Sistema de **assalto ao banco** com mecânicas únicas.
* 🔒 Verificações robustas de saldo e permissões.
* 🎯 Feedbacks e mensagens configuráveis e estilizadas via `config.yml`.
* ⛏️ Proteções contra abusos e exploits com cooldowns e checagens de condições.
* 🧠 Código leve, assíncrono e otimizado para desempenho.
* 🔗 Integração com Vault para compatibilidade com economias existentes.

---

## 🛠️ Comandos

Todos os comandos podem ser usados com `/banco` ou `/bank`, e têm suporte a permissões personalizadas.

| Comando                      | Descrição                                                                                     |
| ---------------------------- | --------------------------------------------------------------------------------------------- |
| `/banco`                     | Exibe ajuda e subcomandos disponíveis.                                                        |
| `/banco depositar <quantia>` | Deposita dinheiro da carteira do jogador para o banco.                                        |
| `/banco sacar <quantia>`     | Retira dinheiro do banco do jogador para sua carteira.                                        |
| `/banco saldo`               | Exibe o saldo atual do banco do jogador.                                                      |
| `/banco total`               | Exibe o valor total acumulado no banco por todos os jogadores.                                |
| `/banco assaltar`            | Inicia um evento de assalto ao banco. Envolve entrega de TNT especial ou itens configuráveis. |

---

## 🔐 Permissões

| Permissão       | Descrição                                                    |
| --------------- | ------------------------------------------------------------ |
| `bank.use`      | Permite uso geral do comando `/banco`.                       |
| `bank.deposit`  | Permite depositar dinheiro.                                  |
| `bank.withdraw` | Permite sacar dinheiro.                                      |
| `bank.balance`  | Permite consultar o próprio saldo.                           |
| `bank.total`    | Permite visualizar o total global.                           |
| `bank.rob`      | Permite iniciar um assalto ao banco.                         |
| `bank.admin`    | Permissão total para administrar o banco (futuras features). |

---

## 🧨 Sistema de Assalto

O evento de assalto é uma mecânica diferenciada:

* Pode ser ativado com `/banco assaltar`.
* O jogador recebe um item especial (como TNT customizada).
* Um cooldown ou tempo de espera pode ser configurado entre assaltos.
* Pode exigir requisitos como estar em PvP, ou com X jogadores online.
* Mensagens e recompensas são **personalizáveis**.

---

## ⚙️ Configuração (`config.yml`)

```yaml
cooldown-assalto-segundos: 3600 # Cooldown de 1 hora entre assaltos

mensagens:
  ajuda: "§aUse /banco depositar <quantia>, /sacar <quantia>, /saldo, /total ou /assaltar"
  deposito: "§aVocê depositou §f{quantia} §ano seu banco."
  saque: "§aVocê sacou §f{quantia} §ado seu banco."
  saldo: "§eSeu saldo no banco é de §f{saldo}"
  total: "§eSaldo total no banco de todos os jogadores: §f{total}"
  sem-dinheiro: "§cVocê não tem dinheiro suficiente."
  sem-saldo: "§cVocê não tem saldo suficiente no banco."
  assalto-iniciado: "§c[ALERTA] Um assalto ao banco foi iniciado por {jogador}!"
  assalto-tnt-entregue: "§eVocê recebeu uma TNT especial! Use com sabedoria."
  assalto-cooldown: "§cO banco está se recuperando de um assalto recente. Tente novamente em {tempo}."
```

---

## 💡 Recomendações

* Utilize com **Vault + um plugin de economia confiável** como EssentialsX ou CMI.
* Para balanceamento, limite o valor de assalto ou crie condições personalizadas.
* Combine com plugins de PvP, território ou guerra para tornar o evento mais competitivo.

---

## 📂 Estrutura Interna (Dev)

| Elemento         | Função                                           |
| ---------------- | ------------------------------------------------ |
| `BankManager`    | Gerencia saldos dos jogadores.                   |
| `RobberyManager` | Controla cooldown, evento de assalto e entregas. |
| `ConfigManager`  | Carrega mensagens e tempos da `config.yml`.      |
| `VaultHook`      | Integra o plugin à economia do servidor.         |
| `BancoCommand`   | Lida com o roteamento de subcomandos.            |

---

## 📥 Instalação

1. Coloque o `.jar` do plugin na pasta `/plugins`.
2. Reinicie o servidor.
3. Configure o `config.yml` se desejar mensagens ou regras personalizadas.
4. Certifique-se que o plugin **Vault** está instalado e funcional.

---

## ✅ Dependências

* [Vault](https://www.spigotmc.org/resources/vault.34315/) (obrigatória para economia)
