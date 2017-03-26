package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import org.ozram1922.autonomous.AutoPlayback;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class PlayDTVoltageRecording extends LeftRightPlaybackAsync {
	
    public PlayDTVoltageRecording(String leftFilePath, String rightFilePath) {
    	super(leftFilePath, rightFilePath, 15);
    	requires(Robot.mDriveTrain);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute(double left, double right) {
		double pdpVoltage = Robot.mPDP.getVoltage();
    	Robot.mDriveTrain.TankControl(left / pdpVoltage, right / pdpVoltage);
		//Robot.mDriveTrain.TankControl(.5,.5);
    }

    // Called once after isFinished returns true
    protected void end(boolean initiate) {
    	Robot.mDriveTrain.TankControl(0, 0);
    }
}
