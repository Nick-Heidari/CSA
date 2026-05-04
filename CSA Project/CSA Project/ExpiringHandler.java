import com.sun.net.httpserver.*;
import java.io.*;
import java.time.*;
import java.util.*;
import java.util.stream.*;

public class ExpiringHandler implements HttpHandler {

    private static final int DAYS_THRESHOLD = 3;

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
        exchange.getResponseHeaders().set("Access-Control-Allow-Methods", "GET, OPTIONS");
        exchange.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

        if (exchange.getRequestMethod().equalsIgnoreCase("OPTIONS")) {
            exchange.sendResponseHeaders(204, -1);
            return;
        }

        List<PantryItem> allItems = PantryStore.loadItems();
        LocalDate today = LocalDate.now();

        List<PantryItem> expiringSoon = new ArrayList<>();
        for (PantryItem item : allItems) {
            try {
                LocalDate expiry = LocalDate.parse(item.expiryDate);
                long daysUntilExpiry = today.until(expiry).getDays();
                if (daysUntilExpiry <= DAYS_THRESHOLD) {
                    expiringSoon.add(item);
                }
            } catch (Exception e) {
                System.out.println("Skipping item with invalid date: " + item.name + " / " + item.expiryDate);
            }
        }
        expiringSoon.sort(Comparator.comparing(item -> item.expiryDate));

        String json = ItemHandler.buildJsonArray(expiringSoon);
        ItemHandler.sendResponse(exchange, 200, json);
    }
}
