namespace QuickFix42
{

  public class MarketDataRequestReject : Message
  {
    public MarketDataRequestReject() : base(MsgType()) {}
    static QuickFix.MsgType MsgType() { return new QuickFix.MsgType("Y"); }

    public MarketDataRequestReject(
      QuickFix.MDReqID aMDReqID )
    : base(MsgType()) {
      set(aMDReqID);
    }

    public void set(QuickFix.MDReqID value)
    { setField(value); }
    public QuickFix.MDReqID get(QuickFix.MDReqID  value)
    { getField(value); return value; }
    public QuickFix.MDReqID getMDReqID()
    { QuickFix.MDReqID value = new QuickFix.MDReqID();
      getField(value); return value; }
    public bool isSet(QuickFix.MDReqID field)
    { return isSetField(field); }
    public bool isSetMDReqID()
    { return isSetField(262); }

    public void set(QuickFix.MDReqRejReason value)
    { setField(value); }
    public QuickFix.MDReqRejReason get(QuickFix.MDReqRejReason  value)
    { getField(value); return value; }
    public QuickFix.MDReqRejReason getMDReqRejReason()
    { QuickFix.MDReqRejReason value = new QuickFix.MDReqRejReason();
      getField(value); return value; }
    public bool isSet(QuickFix.MDReqRejReason field)
    { return isSetField(field); }
    public bool isSetMDReqRejReason()
    { return isSetField(281); }

    public void set(QuickFix.Text value)
    { setField(value); }
    public QuickFix.Text get(QuickFix.Text  value)
    { getField(value); return value; }
    public QuickFix.Text getText()
    { QuickFix.Text value = new QuickFix.Text();
      getField(value); return value; }
    public bool isSet(QuickFix.Text field)
    { return isSetField(field); }
    public bool isSetText()
    { return isSetField(58); }

    public void set(QuickFix.EncodedTextLen value)
    { setField(value); }
    public QuickFix.EncodedTextLen get(QuickFix.EncodedTextLen  value)
    { getField(value); return value; }
    public QuickFix.EncodedTextLen getEncodedTextLen()
    { QuickFix.EncodedTextLen value = new QuickFix.EncodedTextLen();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedTextLen field)
    { return isSetField(field); }
    public bool isSetEncodedTextLen()
    { return isSetField(354); }

    public void set(QuickFix.EncodedText value)
    { setField(value); }
    public QuickFix.EncodedText get(QuickFix.EncodedText  value)
    { getField(value); return value; }
    public QuickFix.EncodedText getEncodedText()
    { QuickFix.EncodedText value = new QuickFix.EncodedText();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedText field)
    { return isSetField(field); }
    public bool isSetEncodedText()
    { return isSetField(355); }

  };

}

