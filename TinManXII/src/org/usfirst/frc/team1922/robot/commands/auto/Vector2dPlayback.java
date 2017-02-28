package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.ozram1922.OzUtils;
import org.ozram1922.Vector2d;
import org.ozram1922.autonomous.AutoPlayback;
import org.ozram1922.autonomous.VectorAutoPlayback;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public abstract class Vector2dPlayback extends Command {

	VectorAutoPlayback _autoPlayback = new VectorAutoPlayback();

	
    public Vector2dPlayback(String filePath) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	try {
    		_autoPlayback.Deserialize(OzUtils.readFile(filePath, Charset.defaultCharset()));
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_autoPlayback.StartPlayback();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	execute(_autoPlayback.GetVectorSetpoint());
    }
    
    protected abstract void execute(Vector2d value);

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return _autoPlayback.IsFinished();
    }

    // Called once after isFinished returns true
    protected abstract void end();

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
