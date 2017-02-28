package org.ozram1922.autonomous;

import org.ozram1922.Vector2d;

public class VectorAutoPlayback {

	private AutoPlayback _xPlayback;
	private AutoPlayback _yPlayback;
	
	public VectorAutoPlayback(AutoPlayback xPlayback, AutoPlayback yPlayback)
	{
		_xPlayback = xPlayback;
		_yPlayback = yPlayback;
	}
	
	public void StartPlayback()
	{
		_xPlayback.StartPlayback();
		_yPlayback.StartPlayback();
	}
	
	public Vector2d GetSetpoint()
	{
		return new Vector2d(_xPlayback.GetSetpoint(), _yPlayback.GetSetpoint());
	}
	
	public boolean IsFinished()
	{
		return _xPlayback.IsFinished() || _yPlayback.IsFinished();
	}
}
