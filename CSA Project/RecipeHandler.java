import com.sun.net.httpserver.*;
import java.io.*;
import java.net.*;

public class RecipeHandler implements HttpHandler {

    private static final String MEALDB_BASE = "https://www.themealdb.com/api/json/v1/1/filter.php?i=";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        String query = exchange.getRequestURI().getQuery();
        if (query == null || !query.contains("ingredient=")) {
            ItemHandler.sendResponse(exchange, 400, "{\"error\":\"Missing ingredient parameter. Use /api/recipe?ingredient=milk\"}");
            return;
        }

        String ingredient = query.split("ingredient=")[1].split("&")[0];
        ingredient = URLDecoder.decode(ingredient, "UTF-8");

        String mealDbUrl = MEALDB_BASE + URLEncoder.encode(ingredient, "UTF-8");

        try {
            URL url = new URL(mealDbUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int status = conn.getResponseCode();
            InputStream responseStream = (status >= 200 && status < 300)
                    ? conn.getInputStream()
                    : conn.getErrorStream();

java.util.Scanner scanner = new java.util.Scanner(responseStream, "UTF-8").useDelimiter("\\A");
String body = scanner.hasNext() ? scanner.next() : "";
            conn.disconnect();

            ItemHandler.sendResponse(exchange, 200, body);
        } catch (Exception e) {
            ItemHandler.sendResponse(exchange, 502, "{\"error\":\"Failed to fetch recipes: " + e.getMessage() + "\"}");
        }
    }
}
