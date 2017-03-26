package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Timer;
import java.util.TimerTask;

import org.ozram1922.autonomous.AutoPlayback;

import edu.wpi.first.wpilibj.command.Command;

public abstract class LeftRightPlayback2Async extends Command {

	private class PlaybackTask extends TimerTask
	{
		@Override
		public void run() {
			double left1, right1, left2, right2;
			synchronized(_leftPlayback1)
			{
				left1 = _leftPlayback1.GetSetpoint();
				right1 = _rightPlayback1.GetSetpoint();
				
				left2 = _leftPlayback2.GetSetpoint();
				right2 = _leftPlayback2.GetSetpoint();
			}
			execute(left1, right1, left2, right2);
		}		
	}

	AutoPlayback _leftPlayback1 = new AutoPlayback();
	AutoPlayback _rightPlayback1 = new AutoPlayback();
	AutoPlayback _leftPlayback2 = new AutoPlayback();
	AutoPlayback _rightPlayback2 = new AutoPlayback();
	Timer _timer;
	int _periodMS = 20;
	static String readFile(String path, Charset encoding) 
			  throws IOException 
	{
	  byte[] encoded = Files.readAllBytes(Paths.get(path));
	  return new String(encoded, encoding);
	}
	
    public LeftRightPlayback2Async(String leftFilePath1, String rightFilePath1, String leftFilePath2, String rightFilePath2, int periodMS) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	_periodMS = periodMS;
    	try {
			_leftPlayback1.Deserialize(readFile(leftFilePath1, Charset.defaultCharset()));
			_rightPlayback1.Deserialize(readFile(rightFilePath1, Charset.defaultCharset()));
			_leftPlayback2.Deserialize(readFile(leftFilePath2, Charset.defaultCharset()));
			_rightPlayback2.Deserialize(readFile(rightFilePath2, Charset.defaultCharset()));
		} catch (NumberFormatException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_leftPlayback1.StartPlayback();
    	_rightPlayback1.StartPlayback();
    	_leftPlayback2.StartPlayback();
    	_rightPlayback2.StartPlayback();
    	initializeInternal();
    	_timer = new Timer();
    	_timer.schedule(new PlaybackTask(), _periodMS);
    }
    
    protected abstract void initializeInternal();

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }
    
    protected abstract void execute(double left1, double right1, 
    								double left2, double right2);

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
    	boolean result = false;
    	synchronized(_leftPlayback1)
    	{
    		result = _leftPlayback1.IsFinished();
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

