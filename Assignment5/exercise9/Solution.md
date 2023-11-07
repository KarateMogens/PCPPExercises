## Exercise 10.1

1. Compile and run PrimeCountingPerf.java. Record the result in a text ﬁle.

>**Answer:**
>
>       Sequential                      2602694,8 ns   24759,71        128


2. Fill in the Java code using a stream for counting the number of primes (in the range: 2..range). Record the result in a text ﬁle.

>**Answer:**
>
>See code: `PrimeCounntingPerf.java`
>
>       Sequential                      2606033,4 ns   44394,75        128
>       IntStream                       2620858,5 ns    2837,64        128

3. Add code to the stream expression that prints all the primes in the range 2..range. To test this program reduce range to a small number e.g. 1000.

>**Answer:**
>
>See code: `PrimeCounntingPerf.java`

4. Fill in the Java code using the intermediate operation parallel for counting the number of primes (in the range: 2..range). Record the result in a text ﬁle.

>>**Answer:**
>
>See code: `PrimeCounntingPerf.java`
>
>       Sequential                      2619598,3 ns   23794,32        128
>       IntStream                       2626041,3 ns    5906,34        128
>       Parallel                         697092,3 ns   12380,81        512


5. Add another prime counting method using a parallelStream for counting the number of primes (in
the range: 2..range). Measure its performance using Mark7 in a way similar to how we measured the
performance of the other three ways of counting primes.

>>**Answer:**
>
>See code: `PrimeCounntingPerf.java`
>
>       Sequential                      2590493,0 ns   11495,41        128
>       IntStream                       2623461,9 ns    3633,88        128
>       Parallel                         702823,4 ns   15395,76        512
>       ParallelStream                   748376,8 ns   13181,27        512


## Exercise 10.2 

This exercise is about processing a large body of English words, using streams of strings. In particular, we use the words in the ﬁle `app/src/main/resources/english-words.txt`, in the exercises
project directory. The exercises below should be solved without any explicit loops (or recursion) as far as possible (that is, use streams).

1. Starting from the TestWordStream.java ﬁle, complete the readWords method and check that you can read
the ﬁle as a stream and count the number of English words in it. For the english-words.txt ﬁle on
the course homepage the result should be 235,886.

>**Answer:**
>
> See code: `TestWordStream.java`

2. Write a stream pipeline to print the ﬁrst 100 words from the ﬁle.

>**Answer:**
>
> See code: `TestWordStream.java`

3. Write a stream pipeline to ﬁnd and print all words that have at least 22 letters.

>**Answer:**
>
> See code: `TestWordStream.java`

4. Write a stream pipeline to ﬁnd and print some word that has at least 22 letters.

>**Answer:**
>
> See code: `TestWordStream.java`

5. Write a method boolean isPalindrome(String s) that tests whether a word s is a palindrome: a word that is the same spelled forward and backward. Write a stream pipeline to ﬁnd all palindromes and
print them.

>**Answer:**
>
> See code: `TestWordStream.java`

6. Make a parallel version of the palindrome-printing stream pipeline. Is it possible to observe whether it is faster or slower than the sequential one?

>**Answer:**
>
> See code: `TestWordStream.java`
>
> ## UNANSWERED QUESTION

7. Make a new version of the method `readWordStream` which can fetch the list of words from the internet There is a (slightly modiﬁed) version of the word list at this URL: https://staunstrups.dk/jst/english-words.txt. Use this version of `readWordStream` to count the number of words (similarly to question 7.2.1). Note, the number of words is not the same in the two ﬁles !!

>**Answer:**
>
> See code: `TestWordStream.java`

8. Use a stream pipeline that turns the stream of words into a stream of their lengths, to ﬁnd and print the minimal, maximal and average word lengths.

Hint: There is a simple solution using an operator exampliﬁed on p. 141 of Java Precisely (included in the readings for this week).

>**Answer:**
>
> See code: `TestWordStream.java`


## Exercise 10.3

This exercise is based on the article: (Introduction to Java 8 Parallel Stream) (on the readme for
week10). Start by reading this.

1. Redo the ﬁrst example (running the code in Java8ParallelStreamMain) described in the article. Your solution should contain the output from doing this experiment and a short explanation of your output.

>**Answer:**
> 
>The ouput below shows the result of running the code described in the first example of the article. In the example we use streams to print the elements of an integer array, along with some information about the thread carrying out the function: `forEach(s->{System.out.println(s+" "+Thread.currentThread().getName());})`. In the sequential run we see the thread `main` is the responsible thread in all cases. In the example using a parallel stream, we see that the work is shared among 5 thread; main, 1, 2, 3, and 4. We also see that the parallel stream uses the ForkJoinPool class to manage threads.
>
>**Result:**
>
        =================================
        Using Sequential Stream
        =================================
        1 main
        2 main
        3 main
        4 main
        5 main
        6 main
        7 main
        8 main
        9 main
        10 main
        =================================
        Using Parallel Stream
        =================================
        7 main
        6 main
        3 ForkJoinPool.commonPool-worker-1
        4 ForkJoinPool.commonPool-worker-3
        2 main
        8 main
        9 ForkJoinPool.commonPool-worker-2
        1 ForkJoinPool.commonPool-worker-3
        10 ForkJoinPool.commonPool-worker-4
        5 ForkJoinPool.commonPool-worker-1

2. Increase the size of the integer array (from the 10 in the article) and see if there is a relation between number of cores on your computer and the number of workes in the ForkJoin.

>**Answer:**
> 
>Already at an array-size of 20, we see a much larger number of threads being utilized, and seemingly all 8 cores are already being used.
>
>**Result:**
>
        =================================
        Using Parallel Stream
        =================================
        12 main
        14 main
        13 main
        6 ForkJoinPool.commonPool-worker-1
        5 ForkJoinPool.commonPool-worker-1
        10 ForkJoinPool.commonPool-worker-4
        17 ForkJoinPool.commonPool-worker-2
        11 main
        4 ForkJoinPool.commonPool-worker-7
        3 ForkJoinPool.commonPool-worker-7
        18 ForkJoinPool.commonPool-worker-7
        0 ForkJoinPool.commonPool-worker-7
        8 ForkJoinPool.commonPool-worker-6
        7 ForkJoinPool.commonPool-worker-1
        9 ForkJoinPool.commonPool-worker-5
        2 ForkJoinPool.commonPool-worker-3
        1 ForkJoinPool.commonPool-worker-4
        19 ForkJoinPool.commonPool-worker-2
        16 main
        15 ForkJoinPool.commonPool-worker-7

3. Change the example by adding a time consuming task (e.g. counting primes in a limited range or the example in the artcle). Report what you see when running the example.

## Exercise 10.4

The solution to this exercise is just a short explanation in English - there is no code to develop and run.

Despite many superﬁcial syntactical similarities between JavaStream and RxJava, the two concepts are fundamentally different. This exercise focus on some of these differences.

Consider the pseudo-code below (that does not compile and run). The `source()` provides english words and the `sink()` absorbs them. Note that `sink()` is a pseudo-operation that just absorbs the data it receives. Your explanations should focus on what happens in between the `source()` and `sink()`.

1. Describe what happens when this code runs:

        source().filter(w -> w.length() > 5).sink()

• as a JavaStream (e.g. the source is a ﬁle)
• as a RxJava statement where the source could be an input ﬁeld where a user types strings'

>**Answer:**
>
> **JavaStream:** The sink function pulls an element from the filter function, which in turn pulls an element from the source-file. The element (a string) is checked against the boolean condition and is either dropped if the length is below 5 or is passed on to sink if it has a length equal to or larger than 5.
> **RxJava Statement:** An observable pushes elements to the filter function, which filters out inputs that are shorter than length 5. The filter function then pushes all non-filtered elements onto the sink() function.

2. Describe what happens when this code runs:
source().filter(w -> w.length() > 5).sink();
source().filter(w -> w.length() > 10).sink()
• as a JavaStream (e.g. the source is a ﬁle)
• as a RxJava statement where the source could be an input ﬁeld where a user types strings

>**Answer:**
>
> ## Whats the question???