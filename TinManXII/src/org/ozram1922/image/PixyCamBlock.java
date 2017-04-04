package org.ozram1922.image;

import javax.print.attribute.standard.MediaSize.Other;

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
	
	private boolean ContainsPoint(int x, int y)
	{
		return (x > X && x < X + Width) && 
				(y > Y && Y < Y + Height);
	}
	
	public boolean ContainsOther(PixyCamBlock other)
	{
		return ContainsPoint(other.X, other.Y) ||
				ContainsPoint(other.X, other.Y + Height) ||
				ContainsPoint(other.X + Width, other.Y) ||
				ContainsPoint(other.X + Width, other.Y + Height);
	}
	
	public void ExpandToFit(PixyCamBlock other)
	{
		int thisBottom = Y + Height;
		int thisRight = X + Width;
		int otherBottom = other.Y + other.Height;
		int otherRight = other.X + other.Width;
		
		int right = Math.max(thisRight, otherRight);
		int bottom = Math.max(thisBottom, otherBottom);
		
		X = Math.min(X, other.X);
		Y = Math.min(Y, other.Y);
		
		Width = X - right;
		Height = Y - bottom;		
	}
}