package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author gmiller
 * $Id$
 */
@ClassVersion("$Id$") //$NON-NLS-1$
public class FieldNameMap<T> {

    /** Creates a new instance of NameManager */
    public FieldNameMap(int fieldID, Class fieldClass) {
        addNames(fieldID, fieldClass);
    }

    Map<T, String> mForwardMap = new HashMap<T, String>();
    Map<String, T> mBackwardMap = new HashMap<String, T>();

    protected String cleanFieldName(String aName) {
        aName = aName.replace("_", " "); //$NON-NLS-1$ //$NON-NLS-2$
        String [] pieces = aName.split(" "); //$NON-NLS-1$
        StringBuffer newString = new StringBuffer();
        boolean first = true;
        for (String aPiece : pieces) {
            if (!first) newString.append(' ');
            first = false;
            newString.append(aPiece.charAt(0));
            newString.append(aPiece.substring(1).toLowerCase());
        }
        return newString.toString();
    }

    @SuppressWarnings("unchecked") //$NON-NLS-1$
    private void addNames(int fieldID, Class fieldClass) {
        Field [] fields = fieldClass.getFields();
        for (Field aField : fields) {
            String fieldName = aField.getName();

            if (! fieldName.equals("FIELD")) { //$NON-NLS-1$
                int modifiers = aField.getModifiers();
                if (Modifier.isPublic(modifiers)) {
                    fieldName = cleanFieldName(fieldName);
                    T fieldValue = null;
                    try {
                        fieldValue = (T)aField.get(null);
                        mForwardMap.put(fieldValue, fieldName);
                        mBackwardMap.put(fieldName, fieldValue);
                    } catch (IllegalAccessException ex) {}
                }
            }
        }
    }

    public Collection<String> values()
    {
        return mForwardMap.values();
    }

    public synchronized String getName(T fieldValue) {
        String aName = mForwardMap.get(fieldValue);
        return aName == null ? "Unknown" : aName; //$NON-NLS-1$
    }

    public synchronized T getValue(String fieldValueName) {
        return mBackwardMap.get(fieldValueName);
    }
}
