package org.usfirst.frc.team1922.robot.commands.auto;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class PlayAutoRecording extends CommandGroup {

    public PlayAutoRecording(String filePath, String gearFilePath, int periodMS) {
    	addParallel(new PlaybackGearFlap(gearFilePath, periodMS));
    	addSequential(new PlayDTVoltageRecording(filePath, periodMS));
    }
}
