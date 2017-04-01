package org.usfirst.frc.team1922.robot.commands.auto;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class RecordAllData extends CommandGroup {

    public RecordAllData(int periodMS, String joySubDir, String posSubDir, String velSubDir, String dtVoltageSubDir, String gearFlapSubDir) {
    	addParallel(new RecordJoystickInput(joySubDir, periodMS));
    	addParallel(new RecordEncoderPosition(posSubDir, periodMS));
		addParallel(new RecordDTVoltage(dtVoltageSubDir, periodMS));
		addParallel(new RecordGearFlap(gearFlapSubDir, periodMS));
    	addSequential(new RecordEncoderVelocity(velSubDir, periodMS));
    }
}
