package org.kamilkhusainov.currency.util;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ResponseUtil {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static void sendOkJson(HttpServletResponse resp,Object object) throws IOException {
        resp.setStatus(200);
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        MAPPER.writeValue(resp.getWriter(), object);
    }

}
