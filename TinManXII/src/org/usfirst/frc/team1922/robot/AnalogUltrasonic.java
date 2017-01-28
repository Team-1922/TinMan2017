package org.usfirst.frc.team1922.robot;

import edu.wpi.first.wpilibj.AnalogInput;

//this is only to be used with MB1013
public class AnalogUltrasonic extends AnalogInput
{
	protected double mScalingFactor =  0.009766;
	
	public AnalogUltrasonic(int id)
	{
		super(id);
	}
	
	//scalingFactor = volts per inch
	public AnalogUltrasonic(int id, double scalingFactor)
	{
		
		super(id);
		mScalingFactor = scalingFactor;
	}
	
	public double GetDistanceInches()
	{
		return 41.90607 * GetRawInput() + 0.92030;
	}
	
	protected double GetRawInput()
	{
		return getAverageVoltage();
	}
		

}
