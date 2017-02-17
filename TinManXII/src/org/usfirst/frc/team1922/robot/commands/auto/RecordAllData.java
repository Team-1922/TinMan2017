package org.usfirst.frc.team1922.robot.commands.auto;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class RecordAllData extends CommandGroup {

    public RecordAllData(String joyTableName, String posTableName, String velTableName) {
    	addParallel(new RecordJoystickInput(joyTableName));
    	addParallel(new RecordEncoderPosition(posTableName));
    	addSequential(new RecordEncoderVelocity(velTableName));
    }
}
