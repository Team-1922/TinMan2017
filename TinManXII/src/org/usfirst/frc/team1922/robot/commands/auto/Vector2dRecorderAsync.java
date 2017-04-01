package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import org.ozram1922.Vector2d;
import org.ozram1922.autonomous.AutoRecorder;
import org.ozram1922.autonomous.VectorAutoRecorder;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public abstract class Vector2dRecorderAsync extends Command {

	int _periodMS = 15;
	Timer _worker = new Timer();
	private class WorkerTask extends TimerTask
	{
		@Override
		public void run() {
			Vector2d val = Get();
			synchronized(_autoRecorder)
			{
		    	_autoRecorder.Update(val);
			}
		}		
	}
	
	
	VectorAutoRecorder _autoRecorder = new VectorAutoRecorder();
	String _subDir;
	static String basePath = "/home/lvuser/TinManRecordings/";

    public Vector2dRecorderAsync(String subDir, int periodMS) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	//_entry = (NetworkTable) NetworkTable.getTable("T1922").getSubTable(tableName);
    	_subDir = subDir;
    	_periodMS = periodMS;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_autoRecorder.StartRecording();

    	new File(basePath).mkdirs();
    	new File(basePath + _subDir).mkdirs();
    	
    	_worker.schedule(new WorkerTask(), 0, _periodMS);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }
    
    protected abstract Vector2d Get();
    

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
    
    protected String GetFileName(int number)
    {
    	return basePath + _subDir + "/" + number + ".csv";
    }

    // Called once after isFinished returns true
    protected void end() {
    	_worker.cancel();
    	
    	int leftEntryNumber = -1;
    	int rightEntryNumber = -1;
    	//while(_entry.containsKey(leftEntryNumber + "L")) leftEntryNumber++;
    	//while(_entry.containsKey(rightEntryNumber + "R")) rightEntryNumber++;
    	//_entry.putString(leftEntryNumber + "L",_leftRecorder.Serialize());
    	//_entry.putString(rightEntryNumber + "R",_rightRecorder.Serialize());
    	
    	//create the file structure if it doesn't exist
    	
    	
    	//get the file name
    	File f;
    	do
    	{
    		rightEntryNumber++;
    		f = new File(GetFileName(rightEntryNumber));
    	}
    	while(f.exists() && !f.isDirectory());
    	
    	
    	//output the information
    	try(PrintWriter out = new PrintWriter(GetFileName(leftEntryNumber)))
    	{
    	    out.println(_autoRecorder.Serialize());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
