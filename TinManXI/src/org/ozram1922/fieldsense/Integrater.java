package org.ozram1922.fieldsense;

public class Integrater<TargetType extends IntegrationTarget> implements IntegrationTarget{

	private TargetType _integrationTarget;	
	private long _prevTime = 0;
	
	public Integrater(TargetType target)
	{
		_integrationTarget = target;
	}
	
	@Override
	public void Cycle(double dxdt)
	{
		long now = System.nanoTime();
		long dt = now - _prevTime;
		_integrationTarget.Cycle(dxdt * (double)dt);
		_prevTime = now;
	}

	@Override
	public double GetValue() {
		return _integrationTarget.GetValue();
	}
}
