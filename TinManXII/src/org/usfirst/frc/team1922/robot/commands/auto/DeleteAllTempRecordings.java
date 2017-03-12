package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.File;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class DeleteAllTempRecordings extends Command {

	private String _basePath;
    public DeleteAllTempRecordings(String basePath) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	_basePath = basePath;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	try
    	{
	    	File dir = new File(_basePath);
	    	for(File file : dir.listFiles())
	    		if(!file.isDirectory())
	    			file.delete();
    	}
    	catch (Exception e)
    	{
    		e.printStackTrace();
    	}
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return true;
    }

    // Called once after isFinished returns true
    protected void end() {
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
