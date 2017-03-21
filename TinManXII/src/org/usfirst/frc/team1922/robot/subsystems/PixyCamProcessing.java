package org.usfirst.frc.team1922.robot.subsystems;

import java.util.ArrayList;

import org.ozram1922.cfg.CfgDocument;
import org.ozram1922.cfg.CfgElement;
import org.ozram1922.cfg.CfgInterface;
import org.ozram1922.image.PixyCamBlock;
import org.ozram1922.image.PixyCamFrame;
import org.ozram1922.image.PixyCamSPI;
import org.usfirst.frc.team1922.robot.commands.RunPixyCam;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;

public class PixyCamProcessing extends Subsystem implements CfgInterface {

	/*
	 * 
	 * Data Members
	 * 
	 */
	private int _windowXCenter = 160;
	private double _proportional = 0.0;
	private int _threshold = 5;
	private int _periodMS = 100;
	
	
	/*
	 * 
	 * GET NAME FOR THESE
	 * 
	 */
	
	private PixyCamSPI _pixyCam;
	private PixyCamFrame _frame;
	private int _targetXPosition;
	private int _targetYPosition;
	private boolean _seesTarget;
	
	
	/*
	 * 
	 * Constructors
	 * 
	 */
	
	public PixyCamProcessing()
	{
		_pixyCam = new PixyCamSPI(SPI.Port.kOnboardCS0);
	}
	
	/*
	 * 
	 * Control Methods
	 * 
	 */
	
	public void UpdateFrame()
	{
		PixyCamFrame nextFrame = _pixyCam.GetFrame(_frame);
		if(nextFrame.IsDifferent(_frame))
		{
			_frame = nextFrame;
			//recalc position
			
			ArrayList<PixyCamBlock> blocks = new ArrayList<PixyCamBlock>();
			for(int i = 0; i < _frame.List.size(); ++i)
			{
				if(i == 0)
				{
					//always add the first block
					blocks.add(_frame.List.get(i));
					continue;
				}
				
				//combine vertically
				for(int j = 0; j < blocks.size(); j++)
				{
					//if the two blocks are within 5 pixels, then add to them.  its probably a duplicate (or cut in half by the hook)
					if(Math.abs(blocks.get(j).X - _frame.List.get(i).X) < 5)
					{
						PixyCamBlock storedBlock = blocks.get(j);
						PixyCamBlock newBlock = _frame.List.get(i);
						
						// expand the rect vertically to include the full size of both
						int storedTop = storedBlock.Y + storedBlock.Height;
						int newTop = newBlock.Y + newBlock.Height;
						storedBlock.Y = Math.min(storedBlock.Y, newBlock.Y);
						storedBlock.Height = Math.max(storedTop, newTop) - storedBlock.Y;
						
						//TODO: this might not be the best strategy for X values
						//average the X positions in case angle is off
						storedBlock.X = (storedBlock.X + newBlock.X) / 2;						
					}
					
					//if not within 5 pixels, then add and break;
					blocks.add(_frame.List.get(i));
					break;
				}
			}
			
			//how many blocks do we have?
			if(blocks.size() < 2)
			{
				_seesTarget = false;
				return;
			}
				
			
			PixyCamBlock bigBlock1, bigBlock2;
			bigBlock1 = blocks.get(0);
			bigBlock2 = blocks.get(1);

			//now that we have combined the blocks into adjacent block clusters, then get the two biggest ones
			for(int i = 2; i < blocks.size(); ++i)
			{
				//always be sure we are overwriting the smaller block
				PixyCamBlock target;
				if(bigBlock1.GetAABBArea() > bigBlock2.GetAABBArea())
				{
					target = bigBlock2;
				}
				else
				{
					target = bigBlock1;
				}
				
				if(blocks.get(i).GetAABBArea() > target.GetAABBArea())
				{
					target = blocks.get(i);
				}
			}
			
			//get the midpoint (center) of the two X & Y Positions
			_targetXPosition = (bigBlock1.X + bigBlock2.X) / 2;
			_targetYPosition = (bigBlock1.Y + bigBlock2.Y) / 2;
			
			//now shift the X to be relative to the center of the screen
			_targetXPosition -= _windowXCenter;
		}
	}
	
	public void Start()
	{
		_pixyCam.Start(_periodMS);
	}
	
	public void Stop()
	{
		_pixyCam.Stop();
	}
	
	/*
	 * 
	 * Returns the Percent V-Bus twist factor (+ = CW; - = CCW);
	 * 	Typically, this value should be divided by two and half be added/subtracted to each side
	 * 
	 */
	public double GetDTTwist()
	{
		UpdateFrame();	
		if(!TargetInView())
			return 0;
		return _targetXPosition * _proportional;
	}	
	
	/*
	 * 
	 * The number of pixels away from the assigned center the target is
	 * 
	 */
	public int GetPixelsOffCenter()
	{
		UpdateFrame();
		if(!TargetInView())
			return Integer.MAX_VALUE;
		return _targetXPosition;
	}
	
	/*
	 * 
	 * Whether the camera has established a target
	 * 
	 */
	public boolean TargetInView()
	{
		return _seesTarget;
	}
	
	/*
	 * 
	 * Subsystem Methods
	 * 
	 */

	@Override
	protected void initDefaultCommand() {
		// TODO Auto-generated method stub
		
	}
	
	
	/*
	 * 
	 * CfgInterface Methods
	 * 
	 */
	@Override
	public boolean Deserialize(CfgElement element) {

		_windowXCenter = element.GetAttributeI("CameraCenter");
		_proportional = element.GetAttributeI("ProportionalControl");
		_threshold = element.GetAttributeI("Threshold");
		_periodMS = element.GetAttributeI("UpdatePeriod");
		return true;
	}

	@Override
	public CfgElement Serialize(CfgElement blank, CfgDocument doc) {

		blank.SetAttribute("CameraCenter", _windowXCenter);
		blank.SetAttribute("ProportionalControl", _proportional);
		blank.SetAttribute("Threshold", _threshold);
		blank.SetAttribute("UpdatePeriod", _periodMS);
		return blank;
	}

	@Override
	public void MakeCfgClassesNull() {		
	}

	@Override
	public String GetElementTitle() {
		return "ImageProcessing";
	}

}
