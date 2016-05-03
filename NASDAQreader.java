import org.apache.commons.io.IOUtils;
import java.util.*;
import java.util.concurrent.*;
import java.io.*;
import java.net.*;

public class NASDAQreader implements Callable
{
	private String address = "http://www.nasdaq.com/markets/indices/sector-indices.aspx";
	private JayList<String> unparsed;
	private Parser parser;

	private class Parser
	{
		private JayList<String[]> parsed;

		private Parser()
		{
			parsed = new JayList<String[]>();
		}

		private JayList<String[]> parse(JayList<String> unparsed)
		{
			for(int j = 0, k = unparsed.length(); j < k; j++)
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

			String temp = null;
			JayList<String> indexAttrib = new JayList<String>();

			while(!unparsed.isEmpty())
			{

				// read symbol.
				temp = unparsed.removeLast();
				indexAttrib.addLast(read(temp, "?symbol=", "&amp"));

				// read index value.
				temp = unparsed.removeLast();
				indexAttrib.addLast(read(temp, "<td>", "</td>"));

				// read change net.
				temp = unparsed.removeLast();
				indexAttrib.addLast(read(temp, "class=", ">"));	// up/down net
				indexAttrib.addLast(read(temp, "\">", "&nbsp"));	// net value
				indexAttrib.addLast(read(temp, ";&nbsp;", "%</td>"));	// percent

				// read high.
				temp = unparsed.removeLast();
				indexAttrib.addLast(read(temp, "<td>", "</td>"));

				// read low.
				temp = unparsed.removeLast();
				indexAttrib.addLast(read(temp, "<td>", "</td>"));

				int index = 0;
				String[] toStore = new String[7];
				while(!indexAttrib.isEmpty())
				{
					for(int j = 0; j < 7; j++)
					{
						// System.out.println("HERE");
						toStore[j] = indexAttrib.removeFirst();
					}
					parsed.addLast(toStore);
				}
			}

			return parsed;
		}

		private String read(String toRead, String readAfter, String readUntil)
		{
			if(!toRead.contains(readAfter) && !toRead.contains(readUntil))
			{
				throw new IllegalArgumentException();
			}

			StringBuilder sb = new StringBuilder();
			int charCounter = toRead.indexOf(readAfter);
			charCounter += readAfter.length() - 1;

			if(readUntil.length() == 1)
			{
				while(toRead.charAt(++charCounter) != readUntil.charAt(0))
				{
					sb.append(toRead.charAt(charCounter));
				}
			}
			else
			{
				while(toRead.charAt(++charCounter) != readUntil.charAt(0) && toRead.charAt(charCounter + 1) != readUntil.charAt(1))
				{
					sb.append(toRead.charAt(charCounter));
				}
			}

			return sb.toString();
		}
	}

	public NASDAQreader()
	{
		unparsed = new JayList<String>();
		parser = new Parser();
	}

	public JayList<String[]> call()
	{
		return read();
	}

	private JayList<String[]> read()
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
						System.out.println(temp2);
						unparsed.addFirst(temp2);
					}
					lineCounter++;
				}
			}
		}
		catch(Exception e) {e.printStackTrace();}

		return parser.parse(unparsed);
	}
}