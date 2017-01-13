package org.ozram1922.fieldsense;

public class VectorIntegrater<IntegraterType extends IntegrationTarget> {

	private IntegraterType[] _integraters;
	private int _count;
	
	public VectorIntegrater(IntegraterType[] integraters)
	{
		_integraters = integraters;
		_count = integraters.length;
	}
	
	public void CycleEuclid(double[] dPdt) throws Exception
	{
		if(dPdt.length != _count)
			throw new Exception("Invalid Number of Vector Quantities");
		for(int i = 0; i < _count; ++i)
		{
			_integraters[i].Cycle(dPdt[i]);
		}
	}
	
	//TODO add vector class
}
