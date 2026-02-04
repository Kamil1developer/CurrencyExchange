package org.kamilkhusainov.currency.dao;

import org.kamilkhusainov.currency.entity.CurrenciesEntity;

import javax.sql.DataSource;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Currency;
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

}
