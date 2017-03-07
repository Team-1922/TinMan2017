package org.usfirst.frc.team1922.robot.subsystems;

import java.io.IOException;
import java.nio.charset.Charset;

import org.ozram1922.OzUtils;
import org.ozram1922.autonomous.VelocityControl;
import org.ozram1922.cfg.CfgDocument;
import org.ozram1922.cfg.CfgElement;
import org.ozram1922.cfg.CfgInterface;
import org.usfirst.frc.team1922.robot.commands.BareTeleDrive;

import com.ctre.CANTalon;
import com.ctre.CANTalon.TalonControlMode;

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
	
	protected double mDTWidth = 20.5;
	
	protected double mEncoderUnitsPerInch = 90.0;
	
	protected int mShifterId = 0;
	
	protected String mVelocityMapPath = "/home/lvuser/VelocityMap.csv";

	
	/*
	 * 
	 * Velocity PID
	 * 
	 */
	protected float mLP;
	protected float mLI;
	protected float mLD;
	protected float mLF;
	
	protected float mRP;
	protected float mRI;
	protected float mRD;
	protected float mRF;
	//this is in inches
	//protected float mMTolerance;
	
	//protected float mInchesToEncoderUnits;
	//protected float mTurningRadius;
	
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
	

	private int cachedLeftPosition = 0;
	private int cachedRightPosition = 0;
	
	//the map for converting DT velocity to voltages.  Note that this is not sided, because the code
	//		that uses this info uses TankControl which accounts for siding variances
	private VelocityControl _velocityControl = new VelocityControl();
	
	public double VoltageLookup(double velocity)
	{
		return _velocityControl.GetVoltage(velocity);
	}
    
	/*
	 * 
	 * Control Methods
	 * 
	 */
	
	public void TankControl(double left, double right)
	{
		ModeSwap(TalonControlMode.PercentVbus);
		mLeftMotor1.set(left * mLeftSensitivity);		
		mRightMotor1.set(right * mRightSensitivity);
	}
	
	public void SetShifterState(boolean shifterState)
	{
		mDTShifter.set(shifterState);
	}
	
	public double GetLeftVelocity()
	{
		return (double)mLeftMotor1.getEncVelocity() / mEncoderUnitsPerInch;
	}
	
	public double GetRightVelocity()
	{
		return -(double)mRightMotor1.getEncVelocity() / mEncoderUnitsPerInch;		
	}
	
	public double GetLeftPosition()
	{
		return (double)(mLeftMotor1.getEncPosition() - cachedLeftPosition)  / mEncoderUnitsPerInch;
	}
	
	public double GetRightPosition()
	{
		return -(double)(mRightMotor1.getEncPosition() - cachedRightPosition) / mEncoderUnitsPerInch;
	}
	
	public void ResetEncoderPositions()
	{
		cachedLeftPosition = mLeftMotor1.getEncPosition();
		cachedRightPosition = mRightMotor1.getEncPosition();
	}
	
	public double GetWidth()
	{
		return mDTWidth;
	}
	
	/*public void SetVelocity(double leftPIDSetpoint, double rightPIDSetpoint)
	{
		ModeSwap(TalonControlMode.Speed);
		mLeftMotor1.set(leftPIDSetpoint);
		mRightMotor1.set(rightPIDSetpoint);
	}*/
	
	protected void ModeSwap(CANTalon.TalonControlMode mode)
	{
		mLeftMotor1.changeControlMode(mode);		
		mRightMotor1.changeControlMode(mode);
	}

	public void Reconstruct()
	{		
		//the id will typically be over 9000 if we aren't using the motor controller
		mLeftMotor1 = new CANTalon(Math.abs(mLeftMotorId1));
		mLeftMotor2 = new CANTalon(Math.abs(mLeftMotorId2));
		mRightMotor1 = new CANTalon(Math.abs(mRightMotorId1));
		mRightMotor2 = new CANTalon(Math.abs(mRightMotorId2));

		mLeftMotor1.setInverted(mLeftMotorId1 < 0);
		mLeftMotor2.setInverted(mLeftMotorId2 < 0);
		mRightMotor1.setInverted(mRightMotorId1 < 0);
		mRightMotor2.setInverted(mRightMotorId2 < 0);
		
		mDTShifter = new Solenoid(Math.abs(mShifterId));

		mLeftMotor1.setPID(mLP, mLI, mLD);
		mLeftMotor1.setF(mLF);
		
		mRightMotor1.setPID(mRP, mRI, mRD);
		mRightMotor1.setF(mRF);
		
		mRightMotor2.changeControlMode(TalonControlMode.Follower);
		mRightMotor2.set(Math.abs(mRightMotorId1));
		
		mLeftMotor2.changeControlMode(TalonControlMode.Follower);
		mLeftMotor2.set(Math.abs(mLeftMotorId1));
		
		
		ResetEncoderPositions();
		//mLeftMotor1.setPID(mMP, mMI, mMD);
		
		
		try {
			_velocityControl.Deserialize(OzUtils.readFile(mVelocityMapPath));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
		
		mVelocityMapPath = element.GetAttribute("VelocityMap");
		
		mLeftMotorId1 = element.GetAttributeI("LeftMotor1");
		mLeftMotorId2 = element.GetAttributeI("LeftMotor2");
		mRightMotorId1 = element.GetAttributeI("RightMotor1");
		mRightMotorId2 = element.GetAttributeI("RightMotor2");
		
		mShifterId = element.GetAttributeI("Shifter");
		
		mLP = element.GetAttributeF("LeftP");
		mLI = element.GetAttributeF("LeftI");
		mLD = element.GetAttributeF("LeftD");
		mLF = element.GetAttributeF("LeftF");

		mRP = element.GetAttributeF("RightP");
		mRI = element.GetAttributeF("RightI");
		mRD = element.GetAttributeF("RightD");
		mRF = element.GetAttributeF("RightF");
		
		mEncoderUnitsPerInch = element.GetAttributeD("EncoderUnitsPerInch");
		mDTWidth = element.GetAttributeD("Width");
		//mMTolerance = element.GetAttributeF("MovementTolerance");
		//mInchesToEncoderUnits = element.GetAttributeF("InchesToEncoderUnits");
		//mTurningRadius = element.GetAttributeF("TurningRadius");
		
		//mATolerance = Float.parseFloat(element.GetAttribute("AimingTolerance"));
		
		//This may be OK to do.  because enc per angle = (enc/in) * radius
		//mRadiansToEncoderUnits = mInchesToEncoderUnits * mTurningRadius;
		////mRadiansToEncoderUnits = Float.parseFloat(mCfgInstance.GetAttribute("RadiansToEncoderUnits"));
		
		Reconstruct();
		
		return true;
	}

	@Override
	public CfgElement Serialize(CfgElement element, CfgDocument doc) {


		element.SetAttribute("LeftSensitivity", mLeftSensitivity);
		element.SetAttribute("RightSensitivity", mRightSensitivity);
		
		element.SetAttribute("VelocityMap", mVelocityMapPath);
		
		element.SetAttribute("LeftMotor1", mLeftMotorId1);
		element.SetAttribute("LeftMotor2", mLeftMotorId2);
		element.SetAttribute("RightMotor1", mRightMotorId1);
		element.SetAttribute("RightMotor2", mRightMotorId2);
		
		element.SetAttribute("Shifter", mShifterId);
		
		element.SetAttribute("LeftP", mLP);
		element.SetAttribute("LeftI", mLI);
		element.SetAttribute("LeftD", mLD);
		element.SetAttribute("LeftF", mLF);
		
		element.SetAttribute("RightP", mRP);
		element.SetAttribute("RightI", mRI);
		element.SetAttribute("RightD", mRD);
		element.SetAttribute("RightF", mRF);
		
		element.SetAttribute("EncoderUnitsPerInch", mEncoderUnitsPerInch);
		element.SetAttribute("Width", mDTWidth);
		//element.SetAttribute("MovementTolerance", mMTolerance);
		//element.SetAttribute("InchesToEncoderUnits", mInchesToEncoderUnits);
		//element.SetAttribute("TurningRadius", mTurningRadius);
		
		//element.SetAttribute("AimingTolerance", mATolerance);
		////mCfgInstance.SetAttribute("RadiansToEncoderUnits", Float.toString(mRadiansToEncoderUnits));
		
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

