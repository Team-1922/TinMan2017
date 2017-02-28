package org.ozram1922;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

public class OzUtils {
	public static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
	public static String readFile(String path)
			throws IOException
	{
		return readFile(path, Charset.defaultCharset());
	}
	
	public static double GetTime()
	{
		return (double)System.nanoTime() / 1000000000.0;
	}
}
