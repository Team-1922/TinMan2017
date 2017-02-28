package org.ozram1922;

import java.util.ArrayList;

public class OzMath {

	public static double LRP(double val1, double val2, double mix)
	{
		return val1*(1-mix) + val2*mix;
	}
	
	public static double LRP(double mix, double key1, double key2, double val1, double val2)
	{
		double normA = (key1 - mix) / (key2-key1);
		return LRP(val1, val2, normA);
	}

	public static ArrayList<Double> LRP(double mix, double key1, double key2, ArrayList<Double> val1, ArrayList<Double> val2)
	{
		int val1Size = val1.size();
		int val2Size = val2.size();
		int size = val1Size > val2Size ? val2Size : val1Size;
		
		ArrayList<Double> ret = new ArrayList<Double>();
		for(int i = 0; i < size; ++i)
		{
			ret.add(LRP(mix, key1, key2, val1.get(i), val2.get(i)));
		}
		
		return ret;
	}
	
	public static double Clamp(double x, double min, double max)
	{
		return Math.min(Math.max(min, x), max);
	}
}
