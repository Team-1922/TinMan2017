package org.ozram1922.image;

import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.networktables.NetworkTable;

import java.util.ArrayList;
import java.util.TimerTask;

import org.usfirst.frc.team1922.robot.commands.auto.ClearNetTables;

/**
 * Class for accessing the PixyCam at a high level.
 * 
 * @author Kevin Mackenzie
 *
 */
public class PixyCam {


	/*
	 * 
	 * Constants
	 * 
	 */

	/**
	 * The 16 bits that indicate the start of a block
	 */
	public static final short StartWord = (short)0xaa55;
	/**
	 * The 16 bits that indicate the start of a color code block
	 */
	public static final short StartWordCC = (short)0xaa56;
	/**
	 * The 16 bits that indicate the start of a block offset by 1 byte
	 */
	public static final short StartWordBigEndianX = (short)0x55; //this is NOT for all endians
	public static final short StartWordLittleEndianX = (short)0x5500;
	/**
	 * The 8 bits that indicate the start of a servo request (send)
	 */
	public static final byte ServoSync = (byte)0xff;
	/**
	 * The 8 bits that indicate the start of a camera brightness request (send)
	 */
	public static final byte CamBrightnessSync = (byte)0xfe;
	/**
	 * The 8 bits that indicate the start of a LED color request (send)
	 */
	public static final byte LEDSync = (byte)0xfd;
	
	//TODO: add methods for relative frames positions that work well with PID controllers
	public static final int FrameWidth = 0;
	public static final int FrameHeight = 0;
	public static final int FrameXCenter = 0;
	public static final int FrameYCenter = 0;
	
	/**
	 * The two kinds of blocks the PixyCam can send
	 * @author Kevin Mackenzie
	 *
	 */
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
	
	/**
	 * the wrapper instance to convert low level data calls to usable sets of bytes/words
	 */
	PixyCamWrapper _wrapper = null;

	/**
	 * The worker thread for getting frame updates
	 */
	java.util.Timer _worker = null;

	/**
	 * The worker task instance for getting frame updates
	 */
	Worker _workerTask = null;
	
	/*
	 * 
	 * Constructors
	 * 
	 */
	
	/**
	 * Creates in an instace of the {@link PixyCam} class
	 * @param wrapper the low-level interface to use to access the PixyCam
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
	
	/**
	 * Converts a series of words to a {@link PixyCamBlock}
	 * @param words the words to convert
	 * @return a {@link PixyCamBlock} instance
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
		else
			ret.Angle = 0;
		
		return ret;
	}
	
	/**
	 * Converts an unsigned word to a signed int.  Since java doesn't support
	 * unsigned data, this transforms unsigned 16 bit data to usable 32 bit data
	 * 
	 * @param word the 16 bits to make unsigned
	 * @return the positive integer representation of the given 16 bits
	 */
	public static int UnsignedWordToSignedInt(short word)
	{
		return word & 0xffff;
	}
	
	/*
	 * 
	 * Public Methods
	 * 
	 */
	/**
	 * Gets a copy of the active frame
	 * @param oldFrame used to check if a new frame has been published
	 * @return a copy of the active frame, may be the same as {@link oldFrame}
	 */
	public PixyCamFrame GetFrame(PixyCamFrame oldFrame)
	{
		return _workerTask.GetFrame(oldFrame);
	}
	
	/**
	 * Gets a copy of the active frame
	 * @return a copy of the active frame
	 */
	public PixyCamFrame GetFrame()
	{
		return _workerTask.GetFrame();
	}
	
	/**
	 * Set the brightness setting on the camera.  Essentially used to control
	 * exposure setting
	 * @param brightness the unsigned byte (0-255) to set the brightness to
	 */
	public void SetBrightness(byte brightness)
	{
		byte[] bytes = new byte[3];
		bytes[0] = 0;
		bytes[1] = (byte)CamBrightnessSync;
		bytes[2] = brightness;
		_wrapper.Send(bytes);
	}
	
	/**
	 * Sets the color of the LED on the PixyCam
	 * @param r the red channel
	 * @param g the green channel
	 * @param b the blue channel
	 */
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
	
	/**
	 * Start the update loop
	 * @param periodMS how frequently the update loop should be called
	 * @param debug whether to output debug information to the network tables
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
	
	/**
	 * Stop the update loop
	 */
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
	
	/**
	 * The background worker task for getting updates from
	 * the PixyCam
	 * 
	 * @author Kevin Mackenzie
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
		/**
		 * Gets a copy of the active frame.  Data returned
		 * is independent from the internal active frame
		 * 
		 * @return a copy of the active frame
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
		
		/**
		 * Creates an instance of the {@link Worker} class
		 * @param debug whether to output debug information to the network tables
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
		
		/**
		 * Gets a copy of the active frame
		 * @param oldFrame the last known frame
		 * @return a copy of the active frame, returns {@link oldFrame} if no update has occurred
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
		
		/**
		 * Gets a copy of the active frame
		 * @return a copy of the active frame
		 */
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
		
		/**
		 * A debug method to output the frame information to the network tables
		 * @param activeFrame the frame to output
		 */
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

		/**
		 * The type of the active block.
		 */
		BlockType _blockType;	
		
		/**
		 * Whether to skip the start byte test for the next block.
		 */
		boolean _skipStart = false;
		
		/**
		 * Gets the blocks from the next frame.  Note that this is a
		 * direct port of the code on the PixyCam wiki
		 * 
		 * @param maxBlocks the maximum number of blocks to read
		 * @return a list of the blocks read from the PixyCam
		 */
		private ArrayList<PixyCamBlock> ReadBlocks(int maxBlocks)
		{
			ArrayList<PixyCamBlock> ret = new ArrayList<PixyCamBlock>();
			
			//start by refreshing the buffer of the wrapper
			_wrapper.RefreshBuffer();
			
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
					if(_blockType == BlockType.kNormal && j >= (PixyCamBlock.WordCount - 1))//no angle for normal mode
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
		
		/**
		 * Checks for the Start Code (either two {@link StartWord}s in a row, or one {@link StartWord} and one {@link StartWordCC})
		 * @return
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
				else if(w == StartWordBigEndianX || w == StartWordLittleEndianX)
				{
					_wrapper.GetByte(); // we're out of sync
					w = StartWord;
				}
				lastw = w;
			}
		}
	}
}
