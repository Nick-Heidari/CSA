import java.util.ArrayList;

public class AnalysisUtils
{
  public static void load()
  {
    System.out.println("AnalysisUtils loaded");
  }

  public static ArrayList<Book> copyBooks(ArrayList<Book> original)
  {
    ArrayList<Book> copy = new ArrayList<Book>();
    for (Book b : original)
    {
      copy.add(new Book(b.getTitle()));
    }
    return copy;
  }

  public static ArrayList<Book> selectionSortBooks(ArrayList<Book> books)
  {
    for (int i = 0; i < books.size() - 1; i++)
    {
      int minIndex = i;
      for (int j = i + 1; j < books.size(); j++)
      {
        if (books.get(j).getTitle().compareToIgnoreCase(books.get(minIndex).getTitle()) < 0)
        {
          minIndex = j;
        }
      }

      Book temp = books.get(i);
      books.set(i, books.get(minIndex));
      books.set(minIndex, temp);
    }
    return books;
  }

  public static ArrayList<Book> insertionSortBooks(ArrayList<Book> books)
  {
    for (int i = 1; i < books.size(); i++)
    {
      Book current = books.get(i);
      int j = i - 1;

      while (j >= 0 && books.get(j).getTitle().compareToIgnoreCase(current.getTitle()) > 0)
      {
        books.set(j + 1, books.get(j));
        j--;
      }

      books.set(j + 1, current);
    }
    return books;
  }

  public static ArrayList<Book> mergeSortBooks(ArrayList<Book> books)
  {
    if (books.size() <= 1)
    {
      return books;
    }

    int mid = books.size() / 2;
    ArrayList<Book> left = new ArrayList<Book>();
    ArrayList<Book> right = new ArrayList<Book>();

    for (int i = 0; i < mid; i++)
    {
      left.add(books.get(i));
    }
    for (int i = mid; i < books.size(); i++)
    {
      right.add(books.get(i));
    }

    left = mergeSortBooks(left);
    right = mergeSortBooks(right);

    return merge(left, right);
  }

  private static ArrayList<Book> merge(ArrayList<Book> left, ArrayList<Book> right)
  {
    ArrayList<Book> merged = new ArrayList<Book>();
    int leftIndex = 0;
    int rightIndex = 0;

    while (leftIndex < left.size() && rightIndex < right.size())
    {
      if (left.get(leftIndex).getTitle().compareToIgnoreCase(right.get(rightIndex).getTitle()) <= 0)
      {
        merged.add(left.get(leftIndex));
        leftIndex++;
      }
      else
      {
        merged.add(right.get(rightIndex));
        rightIndex++;
      }
    }

    while (leftIndex < left.size())
    {
      merged.add(left.get(leftIndex));
      leftIndex++;
    }

    while (rightIndex < right.size())
    {
      merged.add(right.get(rightIndex));
      rightIndex++;
    }

    return merged;
  }

  public static int linearSearchSentiments(ArrayList<Sentiment> sentiments, String target)
  {
    for (int i = 0; i < sentiments.size(); i++)
    {
      if (sentiments.get(i).getPhrase().equalsIgnoreCase(target))
      {
        return i;
      }
    }
    return -1;
  }

  public static int iterativeBinarySearchSentiments(ArrayList<Sentiment> sentiments, String target)
  {
    int low = 0;
    int high = sentiments.size() - 1;

    while (low <= high)
    {
      int mid = (low + high) / 2;
      int comparison = sentiments.get(mid).getPhrase().compareToIgnoreCase(target);

      if (comparison == 0)
      {
        return mid;
      }
      else if (comparison < 0)
      {
        low = mid + 1;
      }
      else
      {
        high = mid - 1;
      }
    }

    return -1;
  }

  public static int recursiveBinarySearchSentiments(ArrayList<Sentiment> sentiments, String target)
  {
    return recursiveBinarySearchSentiments(sentiments, target, 0, sentiments.size() - 1);
  }

  private static int recursiveBinarySearchSentiments(ArrayList<Sentiment> sentiments, String target, int low, int high)
  {
    if (low > high)
    {
      return -1;
    }

    int mid = (low + high) / 2;
    int comparison = sentiments.get(mid).getPhrase().compareToIgnoreCase(target);

    if (comparison == 0)
    {
      return mid;
    }
    else if (comparison < 0)
    {
      return recursiveBinarySearchSentiments(sentiments, target, mid + 1, high);
    }
    else
    {
      return recursiveBinarySearchSentiments(sentiments, target, low, mid - 1);
    }
  }

  public static void printSampleBooks(ArrayList<Book> books, String label)
  {
    System.out.println(label);
    int limit = Math.min(10, books.size());
    for (int i = 0; i < limit; i++)
    {
      System.out.println("  " + books.get(i).getTitle());
    }
    System.out.println();
  }

  public static void printSearchResult(ArrayList<Sentiment> sentiments, String methodName, String target, int index)
  {
    if (index == -1)
    {
      System.out.println(methodName + " -> \"" + target + "\" not found");
    }
    else
    {
      System.out.println(methodName + " -> \"" + target + "\" found at index " + index
          + " with value " + sentiments.get(index).getValue());
    }
  }
}