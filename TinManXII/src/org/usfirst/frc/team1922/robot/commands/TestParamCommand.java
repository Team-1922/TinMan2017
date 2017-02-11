package org.usfirst.frc.team1922.robot.commands;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 *
 */
public class TestParamCommand extends Command {

	String string;
	int integer;
	double doub;
	float floa;
	boolean bool;
    public TestParamCommand(String str, int i, double d, float f, boolean b) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	string = str;
    	integer = i;
    	doub = d;
    	floa = f;
    	bool = b;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	SmartDashboard.putNumber("TestParam", doub);
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
