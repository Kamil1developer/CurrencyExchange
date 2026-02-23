package org.kamilkhusainov.currency.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.CurrencyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/currencies/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService;
    private final ObjectMapper MAPPER = new ObjectMapper();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String requestPathInfo = request.getPathInfo();
        requestPathInfo = requestPathInfo.substring(1);
        try {
            requestPathInfo = requestPathInfo.substring(0, requestPathInfo.indexOf("/"));
        }
        catch (StringIndexOutOfBoundsException e){
            //игнорируем ошибку,ничего страшного
        }
        CurrenciesEntity entity  = currencyService.findByCode(requestPathInfo);
        MAPPER.writeValue(response.getWriter(), entity);
    }
    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        currencyService = appContainer.services().currencyService();
    }
}
