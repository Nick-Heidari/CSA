public class PantryItem {
    public int id;
    public String name;
    public String expiryDate; //YYYY-MM-DD

    public PantryItem(int id, String name, String expiryDate) {
        this.id = id;
        this.name = name;
        this.expiryDate = expiryDate;
    }
}
