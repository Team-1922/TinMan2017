package org.usfirst.frc.team1922.robot.commands.auto;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class RecordAllData extends CommandGroup {

    public RecordAllData(String joySubDir, String posSubDir, String velSubDir, String dtVoltageSubDir) {
    	addParallel(new RecordJoystickInput(joySubDir));
    	addParallel(new RecordEncoderPosition(posSubDir));
		addParallel(new RecordDTVoltage(dtVoltageSubDir));
    	addSequential(new RecordEncoderVelocity(velSubDir));
    }
}
