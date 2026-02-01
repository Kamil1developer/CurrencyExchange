package org.kamilkhusainov.currency.servlet;
import org.kamilkhusainov.currency.dao.CurrencyDao;
import org.kamilkhusainov.currency.infrastructure.db.CurrencyDatabaseInitializer;
import org.kamilkhusainov.currency.service.CurrencyService;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;


@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyService service;
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Hello from servlet");
    }
    public void init(){
        DataSource dataSource = (DataSource) getServletContext().getAttribute("dataSource");
        CurrencyService service = new CurrencyService();
        CurrencyDao dao = new CurrencyDao(dataSource);


    }
}


