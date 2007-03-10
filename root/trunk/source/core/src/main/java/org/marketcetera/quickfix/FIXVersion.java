package org.marketcetera.quickfix;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.core.MessageKey;
import org.marketcetera.core.MarketceteraException;
import org.marketcetera.quickfix.messagefactory.*;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.field.BeginString;

import java.util.HashMap;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public enum FIXVersion {
    // todo: later when QFJ is ready we get rid of MyMessageFactory and just create a straight QFJ quickfix.fix4x.MessageFactory
    FIX40(FIXDataDictionaryManager.FIX_4_0_BEGIN_STRING,
          new FIXMessageFactory(FIXDataDictionaryManager.FIX_4_0_BEGIN_STRING, new MessageFactory40(), new NoOpFIXMessageAugmentor())),
    FIX41(FIXDataDictionaryManager.FIX_4_1_BEGIN_STRING,
            new FIXMessageFactory(FIXDataDictionaryManager.FIX_4_1_BEGIN_STRING, new MessageFactory41(), new NoOpFIXMessageAugmentor())),
    FIX42(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING,
            new FIXMessageFactory(FIXDataDictionaryManager.FIX_4_2_BEGIN_STRING, new MessageFactory42(), new FIXMessageAugmentor_42())),
    FIX43(FIXDataDictionaryManager.FIX_4_3_BEGIN_STRING,
            new FIXMessageFactory(FIXDataDictionaryManager.FIX_4_3_BEGIN_STRING, new MessageFactory43(), new FIXMessageAugmentor_43())),
    FIX44(FIXDataDictionaryManager.FIX_4_4_BEGIN_STRING,
            new FIXMessageFactory(FIXDataDictionaryManager.FIX_4_4_BEGIN_STRING, new MessageFactory44(), new FIXMessageAugmentor_44()));

    private static HashMap<String, FIXVersion> versionMap;

    static {
        versionMap = new HashMap<String, FIXVersion>();
        versionMap.put(FIX40.toString(), FIX40);
        versionMap.put(FIX41.toString(), FIX41);
        versionMap.put(FIX42.toString(), FIX42);
        versionMap.put(FIX43.toString(), FIX43);
        versionMap.put(FIX44.toString(), FIX44);
    }

    public static FIXVersion getFIXVersion(String version) throws MarketceteraException {
        FIXVersion fixVersion = versionMap.get(version);
        if(fixVersion == null) {
            throw new MarketceteraException(MessageKey.FIX_VERSION_UNSUPPORTED.getLocalizedMessage(version));
        }
        return fixVersion;
    }

    public static FIXVersion getFIXVersion(Message inMsg) throws FieldNotFound {
        return versionMap.get(inMsg.getField(new BeginString()).getValue());
    }


    private FIXVersion (String inVersion, FIXMessageFactory inFactory) {
        version = inVersion;
        msgFactory = inFactory;
    }


    private FIXMessageFactory msgFactory;
    private final String version;

    public String toString() {
        return version;
    }

    public FIXMessageFactory getMessageFactory() {
        return msgFactory;
    }

}
