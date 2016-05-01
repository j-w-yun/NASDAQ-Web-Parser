import constant.*;
import static constant.Keyword.*;



/**
*	A circular implementation of an array-based abstract data structure with capabilities of
*	first-in-first-out to be used in the analysis of trade data. Project requires a thread-safe
*	queue.
*
*	@author Jaewan Yun (Jay50@pitt.edu)
*	@version 1.0.0
*/



public class JaeList<T>
{
	// underlying data structure.
	private volatile T[] jaeList = null;

	// class settings.
	private final int MAX_CAPACITY = 1000000000;
	private final int DEFAULT_CAPACITY = 2;	//e.g. 1024
	private final double EXPANSION_FACTOR = 2.0;
	private final double REDUCTION_FACTOR = 2.0;
	private final int REDUCTION_REQUIREMENT = 2;	//e.g. 1025

	// class states.
	private static volatile int concurrentObjects = 0;
	private static volatile long concurrentCapacity = 0;
	private static volatile long concurrentSize = 0;

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
			concurrentObjects++;
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
			concurrentObjects++;
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
			concurrentObjects++;
		}
	}

	/**
	*	@param entry An entry to be added.
	*	@throws IllegalStateException when this has not been properly initialized.
	*	@throws IllegalArgumentException when entry cannot be added due to a predetermined maximum capacity.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
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
			print(1, "\n\nIDDEBUG ENABLED");
			print(1, "Prior to ADD : CAPACITY : " + capacity);
			print(1, "Prior to ADD : HEADCURSOR : " + headCursor);
			print(1, "Prior to ADD : TAILINDEX : " + tailIndex);
		}
		if(keyword == DEBUG || keyword == IDDEBUG)
		{
			print(1, "Prior to ADD : SIZE : " + size + " ADDING " + entry + "(" + entry.getClass().toString() + ")");
		}
		// END DEBUG


		// add the entry to the headCursor position and increment headCursor using modulo.
		checkInitialization();
		if(isFull())
			increaseCapacity(EXPANSION_FACTOR, keyword);
		jaeList[headCursor] = entry;
		headCursor = (headCursor + 1) % capacity;
		size++;
		synchronized(this.getClass())
		{
			concurrentSize++;
		}


		// DEBUG
		if(keyword == IDDEBUG)
		{
			print(1, "After ADD : CAPACITY : " + capacity);
			print(1, "After ADD : HEADCURSOR : " + headCursor);
			print(1, "After ADD : TAILINDEX : " + tailIndex);
		}
		if(keyword == DEBUG || keyword == IDDEBUG)
		{
			print(1, "After ADD : SIZE : " + size + " ADDED " + entry + "(" + entry.getClass().toString() + ")");
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
	*	@return the element that was removed.
	*	@throws IllegalArgumentException if data structure is empty.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
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
			print(1, "\n\nIDDEBUG ENABLED");
			print(1, "Prior to REMOVE : CAPACITY : " + capacity);
			print(1, "Prior to REMOVE : HEADCURSOR : " + headCursor);
			print(1, "Prior to REMOVE : TAILINDEX : " + tailIndex);
		}
		if(keyword == DEBUG || keyword == IDDEBUG)
		{
			print(1, "Prior to REMOVE : SIZE : " + size + " REMOVING " + jaeList[tailIndex] + "(" + jaeList[tailIndex].getClass().toString() + ")");
		}
		// END DEBUG


		// remove an item from the tailIndex and increment tailIndex using modulo.
		T toReturn = jaeList[tailIndex];
		jaeList[tailIndex] = null;
		tailIndex = ++tailIndex % capacity;
		size--;
		synchronized(this.getClass())
		{
			concurrentSize--;
		}


		// DEBUG
		if(keyword == DEBUG || keyword == IDDEBUG)
		{
			print(1, "After REMOVE : SIZE : " + size + " REMOVED " + toReturn + "(" + toReturn.getClass().toString() + ")");
		}
		// END DEBUG


		// reduce capacity.
		if((size < (capacity / 4)) && (capacity > REDUCTION_REQUIREMENT))
			decreaseCapacity(REDUCTION_FACTOR, keyword);


		// // DEBUG
		if(keyword == IDDEBUG)
		{
			print(1, "After REMOVE : CAPACITY : " + capacity);
			print(1, "After REMOVE : HEADCURSOR : " + headCursor);
			print(1, "After REMOVE : TAILINDEX : " + tailIndex);
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
			print(1, "\nSIZE : " + size + " out of " + capacity);
			print(1, "INCREASING CAPACITY...");
			int count = 0;
			for(int j = 0; j < jaeList.length; j++)
			{
				print(0, "\tPOS " + j + " > ");
				if(jaeList[j] != null)
				{
					print(1, jaeList[j] + "(" + jaeList[j].getClass().toString() + ")");
					count++;
				}
				else
				{
					print(1, "null");
				}
			}
			print(1, "objectCounter : " + count);
		}
		// END DEBUG


		// increase capacity.
		synchronized(this.getClass())
		{
			concurrentCapacity -= capacity;
		}
		int originalCapacity = capacity;
		capacity = (int) (capacity * factor);
		synchronized(this.getClass())
		{
			concurrentCapacity += capacity;
		}
		T[] temporaryRef = constructArray(capacity);
		for(int j = 0; j < size; j++)
		{
			temporaryRef[j] = jaeList[tailIndex % (originalCapacity - 1)];
			tailIndex++;
		}
		tailIndex = 0;
		headCursor = size;
		jaeList = temporaryRef;


		// DEBUG
		if(keyword == DEBUG)
		{
			print(1, "CAPACITY INCREASED TO : " + capacity + "\n");
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
				print(1, "\n");
			}
			print(1, "\nSIZE : " + size + " out of " + capacity);
			int count = 0;
			for(int j = 0; j < jaeList.length; j++)
			{
				print(0, "\tPOS " + j + " > ");
				if(jaeList[j] != null)
				{
					print(1, jaeList[j] + "(" + jaeList[j].getClass().toString() + ")");
					count++;
				}
				else
				{
					print(1, "null");
				}
			}
			print(1, "objectCounter : " + count);
			for(int j = 0; j < 10; j++)
			{
				print(1, "\n");
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
			temporaryRef[j] = jaeList[tailIndex++ % originalCapacity];
		}
		tailIndex = 0;
		headCursor = size;
		jaeList = temporaryRef;


		// DEBUG
		if(keyword == DEBUG)
		{
			print(1, "\nSIZE : " + size + " out of " + capacity);
			print(1, "DECREASING CAPACITY...");
			int count = 0;
			for(int j = 0; j < jaeList.length; j++)
			{
				print(0, "\tPOS " + j + " > ");
				if(jaeList[j] != null)
				{
					print(1, jaeList[j] + "(" + jaeList[j].getClass().toString() + ")");
					count++;
				}
				else
				{
					print(1, "null");
				}
			}
			print(1, "objectCounter : " + count);
			print(1, "CAPACITY DECREASED TO : " + capacity + "\n");
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
		synchronized(this.getClass())
		{
			concurrentSize -= size;
			size = 0;
		}
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
			synchronized(this.getClass())
			{
				concurrentSize -= size;
				size = 0;
			}
			for(int j = 0; j < input.length; j++)
			{
				if(input[j] != null)
				{
					jaeList[j] = input[j];
					size++;
				}
			}
			synchronized(this.getClass())
			{
				concurrentSize += size;
			}
			tailIndex = 0;
			headCursor = size;
			return true;
		}
	}

	// /**
	// *	Sets capacity to a minimal value and tailIndex is shifted to array index of zero.
	// *
	// *	@since 1.0.0
	// *	@author Jaewan Yun (Jay50@pitt.edu)
	// */
	// private synchronized void normalize()
	// {
	// 	int originalCapacity = capacity;
	// 	capacity = size + 1;
	// 	T[] temporaryRef = constructArray(capacity);

	// 	for(int j = 0; j < capacity - 1; j++)
	// 	{
	// 		temporaryRef[j] = jaeList[tailIndex++ % (originalCapacity - 1)];
	// 	}
	// 	tailIndex = 0;
	// 	headCursor = size;
	// 	jaeList = temporaryRef;
	// }

	/**
	*	@return A copy of this array.
	*	@throws IllegalStateException when this has not been properly initialized.
	*	@throws NullPointerException when jaeList is null.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	@SuppressWarnings("unchecked") public synchronized T[] toArray()
	{
		checkInitialization();
		int newTailIndex = tailIndex;
		T[] toReturn = (T[]) new Object[size];
		for(int j = 0; j < size; j++)
		{
			toReturn[j] = jaeList[newTailIndex++ % capacity];
		}
		return toReturn;
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

	/**
	*	@return true if data represented is in full state.
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
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
			concurrentObjects--;
			concurrentCapacity -= capacity;
		}
	}

	/**
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	public synchronized String toString()
	{
		return jaeList.toString();
	}

	/**
	*	@param keyword Keyword that the method body portion execution is dependent on
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	public synchronized void showState(Keyword keyword)
	{
		if(keyword == DEBUG || keyword == IDDEBUG)
		{
			print(1, "jaeList Address :\t" + jaeList);
			print(1, "MAX_CAPACITY :\t\t" + MAX_CAPACITY);
			print(1, "DEFAULT_CAPACITY :\t" + DEFAULT_CAPACITY);
			print(1, "EXPANSION_FACTOR :\t" + EXPANSION_FACTOR);
			print(1, "REDUCTION_FACTOR :\t" + REDUCTION_FACTOR);
			print(1, "concurrentObjects :\t" + concurrentObjects);
			print(1, "concurrentCapacity :\t" + concurrentCapacity);
			print(1, "concurrentSize : \t" + concurrentSize);
			print(1, "size :\t\t\t" + size);
			print(1, "capacity :\t\t" + capacity);
			print(1, "initialized :\t\t" + initialized);
			print(1, "headCursor :\t\t" + headCursor);
			print(1, "tailIndex :\t\t" + tailIndex);
			print(1, "\n\tEND OF JaeList EXPLICIT STATE\n");
		}

		if(keyword == IDDEBUG)
		{
			if(jaeList != null)
			{
				print(1, "length :\t\t" + jaeList.length);
				if(jaeList[tailIndex] != null)
					print(1, "tailIndex type :\t" + jaeList[tailIndex].getClass().toString());
				else
					print(1, "tailIndex type :\tnull");
				if(jaeList[headCursor] != null)
					print(1, "headCursor type :\t" + jaeList[tailIndex].getClass().toString());
				else
					print(1, "headCursor type :\tnull");
				if(headCursor - 1 < 0)
					if(jaeList[capacity - 1] != null)
						print(1, "headIndex type :\t" + jaeList[tailIndex].getClass().toString());
				if(headCursor - 1 >= 0)
					if(jaeList[headCursor - 1] != null)
						print(1, "headIndex type :\t" + jaeList[tailIndex].getClass().toString());
				print(1, "\n\tEND OF T[] EXPLICIT STATE\n");

				for(int j = 0; j < jaeList.length; j++)
				{
					print(0, "Index  " + j + ": \t[" + jaeList[j]);
					if(jaeList[j] != null)
						print(1, "\t] of type (" + jaeList[j].getClass().toString() + ")");
					else
						print(0, "\t]\n");
				}
				print(1, "\n\tEND OF T[] ENUMERATION");
			}
			else
			{
				print(2, "jaeList is null therefore unaccessible");
			}
		}
	}

	/**
	*	@since 1.0.0
	*	@author Jaewan Yun (Jay50@pitt.edu)
	*/
	private void print(int skip, String toPrint)
	{
		System.out.print(toPrint);

		if(skip == 0)
		{
			return;
		}

		for(int j = 0; j < skip; j++)
		{
			System.out.print("\n");
		}
	}
}