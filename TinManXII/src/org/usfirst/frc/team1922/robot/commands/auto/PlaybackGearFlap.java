package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PlaybackGearFlap extends Playback {

	public PlaybackGearFlap(String filePath) {
		super(filePath);
		requires(Robot.mGearFlap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void execute(double val) {
		Robot.mGearFlap.SetFlapState(val >= 1 ? true : false);
		
	}

	@Override
	protected void end() {
		Robot.mGearFlap.SetFlapState(false);
	}

}
