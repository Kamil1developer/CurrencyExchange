package org.kamilkhusainov.currency.service;

import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.dto.CurrencyResponseDto;
import org.kamilkhusainov.currency.entity.CurrencyEntity;
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
    public CurrencyResponseDto find(String code){
        Optional<CurrencyEntity> optionalCurrencyEntity = currencyDao.findByCode(code);
        if (optionalCurrencyEntity.isPresent()){
            CurrencyEntity currencyEntity = optionalCurrencyEntity.get();
            return new CurrencyResponseDto(currencyEntity.id(), currencyEntity.code(), currencyEntity.name(),currencyEntity.sign());
        }
        else{
            throw new NotFoundException(ErrorMessages.CURRENCY_NOT_FOUND);
        }

    }
    public CurrencyResponseDto create(CurrencyRequestDto currencyRequestDto){
        currencyDao.insert(currencyRequestDto);
        return find(currencyRequestDto.code());
    }
}
