package org.usfirst.frc.team1922.robot.commands.auto;

import org.ozram1922.autonomous.AutoRecorder;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public class RecordEncoderPosition extends LeftRightRecorder {

	public RecordEncoderPosition(String subDir) {
		super(subDir);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double GetLeft() {
		return Robot.mDriveTrain.GetLeftPosition();
	}

	@Override
	protected double GetRight() {
		return Robot.mDriveTrain.GetRightPosition();
	}
}
