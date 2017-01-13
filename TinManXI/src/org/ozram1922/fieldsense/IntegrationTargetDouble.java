package org.ozram1922.fieldsense;

public class IntegrationTargetDouble implements IntegrationTarget {

	private double _value = 0;
	
	public IntegrationTargetDouble(double initialValue)
	{
		_value = initialValue;
	}
	
	@Override
	public double GetValue() {
		return _value;
	}

	@Override
	public void Cycle(double value) {
		_value += value;
	}

}
