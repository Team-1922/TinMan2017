package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.ozram1922.autonomous.AutoPlayback;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PlayDTVoltageRecording extends LeftRightPlayback {

    public PlayDTVoltageRecording(String filePath) {
    	super(filePath);
    	requires(Robot.mDriveTrain);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute(double left, double right) {
		double pdpVoltage = Robot.mPDP.getVoltage();
    	Robot.mDriveTrain.TankControl(left / pdpVoltage, right / pdpVoltage);
    	
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.mDriveTrain.TankControl(0, 0);
    }
}
