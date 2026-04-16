package org.kamilkhusainov.currency.servlet;

import org.kamilkhusainov.currency.entity.CurrenciesEntity;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.kamilkhusainov.currency.exceptions.NotFoundException;
import org.kamilkhusainov.currency.exceptions.ValidationException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.CurrencyService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.kamilkhusainov.currency.util.ResponseUtil.sendOkJson;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyService currencyService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String requestPathInfo = req.getPathInfo();
        requestPathInfo = requestPathInfo.substring(1);
        try {
            requestPathInfo = requestPathInfo.substring(0, requestPathInfo.indexOf("/"));
        }
        catch (StringIndexOutOfBoundsException e){
            //игнорируем ошибку,ничего страшного
        }
        try {
            if (!isInvalidRequest(requestPathInfo,resp)) {
                CurrenciesEntity entity = currencyService.findByCode(requestPathInfo);

                sendOkJson(resp, entity);
            }
        }
        catch (NotFoundException e){
            throw new NotFoundException(ErrorMessages.CURRENCY_NOT_FOUND);
        }
    }
    private boolean isInvalidRequest(String requestPathInfo, HttpServletResponse resp) throws IOException {
        if (requestPathInfo.length() < 3){
            throw new ValidationException(ErrorMessages.CURRENCY_CODE_MISSING);
        }
        return false;
    }

    @Override
    public void init(){
        AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
        currencyService = appContainer.services().currencyService();
    }
}
