package org.ozram1922.image;

import edu.wpi.first.wpilibj.SPI;
import java.util.ArrayList;

public class PixyCamSPI {
	
	//Constants
	
	public static final int ArraySize = 100;
	public static final int StartWord = 0xaa55;
	public static final int StartWordCC = 0xaa56;
	public static final int StartWordX = 0x55aa;
	public static final int ServoSync = 0xff;
	public static final int CamBrightnessSync = 0xfe;
	public static final int LEDSync = 0xfd;
	public static final int OutBufSize = 64;
	
	public static final byte SyncByte = 0x5a;
	public static final byte SyncByteData = 0x5b;
	
	//subclasses
	public static PixyCamBlock BlockFromWords(short[] words)
	{
		PixyCamBlock ret = new PixyCamBlock();
		if(words.length < PixyCamBlock.WordCount - 1)
			return null;
		ret.Signature = words[0];
		ret.X = words[1];
		ret.Y = words[2];
		ret.Width = words[3];
		ret.Height = words[4];

		if (words.length < PixyCamBlock.WordCount)
			ret.Angle = words[5];
		
		return ret;
	}
	
	private static int BytesToInt(byte[] data, int offset)
	{
		//note that the data will be in 16 bit numbers
		int ret = data[offset];
		ret <<= 8;
		ret |= data[offset + 1];
		return ret;
	}
	
	public enum BlockType
	{
		kNormal,
		kColorCode
	}
	
	private PixyCamSPI()
	{
		//_worker = new Thread(new Worker());
		_pixy = new SPI(_port);
		//_pixy.setReadBufferSize(2);//read words
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
	
	public int SetBrightness(byte brightness)
	{
		byte[] bytes = new byte[3];
		bytes[0] = 0;
		bytes[1] = (byte)CamBrightnessSync;
		bytes[2] = brightness;
		return _pixy.write(bytes, 3);
	}
	
	public int SetLED(byte r, byte g, byte b)
	{
		byte[] bytes = new byte[5];
		bytes[0] = 0;
		bytes[1] = (byte) LEDSync;
		bytes[2] = r;
		bytes[3] = g;
		bytes[4] = b;
		
		return _pixy.write(bytes, 5);
	}

	private SPI _pixy;
	SPI.Port _port = SPI.Port.kOnboardCS0;
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
				ret.add(BlockFromWords(words));
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
				byte[] d = new byte[1];
				_pixy.read(true, d, 1);//out of sync
			}
			lastw = w;
		}
	}
	
	private short GetWord()
	{
		byte[] word = new byte[2];
		int result = _pixy.read(false, word, 2);
		short ret = word[0];
		ret <<= 8;
		ret |= word[1];
		return ret;
	}
}
