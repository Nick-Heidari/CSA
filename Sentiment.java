/*
 * Project 4.1.5
 *
 * A class to manage the sentiments from the consumer lab
 */
public class Sentiment
{
  private String phrase;
  private double value;

  /**
   * The constructor for a Sentiment.
   * 
   * @param phrase the word or phrase
   * @param value the sentiment score
   */
  public Sentiment(String phrase, double value)
  {
    this.phrase = phrase;
    this.value = value;
  }
  
  /** 
   * Gets the phrase of the sentiment
   * 
   * @return the sentiment phrase
   */
  public String getPhrase()
  {
    return this.phrase;
  }

  /** 
   * Gets the sentiment value
   * 
   * @return the sentiment value
   */
  public double getValue()
  {
    return this.value;
  }
}