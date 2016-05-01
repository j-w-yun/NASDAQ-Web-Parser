import java.util.*;
import constant.*;
import static constant.Keyword.*;

public class Tester
{
	public static void main(String[] args)
	{
		JayList<Object> list = new JayList<Object>();
		final int size = 100;//1031;

		Object[] a1 = new Integer[size];
		Object[] a2 = new Double[size];
		Object[] a3 = new Float[size];
		Object[] alist = new Object[size];

		int counter = 0;

		for(int j = 0; j < size; j++)
		{
			counter %= 3;
			counter++;

			a1[j] = new Integer(j+1);
			a2[j] = new Double(j+1);
			a3[j] = new Float(j+1);

			if(counter == 3)
			{
				alist[j] = a3[j];
			}
			else if(counter == 2)
			{
				alist[j] = a2[j];
			}
			else
			{
				alist[j] = a1[j];
			}
		}

		for(int j = 0; j < size; j++)
		{
			System.out.println(list.push(alist[j]));//, DEBUG);
		}
		list.showState(DEBUG);

		for(int j = 0; j < size; j++)
		{
			System.out.println(list.pop());
		}
		list.showState(DEBUG);

		// // list.remove();

		// for(int j = 0; j < 100; j++)
		// {
		// 	list.remove();//DEBUG);
		// }
		// list.showState(DEBUG);

		// for(int j = 0; j < 100000; j++)
		// {
		// 	list.element();
		// }
		// list.showState(DEBUG);

		// // for(int j = 0; j < 2; j++)
		// // {
		// // 	list.add(alist[j]);
		// // }
		// // list.showState(IDDEBUG);

		// // for(int j = 0; j < 1; j++)
		// // {
		// // 	list.remove();
		// // }
		// // list.showState(IDDEBUG);

		// // Object[] a4 = list.toArray();
		// // System.out.print("\nTOARRAY: [");
		// // for(int j = 0; j < a4.length; j++)
		// // {
		// // 	if(j == a4.length - 1)
		// // 	{
		// // 		System.out.print(a4[j]);
		// // 		break;
		// // 	}
		// // 	System.out.print(a4[j] + ", ");
		// // }
		// // System.out.println("]");
		// // System.out.println("ARRAY SIZE : " + a4.length + "\n");
		// // list.showState(DEBUG);
	}
}