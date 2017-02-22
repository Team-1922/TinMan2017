package org.usfirst.frc.team1922.robot.commands.auto;

import java.io.File;
import java.io.PrintWriter;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class PlayMostRecentAutoRecording extends CommandGroup {

    
    protected static String GetFileName(int number, String modifier, String subDir)
    {
    	return "/home/lvuser/TinManRecordings/" + subDir + "/" + number + modifier + ".csv";
    }
    public PlayMostRecentAutoRecording() {
    	int leftEntryNumber = -1;
    	int rightEntryNumber = -1;
    	int gearEntryNumber = -1;

    	//get the left file name
    	File f;
    	do
    	{
    		leftEntryNumber++;
    		f = new File(GetFileName(leftEntryNumber, "L", "DTVoltRec"));
    	}
    	while(f.exists() && !f.isDirectory());
    	
    	//get the right file name
    	do
    	{
    		rightEntryNumber++;
    		f = new File(GetFileName(rightEntryNumber, "R", "DTVoltRec"));
    	}
    	while(f.exists() && !f.isDirectory());

    	
    	//get the gear file name
    	do
    	{
    		gearEntryNumber++;
    		f = new File(GetFileName(gearEntryNumber, "", "GFRec"));
    	}
    	while(f.exists() && !f.isDirectory());
    	
    	
    	leftEntryNumber--;
    	rightEntryNumber--;
    	gearEntryNumber--;

    	SmartDashboard.putNumber("Most Recent Gear", gearEntryNumber);
    	SmartDashboard.putNumber("Most Recent Left", leftEntryNumber);
    	SmartDashboard.putNumber("Most Recent Right", rightEntryNumber);
    	addSequential(new PlayAutoRecording(
    			GetFileName(leftEntryNumber, "L", "DTVoltRec"), 
    			GetFileName(rightEntryNumber, "R", "DTVoltRec"), 
    			GetFileName(gearEntryNumber, "", "GFRec")));
    }
}
