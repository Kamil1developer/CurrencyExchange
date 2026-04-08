package util;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.kamilkhusainov.currency.exceptions.ServiceException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void sendErrorJson(ServiceException.Type error, HttpServletResponse response) throws IOException {
        response.setStatus(error.getCode());
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(response.getWriter(), error.getMessage());
    }
    public static void sendOkJson(HttpServletResponse response,Object object) throws IOException {
        response.setStatus(200);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(response.getWriter(), object);
    }

}
