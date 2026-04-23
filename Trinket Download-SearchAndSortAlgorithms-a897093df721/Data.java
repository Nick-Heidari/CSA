 /*
 * Project 4.1.5
 * 
 * Manage data for search and sort analyses
 */
import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;

public class Data
{
  /**
   * Returns a list of childrens books
   * 
   * @return the ArrayList of childrens books
   */
  public static ArrayList<Book> getBooks()
  { 
    ArrayList<Book> b = new ArrayList<Book>();
    try
    {
      Scanner sc = new Scanner(new File("books.csv"));
      while (sc.hasNextLine())
      {
        String[] tokens = sc.nextLine().trim().split(", ");
        b.add(new Book(tokens[0]));
      }
      sc.close();
    } catch (Exception e) { System.out.println("Error reading or parsing books.txt" + e); }

    return b;
  }

  /**
   * Returns a list of sentiments from the csv file.
   * 
   * @return the ArrayList of Sentiment objects
   */
  public static ArrayList<Sentiment> getSentiments()
  {
    ArrayList<Sentiment> s = new ArrayList<Sentiment>();
    try
    {
      Scanner sc = new Scanner(new File("sentiments.csv"));
      while (sc.hasNextLine())
      {
        String[] tokens = sc.nextLine().trim().split(",");
        s.add(new Sentiment(tokens[0], Double.parseDouble(tokens[1])));
      }
      sc.close();
    } catch (Exception e) { System.out.println("Error reading or parsing sentiments.txt" + e); }

    return s;
  }

  /**
   * Displays the (sorted) ArrayList
   * 
   * @param the ArrayList of Book objects
   */
  public static void showBooks(ArrayList<Book> sortedBooks, String title)  
  {
    System.out.println(title);
    for (Book b : sortedBooks)
      System.out.println("    " +  b.getTitle());
  }
  
}
