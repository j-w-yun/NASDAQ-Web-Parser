/**
*	My circular implementation of an array-based abstract data structure to be used in the analysis of trade data.
*
*	@author Jaewan Yun (Jay50@pitt.edu)
*	@version 1.0.0
*/

public class JaeList<T>
{
	private T[] jaeList = null;

	private final int MAX_CAPACITY = 10000000;
	private final int DEFAULT_CAPACITY = 100;
	private final double EXPANSION_FACTOR = 2.0;

	private static volatile long concurrentCapacity = 0;
	private static volatile int concurrentExistence = 0;

	private int size = 0;
	private int capacity = 0;
	private boolean initialized = false;

	private int headIndex = 0;
	private int tailIndex = 0;

	/**
	*	@since 1.0.0
	*/
	public JaeList()
	{
		jaeList = constructArray(DEFAULT_CAPACITY);
		capacity = DEFAULT_CAPACITY;
		initialized = true;
		concurrentCapacity += DEFAULT_CAPACITY;
		concurrentExistence++;
	}

	/**
	*	@param capacity The desired capacity of the underlying data structure.
	*	@throws IllegalArgumentException when the size of the accepted value exceeds a predetermined maximum capacity.
	* 	@throws IllegalArgumentException when the size of the accepted value is less than one.
	*	@since 1.0.0
	*/
	public JaeList(int capacity)
	{
		jaeList = constructArray(capacity);
		this.capacity = capacity;
		initialized = true;
		concurrentCapacity += capacity;
		concurrentExistence++;
	}

	/**
	*	@param input An array used as a template.
	*	@return true when storage was successful, and false if otherwise.
	*	@since 1.0.0
	*/
	public JaeList(T[] input)
	{
		storeArray(input);
		initialized = true;
		concurrentExistence++;
	}

	/**
	*	@param entry An entry to be added.
	*	@throws IllegalStateException when this has not been properly initialized.
	*	@throws IllegalArgumentException when entry cannot be added due to a predetermined maximum capacity.
	*	@since 1.0.0
	*/
	public synchronized void add(T entry)
	{
		checkInitialization();
		if((headIndex + 1) == tailIndex)
			increaseCapacity(EXPANSION_FACTOR);

		headIndex = ++headIndex % (size - 1);
		jaeList[headIndex] = entry;
		size++;
	}

	/**
	*	@param entry An entry to be added.
	*	@param position The index at which the entry will be inserted into.
	*	@throws IllegalStateException when this has not been properly initialized.
	*	@throws IllegalArgumentException when entry cannot be added due to a predetermined maximum capacity.
	*	@since 1.0.0
	*/
	public synchronized void add(T entry, int position)
	{

	}

	/**
	*	@param factor The multiplicative expansion coefficient.
	*	@throws IllegalArgumentException when capacity cannot increase due to a predetermined maximum capacity.
	*	@since 1.0.0
	*/
	private synchronized void increaseCapacity(double factor)
	{
		concurrentCapacity -= capacity;
		capacity = (int) (capacity * factor);
		concurrentCapacity += capacity;

		T[] temporaryRef = constructArray(capacity);
		for(int j = 0; j < size; j++)
		{
			temporaryRef[j] = jaeList[tailIndex++ % (capacity - 1)];
		}

		tailIndex = 0;
		headIndex = (size - 1);
		jaeList = temporaryRef;
	}

	/**
	*	@since 1.0.0
	*/
	public synchronized void clear()
	{
		jaeList = null;
		jaeList = constructArray(DEFAULT_CAPACITY);
		concurrentCapacity -= (capacity - DEFAULT_CAPACITY);
		capacity = DEFAULT_CAPACITY;
		size = 0;
		headIndex = 0;
		tailIndex = 0;
	}

	/**
	*	@param input An array used as a template.
	*	@return true when storage was successful, and false if otherwise.
	*	@since 1.0.0
	*/
	private synchronized boolean storeArray(T[] input)
	{
		if(input == null)
		{
			return false;
		}

		if(jaeList == null)
		{
			jaeList = constructArray(input.length);
			capacity = input.length;
			concurrentCapacity += input.length;
		}

		if(input.length >= jaeList.length)
		{
			jaeList = constructArray(input.length);
			capacity = input.length;
			concurrentCapacity -= jaeList.length;
			concurrentCapacity += input.length;
		}

		// copy references
		size = 0;
		for(int j = 0; j < input.length; j++)
		{
			if(input[j] != null)
			{
				jaeList[j] = input[j];
				size++;
			}
		}
		headIndex = size;
		return true;
	}

	/**
	*	@return A copy of this array.
	*	@throws IllegalStateException when this has not been properly initialized.
	*	@throws NullPointerException when jaeList is null.
	*	@since 1.0.0
	*/
	public synchronized T[] toArray()
	{
		checkInitialization();
		return copyOf(jaeList);
	}

	/**
	*	@param toCopy An array used as a template.
	*	@return A copy of the accepted array.
	*	@throws NullPointerException when the accepted array is null.
	*	@throws IllegalArgumentException when the size of the accepted array exceeds a predetermined maximum capacity.
	*	@since 1.0.0
	*/
	@SuppressWarnings("unchecked") private T[] copyOf(T[] toCopy)
	{
		synchronized(this)
		{
			if(toCopy == null)
			{
				throw new NullPointerException();
			}

			if(toCopy.length > MAX_CAPACITY)
			{
				throw new IllegalArgumentException();
			}

			// copy the accepted array
			T[] toReturn = (T[]) new Object[toCopy.length];
			for(int j = 0; j < toCopy.length; j++)
			{
				toReturn[j] = toCopy[j];
			}
			return toReturn;
		}
	}

	/**
	*	@param capacity The capacity of the array to be constructed.
	*	@return Initialized array of T types with the accepted value as its capacity.
	*	@throws IllegalArgumentException when the size of the accepted value exceeds a predetermined maximum capacity.
	* 	@throws IllegalArgumentException when the size of the accepted value is less than one.
	*	@since 1.0.0
	*/
	@SuppressWarnings("unchecked") private T[] constructArray(int capacity)
	{
		if(capacity > MAX_CAPACITY || capacity < 1)
		{
			throw new IllegalArgumentException();
		}

		// initialize an array of type T
		T[] toReturn = (T[]) new Object[capacity];
		for(int j = 0; j < capacity; j++)
		{
			toReturn[j] = (T) new Object();
		}

		// setting the states
		initialized = true;
		this.capacity = capacity;
		size = 0;
		return toReturn;
	}

	/**
	*	@throws IllegalStateException when this has not been properly initialized.
	*	@since 1.0.0
	*/
	private void checkInitialization()
	{
		if(!initialized)
		{
			throw new IllegalStateException();
		}
	}

	/**
	*	@return true if no elements exist in this data structure.
	*	@since 1.0.0
	*/
	public boolean isEmpty()
	{
		if(headIndex == tailIndex)
			return true;
		return false;
	}

	/**
	*	@since 1.0.0
	*/
	protected void finalize()
	{
		synchronized(this.getClass())
		{
			concurrentExistence--;
			concurrentCapacity -= capacity;
		}
	}
}