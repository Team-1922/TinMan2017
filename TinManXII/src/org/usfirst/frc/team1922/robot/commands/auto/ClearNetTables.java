package org.usfirst.frc.team1922.robot.commands.auto;

import java.util.Set;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public class ClearNetTables extends Command {

    public ClearNetTables() {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    }

    public static void RecursiveTableDelete(NetworkTable root)
    {
    	//delete the subtables
    	if(root.getSubTables().size() != 0)
    	{
	    	Set<String> subTables = root.getSubTables();
	    	for(String table : subTables)
	    	{
	    		RecursiveTableDelete((NetworkTable)root.getSubTable(table));
	    	}
    	}
    	
    	//delete the entries
    	if(root.getKeys().size() != 0)
    	{
	    	Set<String> keys = root.getKeys();
	    	for(String key : keys)
	    	{
	    		root.delete(key);
	    	}
    	}
    }
    
    // Called just before this Command runs the first time
    protected void initialize() {
    	RecursiveTableDelete(NetworkTable.getTable("T1922"));
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
    }
}
