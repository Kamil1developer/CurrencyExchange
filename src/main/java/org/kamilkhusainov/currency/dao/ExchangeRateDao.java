package org.kamilkhusainov.currency.dao;

import org.kamilkhusainov.currency.dto.ExchangeRateDto;
import org.kamilkhusainov.currency.exceptions.DaoException;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeRateDao {
    private final DataSource dataSource;
    public ExchangeRateDao(DataSource dataSource){
        this.dataSource = dataSource;
    }
    public void insert(ExchangeRateDto exchangeRateDto){
        int rows;
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO ExchangeRates(BaseCurrencyId, TargetCurrencyId, Rate) " +
                    "SELECT ?, ?, ? " +
                    "WHERE NOT EXISTS (" +
                    "   SELECT 1 FROM ExchangeRates " +
                    "   WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?" +
                    ")");
            preparedStatement.setString(1, exchangeRateDto.baseCurrencyId());
            preparedStatement.setString(2, exchangeRateDto.targetCurrencyId());
            preparedStatement.setString(3, exchangeRateDto.rate());
            preparedStatement.setString(4, exchangeRateDto.baseCurrencyId());
            preparedStatement.setString(5, exchangeRateDto.targetCurrencyId());
            rows = preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            if (e.getErrorCode() == DaoException.Type.CONSTRAINT_UNIQUE.getCode()){
                throw new DaoException(DaoException.Type.CONSTRAINT_UNIQUE.getMessage(),e);
            }
        }
    }
    public long findByCodes(String baseCurrencyId,String targetCurrencyId){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM ExchangeRates WHERE BaseCurrencyId = ? AND TargetCurrencyId = ?");
            preparedStatement.setString(1, baseCurrencyId);
            preparedStatement.setString(2, targetCurrencyId);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.getLong("ID");

        }
        catch (SQLException e){
            throw new RuntimeException();
        }
    }
}
