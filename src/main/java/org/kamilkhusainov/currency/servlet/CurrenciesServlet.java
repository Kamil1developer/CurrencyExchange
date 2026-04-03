package org.kamilkhusainov.currency.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.CurrencyService;
import org.kamilkhusainov.currency.model.Currency;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

import static util.ResponseUtil.sendErrorJson;


@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService currencyService;
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        List<CurrenciesEntity> entityList = currencyService.findAll();
        MAPPER.writeValue(response.getWriter(), entityList);

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding("UTF-8");
        if (!isInvalidRequest(request, response)) {
            if (!isIncorrectValue(request)) {
                createCurrency(request,response);
            }
            else{
                sendErrorJson(ServiceException.Type.MAX_SIGN_LENGTH,response);
            }
        } else {
            sendErrorJson(ServiceException.Type.MISSING_FIELD_CODE, response);
        }

    }

    private void createCurrency(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Currency currency = new Currency(request.getParameter("name"), request.getParameter("code"), request.getParameter("sign"));
        try {
            CurrenciesEntity currencyEntity = currencyService.create(currency);
            response.setStatus(201);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(MAPPER.writeValueAsString(currencyEntity));
        } catch (ServiceException serviceException) {
            response.setStatus(ServiceException.Type.DUPLICATE_CURRENCY_CODE.getCode());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(MAPPER.writeValueAsString(ServiceException.Type.DUPLICATE_CURRENCY_CODE.getMessage()));
        }
    }

    private boolean isIncorrectValue(HttpServletRequest request){
        String sign = request.getParameter("sign");
        return sign.length() > 3;
    }
    private boolean isInvalidRequest(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String sign = request.getParameter("sign");
        try {
            return name.isBlank() || code.isBlank() || sign.isBlank();
        } catch (NullPointerException e) {
            return true;
        }
    }

    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        currencyService = appContainer.services().currencyService();
    }
}


