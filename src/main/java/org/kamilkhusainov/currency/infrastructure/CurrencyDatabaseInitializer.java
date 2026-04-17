package org.kamilkhusainov.currency.infrastructure;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
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
        catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }

    }
    private void createTables(Connection connection) throws SQLException, IOException {
        String schemaSql = loadSchemaSql();

        String[] statements = schemaSql.split(";");

        for (String statement : statements) {
            String sql = statement.trim();

            if (!sql.isEmpty()) {
                try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
                    preparedStatement.execute();
                }

            }

        }
    }

    private String loadSchemaSql() throws IOException{
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("db/schema.sql")) {

            if (inputStream == null) {

                throw new IOException("Файл schema.sql не найден в resources");

            }

            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        }
    }

    private void insertValues(Connection connection) {
        String insertCurrenciesSql = """
                    INSERT OR IGNORE INTO Currencies (Code,FullName,Sign) VALUES (?,?,?)""";

        try {
            PreparedStatement preparedStatement = connection.prepareStatement(insertCurrenciesSql);
            preparedStatement.setString(1,"AUD");
            preparedStatement.setString(2,"Australian dollar");
            preparedStatement.setString(3,"A33");
            preparedStatement.execute();

            preparedStatement.close();

        }
        catch (SQLException e){
            throw new RuntimeException(e);
        }
    }
}
