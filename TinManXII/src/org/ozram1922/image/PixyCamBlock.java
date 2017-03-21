package org.ozram1922.image;

public class PixyCamBlock
{
	public int Signature;
	public int X;
	public int Y;
	public int Width;
	public int Height;
	public int Angle;
	
	public static final int WordCount = 6;
	
	public synchronized PixyCamBlock clone()
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
}