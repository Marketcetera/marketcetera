package org.rubypeople.rdt.internal.ui.text;

import org.eclipse.osgi.util.NLS;

public class TextMessages extends NLS {

		private static final String BUNDLE_NAME = TextMessages.class.getName();
		
		public static String RubyOutlineInformationControl_GoIntoTopLevelType_label;
		public static String RubyOutlineInformationControl_GoIntoTopLevelType_tooltip;
		public static String RubyOutlineInformationControl_GoIntoTopLevelType_description;
		public static String RubyOutlineInformationControl_LexicalSortingAction_label;
		public static String RubyOutlineInformationControl_LexicalSortingAction_tooltip;
		public static String RubyOutlineInformationControl_LexicalSortingAction_description;
		public static String RubyOutlineInformationControl_SortByDefiningTypeAction_label;
		public static String RubyOutlineInformationControl_SortByDefiningTypeAction_description;
		public static String RubyOutlineInformationControl_SortByDefiningTypeAction_tooltip;
		
		static {
			NLS.initializeMessages(BUNDLE_NAME, TextMessages.class);
		}
}
