package org.marketcetera.util.quickfix;

import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.DataDictionary;
import quickfix.Group;

/**
 * Analyzes a QuickFIX/J group, producing a human-readable
 * representation of its contents.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class AnalyzedGroup
{

    // INSTANCE DATA.

    private final List<AnalyzedField> mFields=
        new LinkedList<AnalyzedField>();


    // CONSTRUCTOR.

    /**
     * Creates a new analyzed group for the given QuickFIX/J group,
     * which is part of the message with the given type. One data
     * dictionary is used to translate field tags and enumerated
     * values (the <i>name</i> dictionary); another (the <i>scope</i>
     * dictionary) is used for scope-dependent lookups such as
     * required flags and subgroup analysis.
     *
     * @param nameQDict The name dictionary.
     * @param scopeQDict The scope dictionary.
     * @param qGroup The group.
     * @param msgType The message type.
     */

    AnalyzedGroup
        (DataDictionary nameQDict,
         DataDictionary scopeQDict,
         Group qGroup,
         String msgType)
    {        
        AnalyzedMessage.analyzeFields
            (nameQDict,scopeQDict,qGroup,msgType,qGroup.iterator(),mFields);
    }


    // INSTANCE METHODS.

    /**
     * Returns the receiver's fields.
     *
     * @return The fields.
     */

    public List<AnalyzedField> getFields()
    {
        return mFields;
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
        AnalyzedMessage.printFields(stream,prefix,getFields());
    }
}
