package org.kamilkhusainov.currency.dao;

import org.kamilkhusainov.currency.CurrencyConstants;
import org.kamilkhusainov.currency.dto.ExchangeRateDto;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateEntity;
import org.kamilkhusainov.currency.exceptions.DaoException;

import javax.sql.DataSource;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class ExchangeRateDao {
    private final DataSource dataSource;
    public ExchangeRateDao(DataSource dataSource){
        this.dataSource = dataSource;
    }
    public int insert(long baseCurrencyId, long targetCurrencyId, BigDecimal rate){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) " +
                    "SELECT ?, ?, ? " +
                    "WHERE NOT EXISTS (" +
                    "   SELECT 1 FROM ExchangeRates " +
                    "   WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?" +
                    ")");
            preparedStatement.setLong(1, baseCurrencyId);
            preparedStatement.setLong(2, targetCurrencyId);
            preparedStatement.setBigDecimal(3, rate);
            preparedStatement.setLong(4, baseCurrencyId);
            preparedStatement.setLong(5, targetCurrencyId);
            int rows = preparedStatement.executeUpdate();
            if (rows == 0){
                return CurrencyConstants.ALREADY_EXISTS.getValue();
            }
            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if (resultSet.next()) {
                return resultSet.getInt(1);
            }
        }
        catch (SQLException e) {
            if (e.getErrorCode() == DaoException.Type.CONSTRAINT_UNIQUE.getCode()){
                throw new DaoException(DaoException.Type.CONSTRAINT_UNIQUE.getMessage(),e);
            }
        }
        return -1;
    }
    public long findByCodes(long baseCurrencyId,long targetCurrencyId){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?");
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
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ExchangeRates");
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
    public List<ExchangeRateEntity> findAllPairs(long baseCurrencyId){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ?");
            preparedStatement.setLong(1, baseCurrencyId);
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
    public void update(long id, String rate){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE ExchangeRates SET Rate = ? WHERE ID = ?");
            preparedStatement.setBigDecimal(1, new BigDecimal(rate));
            preparedStatement.setLong(2, id);
            int rows;
            rows = preparedStatement.executeUpdate();
            int b = rows;
        }
        catch (SQLException e) {
            if (e.getErrorCode() == DaoException.Type.CONSTRAINT_UNIQUE.getCode()){
                throw new DaoException(DaoException.Type.CONSTRAINT_UNIQUE.getMessage(),e);
            }
        }
    }
}
