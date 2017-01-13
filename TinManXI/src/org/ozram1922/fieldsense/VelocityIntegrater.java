package org.ozram1922.fieldsense;

public class VelocityIntegrater extends Integrater<IntegrationTargetDouble>{

	public VelocityIntegrater(double initialPosition) {
		super(new IntegrationTargetDouble(initialPosition));
	}

}
