package org.kamilkhusainov.currency.infrastructure.db;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class CurrencyDatabaseInitializer {
    private final DataSource dataSource;
    public CurrencyDatabaseInitializer(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void init()
    {
        try (Connection connection = dataSource.getConnection()) {
            createTables(connection);
            insertValues(connection);
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
    private void createTables(Connection connection){
        String createCurrenciesTableSql = """
                    CREATE TABLE IF NOT EXISTS Currencies (
                    ID INTEGER PRIMARY KEY,
                    Code VARCHAR(100),
                    FullName VARCHAR(100),
                    Sign VARCHAR(100))""";
        String createExchangeRatesTableSql = """
                CREATE TABLE IF NOT EXISTS ExchangeRates (
                                    ID INTEGER PRIMARY KEY,
                                    BaseCurrencyId INTEGER,
                                    TargetCurrencyId INTEGER,
                                    Rate DECIMAL(6),
                                    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),
                                    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID)
                                );""";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(createCurrenciesTableSql);
            preparedStatement.execute();
            preparedStatement.close();
//            preparedStatement = connection.prepareStatement(createExchangeRatesTableSql);
//            preparedStatement.execute();
//            preparedStatement.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    private void insertValues(Connection connection) {
        String insertCurrenciesSql = """
                    INSERT OR IGNORE INTO Currencies (ID,Code,FullName,Sign) VALUES (?,?,?,?)""";
        String insertExchangeRatesSql = """
                    INSERT OR IGNORE INTO ExchangeRates (ID,BaseCurrencyId,TargetCurrencyId,Rate) VALUES (?,?,?,?)
                    """;

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertCurrenciesSql);
            preparedStatement.setInt(1,1);
            preparedStatement.setString(2,"AUD");
            preparedStatement.setString(3,"Australian dollar");
            preparedStatement.setString(4,"A$3333");

            preparedStatement.execute();
            preparedStatement.close();

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
