package org.marketcetera.util.quickfix;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.marketcetera.util.except.ExceptUtils;
import org.marketcetera.util.file.CloseableRegistry;
import org.marketcetera.util.log.I18NBoundMessage;
import org.marketcetera.util.misc.ClassVersion;
import quickfix.DataDictionary;
import quickfix.Field;
import quickfix.FieldMap;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.MsgType;

/**
 * Analyzes a QuickFIX/J message, producing a human-readable
 * representation of its contents.
 *
 * @author tlerios@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@ClassVersion("$Id$")
public class AnalyzedMessage
{

    // INSTANCE DATA.

    private final List<AnalyzedField> mHeader=
        new LinkedList<AnalyzedField>();
    private final List<AnalyzedField> mBody=
        new LinkedList<AnalyzedField>();
    private final List<AnalyzedField> mTrailer=
        new LinkedList<AnalyzedField>();
    private Exception mValidationException;


    // CONSTRUCTOR.

    /**
     * Creates a new analyzed message for the given QuickFIX/J
     * message, interpreted using the given data dictionary.
     *
     * @param qDict The data dictionary.
     * @param qMsg The message.
     */

    public AnalyzedMessage
        (DataDictionary qDict,
         Message qMsg)
    {
        // Determine message type.

        String msgType=null;
        try {
            msgType=qMsg.getHeader().getField(new MsgType()).getValue();
        } catch (FieldNotFound ex) {
            Messages.MISSING_TYPE.error(this,ex,qMsg);
            return;
        }

        // Analyze sections.

        analyzeFields(qDict,qDict,qMsg,DataDictionary.HEADER_ID,
                      qMsg.getHeader().iterator(),mHeader);
        analyzeFields(qDict,qDict,qMsg,msgType,
                      qMsg.iterator(),mBody);
        analyzeFields(qDict,qDict,qMsg,DataDictionary.TRAILER_ID,
                      qMsg.getTrailer().iterator(),mTrailer);

        // Validate message.

        try {
            qDict.validate(qMsg);
        } catch (Exception ex) {
            ExceptUtils.interrupt(ex);
            mValidationException=ex;
        }
    }


    // INSTANCE METHODS.

    /**
     * Analyzes the given QuickFIX/J fields that are part of the given
     * field map, and appends the results of the analysis to the given
     * list. The map is part of (or same as) the message with the
     * given type. One data dictionary is used to translate field tags
     * and enumerated values (the <i>name</i> dictionary); another
     * (the <i>scope</i> dictionary) is used for scope-dependent
     * lookups such as required flags and subgroup analysis.
     *
     * @param nameQDict The name dictionary.
     * @param scopeQDict The scope dictionary.
     * @param qMap The field map.
     * @param msgType The message type.
     * @param qFields The fields.
     * @param list The results' list.
     */

    static void analyzeFields
        (DataDictionary nameQDict,
         DataDictionary scopeQDict,
         FieldMap qMap,
         String msgType,
         Iterator<?> qFields,
         List<AnalyzedField> list)
    {
        while (qFields.hasNext()) {
            list.add(new AnalyzedField
                     (nameQDict,scopeQDict,qMap,msgType,
                      (Field<?>)(qFields.next())));
        }
    }

    /**
     * Prints the given field list onto the given stream. Each line
     * printed is preceded by the given prefix.
     *
     * @param stream The stream.
     * @param prefix The prefix.
     * @param list The field list.
     */

    static void printFields
        (PrintStream stream,
         String prefix,
         List<AnalyzedField> list)
    {
        for (AnalyzedField field:list) {
            stream.println();
            field.print(stream,prefix);
        }
    }

    /**
     * Prints the given section title and fields of the receiver onto
     * the given stream. Nothing is printed if the section contains no
     * fields.
     *
     * @param stream The stream.
     * @param title The section title.
     * @param list The section fields.
     */

    private void printSection
        (PrintStream stream,
         I18NBoundMessage title,
         List<AnalyzedField> list)
    {
        if (list.size()==0) {
            return;
        }
        stream.println();
        stream.print(title.getText());
        printFields(stream," ",list); //$NON-NLS-1$
    }

    /**
     * Returns the analyzed fields that comprise the receiver's
     * header.
     *
     * @return The fields.
     */

    public List<AnalyzedField> getHeader()
    {
        return mHeader;
    }

    /**
     * Returns the analyzed fields that comprise the receiver's body.
     *
     * @return The fields.
     */

    public List<AnalyzedField> getBody()
    {
        return mBody;
    }

    /**
     * Returns the analyzed fields that comprise the receiver's
     * trailer.
     *
     * @return The fields.
     */

    public List<AnalyzedField> getTrailer()
    {
        return mTrailer;
    }

    /**
     * Returns the receiver's validation exception.
     *
     * @return The exception. It is null if the receiver is valid.
     */

    public Exception getValidationException()
    {
        return mValidationException;
    }

    /**
     * Prints the receiver onto the given stream.
     *
     * @param stream The stream.
     */

    public void print
        (PrintStream stream)
    {
        if (getValidationException()!=null) {
            stream.println();
            stream.println(Messages.VALIDATION_TITLE.getText());
            stream.print(" "); //$NON-NLS-1$
            stream.print(getValidationException().getLocalizedMessage());
        }
        printSection(stream,Messages.HEADER_TITLE,getHeader());
        printSection(stream,Messages.BODY_TITLE,getBody());
        printSection(stream,Messages.TRAILER_TITLE,getTrailer());
    }


    // Object.

    @Override
    public String toString()
    {
        ByteArrayOutputStream outputStream;
        CloseableRegistry r=new CloseableRegistry();
        try {
            outputStream=new ByteArrayOutputStream();
            r.register(outputStream);
            PrintStream printStream=new PrintStream(outputStream);
            r.register(printStream);
            print(printStream);
        } finally {
            r.close();
        }
        return new String(outputStream.toByteArray());
    }
}
