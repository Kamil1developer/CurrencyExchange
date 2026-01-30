package org.kamilkhusainov.currency.servlet;
import org.kamilkhusainov.currency.db.DatabaseInitializer;
import org.kamilkhusainov.currency.model.Currency;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Hello from servlet");
    }
    public void init(){
        DatabaseInitializer databaseInitializer = new DatabaseInitializer("jdbc:sqlite:/Users/kamilhus/database/currency-exchange.db");
        databaseInitializer.init();
    }
}


