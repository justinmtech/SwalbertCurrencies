package com.justinmtech.customcurrencies.persistence;

import com.justinmtech.customcurrencies.CustomCurrencies;
import com.justinmtech.customcurrencies.currencies.Currency;
import com.justinmtech.customcurrencies.currencies.PlayerModel;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DatabaseManager implements ManageData {
    private final CustomCurrencies plugin;
    private final FileManager fileManager;
    private final String username;
    private final String password;
    private final String host;
    private final int port;
    private final String database;
    private final String table;

    public DatabaseManager(CustomCurrencies plugin) {
        this.plugin = plugin;
        username = plugin.getConfigManager().getSqlUsername();
        password = plugin.getConfigManager().getSqlPassword();
        host = plugin.getConfigManager().getSqlHost();
        port = plugin.getConfigManager().getSqlPort();
        database = plugin.getConfigManager().getSqlDatabase();
        table = plugin.getConfigManager().getSqlTable();
        this.fileManager = new FileManager(plugin);
    }

    private boolean tableExists() {
        try {
            PreparedStatement pStat = getConnection().prepareStatement("SELECT * FROM information_schema.tables WHERE table_schema = ? AND table_name = ? LIMIT 1");
            pStat.setString(1, database);
            pStat.setString(2, table);
            pStat.execute();
            ResultSet rs = pStat.getResultSet();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Connection getConnection() {
        Connection conn;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
/*            String url = "jdbc:mysql://localhost:3306/" + database + "?"
                    + "autoReconnect=true&useSSL=false";*/
            String urlBasic = "jdbc:mysql://" + host + ":" + port + "/" + database;
            conn = DriverManager.getConnection(urlBasic, username, password);
        } catch (Exception e) {
            e.printStackTrace();
            conn = null;
        }
        return conn;
    }

    private void createTable() {
        try {
            PreparedStatement pStat = getConnection().prepareStatement("CREATE TABLE " + table + "(" +
                    "player_id VARCHAR(100) PRIMARY KEY" +
                    ")");
            pStat.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void insertCurrencyColumn(String name) {
        try {
            PreparedStatement pStat = getConnection().prepareStatement("ALTER TABLE " + table + " ADD " + name + " varchar(254) NOT NULL DEFAULT '0'");
            pStat.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private ResultSet getColumns() {
        String sql = "SELECT * FROM information_schema.columns WHERE table_schema = ? AND table_name = ?";
        try {
            PreparedStatement pStat = getConnection().prepareStatement(sql);
            pStat.setString(1, database);
            pStat.setString(2, table);
            pStat.execute();
            return pStat.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<String> getCurrencyColumns() {
        List<String> columns = new ArrayList<>();
        ResultSet rs = getColumns();
        int index = 1;
        try {
            while (rs.next()) {
                String column = rs.getString(4);
                columns.add(column);
                index++;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return columns;
    }

    private boolean isCurrencyInTable(String name) {
        List<String> currencyColumns = getCurrencyColumns();
            if (currencyColumns.contains(name)) {
                return true;
            }
        return false;
    }

    private List<String> getSqlCurrencyList() {
        List<Currency> currencies = plugin.getConfigManager().getCurrencies();
        List<String> sqlCurrencyFields = new ArrayList<>();
        for (Currency currency : currencies) {
            if (!currency.isLocalStorage()) {
                sqlCurrencyFields.add(currency.getName());
            }
        }
        return sqlCurrencyFields;
    }

    private boolean anyLocalCurrencies() {
        List<Currency> currencies = plugin.getConfigManager().getCurrencies();
        for (Currency currency : currencies) {
            if (currency.isLocalStorage()) {
                return true;
            }
        }
        return false;
    }

    private ResultSet executePreparedStatement(String sql, List<String> variables, int parameters) {
        try {
            PreparedStatement pStat = getConnection().prepareStatement(sql);
                for (int i = 0; i < parameters; i++) {
                    int parameterIndex = i + 1;
                    pStat.setString(parameterIndex, variables.get(i));
                }
                pStat.execute();
                return pStat.getResultSet();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getVariables() {
        List<String> currencyList = getSqlCurrencyList();
        String variables = "(player_id";
        for (int i = 0; i < currencyList.size(); i++) {
            if (i <= currencyList.size() - 1) {
            variables = variables.concat(", ");
            }
            variables = variables.concat(currencyList.get(i));
        }
        variables = variables.concat(")");
        return variables;
    }

    private String getValues() {
        List<String> variables = getSqlCurrencyList();
        String values = "(?, ";
        for (int i = 0; i < variables.size(); i++) {
            values = values.concat("0");
            if (i < variables.size() - 1) {
                values = values.concat(", ");
            }
        }
        values = values.concat(")");
        return values;
    }

    private String getInsertPlayerSql() {
        String valueSyntax = " VALUE";
        if (getSqlCurrencyList().size() > 1) {
            valueSyntax = valueSyntax.concat("S ");
        }
        return "INSERT " + table + getVariables() + valueSyntax + getValues();
    }

    private boolean doesPlayerExist(OfflinePlayer player) {
        String sql = "SELECT EXISTS (SELECT * FROM " + table + " WHERE player_id = ?)";
        ResultSet rs = executePreparedStatement(sql, Arrays.asList(player.getUniqueId().toString()), 1);
        try {
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void insertNewPlayer(OfflinePlayer player) {
        executePreparedStatement(getInsertPlayerSql(), Arrays.asList(player.getUniqueId().toString()), 1);
    }

    @Override
    public void initialSetup() {
        if (!tableExists()) {
            createTable();
        }
        for (String currency : getSqlCurrencyList()) {
            if (!isCurrencyInTable(currency)) {
                insertCurrencyColumn(currency);
            }
        }
        if (anyLocalCurrencies()) {
            fileManager.initialSetup();
        }
    }

    @Override
    public BigDecimal getBalance(OfflinePlayer player, Currency currency) {
        if (!currency.isLocalStorage()) {
            String sql = "SELECT " + currency.getName() + " FROM " + table + " WHERE " + "player_id = ?";
            ResultSet rs = executePreparedStatement(sql, Arrays.asList(player.getUniqueId().toString()), 1);
            try {
                if (rs.next()) {
                    BigDecimal balance = BigDecimal.valueOf(Double.parseDouble(rs.getString(1)));
                    return balance;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return null;
        } else {
            return fileManager.getBalance(player, currency);
        }
    }

    @Override
    public boolean setBalance(OfflinePlayer player, Currency currency, BigDecimal amount) {
        if (!currency.isLocalStorage()) {
            String sql = "UPDATE " + table + " SET " + currency.getName() + " = ? WHERE player_id = ?";
            try {
                PreparedStatement pStat = getConnection().prepareStatement(sql);
                pStat.setString(1, amount.toString());
                pStat.setString(2, player.getUniqueId().toString());
                pStat.execute();
                return true;
            } catch (SQLException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            return fileManager.setBalance(player, currency, amount);
        }
    }

    @Override
    public boolean payBalance(OfflinePlayer player1, OfflinePlayer player2, Currency currency, BigDecimal amount) {
        if (!currency.isLocalStorage()) {
            BigDecimal player1Balance = getBalance(player1, currency);
            BigDecimal player1NewBalance = player1Balance.subtract(amount);
            setBalance(player1, currency, player1NewBalance);

            BigDecimal player2Balance = getBalance(player2, currency);
            BigDecimal player2NewBalance = player2Balance.add(amount);
            setBalance(player2, currency, player2NewBalance);
            return true;
        } else {
            return fileManager.payBalance(player1, player2, currency, amount);
        }
    }

    @Override
    public boolean takeBalance(OfflinePlayer player, Currency currency, BigDecimal amount) {
        if (!currency.isLocalStorage()) {
            BigDecimal balance = getBalance(player, currency);
            BigDecimal newBalance = balance.subtract(amount);
            return setBalance(player, currency, newBalance);
        } else {
            return fileManager.takeBalance(player, currency, amount);
        }
    }

    @Override
    public boolean giveBalance(OfflinePlayer player, Currency currency, BigDecimal amount) {
        if (!currency.isLocalStorage()) {
            BigDecimal balance = getBalance(player, currency);
            BigDecimal newBalance = balance.add(amount);
            return setBalance(player, currency, newBalance);
        } else {
            return fileManager.giveBalance(player, currency, amount);
        }
    }

    @Override
    public boolean resetBalance(OfflinePlayer player, Currency currency) {
        if (!currency.isLocalStorage()) {
            return setBalance(player, currency, BigDecimal.ZERO);
        } else {
            return fileManager.resetBalance(player, currency);
        }
    }

    @Override
    public boolean savePlayer(Player player) {
        if (anyLocalCurrencies()) {
            fileManager.savePlayer(player);
            return true;
        }
        return true;
    }

    @Override
    public boolean saveOfflinePlayer(OfflinePlayer player, PlayerModel playerModel) {
        if (anyLocalCurrencies()) {
            fileManager.saveOfflinePlayer(player, playerModel);
            return true;
        }
        return true;
    }

    @Override
    public boolean savePlayers(List<Player> players) {
        if (anyLocalCurrencies()) {
            fileManager.savePlayers(players);
            return true;
        }
        return true;
    }

    @Override
    public boolean loadPlayer(Player player) {
        if (doesPlayerExist(player)) {
            insertNewPlayer(player);
        }

        if (anyLocalCurrencies()) {
            fileManager.loadPlayer(player);
        }
        return true;
    }
}
