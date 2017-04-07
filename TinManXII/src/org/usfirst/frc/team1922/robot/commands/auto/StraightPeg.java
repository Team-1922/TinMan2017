package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.commands.CloseGearFlap;
import org.usfirst.frc.team1922.robot.commands.OpenGearFlap;
import org.usfirst.frc.team1922.robot.commands.TimedTankDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class StraightPeg extends CommandGroup {
	
    public StraightPeg(boolean gear) {
    	
    	//drive forward
    	addSequential(new TimedTankDrive(0.75, 0.75, 1));
    	
    	//Enable vision tracking
    	addSequential(new SetVisionTrackingState(true));
    	
    	//drive forwards towards the peg
    	addSequential(new TimedTankDrive(0.4, 0.4, 3));
    	
    	//during the previous 3 seconds, hopefully the bot has had a chance to correct
    	addSequential(new SetVisionTrackingState(false));
    	
    	//continue driving into the peg
    	addSequential(new TimedTankDrive(0.4, 0.4, 2));
    	
    	//drop off the gear ...
    	if(gear)
    	{
    		addParallel(new OpenGearFlap());
    	}
    	//... while driving backwards
    	addSequential(new TimedTankDrive(-0.75, -0.75, 1));
    	
    	//close the gear flap
    	if(gear)
    	{
    		addSequential(new CloseGearFlap());
    	}
    }
    
    public StraightPeg()
    {
    	this(false);
    }
}
