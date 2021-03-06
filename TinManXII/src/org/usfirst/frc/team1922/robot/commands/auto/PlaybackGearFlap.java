package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

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

		if(!Robot.mGearFlap.IsPegDetected())
			if(Robot.mGearFlap.GetFlapState() == false && val >= 0.9)
				return;
		Robot.mGearFlap.SetFlapState(val >= 0.9 ? true : false);
		SmartDashboard.putNumber("Flap State", val);
	}

	@Override
	protected void end() {
		Robot.mGearFlap.SetFlapState(false);
	}

}
