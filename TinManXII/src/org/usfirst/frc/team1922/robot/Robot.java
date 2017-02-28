
package org.usfirst.frc.team1922.robot;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PowerDistributionPanel;
import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;

import java.io.IOException;

import org.ozram1922.Vector2d;
import org.ozram1922.cfg.CfgLoader;
import org.ozram1922.fieldsense.EncoderIntegrater;
import org.usfirst.frc.team1922.robot.commands.auto.PlayAutoRecording;
import org.usfirst.frc.team1922.robot.subsystems.DriveTrain;
import org.usfirst.frc.team1922.robot.subsystems.DriverCamera;
import org.usfirst.frc.team1922.robot.subsystems.GearFlap;
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
	public static String mCsvRangeAngleName = "/home/lvuser/RangeAngleTable.csv";
	public static DriverCamera mDriverCamera = new DriverCamera();
	public static DriveTrain mDriveTrain = new DriveTrain();
	public static GearFlap mGearFlap = new GearFlap();
	public static RopeClimber mRopeClimber = new RopeClimber();
	public static PowerDistributionPanel mPDP = new PowerDistributionPanel();
	CameraServer server;

	SendableChooser<Command> chooser = new SendableChooser<>();
	
	public static PlayAutoRecording mAutoC = new PlayAutoRecording(
			"/home/lvuser/DTRecordingC.csv",
			"/home/lvuser/GearRecordingC.csv");
	
	public static PlayAutoRecording mAutoL = new PlayAutoRecording(
			"/home/lvuser/DTRecordingL.csv",
			"/home/lvuser/GearRecordingL.csv");
	
	public static PlayAutoRecording mAutoR = new PlayAutoRecording(
			"/home/lvuser/DTRecordingR.csv",
			"/home/lvuser/GearRecordingR.csv");
	
	public static EncoderIntegrater mFieldState;

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
		mCfgLoader.RegisterCfgClass(oi);
		
		//load the xml file here
		mSuccessfulInit = mCfgLoader.LoadFile(mCfgFileName, true);
		if(!mSuccessfulInit)
		{
			System.out.println("Something went REALLY wrong loading the .xml config file");
		}
		
		CameraServer.getInstance().startAutomaticCapture(0);
		CameraServer.getInstance().startAutomaticCapture(1);
		
		//for non-field use initialization
		mFieldState = new EncoderIntegrater(mDriveTrain.GetWidth(), new Vector2d(0,0));
		//startGRIP();

		chooser.addDefault("Center Auto", mAutoC);
		chooser.addObject("Left Auto", mAutoL);
		chooser.addObject("Right Auto", mAutoR);
    }

	//TODO: Make this Good
    @Deprecated
    protected void startGRIP()
    {
        /* Run GRIP in a new process */
        try {
            new ProcessBuilder("/home/lvuser/grip").inheritIO().start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    protected void CycleIntegrater()
    {		
		//cycle the field position integrater
        try
        {
        	mFieldState.Cycle(mDriveTrain.GetLeftPosition(), mDriveTrain.GetRightPosition());
        }
        catch(Exception e){}
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
		
		//TODO: get this from the dashboard choser
		mFieldState = new EncoderIntegrater(mDriveTrain.GetWidth(), new Vector2d(0,0));
    }

    /**
     * This function is called periodically during autonomous
     */
    public void autonomousPeriodic() {
    	if(!mSuccessfulInit)
    		return;
    	
        Scheduler.getInstance().run();
        
        //cycle the field positioning
        CycleIntegrater();
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
		
		//cycle the field position integrater
        CycleIntegrater();
        
        //TESTING
        UpdateSmartDashboardItems();
    }
    
    public void UpdateSmartDashboardItems()
    {
    	SmartDashboard.putNumber("Left Encoder Speed", mDriveTrain.GetLeftVelocity());
    	SmartDashboard.putNumber("Right Encoder Speed", mDriveTrain.GetRightVelocity());

    	SmartDashboard.putNumber("Left Encoder Position", mDriveTrain.GetLeftPosition());
    	SmartDashboard.putNumber("Right Encoder Position", mDriveTrain.GetRightPosition());

		Vector2d position = mFieldState.Position();
    	SmartDashboard.putNumber("Position X", position.x);
		SmartDashboard.putNumber("Position Y", position.y);
		SmartDashboard.putNumber("Direction", mFieldState.Direction());

    }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
        LiveWindow.run();
    }
}
