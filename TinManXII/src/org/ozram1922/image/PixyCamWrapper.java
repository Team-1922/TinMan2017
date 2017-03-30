package org.ozram1922.image;

public interface PixyCamWrapper {
	byte GetByte();
	short GetWord();
	void Send(byte[] data);
	byte[] ViewBuffer();
}
