package org.kamilkhusainov.currency.dao;
import org.kamilkhusainov.currency.entity.ExchangeRateEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateRow;
import org.kamilkhusainov.currency.exceptions.AlreadyExistsException;
import org.kamilkhusainov.currency.exceptions.DataBaseException;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.kamilkhusainov.currency.exceptions.NotFoundException;
import org.sqlite.SQLiteErrorCode;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDao {
    private static final String FIND_RATE_BY_CURRENCY_IDS_SQL = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
    private static final String INSERT_EXCHANGE_RATE_IF_NOT_EXISTS_SQL =
            "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) " +
                    "SELECT ?, ?, ? " +
                    "WHERE NOT EXISTS (" +
                    "    SELECT 1 FROM ExchangeRates " +
                    "    WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?" +
                    ")";
    private static final String FIND_ALL_BY_BASE_CURRENCY_ID_WITH_CURRENCIES_SQL =
            "SELECT " +
                "er.ID AS ExchangeRateId, " +
                "bc.ID AS BaseCurrencyId, " +
                "bc.Code AS BaseCurrencyCode, " +
                "bc.FullName AS BaseCurrencyName, " +
                "bc.Sign AS BaseCurrencySign, " +
                "tc.ID AS TargetCurrencyId, " +
                "tc.Code AS TargetCurrencyCode, " +
                "tc.FullName AS TargetCurrencyName, " +
                "tc.Sign AS TargetCurrencySign, " +
                "er.Rate AS Rate " +
                "FROM ExchangeRates er " +
                "JOIN Currencies bc ON er.BaseCurrencyId = bc.ID " +
                "JOIN Currencies tc ON er.TargetCurrencyId = tc.ID " +
                "WHERE er.BaseCurrencyId = ?";
    private static final String UPDATE_RATE_BY_ID_SQL = "UPDATE ExchangeRates SET Rate = ? WHERE ID = ?";
    private static final String FIND_ALL_WITH_CURRENCIES_SQL =
            "SELECT " +
                    "er.ID AS ExchangeRateId, " +
                    "bc.ID AS BaseCurrencyId, " +
                    "bc.Code AS BaseCurrencyCode, " +
                    "bc.FullName AS BaseCurrencyName, " +
                    "bc.Sign AS BaseCurrencySign, " +
                    "tc.ID AS TargetCurrencyId, " +
                    "tc.Code AS TargetCurrencyCode, " +
                    "tc.FullName AS TargetCurrencyName, " +
                    "tc.Sign AS TargetCurrencySign, " +
                    "er.Rate AS Rate " +
                    "FROM ExchangeRates er " +
                    "JOIN Currencies bc ON er.BaseCurrencyId = bc.ID " +
                    "JOIN Currencies tc ON er.TargetCurrencyId = tc.ID";
    private static final String FIND_BY_CODES_WITH_CURRENCIES_SQL =
            "SELECT " +
                    "er.ID AS ExchangeRateId, " +
                    "bc.ID AS BaseCurrencyId, " +
                    "bc.Code AS BaseCurrencyCode, " +
                    "bc.FullName AS BaseCurrencyName, " +
                    "bc.Sign AS BaseCurrencySign, " +
                    "tc.ID AS TargetCurrencyId, " +
                    "tc.Code AS TargetCurrencyCode, " +
                    "tc.FullName AS TargetCurrencyName, " +
                    "tc.Sign AS TargetCurrencySign, " +
                    "er.Rate AS Rate " +
                    "FROM ExchangeRates er " +
                    "JOIN Currencies bc ON er.BaseCurrencyId = bc.ID " +
                    "JOIN Currencies tc ON er.TargetCurrencyId = tc.ID " +
                    "WHERE bc.Code = ? AND tc.Code = ?";


    private final DataSource dataSource;

    public ExchangeRateDao(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public BigDecimal getRate(long baseCurrencyId, long targetCurrencyId) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_RATE_BY_CURRENCY_IDS_SQL);
            preparedStatement.setLong(1, baseCurrencyId);
            preparedStatement.setLong(2, targetCurrencyId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return resultSet.getBigDecimal("Rate");
            }
            throw new DataBaseException("Rate не имеет значения ");
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    public int insert(long baseCurrencyId, long targetCurrencyId, BigDecimal rate) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE_IF_NOT_EXISTS_SQL);
            preparedStatement.setLong(1, baseCurrencyId);
            preparedStatement.setLong(2, targetCurrencyId);
            preparedStatement.setBigDecimal(3, rate);
            preparedStatement.setLong(4, baseCurrencyId);
            preparedStatement.setLong(5, targetCurrencyId);
            int rows = preparedStatement.executeUpdate();
            if (rows == 0) {
                throw new AlreadyExistsException(SQLiteErrorCode.SQLITE_CONSTRAINT.message);
            }
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return resultSet.getInt(1);
        } catch (SQLException e) {
            throw new AlreadyExistsException(SQLiteErrorCode.SQLITE_CONSTRAINT.message, e);
        }
    }


    public List<ExchangeRateRow> findAll() {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_WITH_CURRENCIES_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExchangeRateRow> entityList = new LinkedList<>();
            while (resultSet.next()) {
                ExchangeRateRow exchangeRateRow = new ExchangeRateRow(
                        resultSet.getLong("ExchangeRateId"),
                        resultSet.getLong("BaseCurrencyId"),
                        resultSet.getString("BaseCurrencyCode"),
                        resultSet.getString("BaseCurrencyName"),
                        resultSet.getString("BaseCurrencySign"),
                        resultSet.getLong("TargetCurrencyId"),
                        resultSet.getString("TargetCurrencyCode"),
                        resultSet.getString("TargetCurrencyName"),
                        resultSet.getString("TargetCurrencySign"),
                        resultSet.getBigDecimal("Rate"));
                entityList.add(exchangeRateRow);
            }
            resultSet.close();
            return entityList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ExchangeRateRow> findAllByBaseCurrencyID(long baseCurrencyId) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_BASE_CURRENCY_ID_WITH_CURRENCIES_SQL)) {

            preparedStatement.setLong(1, baseCurrencyId);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<ExchangeRateRow> rowList = new LinkedList<>();

                while (resultSet.next()) {
                    ExchangeRateRow row = new ExchangeRateRow(
                            resultSet.getLong("ExchangeRateId"),
                            resultSet.getLong("BaseCurrencyId"),
                            resultSet.getString("BaseCurrencyCode"),
                            resultSet.getString("BaseCurrencyName"),
                            resultSet.getString("BaseCurrencySign"),
                            resultSet.getLong("TargetCurrencyId"),
                            resultSet.getString("TargetCurrencyCode"),
                            resultSet.getString("TargetCurrencyName"),
                            resultSet.getString("TargetCurrencySign"),
                            resultSet.getBigDecimal("Rate")
                    );
                    rowList.add(row);
                }

                return rowList;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

        public void update(long id, BigDecimal rate) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RATE_BY_ID_SQL);
            preparedStatement.setBigDecimal(1, rate);
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
                throw new DataBaseException(SQLiteErrorCode.SQLITE_CONSTRAINT.message, e);
            }
        }
    }

    public ExchangeRateRow findByCodesWithCurrencies(String baseCurrencyCode, String targetCurrencyCode) {
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(FIND_BY_CODES_WITH_CURRENCIES_SQL)) {

            preparedStatement.setString(1, baseCurrencyCode);
            preparedStatement.setString(2, targetCurrencyCode);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    return new ExchangeRateRow(
                            resultSet.getLong("ExchangeRateId"),
                            resultSet.getLong("BaseCurrencyId"),
                            resultSet.getString("BaseCurrencyCode"),
                            resultSet.getString("BaseCurrencyName"),
                            resultSet.getString("BaseCurrencySign"),
                            resultSet.getLong("TargetCurrencyId"),
                            resultSet.getString("TargetCurrencyCode"),
                            resultSet.getString("TargetCurrencyName"),
                            resultSet.getString("TargetCurrencySign"),
                            resultSet.getBigDecimal("Rate")
                    );
                }
            }
            throw new NotFoundException(ErrorMessages.EXCHANGE_RATE_NOT_FOUND);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

}


