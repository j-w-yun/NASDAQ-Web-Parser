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

public class WebReader
{
	public static void main(String[] args)
	{
		JayList<String> jayList = new JayList<String>();
		String a = "www.nasdaq.com/markets/indices/sector-indices.aspx";
		// String a = "www.nasdaq.com/markets/indices/major-indices.aspx";

		try
		{
			URL url = new URL("http://" + a + "/");
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);

			boolean read = false;
			int counter = 0;
			int line = 0;

			String[] temp = body.split("</tr>");

			for(int j = 0; j < temp.length; j++)
			{
				for(String temp2 : temp[j].split("\n"))
				{
					if(temp2.contains("<a href=\"http://www.nasdaq.com/aspx/infoquotes.aspx?symbol="))
					{
						read = true;
						counter = 0;
					}

					if(read == true && counter < 8)
					{
						jayList.addFirst(temp2);
					}

					counter++;
					line++;
				}
			}

			// System.out.println(body);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}

		for(int j = 0, k = jayList.size(); j < k; j++)
		{
				System.out.println(jayList.removeLast());
		}
	}
}
