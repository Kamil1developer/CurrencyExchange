package org.kamilkhusainov.currency.servlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.dto.ExchangeRateDto;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.ExchangeRateService;
import util.ResponseUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import static util.ResponseUtil.sendErrorJson;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Map<String, Object>> list = exchangeRateService.findAll();
            ResponseUtil.sendOkJson(resp,list);
        }
        catch (RuntimeException e) {
            ResponseUtil.sendErrorJson(ServiceException.Type.DATABASE_ERROR, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            if (!isInvalidRequest(req, resp)) {
                String baseCurrencyCode = req.getParameter("baseCurrencyCode");
                String targetCurrencyCode = req.getParameter("targetCurrencyCode");
                String rate = req.getParameter("rate");
                ExchangeRateDto exchangeRateDto = new ExchangeRateDto(baseCurrencyCode, targetCurrencyCode, rate);
                Map<String, Object> map = exchangeRateService.create(exchangeRateDto);
                String json = MAPPER.writeValueAsString(map);

                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(201);
                resp.getWriter().write(json);
            } else {
                sendErrorJson(ServiceException.Type.MISSING_FIELD_CODE, resp);
            }
        }
        catch (NumberFormatException | NullPointerException numberFormatException ){
            sendErrorJson(ServiceException.Type.MISSING_FIELD_CODE ,resp);
            resp.setStatus(ServiceException.Type.NOT_INTEGER_CODE.getCode());
        }
        catch (ServiceException serviceException){
            if (ServiceException.Type.DUPLICATE_EXCHANGE_RATE_CODE == serviceException.getType()){
                sendErrorJson(ServiceException.Type.DUPLICATE_EXCHANGE_RATE_CODE, resp);
            }
            if (ServiceException.Type.MISSING_FIELD_CODE == serviceException.getType()){
                sendErrorJson(ServiceException.Type.MISSING_FIELD_CODE,resp);
            }
            if (ServiceException.Type.CURRENCY_NOT_FOUND == serviceException.getType()){
                sendErrorJson(ServiceException.Type.CURRENCY_NOT_FOUND,resp);
            }
        }
    }

    private boolean isInvalidRequest(HttpServletRequest req,HttpServletResponse resp) throws IOException, NumberFormatException {
        String baseCurrencyId= req.getParameter("baseCurrencyCode");
        String targetCurrencyId= req.getParameter("targetCurrencyCode");
        BigDecimal rate = new BigDecimal(req.getParameter("rate"));

        if (baseCurrencyId.length() < 3 || targetCurrencyId.length() < 3){
            throw new ServiceException(ServiceException.Type.MISSING_FIELD_CODE);
        }
        if( baseCurrencyId == null || targetCurrencyId == null){
            throw new ServiceException(ServiceException.Type.MISSING_FIELD_CODE);
        }


        return false;
    }
    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        exchangeRateService = appContainer.services().exchangeRateService();
    }
}
