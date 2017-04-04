package org.usfirst.frc.team1922.robot.subsystems;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import org.ozram1922.cfg.CfgDocument;
import org.ozram1922.cfg.CfgElement;
import org.ozram1922.cfg.CfgInterface;
import org.ozram1922.image.PixyCam;
import org.ozram1922.image.PixyCamBlock;
import org.ozram1922.image.PixyCamFrame;
import org.ozram1922.image.PixyI2CWrapper;
import org.usfirst.frc.team1922.robot.commands.RunPixyCam;
import org.usfirst.frc.team1922.robot.commands.auto.ClearNetTables;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

/**
 * The game-specific processing class for the data coming from the Pixy-Cam
 * 
 * @author Kevin Mackenzie
 *
 */
public class PixyCamProcessing extends Subsystem implements CfgInterface {

	/*
	 * 
	 * Data Members
	 * 
	 */
	/**
	 * The minimum width for a detected block
	 */
	private int _minWidth = 10;
	/**
	 * The minimum height for a detected block
	 */
	private int _minHeight = 20;
	/**
	 * The x center of the window (in pixels)
	 */
	private int _windowXCenter = 160;
	/**
	 * The proportional constant (Volts/pixel)
	 */
	private double _proportional = 0.0;
	/**
	 * The threshold of acceptable difference from center (+/-)
	 */
	private int _threshold = 5;
	/**
	 * The update period length in milliseconds
	 */
	private int _periodMS = 100;
	/**
	 * Whether to output debug information to the network tables
	 */
	private boolean _debug = false;
	
	
	/*
	 * Runtime Variables
	 */
	
	/**
	 * The Medium-level PixyCam interface
	 */
	private PixyCam _pixyCam;
	/**
	 * The active frame from the PixyCam
	 */
	private PixyCamFrame _frame = null;
	/**
	 * The x position of the target on screen
	 */
	private int _targetXPosition = 0;
	/**
	 * The y position of the target on screen
	 */
	private int _targetYPosition = 0;
	/**
	 * Whether the PixyCam sees the target
	 */
	private boolean _seesTarget = false;
	
	private int _condensedBlockCount = 0;
	
	
	/*
	 * 
	 * Constructors
	 * 
	 */
	/**
	 * Creates an instance of the {@link PixyCamProcessing} class
	 */
	public PixyCamProcessing()
	{
		_pixyCam = new PixyCam(new PixyI2CWrapper());
	}
	
	/*
	 * 
	 * Control Methods
	 * 
	 */
	/**
	 * Gets the next frame and updates the target position information
	 */
	public void UpdateFrame()
	{
		PixyCamFrame nextFrame = _pixyCam.GetFrame(_frame);
		if(nextFrame == null)
			return;
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
				
				boolean wasCombined = false;
				
				//combine vertically
				for(int j = 0; j < blocks.size(); j++)
				{
					//if the two blocks are within 5 pixels, then add to them.  its probably a duplicate (or cut in half by the hook)
					if(Math.abs(blocks.get(j).X - _frame.List.get(i).X) < 5)
					{
						PixyCamBlock storedBlock = blocks.get(j);
						PixyCamBlock newBlock = _frame.List.get(i);
						
						storedBlock.ExpandToFit(newBlock);
						
						// expand the rect vertically to include the full size of both
						//int storedBottom = storedBlock.Y + storedBlock.Height;
						//int newBottom = newBlock.Y + newBlock.Height;
						//storedBlock.Y = Math.min(storedBlock.Y, newBlock.Y);
						//storedBlock.Height = Math.max(storedBottom, newBottom) - storedBlock.Y;
						
						//TODO: this might not be the best strategy for X values
						//average the X positions in case angle is off
						//storedBlock.X = (storedBlock.X + newBlock.X) / 2;
						
						wasCombined = true;
						break;
					}
					else if(blocks.get(j).ContainsOther(_frame.List.get(i)))
					{
						blocks.get(j).ExpandToFit(_frame.List.get(i));
						wasCombined = true;
						break;
					}
				}

				if(!wasCombined)
				{
					//if not within 5 pixels, then add and break;
					blocks.add(_frame.List.get(i));
				}
			}
			
			_condensedBlockCount = blocks.size();
			
			//how many blocks do we have?
			if(blocks.size() < 1)
			{
				_seesTarget = false;
				_targetXPosition = 0;
				_targetYPosition = 0;
				return;
			}
			
			if(blocks.size() == 1)
			{
				PixyCamBlock block = blocks.get(0);
				_targetXPosition = block.X + block.Width/2;
				_targetYPosition = block.Y;
				_seesTarget = true;
			}
			else
			{
			
				
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
				
				//enforce a minimum width and Height (so off-angle or far-away detections don't count.  This is especially important for tele-op drive assist)
				if(bigBlock1.Width < _minWidth || bigBlock2.Width < _minWidth || bigBlock1.Height < _minHeight || bigBlock2.Height < _minHeight)
				{
					//TODO: Test everything else before adding this
					//_seesTarget = false;
					//return;
				}
				
				//get the midpoint (center) of the two X & Y Positions
				_targetXPosition = (bigBlock1.X + bigBlock2.X) / 2;
				_targetYPosition = (bigBlock1.Y + bigBlock2.Y) / 2;
			}
			
			//now shift the X to be relative to the center of the screen
			_targetXPosition -= _windowXCenter;
		}
	}
	
	
	class OutputWorker extends TimerTask
	{

		NetworkTable _netTableDT = NetworkTable.getTable("PixyProcessing");
		int i = 0;
		@Override
		public void run() {
			UpdateFrame();
			_netTableDT.putNumber("Block Count", _condensedBlockCount);
			_netTableDT.putNumber("DT Twist", (double)_targetXPosition * _proportional);
			_netTableDT.putNumber("Center", _targetXPosition);
			i++;
			_netTableDT.putNumber("I", i);
		}
		
	}
	
	Timer _outputWorker = new Timer();
	
	/**
	 * Start the PixyCam processing loop
	 */
	public void Start()
	{
		_pixyCam.Start(_periodMS, true);
		_outputWorker.schedule(new OutputWorker(), 0, _periodMS);
	}
	
	/**
	 * Stop the PixyCam processing loop
	 */
	public void Stop()
	{
		_pixyCam.Stop();
		_outputWorker.cancel();
	}


	
	/**
	 * Typically the result should be divided by two and added/subtracted to each side
	 * @return the Percent V-Bus twist factor (positive = Clockwise; negative = Counter-Clockwise)
	 */
	public double GetDTTwist()
	{
		UpdateFrame();	
		if(!TargetInView())
			return 0;
		if(IsOnTarget())
			return 0;
		return _targetXPosition * _proportional;
	}	
	
	/**
	 * 
	 * @return The number of pixels away from the assigned center the target is
	 */
	public int GetPixelsOffCenter()
	{
		UpdateFrame();
		if(!TargetInView())
			return Integer.MAX_VALUE;
		return _targetXPosition;
	}
	
	/**
	 * 
	 * @return Whether the target is within the threshold
	 */
	public boolean IsOnTarget()
	{
		UpdateFrame();
		if(!TargetInView())
			return true;
		return Math.abs(_targetXPosition) < _threshold;
	}

	/**
	 * 
	 * @return Whether the camera has established a target
	 */
	public boolean TargetInView()
	{
		return _seesTarget;
	}
	
	/**
	 * Try not to call this too frequently in performance-sensitive situations
	 * 
	 * @return a copy of the active frame
	 */
	public PixyCamFrame GetActiveFrame()
	{
		UpdateFrame();
		return _pixyCam.GetFrame();
	}

	
	/*
	 * 
	 * Subsystem Methods
	 * 
	 */

	@Override
	protected void initDefaultCommand() {
		setDefaultCommand(new RunPixyCam());
	}
	
	
	/*
	 * 
	 * CfgInterface Methods
	 * 
	 */
	@Override
	public boolean Deserialize(CfgElement element) {

		_windowXCenter = element.GetAttributeI("CameraCenter");
		_proportional = element.GetAttributeD("ProportionalControl");
		_threshold = element.GetAttributeI("Threshold");
		_periodMS = element.GetAttributeI("UpdatePeriod");
		_minWidth = element.GetAttributeI("MinWidth");
		_minHeight = element.GetAttributeI("MinHeight");
		_debug = element.GetAttributeI("Debug") > 0;
		return true;
	}

	@Override
	public CfgElement Serialize(CfgElement blank, CfgDocument doc) {

		blank.SetAttribute("CameraCenter", _windowXCenter);
		blank.SetAttribute("ProportionalControl", _proportional);
		blank.SetAttribute("Threshold", _threshold);
		blank.SetAttribute("UpdatePeriod", _periodMS);
		blank.SetAttribute("MinWidth", _minWidth);
		blank.SetAttribute("MinHeight", _minHeight);
		blank.SetAttribute("Debug", _debug ? 1 : 0);
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
