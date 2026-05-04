import com.sun.net.httpserver.*;
import java.net.*;

public class Main {

    public static void main(String[] args) throws Exception {
        int port = 8000;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/items",    new ItemHandler());
        server.createContext("/api/delete",   new DeleteHandler());
        server.createContext("/api/expiring", new ExpiringHandler());
        server.createContext("/api/recipe",   new RecipeHandler());

        server.setExecutor(null); 
        server.start();

        System.out.println("PantryPal backend running at http://localhost:" + port);
        System.out.println();
        System.out.println("Endpoints:");
        System.out.println("  GET  http://localhost:" + port + "/api/items              - list all items");
        System.out.println("  POST http://localhost:" + port + "/api/items              - add an item");
        System.out.println("  GET  http://localhost:" + port + "/api/delete?id=1        - delete item by id");
        System.out.println("  GET  http://localhost:" + port + "/api/expiring           - items expiring within 3 days");
        System.out.println("  GET  http://localhost:" + port + "/api/recipe?ingredient=milk - recipes by ingredient");
    }
}
