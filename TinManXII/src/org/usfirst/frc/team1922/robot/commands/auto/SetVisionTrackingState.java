package org.usfirst.frc.team1922.robot.commands.auto;

import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;

/**
 *
 */
public class SetVisionTrackingState extends Command {

	boolean _state; 
    public SetVisionTrackingState(boolean state) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	_state = state;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	Robot.mPixyCam.SetTrackingState(_state);
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
