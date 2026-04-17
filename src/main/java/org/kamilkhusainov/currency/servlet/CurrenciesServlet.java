package org.kamilkhusainov.currency.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.exceptions.AlreadyExistsException;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.kamilkhusainov.currency.exceptions.NotFoundException;
import org.kamilkhusainov.currency.exceptions.ValidationException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.CurrencyService;
import org.kamilkhusainov.currency.model.Currency;
import org.kamilkhusainov.currency.util.ResponseUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;



@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        List<CurrenciesEntity> entityList = currencyService.findAll();
        ResponseUtil.sendOkJson(resp, entityList);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        if (!isInvalidRequest(req)) {
            if (!isIncorrectValue(req)) {
                createCurrency(req,resp);
            }
            else{
                throw new ValidationException(ErrorMessages.MAX_SIGN_LENGTH);
            }
        } else {
            throw new ValidationException(ErrorMessages.MISSING_FIELD);
        }

    }

    private void createCurrency(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Currency currency = new Currency(req.getParameter("name"), req.getParameter("code"), req.getParameter("sign"));
        try {
            CurrenciesEntity currencyEntity = currencyService.create(currency);
            resp.setStatus(201);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(MAPPER.writeValueAsString(currencyEntity));
        } catch (AlreadyExistsException alreadyExistsException) {
            throw new AlreadyExistsException(ErrorMessages.DUPLICATE_CURRENCY);
        }
    }

    private boolean isIncorrectValue(HttpServletRequest req){
        String sign = req.getParameter("sign");
        return sign.length() > 3;
    }
    private boolean isInvalidRequest(HttpServletRequest req) throws IOException {
        String code = req.getParameter("code");
        String name = req.getParameter("name");
        String sign = req.getParameter("sign");
        try {
            return name.isBlank() || code.isBlank() || sign.isBlank();
        } catch (NullPointerException e) {
            throw new NullPointerException(ErrorMessages.MISSING_FIELD);
        }
    }

    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        currencyService = appContainer.services().currencyService();
    }
}


