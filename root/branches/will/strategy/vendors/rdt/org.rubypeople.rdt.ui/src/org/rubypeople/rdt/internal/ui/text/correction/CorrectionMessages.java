package org.rubypeople.rdt.internal.ui.text.correction;

import org.eclipse.osgi.util.NLS;

public class CorrectionMessages extends NLS {

	private static final String BUNDLE_NAME = CorrectionMessages.class.getName();
	
	public static String RubyCorrectionProcessor_error_status;
	public static String RubyCorrectionProcessor_error_quickfix_message;
	public static String MarkerResolutionProposal_additionaldesc;
	public static String ChangeCorrectionProposal_error_title;
	public static String ChangeCorrectionProposal_error_message;
	public static String ChangeCorrectionProposal_name_with_shortcut;
	public static String CUCorrectionProposal_error_title;
	public static String CUCorrectionProposal_error_message;
	public static String NoCorrectionProposal_description;
	public static String RubyCorrectionProcessor_error_quickassist_message;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, CorrectionMessages.class);
	}
	
}
