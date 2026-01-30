package org.kamilkhusainov.currency.db;
import java.sql.*;

public class DatabaseInitializer {
    private final String url;
    public DatabaseInitializer(String url){
        this.url = url;
    }

    public void init()
    {
        try {
            createTables();
            insertValues();
        }
        catch (SQLException e){
            e.printStackTrace();
        }

    }
    private void createTables() throws SQLException {
        String createCurrenciesTableSql = """
                    CREATE TABLE IF NOT EXISTS Currencies (
                    ID INT PRIMARY KEY,
                    Code VARCHAR(100),
                    FullName VARCHAR(100),
                    Sign VARCHAR(100))""";
        String createExchangeRatesTableSql = """
                    CREATE TABLE IF NOT EXISTS ExchangeRates (
                    ID INT PRIMARY KEY,
                    BaseCurrencyId INT,FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),
                    TargetCurrencyId INT,FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID),
                    Rate Decimal(6))""";

        Connection connection = DriverManager.getConnection(url);
        PreparedStatement preparedStatement = connection.prepareStatement(createCurrenciesTableSql);
        preparedStatement.execute();
        preparedStatement = connection.prepareStatement(createExchangeRatesTableSql);
        preparedStatement.execute();
    }
    private void insertValues() {
        String insertCurrenciesSql = """
                    INSERT INTO Currencies (ID,Code,FullName,Sign) VALUES (?,?,?,?)""";
        String insertExchangeRatesSql = """
                    INSERT INTO ExchangeRates (ID,BaseCurrencyId,TargetCurrencyId,Rate) VALUES (?,?,?,?)
                    """;

        try {
            Connection connection = DriverManager.getConnection(url);
            PreparedStatement preparedStatement = connection.prepareStatement(insertCurrenciesSql);
            preparedStatement.setInt(1,1);
            preparedStatement.setString(2,"AUD");
            preparedStatement.setString(3,"Australian dollar");
            preparedStatement.setString(1,"A$3333");

            preparedStatement.execute();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    }
