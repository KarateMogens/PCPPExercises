package exercises10;

import java.util.Arrays;
import java.util.stream.IntStream;
public class Java8ParallelStreamMain {
	public static void main(String[] args) {
		System.out.println("=================================");
		System.out.println("Using Sequential Stream");
		System.out.println("=================================");
		int size = 20;
		int[] array = new int[size];
		for (int i = 0; i < size; i++) {
			array[i] = i;
		}
		//int[] array= {1,2,3,4,5,6,7,8,9,10};
		IntStream intArrStream=Arrays.stream(array);
		intArrStream.forEach(s->
		{
			System.out.println(s+" "+Thread.currentThread().getName());
		}
				);
		System.out.println("=================================");
		System.out.println("Using Parallel Stream");
		System.out.println("=================================");
		IntStream intParallelStream=Arrays.stream(array).parallel();
		intParallelStream.forEach(s->
		{
			System.out.println(s+" "+Thread.currentThread().getName());
		}
				);
	}
}