package org.usfirst.frc.team1922.robot.subsystems;

import org.ozram1922.cfg.CfgDocument;
import org.ozram1922.cfg.CfgElement;
import org.ozram1922.cfg.CfgInterface;
import org.usfirst.frc.team1922.robot.commands.BareTeleDrive;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class DriveTrain extends Subsystem implements CfgInterface {

	/*
	 * 
	 * Config Variables
	 * 
	 */
	
	protected float mLeftSensitivity = 1.0f;
	protected float mRightSensitivity = 1.0f;
	
	//if these values are negative, that motor is inverted
	protected int mLeftMotorId1 = 1;
	protected int mLeftMotorId2 = 2;
	protected int mRightMotorId1 = 3;
	protected int mRightMotorId2 = 4;
	
	protected int mShifterId = 0;

	
	/*
	 * 
	 * Regular PID
	 * 
	 */
	protected float mMP;
	protected float mMI;
	protected float mMD;
	//this is in inches
	protected float mMTolerance;
	
	protected float mInchesToEncoderUnits;
	protected float mTurningRadius;

	/*
	 * 
	 * Rotational PID
	 * 
	 */
	//this is in RADIANS
	protected float mATolerance;
	protected float mRadiansToEncoderUnits;
	
	/*
	 * 
	 * Actual Member Variables
	 * 
	 */
	protected CANTalon mLeftMotor1;
	protected CANTalon mLeftMotor2;
	protected CANTalon mRightMotor1;
	protected CANTalon mRightMotor2;
	
	protected Solenoid mDTShifter;
	
	protected double mClutchRatio = 1.0;
    
	/*
	 * 
	 * Control Methods
	 * 
	 */
	
	public void TankControl(double left, double right)
	{
		mLeftMotor1.set(left * mLeftSensitivity);
		mLeftMotor2.set(left * mLeftSensitivity);
		
		mRightMotor1.set(right * mLeftSensitivity);
		mRightMotor2.set(right * mLeftSensitivity);
	}
	
	public void SetShifterState(boolean shifterState)
	{
		mDTShifter.set(shifterState);
	}
	

	public void Reconstruct()
	{		
		//the id will typically be over 9000 if we aren't using the motor controller
		mLeftMotor1 = new CANTalon(Math.abs(mLeftMotorId1));
		mLeftMotor2 = new CANTalon(Math.abs(mLeftMotorId2));
		mRightMotor1 = new CANTalon(Math.abs(mRightMotorId1));
		mRightMotor2 = new CANTalon(Math.abs(mRightMotorId2));

		mDTShifter = new Solenoid(Math.abs(mShifterId));
		
		//mLeftMotor1.setPID(mMP, mMI, mMD);
	}
	

	/*
	 * 
	 * Override Functions
	 * 
	 */

	@Override
	public boolean Deserialize(CfgElement element) {

		mLeftSensitivity = element.GetAttributeF("LeftSensitivity");
		mRightSensitivity = element.GetAttributeF("RightSensitivity");
		
		mLeftMotorId1 = element.GetAttributeI("LeftMotor1");
		mLeftMotorId2 = element.GetAttributeI("LeftMotor2");
		mRightMotorId1 = element.GetAttributeI("RightMotor1");
		mRightMotorId2 = element.GetAttributeI("RightMotor2");
		
		mShifterId = element.GetAttributeI("Shifter");
		
		mMP = element.GetAttributeF("MovementP");
		mMI = element.GetAttributeF("MovementI");
		mMD = element.GetAttributeF("MovementD");
		mMTolerance = element.GetAttributeF("MovementTolerance");
		mInchesToEncoderUnits = element.GetAttributeF("InchesToEncoderUnits");
		mTurningRadius = element.GetAttributeF("TurningRadius");
		
		mATolerance = Float.parseFloat(element.GetAttribute("AimingTolerance"));
		
		//This may be OK to do.  because enc per angle = (enc/in) * radius
		mRadiansToEncoderUnits = mInchesToEncoderUnits * mTurningRadius;
		//mRadiansToEncoderUnits = Float.parseFloat(mCfgInstance.GetAttribute("RadiansToEncoderUnits"));
		
		Reconstruct();
		
		return true;
	}

	@Override
	public CfgElement Serialize(CfgElement element, CfgDocument doc) {


		element.SetAttribute("LeftSensitivity", mLeftSensitivity);
		element.SetAttribute("RightSensitivity", mRightSensitivity);
		
		element.SetAttribute("LeftMotor1", mLeftMotorId1);
		element.SetAttribute("LeftMotor2", mLeftMotorId2);
		element.SetAttribute("RightMotor1", mRightMotorId1);
		element.SetAttribute("RightMotor2", mRightMotorId2);
		
		element.SetAttribute("Shifter", mShifterId);
		
		element.SetAttribute("MovementP", mMP);
		element.SetAttribute("MovementI", mMI);
		element.SetAttribute("MovementD", mMD);
		element.SetAttribute("MovementTolerance", mMTolerance);
		element.SetAttribute("InchesToEncoderUnits", mInchesToEncoderUnits);
		element.SetAttribute("TurningRadius", mTurningRadius);
		
		element.SetAttribute("AimingTolerance", mATolerance);
		//mCfgInstance.SetAttribute("RadiansToEncoderUnits", Float.toString(mRadiansToEncoderUnits));
		
		return element;
	}

	@Override
	public String GetElementTitle()
	{
		return "DriveTrain";
	}

    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        setDefaultCommand(new BareTeleDrive());
    }
	@Override
	public void MakeCfgClassesNull() {
		if(mLeftMotor1 != null)
			mLeftMotor1.delete();
		if(mLeftMotor2 != null)
			mLeftMotor2.delete();
		if(mRightMotor1 != null)
			mRightMotor1.delete();
		if(mRightMotor2 != null)
			mRightMotor2.delete();
		if(mDTShifter != null)
			mDTShifter.free();
		
		mLeftMotor1 = null;
		mLeftMotor2 = null;
		mRightMotor1 = null;
		mRightMotor2 = null;
		
		mDTShifter = null;
	}

}

