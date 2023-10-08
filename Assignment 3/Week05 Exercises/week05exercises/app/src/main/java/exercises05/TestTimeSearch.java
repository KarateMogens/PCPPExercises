package exercises05;
// jst@itu.dk * 2023-09-05

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

import benchmarking.Benchmark;
import benchmarking.Benchmarkable;

public class TestTimeSearch {
  public static void main(String[] args) { new TestTimeSearch(4); }

  public TestTimeSearch() {
    final String filename = "src/main/resources/long-text-file.txt";
    final String target= "ipsum";

    final LongCounter lc= new LongCounter();  //name is abit misleading, it is just a counter
    String[] lineArray= readWords(filename);

    System.out.println("Array Size: "+ lineArray.length);
    System.out.println("# Occurences of "+target+ " :"+search(target, lineArray, 0, lineArray.length, lc));
  }

  public TestTimeSearch(int threads) {
    final String filename = "src/main/resources/long-text-file.txt";
    final String target= "ipsum";
    final String target2 = "libero";
    final String target3 = "lorem";


    final LongCounter lc = new LongCounter();  //name is abit misleading, it is just a counter
    String[] lineArray= readWords(filename);
    Benchmark.Mark7("TestTimeSearch (Linear)", i -> search(target, lineArray, 0, lineArray.length, lc)); lc.reset();
    Benchmark.Mark7("TestTimeSearch (Threads: " + threads + ")", i -> countParallelN(target, lineArray, threads, lc)); lc.reset();
    System.out.println("Occurences of " + target + " in linear search:" + search(target, lineArray, 0, lineArray.length, lc)); lc.reset();
    System.out.println("Occurences of " + target + " in parallel search:" + countParallelN(target, lineArray, threads, lc)); lc.reset();
    System.out.println("Occurences of " + target2 + " in linear search:" + search(target2, lineArray, 0, lineArray.length, lc)); lc.reset();
    System.out.println("Occurences of " + target2 + " in parallel search:" + countParallelN(target2, lineArray, threads, lc)); lc.reset();
    System.out.println("Occurences of " + target3 + " in linear search:" + search(target3, lineArray, 0, lineArray.length, lc)); lc.reset();
    System.out.println("Occurences of " + target3 + " in parallel search:" + countParallelN(target3, lineArray, threads, lc)); lc.reset();
  }

  private static long search(String x, String[] lineArray, int from, int to, LongCounter lc){
    //Search each line of file
    for (int i=from; i<to; i++ ) lc.add(linearSearch(x, lineArray[i]));
    //System.out.println("Found: "+lc.get());
    return lc.get();
  }

  private static int linearSearch(String x, String line) {
    //Search for occurences of c in line
    String[] arr= line.split(" ");
    int count= 0;
    for (int i=0; i<arr.length; i++ ) if ( (arr[i].equals(x)) ) count++;                   
    return count;
  }

  private static long countParallelN(String target, String[] lineArray, int noOfThreads, LongCounter lc) {
    
    //lc.reset();

    int range = lineArray.length;

    int rangeSize = range / noOfThreads;
    Thread[] threads = new Thread[noOfThreads];
    for (int i = 0; i < noOfThreads; i++) {
      int from = i*rangeSize;
      int to = (i + 1 == noOfThreads) ? range : rangeSize*(i+1); //Checks if we've reached the last range
      threads[i] = new Thread( () -> {
        for (int j = from; j < to; j++ ) {
          lc.add(linearSearch(target, lineArray[j]));
        }
      });
      threads[i].start();      
    }
    try {
      for (int i = 0; i < noOfThreads; i++) {
        threads[i].join();
      }
    } catch (InterruptedException e) {
      e.printStackTrace();
    } 
    return lc.get();
 }

  public static String[] readWords(String filename) {
    try {
        BufferedReader reader = new BufferedReader(new FileReader(filename));
        return reader.lines().toArray(String[]::new);   //will be explained in Week07;
    } catch (IOException exn) { return null;}
  }

  
}
