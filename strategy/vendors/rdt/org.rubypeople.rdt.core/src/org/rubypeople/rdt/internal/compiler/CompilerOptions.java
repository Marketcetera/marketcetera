package org.rubypeople.rdt.internal.compiler;

import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.core.RubyCore;

public class CompilerOptions {

	public static final long EmptyStatement = 0x01;
	public static final long ConstantReassignment = 0x02;
	public static final long UnreachableCode = 0x04;
	public static final long CoreClassMethodRedefinition = 0x08;
	public static final long Ruby19WhenStatements = 0x10;
	public static final long Ruby19HashCommaSyntax = 0x20;
	
	public static final String ERROR = RubyCore.ERROR; //$NON-NLS-1$
	public static final String WARNING = RubyCore.WARNING; //$NON-NLS-1$
	public static final String IGNORE = RubyCore.IGNORE; //$NON-NLS-1$
	
//	 Default severity level for handlers
	public long errorThreshold = 0;
	public long warningThreshold = 
		ConstantReassignment | 
		UnreachableCode |
		Ruby19WhenStatements |
		Ruby19HashCommaSyntax;
	
	public Map getMap() {
		Map<String, String> optionsMap = new HashMap<String, String>(30);
		optionsMap.put(RubyCore.COMPILER_PB_EMPTY_STATEMENT, getSeverityString(EmptyStatement));
		optionsMap.put(RubyCore.COMPILER_PB_CONSTANT_REASSIGNMENT, getSeverityString(ConstantReassignment));
		optionsMap.put(RubyCore.COMPILER_PB_UNREACHABLE_CODE, getSeverityString(UnreachableCode));
		optionsMap.put(RubyCore.COMPILER_PB_REDEFINITION_CORE_CLASS_METHOD, getSeverityString(CoreClassMethodRedefinition));
		optionsMap.put(RubyCore.COMPILER_PB_RUBY_19_WHEN_STATEMENTS, getSeverityString(Ruby19WhenStatements));
		optionsMap.put(RubyCore.COMPILER_PB_RUBY_19_HASH_COMMA_SYTNAX, getSeverityString(Ruby19HashCommaSyntax));
		return optionsMap;
	}
	
	public String getSeverityString(long irritant) {
		if((this.warningThreshold & irritant) != 0)
			return WARNING;
		if((this.errorThreshold & irritant) != 0)
			return ERROR;
		return IGNORE;
	}
	
	public void set(Map optionsMap) {
		Object optionValue;
		if ((optionValue = optionsMap.get(RubyCore.COMPILER_PB_EMPTY_STATEMENT)) != null) updateSeverity(EmptyStatement, optionValue);
		if ((optionValue = optionsMap.get(RubyCore.COMPILER_PB_CONSTANT_REASSIGNMENT)) != null) updateSeverity(ConstantReassignment, optionValue);
		if ((optionValue = optionsMap.get(RubyCore.COMPILER_PB_UNREACHABLE_CODE)) != null) updateSeverity(UnreachableCode, optionValue);
		if ((optionValue = optionsMap.get(RubyCore.COMPILER_PB_REDEFINITION_CORE_CLASS_METHOD)) != null) updateSeverity(CoreClassMethodRedefinition, optionValue);
		if ((optionValue = optionsMap.get(RubyCore.COMPILER_PB_RUBY_19_WHEN_STATEMENTS)) != null) updateSeverity(Ruby19WhenStatements, optionValue);
		if ((optionValue = optionsMap.get(RubyCore.COMPILER_PB_RUBY_19_HASH_COMMA_SYTNAX)) != null) updateSeverity(Ruby19HashCommaSyntax, optionValue);
	}
	
	void updateSeverity(long irritant, Object severityString) {
		if (ERROR.equals(severityString)) {
			this.errorThreshold |= irritant;
			this.warningThreshold &= ~irritant;
		} else if (WARNING.equals(severityString)) {
			this.errorThreshold &= ~irritant;
			this.warningThreshold |= irritant;
		} else if (IGNORE.equals(severityString)) {
			this.errorThreshold &= ~irritant;
			this.warningThreshold &= ~irritant;
		}
	}	

}
