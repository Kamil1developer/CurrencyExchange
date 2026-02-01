package org.kamilkhusainov.currency.infrastructure.db;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class CurrencyDatabaseInitializer {

    private final DataSource dataSource;
    private Connection connection;
    public CurrencyDatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init()
    {
        try {
            connection = dataSource.getConnection();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        createTables();
        insertValues();

    }
    private void createTables(){
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
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(createCurrenciesTableSql);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement(createExchangeRatesTableSql);
            preparedStatement.execute();
        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
    private void insertValues() {
        String insertCurrenciesSql = """
                    INSERT INTO Currencies (ID,Code,FullName,Sign) VALUES (?,?,?,?)""";
        String insertExchangeRatesSql = """
                    INSERT INTO ExchangeRates (ID,BaseCurrencyId,TargetCurrencyId,Rate) VALUES (?,?,?,?)
                    """;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertCurrenciesSql);
            preparedStatement.setInt(1,1);
            preparedStatement.setString(2,"AUD");
            preparedStatement.setString(3,"Australian dollar");
            preparedStatement.setString(4,"A$3333");

            preparedStatement.execute();

        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }
}
