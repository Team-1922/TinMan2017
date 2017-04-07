package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.commands.CloseGearFlap;
import org.usfirst.frc.team1922.robot.commands.OpenGearFlap;
import org.usfirst.frc.team1922.robot.commands.TimedTankDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class SidedPeg extends CommandGroup {
	
	//side = 0: left;
	//side = 1: right;
    public SidedPeg(int side, boolean gear) {
    	
    	//drive forward
    	addSequential(new TimedTankDrive(0.75, 0.75, 2));
    	
    	//turn to see peg
    	if(side == 0)
    	{
    		addSequential(new TimedTankDrive(0.5, -0.5, 0.55));
    	}
    	else
    	{
    		addSequential(new TimedTankDrive(-0.5, 0.5, 0.55));
    	}
    	
    	//go forward a little
    	//addSequential(new TimedTankDrive(0.5, 0.5, 1));
    	
    	//Enable vision tracking
    	addSequential(new SetVisionTrackingState(true));
    	
    	//drive forwards towards the peg
    	addSequential(new TimedTankDrive(0.4, 0.4, 1.5));
    	
    	//during the previous 3 seconds, hopefully the bot has had a chance to correct
    	addSequential(new SetVisionTrackingState(false));
    	
    	//continue driving into the peg
    	addSequential(new TimedTankDrive(0.4, 0.4, 3));
    	
    	//drop off the gear ...
    	if(gear)
    	{
    		addParallel(new OpenGearFlap());
    	}
    	//... while driving backwards
    	addSequential(new TimedTankDrive(-0.75, -0.75, 1));
    	
    	//close the gear flap ...
    	if(gear)
    	{
        	addParallel(new CloseGearFlap());	
    	}
    	//... while turning
    	if(side == 0)
    	{
    		addSequential(new TimedTankDrive(-0.75, 0.75, 0.4));
    	}
    	else
    	{
    		addSequential(new TimedTankDrive(0.75, -0.75, 0.4));
    	}
    	
    	//cross the baseline
    	addSequential(new TimedTankDrive(0.75, 0.75, 1.5));
    }
    
    public SidedPeg(int side)
    {
    	this(side, false);
    }
}
