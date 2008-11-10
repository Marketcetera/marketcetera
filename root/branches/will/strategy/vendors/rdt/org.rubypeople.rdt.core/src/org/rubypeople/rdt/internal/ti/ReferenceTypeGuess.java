package org.rubypeople.rdt.internal.ti;

/**
 * Represents a type guess established by assignment from another variable.  e.g.:
 * 
 * def foo(x)
 *   @y = x
 * end
 * 
 * @author Jason
 *
 */
public class ReferenceTypeGuess implements ITypeGuess {

	private Variable other;
	public ReferenceTypeGuess( Variable other )
	{
		this.other = other;
	}
//todo: how to handle this?  it's one type guess essentially referencing many... you could
	//  copy all type refs. at the time of assignment (y=x), but then you'd lose subsequent
	//  type information.  maybe the link should be represented another way?
	public int getConfidence() {
		// TODO Auto-generated method stub
		return 0;
	}

	public String getType() {
		// TODO Auto-generated method stub
		return null;
	}

}
