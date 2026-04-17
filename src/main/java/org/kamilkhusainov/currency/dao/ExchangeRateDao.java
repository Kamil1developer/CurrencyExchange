package org.kamilkhusainov.currency.dao;
import org.kamilkhusainov.currency.entity.ExchangeRateEntity;
import org.kamilkhusainov.currency.exceptions.AlreadyExistsException;
import org.kamilkhusainov.currency.exceptions.DataBaseException;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.sqlite.SQLiteErrorCode;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ExchangeRateDao {
    private static final String FIND_RATE_BY_CURRENCY_IDS_SQL = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
    private static final String INSERT_EXCHANGE_RATE_IF_NOT_EXISTS_SQL =
            "INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) " +
                    "SELECT ?, ?, ? " +
                    "WHERE NOT EXISTS (" +
                    "    SELECT 1 FROM ExchangeRates " +
                    "    WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?" +
                    ")";
    private static final String FIND_EXCHANGE_RATE_ID_BY_CURRENCY_IDS_SQL = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?";
    private static final String FIND_EXCHANGE_RATES_ID_BY_CURRENCY_IDS_SQL = "SELECT * FROM ExchangeRates";
    private static final String FIND_ALL_BY_BASE_CURRENCY_ID_SQL = "SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ?";
    private static final String UPDATE_RATE_BY_ID_SQL = "UPDATE ExchangeRates SET Rate = ? WHERE ID = ?";

    private final DataSource dataSource;

    public ExchangeRateDao(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public BigDecimal getRate(long baseCurrencyId, long targetCurrencyId){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_RATE_BY_CURRENCY_IDS_SQL);
            preparedStatement.setLong(1, baseCurrencyId);
            preparedStatement.setLong(2, targetCurrencyId);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
                return resultSet.getBigDecimal("Rate");
            }
            throw new DataBaseException("Rate не имеет значения ");
        }
        catch (SQLException e){
            throw new RuntimeException();
        }
    }

    public int insert(long baseCurrencyId, long targetCurrencyId, BigDecimal rate){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_EXCHANGE_RATE_IF_NOT_EXISTS_SQL);
            preparedStatement.setLong(1, baseCurrencyId);
            preparedStatement.setLong(2, targetCurrencyId);
            preparedStatement.setBigDecimal(3, rate);
            preparedStatement.setLong(4, baseCurrencyId);
            preparedStatement.setLong(5, targetCurrencyId);
            int rows = preparedStatement.executeUpdate();
            if (rows == 0){
                throw new AlreadyExistsException(SQLiteErrorCode.SQLITE_CONSTRAINT.message);
            }
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
            return resultSet.getInt(1);
        }
        catch (SQLException e) {
            throw new AlreadyExistsException(SQLiteErrorCode.SQLITE_CONSTRAINT.message,e);
        }
    }

    public long findByCodes(long baseCurrencyId,long targetCurrencyId){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_EXCHANGE_RATE_ID_BY_CURRENCY_IDS_SQL);
            preparedStatement.setLong(1, baseCurrencyId);
            preparedStatement.setLong(2, targetCurrencyId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getLong("ID");

        }
        catch (SQLException e){
            throw new RuntimeException();
        }
    }

    public List<ExchangeRateEntity> findAll(){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_EXCHANGE_RATES_ID_BY_CURRENCY_IDS_SQL);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExchangeRateEntity> entityList = new LinkedList<>();
            while (resultSet.next()){
                ExchangeRateEntity entity = new ExchangeRateEntity(resultSet.getLong("ID"),
                        resultSet.getInt("BaseCurrencyId"),
                        resultSet.getInt("TargetCurrencyId"),resultSet.getBigDecimal("Rate"));
                entityList.add(entity);
            }
            resultSet.close();
            return entityList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ExchangeRateEntity> findAllByBaseCurrencyID(long baseCurrencyId) {
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_BY_BASE_CURRENCY_ID_SQL);
            preparedStatement.setLong(1, baseCurrencyId);
            ResultSet resultSet = preparedStatement.executeQuery();
            List<ExchangeRateEntity> entityList = new LinkedList<>();
            while (resultSet.next()) {
                ExchangeRateEntity entity = new ExchangeRateEntity(resultSet.getLong("ID"),
                        resultSet.getInt("BaseCurrencyId"),
                        resultSet.getInt("TargetCurrencyId"), resultSet.getBigDecimal("Rate"));
                entityList.add(entity);
            }
            resultSet.close();
            return entityList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void update(long id, String rate){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_RATE_BY_ID_SQL);
            preparedStatement.setBigDecimal(1, new BigDecimal(rate));
            preparedStatement.setLong(2, id);
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code){
                throw new DataBaseException(SQLiteErrorCode.SQLITE_CONSTRAINT.message,e);
            }
        }
    }
}
