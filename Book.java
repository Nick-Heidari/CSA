/*
 * Project 4.1.5
 *
 * A Book class to define a Book in a library.
 */
public class Book
{
  private String title;

  /**
   * The constructor for a Book. All Books require
   * a title, an author, and a rating. 
   */
  public Book(String title)
  {
    this.title = title;
  }

  /** 
   * Gets the title of the Book
   * 
   * @return the Book's title
   * */
  public String getTitle()
  {
    return this.title;
  }
  
}