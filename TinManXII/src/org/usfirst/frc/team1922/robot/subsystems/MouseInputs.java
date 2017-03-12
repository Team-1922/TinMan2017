package org.usfirst.frc.team1922.robot.subsystems;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import org.ozram1922.cfg.CfgDocument;
import org.ozram1922.cfg.CfgElement;
import org.ozram1922.cfg.CfgInterface;

import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class MouseInputs extends Subsystem implements CfgInterface {

	//config variables
	
	private String _leftMouseInputName = "";
	private String _rightMouseInputName = "";

	
	// runtime variables
	BufferedInputStream _leftBIS;
	BufferedInputStream _rightBIS;
	Thread _updateThread;
	
	//private methods for sensor
	private class SensorUpdateTask implements Runnable
	{
		@Override
		public void run()
		{
			
			try
			{
				byte[] leftBytes = new byte[3];
				int leftInput = _leftBIS.read(leftBytes, 0, 3);
				
				byte[] rightBytes = new byte[3];
				int rightInput = _rightBIS.read(leftBytes, 0, 3);
				//TODO: do some locking
				
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	private void UpdateTask()
	{
		
	}
	
	private void StartUpdateTask()
	{
		_updateThread = new Thread(new SensorUpdateTask());
	}
	
	public void StopStreaming()
	{
		_updateThread.interrupt();
	}
	
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }
    
    public double GetXVelocity()
    {
    	
    }

    protected boolean Reconstruct()
    {
    	try
    	{
    		_leftBIS = new BufferedInputStream(new FileInputStream(new File(_leftMouseInputName)));
    		_rightBIS = new BufferedInputStream(new FileInputStream(new File(_rightMouseInputName)));
    	}
    	catch(FileNotFoundException ex)
    	{
    		return false;
    	}
    	
    	return true;
    }
    
	@Override
	public boolean Deserialize(CfgElement element) {
		_leftMouseInputName = element.GetAttribute("LeftPath");
		_rightMouseInputName = element.GetAttribute("RightPath");
		return Reconstruct();
	}

	@Override
	public CfgElement Serialize(CfgElement blank, CfgDocument doc) {
		blank.SetAttribute("LeftPath", _leftMouseInputName);
		blank.SetAttribute("RightPath", _rightMouseInputName);
		return blank;
	}

	@Override
	public void MakeCfgClassesNull() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public String GetElementTitle() {
		// TODO Auto-generated method stub
		return null;
	}
}

