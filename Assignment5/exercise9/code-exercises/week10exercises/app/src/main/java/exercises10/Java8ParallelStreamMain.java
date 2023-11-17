package exercises10;

import java.util.Arrays;
import java.util.stream.IntStream;

public class Java8ParallelStreamMain {
	public static void main(String[] args) {
	
		int size = 20;
		int range = 10000000;
		int[] array = new int[size];

		for (int i = 0; i < size; i++) {
			array[i] = i;
		}
		
		System.out.println("=================================");
		System.out.println("Using Sequential Stream");
		System.out.println("=================================");
		IntStream intArrStream = Arrays.stream(array);
		intArrStream.forEach(s -> {
			int ctr = 0;
			for (int i = 0; i < range; i++) {
				if (isPrime(i)) {
					ctr++;
				};
			}
			System.out.println(s + " " + Thread.currentThread().getName() + "\t\t result: " + ctr);
		});


		System.out.println("=================================");
		System.out.println("Using Parallel Stream");
		System.out.println("=================================");
		IntStream intParallelStream = Arrays.stream(array).parallel();
		intParallelStream.forEach(s -> {
			int ctr = 0;
			for (int i = 0; i < range; i++) {
				if (isPrime(i)) {
					ctr++;
				};
			}
			System.out.println(s + " " + Thread.currentThread().getName() + "\t\t result: " + ctr);
		});
	}

	private static boolean isPrime(int n) {
		int k = 2;
		while (k * k <= n && n % k != 0)
		  k++;
		return n >= 2 && k * k > n;
	  }
}