
package org.usfirst.frc.team1922.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import org.ozram1922.cfg.CfgLoader;
import org.usfirst.frc.team1922.robot.commands.TimedTankDrive;
import org.usfirst.frc.team1922.robot.commands.auto.PlayAutoRecording;
import org.usfirst.frc.team1922.robot.commands.auto.SidedPeg;
import org.usfirst.frc.team1922.robot.commands.auto.StraightPeg;
import org.usfirst.frc.team1922.robot.subsystems.DriveTrain;
import org.usfirst.frc.team1922.robot.subsystems.DriverCamera;
import org.usfirst.frc.team1922.robot.subsystems.GearFlap;
import org.usfirst.frc.team1922.robot.subsystems.PixyCamProcessing;
import org.usfirst.frc.team1922.robot.subsystems.RopeClimber;

import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

	public static OI oi = new OI();
	public static CfgLoader mCfgLoader = new CfgLoader();
	public static String mCfgFileName = "/home/lvuser/TinManXII.cfg.xml";
	public static DriverCamera mDriverCamera = new DriverCamera();
	public static DriveTrain mDriveTrain = new DriveTrain();
	public static GearFlap mGearFlap = new GearFlap();
	public static RopeClimber mRopeClimber = new RopeClimber();
	public static PixyCamProcessing mPixyCam = new PixyCamProcessing();
	public static PowerDistributionPanel mPDP = new PowerDistributionPanel();

	SendableChooser<Command> chooser = new SendableChooser<>();
	
	public static PlayAutoRecording mOldAutoC = new PlayAutoRecording(
			"/home/lvuser/LeftRecordingC.csv",
			"/home/lvuser/RightRecordingC.csv",
			"/home/lvuser/GearRecordingC.csv");
	
	//public static PlayAutoRecording mAutoL = new PlayAutoRecording(
	//		"/home/lvuser/LeftRecordingL.csv",
	//		"/home/lvuser/RightRecordingL.csv",
	//		"/home/lvuser/GearRecordingL.csv");
	
	//public static PlayAutoRecording mAutoR = new PlayAutoRecording(
	//		"/home/lvuser/LeftRecordingR.csv",
	//		"/home/lvuser/RightRecordingR.csv",
	//		"/home/lvuser/GearRecordingR.csv");
	
	public static SidedPeg mAutoL = new SidedPeg(0);
	public static SidedPeg mAutoR = new SidedPeg(1);
	public static StraightPeg mAutoC = new StraightPeg();
	
	public static TimedTankDrive mAutoBase = new TimedTankDrive(0.75,0.75,4); 

    Command autonomousCommand;
    
    //Command mJoyCtrlAngle;
    //Command mSetAngle;
    
    //store variable to make sure robot state is good
    boolean mSuccessfulInit = false;

    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
    public void robotInit() {

		SmartDashboard.putNumber("Command With Param",14);
		//register XML loading classes here
		mCfgLoader.RegisterCfgClass(mDriverCamera);
		mCfgLoader.RegisterCfgClass(mDriveTrain);
		mCfgLoader.RegisterCfgClass(mGearFlap);
		mCfgLoader.RegisterCfgClass(mRopeClimber);
		mCfgLoader.RegisterCfgClass(mPixyCam);
		mCfgLoader.RegisterCfgClass(oi);
		
		//load the xml file here
		mSuccessfulInit = mCfgLoader.LoadFile(mCfgFileName, true);
		if(!mSuccessfulInit)
		{
			System.out.println("Something went REALLY wrong loading the .xml config file");
		}
		
		CameraServer.getInstance().startAutomaticCapture(0);
		CameraServer.getInstance().startAutomaticCapture(1);
		
		//comment this out 
		mPixyCam.Start();

		chooser.addDefault("Center Auto", mAutoC);
		chooser.addDefault("Old Center Auto", mOldAutoC);
		chooser.addObject("Left Auto", mAutoL);
		chooser.addObject("Right Auto", mAutoR);
		chooser.addObject("BaseLine Auto", mAutoBase);
		chooser.addObject("None", null);
		SmartDashboard.putData("Auto Choices", chooser);
    }
	
	/**
     * This function is called once each time the robot enters Disabled mode.
     * You can use it to reset any subsystem information you want to clear when
	 * the robot is disabled.
     */
    public void disabledInit(){

    }
	
	public void disabledPeriodic() {
		Scheduler.getInstance().run();
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select between different autonomous modes
	 * using the dashboard. The sendable chooser code works with the Java SmartDashboard. If you prefer the LabVIEW
	 * Dashboard, remove all of the chooser code and uncomment the getString code to get the auto name from the text box
	 * below the Gyro
	 *
	 * You can add additional auto modes by adding additional commands to the chooser code above (like the commented example)
	 * or additional comparisons to the switch structure below with additional strings & commands.
	 */
    public void autonomousInit() {

		autonomousCommand = chooser.getSelected();
    	// schedule the autonomous command (example)
        if (autonomousCommand != null) autonomousCommand.start();
		
		//tare the value during auto mode
		mDriveTrain.ResetEncoderPositions();
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	if(!mSuccessfulInit)
    		return;
    	
        Scheduler.getInstance().run();
    }

    public void teleopInit() {
		// This makes sure that the autonomous stops running when
        // teleop starts running. If you want the autonomous to 
        // continue until interrupted by another command, remove
        // this line or comment it out.
        if (autonomousCommand != null) autonomousCommand.cancel();
        
    }

    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	if(!mSuccessfulInit)
    		return;
    	
        Scheduler.getInstance().run();
        
        //TESTING
        //UpdateSmartDashboardItems();
    }
    
    public void UpdateSmartDashboardItems()
    {
    	SmartDashboard.putNumber("Left Encoder Speed", mDriveTrain.GetLeftVelocity());
    	SmartDashboard.putNumber("Right Encoder Speed", mDriveTrain.GetRightVelocity());

    	SmartDashboard.putNumber("Left Encoder Position", mDriveTrain.GetLeftPosition());
    	SmartDashboard.putNumber("Right Encoder Position", mDriveTrain.GetRightPosition());
    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
