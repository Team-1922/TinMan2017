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
    	int leftPosEntryNumber = -1;
    	int rightPosEntryNumber = -1;
    	int gearEntryNumber = -1;

    	//get the left voltage file name
    	File f;
    	do
    	{
    		leftEntryNumber++;
    		f = new File(GetFileName(leftEntryNumber, "L", "DTVoltRec"));
    	}
    	while(f.exists() && !f.isDirectory());
    	
    	//get the right voltage file name
    	do
    	{
    		rightEntryNumber++;
    		f = new File(GetFileName(rightEntryNumber, "R", "DTVoltRec"));
    	}
    	while(f.exists() && !f.isDirectory());

    	//get the left position file name
    	/*do
    	{
    		leftPosEntryNumber++;
    		f = new File(GetFileName(leftEntryNumber, "L", "PosRec"));
    	}
    	while(f.exists() && !f.isDirectory());
    	
    	//get the right position file name
    	do
    	{
    		rightPosEntryNumber++;
    		f = new File(GetFileName(rightEntryNumber, "R", "PosRec"));
    	}
    	while(f.exists() && !f.isDirectory());*/

    	
    	//get the gear file name
    	do
    	{
    		gearEntryNumber++;
    		f = new File(GetFileName(gearEntryNumber, "", "GFRec"));
    	}
    	while(f.exists() && !f.isDirectory());
    	
    	
    	leftEntryNumber--;
    	rightEntryNumber--;
    	leftPosEntryNumber--;
    	rightPosEntryNumber--;
    	gearEntryNumber--;

    	SmartDashboard.putNumber("Most Recent Gear", gearEntryNumber);
    	SmartDashboard.putNumber("Most Recent Left", leftEntryNumber);
    	SmartDashboard.putNumber("Most Recent Right", rightEntryNumber);
    	SmartDashboard.putNumber("Most Recient Left Pos", leftPosEntryNumber);
    	SmartDashboard.putNumber("Most Recient Right Pos", rightPosEntryNumber);
    	//addSequential(new PlayAutoRecording();

    	addParallel(new PlaybackGearFlap(GetFileName(gearEntryNumber, "", "GFRec")));
		addSequential(
				new PlayDTVoltageRecording(
						GetFileName(leftEntryNumber, "L", "DTVoltRec"), 
						GetFileName(rightEntryNumber, "R", "DTVoltRec"))); 
		
		//addSequential(
		//		new PlayDTVoltagePositionRecordingAsync(
		//				GetFileName(leftEntryNumber, "L", "DTVoltRec"), 
		//				GetFileName(rightEntryNumber, "R", "DTVoltRec"), 
		//				GetFileName(leftPosEntryNumber, "L", "PosRec"), 
		//				GetFileName(rightPosEntryNumber, "R", "PosRec")));
    }
}
