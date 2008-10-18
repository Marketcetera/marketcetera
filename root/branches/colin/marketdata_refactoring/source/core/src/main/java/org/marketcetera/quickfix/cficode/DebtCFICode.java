package org.marketcetera.quickfix.cficode;

public class DebtCFICode extends CFICode {
	
	public DebtCFICode() {
		super(CATEGORY_DEBT+"XXXXX"); //$NON-NLS-1$
	}

	public DebtCFICode(CharSequence codes) {
		super(codes);
	}

	@Override
	public boolean isValid() {
		// not yet implemented
		return false;
	}
	
}
