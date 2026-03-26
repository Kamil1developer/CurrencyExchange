package org.kamilkhusainov.currency.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.dto.ExchangeRateDto;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.infrastructure.Infrastructure;
import org.kamilkhusainov.currency.service.ExchangeRateService;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService;
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        if ("PATCH".equalsIgnoreCase(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String rate = parseRate(req);
        if (!rate.isEmpty()) {
            String currencyCodes = req.getPathInfo().substring(1);;
            Map<String, Object> map = exchangeRateService.patch(currencyCodes, rate);
            String json = mapper.writeValueAsString(map);

            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(200);
            resp.getWriter().write(json);

        }
        else{
            resp.setStatus(ServiceException.Type.MISSING_FIELD_CODE.getCode());
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write(ServiceException.Type.MISSING_FIELD_CODE.getMessage());
        }
    }
    private String parseRate(HttpServletRequest req) throws IOException {
        String body = req.getReader().lines().collect(Collectors.joining());
        int indexOf = body.indexOf("=");
        return body.substring(indexOf + 1);
    }
    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        exchangeRateService = appContainer.services().exchangeRateService();
    }
}
