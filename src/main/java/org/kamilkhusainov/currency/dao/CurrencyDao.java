package org.kamilkhusainov.currency.dao;

import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.exceptions.DaoException;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.model.Currency;
import org.sqlite.SQLiteErrorCode;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import java.util.List;

public class CurrencyDao {
    private final DataSource dataSource;

    public CurrencyDao(DataSource dataSource){
        this.dataSource = dataSource;
    }
    public List<CurrenciesEntity> findAll(){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Currencies");
            ResultSet resultSet = preparedStatement.executeQuery();
            List<CurrenciesEntity> entityList = new ArrayList<>();
            while (resultSet.next()){
                CurrenciesEntity entity = new CurrenciesEntity(resultSet.getLong("ID"),
                        resultSet.getString("Code"),
                        resultSet.getString("FullName"),resultSet.getString("Sign"));
                entityList.add(entity);
            }
            resultSet.close();
            return entityList;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public CurrenciesEntity findByCode(String code){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM Currencies WHERE Code = ?");
            preparedStatement.setString(1,code);
            ResultSet resultSet = preparedStatement.executeQuery();
            CurrenciesEntity entity = new CurrenciesEntity(resultSet.getLong("ID"),
                    resultSet.getString("Code"),
                    resultSet.getString("FullName"),resultSet.getString("Sign"));
            resultSet.close();
            return entity;
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public void insert(Currency currency){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO Currencies(Code,FullName,Sign) VALUES (?, ?, ?)");
            preparedStatement.setString(1,currency.getCode());
            preparedStatement.setString(2,currency.getName());
            preparedStatement.setString(3,currency.getSign());
            preparedStatement.executeUpdate();
        }
        catch (SQLException e) {
            if (e.getErrorCode() == DaoException.Type.CONSTRAINT_UNIQUE.getCode()){
                throw new DaoException(DaoException.Type.CONSTRAINT_UNIQUE.getMessage(),e);
            }
        }
    }

}
