package org.usfirst.frc.team1922.robot.commands.auto;

import org.ozram1922.Vector2d;

public abstract class LeftRightRecorder extends Vector2dRecorder {

	public LeftRightRecorder(String subDir)
	{
		super(subDir);
	}

	@Override
	protected Vector2d Get() {
		return new Vector2d(GetLeft(), GetRight());
	}
	
	protected abstract double GetLeft();
	protected abstract double GetRight();
}
