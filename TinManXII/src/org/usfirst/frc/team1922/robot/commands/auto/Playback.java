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
public abstract class Playback extends Command {

	AutoPlayback _playback = new AutoPlayback();
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
    public Playback(String filePath) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	requires(Robot.mDriveTrain);
    	try {
			_playback.Deserialize(readFile(filePath, Charset.defaultCharset()));
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_playback.StartPlayback();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	execute(_playback.GetSetpoint());
    }

    protected abstract void execute(double val);
    
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return _playback.IsFinished();
    }

    // Called once after isFinished returns true
    protected abstract void end();

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
