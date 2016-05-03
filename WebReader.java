/**
*	Extracting stock market indices from nasdaq.
*
*	@author Jaewan Yun (Jay50@pitt.edu)
*	@version 1.0.0
*/

import org.apache.commons.io.IOUtils;
import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class WebReader
{
	public static void main(String[] args) throws Exception
	{
		ExecutorService service = Executors.newFixedThreadPool(1);
		@SuppressWarnings("unchecked")
		Future<JayList<String[]>> result = service.submit(new NASDAQreader());
		service.shutdown();
		JayList<String[]> parsed = result.get();

		while(!parsed.isEmpty())
		{
			String[] temp = parsed.removeFirst();
			for(int j = 0; j < temp.length; j++)
			{
				System.out.print(temp[j] + " ");
			}
			System.out.println();
		}
	}
}
