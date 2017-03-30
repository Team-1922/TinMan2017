package org.ozram1922.image;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import java.util.ArrayList;
import java.util.TimerTask;

import org.usfirst.frc.team1922.robot.commands.auto.ClearNetTables;

public class PixyCam {


	/*
	 * 
	 * Constants
	 * 
	 */
	
	//public static final int ArraySize = 100;
	public static final short StartWord = (short)0xaa55;
	public static final short StartWordCC = (short)0xaa56;
	public static final short StartWordX = (short)0x55aa;
	public static final byte ServoSync = (byte)0xff;
	public static final byte CamBrightnessSync = (byte)0xfe;
	public static final byte LEDSync = (byte)0xfd;
	
	//TODO: add methods for relative frames positions that work well with PID controllers
	public static final int FrameWidth = 0;
	public static final int FrameHeight = 0;
	public static final int FrameXCenter = 0;
	public static final int FrameYCenter = 0;
	
	public enum BlockType
	{
		kNormal,
		kColorCode
	}
	
	/*
	 * 
	 * Member Data
	 * 
	 */
	
	//the instance to convert SPI data calls to cooperate with the PixyCam
	PixyCamWrapper _wrapper = null;
	
	//the worker thread for getting updates
	java.util.Timer _worker = null;
	
	//the worker task
	Worker _workerTask = null;
	
	/*
	 * 
	 * Constructors
	 * 
	 */
	
	public PixyCam(PixyCamWrapper wrapper)
	{
		_wrapper = wrapper;
		
	}
	
	/*
	 * 
	 * Singleton Methods 
	 * 
	 */
	
	/*private static PixyCamSPI _instance = null;
	public static PixyCamSPI getInstance()
	{
		if(_instance == null)
			Init();
		return _instance;
	}
	
	public static void Init()
	{
		if(_instance != null)
			return;
		_instance = new PixyCamSPI(SPI.Port.kOnboardCS0);
	}*/
	
	/*
	 * 
	 * Conversion Methods
	 * 
	 */
	
	public static PixyCamBlock BlockFromWords(short[] words)
	{
		PixyCamBlock ret = new PixyCamBlock();
		if (words.length < PixyCamBlock.WordCount - 1)
			return null;
		ret.Signature = UnsignedWordToSignedInt(words[0]);
		ret.X = UnsignedWordToSignedInt(words[1]);
		ret.Y = UnsignedWordToSignedInt(words[2]);
		ret.Width = UnsignedWordToSignedInt(words[3]);
		ret.Height = UnsignedWordToSignedInt(words[4]);

		if (words.length == PixyCamBlock.WordCount)
			ret.Angle = UnsignedWordToSignedInt(words[5]);
		
		return ret;
	}
	
	public static int UnsignedWordToSignedInt(short word)
	{
		return word & 0xffff;
	}
	
	/*
	 * 
	 * Public Methods
	 * 
	 */
	
	public PixyCamFrame GetFrame(PixyCamFrame oldFrame)
	{
		return _workerTask.GetFrame(oldFrame);
	}
	
	public PixyCamFrame GetFrame()
	{
		return _workerTask.GetFrame();
	}
	
	public void SetBrightness(byte brightness)
	{
		byte[] bytes = new byte[3];
		bytes[0] = 0;
		bytes[1] = (byte)CamBrightnessSync;
		bytes[2] = brightness;
		_wrapper.Send(bytes);
	}
	
	public void SetLED(byte r, byte g, byte b)
	{
		byte[] bytes = new byte[5];
		bytes[0] = 0;
		bytes[1] = (byte) LEDSync;
		bytes[2] = r;
		bytes[3] = g;
		bytes[4] = b;
		
		_wrapper.Send(bytes);
	}
	
	/*
	 * 
	 * Thread Control Methods
	 * 
	 */
	
	public void Start(int periodMS, boolean debug)
	{
		if(_worker == null && periodMS > 0)
		{
			_worker = new java.util.Timer();
			_workerTask = new Worker(debug);
			_worker.schedule(_workerTask, 0L, periodMS);
		}
	}
	
	public void Stop()
	{
		if(_worker != null)
		{
			_worker.cancel();
			synchronized(this)
			{
				_worker = null;
				_workerTask = null;
			}
		}
	}

	/*
	 * 
	 * Worker Thread
	 * 
	 */
	

	class Worker extends TimerTask
	{
		/*
		 * 
		 * Data
		 * 
		 */
		private ArrayList<PixyCamBlock> _blocks = new ArrayList<PixyCamBlock>();
		private int _frameId = 0;
		private boolean _debug = false;
		
		/*
		 * 
		 * Data Copier
		 * 
		 */
		private PixyCamFrame CopyOfFrame()
		{
			PixyCamFrame copy;
			synchronized(this)
			{
				copy = new PixyCamFrame(_frameId);
				for(PixyCamBlock item : _blocks)
				{
					copy.List.add(item.clone());
				}
			}
			return copy;
		}
		
		/*
		 * 
		 * Constructor
		 * 
		 */
		
		public Worker(boolean debug)
		{
			_debug = debug;
			ClearNetTables.RecursiveTableDelete(_netTable);
		}
		
		/*
		 * 
		 * Public Access of Data
		 * 
		 */
		
		public PixyCamFrame GetFrame(PixyCamFrame oldFrame)
		{
			synchronized(this)
			{
				if(oldFrame.IsDifferent(_frameId))
					return oldFrame;
			}
			return CopyOfFrame();
		}
		
		public PixyCamFrame GetFrame()
		{
			return CopyOfFrame();
		}
		
		/*
		 * 
		 * Worker Method
		 * 
		 */
		int number = 0;
		@Override
		public void run() {			
			//number++;
			//_netTable.putNumber("Test Number", number);
			ArrayList<PixyCamBlock> blocks = ReadBlocks(10);
			if(blocks == null)
				return;
			//ClearNetTables.RecursiveTableDelete(_netTable);
			synchronized(this)
			{
				_frameId ++;
				_blocks = blocks;
				//_netTable.putNumber("Test Number", _frameId);
				//_netTable.putNumber("Word", _wrapper.GetWord());
				/*byte[] bytes = _spiWrapper.Get2Bytes();
				for(int i = 0; i < bytes.length; ++i)
				{
					_netTable.putNumber("Byte" + i, bytes[i]);
				}*/
			}
			//for(int i = 0; i < 32; ++i)
			//{
			//	_netTable.putNumber("Word " + i, _wrapper.GetWord());
			//}
			//byte[] bufferBytes = _wrapper.ViewBuffer();
			//for(int i = 0; i < bufferBytes.length; ++i)
			//{
			//	_netTable.putNumber("Byte " + i, bufferBytes[i]);
			//}
			_debug = true;
			if(_debug)
			{
				PixyCamFrame frame = new PixyCamFrame(_frameId);
				frame.List = _blocks;
				OutputToNetTable(frame);
			}
		}
		
		/*
		 * 
		 * Output Frame to Network Tables
		 * 
		 */
		NetworkTable _netTable = NetworkTable.getTable("PixyCam");
		public void OutputToNetTable(PixyCamFrame activeFrame)
		{
			
			_netTable.putNumber("Frame ID", activeFrame.GetFrameID());
			for(int i = 0; i < activeFrame.List.size(); ++i)
			{
				PixyCamBlock block = activeFrame.List.get(i);
				//NetworkTable subTable = (NetworkTable) _netTable.getSubTable(Integer.toString(i));

				_netTable.putNumber("Block" + i + "Signature", block.Signature);
				_netTable.putNumber("Block" + i + "X", block.X);
				_netTable.putNumber("Block" + i + "Y", block.Y);
				_netTable.putNumber("Block" + i + "Width", block.Width);
				_netTable.putNumber("Block" + i + "Height", block.Height);
				_netTable.putNumber("Block" + i + "Angle", block.Angle);
			}
		}

		/*
		 * 
		 * Fields
		 * 
		 */

		BlockType _blockType;	
		boolean _skipStart = false;
		
		/*
		 * 
		 * Gets Blocks from the Stream
		 * 
		 */
		private ArrayList<PixyCamBlock> ReadBlocks(int maxBlocks)
		{
			ArrayList<PixyCamBlock> ret = new ArrayList<PixyCamBlock>();
			if(!_skipStart)
			{
				if(GetStart() == 0)
					return null;
			}
			else
				_skipStart = false;
			
			

			short w, checksum, sum;
			
			for(int i = 0; i < maxBlocks; ++i)
			{
				sum = 0;
				checksum = _wrapper.GetWord();
				if(checksum == StartWord) //we've reached the beginning of the next frame
				{
					_skipStart = true;
					_blockType = BlockType.kNormal;
					return ret;
				}
				else if(checksum == StartWordCC)
				{
					_skipStart = true;
					_blockType = BlockType.kColorCode;
					return ret;
				}
				else if(checksum == 0)
					return ret;
				
				_netTable.putNumber("Checksum", checksum);
				
				_netTable.putString("Block Type", _blockType.toString());
				
				short[] words = new short[PixyCamBlock.WordCount];
				for(int j = 0; j < PixyCamBlock.WordCount; ++j)
				{
					if(_blockType == BlockType.kNormal && i >= (PixyCamBlock.WordCount - 1))//no angle for normal mode
					{
						words[PixyCamBlock.WordCount - 1] = 0;
						break;
					}
					w = _wrapper.GetWord();
					sum += w;
					words[j] = w;
				}
				
				_netTable.putNumber("Sum", sum);
				//check checksum
				if(checksum == sum)
				{
					PixyCamBlock b = BlockFromWords(words);
					
					//make sure it converts correctly
					if(b != null)
						ret.add(b);
				}
				else
				{
					//TODO: error string
				}
				
				w = _wrapper.GetWord();
				if(w==StartWord)
					_blockType = BlockType.kNormal;
				else if(w == StartWordCC)
					_blockType = BlockType.kColorCode;
				else
					return ret;
				
			}
			return ret;
		}
		
		/*
		 * 
		 * Checks for Start code (two StartCode in a row, or one StartCode one StartCodeCC)
		 * 
		 */
		private int GetStart()
		{
			short w, lastw;
			lastw = (short)0xffff;
			
			while(true)
			{
				w = _wrapper.GetWord();
				if((w == 0) && (lastw == 0))
				{
					_netTable.putString("Block Type", "None");
					return 0; // no start code
				}
				else if(w == StartWord && lastw == StartWord)
				{
					_netTable.putString("Block Type", "Normal");
					_blockType = BlockType.kNormal;
					return 1; // code found
				}
				else if(w == StartWordCC && lastw == StartWord)
				{
					_netTable.putString("Block Type", "Color Code");
					_blockType = BlockType.kColorCode; // color code block
					return 1;
				}
				else if(w == StartWordX)
				{
					_wrapper.GetByte(); // we're out of sync
				}
				lastw = w;
			}
		}
	}
}
