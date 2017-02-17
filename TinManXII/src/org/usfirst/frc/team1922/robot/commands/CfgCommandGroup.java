package org.usfirst.frc.team1922.robot.commands;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class CfgCommandGroup extends CommandGroup {

	private String _name = "";
	
	//Note: this does NO validation for correctness of command group
    public CfgCommandGroup(String name, ArrayList<CommandGroupItem> commands) {
    	_name = name;
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
    	
    	for(int i = 0; i < commands.size(); ++i)
    	{
    		if(commands.get(i).IsSequential())
    		{
    			addSequential(commands.get(i).GetCommand());
    		}
    		else
    		{
    			addParallel(commands.get(i).GetCommand());
    		}
    	}
    }
    
    public String GetName()
    {
    	return _name;
    }
}
