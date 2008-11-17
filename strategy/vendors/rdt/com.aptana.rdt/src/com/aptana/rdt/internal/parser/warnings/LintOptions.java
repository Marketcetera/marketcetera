package com.aptana.rdt.internal.parser.warnings;

import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.core.RubyCore;

import com.aptana.rdt.AptanaRDTPlugin;

public class LintOptions {
	
	public static final long UnusedPrivateMember = 0x4;
	public static final long UnusedArgument = 0x10;	
	public static final long UnnecessaryElse = 0x40;
	public static final long SimilarVariableNames = 0x100;
	public static final long MisspelledConstructor = 0x200;
	public static final long PossibleAccidentalBooleanAssignment = 0x400;
	public static final long LocalVariableMasksMethod = 0x800;
	public static final long MaxLocals = 0x1000;
	public static final long MaxReturns = 0x2000;
	public static final long MaxLines = 0x4000;
	public static final long MaxBranches = 0x8000;
	public static final long MaxArguments = 0x10000;
	public static final long UnreachableCode = 0x20000;
	public static final long ComparableMissingMethod = 0x40000;
	public static final long EnumerableMissingMethod = 0x80000;
	public static final long SubclassDoesntCallSuper = 0x100000;
	public static final long AssignmentPrecedence = 0x200000;
	public static final long MethodMissingWithoutRespondTo = 0x400000;
	public static final long ConstantNamingConvention = 0x800000;
	public static final long DynamicVariableAliasesLocal = 0x1000000;
	public static final long LocalVariablePossibleAttributeAccess = 0x2000000;
	public static final long LocalMethodNamingConvention = 0x4000000;
	public static final long UnusedLocalVariable = 0x8000000;
	public static final long DeprecatedRequireGem = 0x10000000;
	public static final long RetryOutsideRescueBody = 0x20000000;
	
	public static final String ERROR = RubyCore.ERROR;
	public static final String WARNING = RubyCore.WARNING;
	public static final String IGNORE = RubyCore.IGNORE;
	public static final String ENABLED = RubyCore.ENABLED;
	public static final String DISABLED = RubyCore.DISABLED;
	
//	 Default severity level for handlers
	public long errorThreshold = 0;
	
	public long warningThreshold = 
		UnusedPrivateMember
		| MisspelledConstructor
		| PossibleAccidentalBooleanAssignment
		| LocalVariableMasksMethod
		| UnreachableCode
		| AssignmentPrecedence
		| SubclassDoesntCallSuper
		| MethodMissingWithoutRespondTo
		| DynamicVariableAliasesLocal
		| LocalVariablePossibleAttributeAccess
		| UnusedLocalVariable
		| DeprecatedRequireGem
		| RetryOutsideRescueBody
		/*| NullReference -- keep RubyCore#getDefaultOptions comment in sync */;
	
	public int maxLocals = 5;
	public int maxLines = 50;
	public int maxBranches = 10;
	public int maxReturns = 10;
	public int maxArguments = 10;
	
	public Map getMap() {
		Map<String, String> optionsMap = new HashMap<String, String>(30);		
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_DEPRECATED_REQUIRE_GEM, getSeverityString(DeprecatedRequireGem));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_RETRY_OUTSIDE_RESCUE, getSeverityString(RetryOutsideRescueBody));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_UNUSED_PRIVATE_MEMBER, getSeverityString(UnusedPrivateMember));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_UNUSED_LOCAL_VARIABLE, getSeverityString(UnusedLocalVariable));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_SUBCLASS_DOESNT_CALL_SUPER, getSeverityString(SubclassDoesntCallSuper));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_ASSIGNMENT_PRECEDENCE, getSeverityString(AssignmentPrecedence));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_UNUSED_PARAMETER, getSeverityString(UnusedArgument));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_UNNECESSARY_ELSE, getSeverityString(UnnecessaryElse));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_MISSPELLED_CONSTRUCTOR, getSeverityString(MisspelledConstructor));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_POSSIBLE_ACCIDENTAL_BOOLEAN_ASSIGNMENT, getSeverityString(PossibleAccidentalBooleanAssignment));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_LOCAL_MASKS_METHOD, getSeverityString(LocalVariableMasksMethod));		
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_ARGUMENTS, getSeverityString(MaxArguments));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_BRANCHES, getSeverityString(MaxBranches));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_LINES, getSeverityString(MaxLines));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_LOCALS, getSeverityString(MaxLocals));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_RETURNS, getSeverityString(MaxReturns));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_SIMILAR_VARIABLE_NAMES, getSeverityString(SimilarVariableNames));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_UNREACHABLE_CODE, getSeverityString(UnreachableCode));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_COMPARABLE_MISSING_METHOD, getSeverityString(ComparableMissingMethod));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_ENUMERABLE_MISSING_METHOD, getSeverityString(EnumerableMissingMethod));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_CONSTANT_NAMING_CONVENTION, getSeverityString(ConstantNamingConvention));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_METHOD_MISSING_NO_RESPOND_TO, getSeverityString(MethodMissingWithoutRespondTo));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_DYNAMIC_VARIABLE_ALIASES_LOCAL, getSeverityString(DynamicVariableAliasesLocal));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_LOCAL_VARIABLE_POSSIBLE_ATTRIBUTE_ACCESS, getSeverityString(LocalVariablePossibleAttributeAccess));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_LOCAL_METHOD_NAMING_CONVENTION, getSeverityString(LocalMethodNamingConvention));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_MAX_ARGUMENTS, String.valueOf(maxArguments));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_MAX_LINES, String.valueOf(maxLines));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_MAX_LOCALS, String.valueOf(maxLocals));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_MAX_RETURNS, String.valueOf(maxReturns));
		optionsMap.put(AptanaRDTPlugin.COMPILER_PB_MAX_BRANCHES, String.valueOf(maxBranches));		
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
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_DEPRECATED_REQUIRE_GEM)) != null)  updateSeverity(DeprecatedRequireGem, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_RETRY_OUTSIDE_RESCUE)) != null)  updateSeverity(RetryOutsideRescueBody, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_UNUSED_PRIVATE_MEMBER)) != null)  updateSeverity(UnusedPrivateMember, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_UNUSED_LOCAL_VARIABLE)) != null)  updateSeverity(UnusedLocalVariable, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_UNUSED_PARAMETER)) != null)  updateSeverity(UnusedArgument, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_UNNECESSARY_ELSE)) != null)  updateSeverity(UnnecessaryElse, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_MISSPELLED_CONSTRUCTOR)) != null)  updateSeverity(MisspelledConstructor, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_POSSIBLE_ACCIDENTAL_BOOLEAN_ASSIGNMENT)) != null)  updateSeverity(PossibleAccidentalBooleanAssignment, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_LOCAL_MASKS_METHOD)) != null)  updateSeverity(LocalVariableMasksMethod, optionValue);		
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_ARGUMENTS)) != null)  updateSeverity(MaxArguments, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_BRANCHES)) != null)  updateSeverity(MaxBranches, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_LINES)) != null)  updateSeverity(MaxLines, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_LOCALS)) != null)  updateSeverity(MaxLocals, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_CODE_COMPLEXITY_RETURNS)) != null)  updateSeverity(MaxReturns, optionValue);	
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_SIMILAR_VARIABLE_NAMES)) != null)  updateSeverity(SimilarVariableNames, optionValue);	
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_UNREACHABLE_CODE)) != null)  updateSeverity(UnreachableCode, optionValue);	
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_COMPARABLE_MISSING_METHOD)) != null)  updateSeverity(ComparableMissingMethod, optionValue);	
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_ENUMERABLE_MISSING_METHOD)) != null)  updateSeverity(EnumerableMissingMethod, optionValue);	
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_SUBCLASS_DOESNT_CALL_SUPER)) != null)  updateSeverity(SubclassDoesntCallSuper, optionValue);	
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_ASSIGNMENT_PRECEDENCE)) != null)  updateSeverity(AssignmentPrecedence, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_CONSTANT_NAMING_CONVENTION)) != null)  updateSeverity(ConstantNamingConvention, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_METHOD_MISSING_NO_RESPOND_TO)) != null)  updateSeverity(MethodMissingWithoutRespondTo, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_DYNAMIC_VARIABLE_ALIASES_LOCAL)) != null)  updateSeverity(DynamicVariableAliasesLocal, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_LOCAL_VARIABLE_POSSIBLE_ATTRIBUTE_ACCESS)) != null)  updateSeverity(LocalVariablePossibleAttributeAccess, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_LOCAL_METHOD_NAMING_CONVENTION)) != null)  updateSeverity(LocalMethodNamingConvention, optionValue);
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_MAX_LOCALS)) != null) {
			if (optionValue instanceof String) {
				String stringValue = (String) optionValue;
				try {
					int val = Integer.parseInt(stringValue);
					if (val >= 0) this.maxLocals = val;
				} catch(NumberFormatException e){
					// ignore ill-formatted limit
				}				
			}
		}
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_MAX_LINES)) != null) {
			if (optionValue instanceof String) {
				String stringValue = (String) optionValue;
				try {
					int val = Integer.parseInt(stringValue);
					if (val >= 0) this.maxLines = val;
				} catch(NumberFormatException e){
					// ignore ill-formatted limit
				}				
			}
		}
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_MAX_BRANCHES)) != null) {
			if (optionValue instanceof String) {
				String stringValue = (String) optionValue;
				try {
					int val = Integer.parseInt(stringValue);
					if (val >= 0) this.maxBranches = val;
				} catch(NumberFormatException e){
					// ignore ill-formatted limit
				}				
			}
		}
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_MAX_ARGUMENTS)) != null) {
			if (optionValue instanceof String) {
				String stringValue = (String) optionValue;
				try {
					int val = Integer.parseInt(stringValue);
					if (val >= 0) this.maxArguments = val;
				} catch(NumberFormatException e){
					// ignore ill-formatted limit
				}				
			}
		}
		if ((optionValue = optionsMap.get(AptanaRDTPlugin.COMPILER_PB_MAX_RETURNS)) != null) {
			if (optionValue instanceof String) {
				String stringValue = (String) optionValue;
				try {
					int val = Integer.parseInt(stringValue);
					if (val >= 0) this.maxReturns = val;
				} catch(NumberFormatException e){
					// ignore ill-formatted limit
				}				
			}
		}
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
