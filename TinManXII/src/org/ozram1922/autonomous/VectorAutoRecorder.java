package org.ozram1922.autonomous;

import java.util.ArrayList;

import org.ozram1922.Vector2d;

public class VectorAutoRecorder extends AutoRecorder {

	public VectorAutoRecorder()
	{
		super(2);
	}
	
	public void Update(Vector2d value)
	{
		ArrayList<Double> arrayValue = new ArrayList<Double>(2);
		arrayValue.set(0, value.x);
		arrayValue.set(1, value.y);
		Update(arrayValue);
	}
}
