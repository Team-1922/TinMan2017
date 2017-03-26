package org.usfirst.frc.team1922.robot.subsystems;

import org.ozram1922.cfg.CfgDocument;
import org.ozram1922.cfg.CfgElement;
import org.ozram1922.cfg.CfgInterface;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class GearFlap extends Subsystem implements CfgInterface {

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	public int solenoidId = 1;
	
	public int pegSensorId = 0;
	
	Solenoid flapActuator;
	DigitalInput pegSensor;

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

    public void SetFlapState(boolean open)
    {
    	flapActuator.set(open);
    }
    
    public boolean GetFlapState()
    {
    	return flapActuator.get();
    }
    
    public boolean IsPegDetected()
    {
    	return pegSensor.get();
    }
    
    public int GetChannel()
    {
    	return pegSensor.getChannel();
    }
    
    /*
     * Override Methods
     * @see org.ozram1922.cfg.CfgInterface#Deserialize(org.ozram1922.cfg.CfgElement)
     */
    
    private void Reconstruct()
    {
    	flapActuator = new Solenoid(solenoidId);
    	pegSensor = new DigitalInput(pegSensorId);
    }
    
	@Override
	public boolean Deserialize(CfgElement element) {

		solenoidId = element.GetAttributeI("ActuatorId");
		pegSensorId = element.GetAttributeI("PegSensorId");
		Reconstruct();
		return true;
	}

	@Override
	public CfgElement Serialize(CfgElement blank, CfgDocument doc) {
		blank.SetAttribute("ActuatorId", solenoidId);
		blank.SetAttribute("PegSensorId", pegSensorId);
		return blank;
	}

	@Override
	public void MakeCfgClassesNull() {
		if(flapActuator != null)
			flapActuator.free();
		if(pegSensor != null)
			pegSensor.free();
		
		pegSensor = null;
		flapActuator = null;
	}

	@Override
	public String GetElementTitle() {
		// TODO Auto-generated method stub
		return "GearFlap";
	}
}

