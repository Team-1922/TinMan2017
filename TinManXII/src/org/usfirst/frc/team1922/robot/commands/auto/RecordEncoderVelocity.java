package org.usfirst.frc.team1922.robot.commands.auto;

import org.ozram1922.autonomous.AutoRecorder;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public class RecordEncoderVelocity extends LeftRightRecorder {

	public RecordEncoderVelocity(String subDir, int periodMS) {
		super(subDir, periodMS);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double GetLeft() {
		return Robot.mDriveTrain.GetLeftVelocity();
	}

	@Override
	protected double GetRight() {
		return Robot.mDriveTrain.GetRightVelocity();
	}
}
