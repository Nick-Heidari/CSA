import java.util.ArrayList;
import java.io.FileNotFoundException;

public class SearchAndSortAnalysis
{
  public static void main(String[] args) throws FileNotFoundException
  {
    AnalysisUtils.load();

    ArrayList<Book> books = Data.getBooks();
    ArrayList<Sentiment> sentiments = Data.getSentiments();

    System.out.println("BOOK SORT ANALYSIS");
    System.out.println("------------------");

    ArrayList<Book> selectionBooks = AnalysisUtils.copyBooks(books);
    double startTime = System.nanoTime();
    selectionBooks = AnalysisUtils.selectionSortBooks(selectionBooks);
    double selectionTime = (System.nanoTime() - startTime) / 1e6;
    System.out.println("Selection sort time: " + selectionTime + " ms");
    AnalysisUtils.printSampleBooks(selectionBooks, "First 10 titles after selection sort:");

    ArrayList<Book> insertionBooks = AnalysisUtils.copyBooks(books);
    startTime = System.nanoTime();
    insertionBooks = AnalysisUtils.insertionSortBooks(insertionBooks);
    double insertionTime = (System.nanoTime() - startTime) / 1e6;
    System.out.println("Insertion sort time: " + insertionTime + " ms");
    AnalysisUtils.printSampleBooks(insertionBooks, "First 10 titles after insertion sort:");

    ArrayList<Book> mergeBooks = AnalysisUtils.copyBooks(books);
    startTime = System.nanoTime();
    mergeBooks = AnalysisUtils.mergeSortBooks(mergeBooks);
    double mergeTime = (System.nanoTime() - startTime) / 1e6;
    System.out.println("Merge sort time: " + mergeTime + " ms");
    AnalysisUtils.printSampleBooks(mergeBooks, "First 10 titles after merge sort:");

    System.out.println("SENTIMENT SEARCH ANALYSIS");
    System.out.println("-------------------------");

    String[] targets = {"able", "movie", "terrible", "zoo"};

    for (String target : targets)
    {
      System.out.println("Searching for: " + target);

      startTime = System.nanoTime();
      int linearIndex = AnalysisUtils.linearSearchSentiments(sentiments, target);
      double linearTime = (System.nanoTime() - startTime) / 1e6;
      AnalysisUtils.printSearchResult(sentiments, "Linear search", target, linearIndex);
      System.out.println("Linear search time: " + linearTime + " ms");

      startTime = System.nanoTime();
      int iterativeIndex = AnalysisUtils.iterativeBinarySearchSentiments(sentiments, target);
      double iterativeTime = (System.nanoTime() - startTime) / 1e6;
      AnalysisUtils.printSearchResult(sentiments, "Iterative binary search", target, iterativeIndex);
      System.out.println("Iterative binary search time: " + iterativeTime + " ms");

      startTime = System.nanoTime();
      int recursiveIndex = AnalysisUtils.recursiveBinarySearchSentiments(sentiments, target);
      double recursiveTime = (System.nanoTime() - startTime) / 1e6;
      AnalysisUtils.printSearchResult(sentiments, "Recursive binary search", target, recursiveIndex);
      System.out.println("Recursive binary search time: " + recursiveTime + " ms");

      System.out.println();
    }
  }
}