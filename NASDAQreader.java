import org.apache.commons.io.IOUtils;
import java.util.*;
import java.io.*;
import java.net.*;

public class NASDAQreader implements Runnable
{
	private String address = "http://www.nasdaq.com/markets/indices/sector-indices.aspx";
	private JayList<String> unparsed;
	private Parser parser;

	private class Parser
	{
		private JayList<String[]> parse(JayList<String> unparsed)
		{
			JayList<String[]> parsed = new JayList<String[]>();

			for(int j = 0, k = unparsed.size(); j < k; j++)
			{
				String read = unparsed.removeLast();
				int l = j % 11;

				switch(l)
				{
					case 0:
						unparsed.addFirst(read);
					case 1:
					case 2:
					case 3:
						break;
					case 4:
					case 5:
					case 6:
					case 7:
						unparsed.addFirst(read);
					case 8:
					case 9:
					case 10:
						break;
				}
			}

			for(int j = 0, k = unparsed.size(); j < k; j++)
			{
				System.out.println(unparsed.removeLast());
			}

			return parsed;
		}
	}

	public NASDAQreader()
	{
		unparsed = new JayList<String>();
		parser = new Parser();
	}

	@SuppressWarnings("unchecked") public void run()
	{
		read();
	}
	private void read()
	{
		try
		{
			URL url = new URL(address);
			URLConnection con = url.openConnection();
			InputStream in = con.getInputStream();
			String encoding = con.getContentEncoding();
			encoding = encoding == null ? "UTF-8" : encoding;
			String body = IOUtils.toString(in, encoding);

			boolean read = false;
			int lineCounter = 0;

			String[] temp = body.split("</tr>");
			for(int j = 0; j < temp.length; j++)
			{
				for(String temp2 : temp[j].split("\n"))
				{
					if(temp2.contains("<a href=\"http://www.nasdaq.com/aspx/infoquotes.aspx?symbol="))
					{
						read = true;
						lineCounter = 0;
					}

					if(read == true && lineCounter < 8)
					{
						unparsed.addFirst(temp2);
					}
					lineCounter++;
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}


		parser.parse(unparsed);
	}
}