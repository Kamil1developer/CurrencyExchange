package org.kamilkhusainov.currency.servlet;
import org.kamilkhusainov.currency.dto.ExchangeRateDto;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.infrastructure.Services;
import org.kamilkhusainov.currency.model.ExchangeRate;
import org.kamilkhusainov.currency.service.ExchangeRateService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isInvalidRequest(req,resp)) {
            String baseCurrencyId = req.getParameter("baseCurrencyCode");
            String targetCurrencyId = req.getParameter("targetCurrencyCode");
            String rate = req.getParameter("rate");
            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(baseCurrencyId,targetCurrencyId,rate);
            exchangeRateService.create(exchangeRateDto);
        }
        else{
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(ServiceException.Type.MISSING_FIELD_CODE.getCode());
            resp.getWriter().write(ServiceException.Type.MISSING_FIELD_CODE.getText());
        }
    }
    private boolean isInvalidRequest(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        String baseCurrencyId= req.getParameter("baseCurrencyCode");
        String targetCurrencyId= req.getParameter("targetCurrencyCode");
        try {
            int rate = Integer.parseInt(req.getParameter("rate"));
        }
        catch (NumberFormatException numberFormatException){
            resp.setStatus(ServiceException.Type.NOT_INTEGER_CODE.getCode());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ServiceException.Type.NOT_INTEGER_CODE.getText());
        }
        return baseCurrencyId.isBlank() && targetCurrencyId.isBlank() ;
    }
    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        exchangeRateService = appContainer.services().exchangeRateService();
    }
}
