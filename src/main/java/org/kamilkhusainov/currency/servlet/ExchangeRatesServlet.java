package org.kamilkhusainov.currency.servlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.dto.ExchangeRateResponseDto;
import org.kamilkhusainov.currency.dto.ExchangeRequestDto;
import org.kamilkhusainov.currency.dto.ExchangeAmountResponseDto;
import org.kamilkhusainov.currency.exceptions.*;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.ExchangeRateService;
import org.kamilkhusainov.currency.util.ResponseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<ExchangeRateResponseDto> list = exchangeRateService.findAll();
            ResponseUtil.sendOkJson(resp,list);
        }
        catch (RuntimeException e) {
            throw new DataBaseException(ErrorMessages.DATABASE_ERROR);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (!isInvalidRequest(req, resp)) {
                String baseCurrencyCode = req.getParameter("baseCurrencyCode");
                String targetCurrencyCode = req.getParameter("targetCurrencyCode");
                String rate = req.getParameter("rate");
                ExchangeRequestDto exchangeRateDto = new ExchangeRequestDto(baseCurrencyCode, targetCurrencyCode, rate);
                ExchangeRateResponseDto exchangeAmountResponseDto = exchangeRateService.create(exchangeRateDto);
                String json = MAPPER.writeValueAsString(exchangeAmountResponseDto);

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(201);
                resp.getWriter().write(json);
            } else {
                throw new ValidationException(ErrorMessages.MISSING_FIELD);
            }
        }
        catch (NumberFormatException | NullPointerException numberFormatException ){
            throw new ValidationException(ErrorMessages.MISSING_FIELD);
        }
        catch (AlreadyExistsException e) {
            throw new AlreadyExistsException(ErrorMessages.DUPLICATE_EXCHANGE_RATE);
        }
        catch (ValidationException e) {
            throw new ValidationException(ErrorMessages.DUPLICATE_EXCHANGE_RATE);
        }
        catch (NotFoundException e) {
            throw new NotFoundException(ErrorMessages.DUPLICATE_EXCHANGE_RATE);
        }
    }

    private boolean isInvalidRequest(HttpServletRequest req,HttpServletResponse resp) throws IOException, NumberFormatException {
        String baseCurrencyId= req.getParameter("baseCurrencyCode");
        String targetCurrencyId= req.getParameter("targetCurrencyCode");
        BigDecimal rate = new BigDecimal(req.getParameter("rate"));

        if (baseCurrencyId.length() < 3 || targetCurrencyId.length() < 3){
            throw new ValidationException(ErrorMessages.MISSING_FIELD);
        }
        if( baseCurrencyId == null || targetCurrencyId == null){
            throw new ValidationException(ErrorMessages.MISSING_FIELD);
        }


        return false;
    }
    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        exchangeRateService = appContainer.services().exchangeRateService();
    }
}
