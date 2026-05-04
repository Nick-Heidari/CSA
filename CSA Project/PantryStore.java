import java.io.*;
import java.util.*;

public class PantryStore {

    private static final String FILE_PATH = "pantry.txt";

    public static List<PantryItem> loadItems() {
        List<PantryItem> items = new ArrayList<>();
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            return items;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty()) continue;
                String[] parts = line.split(",", 3);
                if (parts.length == 3) {
                    int id = Integer.parseInt(parts[0].trim());
                    String name = parts[1].trim();
                    String expiryDate = parts[2].trim();
                    items.add(new PantryItem(id, name, expiryDate));
                }
            }
        } catch (IOException e) {
            System.out.println("Error reading pantry.txt: " + e.getMessage());
        }

        return items;
    }

    public static void saveItems(List<PantryItem> items) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, false))) {
            for (PantryItem item : items) {
                writer.write(item.id + "," + item.name + "," + item.expiryDate);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing pantry.txt: " + e.getMessage());
        }
    }

    public static int getNextId(List<PantryItem> items) {
        int maxId = 0;
        for (PantryItem item : items) {
            if (item.id > maxId) maxId = item.id;
        }
        return maxId + 1;
    }
}
