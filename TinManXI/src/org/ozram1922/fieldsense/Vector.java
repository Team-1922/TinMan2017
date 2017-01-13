package org.ozram1922.fieldsense;

public interface Vector<Type> {
	Type ValueAt(int index);
	int GetCount();
	Vector<Type> AddTo(Vector<Type>);
	Vector<Type> SubtractFrom(Vector<Type>);
	Type Dot(Vector<Type>);
}