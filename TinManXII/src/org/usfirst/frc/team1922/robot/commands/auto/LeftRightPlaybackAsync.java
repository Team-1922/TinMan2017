package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import org.ozram1922.autonomous.AutoPlayback;

import edu.wpi.first.wpilibj.command.Command;

public abstract class LeftRightPlaybackAsync extends Command {

	private class PlaybackTask extends TimerTask
	{
		@Override
		public void run() {
			double left, right;
			left = 0.5;
			right = 0.5;

			try
			{
				_lock.acquire();
				left = _leftPlayback.GetSetpoint();
				right = _rightPlayback.GetSetpoint();
				
				if(Double.isNaN(left))
					left = 0.5;
				if(Double.isNaN(right))
					right = 0.5;
			}
			catch(Exception e){
				
			}
			finally
			{
				_lock.release();
			}
			execute(left, right);
		}		
	}
	
	Semaphore _lock = new Semaphore(1);
	
	AutoPlayback _leftPlayback = new AutoPlayback();
	AutoPlayback _rightPlayback = new AutoPlayback();
	Timer _timer;
	int _periodMS = 20;
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
    public LeftRightPlaybackAsync(String leftFilePath, String rightFilePath, int periodMS) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	_periodMS = periodMS;
    	try {
			_leftPlayback.Deserialize(readFile(leftFilePath, Charset.defaultCharset()));
			_rightPlayback.Deserialize(readFile(rightFilePath, Charset.defaultCharset()));
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_leftPlayback.StartPlayback();
    	_rightPlayback.StartPlayback();
    	_timer = new Timer();
    	_timer.schedule(new PlaybackTask(), _periodMS);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }
    
    protected abstract void execute(double left, double right);

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean result = false;
    	try
    	{
    		_lock.acquire();
    		result = _leftPlayback.IsFinished();
    	}
    	catch(Exception e)
    	{
    		
    	}
    	finally
    	{
    		_lock.release();
    	}
        return result;
    }
    
    protected void end()
    {
    	_timer.cancel();
    	end(true);
    }

    // Called once after isFinished returns true
    protected abstract void end(boolean ignore);

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}

