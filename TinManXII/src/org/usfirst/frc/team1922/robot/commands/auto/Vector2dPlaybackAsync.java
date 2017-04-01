package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import org.ozram1922.OzUtils;
import org.ozram1922.Vector2d;
import org.ozram1922.autonomous.AutoPlayback;
import org.ozram1922.autonomous.VectorAutoPlayback;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public abstract class Vector2dPlaybackAsync extends Command {


	int _periodMS = 15;
	Timer _worker = new Timer();
	private class WorkerTask extends TimerTask
	{
		@Override
		public void run() {
			Vector2d setpoint;
			synchronized(_autoPlayback)
			{
		    	setpoint = _autoPlayback.GetVectorSetpoint();
			}
			execute(setpoint);
		}		
	}
	
	
	VectorAutoPlayback _autoPlayback = new VectorAutoPlayback();

	
    public Vector2dPlaybackAsync(String filePath, int periodMS) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	try {
    		_autoPlayback.Deserialize(OzUtils.readFile(filePath, Charset.defaultCharset()));
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	_periodMS = periodMS;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_autoPlayback.StartPlayback();
    	_worker.schedule(new WorkerTask(), 0, _periodMS);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }
    
    protected abstract void execute(Vector2d value);

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean ret;
    	
    	synchronized(_autoPlayback)
    	{
        	ret = _autoPlayback.IsFinished();
    	}
    	return ret;
    }

    // Called once after isFinished returns true
    protected void end()
    {
    	_worker.cancel();
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
