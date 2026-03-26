package org.kamilkhusainov.currency.infrastructure;
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
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    Code TEXT UNIQUE,
                    FullName TEXT,
                    Sign TEXT
                );
                """;
        String createExchangeRatesTableSql = """
                CREATE TABLE IF NOT EXISTS ExchangeRates (
                    ID INTEGER PRIMARY KEY AUTOINCREMENT,
                    BaseCurrencyId INTEGER,
                    TargetCurrencyId INTEGER,
                    Rate NUMERIC NOT NULL, UNIQUE(BaseCurrencyId, TargetCurrencyId),
                    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),
                    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID)
                );
                """;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(createCurrenciesTableSql);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement(createExchangeRatesTableSql);
            preparedStatement.execute();
            preparedStatement.close();
//            PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE Currencies");
//            preparedStatement.execute();
//            preparedStatement = connection.prepareStatement("DROP TABLE ExchangeRates");
//            preparedStatement.execute();
//            preparedStatement.close();
        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
    private void insertValues(Connection connection) {
        String insertCurrenciesSql = """
                    INSERT OR IGNORE INTO Currencies (Code,FullName,Sign) VALUES (?,?,?)""";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertCurrenciesSql);
            preparedStatement.setString(1,"AUD");
            preparedStatement.setString(2,"Australian dollar");
            preparedStatement.setString(3,"A$3333");
            preparedStatement.execute();

            preparedStatement.close();

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
