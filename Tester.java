import java.util.*;
import constant.Keyword;
import static constant.Keyword.*;

public class Tester
{
	public static void main(String[] args)
	{
		JaeList<Object> list = new JaeList<Object>();
		final int size = 100;

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
			list.add(alist[j]);
		}

		for(int j = 0; j < size; j++)
		{
			list.remove(DEBUG);
		}

		for(int j = 0; j < size; j++)
		{
			list.add(alist[j], DEBUG);
		}
	}
}