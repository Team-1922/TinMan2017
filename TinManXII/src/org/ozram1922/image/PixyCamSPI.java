package org.ozram1922.image;

import edu.wpi.first.wpilibj.SPI;
import java.util.ArrayList;
import java.util.TimerTask;

public class PixyCamSPI {


	/*
	 * 
	 * Constants
	 * 
	 */
	
	public static final int ArraySize = 100;
	public static final int StartWord = 0xaa55;
	public static final int StartWordCC = 0xaa56;
	public static final int StartWordX = 0x55aa;
	public static final int ServoSync = 0xff;
	public static final int CamBrightnessSync = 0xfe;
	public static final int LEDSync = 0xfd;
	
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
	PixySPIWrapper _spiWrapper;
	
	//the worker thread for getting updates
	java.util.Timer _worker;
	
	//the worker task
	Worker _workerTask;
	
	/*
	 * 
	 * Constructors
	 * 
	 */
	
	public PixyCamSPI(SPI.Port port)
	{
		_spiWrapper = new PixySPIWrapper(port);
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
		if(word < 0)
		{
			//flip sign and add MSB of short
			return -word + 0x8000;
		}
		else
		{
			return word;
		}
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
	
	public int SetBrightness(byte brightness)
	{
		byte[] bytes = new byte[3];
		bytes[0] = 0;
		bytes[1] = (byte)CamBrightnessSync;
		bytes[2] = brightness;
		return _spiWrapper.Send(bytes, 3);
	}
	
	public int SetLED(byte r, byte g, byte b)
	{
		byte[] bytes = new byte[5];
		bytes[0] = 0;
		bytes[1] = (byte) LEDSync;
		bytes[2] = r;
		bytes[3] = g;
		bytes[4] = b;
		
		return _spiWrapper.Send(bytes, 5);
	}
	
	/*
	 * 
	 * Thread Control Methods
	 * 
	 */
	
	public void Start(double period)
	{
		if(_worker == null && period > 0)
		{
			_worker = new java.util.Timer();
			_workerTask = new Worker();
			_worker.schedule(_workerTask, 0L, (long) (period * 1000));
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
		@Override
		public void run() {
			ArrayList<PixyCamBlock> blocks = ReadBlocks(10);
			synchronized(this)
			{
				_frameId ++;
				_blocks = blocks;
			}
		}

		/*
		 * 
		 * Fields
		 * 
		 */

		BlockType _blockType;	
		boolean _skipStart = true;
		
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
				if(GetStart()== 0)
					return null;
			}
			else
				_skipStart = false;
			

			short w, checksum, sum;
			
			sum = 0;
			for(int i = 0; i < maxBlocks; ++i)
			{
				checksum = _spiWrapper.GetWord();
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
				
				short[] words = new short[PixyCamBlock.WordCount];
				for(int j = 0; j < PixyCamBlock.WordCount; ++j)
				{
					if(_blockType == BlockType.kNormal && i >= (PixyCamBlock.WordCount - 1))//no angle for normal mode
					{
						words[PixyCamBlock.WordCount - 1] = 0;
						break;
					}
					w = _spiWrapper.GetWord();
					sum += w;
					words[j] = w;
				}
				
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
				
				w = _spiWrapper.GetWord();
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
				w = _spiWrapper.GetWord();
				if(w == 0 && lastw == 0)
					return 0; // no start code
				else if(w == StartWord && lastw == StartWord)
				{
					_blockType = BlockType.kNormal;
					return 1; // code found
				}
				else if(w == StartWordCC && lastw == StartWord)
				{
					_blockType = BlockType.kColorCode; // color code block
					return 1;
				}
				else if(w == StartWordX)
				{
					_spiWrapper.GetByte((byte)0); // we're out of sync
				}
				lastw = w;
			}
		}
	}
}
