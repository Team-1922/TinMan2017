package org.usfirst.frc.team1922.robot.commands.auto;

import org.ozram1922.autonomous.AutoRecorder;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public class RecordJoystickInput extends LeftRightRecorder {

	public RecordJoystickInput(String subDir) {
		super(subDir);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double GetLeft() {
		return Robot.oi.GetLeftJoystick().getY();
	}

	@Override
	protected double GetRight() {
		return Robot.oi.GetRightJoystick().getY();
	}
	

}
