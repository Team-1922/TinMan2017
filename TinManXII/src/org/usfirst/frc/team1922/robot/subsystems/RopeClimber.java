package org.usfirst.frc.team1922.robot.subsystems;

import org.ozram1922.cfg.CfgDocument;
import org.ozram1922.cfg.CfgElement;
import org.ozram1922.cfg.CfgInterface;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class RopeClimber extends Subsystem implements CfgInterface {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	/*
	 * Config Values
	 */
	int motorId = 5;
	
	
	/*
	 * Member Variables
	 */
	CANTalon climbingMotor;

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public void Reconstruct()
    {		
		climbingMotor = new CANTalon(Math.abs(motorId));
		climbingMotor.setInverted(motorId < 0);
    }

	@Override
	public boolean Deserialize(CfgElement element) {

		motorId = element.GetAttributeI("MotorID");
		
		Reconstruct();
		return true;
	}

	@Override
	public CfgElement Serialize(CfgElement blank, CfgDocument doc) {
		blank.SetAttribute("MotorID", motorId);
		return blank;
	}

	@Override
	public void MakeCfgClassesNull() {
		if(climbingMotor != null)
			climbingMotor.delete();
		climbingMotor = null;
	}

	@Override
	public String GetElementTitle() {
		return "RopeClimber";
	}
}

