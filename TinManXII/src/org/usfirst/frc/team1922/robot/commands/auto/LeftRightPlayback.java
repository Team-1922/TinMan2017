package org.usfirst.frc.team1922.robot.commands.auto;

import org.ozram1922.Vector2d;

public abstract class LeftRightPlayback extends Vector2dPlaybackAsync {

	public LeftRightPlayback(String filePath, int periodMS) {
		super(filePath, periodMS);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void execute(Vector2d value) {
		// TODO Auto-generated method stub
		execute(value.x, value.y);
	}
	
	protected abstract void execute(double left, double right);
}
