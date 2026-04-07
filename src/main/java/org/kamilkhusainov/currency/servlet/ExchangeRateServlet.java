package org.kamilkhusainov.currency.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.dto.ExchangeRateDto;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.infrastructure.Infrastructure;
import org.kamilkhusainov.currency.service.ExchangeRateService;
import util.ResponseUtil;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

import static util.ResponseUtil.*;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private final ObjectMapper MAPPER = new ObjectMapper();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestPathInfo = req.getPathInfo();

        try {
            requestPathInfo = requestPathInfo.substring(1);
            requestPathInfo = requestPathInfo.substring(0, requestPathInfo.indexOf("/"));


        }
        catch (StringIndexOutOfBoundsException e){
            //игнорируем ошибку,ничего страшного
        }
        catch (NullPointerException e){
            sendErrorJson(ServiceException.Type.EXCHANGE_RATES_NOT_FOUND, resp);
            return;
        }
        try {
            Map<String, Object> map = exchangeRateService.getExchangeRate(requestPathInfo);
            sendOkJson(resp, map);
        }
        catch (ServiceException e){
            sendErrorJson(e.getType(),resp);
        }

    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            req.setCharacterEncoding("UTF-8");
            String rate = parseRate(req);
            if (!rate.isEmpty()) {
                String exchangeRateCodes = req.getPathInfo().substring(1);
                Map<String, Object> map = exchangeRateService.patch(exchangeRateCodes, rate);
                sendOkJson(resp, map);

            } else {
                sendErrorJson(ServiceException.Type.MISSING_FIELD_CODE,resp);
            }
        }
        catch (ServiceException serviceException) {
            sendErrorJson(ServiceException.Type.EXCHANGE_RATE_NOT_FOUND,resp);
        }
        catch (NumberFormatException numberFormatException){
            sendErrorJson(ServiceException.Type.EXCHANGE_RATES_NOT_FOUND,resp);
        }
    }
    private boolean isInvalidRequest(HttpServletRequest request,HttpServletResponse response) throws IOException {
        String code = request.getParameter("code");
        String name = request.getParameter("name");
        String sign = request.getParameter("sign");
        try {
            return name.isBlank() || code.isBlank() || sign.isBlank();
        } catch (NullPointerException e) {
            sendErrorJson(ServiceException.Type.EXCHANGE_RATES_NOT_FOUND, response);
            return true;
        }
    }
    private String parseRate(HttpServletRequest req) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        int indexOf = body.indexOf("=");
        body = body.substring(indexOf + 1);
        if (!body.equals("%")){
            new BigDecimal(body);
            return body;
        }
        throw new ServiceException(ServiceException.Type.EXCHANGE_RATES_NOT_FOUND);
    }
    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        exchangeRateService = appContainer.services().exchangeRateService();
    }
}
