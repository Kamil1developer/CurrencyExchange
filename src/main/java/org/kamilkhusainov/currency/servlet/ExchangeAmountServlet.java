package org.kamilkhusainov.currency.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.dto.ExchangeRateAmountDto;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.ExchangeAmountService;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;

import static util.ResponseUtil.sendErrorJson;
import static util.ResponseUtil.sendOkJson;

@WebServlet("/exchange/*")
public class ExchangeAmountServlet extends HttpServlet {
    private final ObjectMapper MAPPER = new ObjectMapper();
    private ExchangeAmountService exchangeAmountService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if (!isInvalidRequest(req,resp)) {
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));
            ExchangeRateAmountDto body = exchangeAmountService.existsExchangeRate(from,to,amount);
            if (!body.isEmpty()) {
                sendOkJson(resp,body);
            }
            else {
                sendOkJson(resp,Map.of("message","Валюта не найдена"));
            }
        }
    }
    private boolean isInvalidRequest(HttpServletRequest req,HttpServletResponse resp) throws IOException {
        try {
            Integer.parseInt(req.getParameter("amount"));
            return false;
        }
        catch (NumberFormatException numberFormatException){
            resp.setStatus(400);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(MAPPER.writeValueAsString(Map.of("message","Поле amount должно быть неотрицательным числом")));
            return true;
        }
    }
    @Override
    public void init(){
         AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
         exchangeAmountService = appContainer.services().exchangeAmountService();
    }
}
