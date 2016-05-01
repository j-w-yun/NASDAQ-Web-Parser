public interface Queue<T>
{
	// Insert. Never returns null
	T add(T entry);

	// Remove. Never returns null
	T remove();

	// Examine. Never returns null
	T element();
}