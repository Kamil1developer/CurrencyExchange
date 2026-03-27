package org.kamilkhusainov.currency.servlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.dto.ExchangeRateDto;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.ExchangeRateService;

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
            resp.setContentType("application/json");
            resp.setStatus(200);
            List<Map<String, Object>> list = exchangeRateService.findAll();
            resp.getWriter().write(MAPPER.writeValueAsString(list));
        }
        catch (RuntimeException e) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(ServiceException.Type.DATABASE_ERROR.getCode());
            resp.getWriter().write(ServiceException.Type.DATABASE_ERROR.getMessage());
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
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.setStatus(ServiceException.Type.MISSING_FIELD_CODE.getCode());
                resp.getWriter().write(ServiceException.Type.MISSING_FIELD_CODE.getMessage());
            }
        }
        catch (NumberFormatException numberFormatException){
            resp.setStatus(ServiceException.Type.NOT_INTEGER_CODE.getCode());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ServiceException.Type.NOT_INTEGER_CODE.getMessage());
        }
        catch (ServiceException serviceException){
            if (ServiceException.Type.DUPLICATE_EXCHANGE_RATE_CODE == serviceException.getType()){
                resp.setStatus(ServiceException.Type.DUPLICATE_EXCHANGE_RATE_CODE.getCode());
                resp.setContentType("application/json");
                resp.setCharacterEncoding("UTF-8");
                resp.getWriter().write(ServiceException.Type.DUPLICATE_EXCHANGE_RATE_CODE.getMessage());
            }
        }
    }

    private boolean isInvalidRequest(HttpServletRequest req,HttpServletResponse resp) throws IOException, NumberFormatException {
        String baseCurrencyId= req.getParameter("baseCurrencyCode");
        String targetCurrencyId= req.getParameter("targetCurrencyCode");
        BigDecimal rate = new BigDecimal(req.getParameter("rate"));

        return baseCurrencyId.isBlank() && targetCurrencyId.isBlank() ;
    }
    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        exchangeRateService = appContainer.services().exchangeRateService();
    }
}
