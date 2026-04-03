package org.kamilkhusainov.currency.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.exceptions.ServiceException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.CurrencyService;
import util.ResponseUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.PreparedStatement;

import static util.ResponseUtil.sendErrorJson;
import static util.ResponseUtil.sendOkJson;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String requestPathInfo = request.getPathInfo();
        requestPathInfo = requestPathInfo.substring(1);
        try {
            requestPathInfo = requestPathInfo.substring(0, requestPathInfo.indexOf("/"));
        }
        catch (StringIndexOutOfBoundsException e){
            //игнорируем ошибку,ничего страшного
        }
        try {
            if (!isInvalidRequest(requestPathInfo,response)) {
                CurrenciesEntity entity = currencyService.findByCode(requestPathInfo);

                sendOkJson(response, entity);
            }
        }
        catch (ServiceException e){
            sendErrorJson(ServiceException.Type.CURRENCY_NOT_FOUND,response);
        }
    }
    private boolean isInvalidRequest(String requestPathInfo, HttpServletResponse response) throws IOException {
        if (requestPathInfo.length() < 3){
            sendErrorJson(ServiceException.Type.CURRENCY_CODE_MISSING,response);
            return true;
        }
        return false;
    }

    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        currencyService = appContainer.services().currencyService();
    }
}
