package org.ozram1922;

public class Vector2d {	
	public double x = 0;
	public double y = 0;
	
	public Vector2d(double X, double Y)
	{
		x = X;
		y = Y;
	}
	
	public Vector2d()
	{
		x = 0;
		y = 0;
	}
	
	public Vector2d ScalarMultiply(double s)
	{
		return new Vector2d(x*s, y*s);
	}
	
	public Vector2d ScalarDivide(double s)
	{
		return new Vector2d(x / s, y / s);
	}
	
	public double Magnitude()
	{
		return Math.sqrt(x*x + y*y);
	}
	
	public Vector2d SubtractFromThis(Vector2d other)
	{
		return new Vector2d(x - other.x, y - other.y);
	}
	
	public Vector2d AddToThis(Vector2d other)
	{
		return new Vector2d(x + other.x, y + other.y);
	}
	
	public double Dot(Vector2d other)
	{
		return x * other.x + y*other.y;
	}
}