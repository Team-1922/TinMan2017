package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class RecordGearFlap extends Recorder {

	public RecordGearFlap(String subDir) {
		super(subDir);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double GetValue() {
		return Robot.mGearFlap.GetFlapState() ? 1.0 : 0.0;
	}


}
