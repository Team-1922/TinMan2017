package org.usfirst.frc.team1922.robot.commands;

import edu.wpi.first.wpilibj.command.Command;

public class CommandGroupItem {

	private Command _command;
	private boolean _isSequential;
	public CommandGroupItem(Command command, boolean sequential)
	{
		_command = command;
		_isSequential = sequential;
	}
	
	public Command GetCommand()
	{
		return _command;
	}
	
	public boolean IsSequential()
	{
		return _isSequential;
	}
}
