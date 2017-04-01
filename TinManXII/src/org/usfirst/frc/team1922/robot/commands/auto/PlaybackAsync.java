package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import org.ozram1922.Vector2d;
import org.ozram1922.autonomous.AutoPlayback;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public abstract class PlaybackAsync extends Command {


	int _periodMS = 15;
	Timer _worker = new Timer();
	private class WorkerTask extends TimerTask
	{
		@Override
		public void run() {
			double setpoint;
			synchronized(_playback)
			{
		    	setpoint = _playback.GetSetpoint().get(0);
			}
			execute(setpoint);
		}		
	}
	
	AutoPlayback _playback = new AutoPlayback();
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
    public PlaybackAsync(String filePath, int periodMS) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	try {
			_playback.Deserialize(readFile(filePath, Charset.defaultCharset()));
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	_periodMS = periodMS;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_playback.StartPlayback();
    	_worker.schedule(new WorkerTask(), 0, _periodMS);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    protected abstract void execute(double val);
    
    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean ret;
    	synchronized(_playback)
    	{
    		ret = _playback.IsFinished();
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
