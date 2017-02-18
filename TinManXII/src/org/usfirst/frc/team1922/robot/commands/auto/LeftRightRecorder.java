package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

import org.ozram1922.autonomous.AutoRecorder;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public abstract class LeftRightRecorder extends Command {

	AutoRecorder _leftRecorder = new AutoRecorder();
	AutoRecorder _rightRecorder = new AutoRecorder();
	String subDir;
	static String basePath = "/home/lvuser/TinManRecordings/";

    public LeftRightRecorder(String tableName) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	//_entry = (NetworkTable) NetworkTable.getTable("T1922").getSubTable(tableName);
    	subDir = tableName;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_leftRecorder.StartRecording();
    	_rightRecorder.StartRecording();

    	new File(basePath).mkdirs();
    	new File(basePath + subDir).mkdirs();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	_leftRecorder.Update(GetLeft());
    	_rightRecorder.Update(GetRight());
    }
    
    protected abstract double GetLeft();
    protected abstract double GetRight();
    

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }
    
    protected String GetFileName(int number, String modifier)
    {
    	return basePath + subDir + "/" + number + modifier + ".csv";
    }

    // Called once after isFinished returns true
    protected void end() {
    	int leftEntryNumber = -1;
    	int rightEntryNumber = -1;
    	//while(_entry.containsKey(leftEntryNumber + "L")) leftEntryNumber++;
    	//while(_entry.containsKey(rightEntryNumber + "R")) rightEntryNumber++;
    	//_entry.putString(leftEntryNumber + "L",_leftRecorder.Serialize());
    	//_entry.putString(rightEntryNumber + "R",_rightRecorder.Serialize());
    	
    	//create the file structure if it doesn't exist
    	
    	//get the left file name
    	File f;
    	do
    	{
    		leftEntryNumber++;
    		f = new File(GetFileName(leftEntryNumber, "L"));
    	}
    	while(f.exists() && !f.isDirectory());
    	
    	//get the right file name
    	do
    	{
    		rightEntryNumber++;
    		f = new File(GetFileName(rightEntryNumber, "R"));
    	}
    	while(f.exists() && !f.isDirectory());
    	
    	
    	//output the left information
    	try(PrintWriter out = new PrintWriter(GetFileName(leftEntryNumber, "L")))
    	{
    	    out.println(_leftRecorder.Serialize());
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	//output the right information
    	try(PrintWriter out = new PrintWriter(GetFileName(rightEntryNumber, "R")))
    	{
    	    out.println(_rightRecorder.Serialize());
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
