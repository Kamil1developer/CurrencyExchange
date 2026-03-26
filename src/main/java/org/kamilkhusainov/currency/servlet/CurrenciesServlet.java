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
        if (!isInvalidRequest(request)){
            Currency currency = new Currency(request.getParameter("name"),request.getParameter("code"),request.getParameter("sign"));
            try {
                currencyService.create(currency);
            } catch (ServiceException serviceException) {
                response.setStatus(ServiceException.Type.DUPLICATE_CURRENCY_CODE.getCode());
                response.setContentType("application/json");
                response.setCharacterEncoding("UTF-8");
                response.getWriter().write(ServiceException.Type.DUPLICATE_CURRENCY_CODE.getMessage());
            }
        }
        else{
            response.setStatus(ServiceException.Type.MISSING_FIELD_CODE.getCode());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(ServiceException.Type.MISSING_FIELD_CODE.getMessage());
        }

    }
    private boolean isInvalidRequest(HttpServletRequest request){
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String sign = request.getParameter("sign");

        return name.isBlank() || code.isBlank() || sign.isBlank();
    }

    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        currencyService = appContainer.services().currencyService();
    }
}


