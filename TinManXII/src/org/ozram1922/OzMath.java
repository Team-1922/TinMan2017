package org.ozram1922;

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
	
	public static double Clamp(double x, double min, double max)
	{
		return Math.min(Math.max(min, x), max);
	}
}
