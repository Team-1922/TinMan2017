package org.ozram1922.image;

/**
 * A single block of data as outlined on the PixyCam website
 *  
 * @author Kevin Mackenzie
 *
 */
public class PixyCamBlock
{
	public int Signature;
	public int X;
	public int Y;
	public int Width;
	public int Height;
	public int Angle;
	
	public static final int WordCount = 6;
	
	/**
	 * Gets a copy of this data
	 */
	public PixyCamBlock clone()
	{
		PixyCamBlock ret = new PixyCamBlock();
		ret.Signature = Signature;
		ret.X = X;
		ret.Y = Y;
		ret.Width = Width;
		ret.Height = Height;
		ret.Angle = Angle;
		return ret;
	}
	
	/**
	 * 
	 * @return the width*height area
	 */
	public int GetAABBArea()
	{
		return Width*Height;
	}
}