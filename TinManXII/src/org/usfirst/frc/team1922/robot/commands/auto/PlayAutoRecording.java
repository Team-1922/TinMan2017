package org.usfirst.frc.team1922.robot.commands.auto;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class PlayAutoRecording extends CommandGroup {

    public PlayAutoRecording(String leftFilePath, String rightFilePath, String gearFilePath) {
    	addParallel(new PlaybackGearFlap(gearFilePath));
    	addSequential(new PlayDTVoltageRecording(leftFilePath, rightFilePath));
    }
}
