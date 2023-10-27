#### Syntax for concurrent executions, sequential executions and linearizations
For the answers in the exercises below, you may use the mathematical notation for histories in Herlihy, Chapter 3. However, to avoid getting into the details of the mathematical notation, you may use the following syntax to provide concurrent executions, sequential executions and linearizations. We describe the syntax by example.
Concurrent executions Consider the concurrent execution at the top of slide 34 in lecture 8. The following syntax represents the execution:

    A: -|  q.enq(x)  |-------|  p.enq(y)  |---->
    B: -----|  q.deq(x)  |------|  p.enq(y)  |->

The text in between `| |` denotes the method call. The left `|` denotes the point in time when the method was invoked. The right `|` denotes the point int time when the response of the method call is received (in other words, whenthemethodcallfinished). The width of `| |` denotes the duration of the method call. On the left hand side, A: and B: are thread names. In this case, the upper execution corresponds to thread A and the lower execution to thread B. The dashed line represents real time.
Sequential execution In slide 34 (lecture 8), we provide a (possible) sequential execution for the concurrent execution above. The sequential execution can be written using the following syntax:

    <q.enq(x),p.enq(y),q.deq(x),p.deq(y)>

The symbol `<` denotes the beginning of the execution, and the symbol > denotes the end of the execution. In a sequential execution, the sequence of method calls are listed in the (sequential) order of execution. Recall that real-time is irrelevant for sequential executions. We are only interested on whether a method call happens before another.
Linearizations Linearizations have the same syntax as sequential executions. Remember that the process of finding a linearization involves setting linearization points and map them to a sequential execution. For instance, the linearization of slide 37 (lecture 8) for the concurrent execution above is written as:

    <q.enq(x),q.deq(x),p.enq(y),p.deq(y)>


## Exercise 8.1 

In this exercise, we look into several concurrent executions of a FIFO queue. Your task is to determine whether they are sequentially consistent or linearizable (according to the standard specification of a sequential FIFO queue).

**Mandatory**

1. Is this execution sequentially consistent? If so, provide a sequential execution that satisfies the standard specification of a sequential FIFO queue. Otherwise, explain why it is not sequentially consistent.

        A: ---------------|q.enq(x)|--|q.enq(y)|->
        B: ---|q.deq(x)|------------------------->

>Answer:
The execution is sequentially consistent. Consider the execution:
>
>   `< q.enq(x), q.deq(x), q.enq(y) >`  or  `< q.eng(x), q.enq(y), q.deq(x) >`

2. Is this execution (same as above) linearizable? If so, provide a linearization that satisfies the standard specification of a sequential FIFO queue. Otherwise, explain why it is not linearizable.

        A: ---------------|q.enq(x)|--|q.enq(y)|->
        B: ---|q.deq(x)|------------------------->

>Answer:
> NO! Because we cannot have a combination of linerialization points where q.deq(x) happens after q.enq(x). Therefore, there is no possible execution that satisfies the specification of the object (FIFO).


3. Is this execution linearizable? If so, provide a linearization that satisfies the standard specification of a sequential FIFO queue. Otherwise, explain why it is not linearizable.

        A: ---|      q.enq(x)          |-->
        B: ------|q.deq(x)|--------------->

>Answer:
>
>Yes, the execution is linerealizable. Consider the following linerealization points:
>
>`< q.enq(x), q.deq(x) >`


4. Is this execution linearizable? If so, provide a linearization that satisfies the standard specification of a sequential FIFO queue. Otherwise, explain why it is not linearizable.

        A: ---|q.enq(x)|-----|q.enq(y)|-->
        B: --|       q.deq(y)          |->

>Answer:
>
>No, the execution is not linearizable. It is not possible to execute, without violating the specification of the FIFO. The 'x' that is first enqueued should be dequeued before the 'y' can be dequeued. No matter where the linearization point, for the `q.deq(y)`, is placed according to everything happening in A, it will violate the specification of the FIFO. 
>

**Challenging**

5. Is this execution sequentially consistent? If so, provide a sequential execution that satisfies the standard specification of a sequential FIFO queue. Otherwise, explain why it is not sequentially consistent.

        A: -|p.enq(x)|------------|q.enq(x)|------------|p.deq(y)|------------>
        B: ------------|q.enq(y)|------------|p.enq(y)|------------|q.deq(x)|->

>Answer:
>
> No it is not possible to execute sequentially consistent. No matter the sequence of execution it will violate the specification og the FIFO. 


## Exercise 8.2

In this exercise, we look into the Treiber Stack. Your task is to reason about its correctness using linearization and testing.

**Mandatory**
1. Define linearization points for the push and pop methods in the Treiber Stack code provided in `app/src/ main/java/exercises08/LockFreeStack.java`. Explain why those linearization points show that the implementation of the Treiber Stack is correct. For the explanation, follow a similar approach as we did in lecture 8 for the MS Queue (slides 44 and 45).


>**Answer:** 
> 
>Consider the following linearization points:

        public void push(T value) {
                Node<T> newHead = new Node<T>(value);
                Node<T> oldHead;
                do {
                    oldHead = top.get(); // pu1
                    newHead.next = oldHead; // pu2
                } while (!top.compareAndSet(oldHead, newHead)); // pu3 inearization point

            }

            public T pop() {
                Node<T> newHead;
                Node<T> oldHead;
                do {
                    oldHead = top.get(); // po1 linearization point (if stack is empty)
                    if (oldHead == null) { // po2
                        return null; // po3
                    } 
                    newHead = oldHead.next; // po4
                } while (!top.compareAndSet(oldHead, newHead)); // po5 - linearization point (if succeeds)

                return oldHead.value;
            }
>
>**Push-method**: 
>- `Push()` Has one lineriaztion point `pu3` - if succesfully executed, the element is pushed to the stack.
>- Take two threads; t1 and t2. If t1 and t2 both call `push()` on the stack to push a new item to the top of the stack, only one of the two threads will be successful, due to the atomic nature of `top.compareAndSet()` in `pu3`. The unsuccesfull thread will then fetch the new value for top and try again.
>- Take two threads; t1 and t2. If t1 calls push after t2 has succesfully called `push()`, t1 will simply change top to point to the item added by t2.

>**Pop-method**: 
>- `Pop()` has two linerization points `po1` and `po4`. 
>- `po1` - If the stack is empty the oldhead will become `null`. `po2` and `po3` will then be executed and return null. 
>- `po4` - If succesfully executed, the element is poped from the stack.
>- Take two threads t1 and t2. If both t1 and t2 call `pop()` at the same time on a non-empty stack (with at least two items), only one of the two threads will be able to set `top` to `top.next` due to the atomic nature of `top.compareAndSet()`. The failing thread wil fetch the new node `top` and try again.
>- If t1 and t2 call `pop()` on a stack with exactly 1 item left, only one of the two will update `top` to `null` and return the item. The second thread will fail and retry the `pop()`. Doing so it will fetch the new value for `top` and fail in `po2` and return `null`.
>- If t1 calls `push()` and t2 calls `pop()` at the same time several things can happen. If t1 is initially succesfull in calling `push()`, `top` will be updated to the newly pushed value and t2 will fail on `po5` and immediate try again, fetching the updated value for `top`. 
>- On the other hand, if t2 is initially succesfull, `top` will be updated to `top.next` and t1 will fail on `pu3` and immediately try again, fetching the updated value for `top` and succesfully push the new item.

2. Write a JUnit 5 functional correctness test for the push method of the Treiber Stack. Consider a stack of integers. The test must assert that after *n* threads push integers $x_1, x_2 , . . . , x_n$ , respectively, the total sum of the elements in the stack equals $\sum_{i=1}^{n}x_i$. Write your test in the test skeleton file `app/src/test/java/exercises08/TestLockFreeStack.java`.

> **Answer:**
>
> See code `TestLockFreeStack.java`. 
>
> Command to run: `gradle cleanTest test --tests exercises08.TestLockFreeStack`

3. Write a JUnit 5 functional correctness test for the pop method of the Treiber Stack. As before, consider a stack of integers. Given a stack with n elements $x_1, x_2, . . . , x_n$ already pushed, the test must assert the following: after n threads pop one element $y_i$ each, the sum of popped elements equals the sum of elements originally in the stack. That is: $\sum_{i=1}^{n}x_i = \sum_{i=1}^{n}y_i $. Write your test in the test skeleton file `app/src/test/java/exercises08/TestLockFreeStack.java`.

> **Answer:**
>
> See code `TestLockFreeStack.java`.
>
> Command to run: `gradle cleanTest test --tests exercises08.TestLockFreeStack`

## Exercise 8.3

In this exercise, we revisit the progress notions for non-blocking computation that we discussed
in lecture 7.

**Mandatory**
1. Consider the reader-writer locks exercise from week 7. There are four methods included in this type of locks: `writerTryLock`, `writerUnlock`, `readerTryLock` and `readerUnlock`. State, for each method, whether they are wait-free, lock-free or obstruction-free and explain your answers.

>**Answer:**
> **readerTryLock()**:
> This method is lock-free. It is lock-free because, we can guarantee that some method call finishes in a finite number of steps. That is, even for concurrent calls to readerTryLock() at least 1 thread is guaranteed to finish the execution in a single call. It is not wait-free because we cannot guarantee this for every call.
> **readerUnlock()**: This method is also lock-free. The same reasoning applies as the method described above - at least 1 thread will always complete its execution if the there is contention.
> **writerTryLock()**: This method is wait-free. This method is guaranteed to execute in a bounded number of steps, regardless of the fact that other threads are calling it or updating the monitor at the same time. In simpler terms - we have no while-loop.
> **writerUnlock()**: This method is also wait-free. This method is guaranteed to execute in a bounded number of steps, regardless of the fact that other threads are calling it or updating the monitor at the same time.


## Exercise 8.4

This exercise is a slightly modified version of exercise 3.7 in Herlihy page 71. This exercise concerns the following lock-free queue implementation:

        class IQueue<T> {
            AtomicInteger head = new AtomicInteger(0);
            AtomicInteger tail = new AtomicInteger(0);
            T[] items = (T[]) new Object[Integer.MAX_VALUE];

            public void enq(T x) {
                int slot;
                do {
                    slot = tail.get();
                } while (!tail.compareAndSet(slot, slot+1)); // E4
                items[slot] = x;
            }

            public T deq() throws EmptyException {
                T value;
                int slot;
                do {
                    slot = head.get();
                value = items[slot];
                if (value == null)
                    throw new EmptyException();
                } while (!head.compareAndSet(slot, slot+1)); // D8
                return value;
            }
        }

**Challenging**
1. In the code, we have marked two linearization points E4 and D8 for enq and deq, respectively. Show that, with these linearization points, the implementation is not linearizable (according to the standard specification of a sequential FIFO queue).

>**Answer:**
> 
>The implementation is not linearizable. Starting with an empty queue, a thread starting an execution of enq(x) it can reach the linearizations point E4, but before adding the x to the queue, another thread could start and finish an execution of deq(). This would return null, and violate the specification of the queue. 
