import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;

public class ItemHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        String method = exchange.getRequestMethod();

        if (method.equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        if (method.equalsIgnoreCase("GET")) {
            handleGet(exchange);
        } else if (method.equalsIgnoreCase("POST")) {
            handlePost(exchange);
        } else {
            sendResponse(exchange, 405, "{\"error\":\"Method not allowed\"}");
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        List<PantryItem> items = PantryStore.loadItems();
        String json = buildJsonArray(items);
        sendResponse(exchange, 200, json);
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
      java.util.Scanner scanner = new java.util.Scanner(is).useDelimiter("\\A");
String body = scanner.hasNext() ? scanner.next().trim() : "";

        String name = null;
        String date = null;

        try {
            name = body.split("\"name\"\\s*:\\s*\"")[1].split("\"")[0];
            date = body.split("\"expiryDate\"\\s*:\\s*\"")[1].split("\"")[0];
        } catch (Exception e) {
            sendResponse(exchange, 400, "{\"error\":\"Invalid request body. Expected JSON with name and expiryDate.\"}");
            return;
        }

        if (name == null || name.isEmpty() || date == null || date.isEmpty()) {
            sendResponse(exchange, 400, "{\"error\":\"name and expiryDate are required\"}");
            return;
        }

        List<PantryItem> items = PantryStore.loadItems();
        int newId = PantryStore.getNextId(items);
        PantryItem newItem = new PantryItem(newId, name, date);
        items.add(newItem);
        PantryStore.saveItems(items);

        String json = "{\"id\":" + newItem.id + ",\"name\":\"" + newItem.name + "\",\"expiryDate\":\"" + newItem.expiryDate + "\"}";
        sendResponse(exchange, 201, json);
    }

    static String buildJsonArray(List<PantryItem> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < items.size(); i++) {
            PantryItem item = items.get(i);
            sb.append("{");
            sb.append("\"id\":").append(item.id).append(",");
            sb.append("\"name\":\"").append(item.name).append("\",");
            sb.append("\"expiryDate\":\"").append(item.expiryDate).append("\"");
            sb.append("}");
            if (i < items.size() - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }

    static void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        byte[] bytes = response.getBytes("UTF-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        OutputStream os = exchange.getResponseBody();
        os.write(bytes);
        os.close();
    }
}
