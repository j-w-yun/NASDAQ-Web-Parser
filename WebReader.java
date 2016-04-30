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
		String a = "www.nasdaq.com/markets/indices/sector-indices.aspx";
		// String a = "www.nasdaq.com/markets/indices/major-indices.aspx";

		if(args.length != 0)
		{
			Scanner sc = new Scanner(System.in);
			System.out.print("Enter an address (e.g. www.google.com) > ");
			args = new String[1];
			args[0] = sc.nextLine();
		}

		try
		{
			URL url = new URL("http://" + args[0] + "/");
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);

			boolean read = false;
			int counter = 0;
			int line = 0;

			String[] temp = body.split("</tr>");

			// DEBUG
			System.out.println("LENGTH OF FIRST CUT > " + temp.length);

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
						System.out.println(temp2);
					}

					counter++;
					line++;
				}
			}

			// DEBUG
			System.out.println("NUMBER OF LINES > " + line);
			// System.out.println(body);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
