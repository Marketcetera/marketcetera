package org.rubypeople.rdt.internal.ti.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a bunch of typical returns for methods on core classes
 * 
 * @author Jason
 *
 */
//todo: make it more complete`
//todo: make it a MultiHashMap from http://jakarta.apache.org/commons/ i.e. {'split'=>['String','Array']}
public class TypicalMethodReturnNames {
	
	public static String get(String method)
	{
		return TYPICAL_METHOD_RETURN_TYPE_NAMES.get(method);
	}
	
	private static final Map<String,String> TYPICAL_METHOD_RETURN_TYPE_NAMES = new HashMap<String,String>();
	static
	{
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "to_a", "Array" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "to_ary", "Array" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "to_s", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "to_i", "Fixnum" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "to_f", "Float" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "to_proc", "Proc" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "round", "Fixnum" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "capitalize", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "capitalize!", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "center", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "chomp", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "chomp!", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "chop", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "chop!", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "concat", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "count", "Fixnum" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "crypt", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "downcase", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "downcase!", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "gsub", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "gsub!", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "index", "Fixnum" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "intern", "Symbol" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "length", "Fixnum" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "slice", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "slice!", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "slice", "Array" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "slice!", "Array" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "strip", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "strip!", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "sub", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "sub!", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "swapcase", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "swapcase!", "String" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "unpack", "Array" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "ceil", "Fixnum" );
		TYPICAL_METHOD_RETURN_TYPE_NAMES.put( "floor", "Fixnum" );
	}
}
