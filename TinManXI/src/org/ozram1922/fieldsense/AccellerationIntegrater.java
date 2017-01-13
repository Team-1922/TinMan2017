package org.ozram1922.fieldsense;

public class AccellerationIntegrater extends Integrater<Integrater<IntegrationTargetDouble>>{

	public AccellerationIntegrater(double initialPosition, double initialVelocity) {
		super(new VelocityIntegrater(initialPosition));
	}
}
