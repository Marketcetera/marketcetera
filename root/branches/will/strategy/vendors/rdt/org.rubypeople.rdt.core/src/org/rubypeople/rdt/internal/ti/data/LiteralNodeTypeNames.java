package org.rubypeople.rdt.internal.ti.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Maps from JRuby AST Literal Node classnames to the Ruby type they represent.
 * @author Jason
 *
 */
public class LiteralNodeTypeNames {
	public static String get(String nodeType)
	{
		return CONST_NODE_TYPE_NAMES.get(nodeType);
	}
	
	private static final Map<String,String> CONST_NODE_TYPE_NAMES = new HashMap<String,String>();
	static {
		CONST_NODE_TYPE_NAMES.put("FixnumNode", "Fixnum");
		CONST_NODE_TYPE_NAMES.put("DStrNode",   "String");
		CONST_NODE_TYPE_NAMES.put("StrNode",    "String");
		CONST_NODE_TYPE_NAMES.put("ZArrayNode", "Array");
		CONST_NODE_TYPE_NAMES.put("ArrayNode",  "Array");
		CONST_NODE_TYPE_NAMES.put("TrueNode",   "TrueClass");
		CONST_NODE_TYPE_NAMES.put("FalseNode",  "FalseClass");
		CONST_NODE_TYPE_NAMES.put("NilNode",    "NilClass");
		CONST_NODE_TYPE_NAMES.put("FloatNode",  "Float");
		CONST_NODE_TYPE_NAMES.put("BignumNode", "Bignum");
		CONST_NODE_TYPE_NAMES.put("SymbolNode", "Symbol");
		CONST_NODE_TYPE_NAMES.put("DSymbolNode","Symbol");
		CONST_NODE_TYPE_NAMES.put("HashNode",   "Hash");
		CONST_NODE_TYPE_NAMES.put("RegexpNode", "Regexp");
	}
}
