package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.dto.CurrencyResponseDto;
import org.kamilkhusainov.currency.entity.CurrencyEntity;
import org.kamilkhusainov.currency.entity.ExchangeRateRow;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.kamilkhusainov.currency.exceptions.NotFoundException;
import org.kamilkhusainov.currency.dto.CurrencyRequestDto;

import java.util.List;
import java.util.Optional;

public class CurrencyService {
    private final CurrencyDao currencyDao;

    public CurrencyService(CurrencyDao currencyDao) {
        this.currencyDao = currencyDao;
    }

    public List<CurrencyEntity> findAll(){
        return currencyDao.findAll();
    }
    public CurrencyResponseDto findByCode(String code){
        Optional<CurrencyEntity> optionalCurrencyEntity = currencyDao.findByCode(code);
        if (optionalCurrencyEntity.isPresent()){
            CurrencyEntity currencyEntity = optionalCurrencyEntity.get();
            return new CurrencyResponseDto(currencyEntity.id(), currencyEntity.code(), currencyEntity.name(),currencyEntity.sign());
        }
        else{
            throw new NotFoundException(ErrorMessages.CURRENCY_NOT_FOUND);
        }

    }
    public CurrencyResponseDto findById(long id){
        Optional<CurrencyEntity> optionalCurrencyEntity = currencyDao.findById(id);
        if (optionalCurrencyEntity.isPresent()){
            CurrencyEntity currencyEntity = optionalCurrencyEntity.get();
            return new CurrencyResponseDto(currencyEntity.id(),
                    currencyEntity.code(),
                    currencyEntity.name(),
                    currencyEntity.sign());
        }
        throw new NotFoundException(ErrorMessages.CURRENCY_NOT_FOUND);
    }
    public CurrencyResponseDto create(CurrencyRequestDto currencyRequestDto){
        long id = currencyDao.insert(currencyRequestDto);
        return findById(id);
    }
}
