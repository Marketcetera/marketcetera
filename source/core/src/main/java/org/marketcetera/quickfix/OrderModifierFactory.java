package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.ConfigData;
import org.marketcetera.core.LoggerAdapter;

import java.util.prefs.BackingStoreException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 * @author Graham Miller
 * @version $Id$
 */
@ClassVersion("$Id$")
public class OrderModifierFactory {
    public static final String FIX_HEADER_PREFIX = "fix.header.";
    public static final String FIX_TRAILER_PREFIX = "fix.trailer.";
    public static final String FIX_FIELDS_PREFIX = "fix.fields.";
    private static final String PARSER_REGEX = "fix.(fields|header|trailer).([0-9]+)(\\((\\*|[0-9a-z]|admin|app)\\))?";
    private static final Pattern DEFAULT_FIELDS_PATTERN = Pattern.compile(PARSER_REGEX, Pattern.CASE_INSENSITIVE);

    public static DefaultOrderModifier defaultsModifierInstance(ConfigData props) throws BackingStoreException {
        DefaultOrderModifier orderModifier = new DefaultOrderModifier();
        String[] propNames = props.keys();
        for (String oneName : propNames) {
            if (oneName.startsWith(FIX_FIELDS_PREFIX)) {
                readDefaultFieldsHelper(props, oneName, orderModifier,
                        DefaultOrderModifier.MessageFieldType.MESSAGE);
            } else if (oneName.startsWith(FIX_HEADER_PREFIX)) {
                readDefaultFieldsHelper(props, oneName, orderModifier,
                        DefaultOrderModifier.MessageFieldType.HEADER);
            } else if (oneName.startsWith(FIX_TRAILER_PREFIX)) {
                readDefaultFieldsHelper(props, oneName, orderModifier,
                        DefaultOrderModifier.MessageFieldType.TRAILER);
            }
        }
        return orderModifier;
    }

    /**
     * The header fields are of form:
     * <prefix>.<fieldName>=<fieldValue>
     * Where fieldName is an integer number.
     * So we parse out the field name, store it as an int, and store the value as an object.
     *
     * @param inProps
     * @param propName
     * @param inOrderModifier
     * @param fieldType       Which particular kind of field we are modifying: trailer/header/message
     */
    protected static void readDefaultFieldsHelper(ConfigData inProps, String propName,
                                                  DefaultOrderModifier inOrderModifier,
                                                  DefaultOrderModifier.MessageFieldType fieldType) {

        Matcher defaultFieldsMatcher = DEFAULT_FIELDS_PATTERN.matcher(propName);
        String predicate = null;
        if (defaultFieldsMatcher.matches()){
            int groupCount = defaultFieldsMatcher.groupCount();
            String fieldIDString = defaultFieldsMatcher.group(2);
            int fieldID = Integer.parseInt(fieldIDString);
            if (groupCount == 4){
                predicate = defaultFieldsMatcher.group(4);
            }
            inOrderModifier.addDefaultField(fieldID, inProps.get(propName, ""), fieldType, predicate);
        }


    }
}
