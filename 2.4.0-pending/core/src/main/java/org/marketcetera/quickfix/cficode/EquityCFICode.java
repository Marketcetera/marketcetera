package org.marketcetera.quickfix.cficode;

public class EquityCFICode extends CFICode {

	public EquityCFICode() {
		super(CATEGORY_EQUITY+"XXXXX"); //$NON-NLS-1$
	}

	public EquityCFICode(CharSequence codes) {
		super(codes);
	}

	@Override
	public boolean isValid() {
		// not yet implemented
		return false;
	}
	
}
