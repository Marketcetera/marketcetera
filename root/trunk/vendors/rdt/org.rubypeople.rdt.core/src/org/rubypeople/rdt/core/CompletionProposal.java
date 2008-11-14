package org.rubypeople.rdt.core;

public class CompletionProposal {

	public static final int GLOBAL_REF = 1;
	public static final int CONSTANT_REF = 2;
	public static final int KEYWORD = 3;
	public static final int INSTANCE_VARIABLE_REF = 4;
	public static final int LOCAL_VARIABLE_REF = 5;
	public static final int METHOD_REF = 6;
	public static final int METHOD_DECLARATION = 7;
	public static final int CLASS_VARIABLE_REF = 8;
	public static final int TYPE_REF = 9;
	public static final int VARIABLE_DECLARATION = 10;
	public static final int POTENTIAL_METHOD_DECLARATION = 11;
	public static final int METHOD_NAME_REFERENCE = 12;
	
	protected static final int FIRST_KIND = GLOBAL_REF;
	protected static final int LAST_KIND = METHOD_NAME_REFERENCE;

	/**
	 * Kind of completion request.
	 */
	private int completionKind;
	
	/**
	 * Offset in original buffer where ICodeAssist.codeComplete() was
	 * requested.
	 */
	private int completionLocation;
	
	/**
	 * Start position (inclusive) of source range in original buffer 
	 * containing the relevant token
	 * defaults to empty subrange at [0,0).
	 */
	private int tokenStart = 0;
	
	/**
	 * End position (exclusive) of source range in original buffer 
	 * containing the relevant token;
	 * defaults to empty subrange at [0,0).
	 */
	private int tokenEnd = 0;
	
	/**
	 * Completion string; defaults to empty string.
	 */
	private String completion = "";
	
	/**
	 * Start position (inclusive) of source range in original buffer 
	 * to be replaced by completion string; 
	 * defaults to empty subrange at [0,0).
	 */
	private int replaceStart = 0;
	
	/**
	 * End position (exclusive) of source range in original buffer 
	 * to be replaced by completion string;
	 * defaults to empty subrange at [0,0).
	 */
	private int replaceEnd = 0;
	
	/**
	 * Relevance rating; positive; higher means better;
	 * defaults to minimum rating.
	 */
	private int relevance = 1;
	
	/**
	 * Parameter names (for method completions), or
	 * <code>null</code> if none. Lazily computed.
	 * Defaults to <code>null</code>.
	 */
	private String[] parameterNames = null;
	
	/**
	 * Indicates whether parameter names have been computed.
	 */
	private boolean parameterNamesComputed = false;
	
	/**
	 * Simple name of the method, field,
	 * member, or variable relevant in the context, or
	 * <code>null</code> if none.
	 * Defaults to null.
	 */
	private String name = null;
	private int flags;
	private String type;
	private String declaringType;
	private IRubyElement element;
	private boolean blockNamesComputed;
	private String[] blockNames;

	public CompletionProposal(int kind, String completion, int relevance) {
		this.completionKind = kind;
		this.completion = completion;
		this.relevance = relevance;
	}

	public int getKind() {
		return completionKind;
	}

	public String getCompletion() {
		return completion;
	}

	public int getReplaceStart() {
		return replaceStart;
	}

	public int getReplaceEnd() {
		return replaceEnd;
	}

	public int getCompletionLocation() {
		return completionLocation;
	}

	public int getRelevance() {
		return relevance;
	}

	public String getName() {
		return name;
	}
	
	/**
	 * Returns the modifier flags relevant in the context, or
	 * <code>Flags.AccDefault</code> if none.
	 * <p>
	 * This field is available for the following kinds of
	 * completion proposals:
	 * <ul>
	 * <li><code>ANNOTATION_ATTRIBUT_REF</code> - modifier flags
	 * of the attribute that is referenced; 
	 * <li><code>ANONYMOUS_CLASS_DECLARATION</code> - modifier flags
	 * of the constructor that is referenced</li>
	 * 	<li><code>FIELD_REF</code> - modifier flags
	 * of the field that is referenced; 
	 * <code>Flags.AccEnum</code> can be used to recognize
	 * references to enum constants
	 * </li>
	 * 	<li><code>KEYWORD</code> - modifier flag
	 * corrresponding to the modifier keyword</li>
	 * 	<li><code>LOCAL_VARIABLE_REF</code> - modifier flags
	 * of the local variable that is referenced</li>
	 * 	<li><code>METHOD_REF</code> - modifier flags
	 * of the method that is referenced;
	 * <code>Flags.AccAnnotation</code> can be used to recognize
	 * references to annotation type members
	 * </li>
	 * 	<li><code>METHOD_DECLARATION</code> - modifier flags
	 * for the method that is being implemented or overridden</li>
	 * 	<li><code>TYPE_REF</code> - modifier flags
	 * of the type that is referenced; <code>Flags.AccInterface</code>
	 * can be used to recognize references to interfaces, 
	 * <code>Flags.AccEnum</code> enum types,
	 * and <code>Flags.AccAnnotation</code> annotation types
	 * </li>
	 * 	<li><code>VARIABLE_DECLARATION</code> - modifier flags
	 * for the variable being declared</li>
	 * 	<li><code>POTENTIAL_METHOD_DECLARATION</code> - modifier flags
	 * for the method that is being created</li>
	 * </ul>
	 * For other kinds of completion proposals, this method returns
	 * <code>Flags.AccDefault</code>.
	 * </p>
	 * 
	 * @return the modifier flags, or
	 * <code>Flags.AccDefault</code> if none
	 * @see Flags
	 */
	public int getFlags() {
		return this.flags;
	}
	
	/**
	 * Sets the modifier flags relevant in the context.
	 * <p>
	 * If not set, defaults to none.
	 * </p>
	 * <p>
	 * The completion engine creates instances of this class and sets
	 * its properties; this method is not intended to be used by other clients.
	 * </p>
	 * 
	 * @param flags the modifier flags, or
	 * <code>Flags.AccDefault</code> if none
	 */
	public void setFlags(int flags) {
		this.flags = flags;
	}

	public void setReplaceRange(int startIndex, int endIndex) {
		if (startIndex < 0 || endIndex < startIndex) {
			throw new IllegalArgumentException();
		}
		this.replaceStart = startIndex;
		this.replaceEnd = endIndex;		
	}

	public String[] getParameterNames() {
		if (!parameterNamesComputed) {
			if (getElement() != null && getElement().isType(IRubyElement.METHOD)) {
				IMethod method = (IMethod) getElement();
				try {
					parameterNames = method.getParameterNames();
				} catch (RubyModelException e) {
					RubyCore.log(e);
				}
			}
			parameterNamesComputed = true;
		}		
		return parameterNames;
	}

	public String getType() {
		if (type != null) return type;
		return "";
	}

	public String getDeclaringType() {
		if (declaringType != null) return declaringType;
		return "";
	}

	public void setType(String name) {
		this.type = name;		
	}

	public void setDeclaringType(String elementName) {
		this.declaringType = elementName;		
	}
	
	public void setName(String newName) {
		this.name = newName;
	}
	
	public void setElement(IRubyElement element) {
		this.element = element;
	}
	
	public IRubyElement getElement() {
		return element;
	}

	public String[] getBlockVars() {
		if (!blockNamesComputed) {
			if (getElement().isType(IRubyElement.METHOD)) {
				IMethod method = (IMethod) getElement();
				try {
					blockNames = method.getBlockParameters();
				} catch (RubyModelException e) {
					RubyCore.log(e);
				}
			}
			blockNamesComputed = true;
		}		
		return blockNames;
	}
	
	@Override
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(name);
		if (element != null) {
			buffer.append(" (");
			buffer.append(element.toString());
			buffer.append(")");
		}
		return buffer.toString();
	}
}
