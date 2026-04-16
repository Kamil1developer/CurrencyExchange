package org.kamilkhusainov.currency.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.exceptions.AlreadyExistsException;
import org.kamilkhusainov.currency.exceptions.DataBaseException;
import org.kamilkhusainov.currency.exceptions.NotFoundException;
import org.kamilkhusainov.currency.exceptions.ValidationException;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter("/*")
public class CorsFilter implements Filter {
    private final ObjectMapper MAPPER = new ObjectMapper();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse resp = (HttpServletResponse) res;

        String origin = req.getHeader("Origin");

        if ("http://localhost".equals(origin) || "http://localhost:80".equals(origin) || "https://bec0-178-208-82-152.ngrok-free.app".equals(origin) || "https://bec0-178-208-82-152.ngrok-free.app:80".equals(origin)) {
            resp.setHeader("Access-Control-Allow-Origin", origin);
            resp.setHeader("Vary", "Origin");
            resp.setHeader("Access-Control-Allow-Methods", "GET,POST,PATCH,PUT,DELETE,OPTIONS");
            resp.setHeader("Access-Control-Allow-Headers", "Content-Type, Authorization");
        }

        if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
            resp.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        HttpServletResponse response = (HttpServletResponse) res;

        try {
            chain.doFilter(req, res);

        } catch (ValidationException e) {
            sendError(400, e, resp);
        }
        catch (NotFoundException e) {
            sendError(404, e, resp);
        }
        catch (AlreadyExistsException e) {
            sendError(409, e, resp);
        }
        catch (DataBaseException e) {
            sendError(500, e, resp);

        }
    }

    public  void sendError(int error, RuntimeException exception, HttpServletResponse resp) throws IOException {
        resp.setStatus(error);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(resp.getWriter(), exception.getMessage());
    }
}
