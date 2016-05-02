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
	public static void main(String[] args) throws Exception
	{
		Thread reader = new Thread(new NASDAQreader());
		reader.start();
		reader.join();


	}
}
