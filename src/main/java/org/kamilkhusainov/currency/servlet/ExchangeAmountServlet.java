package org.kamilkhusainov.currency.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.dto.ExchangeAmountResponseDto;
import org.kamilkhusainov.currency.exceptions.ErrorMessages;
import org.kamilkhusainov.currency.exceptions.NotFoundException;
import org.kamilkhusainov.currency.exceptions.ValidationException;
import org.kamilkhusainov.currency.infrastructure.AppContainer;
import org.kamilkhusainov.currency.service.ExchangeAmountService;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

import static org.kamilkhusainov.currency.util.ResponseUtil.sendOkJson;

@WebServlet("/exchange/*")
public class ExchangeAmountServlet extends HttpServlet {
    private final ObjectMapper MAPPER = new ObjectMapper();
    private ExchangeAmountService exchangeAmountService;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        if (!isInvalidRequest(req,resp)) {
            String from = req.getParameter("from");
            String to = req.getParameter("to");
            BigDecimal amount = new BigDecimal(req.getParameter("amount"));
            Optional<ExchangeAmountResponseDto> body = exchangeAmountService.existsExchangeRate(from,to,amount);
            if (body.isPresent()) {
                sendOkJson(resp,body.get());
            }
            else {
                throw new NotFoundException(ErrorMessages.CURRENCY_NOT_FOUND);
            }
        }
        else {
            throw new ValidationException(ErrorMessages.EXCHANGE_MISSING);
        }
    }
    @Override
    public void init(){
         AppContainer appContainer = (AppContainer) getServletContext().getAttribute("appContainer");
         exchangeAmountService = appContainer.services().exchangeAmountService();
    }
}
