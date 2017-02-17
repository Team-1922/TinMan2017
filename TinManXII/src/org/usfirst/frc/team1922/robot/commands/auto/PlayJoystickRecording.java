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
public class PlayJoystickRecording extends Command {

	AutoPlayback _leftPlayback = new AutoPlayback();
	AutoPlayback _rightPlayback = new AutoPlayback();
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
    public PlayJoystickRecording() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.mDriveTrain);
    	try {
			_leftPlayback.Deserialize(readFile("LeftRecording.csv", Charset.defaultCharset()));
			_rightPlayback.Deserialize(readFile("RightRecording.csv", Charset.defaultCharset()));
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_leftPlayback.StartPlayback();
    	_rightPlayback.StartPlayback();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	Robot.mDriveTrain.TankControl(_leftPlayback.GetSetpoint(), _rightPlayback.GetSetpoint());
    	
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return _leftPlayback.IsFinished();
    }

    // Called once after isFinished returns true
    protected void end() {
    	Robot.mDriveTrain.TankControl(0, 0);
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
