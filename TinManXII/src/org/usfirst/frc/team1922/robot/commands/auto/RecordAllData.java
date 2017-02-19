package org.usfirst.frc.team1922.robot.commands.auto;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class RecordAllData extends CommandGroup {

    public RecordAllData(String joyTableName, String posTableName, String velTableName, String dtVoltageTableName) {
    	addParallel(new RecordJoystickInput(joyTableName));
    	addParallel(new RecordEncoderPosition(posTableName));
		addParallel(new RecordDTVoltage(dtVoltageTableName));
    	addSequential(new RecordEncoderVelocity(velTableName));
    }
}
