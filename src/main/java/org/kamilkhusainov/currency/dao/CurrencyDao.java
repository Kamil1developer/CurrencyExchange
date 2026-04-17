package org.kamilkhusainov.currency.dao;

import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.exceptions.AlreadyExistsException;
import org.kamilkhusainov.currency.exceptions.DataBaseException;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.kamilkhusainov.currency.model.Currency;
import org.sqlite.SQLiteErrorCode;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

import java.util.List;
import java.util.Optional;

public class CurrencyDao {
    private static final String FIND_ALL_CURRENCIES_SQL =
            "SELECT ID, Code, FullName, Sign FROM Currencies";

    private static final String FIND_CURRENCY_BY_CODE_SQL =
            "SELECT ID, Code, FullName, Sign FROM Currencies WHERE Code = ?";

    private static final String FIND_CURRENCY_BY_ID_SQL =
            "SELECT ID, Code, FullName, Sign FROM Currencies WHERE ID = ?";

    private static final String INSERT_CURRENCY_SQL =
            "INSERT INTO Currencies(Code, FullName, Sign) VALUES (?, ?, ?)";

    private final DataSource dataSource;

    public CurrencyDao(DataSource dataSource){
        this.dataSource = dataSource;
    }

    public List<CurrenciesEntity> findAll(){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_CURRENCIES_SQL);
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

    public Optional<CurrenciesEntity> findByCode(String code){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_CURRENCY_BY_CODE_SQL);
            preparedStatement.setString(1,code);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.getString("Code") != null){
                CurrenciesEntity entity = new CurrenciesEntity(resultSet.getLong("ID"),
                        resultSet.getString("Code"),
                        resultSet.getString("FullName"), resultSet.getString("Sign"));
                resultSet.close();
                return Optional.of(entity);
            }
            return Optional.empty();
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public CurrenciesEntity findById(long id){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(FIND_CURRENCY_BY_ID_SQL);
            preparedStatement.setLong(1,id);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()){
            CurrenciesEntity entity = new CurrenciesEntity(resultSet.getLong("ID"),
                    resultSet.getString("Code"),
                    resultSet.getString("FullName"), resultSet.getString("Sign"));
            resultSet.close();
            return entity;
            }
        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return new CurrenciesEntity(-1,"nullNull","nullNull","nullNull");
    }

    public long insert(Currency currency){
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_CURRENCY_SQL, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1,currency.code());
            preparedStatement.setString(2,currency.name());
            preparedStatement.setString(3,currency.sign());
            preparedStatement.executeUpdate();
            ResultSet rs = preparedStatement.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new DataBaseException("Не удалось получить сгенерированный ID");

        }
        catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code){
                throw new AlreadyExistsException(ErrorMessages.DUPLICATE_CURRENCY,e);
            }
            throw new DataBaseException("Ошибка БД",e);
        }
    }
}
