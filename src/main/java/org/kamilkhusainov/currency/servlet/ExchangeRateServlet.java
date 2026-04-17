package org.kamilkhusainov.currency.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.kamilkhusainov.currency.exceptions.NotFoundException;
import org.kamilkhusainov.currency.exceptions.ValidationException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.ExchangeRateService;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.stream.Collectors;

import static org.kamilkhusainov.currency.util.ResponseUtil.sendOkJson;

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
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, NotFoundException {
        String requestPathInfo = req.getPathInfo();
        try {
        requestPathInfo = requestPathInfo.substring(1);
        requestPathInfo = requestPathInfo.substring(0, requestPathInfo.indexOf("/"));

        } catch (StringIndexOutOfBoundsException e) {
            //игнорируем ошибку,ничего страшного
        } catch (NullPointerException e) {
            throw new ValidationException(ErrorMessages.MISSING_FIELD);
        }
        if(!isInvalidRequest(requestPathInfo)) {
            Map<String, Object> map = exchangeRateService.getExchangeRate(requestPathInfo);
            sendOkJson(resp, map);
        }
        else {
            throw new ValidationException(ErrorMessages.MISSING_FIELD);

        }
    }

    private boolean isInvalidRequest(String requestPathInfo){
        return requestPathInfo.isEmpty();
    }

    protected void doPatch(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        try {
            req.setCharacterEncoding("UTF-8");
            String rate = parseRate(req);
            if (!rate.isEmpty()) {
                String exchangeRateCodes = req.getPathInfo().substring(1);
                Map<String, Object> map = exchangeRateService.patch(exchangeRateCodes, rate);
                sendOkJson(resp, map);

            }
        }
        catch (NumberFormatException e){
            throw new ValidationException(ErrorMessages.MISSING_FIELD);
        }
    }
    private String parseRate(HttpServletRequest req) throws IOException,NumberFormatException {
        String body = req.getReader().lines().collect(Collectors.joining());
        int indexOf = body.indexOf("=");
        body = body.substring(indexOf + 1);
        if (!body.equals("%")){
            new BigDecimal(body);
            return body;
        }
        throw new ValidationException(ErrorMessages.MISSING_FIELD);
    }
    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        exchangeRateService = appContainer.services().exchangeRateService();
    }
}
