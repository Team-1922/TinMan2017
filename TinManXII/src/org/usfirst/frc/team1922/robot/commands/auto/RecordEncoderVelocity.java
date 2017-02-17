package org.usfirst.frc.team1922.robot.commands.auto;

import org.ozram1922.autonomous.AutoRecorder;
import org.usfirst.frc.team1922.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 *
 */
public class RecordEncoderVelocity extends Command {
	
	AutoRecorder _leftRecorder = new AutoRecorder();
	AutoRecorder _rightRecorder = new AutoRecorder();
	NetworkTable _entry;
	int leftEntryNumber = 0;
	int rightEntryNumber = 0;

    public RecordEncoderVelocity(String tableName) {
        // Use requires() here to declare subsystem dependencies
        // eg. requires(chassis);
    	_entry = NetworkTable.getTable(tableName);
    	while(_entry.containsKey(Integer.toString(leftEntryNumber) + "L")) leftEntryNumber++;
    	while(_entry.containsKey(Integer.toString(rightEntryNumber) + "R")) rightEntryNumber++;
    }

    // Called just before this Command runs the first time
    protected void initialize() {
    	_leftRecorder.StartRecording();
    	_rightRecorder.StartRecording();
    }

    // Called repeatedly when this Command is scheduled to run
    protected void execute() {
    	_leftRecorder.Update(Robot.mDriveTrain.GetLeftVelocity());
    	_rightRecorder.Update(Robot.mDriveTrain.GetRightVelocity());
    }

    // Make this return true when this Command no longer needs to run execute()
    protected boolean isFinished() {
        return false;
    }

    // Called once after isFinished returns true
    protected void end() {
    	_entry.putString(leftEntryNumber + "L",_leftRecorder.Serialize());
    	_entry.putString(rightEntryNumber + "R",_rightRecorder.Serialize());
    }

    // Called when another command which requires one or more of the same
    // subsystems is scheduled to run
    protected void interrupted() {
    	end();
    }
}
