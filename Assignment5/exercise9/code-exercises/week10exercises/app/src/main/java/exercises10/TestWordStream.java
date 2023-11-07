//Exercise 10.?
//JSt vers Oct 23, 2023

//install  src/main/resources/english-words.txt
package exercises10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.IntSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TestWordStream {
  public static void main(String[] args) {
    String filename = "src/main/resources/english-words.txt";
    String websiteURL = "https://staunstrups.dk/jst/english-words.txt";
    System.out.println(readWords(filename).count());
    // 10.2.2
    System.out.println("10.2.2:");
    readWords(filename).limit(100).forEach(System.out::println);
    // 10.2.3
    System.out.println("10.2.3:");
    readWords(filename).filter(word -> word.length() >= 22).forEach(System.out::println);
    // 10.2.4
    System.out.println("10.2.4:");
    readWords(filename).filter(word -> word.length() >= 22).limit(1).forEach(System.out::println);
    // 10.2.5
    System.out.println("10.2.5");
    readWords(filename).filter(word -> isPalindrome(word.toLowerCase())).forEach(System.out::println);
    // 10.2.6
    System.out.println("10.2.6");
    readWords(filename).parallel().filter(word -> isPalindrome(word.toLowerCase())).forEach(System.out::println);
    // 10.2.7
    System.out.println("10.2.7");
    System.out.println(readWordsFromURL(websiteURL).count());
    //10.2.8
    System.out.println("10.2.8");
    int max = readWordsFromURL(websiteURL).mapToInt(word -> word.length()).max().getAsInt();
    int min = readWordsFromURL(websiteURL).mapToInt(word -> word.length()).min().getAsInt();
    double average = readWordsFromURL(websiteURL).mapToInt(word -> word.length()).average().getAsDouble();
    System.out.println("max: " + max + "\nmin: " + min + "\navg: " + average);
  }

  public static Stream<String> readWords(String filename) {
    try {
      BufferedReader reader = new BufferedReader(new FileReader(filename));
      return reader.lines();
    } catch (IOException exn) { 
      return Stream.<String>empty();
    } 
  }

  public static boolean isPalindrome(String s) {
    
    int length = s.length();
    for (int i = 0; i < length/2; i++) {
      if (s.charAt(i) != s.charAt(length-1-i)) {
        return false;
      }
    }
    return true;

  }

  public static Stream<String> readWordsFromURL(String urlname) {
    try {
      HttpURLConnection connection = (HttpURLConnection) new URL(urlname).openConnection();
      BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
      return reader.lines();
    } catch (IOException exn) {
      return Stream.<String>empty();
    }
  }

  public static Map<Character,Integer> letters(String s) {
    Map<Character,Integer> res = new TreeMap<>();
    // TO DO: Implement properly
    return res;
  }
}
