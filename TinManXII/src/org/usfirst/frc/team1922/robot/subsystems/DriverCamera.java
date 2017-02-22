package org.usfirst.frc.team1922.robot.subsystems;

import org.ozram1922.cfg.CfgDocument;
import org.ozram1922.cfg.CfgElement;
import org.ozram1922.cfg.CfgInterface;

import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriverCamera extends Subsystem implements CfgInterface {
	
    // Put methods for controlling this subsystem
    // here. Call these from Commands.
	
	protected Servo mRotServo;
	
	protected int mServoId;
	protected float mFullPos;
	protected float mFullNeg;
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public void SetFullPos()
    {
    	Set(mFullPos);
    }
    
    public void SetFullNeg()
    {
    	Set(mFullNeg);
    }
    
    public void Set(double pos)
    {
    	mRotServo.set(pos);
    }
    
    public double Get()
    {
    	return mRotServo.get();
    }
    
    public boolean IsReversed()
    {
    	return Math.abs(mFullNeg - mRotServo.get()) < 0.1;
    }

	@Override
	public boolean Deserialize(CfgElement element) {
		mServoId = element.GetAttributeI("ServoId");
		mFullPos = element.GetAttributeF("FullPos");
		mFullNeg = element.GetAttributeF("FullNeg");
		
		mRotServo = new Servo(mServoId);
		return true;
	}

	@Override
	public CfgElement Serialize(CfgElement element, CfgDocument doc) {
		element.SetAttribute("ServoId", mServoId);
		element.SetAttribute("FullPos", mFullPos);
		element.SetAttribute("FullNeg", mFullNeg);
		
		return element;
	}
	
	@Override
	public String GetElementTitle()
	{
		return "DriverCamera";
	}

	@Override
	public void MakeCfgClassesNull() {
		if(mRotServo != null)
			mRotServo.free();
		
		mRotServo = null;
	}
}

