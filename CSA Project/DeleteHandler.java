import com.sun.net.httpserver.*;
import java.io.*;
import java.util.*;

public class DeleteHandler implements HttpHandler {

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
        if (query == null || !query.startsWith("id=")) {
            ItemHandler.sendResponse(exchange, 400, "{\"error\":\"Missing id parameter. Use /api/delete?id=1\"}");
            return;
        }

        int targetId;
        try {
            targetId = Integer.parseInt(query.split("id=")[1].split("&")[0]);
        } catch (NumberFormatException e) {
            ItemHandler.sendResponse(exchange, 400, "{\"error\":\"id must be a number\"}");
            return;
        }

        List<PantryItem> items = PantryStore.loadItems();
        boolean found = items.removeIf(item -> item.id == targetId);

        if (!found) {
            ItemHandler.sendResponse(exchange, 404, "{\"error\":\"Item with id " + targetId + " not found\"}");
            return;
        }

        PantryStore.saveItems(items);
        ItemHandler.sendResponse(exchange, 200, "{\"message\":\"Item " + targetId + " deleted successfully\"}");
    }
}
