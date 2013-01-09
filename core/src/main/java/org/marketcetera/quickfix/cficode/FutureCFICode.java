package org.marketcetera.quickfix.cficode;

public class FutureCFICode extends CFICode {
	
	public FutureCFICode() {
		super(CATEGORY_FUTURE+"XXXXX"); //$NON-NLS-1$
	}

	public FutureCFICode(CharSequence codes) {
		super(codes);
	}

	@Override
	public boolean isValid() {
		// not yet implemented
		return false;
	}
	
}
