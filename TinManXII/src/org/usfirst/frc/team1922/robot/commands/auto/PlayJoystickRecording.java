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
public class PlayJoystickRecording extends LeftRightPlayback {

    public PlayJoystickRecording(String leftFilePath, String rightFilePath) {
    	super(leftFilePath, rightFilePath);
    	requires(Robot.mDriveTrain);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute(double left, double right) {
    	Robot.mDriveTrain.TankControl(left, right);
    	
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.mDriveTrain.TankControl(0, 0);
    }
}
