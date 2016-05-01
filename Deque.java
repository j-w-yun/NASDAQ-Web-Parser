public interface Deque<T>
{
	T addFirst(T entry);
	T addLast(T entry);
	T removeFirst();
	T removeLast();
	T getFirst();
	T getLast();
}