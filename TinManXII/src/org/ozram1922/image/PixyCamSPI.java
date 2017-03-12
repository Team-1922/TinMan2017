package org.ozram1922.image;

import edu.wpi.first.wpilibj.SerialPort.Port;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.SerialPort;

public class PixyCamSPI {
	
	//Constants
	
	public static final int ArraySize = 100;
	public static final short StartWord = (short) 0xaa55;
	public static final short StartWordCC = (short) 0xaa56;
	public static final short StartWordX = 0x55aa;
	public static final byte ServoSync = (byte) 0xff;
	public static final byte CamBrightnessSync = (byte) 0xfe;
	public static final byte LEDSync = (byte) 0xfd;
	public static final int OutBufSize = 64;
	
	public static final byte SyncByte = 0x5a;
	public static final byte SyncByteData = 0x5b;
	
	//subclasses
	public class PixyCamBlock
	{
		public short Signature;
		public short X;
		public short Y;
		public short Width;
		public short Height;
		public short Angle;
		
		public PixyCamBlock(short[] words)
		{
			if(words.length < 5)
				return;//failure, but don't log
			Signature = words[0];
			X = words[1];
			Y = words[2];
			Width = words[3];
			Height = words[4];
			if(words.length < 6)
				Angle = 0;
			else
				Angle = words[5];
		}
		
		public static final int WordCount = 6;
	}
	
	public enum BlockType
	{
		kNormal,
		kColorCode
	}
	
	private Thread _worker;
	private PixyCamSPI()
	{
		//_worker = new Thread(new Worker());
		_pixy = new SerialPort(192000, _port);
		_pixy.setReadBufferSize(2);//read words
	}
	
	public static void Init()
	{
		if(_instance != null)
			return;
		_instance = new PixyCamSPI();
	}
	
	private static PixyCamSPI _instance = null;
	public static PixyCamSPI getInstance()
	{
		if(_instance == null)
			Init();
		return _instance;
	}
	
	public ArrayList<PixyCamBlock> GetFrameBlocks()
	{
		return null;
	}
	
	public int SetBrightness(int brightness)
	{
		byte[] bytes = new byte[3];
		bytes[0] = 0;
		bytes[1] = CamBrightnessSync;
		bytes[2] = (byte) brightness;
		return _pixy.write(bytes, 3);
	}
	
	public int SetLED(int r, int g, int b)
	{
		byte[] bytes = new byte[5];
		bytes[0] = 0;
		bytes[1] = LEDSync;
		bytes[2] = (byte)r;
		bytes[3] = (byte)g;
		bytes[4] = (byte)b;
		
		return _pixy.write(bytes, 5);
	}

	private SerialPort _pixy;
	Port _port = Port.kMXP;
	BlockType _blockType;
	
	boolean _skipStart = true;
	
	//@Override
	public void run() {
	}
	
	private ArrayList<PixyCamBlock> GetBlocks(int maxBlocks)
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
			checksum = GetWord();
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
			
			short[] words = new short[6];
			for(int j = 0; j < PixyCamBlock.WordCount; ++j)
			{
				if(_blockType == BlockType.kNormal && i >= 5)//no angle for normal mode
				{
					words[5] = 0;
					break;
				}
				w = GetWord();
				sum += w;
				words[j] = w;
			}
			
			//check checksum
			if(checksum == sum)
				ret.add(new PixyCamBlock(words));
			else
			{
				//TODO: error string
			}
			
			w = GetWord();
			if(w==StartWord)
				_blockType = BlockType.kNormal;
			else if(w == StartWordCC)
				_blockType = BlockType.kColorCode;
			else
				return ret;
			
		}
		return null;
	}
	
	private int GetStart()
	{
		short w, lastw;
		lastw = (short)0xffff;
		
		while(true)
		{
			w = GetWord();
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
				_pixy.read(1);//out of sync
			}
			lastw = w;
		}
	}
	
	private short GetWord()
	{
		byte[] word = _pixy.read(2);
		short ret = word[0];
		ret <<= 8;
		ret |= word[1];
		return ret;
	}
}
