package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.commands.TimedTankDrive;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class SidedPeg extends CommandGroup {

    public SidedPeg() {
    	
    	addSequential(new TimedTankDrive(0.75, 0.75, 2));
    	addSequential(new TimedTankDrive(0.5, -0.5, 0.5));
    	addSequential(new SetVisionTrackingState(true));
    	addSequential(new TimedTankDrive(0.4, 0.4, 3));
    	addSequential(new SetVisionTrackingState(false));
    	addSequential(new TimedTankDrive(0.4, 0.4, 2));
    	addSequential(new TimedTankDrive(-0.75, -0.75, 1));
    	addSequential(new TimedTankDrive(-0.5, 0.75, 0.4));
    	addSequential(new TimedTankDrive(0.75, 0.75, 1.5));
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
    }
}
