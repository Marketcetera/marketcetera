package org.marketcetera.quickfix.cficode;

public class EntitlementCFICode extends CFICode {
	

	public EntitlementCFICode(CharSequence seq) {
		super(CATEGORY_ENTITLEMENT+"XXXXX");
	}

	@Override
	public boolean isValid() {
		// not yet implemented
		return false;
	}
	
}
