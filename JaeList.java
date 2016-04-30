import constant.*;
import static constant.Keyword.*;



/**
*	My circular implementation of an array-based abstract data structure resembling a queue to be used in the analysis of trade data.
*
*	@author Jaewan Yun (Jay50@pitt.edu)
*	@version 1.0.0
*/



public class JaeList<T>
{
	// underlying data structure.
	private volatile T[] jaeList = null;

	// class settings.
	private final int MAX_CAPACITY = 10000000;
	private final int DEFAULT_CAPACITY = 2;
	private final double EXPANSION_FACTOR = 2.0;
	private final double REDUCTION_FACTOR = 2.0;

	// class states.
	private static volatile long concurrentCapacity = 0;
	private static volatile int concurrentExistence = 0;

	// array states.
	private volatile int size = 0;
	private volatile int capacity = 0;

	private boolean initialized = false;

	// note that cursor does not indicate index.
	private volatile int headCursor = 0;
	private volatile int tailIndex = 0;

	/**
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	public JaeList()
	{
		jaeList = constructArray(DEFAULT_CAPACITY);
		capacity = DEFAULT_CAPACITY;
		initialized = true;
		synchronized(this.getClass())
		{
			concurrentCapacity += DEFAULT_CAPACITY;
			concurrentExistence++;
		}
	}

	/**
	*	@param capacity The desired capacity of the underlying data structure.
	*	@throws IllegalArgumentException when the size of the accepted value exceeds a predetermined maximum capacity.
	* 	@throws IllegalArgumentException when the size of the accepted value is less than one.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	public JaeList(int capacity)
	{
		jaeList = constructArray(capacity);
		this.capacity = capacity;
		initialized = true;
		synchronized(this.getClass())
		{
			concurrentCapacity += capacity;
			concurrentExistence++;
		}
	}

	/**
	*	@param input An array used as a template.
	*	@return true when storage was successful, and false if otherwise.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	public JaeList(T[] input)
	{
		storeArray(input);
		initialized = true;
		synchronized(this.getClass())
		{
			concurrentExistence++;
		}
	}

	/**
	*	A helper method for add(T, Keyword).
	*/
	public void add(T entry)
	{
		add(entry, NULL);
	}
	/**
	*	@param entry An entry to be added.
	*	@param keyword Used for development.
	*	@throws IllegalStateException when this has not been properly initialized.
	*	@throws IllegalArgumentException when entry cannot be added due to a predetermined maximum capacity.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	public synchronized void add(T entry, Keyword keyword)
	{
		// DEBUG
		if(keyword == IDDEBUG)
		{
			System.out.println("\n\nDEBUG ENABLED");
			System.out.println("Prior to ADD : CAPACITY : " + capacity);
			System.out.println("Prior to ADD : HEADCURSOR : " + headCursor);
			System.out.println("Prior to ADD : TAILINDEX : " + tailIndex);
			System.out.println("Prior to ADD : SIZE : " + size + " ADDING " + entry + "(" + entry.getClass().toString() + ")");
		}
		else if(keyword == DEBUG)
		{
			System.out.println("Prior to ADD : SIZE : " + size + " ADDING " + entry + "(" + entry.getClass().toString() + ")");
		}
		// END DEBUG


		// add the entry to the headCursor position and increment headCursor using modulo.
		checkInitialization();
		if(isFull())
			increaseCapacity(EXPANSION_FACTOR, keyword);
		jaeList[headCursor] = entry;
		headCursor = (headCursor + 1) % capacity;
		size++;


		// DEBUG
		if(keyword == IDDEBUG)
		{
			System.out.println("After ADD : CAPACITY : " + capacity);
			System.out.println("After ADD : HEADCURSOR : " + headCursor);
			System.out.println("After ADD : TAILINDEX : " + tailIndex);
			System.out.println("After ADD : SIZE : " + size + " ADDED " + entry + "(" + entry.getClass().toString() + ")");
		}
		else if(keyword == DEBUG)
		{
			System.out.println("After ADD : SIZE : " + size + " ADDED " + entry + "(" + entry.getClass().toString() + ")");
		}
		// END DEBUG
	}

	// /**
	// *	@param entry An entry to be added.
	// *	@param position The index at which the entry will be inserted into.
	// *	@throws IllegalStateException when this has not been properly initialized.
	// *	@throws IllegalArgumentException when entry cannot be added due to a predetermined maximum capacity.
	// *	@since 1.0.0
	// *	@author Jaewan Yun (Jay50@pitt.edu)
	// */
	// public synchronized void add(T entry, int position)
	// {
	// 	checkInitialization();
	// 	if(isFull())
	// 		increaseCapacity(EXPANSION_FACTOR);
	// }

	/**
	*	A helper method for remove(Keyword).
	*/
	public T remove()
	{
		return remove(NULL);
	}
	/**
	*	@param keyword Used for development.
	*	@return the element that was removed.
	*	@throws IllegalArgumentException if data structure is empty.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	public synchronized T remove(Keyword keyword)
	{
		// check that data structure is non-empty
		if(isEmpty())
			throw new IllegalArgumentException();


		// DEBUG
		if(keyword == IDDEBUG)
		{
			System.out.println("\n\nDEBUG ENABLED");
			System.out.println("Prior to REMOVE : CAPACITY : " + capacity);
			System.out.println("Prior to REMOVE : HEADCURSOR : " + headCursor);
			System.out.println("Prior to REMOVE : TAILINDEX : " + tailIndex);
			System.out.println("Prior to REMOVE : SIZE : " + size + " REMOVING " + jaeList[tailIndex] + "(" + jaeList[tailIndex].getClass().toString() + ")");
		}
		else if(keyword == DEBUG)
		{
			System.out.println("Prior to REMOVE : SIZE : " + size + " REMOVING " + jaeList[tailIndex] + "(" + jaeList[tailIndex].getClass().toString() + ")");
		}
		// END DEBUG


		// remove an item from the tailIndex and increment tailIndex using modulo.
		T toReturn = jaeList[tailIndex];
		jaeList[tailIndex] = null;
		tailIndex = ++tailIndex % capacity;
		size--;


		// DEBUG
		if(keyword == DEBUG || keyword == IDDEBUG)
		{
			System.out.println("After REMOVE : SIZE : " + size + " REMOVED " + toReturn + "(" + toReturn.getClass().toString() + ")");
		}
		// END DEBUG


		// reduce capacity.
		if(size < (capacity / 4))
			decreaseCapacity(REDUCTION_FACTOR, keyword);


		// // DEBUG
		if(keyword == IDDEBUG)
		{
			System.out.println("After REMOVE : CAPACITY : " + capacity);
			System.out.println("After REMOVE : HEADCURSOR : " + headCursor);
			System.out.println("After REMOVE : TAILINDEX : " + tailIndex);
		}
		// // END DEBUG


		return toReturn;
	}

	/**
	*	@param factor The multiplicative expansion coefficient.
	*	@param keyword Used for development.
	*	@throws IllegalArgumentException when capacity cannot increase due to a predetermined maximum capacity.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	private synchronized void increaseCapacity(double factor, Keyword keyword)
	{
		// DEBUG
		if(keyword == DEBUG)
		{
			System.out.println("\nSIZE : " + size + " out of " + capacity);
			System.out.println("INCREASING CAPACITY...");
			int count = 0;
			for(int j = 0; j < jaeList.length; j++)
			{
				System.out.print("\tPOS " + j + " > ");
				if(jaeList[j] != null)
				{
					System.out.println(jaeList[j] + "(" + jaeList[j].getClass().toString() + "), ");
					count++;
				}
				else
				{
					System.out.println("null");
				}
			}
			System.out.println("objectCounter : " + count);
		}
		// END DEBUG


		// increase capacity.
		synchronized(this.getClass())
		{
			concurrentCapacity -= capacity;
		}
		capacity = (int) (capacity * factor);
		synchronized(this.getClass())
		{
			concurrentCapacity += capacity;
		}
		T[] temporaryRef = constructArray(capacity);
		for(int j = 0; j < size; j++)
		{
			temporaryRef[j] = jaeList[tailIndex++ % (capacity - 1)];
		}
		tailIndex = 0;
		headCursor = size;
		jaeList = temporaryRef;


		// DEBUG
		if(keyword == DEBUG)
		{
			System.out.println("CAPACITY INCREASED TO : " + capacity + "\n");
		}
		// END DEBUG
	}

	/**
	*	@param factor The multiplicative reduction coefficient.
	*	@throws IllegalArgumentException when capacity cannot increase due to a predetermined maximum capacity.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	private synchronized void decreaseCapacity(double factor, Keyword keyword)
	{
		// DEBUG
		if(keyword == DEBUG)
		{
			for(int j = 0; j < 10; j++)
			{
				System.out.print("\n");
			}
			System.out.println("\nSIZE : " + size + " out of " + capacity);
			int count = 0;
			for(int j = 0; j < jaeList.length; j++)
			{
				System.out.print("\tPOS " + j + " > ");
				if(jaeList[j] != null)
				{
					System.out.println(jaeList[j] + "(" + jaeList[j].getClass().toString() + "), ");
					count++;
				}
				else
				{
					System.out.println("null");
				}
			}
			System.out.println("objectCounter : " + count);
			for(int j = 0; j < 10; j++)
			{
				System.out.print("\n");
			}
		}
		// END DEBUG


		// decrease capacity.
		int originalCapacity = capacity;
		synchronized(this.getClass())
		{
			concurrentCapacity -= capacity;
		}
		capacity = (int) (capacity / factor);
		synchronized(this.getClass())
		{
			concurrentCapacity += capacity;
		}
		T[] temporaryRef = constructArray(capacity);
		for(int j = 0; j < capacity - 1; j++)
		{
			temporaryRef[j] = jaeList[tailIndex++ % (originalCapacity - 1)];
		}
		tailIndex = 0;
		headCursor = size;
		jaeList = temporaryRef;


		// DEBUG
		if(keyword == DEBUG)
		{
			System.out.println("\nSIZE : " + size + " out of " + capacity);
			System.out.println("DECREASING CAPACITY...");
			int count = 0;
			for(int j = 0; j < jaeList.length; j++)
			{
				System.out.print("\tPOS " + j + " > ");
				if(jaeList[j] != null)
				{
					System.out.println(jaeList[j] + "(" + jaeList[j].getClass().toString() + "), ");
					count++;
				}
				else
				{
					System.out.println("null");
				}
			}
			System.out.println("objectCounter : " + count);
			System.out.println("CAPACITY DECREASED TO : " + capacity + "\n");
		}
		// END DEBUG
	}

	/**
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	public synchronized void clear()
	{
		jaeList = null;
		jaeList = constructArray(DEFAULT_CAPACITY);
		synchronized(this.getClass())
		{
			concurrentCapacity -= (capacity - DEFAULT_CAPACITY);

		}
		capacity = DEFAULT_CAPACITY;
		size = 0;
		headCursor = 0;
		tailIndex = 0;
	}

	/**
	*	@param input An array used as a template.
	*	@return true when storage was successful, and false if otherwise.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	private boolean storeArray(T[] input)
	{
		if(input == null)
		{
			return false;
		}

		if(jaeList == null)
		{
			synchronized(this.getClass())
			{
				jaeList = constructArray(input.length);
				capacity = input.length;
				concurrentCapacity += input.length;
			}
		}

		if(input.length >= jaeList.length)
		{
			synchronized(this.getClass())
			{
				jaeList = constructArray(input.length);
				capacity = input.length;
				concurrentCapacity -= jaeList.length;
				concurrentCapacity += input.length;
			}
		}

		// copy references
		synchronized(this)
		{
			size = 0;
			for(int j = 0; j < input.length; j++)
			{
				if(input[j] != null)
				{
					jaeList[j] = input[j];
					size++;
				}
			}
			headCursor = size;
			return true;
		}
	}

	/**
	*	@return A copy of this array.
	*	@throws IllegalStateException when this has not been properly initialized.
	*	@throws NullPointerException when jaeList is null.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
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
	*	@author Jaewan Yun (Jay50@pitt.edu)
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
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	@SuppressWarnings("unchecked") private T[] constructArray(int capacity)
	{
		if(capacity > MAX_CAPACITY || capacity < 1)
		{
			throw new IllegalArgumentException();
		}

		// initialize an array of type T
		T[] toReturn = (T[]) new Object[capacity];

		// setting the states
		initialized = true;
		return toReturn;
	}

	/**
	*	@throws IllegalStateException when this has not been properly initialized.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
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
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	public boolean isEmpty()
	{
		if(headCursor == tailIndex)
			return true;
		return false;
	}

	private boolean isFull()
	{
		if(((headCursor + 1) % capacity) == tailIndex)
			return true;
		return false;
	}

	/**
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	protected void finalize()
	{
		synchronized(this.getClass())
		{
			concurrentExistence--;
			concurrentCapacity -= capacity;
		}
	}

	public synchronized String toString()
	{
		return jaeList.toString();
	}

	/**
	*	@param keyword Keyword that the method body portion execution depends on
	*/
	public synchronized void showState(Keyword keyword)
	{
		if(keyword == DEBUG)
		{
			System.out.println("jaeList :\t" + jaeList);
			System.out.println("MAX_CAPACITY :\t" + MAX_CAPACITY);
			System.out.println("DEFAULT_CAPACITY :\t" + DEFAULT_CAPACITY);
			System.out.println("EXPANSION_FACTOR :\t" + EXPANSION_FACTOR);
			System.out.println("REDUCTION_FACTOR :\t" + REDUCTION_FACTOR);
			System.out.println("concurrentCapacity :\t" + concurrentCapacity);
			System.out.println("concurrentExistence :\t" + concurrentExistence);
			System.out.println("size :\t" + size);
			System.out.println("capacity :\t" + capacity);
			System.out.println("initialized :\t" + initialized);
			System.out.println("headCursor :\t" + headCursor);
			System.out.println("tailIndex :\t" + tailIndex);
			System.out.println("END OF JaeList EXPLICIT STATE\n");

			if(jaeList != null)
			{
				System.out.println("length :\t" + jaeList.length);
				if(jaeList[tailIndex] != null)
					System.out.println("tailIndex type :\t" + jaeList[tailIndex].getClass().toString());
				else
					System.out.println("tailIndex type :\tnull");
				if(jaeList[headCursor] != null)
					System.out.println("headCursor type :\t" + jaeList[tailIndex].getClass().toString());
				else
					System.out.println("headCursor type :\tnull");
				if(headCursor - 1 < 0)
					if(jaeList[capacity - 1] != null)
						System.out.println("headIndex type :\t" + jaeList[tailIndex].getClass().toString());
				if(headCursor - 1 >= 0)
					if(jaeList[headCursor - 1] != null)
						System.out.println("headIndex type :\t" + jaeList[tailIndex].getClass().toString());
				System.out.println("END OF T[] EXPLICIT STATE\n");

				for(int j = 0; j < jaeList.length; j++)
				{
					System.out.print("Index " + j + "\t:\t" + jaeList[j]);
					if(jaeList[j] != null)
						System.out.println(" of type " + jaeList[j].getClass().toString());
					else
						System.out.println();
				}
			}
			else
			{
				System.out.println("jaeList is null therefore unaccessible");
			}
		}
	}
}