import java.util.*;
import constant.*;
import static constant.Keyword.*;

public class Tester
{
	public static void main(String[] args)
	{
		JayList<Object> list = new JayList<Object>();
		final int size = 1000;//1031;

		Object[] a1 = new Integer[size];
		Object[] a2 = new Double[size];
		Object[] a3 = new Float[size];
		Object[] alist = new Object[size];

		int counter = 0;

		for(int j = 0; j < size; j++)
		{
			a1[j] = new Integer(j);
			a2[j] = new Double(j);
			a3[j] = new Float(j);

			// if(counter == 2)
			// {
			// 	alist[j] = a3[j];
			// 	counter++;
			// }
			// else if(counter == 1)
			// {
			// 	alist[j] = a2[j];
			// 	counter++;
			// }
			// else
			// {
				alist[j] = a1[j];
			// }
			// counter = ++counter % 3;
		}

		System.out.println("\n\n\t\t\tADD LAST");
		for(int j = 0; j < 10; j++)
		{
			System.out.print(list.addLast(alist[j]));//, DEBUG);
			list.showState(IDDEBUG);
		}

		System.out.println("\n\n\t\t\tADD POSITION");
		for(int j = 0; j < 10; j++)
		{
			System.out.println(list.add(alist[100 + j], 10, IDDEBUG));
			list.showState(IDDEBUG);
		}

		System.out.println("\n\n\t\t\tREMOVE POSITION");
		for(int j = 0; j < 10; j++)
		{
			System.out.println(list.remove(5, IDDEBUG));
			list.showState(IDDEBUG);
		}
	}
}