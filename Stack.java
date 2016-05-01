public interface Stack<T>
{
	// Insert. Never returns null
	T push(T entry);

	// Remove. Never returns null
	T pop();

	// Examine. Never returns null
	T peek();
}