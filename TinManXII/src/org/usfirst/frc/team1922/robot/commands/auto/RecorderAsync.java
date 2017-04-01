package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.File;
import java.io.PrintWriter;
import java.util.Timer;
import java.util.TimerTask;

import org.ozram1922.autonomous.AutoRecorder;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public abstract class RecorderAsync extends Command {


	int _periodMS = 15;
	Timer _worker = new Timer();
	private class WorkerTask extends TimerTask
	{
		@Override
		public void run() {
			double val = Get();
			synchronized(_recorder)
			{
		    	_recorder.Update(val);
			}
		}		
	}
	
	AutoRecorder _recorder = new AutoRecorder();
	String _subDir;
	static String basePath = "/home/lvuser/TinManRecordings/";

    public RecorderAsync(String subDir, int periodMS) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	//_entry = (NetworkTable) NetworkTable.getTable("T1922").getSubTable(tableName);
    	_subDir = subDir;
    	_periodMS = periodMS;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_recorder.StartRecording();

    	new File(basePath).mkdirs();
    	new File(basePath + _subDir).mkdirs();
    	
    	_worker.schedule(new WorkerTask(), 0, _periodMS);
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }
    
    protected abstract double Get();
    

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
    
    protected String GetFileName(int number, String modifier)
    {
    	return basePath + _subDir + "/" + number + modifier + ".csv";
    }

    // Called once after isFinished returns true
    protected void end() {
    	_worker.cancel();
    	int entryNumber = -1;
    	//while(_entry.containsKey(leftEntryNumber + "L")) leftEntryNumber++;
    	//while(_entry.containsKey(rightEntryNumber + "R")) rightEntryNumber++;
    	//_entry.putString(leftEntryNumber + "L",_leftRecorder.Serialize());
    	//_entry.putString(rightEntryNumber + "R",_rightRecorder.Serialize());
    	
    	//create the file structure if it doesn't exist
    	
    	//get the file name
    	File f;
    	do
    	{
    		entryNumber++;
    		f = new File(GetFileName(entryNumber, ""));
    	}
    	while(f.exists() && !f.isDirectory());
    	
    	
    	//output the left information
    	try(PrintWriter out = new PrintWriter(GetFileName(entryNumber, "")))
    	{
    	    out.println(_recorder.Serialize());
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
