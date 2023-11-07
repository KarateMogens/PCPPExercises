package exercises10;
//Exercise 10.1

//JSt vers Oct 23, 2023

import java.util.*;
import java.util.stream.*;
import java.io.IOException;
import java.lang.NumberFormatException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.concurrent.atomic.AtomicLong;
import benchmarking.Benchmark;

class PrimeCountingPerf {
  public static void main(String[] args) {
    new PrimeCountingPerf();

  }

  static final int range = 100000;

  // Test whether n is a prime number
  private static boolean isPrime(int n) {
    int k = 2;
    while (k * k <= n && n % k != 0)
      k++;
    return n >= 2 && k * k > n;
  }

  // Sequential solution
  private static long countSequential(int range) {
    long count = 0;
    final int from = 0, to = range;
    for (int i = from; i < to; i++)
      if (isPrime(i))
        count++;
    return count;
  }

  // IntStream solution
  private static long countIntStream(int range) {
    return IntStream.range(2,range)
    .filter(number -> isPrime(number))
    .peek(System.out::println)
    .count();
  }

  // Parallel Stream solution
  private static long countParallel(int range) {
    return IntStream.range(2,range)
    .parallel()
    .filter(number -> isPrime(number))
    .count();
  }

  // parallelStream solution
  private static long countparallelStream(List<Integer> list) {
    return list.parallelStream()
    .filter(number -> isPrime(number))
    .count();
  }

  public PrimeCountingPerf() {
    Benchmark.Mark7("Sequential", i -> countSequential(range));

    Benchmark.Mark7("IntStream", i -> countIntStream(range));

    Benchmark.Mark7("Parallel", i -> countParallel(range));

    List<Integer> list = new ArrayList<Integer>();
    for (int i = 2; i < range; i++) {
      list.add(i);
    }
    Benchmark.Mark7("ParallelStream", i -> countparallelStream(list));
  }
}
