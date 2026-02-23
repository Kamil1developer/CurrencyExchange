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
                    ID INT AUTO_INCREMENT PRIMARY KEY,
                    Code VARCHAR(100) UNIQUE,
                    FullName VARCHAR(100) ,
                    Sign VARCHAR(100) )""";
        String createExchangeRatesTableSql = """
                CREATE TABLE IF NOT EXISTS ExchangeRates (
                                    INT AUTO_INCREMENT PRIMARY KEY,
                                    BaseCurrencyId INTEGER UNIQUE,
                                    TargetCurrencyId INTEGER UNIQUE,
                                    Rate DECIMAL(6) UNIQUE,
                                    FOREIGN KEY (BaseCurrencyId) REFERENCES Currencies(ID),
                                    FOREIGN KEY (TargetCurrencyId) REFERENCES Currencies(ID)
                                );""";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(createCurrenciesTableSql);
            preparedStatement.execute();
            preparedStatement = connection.prepareStatement(createExchangeRatesTableSql);
            preparedStatement.close();
//            PreparedStatement preparedStatement = connection.prepareStatement("DROP TABLE Currencies");
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
