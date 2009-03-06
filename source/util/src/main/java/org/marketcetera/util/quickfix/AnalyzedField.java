package org.marketcetera.util.quickfix;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang.ObjectUtils;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.FieldType;
import quickfix.Group;

/**
 * Analyzes a QuickFIX/J field, producing a human-readable
 * representation of its contents.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class AnalyzedField
{

    // INSTANCE DATA.

    private final Field<?> mQField;
    private final FieldType mQType;
    private final String mName;
    private final boolean mRequired;
    private final String mValue;
    private final List<AnalyzedGroup> mGroups=
        new LinkedList<AnalyzedGroup>();


    // CONSTRUCTOR.

    /**
     * Creates a new analyzed field for the given QuickFIX/J field.
     * The field is part of the given QuickFIX/J field map, and the
     * map is part of (or same as) the message with the given
     * type. One data dictionary is used to translate field tags and
     * enumerated values (the <i>name</i> dictionary); another (the
     * <i>scope</i> dictionary) is used for scope-dependent lookups
     * such as required flags and subgroup analysis.
     *
     * @param nameQDict The name dictionary.
     * @param scopeQDict The scope dictionary.
     * @param qMap The field map.
     * @param msgType The message type.
     * @param qField The field.
     */

    AnalyzedField
        (DataDictionary nameQDict,
         DataDictionary scopeQDict,
         FieldMap qMap,
         String msgType,
         Field<?> qField)
    {
        mQField=qField;
        mQType=nameQDict.getFieldTypeEnum(getQFieldTag());
        mName=nameQDict.getFieldName(getQFieldTag());
        mRequired=scopeQDict.isRequiredField(msgType,getQFieldTag());

        // Value: enumerated (valid or invalid) or arbitrary.

        String value=getQFieldValueAsString();
        if (nameQDict.hasFieldValue(getQFieldTag())) {
            if (nameQDict.isFieldValue(getQFieldTag(),value)) {
                mValue=Messages.ENUM_FIELD_VALUE.getText
                    (value,nameQDict.getValueName(getQFieldTag(),value));
            }  else {
                mValue=Messages.INVALID_FIELD_VALUE.getText(value);
            }
        } else {
            mValue=value;
        }

        // Groups.

        DataDictionary.GroupInfo info=scopeQDict.getGroup
            (msgType,getQFieldTag());
        if (info==null) {
            return;
        }
        int count=Integer.valueOf(value);
        for (int i=0;i<count;i++) {
            Group group=new Group(getQFieldTag(),info.getDelimeterField());
            try {
                qMap.getGroup(i+1,group);
            } catch (FieldNotFound ex) {
                Messages.MISSING_GROUP.error(this,ex,i+1,qMap);
                continue;
            }
            mGroups.add(new AnalyzedGroup
                        (nameQDict,info.getDataDictionary(),group,msgType));
        }
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's QuickFIX/J field.
     *
     * @return The field.
     */

    public Field<?> getQField()
    {
        return mQField;
    }

    /**
     * Returns the receiver's QuickFIX/J field type.
     *
     * @return The type.
     */

    public FieldType getQType()
    {
        return mQType;
    }

    /**
     * Returns the receiver's name.
     *
     * @return The name.
     */

    public String getName()
    {
        return mName;
    }

    /**
     * Returns true if the receiver is a required field.
     *
     * @return True if so.
     */

    public boolean getRequired()
    {
        return mRequired;
    }

    /**
     * Returns the receiver's (analyzed) value.
     *
     * @return The value.
     */

    public String getValue()
    {
        return mValue;
    }

    /**
     * Returns the receiver's groups.
     *
     * @return The groups.
     */

    public List<AnalyzedGroup> getGroups()
    {
        return mGroups;
    }

    /**
     * Returns the receiver's QuickFIX/J field tag.
     *
     * @return The tag.
     */

    public int getQFieldTag()
    {
        return getQField().getTag();
    }

    /**
     * Returns the receiver's QuickFIX/J field value.
     *
     * @return The value. It may be null.
     */

    public Object getQFieldValue()
    {
        return getQField().getObject();
    }

    /**
     * Returns the receiver's QuickFIX/J field value in string form.
     *
     * @return The value. It may be null.
     */

    public String getQFieldValueAsString()
    {
        return ObjectUtils.toString(getQFieldValue(),null);
    }

    /**
     * Prints the receiver onto the given stream. Each line printed is
     * preceded by the given prefix.
     *
     * @param stream The stream.
     * @param prefix The prefix.
     */

    public void print
        (PrintStream stream,
         String prefix)
    {
        stream.print(prefix);
        stream.print(Messages.SINGLE_FIELD.getText
                     (getName(),getQFieldTag(),getValue(),
                      (getRequired()?1:0)));
        prefix+=' '; //$NON-NLS-1$
        String groupPrefix=prefix+' '; //$NON-NLS-1$
        int i=0;
        for (AnalyzedGroup group:getGroups()) {
            stream.println();
            stream.print(prefix);
            stream.print(Messages.GROUP_TITLE.getText(++i));
            group.print(stream,groupPrefix);
        }
    }
}
