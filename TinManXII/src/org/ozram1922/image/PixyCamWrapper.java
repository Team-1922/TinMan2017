package org.ozram1922.image;

/**
 * The interface for the different interfaces that the PixyCam supports
 *  
 * @author Kevin Mackenzie
 */
public interface PixyCamWrapper {
	/**
	 * Removes the current buffer and pulls a fresh one down from the PixyCam.
	 * Typically this is called once each update cycle
	 */
	void RefreshBuffer();
	
	/**
	 * @return The next byte that the PixyCam sent
	 */
	byte GetByte();
	
	/**
	 * @return
	 */
	short GetWord();
	
	/**
	 * Sends data to the PixyCam
	 * 
	 * @param data A byte buffer to be sent to the PixyCam
	 */
	void Send(byte[] data);
	
	/**
	 * Allows users to see the internal byte buffer.  Only for debugging purposes.
	 * @return the internal byte buffer cached from the PixyCam
	 */
	byte[] ViewBuffer();
}
