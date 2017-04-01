package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class PlaybackGearFlap extends PlaybackAsync {

	public PlaybackGearFlap(String filePath, int periodMS) {
		super(filePath, periodMS);
		requires(Robot.mGearFlap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void execute(double val) {
		Robot.mGearFlap.SetFlapState(val >= 0.9 ? true : false);
		SmartDashboard.putNumber("Flap State", val);
	}

	@Override
	protected void end() {
		Robot.mGearFlap.SetFlapState(false);
	}

}
