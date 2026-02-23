package org.kamilkhusainov.currency.servlet;
import org.kamilkhusainov.currency.model.ExchangeRate;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        isInvalidRequest(req);
//        ExchangeRate exchangeRate = new ExchangeRate(BaseCurrencyId,TargetCurrencyId,Rate);
    }
    private boolean isInvalidRequest(HttpServletRequest req){
        String s = req.getParameter("baseCurrencyCode");
        int BaseCurrencyId = Integer.parseInt(req.getParameter("BaseCurrencyId"));
        int TargetCurrencyId = Integer.parseInt(req.getParameter("TargetCurrencyId"));
        double Rate = Double.parseDouble(req.getParameter("Rate"));
        return true;
    }
}
