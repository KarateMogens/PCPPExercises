// For week 8
// raup@itu.dk * 2023-10-20
package exercises08;

import java.util.concurrent.atomic.AtomicReference;

// Treiber's LockFree Stack (Goetz 15.4 & Herlihy 11.2)
class LockFreeStack<T> {
	AtomicReference<Node<T>> top = new AtomicReference<Node<T>>(); // Initializes to null

	public void push(T value) {
		Node<T> newHead = new Node<T>(value);
		Node<T> oldHead;
		do {
			oldHead = top.get(); // pu1
			newHead.next = oldHead; // pu2
		} while (!top.compareAndSet(oldHead, newHead)); // pu3 Linearization pint

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

	// class for nodes
	private static class Node<T> {
		public final T value;
		public Node<T> next;

		public Node(T value) {
			this.value = value;
			this.next = null;
		}
	}
}
