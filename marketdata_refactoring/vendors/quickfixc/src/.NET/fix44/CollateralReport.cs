namespace QuickFix44
{

  public class CollateralReport : Message
  {
    public CollateralReport() : base(MsgType()) {}
    static QuickFix.MsgType MsgType() { return new QuickFix.MsgType("BA"); }

    public CollateralReport(
      QuickFix.CollRptID aCollRptID,
      QuickFix.CollStatus aCollStatus )
    : base(MsgType()) {
      set(aCollRptID);
      set(aCollStatus);
    }

    public void set(QuickFix.CollRptID value)
    { setField(value); }
    public QuickFix.CollRptID get(QuickFix.CollRptID  value)
    { getField(value); return value; }
    public QuickFix.CollRptID getCollRptID()
    { QuickFix.CollRptID value = new QuickFix.CollRptID();
      getField(value); return value; }
    public bool isSet(QuickFix.CollRptID field)
    { return isSetField(field); }
    public bool isSetCollRptID()
    { return isSetField(908); }

    public void set(QuickFix.CollInquiryID value)
    { setField(value); }
    public QuickFix.CollInquiryID get(QuickFix.CollInquiryID  value)
    { getField(value); return value; }
    public QuickFix.CollInquiryID getCollInquiryID()
    { QuickFix.CollInquiryID value = new QuickFix.CollInquiryID();
      getField(value); return value; }
    public bool isSet(QuickFix.CollInquiryID field)
    { return isSetField(field); }
    public bool isSetCollInquiryID()
    { return isSetField(909); }

    public void set(QuickFix.CollStatus value)
    { setField(value); }
    public QuickFix.CollStatus get(QuickFix.CollStatus  value)
    { getField(value); return value; }
    public QuickFix.CollStatus getCollStatus()
    { QuickFix.CollStatus value = new QuickFix.CollStatus();
      getField(value); return value; }
    public bool isSet(QuickFix.CollStatus field)
    { return isSetField(field); }
    public bool isSetCollStatus()
    { return isSetField(910); }

    public void set(QuickFix.TotNumReports value)
    { setField(value); }
    public QuickFix.TotNumReports get(QuickFix.TotNumReports  value)
    { getField(value); return value; }
    public QuickFix.TotNumReports getTotNumReports()
    { QuickFix.TotNumReports value = new QuickFix.TotNumReports();
      getField(value); return value; }
    public bool isSet(QuickFix.TotNumReports field)
    { return isSetField(field); }
    public bool isSetTotNumReports()
    { return isSetField(911); }

    public void set(QuickFix.LastRptRequested value)
    { setField(value); }
    public QuickFix.LastRptRequested get(QuickFix.LastRptRequested  value)
    { getField(value); return value; }
    public QuickFix.LastRptRequested getLastRptRequested()
    { QuickFix.LastRptRequested value = new QuickFix.LastRptRequested();
      getField(value); return value; }
    public bool isSet(QuickFix.LastRptRequested field)
    { return isSetField(field); }
    public bool isSetLastRptRequested()
    { return isSetField(912); }

    public void set(QuickFix.NoPartyIDs value)
    { setField(value); }
    public QuickFix.NoPartyIDs get(QuickFix.NoPartyIDs  value)
    { getField(value); return value; }
    public QuickFix.NoPartyIDs getNoPartyIDs()
    { QuickFix.NoPartyIDs value = new QuickFix.NoPartyIDs();
      getField(value); return value; }
    public bool isSet(QuickFix.NoPartyIDs field)
    { return isSetField(field); }
    public bool isSetNoPartyIDs()
    { return isSetField(453); }

    public class NoPartyIDs: QuickFix.Group
    {
    public NoPartyIDs() : base(453,448,message_order ) {}
    static int[] message_order = new int[] {448,447,452,802,0};
      public void set(QuickFix.PartyID value)
      { setField(value); }
      public QuickFix.PartyID get(QuickFix.PartyID  value)
      { getField(value); return value; }
      public QuickFix.PartyID getPartyID()
      { QuickFix.PartyID value = new QuickFix.PartyID();
        getField(value); return value; }
      public bool isSet(QuickFix.PartyID field)
      { return isSetField(field); }
      public bool isSetPartyID()
      { return isSetField(448); }

      public void set(QuickFix.PartyIDSource value)
      { setField(value); }
      public QuickFix.PartyIDSource get(QuickFix.PartyIDSource  value)
      { getField(value); return value; }
      public QuickFix.PartyIDSource getPartyIDSource()
      { QuickFix.PartyIDSource value = new QuickFix.PartyIDSource();
        getField(value); return value; }
      public bool isSet(QuickFix.PartyIDSource field)
      { return isSetField(field); }
      public bool isSetPartyIDSource()
      { return isSetField(447); }

      public void set(QuickFix.PartyRole value)
      { setField(value); }
      public QuickFix.PartyRole get(QuickFix.PartyRole  value)
      { getField(value); return value; }
      public QuickFix.PartyRole getPartyRole()
      { QuickFix.PartyRole value = new QuickFix.PartyRole();
        getField(value); return value; }
      public bool isSet(QuickFix.PartyRole field)
      { return isSetField(field); }
      public bool isSetPartyRole()
      { return isSetField(452); }

      public void set(QuickFix.NoPartySubIDs value)
      { setField(value); }
      public QuickFix.NoPartySubIDs get(QuickFix.NoPartySubIDs  value)
      { getField(value); return value; }
      public QuickFix.NoPartySubIDs getNoPartySubIDs()
      { QuickFix.NoPartySubIDs value = new QuickFix.NoPartySubIDs();
        getField(value); return value; }
      public bool isSet(QuickFix.NoPartySubIDs field)
      { return isSetField(field); }
      public bool isSetNoPartySubIDs()
      { return isSetField(802); }

      public class NoPartySubIDs: QuickFix.Group
      {
      public NoPartySubIDs() : base(802,523,message_order ) {}
      static int[] message_order = new int[] {523,803,0};
        public void set(QuickFix.PartySubID value)
        { setField(value); }
        public QuickFix.PartySubID get(QuickFix.PartySubID  value)
        { getField(value); return value; }
        public QuickFix.PartySubID getPartySubID()
        { QuickFix.PartySubID value = new QuickFix.PartySubID();
          getField(value); return value; }
        public bool isSet(QuickFix.PartySubID field)
        { return isSetField(field); }
        public bool isSetPartySubID()
        { return isSetField(523); }

        public void set(QuickFix.PartySubIDType value)
        { setField(value); }
        public QuickFix.PartySubIDType get(QuickFix.PartySubIDType  value)
        { getField(value); return value; }
        public QuickFix.PartySubIDType getPartySubIDType()
        { QuickFix.PartySubIDType value = new QuickFix.PartySubIDType();
          getField(value); return value; }
        public bool isSet(QuickFix.PartySubIDType field)
        { return isSetField(field); }
        public bool isSetPartySubIDType()
        { return isSetField(803); }

      };
    };
    public void set(QuickFix.Account value)
    { setField(value); }
    public QuickFix.Account get(QuickFix.Account  value)
    { getField(value); return value; }
    public QuickFix.Account getAccount()
    { QuickFix.Account value = new QuickFix.Account();
      getField(value); return value; }
    public bool isSet(QuickFix.Account field)
    { return isSetField(field); }
    public bool isSetAccount()
    { return isSetField(1); }

    public void set(QuickFix.AccountType value)
    { setField(value); }
    public QuickFix.AccountType get(QuickFix.AccountType  value)
    { getField(value); return value; }
    public QuickFix.AccountType getAccountType()
    { QuickFix.AccountType value = new QuickFix.AccountType();
      getField(value); return value; }
    public bool isSet(QuickFix.AccountType field)
    { return isSetField(field); }
    public bool isSetAccountType()
    { return isSetField(581); }

    public void set(QuickFix.ClOrdID value)
    { setField(value); }
    public QuickFix.ClOrdID get(QuickFix.ClOrdID  value)
    { getField(value); return value; }
    public QuickFix.ClOrdID getClOrdID()
    { QuickFix.ClOrdID value = new QuickFix.ClOrdID();
      getField(value); return value; }
    public bool isSet(QuickFix.ClOrdID field)
    { return isSetField(field); }
    public bool isSetClOrdID()
    { return isSetField(11); }

    public void set(QuickFix.OrderID value)
    { setField(value); }
    public QuickFix.OrderID get(QuickFix.OrderID  value)
    { getField(value); return value; }
    public QuickFix.OrderID getOrderID()
    { QuickFix.OrderID value = new QuickFix.OrderID();
      getField(value); return value; }
    public bool isSet(QuickFix.OrderID field)
    { return isSetField(field); }
    public bool isSetOrderID()
    { return isSetField(37); }

    public void set(QuickFix.SecondaryOrderID value)
    { setField(value); }
    public QuickFix.SecondaryOrderID get(QuickFix.SecondaryOrderID  value)
    { getField(value); return value; }
    public QuickFix.SecondaryOrderID getSecondaryOrderID()
    { QuickFix.SecondaryOrderID value = new QuickFix.SecondaryOrderID();
      getField(value); return value; }
    public bool isSet(QuickFix.SecondaryOrderID field)
    { return isSetField(field); }
    public bool isSetSecondaryOrderID()
    { return isSetField(198); }

    public void set(QuickFix.SecondaryClOrdID value)
    { setField(value); }
    public QuickFix.SecondaryClOrdID get(QuickFix.SecondaryClOrdID  value)
    { getField(value); return value; }
    public QuickFix.SecondaryClOrdID getSecondaryClOrdID()
    { QuickFix.SecondaryClOrdID value = new QuickFix.SecondaryClOrdID();
      getField(value); return value; }
    public bool isSet(QuickFix.SecondaryClOrdID field)
    { return isSetField(field); }
    public bool isSetSecondaryClOrdID()
    { return isSetField(526); }

    public void set(QuickFix.Symbol value)
    { setField(value); }
    public QuickFix.Symbol get(QuickFix.Symbol  value)
    { getField(value); return value; }
    public QuickFix.Symbol getSymbol()
    { QuickFix.Symbol value = new QuickFix.Symbol();
      getField(value); return value; }
    public bool isSet(QuickFix.Symbol field)
    { return isSetField(field); }
    public bool isSetSymbol()
    { return isSetField(55); }

    public void set(QuickFix.SymbolSfx value)
    { setField(value); }
    public QuickFix.SymbolSfx get(QuickFix.SymbolSfx  value)
    { getField(value); return value; }
    public QuickFix.SymbolSfx getSymbolSfx()
    { QuickFix.SymbolSfx value = new QuickFix.SymbolSfx();
      getField(value); return value; }
    public bool isSet(QuickFix.SymbolSfx field)
    { return isSetField(field); }
    public bool isSetSymbolSfx()
    { return isSetField(65); }

    public void set(QuickFix.SecurityID value)
    { setField(value); }
    public QuickFix.SecurityID get(QuickFix.SecurityID  value)
    { getField(value); return value; }
    public QuickFix.SecurityID getSecurityID()
    { QuickFix.SecurityID value = new QuickFix.SecurityID();
      getField(value); return value; }
    public bool isSet(QuickFix.SecurityID field)
    { return isSetField(field); }
    public bool isSetSecurityID()
    { return isSetField(48); }

    public void set(QuickFix.SecurityIDSource value)
    { setField(value); }
    public QuickFix.SecurityIDSource get(QuickFix.SecurityIDSource  value)
    { getField(value); return value; }
    public QuickFix.SecurityIDSource getSecurityIDSource()
    { QuickFix.SecurityIDSource value = new QuickFix.SecurityIDSource();
      getField(value); return value; }
    public bool isSet(QuickFix.SecurityIDSource field)
    { return isSetField(field); }
    public bool isSetSecurityIDSource()
    { return isSetField(22); }

    public void set(QuickFix.Product value)
    { setField(value); }
    public QuickFix.Product get(QuickFix.Product  value)
    { getField(value); return value; }
    public QuickFix.Product getProduct()
    { QuickFix.Product value = new QuickFix.Product();
      getField(value); return value; }
    public bool isSet(QuickFix.Product field)
    { return isSetField(field); }
    public bool isSetProduct()
    { return isSetField(460); }

    public void set(QuickFix.CFICode value)
    { setField(value); }
    public QuickFix.CFICode get(QuickFix.CFICode  value)
    { getField(value); return value; }
    public QuickFix.CFICode getCFICode()
    { QuickFix.CFICode value = new QuickFix.CFICode();
      getField(value); return value; }
    public bool isSet(QuickFix.CFICode field)
    { return isSetField(field); }
    public bool isSetCFICode()
    { return isSetField(461); }

    public void set(QuickFix.SecurityType value)
    { setField(value); }
    public QuickFix.SecurityType get(QuickFix.SecurityType  value)
    { getField(value); return value; }
    public QuickFix.SecurityType getSecurityType()
    { QuickFix.SecurityType value = new QuickFix.SecurityType();
      getField(value); return value; }
    public bool isSet(QuickFix.SecurityType field)
    { return isSetField(field); }
    public bool isSetSecurityType()
    { return isSetField(167); }

    public void set(QuickFix.SecuritySubType value)
    { setField(value); }
    public QuickFix.SecuritySubType get(QuickFix.SecuritySubType  value)
    { getField(value); return value; }
    public QuickFix.SecuritySubType getSecuritySubType()
    { QuickFix.SecuritySubType value = new QuickFix.SecuritySubType();
      getField(value); return value; }
    public bool isSet(QuickFix.SecuritySubType field)
    { return isSetField(field); }
    public bool isSetSecuritySubType()
    { return isSetField(762); }

    public void set(QuickFix.MaturityMonthYear value)
    { setField(value); }
    public QuickFix.MaturityMonthYear get(QuickFix.MaturityMonthYear  value)
    { getField(value); return value; }
    public QuickFix.MaturityMonthYear getMaturityMonthYear()
    { QuickFix.MaturityMonthYear value = new QuickFix.MaturityMonthYear();
      getField(value); return value; }
    public bool isSet(QuickFix.MaturityMonthYear field)
    { return isSetField(field); }
    public bool isSetMaturityMonthYear()
    { return isSetField(200); }

    public void set(QuickFix.MaturityDate value)
    { setField(value); }
    public QuickFix.MaturityDate get(QuickFix.MaturityDate  value)
    { getField(value); return value; }
    public QuickFix.MaturityDate getMaturityDate()
    { QuickFix.MaturityDate value = new QuickFix.MaturityDate();
      getField(value); return value; }
    public bool isSet(QuickFix.MaturityDate field)
    { return isSetField(field); }
    public bool isSetMaturityDate()
    { return isSetField(541); }

    public void set(QuickFix.CouponPaymentDate value)
    { setField(value); }
    public QuickFix.CouponPaymentDate get(QuickFix.CouponPaymentDate  value)
    { getField(value); return value; }
    public QuickFix.CouponPaymentDate getCouponPaymentDate()
    { QuickFix.CouponPaymentDate value = new QuickFix.CouponPaymentDate();
      getField(value); return value; }
    public bool isSet(QuickFix.CouponPaymentDate field)
    { return isSetField(field); }
    public bool isSetCouponPaymentDate()
    { return isSetField(224); }

    public void set(QuickFix.IssueDate value)
    { setField(value); }
    public QuickFix.IssueDate get(QuickFix.IssueDate  value)
    { getField(value); return value; }
    public QuickFix.IssueDate getIssueDate()
    { QuickFix.IssueDate value = new QuickFix.IssueDate();
      getField(value); return value; }
    public bool isSet(QuickFix.IssueDate field)
    { return isSetField(field); }
    public bool isSetIssueDate()
    { return isSetField(225); }

    public void set(QuickFix.RepoCollateralSecurityType value)
    { setField(value); }
    public QuickFix.RepoCollateralSecurityType get(QuickFix.RepoCollateralSecurityType  value)
    { getField(value); return value; }
    public QuickFix.RepoCollateralSecurityType getRepoCollateralSecurityType()
    { QuickFix.RepoCollateralSecurityType value = new QuickFix.RepoCollateralSecurityType();
      getField(value); return value; }
    public bool isSet(QuickFix.RepoCollateralSecurityType field)
    { return isSetField(field); }
    public bool isSetRepoCollateralSecurityType()
    { return isSetField(239); }

    public void set(QuickFix.RepurchaseTerm value)
    { setField(value); }
    public QuickFix.RepurchaseTerm get(QuickFix.RepurchaseTerm  value)
    { getField(value); return value; }
    public QuickFix.RepurchaseTerm getRepurchaseTerm()
    { QuickFix.RepurchaseTerm value = new QuickFix.RepurchaseTerm();
      getField(value); return value; }
    public bool isSet(QuickFix.RepurchaseTerm field)
    { return isSetField(field); }
    public bool isSetRepurchaseTerm()
    { return isSetField(226); }

    public void set(QuickFix.RepurchaseRate value)
    { setField(value); }
    public QuickFix.RepurchaseRate get(QuickFix.RepurchaseRate  value)
    { getField(value); return value; }
    public QuickFix.RepurchaseRate getRepurchaseRate()
    { QuickFix.RepurchaseRate value = new QuickFix.RepurchaseRate();
      getField(value); return value; }
    public bool isSet(QuickFix.RepurchaseRate field)
    { return isSetField(field); }
    public bool isSetRepurchaseRate()
    { return isSetField(227); }

    public void set(QuickFix.Factor value)
    { setField(value); }
    public QuickFix.Factor get(QuickFix.Factor  value)
    { getField(value); return value; }
    public QuickFix.Factor getFactor()
    { QuickFix.Factor value = new QuickFix.Factor();
      getField(value); return value; }
    public bool isSet(QuickFix.Factor field)
    { return isSetField(field); }
    public bool isSetFactor()
    { return isSetField(228); }

    public void set(QuickFix.CreditRating value)
    { setField(value); }
    public QuickFix.CreditRating get(QuickFix.CreditRating  value)
    { getField(value); return value; }
    public QuickFix.CreditRating getCreditRating()
    { QuickFix.CreditRating value = new QuickFix.CreditRating();
      getField(value); return value; }
    public bool isSet(QuickFix.CreditRating field)
    { return isSetField(field); }
    public bool isSetCreditRating()
    { return isSetField(255); }

    public void set(QuickFix.InstrRegistry value)
    { setField(value); }
    public QuickFix.InstrRegistry get(QuickFix.InstrRegistry  value)
    { getField(value); return value; }
    public QuickFix.InstrRegistry getInstrRegistry()
    { QuickFix.InstrRegistry value = new QuickFix.InstrRegistry();
      getField(value); return value; }
    public bool isSet(QuickFix.InstrRegistry field)
    { return isSetField(field); }
    public bool isSetInstrRegistry()
    { return isSetField(543); }

    public void set(QuickFix.CountryOfIssue value)
    { setField(value); }
    public QuickFix.CountryOfIssue get(QuickFix.CountryOfIssue  value)
    { getField(value); return value; }
    public QuickFix.CountryOfIssue getCountryOfIssue()
    { QuickFix.CountryOfIssue value = new QuickFix.CountryOfIssue();
      getField(value); return value; }
    public bool isSet(QuickFix.CountryOfIssue field)
    { return isSetField(field); }
    public bool isSetCountryOfIssue()
    { return isSetField(470); }

    public void set(QuickFix.StateOrProvinceOfIssue value)
    { setField(value); }
    public QuickFix.StateOrProvinceOfIssue get(QuickFix.StateOrProvinceOfIssue  value)
    { getField(value); return value; }
    public QuickFix.StateOrProvinceOfIssue getStateOrProvinceOfIssue()
    { QuickFix.StateOrProvinceOfIssue value = new QuickFix.StateOrProvinceOfIssue();
      getField(value); return value; }
    public bool isSet(QuickFix.StateOrProvinceOfIssue field)
    { return isSetField(field); }
    public bool isSetStateOrProvinceOfIssue()
    { return isSetField(471); }

    public void set(QuickFix.LocaleOfIssue value)
    { setField(value); }
    public QuickFix.LocaleOfIssue get(QuickFix.LocaleOfIssue  value)
    { getField(value); return value; }
    public QuickFix.LocaleOfIssue getLocaleOfIssue()
    { QuickFix.LocaleOfIssue value = new QuickFix.LocaleOfIssue();
      getField(value); return value; }
    public bool isSet(QuickFix.LocaleOfIssue field)
    { return isSetField(field); }
    public bool isSetLocaleOfIssue()
    { return isSetField(472); }

    public void set(QuickFix.RedemptionDate value)
    { setField(value); }
    public QuickFix.RedemptionDate get(QuickFix.RedemptionDate  value)
    { getField(value); return value; }
    public QuickFix.RedemptionDate getRedemptionDate()
    { QuickFix.RedemptionDate value = new QuickFix.RedemptionDate();
      getField(value); return value; }
    public bool isSet(QuickFix.RedemptionDate field)
    { return isSetField(field); }
    public bool isSetRedemptionDate()
    { return isSetField(240); }

    public void set(QuickFix.StrikePrice value)
    { setField(value); }
    public QuickFix.StrikePrice get(QuickFix.StrikePrice  value)
    { getField(value); return value; }
    public QuickFix.StrikePrice getStrikePrice()
    { QuickFix.StrikePrice value = new QuickFix.StrikePrice();
      getField(value); return value; }
    public bool isSet(QuickFix.StrikePrice field)
    { return isSetField(field); }
    public bool isSetStrikePrice()
    { return isSetField(202); }

    public void set(QuickFix.StrikeCurrency value)
    { setField(value); }
    public QuickFix.StrikeCurrency get(QuickFix.StrikeCurrency  value)
    { getField(value); return value; }
    public QuickFix.StrikeCurrency getStrikeCurrency()
    { QuickFix.StrikeCurrency value = new QuickFix.StrikeCurrency();
      getField(value); return value; }
    public bool isSet(QuickFix.StrikeCurrency field)
    { return isSetField(field); }
    public bool isSetStrikeCurrency()
    { return isSetField(947); }

    public void set(QuickFix.OptAttribute value)
    { setField(value); }
    public QuickFix.OptAttribute get(QuickFix.OptAttribute  value)
    { getField(value); return value; }
    public QuickFix.OptAttribute getOptAttribute()
    { QuickFix.OptAttribute value = new QuickFix.OptAttribute();
      getField(value); return value; }
    public bool isSet(QuickFix.OptAttribute field)
    { return isSetField(field); }
    public bool isSetOptAttribute()
    { return isSetField(206); }

    public void set(QuickFix.ContractMultiplier value)
    { setField(value); }
    public QuickFix.ContractMultiplier get(QuickFix.ContractMultiplier  value)
    { getField(value); return value; }
    public QuickFix.ContractMultiplier getContractMultiplier()
    { QuickFix.ContractMultiplier value = new QuickFix.ContractMultiplier();
      getField(value); return value; }
    public bool isSet(QuickFix.ContractMultiplier field)
    { return isSetField(field); }
    public bool isSetContractMultiplier()
    { return isSetField(231); }

    public void set(QuickFix.CouponRate value)
    { setField(value); }
    public QuickFix.CouponRate get(QuickFix.CouponRate  value)
    { getField(value); return value; }
    public QuickFix.CouponRate getCouponRate()
    { QuickFix.CouponRate value = new QuickFix.CouponRate();
      getField(value); return value; }
    public bool isSet(QuickFix.CouponRate field)
    { return isSetField(field); }
    public bool isSetCouponRate()
    { return isSetField(223); }

    public void set(QuickFix.SecurityExchange value)
    { setField(value); }
    public QuickFix.SecurityExchange get(QuickFix.SecurityExchange  value)
    { getField(value); return value; }
    public QuickFix.SecurityExchange getSecurityExchange()
    { QuickFix.SecurityExchange value = new QuickFix.SecurityExchange();
      getField(value); return value; }
    public bool isSet(QuickFix.SecurityExchange field)
    { return isSetField(field); }
    public bool isSetSecurityExchange()
    { return isSetField(207); }

    public void set(QuickFix.Issuer value)
    { setField(value); }
    public QuickFix.Issuer get(QuickFix.Issuer  value)
    { getField(value); return value; }
    public QuickFix.Issuer getIssuer()
    { QuickFix.Issuer value = new QuickFix.Issuer();
      getField(value); return value; }
    public bool isSet(QuickFix.Issuer field)
    { return isSetField(field); }
    public bool isSetIssuer()
    { return isSetField(106); }

    public void set(QuickFix.EncodedIssuerLen value)
    { setField(value); }
    public QuickFix.EncodedIssuerLen get(QuickFix.EncodedIssuerLen  value)
    { getField(value); return value; }
    public QuickFix.EncodedIssuerLen getEncodedIssuerLen()
    { QuickFix.EncodedIssuerLen value = new QuickFix.EncodedIssuerLen();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedIssuerLen field)
    { return isSetField(field); }
    public bool isSetEncodedIssuerLen()
    { return isSetField(348); }

    public void set(QuickFix.EncodedIssuer value)
    { setField(value); }
    public QuickFix.EncodedIssuer get(QuickFix.EncodedIssuer  value)
    { getField(value); return value; }
    public QuickFix.EncodedIssuer getEncodedIssuer()
    { QuickFix.EncodedIssuer value = new QuickFix.EncodedIssuer();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedIssuer field)
    { return isSetField(field); }
    public bool isSetEncodedIssuer()
    { return isSetField(349); }

    public void set(QuickFix.SecurityDesc value)
    { setField(value); }
    public QuickFix.SecurityDesc get(QuickFix.SecurityDesc  value)
    { getField(value); return value; }
    public QuickFix.SecurityDesc getSecurityDesc()
    { QuickFix.SecurityDesc value = new QuickFix.SecurityDesc();
      getField(value); return value; }
    public bool isSet(QuickFix.SecurityDesc field)
    { return isSetField(field); }
    public bool isSetSecurityDesc()
    { return isSetField(107); }

    public void set(QuickFix.EncodedSecurityDescLen value)
    { setField(value); }
    public QuickFix.EncodedSecurityDescLen get(QuickFix.EncodedSecurityDescLen  value)
    { getField(value); return value; }
    public QuickFix.EncodedSecurityDescLen getEncodedSecurityDescLen()
    { QuickFix.EncodedSecurityDescLen value = new QuickFix.EncodedSecurityDescLen();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedSecurityDescLen field)
    { return isSetField(field); }
    public bool isSetEncodedSecurityDescLen()
    { return isSetField(350); }

    public void set(QuickFix.EncodedSecurityDesc value)
    { setField(value); }
    public QuickFix.EncodedSecurityDesc get(QuickFix.EncodedSecurityDesc  value)
    { getField(value); return value; }
    public QuickFix.EncodedSecurityDesc getEncodedSecurityDesc()
    { QuickFix.EncodedSecurityDesc value = new QuickFix.EncodedSecurityDesc();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedSecurityDesc field)
    { return isSetField(field); }
    public bool isSetEncodedSecurityDesc()
    { return isSetField(351); }

    public void set(QuickFix.Pool value)
    { setField(value); }
    public QuickFix.Pool get(QuickFix.Pool  value)
    { getField(value); return value; }
    public QuickFix.Pool getPool()
    { QuickFix.Pool value = new QuickFix.Pool();
      getField(value); return value; }
    public bool isSet(QuickFix.Pool field)
    { return isSetField(field); }
    public bool isSetPool()
    { return isSetField(691); }

    public void set(QuickFix.ContractSettlMonth value)
    { setField(value); }
    public QuickFix.ContractSettlMonth get(QuickFix.ContractSettlMonth  value)
    { getField(value); return value; }
    public QuickFix.ContractSettlMonth getContractSettlMonth()
    { QuickFix.ContractSettlMonth value = new QuickFix.ContractSettlMonth();
      getField(value); return value; }
    public bool isSet(QuickFix.ContractSettlMonth field)
    { return isSetField(field); }
    public bool isSetContractSettlMonth()
    { return isSetField(667); }

    public void set(QuickFix.CPProgram value)
    { setField(value); }
    public QuickFix.CPProgram get(QuickFix.CPProgram  value)
    { getField(value); return value; }
    public QuickFix.CPProgram getCPProgram()
    { QuickFix.CPProgram value = new QuickFix.CPProgram();
      getField(value); return value; }
    public bool isSet(QuickFix.CPProgram field)
    { return isSetField(field); }
    public bool isSetCPProgram()
    { return isSetField(875); }

    public void set(QuickFix.CPRegType value)
    { setField(value); }
    public QuickFix.CPRegType get(QuickFix.CPRegType  value)
    { getField(value); return value; }
    public QuickFix.CPRegType getCPRegType()
    { QuickFix.CPRegType value = new QuickFix.CPRegType();
      getField(value); return value; }
    public bool isSet(QuickFix.CPRegType field)
    { return isSetField(field); }
    public bool isSetCPRegType()
    { return isSetField(876); }

    public void set(QuickFix.DatedDate value)
    { setField(value); }
    public QuickFix.DatedDate get(QuickFix.DatedDate  value)
    { getField(value); return value; }
    public QuickFix.DatedDate getDatedDate()
    { QuickFix.DatedDate value = new QuickFix.DatedDate();
      getField(value); return value; }
    public bool isSet(QuickFix.DatedDate field)
    { return isSetField(field); }
    public bool isSetDatedDate()
    { return isSetField(873); }

    public void set(QuickFix.InterestAccrualDate value)
    { setField(value); }
    public QuickFix.InterestAccrualDate get(QuickFix.InterestAccrualDate  value)
    { getField(value); return value; }
    public QuickFix.InterestAccrualDate getInterestAccrualDate()
    { QuickFix.InterestAccrualDate value = new QuickFix.InterestAccrualDate();
      getField(value); return value; }
    public bool isSet(QuickFix.InterestAccrualDate field)
    { return isSetField(field); }
    public bool isSetInterestAccrualDate()
    { return isSetField(874); }

    public void set(QuickFix.NoSecurityAltID value)
    { setField(value); }
    public QuickFix.NoSecurityAltID get(QuickFix.NoSecurityAltID  value)
    { getField(value); return value; }
    public QuickFix.NoSecurityAltID getNoSecurityAltID()
    { QuickFix.NoSecurityAltID value = new QuickFix.NoSecurityAltID();
      getField(value); return value; }
    public bool isSet(QuickFix.NoSecurityAltID field)
    { return isSetField(field); }
    public bool isSetNoSecurityAltID()
    { return isSetField(454); }

    public class NoSecurityAltID: QuickFix.Group
    {
    public NoSecurityAltID() : base(454,455,message_order ) {}
    static int[] message_order = new int[] {455,456,0};
      public void set(QuickFix.SecurityAltID value)
      { setField(value); }
      public QuickFix.SecurityAltID get(QuickFix.SecurityAltID  value)
      { getField(value); return value; }
      public QuickFix.SecurityAltID getSecurityAltID()
      { QuickFix.SecurityAltID value = new QuickFix.SecurityAltID();
        getField(value); return value; }
      public bool isSet(QuickFix.SecurityAltID field)
      { return isSetField(field); }
      public bool isSetSecurityAltID()
      { return isSetField(455); }

      public void set(QuickFix.SecurityAltIDSource value)
      { setField(value); }
      public QuickFix.SecurityAltIDSource get(QuickFix.SecurityAltIDSource  value)
      { getField(value); return value; }
      public QuickFix.SecurityAltIDSource getSecurityAltIDSource()
      { QuickFix.SecurityAltIDSource value = new QuickFix.SecurityAltIDSource();
        getField(value); return value; }
      public bool isSet(QuickFix.SecurityAltIDSource field)
      { return isSetField(field); }
      public bool isSetSecurityAltIDSource()
      { return isSetField(456); }

    };
    public void set(QuickFix.NoEvents value)
    { setField(value); }
    public QuickFix.NoEvents get(QuickFix.NoEvents  value)
    { getField(value); return value; }
    public QuickFix.NoEvents getNoEvents()
    { QuickFix.NoEvents value = new QuickFix.NoEvents();
      getField(value); return value; }
    public bool isSet(QuickFix.NoEvents field)
    { return isSetField(field); }
    public bool isSetNoEvents()
    { return isSetField(864); }

    public class NoEvents: QuickFix.Group
    {
    public NoEvents() : base(864,865,message_order ) {}
    static int[] message_order = new int[] {865,866,867,868,0};
      public void set(QuickFix.EventType value)
      { setField(value); }
      public QuickFix.EventType get(QuickFix.EventType  value)
      { getField(value); return value; }
      public QuickFix.EventType getEventType()
      { QuickFix.EventType value = new QuickFix.EventType();
        getField(value); return value; }
      public bool isSet(QuickFix.EventType field)
      { return isSetField(field); }
      public bool isSetEventType()
      { return isSetField(865); }

      public void set(QuickFix.EventDate value)
      { setField(value); }
      public QuickFix.EventDate get(QuickFix.EventDate  value)
      { getField(value); return value; }
      public QuickFix.EventDate getEventDate()
      { QuickFix.EventDate value = new QuickFix.EventDate();
        getField(value); return value; }
      public bool isSet(QuickFix.EventDate field)
      { return isSetField(field); }
      public bool isSetEventDate()
      { return isSetField(866); }

      public void set(QuickFix.EventPx value)
      { setField(value); }
      public QuickFix.EventPx get(QuickFix.EventPx  value)
      { getField(value); return value; }
      public QuickFix.EventPx getEventPx()
      { QuickFix.EventPx value = new QuickFix.EventPx();
        getField(value); return value; }
      public bool isSet(QuickFix.EventPx field)
      { return isSetField(field); }
      public bool isSetEventPx()
      { return isSetField(867); }

      public void set(QuickFix.EventText value)
      { setField(value); }
      public QuickFix.EventText get(QuickFix.EventText  value)
      { getField(value); return value; }
      public QuickFix.EventText getEventText()
      { QuickFix.EventText value = new QuickFix.EventText();
        getField(value); return value; }
      public bool isSet(QuickFix.EventText field)
      { return isSetField(field); }
      public bool isSetEventText()
      { return isSetField(868); }

    };
    public void set(QuickFix.AgreementDesc value)
    { setField(value); }
    public QuickFix.AgreementDesc get(QuickFix.AgreementDesc  value)
    { getField(value); return value; }
    public QuickFix.AgreementDesc getAgreementDesc()
    { QuickFix.AgreementDesc value = new QuickFix.AgreementDesc();
      getField(value); return value; }
    public bool isSet(QuickFix.AgreementDesc field)
    { return isSetField(field); }
    public bool isSetAgreementDesc()
    { return isSetField(913); }

    public void set(QuickFix.AgreementID value)
    { setField(value); }
    public QuickFix.AgreementID get(QuickFix.AgreementID  value)
    { getField(value); return value; }
    public QuickFix.AgreementID getAgreementID()
    { QuickFix.AgreementID value = new QuickFix.AgreementID();
      getField(value); return value; }
    public bool isSet(QuickFix.AgreementID field)
    { return isSetField(field); }
    public bool isSetAgreementID()
    { return isSetField(914); }

    public void set(QuickFix.AgreementDate value)
    { setField(value); }
    public QuickFix.AgreementDate get(QuickFix.AgreementDate  value)
    { getField(value); return value; }
    public QuickFix.AgreementDate getAgreementDate()
    { QuickFix.AgreementDate value = new QuickFix.AgreementDate();
      getField(value); return value; }
    public bool isSet(QuickFix.AgreementDate field)
    { return isSetField(field); }
    public bool isSetAgreementDate()
    { return isSetField(915); }

    public void set(QuickFix.AgreementCurrency value)
    { setField(value); }
    public QuickFix.AgreementCurrency get(QuickFix.AgreementCurrency  value)
    { getField(value); return value; }
    public QuickFix.AgreementCurrency getAgreementCurrency()
    { QuickFix.AgreementCurrency value = new QuickFix.AgreementCurrency();
      getField(value); return value; }
    public bool isSet(QuickFix.AgreementCurrency field)
    { return isSetField(field); }
    public bool isSetAgreementCurrency()
    { return isSetField(918); }

    public void set(QuickFix.TerminationType value)
    { setField(value); }
    public QuickFix.TerminationType get(QuickFix.TerminationType  value)
    { getField(value); return value; }
    public QuickFix.TerminationType getTerminationType()
    { QuickFix.TerminationType value = new QuickFix.TerminationType();
      getField(value); return value; }
    public bool isSet(QuickFix.TerminationType field)
    { return isSetField(field); }
    public bool isSetTerminationType()
    { return isSetField(788); }

    public void set(QuickFix.StartDate value)
    { setField(value); }
    public QuickFix.StartDate get(QuickFix.StartDate  value)
    { getField(value); return value; }
    public QuickFix.StartDate getStartDate()
    { QuickFix.StartDate value = new QuickFix.StartDate();
      getField(value); return value; }
    public bool isSet(QuickFix.StartDate field)
    { return isSetField(field); }
    public bool isSetStartDate()
    { return isSetField(916); }

    public void set(QuickFix.EndDate value)
    { setField(value); }
    public QuickFix.EndDate get(QuickFix.EndDate  value)
    { getField(value); return value; }
    public QuickFix.EndDate getEndDate()
    { QuickFix.EndDate value = new QuickFix.EndDate();
      getField(value); return value; }
    public bool isSet(QuickFix.EndDate field)
    { return isSetField(field); }
    public bool isSetEndDate()
    { return isSetField(917); }

    public void set(QuickFix.DeliveryType value)
    { setField(value); }
    public QuickFix.DeliveryType get(QuickFix.DeliveryType  value)
    { getField(value); return value; }
    public QuickFix.DeliveryType getDeliveryType()
    { QuickFix.DeliveryType value = new QuickFix.DeliveryType();
      getField(value); return value; }
    public bool isSet(QuickFix.DeliveryType field)
    { return isSetField(field); }
    public bool isSetDeliveryType()
    { return isSetField(919); }

    public void set(QuickFix.MarginRatio value)
    { setField(value); }
    public QuickFix.MarginRatio get(QuickFix.MarginRatio  value)
    { getField(value); return value; }
    public QuickFix.MarginRatio getMarginRatio()
    { QuickFix.MarginRatio value = new QuickFix.MarginRatio();
      getField(value); return value; }
    public bool isSet(QuickFix.MarginRatio field)
    { return isSetField(field); }
    public bool isSetMarginRatio()
    { return isSetField(898); }

    public void set(QuickFix.SettlDate value)
    { setField(value); }
    public QuickFix.SettlDate get(QuickFix.SettlDate  value)
    { getField(value); return value; }
    public QuickFix.SettlDate getSettlDate()
    { QuickFix.SettlDate value = new QuickFix.SettlDate();
      getField(value); return value; }
    public bool isSet(QuickFix.SettlDate field)
    { return isSetField(field); }
    public bool isSetSettlDate()
    { return isSetField(64); }

    public void set(QuickFix.Quantity value)
    { setField(value); }
    public QuickFix.Quantity get(QuickFix.Quantity  value)
    { getField(value); return value; }
    public QuickFix.Quantity getQuantity()
    { QuickFix.Quantity value = new QuickFix.Quantity();
      getField(value); return value; }
    public bool isSet(QuickFix.Quantity field)
    { return isSetField(field); }
    public bool isSetQuantity()
    { return isSetField(53); }

    public void set(QuickFix.QtyType value)
    { setField(value); }
    public QuickFix.QtyType get(QuickFix.QtyType  value)
    { getField(value); return value; }
    public QuickFix.QtyType getQtyType()
    { QuickFix.QtyType value = new QuickFix.QtyType();
      getField(value); return value; }
    public bool isSet(QuickFix.QtyType field)
    { return isSetField(field); }
    public bool isSetQtyType()
    { return isSetField(854); }

    public void set(QuickFix.Currency value)
    { setField(value); }
    public QuickFix.Currency get(QuickFix.Currency  value)
    { getField(value); return value; }
    public QuickFix.Currency getCurrency()
    { QuickFix.Currency value = new QuickFix.Currency();
      getField(value); return value; }
    public bool isSet(QuickFix.Currency field)
    { return isSetField(field); }
    public bool isSetCurrency()
    { return isSetField(15); }

    public void set(QuickFix.NoLegs value)
    { setField(value); }
    public QuickFix.NoLegs get(QuickFix.NoLegs  value)
    { getField(value); return value; }
    public QuickFix.NoLegs getNoLegs()
    { QuickFix.NoLegs value = new QuickFix.NoLegs();
      getField(value); return value; }
    public bool isSet(QuickFix.NoLegs field)
    { return isSetField(field); }
    public bool isSetNoLegs()
    { return isSetField(555); }

    public void set(QuickFix.LegSymbol value)
    { setField(value); }
    public QuickFix.LegSymbol get(QuickFix.LegSymbol  value)
    { getField(value); return value; }
    public QuickFix.LegSymbol getLegSymbol()
    { QuickFix.LegSymbol value = new QuickFix.LegSymbol();
      getField(value); return value; }
    public bool isSet(QuickFix.LegSymbol field)
    { return isSetField(field); }
    public bool isSetLegSymbol()
    { return isSetField(600); }

    public void set(QuickFix.LegSymbolSfx value)
    { setField(value); }
    public QuickFix.LegSymbolSfx get(QuickFix.LegSymbolSfx  value)
    { getField(value); return value; }
    public QuickFix.LegSymbolSfx getLegSymbolSfx()
    { QuickFix.LegSymbolSfx value = new QuickFix.LegSymbolSfx();
      getField(value); return value; }
    public bool isSet(QuickFix.LegSymbolSfx field)
    { return isSetField(field); }
    public bool isSetLegSymbolSfx()
    { return isSetField(601); }

    public void set(QuickFix.LegSecurityID value)
    { setField(value); }
    public QuickFix.LegSecurityID get(QuickFix.LegSecurityID  value)
    { getField(value); return value; }
    public QuickFix.LegSecurityID getLegSecurityID()
    { QuickFix.LegSecurityID value = new QuickFix.LegSecurityID();
      getField(value); return value; }
    public bool isSet(QuickFix.LegSecurityID field)
    { return isSetField(field); }
    public bool isSetLegSecurityID()
    { return isSetField(602); }

    public void set(QuickFix.LegSecurityIDSource value)
    { setField(value); }
    public QuickFix.LegSecurityIDSource get(QuickFix.LegSecurityIDSource  value)
    { getField(value); return value; }
    public QuickFix.LegSecurityIDSource getLegSecurityIDSource()
    { QuickFix.LegSecurityIDSource value = new QuickFix.LegSecurityIDSource();
      getField(value); return value; }
    public bool isSet(QuickFix.LegSecurityIDSource field)
    { return isSetField(field); }
    public bool isSetLegSecurityIDSource()
    { return isSetField(603); }

    public void set(QuickFix.LegProduct value)
    { setField(value); }
    public QuickFix.LegProduct get(QuickFix.LegProduct  value)
    { getField(value); return value; }
    public QuickFix.LegProduct getLegProduct()
    { QuickFix.LegProduct value = new QuickFix.LegProduct();
      getField(value); return value; }
    public bool isSet(QuickFix.LegProduct field)
    { return isSetField(field); }
    public bool isSetLegProduct()
    { return isSetField(607); }

    public void set(QuickFix.LegCFICode value)
    { setField(value); }
    public QuickFix.LegCFICode get(QuickFix.LegCFICode  value)
    { getField(value); return value; }
    public QuickFix.LegCFICode getLegCFICode()
    { QuickFix.LegCFICode value = new QuickFix.LegCFICode();
      getField(value); return value; }
    public bool isSet(QuickFix.LegCFICode field)
    { return isSetField(field); }
    public bool isSetLegCFICode()
    { return isSetField(608); }

    public void set(QuickFix.LegSecurityType value)
    { setField(value); }
    public QuickFix.LegSecurityType get(QuickFix.LegSecurityType  value)
    { getField(value); return value; }
    public QuickFix.LegSecurityType getLegSecurityType()
    { QuickFix.LegSecurityType value = new QuickFix.LegSecurityType();
      getField(value); return value; }
    public bool isSet(QuickFix.LegSecurityType field)
    { return isSetField(field); }
    public bool isSetLegSecurityType()
    { return isSetField(609); }

    public void set(QuickFix.LegSecuritySubType value)
    { setField(value); }
    public QuickFix.LegSecuritySubType get(QuickFix.LegSecuritySubType  value)
    { getField(value); return value; }
    public QuickFix.LegSecuritySubType getLegSecuritySubType()
    { QuickFix.LegSecuritySubType value = new QuickFix.LegSecuritySubType();
      getField(value); return value; }
    public bool isSet(QuickFix.LegSecuritySubType field)
    { return isSetField(field); }
    public bool isSetLegSecuritySubType()
    { return isSetField(764); }

    public void set(QuickFix.LegMaturityMonthYear value)
    { setField(value); }
    public QuickFix.LegMaturityMonthYear get(QuickFix.LegMaturityMonthYear  value)
    { getField(value); return value; }
    public QuickFix.LegMaturityMonthYear getLegMaturityMonthYear()
    { QuickFix.LegMaturityMonthYear value = new QuickFix.LegMaturityMonthYear();
      getField(value); return value; }
    public bool isSet(QuickFix.LegMaturityMonthYear field)
    { return isSetField(field); }
    public bool isSetLegMaturityMonthYear()
    { return isSetField(610); }

    public void set(QuickFix.LegMaturityDate value)
    { setField(value); }
    public QuickFix.LegMaturityDate get(QuickFix.LegMaturityDate  value)
    { getField(value); return value; }
    public QuickFix.LegMaturityDate getLegMaturityDate()
    { QuickFix.LegMaturityDate value = new QuickFix.LegMaturityDate();
      getField(value); return value; }
    public bool isSet(QuickFix.LegMaturityDate field)
    { return isSetField(field); }
    public bool isSetLegMaturityDate()
    { return isSetField(611); }

    public void set(QuickFix.LegCouponPaymentDate value)
    { setField(value); }
    public QuickFix.LegCouponPaymentDate get(QuickFix.LegCouponPaymentDate  value)
    { getField(value); return value; }
    public QuickFix.LegCouponPaymentDate getLegCouponPaymentDate()
    { QuickFix.LegCouponPaymentDate value = new QuickFix.LegCouponPaymentDate();
      getField(value); return value; }
    public bool isSet(QuickFix.LegCouponPaymentDate field)
    { return isSetField(field); }
    public bool isSetLegCouponPaymentDate()
    { return isSetField(248); }

    public void set(QuickFix.LegIssueDate value)
    { setField(value); }
    public QuickFix.LegIssueDate get(QuickFix.LegIssueDate  value)
    { getField(value); return value; }
    public QuickFix.LegIssueDate getLegIssueDate()
    { QuickFix.LegIssueDate value = new QuickFix.LegIssueDate();
      getField(value); return value; }
    public bool isSet(QuickFix.LegIssueDate field)
    { return isSetField(field); }
    public bool isSetLegIssueDate()
    { return isSetField(249); }

    public void set(QuickFix.LegRepoCollateralSecurityType value)
    { setField(value); }
    public QuickFix.LegRepoCollateralSecurityType get(QuickFix.LegRepoCollateralSecurityType  value)
    { getField(value); return value; }
    public QuickFix.LegRepoCollateralSecurityType getLegRepoCollateralSecurityType()
    { QuickFix.LegRepoCollateralSecurityType value = new QuickFix.LegRepoCollateralSecurityType();
      getField(value); return value; }
    public bool isSet(QuickFix.LegRepoCollateralSecurityType field)
    { return isSetField(field); }
    public bool isSetLegRepoCollateralSecurityType()
    { return isSetField(250); }

    public void set(QuickFix.LegRepurchaseTerm value)
    { setField(value); }
    public QuickFix.LegRepurchaseTerm get(QuickFix.LegRepurchaseTerm  value)
    { getField(value); return value; }
    public QuickFix.LegRepurchaseTerm getLegRepurchaseTerm()
    { QuickFix.LegRepurchaseTerm value = new QuickFix.LegRepurchaseTerm();
      getField(value); return value; }
    public bool isSet(QuickFix.LegRepurchaseTerm field)
    { return isSetField(field); }
    public bool isSetLegRepurchaseTerm()
    { return isSetField(251); }

    public void set(QuickFix.LegRepurchaseRate value)
    { setField(value); }
    public QuickFix.LegRepurchaseRate get(QuickFix.LegRepurchaseRate  value)
    { getField(value); return value; }
    public QuickFix.LegRepurchaseRate getLegRepurchaseRate()
    { QuickFix.LegRepurchaseRate value = new QuickFix.LegRepurchaseRate();
      getField(value); return value; }
    public bool isSet(QuickFix.LegRepurchaseRate field)
    { return isSetField(field); }
    public bool isSetLegRepurchaseRate()
    { return isSetField(252); }

    public void set(QuickFix.LegFactor value)
    { setField(value); }
    public QuickFix.LegFactor get(QuickFix.LegFactor  value)
    { getField(value); return value; }
    public QuickFix.LegFactor getLegFactor()
    { QuickFix.LegFactor value = new QuickFix.LegFactor();
      getField(value); return value; }
    public bool isSet(QuickFix.LegFactor field)
    { return isSetField(field); }
    public bool isSetLegFactor()
    { return isSetField(253); }

    public void set(QuickFix.LegCreditRating value)
    { setField(value); }
    public QuickFix.LegCreditRating get(QuickFix.LegCreditRating  value)
    { getField(value); return value; }
    public QuickFix.LegCreditRating getLegCreditRating()
    { QuickFix.LegCreditRating value = new QuickFix.LegCreditRating();
      getField(value); return value; }
    public bool isSet(QuickFix.LegCreditRating field)
    { return isSetField(field); }
    public bool isSetLegCreditRating()
    { return isSetField(257); }

    public void set(QuickFix.LegInstrRegistry value)
    { setField(value); }
    public QuickFix.LegInstrRegistry get(QuickFix.LegInstrRegistry  value)
    { getField(value); return value; }
    public QuickFix.LegInstrRegistry getLegInstrRegistry()
    { QuickFix.LegInstrRegistry value = new QuickFix.LegInstrRegistry();
      getField(value); return value; }
    public bool isSet(QuickFix.LegInstrRegistry field)
    { return isSetField(field); }
    public bool isSetLegInstrRegistry()
    { return isSetField(599); }

    public void set(QuickFix.LegCountryOfIssue value)
    { setField(value); }
    public QuickFix.LegCountryOfIssue get(QuickFix.LegCountryOfIssue  value)
    { getField(value); return value; }
    public QuickFix.LegCountryOfIssue getLegCountryOfIssue()
    { QuickFix.LegCountryOfIssue value = new QuickFix.LegCountryOfIssue();
      getField(value); return value; }
    public bool isSet(QuickFix.LegCountryOfIssue field)
    { return isSetField(field); }
    public bool isSetLegCountryOfIssue()
    { return isSetField(596); }

    public void set(QuickFix.LegStateOrProvinceOfIssue value)
    { setField(value); }
    public QuickFix.LegStateOrProvinceOfIssue get(QuickFix.LegStateOrProvinceOfIssue  value)
    { getField(value); return value; }
    public QuickFix.LegStateOrProvinceOfIssue getLegStateOrProvinceOfIssue()
    { QuickFix.LegStateOrProvinceOfIssue value = new QuickFix.LegStateOrProvinceOfIssue();
      getField(value); return value; }
    public bool isSet(QuickFix.LegStateOrProvinceOfIssue field)
    { return isSetField(field); }
    public bool isSetLegStateOrProvinceOfIssue()
    { return isSetField(597); }

    public void set(QuickFix.LegLocaleOfIssue value)
    { setField(value); }
    public QuickFix.LegLocaleOfIssue get(QuickFix.LegLocaleOfIssue  value)
    { getField(value); return value; }
    public QuickFix.LegLocaleOfIssue getLegLocaleOfIssue()
    { QuickFix.LegLocaleOfIssue value = new QuickFix.LegLocaleOfIssue();
      getField(value); return value; }
    public bool isSet(QuickFix.LegLocaleOfIssue field)
    { return isSetField(field); }
    public bool isSetLegLocaleOfIssue()
    { return isSetField(598); }

    public void set(QuickFix.LegRedemptionDate value)
    { setField(value); }
    public QuickFix.LegRedemptionDate get(QuickFix.LegRedemptionDate  value)
    { getField(value); return value; }
    public QuickFix.LegRedemptionDate getLegRedemptionDate()
    { QuickFix.LegRedemptionDate value = new QuickFix.LegRedemptionDate();
      getField(value); return value; }
    public bool isSet(QuickFix.LegRedemptionDate field)
    { return isSetField(field); }
    public bool isSetLegRedemptionDate()
    { return isSetField(254); }

    public void set(QuickFix.LegStrikePrice value)
    { setField(value); }
    public QuickFix.LegStrikePrice get(QuickFix.LegStrikePrice  value)
    { getField(value); return value; }
    public QuickFix.LegStrikePrice getLegStrikePrice()
    { QuickFix.LegStrikePrice value = new QuickFix.LegStrikePrice();
      getField(value); return value; }
    public bool isSet(QuickFix.LegStrikePrice field)
    { return isSetField(field); }
    public bool isSetLegStrikePrice()
    { return isSetField(612); }

    public void set(QuickFix.LegStrikeCurrency value)
    { setField(value); }
    public QuickFix.LegStrikeCurrency get(QuickFix.LegStrikeCurrency  value)
    { getField(value); return value; }
    public QuickFix.LegStrikeCurrency getLegStrikeCurrency()
    { QuickFix.LegStrikeCurrency value = new QuickFix.LegStrikeCurrency();
      getField(value); return value; }
    public bool isSet(QuickFix.LegStrikeCurrency field)
    { return isSetField(field); }
    public bool isSetLegStrikeCurrency()
    { return isSetField(942); }

    public void set(QuickFix.LegOptAttribute value)
    { setField(value); }
    public QuickFix.LegOptAttribute get(QuickFix.LegOptAttribute  value)
    { getField(value); return value; }
    public QuickFix.LegOptAttribute getLegOptAttribute()
    { QuickFix.LegOptAttribute value = new QuickFix.LegOptAttribute();
      getField(value); return value; }
    public bool isSet(QuickFix.LegOptAttribute field)
    { return isSetField(field); }
    public bool isSetLegOptAttribute()
    { return isSetField(613); }

    public void set(QuickFix.LegContractMultiplier value)
    { setField(value); }
    public QuickFix.LegContractMultiplier get(QuickFix.LegContractMultiplier  value)
    { getField(value); return value; }
    public QuickFix.LegContractMultiplier getLegContractMultiplier()
    { QuickFix.LegContractMultiplier value = new QuickFix.LegContractMultiplier();
      getField(value); return value; }
    public bool isSet(QuickFix.LegContractMultiplier field)
    { return isSetField(field); }
    public bool isSetLegContractMultiplier()
    { return isSetField(614); }

    public void set(QuickFix.LegCouponRate value)
    { setField(value); }
    public QuickFix.LegCouponRate get(QuickFix.LegCouponRate  value)
    { getField(value); return value; }
    public QuickFix.LegCouponRate getLegCouponRate()
    { QuickFix.LegCouponRate value = new QuickFix.LegCouponRate();
      getField(value); return value; }
    public bool isSet(QuickFix.LegCouponRate field)
    { return isSetField(field); }
    public bool isSetLegCouponRate()
    { return isSetField(615); }

    public void set(QuickFix.LegSecurityExchange value)
    { setField(value); }
    public QuickFix.LegSecurityExchange get(QuickFix.LegSecurityExchange  value)
    { getField(value); return value; }
    public QuickFix.LegSecurityExchange getLegSecurityExchange()
    { QuickFix.LegSecurityExchange value = new QuickFix.LegSecurityExchange();
      getField(value); return value; }
    public bool isSet(QuickFix.LegSecurityExchange field)
    { return isSetField(field); }
    public bool isSetLegSecurityExchange()
    { return isSetField(616); }

    public void set(QuickFix.LegIssuer value)
    { setField(value); }
    public QuickFix.LegIssuer get(QuickFix.LegIssuer  value)
    { getField(value); return value; }
    public QuickFix.LegIssuer getLegIssuer()
    { QuickFix.LegIssuer value = new QuickFix.LegIssuer();
      getField(value); return value; }
    public bool isSet(QuickFix.LegIssuer field)
    { return isSetField(field); }
    public bool isSetLegIssuer()
    { return isSetField(617); }

    public void set(QuickFix.EncodedLegIssuerLen value)
    { setField(value); }
    public QuickFix.EncodedLegIssuerLen get(QuickFix.EncodedLegIssuerLen  value)
    { getField(value); return value; }
    public QuickFix.EncodedLegIssuerLen getEncodedLegIssuerLen()
    { QuickFix.EncodedLegIssuerLen value = new QuickFix.EncodedLegIssuerLen();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedLegIssuerLen field)
    { return isSetField(field); }
    public bool isSetEncodedLegIssuerLen()
    { return isSetField(618); }

    public void set(QuickFix.EncodedLegIssuer value)
    { setField(value); }
    public QuickFix.EncodedLegIssuer get(QuickFix.EncodedLegIssuer  value)
    { getField(value); return value; }
    public QuickFix.EncodedLegIssuer getEncodedLegIssuer()
    { QuickFix.EncodedLegIssuer value = new QuickFix.EncodedLegIssuer();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedLegIssuer field)
    { return isSetField(field); }
    public bool isSetEncodedLegIssuer()
    { return isSetField(619); }

    public void set(QuickFix.LegSecurityDesc value)
    { setField(value); }
    public QuickFix.LegSecurityDesc get(QuickFix.LegSecurityDesc  value)
    { getField(value); return value; }
    public QuickFix.LegSecurityDesc getLegSecurityDesc()
    { QuickFix.LegSecurityDesc value = new QuickFix.LegSecurityDesc();
      getField(value); return value; }
    public bool isSet(QuickFix.LegSecurityDesc field)
    { return isSetField(field); }
    public bool isSetLegSecurityDesc()
    { return isSetField(620); }

    public void set(QuickFix.EncodedLegSecurityDescLen value)
    { setField(value); }
    public QuickFix.EncodedLegSecurityDescLen get(QuickFix.EncodedLegSecurityDescLen  value)
    { getField(value); return value; }
    public QuickFix.EncodedLegSecurityDescLen getEncodedLegSecurityDescLen()
    { QuickFix.EncodedLegSecurityDescLen value = new QuickFix.EncodedLegSecurityDescLen();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedLegSecurityDescLen field)
    { return isSetField(field); }
    public bool isSetEncodedLegSecurityDescLen()
    { return isSetField(621); }

    public void set(QuickFix.EncodedLegSecurityDesc value)
    { setField(value); }
    public QuickFix.EncodedLegSecurityDesc get(QuickFix.EncodedLegSecurityDesc  value)
    { getField(value); return value; }
    public QuickFix.EncodedLegSecurityDesc getEncodedLegSecurityDesc()
    { QuickFix.EncodedLegSecurityDesc value = new QuickFix.EncodedLegSecurityDesc();
      getField(value); return value; }
    public bool isSet(QuickFix.EncodedLegSecurityDesc field)
    { return isSetField(field); }
    public bool isSetEncodedLegSecurityDesc()
    { return isSetField(622); }

    public void set(QuickFix.LegRatioQty value)
    { setField(value); }
    public QuickFix.LegRatioQty get(QuickFix.LegRatioQty  value)
    { getField(value); return value; }
    public QuickFix.LegRatioQty getLegRatioQty()
    { QuickFix.LegRatioQty value = new QuickFix.LegRatioQty();
      getField(value); return value; }
    public bool isSet(QuickFix.LegRatioQty field)
    { return isSetField(field); }
    public bool isSetLegRatioQty()
    { return isSetField(623); }

    public void set(QuickFix.LegSide value)
    { setField(value); }
    public QuickFix.LegSide get(QuickFix.LegSide  value)
    { getField(value); return value; }
    public QuickFix.LegSide getLegSide()
    { QuickFix.LegSide value = new QuickFix.LegSide();
      getField(value); return value; }
    public bool isSet(QuickFix.LegSide field)
    { return isSetField(field); }
    public bool isSetLegSide()
    { return isSetField(624); }

    public void set(QuickFix.LegCurrency value)
    { setField(value); }
    public QuickFix.LegCurrency get(QuickFix.LegCurrency  value)
    { getField(value); return value; }
    public QuickFix.LegCurrency getLegCurrency()
    { QuickFix.LegCurrency value = new QuickFix.LegCurrency();
      getField(value); return value; }
    public bool isSet(QuickFix.LegCurrency field)
    { return isSetField(field); }
    public bool isSetLegCurrency()
    { return isSetField(556); }

    public void set(QuickFix.LegPool value)
    { setField(value); }
    public QuickFix.LegPool get(QuickFix.LegPool  value)
    { getField(value); return value; }
    public QuickFix.LegPool getLegPool()
    { QuickFix.LegPool value = new QuickFix.LegPool();
      getField(value); return value; }
    public bool isSet(QuickFix.LegPool field)
    { return isSetField(field); }
    public bool isSetLegPool()
    { return isSetField(740); }

    public void set(QuickFix.LegDatedDate value)
    { setField(value); }
    public QuickFix.LegDatedDate get(QuickFix.LegDatedDate  value)
    { getField(value); return value; }
    public QuickFix.LegDatedDate getLegDatedDate()
    { QuickFix.LegDatedDate value = new QuickFix.LegDatedDate();
      getField(value); return value; }
    public bool isSet(QuickFix.LegDatedDate field)
    { return isSetField(field); }
    public bool isSetLegDatedDate()
    { return isSetField(739); }

    public void set(QuickFix.LegContractSettlMonth value)
    { setField(value); }
    public QuickFix.LegContractSettlMonth get(QuickFix.LegContractSettlMonth  value)
    { getField(value); return value; }
    public QuickFix.LegContractSettlMonth getLegContractSettlMonth()
    { QuickFix.LegContractSettlMonth value = new QuickFix.LegContractSettlMonth();
      getField(value); return value; }
    public bool isSet(QuickFix.LegContractSettlMonth field)
    { return isSetField(field); }
    public bool isSetLegContractSettlMonth()
    { return isSetField(955); }

    public void set(QuickFix.LegInterestAccrualDate value)
    { setField(value); }
    public QuickFix.LegInterestAccrualDate get(QuickFix.LegInterestAccrualDate  value)
    { getField(value); return value; }
    public QuickFix.LegInterestAccrualDate getLegInterestAccrualDate()
    { QuickFix.LegInterestAccrualDate value = new QuickFix.LegInterestAccrualDate();
      getField(value); return value; }
    public bool isSet(QuickFix.LegInterestAccrualDate field)
    { return isSetField(field); }
    public bool isSetLegInterestAccrualDate()
    { return isSetField(956); }

    public void set(QuickFix.NoLegSecurityAltID value)
    { setField(value); }
    public QuickFix.NoLegSecurityAltID get(QuickFix.NoLegSecurityAltID  value)
    { getField(value); return value; }
    public QuickFix.NoLegSecurityAltID getNoLegSecurityAltID()
    { QuickFix.NoLegSecurityAltID value = new QuickFix.NoLegSecurityAltID();
      getField(value); return value; }
    public bool isSet(QuickFix.NoLegSecurityAltID field)
    { return isSetField(field); }
    public bool isSetNoLegSecurityAltID()
    { return isSetField(604); }

    public class NoLegSecurityAltID: QuickFix.Group
    {
    public NoLegSecurityAltID() : base(604,605,message_order ) {}
    static int[] message_order = new int[] {605,606,0};
      public void set(QuickFix.LegSecurityAltID value)
      { setField(value); }
      public QuickFix.LegSecurityAltID get(QuickFix.LegSecurityAltID  value)
      { getField(value); return value; }
      public QuickFix.LegSecurityAltID getLegSecurityAltID()
      { QuickFix.LegSecurityAltID value = new QuickFix.LegSecurityAltID();
        getField(value); return value; }
      public bool isSet(QuickFix.LegSecurityAltID field)
      { return isSetField(field); }
      public bool isSetLegSecurityAltID()
      { return isSetField(605); }

      public void set(QuickFix.LegSecurityAltIDSource value)
      { setField(value); }
      public QuickFix.LegSecurityAltIDSource get(QuickFix.LegSecurityAltIDSource  value)
      { getField(value); return value; }
      public QuickFix.LegSecurityAltIDSource getLegSecurityAltIDSource()
      { QuickFix.LegSecurityAltIDSource value = new QuickFix.LegSecurityAltIDSource();
        getField(value); return value; }
      public bool isSet(QuickFix.LegSecurityAltIDSource field)
      { return isSetField(field); }
      public bool isSetLegSecurityAltIDSource()
      { return isSetField(606); }

    };
    public void set(QuickFix.MarginExcess value)
    { setField(value); }
    public QuickFix.MarginExcess get(QuickFix.MarginExcess  value)
    { getField(value); return value; }
    public QuickFix.MarginExcess getMarginExcess()
    { QuickFix.MarginExcess value = new QuickFix.MarginExcess();
      getField(value); return value; }
    public bool isSet(QuickFix.MarginExcess field)
    { return isSetField(field); }
    public bool isSetMarginExcess()
    { return isSetField(899); }

    public void set(QuickFix.TotalNetValue value)
    { setField(value); }
    public QuickFix.TotalNetValue get(QuickFix.TotalNetValue  value)
    { getField(value); return value; }
    public QuickFix.TotalNetValue getTotalNetValue()
    { QuickFix.TotalNetValue value = new QuickFix.TotalNetValue();
      getField(value); return value; }
    public bool isSet(QuickFix.TotalNetValue field)
    { return isSetField(field); }
    public bool isSetTotalNetValue()
    { return isSetField(900); }

    public void set(QuickFix.CashOutstanding value)
    { setField(value); }
    public QuickFix.CashOutstanding get(QuickFix.CashOutstanding  value)
    { getField(value); return value; }
    public QuickFix.CashOutstanding getCashOutstanding()
    { QuickFix.CashOutstanding value = new QuickFix.CashOutstanding();
      getField(value); return value; }
    public bool isSet(QuickFix.CashOutstanding field)
    { return isSetField(field); }
    public bool isSetCashOutstanding()
    { return isSetField(901); }

    public void set(QuickFix.NoTrdRegTimestamps value)
    { setField(value); }
    public QuickFix.NoTrdRegTimestamps get(QuickFix.NoTrdRegTimestamps  value)
    { getField(value); return value; }
    public QuickFix.NoTrdRegTimestamps getNoTrdRegTimestamps()
    { QuickFix.NoTrdRegTimestamps value = new QuickFix.NoTrdRegTimestamps();
      getField(value); return value; }
    public bool isSet(QuickFix.NoTrdRegTimestamps field)
    { return isSetField(field); }
    public bool isSetNoTrdRegTimestamps()
    { return isSetField(768); }

    public class NoTrdRegTimestamps: QuickFix.Group
    {
    public NoTrdRegTimestamps() : base(768,769,message_order ) {}
    static int[] message_order = new int[] {769,770,771,0};
      public void set(QuickFix.TrdRegTimestamp value)
      { setField(value); }
      public QuickFix.TrdRegTimestamp get(QuickFix.TrdRegTimestamp  value)
      { getField(value); return value; }
      public QuickFix.TrdRegTimestamp getTrdRegTimestamp()
      { QuickFix.TrdRegTimestamp value = new QuickFix.TrdRegTimestamp();
        getField(value); return value; }
      public bool isSet(QuickFix.TrdRegTimestamp field)
      { return isSetField(field); }
      public bool isSetTrdRegTimestamp()
      { return isSetField(769); }

      public void set(QuickFix.TrdRegTimestampType value)
      { setField(value); }
      public QuickFix.TrdRegTimestampType get(QuickFix.TrdRegTimestampType  value)
      { getField(value); return value; }
      public QuickFix.TrdRegTimestampType getTrdRegTimestampType()
      { QuickFix.TrdRegTimestampType value = new QuickFix.TrdRegTimestampType();
        getField(value); return value; }
      public bool isSet(QuickFix.TrdRegTimestampType field)
      { return isSetField(field); }
      public bool isSetTrdRegTimestampType()
      { return isSetField(770); }

      public void set(QuickFix.TrdRegTimestampOrigin value)
      { setField(value); }
      public QuickFix.TrdRegTimestampOrigin get(QuickFix.TrdRegTimestampOrigin  value)
      { getField(value); return value; }
      public QuickFix.TrdRegTimestampOrigin getTrdRegTimestampOrigin()
      { QuickFix.TrdRegTimestampOrigin value = new QuickFix.TrdRegTimestampOrigin();
        getField(value); return value; }
      public bool isSet(QuickFix.TrdRegTimestampOrigin field)
      { return isSetField(field); }
      public bool isSetTrdRegTimestampOrigin()
      { return isSetField(771); }

    };
    public void set(QuickFix.Side value)
    { setField(value); }
    public QuickFix.Side get(QuickFix.Side  value)
    { getField(value); return value; }
    public QuickFix.Side getSide()
    { QuickFix.Side value = new QuickFix.Side();
      getField(value); return value; }
    public bool isSet(QuickFix.Side field)
    { return isSetField(field); }
    public bool isSetSide()
    { return isSetField(54); }

    public void set(QuickFix.Price value)
    { setField(value); }
    public QuickFix.Price get(QuickFix.Price  value)
    { getField(value); return value; }
    public QuickFix.Price getPrice()
    { QuickFix.Price value = new QuickFix.Price();
      getField(value); return value; }
    public bool isSet(QuickFix.Price field)
    { return isSetField(field); }
    public bool isSetPrice()
    { return isSetField(44); }

    public void set(QuickFix.PriceType value)
    { setField(value); }
    public QuickFix.PriceType get(QuickFix.PriceType  value)
    { getField(value); return value; }
    public QuickFix.PriceType getPriceType()
    { QuickFix.PriceType value = new QuickFix.PriceType();
      getField(value); return value; }
    public bool isSet(QuickFix.PriceType field)
    { return isSetField(field); }
    public bool isSetPriceType()
    { return isSetField(423); }

    public void set(QuickFix.AccruedInterestAmt value)
    { setField(value); }
    public QuickFix.AccruedInterestAmt get(QuickFix.AccruedInterestAmt  value)
    { getField(value); return value; }
    public QuickFix.AccruedInterestAmt getAccruedInterestAmt()
    { QuickFix.AccruedInterestAmt value = new QuickFix.AccruedInterestAmt();
      getField(value); return value; }
    public bool isSet(QuickFix.AccruedInterestAmt field)
    { return isSetField(field); }
    public bool isSetAccruedInterestAmt()
    { return isSetField(159); }

    public void set(QuickFix.EndAccruedInterestAmt value)
    { setField(value); }
    public QuickFix.EndAccruedInterestAmt get(QuickFix.EndAccruedInterestAmt  value)
    { getField(value); return value; }
    public QuickFix.EndAccruedInterestAmt getEndAccruedInterestAmt()
    { QuickFix.EndAccruedInterestAmt value = new QuickFix.EndAccruedInterestAmt();
      getField(value); return value; }
    public bool isSet(QuickFix.EndAccruedInterestAmt field)
    { return isSetField(field); }
    public bool isSetEndAccruedInterestAmt()
    { return isSetField(920); }

    public void set(QuickFix.StartCash value)
    { setField(value); }
    public QuickFix.StartCash get(QuickFix.StartCash  value)
    { getField(value); return value; }
    public QuickFix.StartCash getStartCash()
    { QuickFix.StartCash value = new QuickFix.StartCash();
      getField(value); return value; }
    public bool isSet(QuickFix.StartCash field)
    { return isSetField(field); }
    public bool isSetStartCash()
    { return isSetField(921); }

    public void set(QuickFix.EndCash value)
    { setField(value); }
    public QuickFix.EndCash get(QuickFix.EndCash  value)
    { getField(value); return value; }
    public QuickFix.EndCash getEndCash()
    { QuickFix.EndCash value = new QuickFix.EndCash();
      getField(value); return value; }
    public bool isSet(QuickFix.EndCash field)
    { return isSetField(field); }
    public bool isSetEndCash()
    { return isSetField(922); }

    public void set(QuickFix.Spread value)
    { setField(value); }
    public QuickFix.Spread get(QuickFix.Spread  value)
    { getField(value); return value; }
    public QuickFix.Spread getSpread()
    { QuickFix.Spread value = new QuickFix.Spread();
      getField(value); return value; }
    public bool isSet(QuickFix.Spread field)
    { return isSetField(field); }
    public bool isSetSpread()
    { return isSetField(218); }

    public void set(QuickFix.BenchmarkCurveCurrency value)
    { setField(value); }
    public QuickFix.BenchmarkCurveCurrency get(QuickFix.BenchmarkCurveCurrency  value)
    { getField(value); return value; }
    public QuickFix.BenchmarkCurveCurrency getBenchmarkCurveCurrency()
    { QuickFix.BenchmarkCurveCurrency value = new QuickFix.BenchmarkCurveCurrency();
      getField(value); return value; }
    public bool isSet(QuickFix.BenchmarkCurveCurrency field)
    { return isSetField(field); }
    public bool isSetBenchmarkCurveCurrency()
    { return isSetField(220); }

    public void set(QuickFix.BenchmarkCurveName value)
    { setField(value); }
    public QuickFix.BenchmarkCurveName get(QuickFix.BenchmarkCurveName  value)
    { getField(value); return value; }
    public QuickFix.BenchmarkCurveName getBenchmarkCurveName()
    { QuickFix.BenchmarkCurveName value = new QuickFix.BenchmarkCurveName();
      getField(value); return value; }
    public bool isSet(QuickFix.BenchmarkCurveName field)
    { return isSetField(field); }
    public bool isSetBenchmarkCurveName()
    { return isSetField(221); }

    public void set(QuickFix.BenchmarkCurvePoint value)
    { setField(value); }
    public QuickFix.BenchmarkCurvePoint get(QuickFix.BenchmarkCurvePoint  value)
    { getField(value); return value; }
    public QuickFix.BenchmarkCurvePoint getBenchmarkCurvePoint()
    { QuickFix.BenchmarkCurvePoint value = new QuickFix.BenchmarkCurvePoint();
      getField(value); return value; }
    public bool isSet(QuickFix.BenchmarkCurvePoint field)
    { return isSetField(field); }
    public bool isSetBenchmarkCurvePoint()
    { return isSetField(222); }

    public void set(QuickFix.BenchmarkPrice value)
    { setField(value); }
    public QuickFix.BenchmarkPrice get(QuickFix.BenchmarkPrice  value)
    { getField(value); return value; }
    public QuickFix.BenchmarkPrice getBenchmarkPrice()
    { QuickFix.BenchmarkPrice value = new QuickFix.BenchmarkPrice();
      getField(value); return value; }
    public bool isSet(QuickFix.BenchmarkPrice field)
    { return isSetField(field); }
    public bool isSetBenchmarkPrice()
    { return isSetField(662); }

    public void set(QuickFix.BenchmarkPriceType value)
    { setField(value); }
    public QuickFix.BenchmarkPriceType get(QuickFix.BenchmarkPriceType  value)
    { getField(value); return value; }
    public QuickFix.BenchmarkPriceType getBenchmarkPriceType()
    { QuickFix.BenchmarkPriceType value = new QuickFix.BenchmarkPriceType();
      getField(value); return value; }
    public bool isSet(QuickFix.BenchmarkPriceType field)
    { return isSetField(field); }
    public bool isSetBenchmarkPriceType()
    { return isSetField(663); }

    public void set(QuickFix.BenchmarkSecurityID value)
    { setField(value); }
    public QuickFix.BenchmarkSecurityID get(QuickFix.BenchmarkSecurityID  value)
    { getField(value); return value; }
    public QuickFix.BenchmarkSecurityID getBenchmarkSecurityID()
    { QuickFix.BenchmarkSecurityID value = new QuickFix.BenchmarkSecurityID();
      getField(value); return value; }
    public bool isSet(QuickFix.BenchmarkSecurityID field)
    { return isSetField(field); }
    public bool isSetBenchmarkSecurityID()
    { return isSetField(699); }

    public void set(QuickFix.BenchmarkSecurityIDSource value)
    { setField(value); }
    public QuickFix.BenchmarkSecurityIDSource get(QuickFix.BenchmarkSecurityIDSource  value)
    { getField(value); return value; }
    public QuickFix.BenchmarkSecurityIDSource getBenchmarkSecurityIDSource()
    { QuickFix.BenchmarkSecurityIDSource value = new QuickFix.BenchmarkSecurityIDSource();
      getField(value); return value; }
    public bool isSet(QuickFix.BenchmarkSecurityIDSource field)
    { return isSetField(field); }
    public bool isSetBenchmarkSecurityIDSource()
    { return isSetField(761); }

    public void set(QuickFix.NoStipulations value)
    { setField(value); }
    public QuickFix.NoStipulations get(QuickFix.NoStipulations  value)
    { getField(value); return value; }
    public QuickFix.NoStipulations getNoStipulations()
    { QuickFix.NoStipulations value = new QuickFix.NoStipulations();
      getField(value); return value; }
    public bool isSet(QuickFix.NoStipulations field)
    { return isSetField(field); }
    public bool isSetNoStipulations()
    { return isSetField(232); }

    public class NoStipulations: QuickFix.Group
    {
    public NoStipulations() : base(232,233,message_order ) {}
    static int[] message_order = new int[] {233,234,0};
      public void set(QuickFix.StipulationType value)
      { setField(value); }
      public QuickFix.StipulationType get(QuickFix.StipulationType  value)
      { getField(value); return value; }
      public QuickFix.StipulationType getStipulationType()
      { QuickFix.StipulationType value = new QuickFix.StipulationType();
        getField(value); return value; }
      public bool isSet(QuickFix.StipulationType field)
      { return isSetField(field); }
      public bool isSetStipulationType()
      { return isSetField(233); }

      public void set(QuickFix.StipulationValue value)
      { setField(value); }
      public QuickFix.StipulationValue get(QuickFix.StipulationValue  value)
      { getField(value); return value; }
      public QuickFix.StipulationValue getStipulationValue()
      { QuickFix.StipulationValue value = new QuickFix.StipulationValue();
        getField(value); return value; }
      public bool isSet(QuickFix.StipulationValue field)
      { return isSetField(field); }
      public bool isSetStipulationValue()
      { return isSetField(234); }

    };
    public void set(QuickFix.SettlDeliveryType value)
    { setField(value); }
    public QuickFix.SettlDeliveryType get(QuickFix.SettlDeliveryType  value)
    { getField(value); return value; }
    public QuickFix.SettlDeliveryType getSettlDeliveryType()
    { QuickFix.SettlDeliveryType value = new QuickFix.SettlDeliveryType();
      getField(value); return value; }
    public bool isSet(QuickFix.SettlDeliveryType field)
    { return isSetField(field); }
    public bool isSetSettlDeliveryType()
    { return isSetField(172); }

    public void set(QuickFix.StandInstDbType value)
    { setField(value); }
    public QuickFix.StandInstDbType get(QuickFix.StandInstDbType  value)
    { getField(value); return value; }
    public QuickFix.StandInstDbType getStandInstDbType()
    { QuickFix.StandInstDbType value = new QuickFix.StandInstDbType();
      getField(value); return value; }
    public bool isSet(QuickFix.StandInstDbType field)
    { return isSetField(field); }
    public bool isSetStandInstDbType()
    { return isSetField(169); }

    public void set(QuickFix.StandInstDbName value)
    { setField(value); }
    public QuickFix.StandInstDbName get(QuickFix.StandInstDbName  value)
    { getField(value); return value; }
    public QuickFix.StandInstDbName getStandInstDbName()
    { QuickFix.StandInstDbName value = new QuickFix.StandInstDbName();
      getField(value); return value; }
    public bool isSet(QuickFix.StandInstDbName field)
    { return isSetField(field); }
    public bool isSetStandInstDbName()
    { return isSetField(170); }

    public void set(QuickFix.StandInstDbID value)
    { setField(value); }
    public QuickFix.StandInstDbID get(QuickFix.StandInstDbID  value)
    { getField(value); return value; }
    public QuickFix.StandInstDbID getStandInstDbID()
    { QuickFix.StandInstDbID value = new QuickFix.StandInstDbID();
      getField(value); return value; }
    public bool isSet(QuickFix.StandInstDbID field)
    { return isSetField(field); }
    public bool isSetStandInstDbID()
    { return isSetField(171); }

    public void set(QuickFix.NoDlvyInst value)
    { setField(value); }
    public QuickFix.NoDlvyInst get(QuickFix.NoDlvyInst  value)
    { getField(value); return value; }
    public QuickFix.NoDlvyInst getNoDlvyInst()
    { QuickFix.NoDlvyInst value = new QuickFix.NoDlvyInst();
      getField(value); return value; }
    public bool isSet(QuickFix.NoDlvyInst field)
    { return isSetField(field); }
    public bool isSetNoDlvyInst()
    { return isSetField(85); }

    public class NoDlvyInst: QuickFix.Group
    {
    public NoDlvyInst() : base(85,165,message_order ) {}
    static int[] message_order = new int[] {165,787,781,0};
      public void set(QuickFix.SettlInstSource value)
      { setField(value); }
      public QuickFix.SettlInstSource get(QuickFix.SettlInstSource  value)
      { getField(value); return value; }
      public QuickFix.SettlInstSource getSettlInstSource()
      { QuickFix.SettlInstSource value = new QuickFix.SettlInstSource();
        getField(value); return value; }
      public bool isSet(QuickFix.SettlInstSource field)
      { return isSetField(field); }
      public bool isSetSettlInstSource()
      { return isSetField(165); }

      public void set(QuickFix.DlvyInstType value)
      { setField(value); }
      public QuickFix.DlvyInstType get(QuickFix.DlvyInstType  value)
      { getField(value); return value; }
      public QuickFix.DlvyInstType getDlvyInstType()
      { QuickFix.DlvyInstType value = new QuickFix.DlvyInstType();
        getField(value); return value; }
      public bool isSet(QuickFix.DlvyInstType field)
      { return isSetField(field); }
      public bool isSetDlvyInstType()
      { return isSetField(787); }

      public void set(QuickFix.NoSettlPartyIDs value)
      { setField(value); }
      public QuickFix.NoSettlPartyIDs get(QuickFix.NoSettlPartyIDs  value)
      { getField(value); return value; }
      public QuickFix.NoSettlPartyIDs getNoSettlPartyIDs()
      { QuickFix.NoSettlPartyIDs value = new QuickFix.NoSettlPartyIDs();
        getField(value); return value; }
      public bool isSet(QuickFix.NoSettlPartyIDs field)
      { return isSetField(field); }
      public bool isSetNoSettlPartyIDs()
      { return isSetField(781); }

      public class NoSettlPartyIDs: QuickFix.Group
      {
      public NoSettlPartyIDs() : base(781,782,message_order ) {}
      static int[] message_order = new int[] {782,783,784,801,0};
        public void set(QuickFix.SettlPartyID value)
        { setField(value); }
        public QuickFix.SettlPartyID get(QuickFix.SettlPartyID  value)
        { getField(value); return value; }
        public QuickFix.SettlPartyID getSettlPartyID()
        { QuickFix.SettlPartyID value = new QuickFix.SettlPartyID();
          getField(value); return value; }
        public bool isSet(QuickFix.SettlPartyID field)
        { return isSetField(field); }
        public bool isSetSettlPartyID()
        { return isSetField(782); }

        public void set(QuickFix.SettlPartyIDSource value)
        { setField(value); }
        public QuickFix.SettlPartyIDSource get(QuickFix.SettlPartyIDSource  value)
        { getField(value); return value; }
        public QuickFix.SettlPartyIDSource getSettlPartyIDSource()
        { QuickFix.SettlPartyIDSource value = new QuickFix.SettlPartyIDSource();
          getField(value); return value; }
        public bool isSet(QuickFix.SettlPartyIDSource field)
        { return isSetField(field); }
        public bool isSetSettlPartyIDSource()
        { return isSetField(783); }

        public void set(QuickFix.SettlPartyRole value)
        { setField(value); }
        public QuickFix.SettlPartyRole get(QuickFix.SettlPartyRole  value)
        { getField(value); return value; }
        public QuickFix.SettlPartyRole getSettlPartyRole()
        { QuickFix.SettlPartyRole value = new QuickFix.SettlPartyRole();
          getField(value); return value; }
        public bool isSet(QuickFix.SettlPartyRole field)
        { return isSetField(field); }
        public bool isSetSettlPartyRole()
        { return isSetField(784); }

        public void set(QuickFix.NoSettlPartySubIDs value)
        { setField(value); }
        public QuickFix.NoSettlPartySubIDs get(QuickFix.NoSettlPartySubIDs  value)
        { getField(value); return value; }
        public QuickFix.NoSettlPartySubIDs getNoSettlPartySubIDs()
        { QuickFix.NoSettlPartySubIDs value = new QuickFix.NoSettlPartySubIDs();
          getField(value); return value; }
        public bool isSet(QuickFix.NoSettlPartySubIDs field)
        { return isSetField(field); }
        public bool isSetNoSettlPartySubIDs()
        { return isSetField(801); }

        public class NoSettlPartySubIDs: QuickFix.Group
        {
        public NoSettlPartySubIDs() : base(801,785,message_order ) {}
        static int[] message_order = new int[] {785,786,0};
          public void set(QuickFix.SettlPartySubID value)
          { setField(value); }
          public QuickFix.SettlPartySubID get(QuickFix.SettlPartySubID  value)
          { getField(value); return value; }
          public QuickFix.SettlPartySubID getSettlPartySubID()
          { QuickFix.SettlPartySubID value = new QuickFix.SettlPartySubID();
            getField(value); return value; }
          public bool isSet(QuickFix.SettlPartySubID field)
          { return isSetField(field); }
          public bool isSetSettlPartySubID()
          { return isSetField(785); }

          public void set(QuickFix.SettlPartySubIDType value)
          { setField(value); }
          public QuickFix.SettlPartySubIDType get(QuickFix.SettlPartySubIDType  value)
          { getField(value); return value; }
          public QuickFix.SettlPartySubIDType getSettlPartySubIDType()
          { QuickFix.SettlPartySubIDType value = new QuickFix.SettlPartySubIDType();
            getField(value); return value; }
          public bool isSet(QuickFix.SettlPartySubIDType field)
          { return isSetField(field); }
          public bool isSetSettlPartySubIDType()
          { return isSetField(786); }

        };
      };
    };
    public void set(QuickFix.TradingSessionID value)
    { setField(value); }
    public QuickFix.TradingSessionID get(QuickFix.TradingSessionID  value)
    { getField(value); return value; }
    public QuickFix.TradingSessionID getTradingSessionID()
    { QuickFix.TradingSessionID value = new QuickFix.TradingSessionID();
      getField(value); return value; }
    public bool isSet(QuickFix.TradingSessionID field)
    { return isSetField(field); }
    public bool isSetTradingSessionID()
    { return isSetField(336); }

    public void set(QuickFix.TradingSessionSubID value)
    { setField(value); }
    public QuickFix.TradingSessionSubID get(QuickFix.TradingSessionSubID  value)
    { getField(value); return value; }
    public QuickFix.TradingSessionSubID getTradingSessionSubID()
    { QuickFix.TradingSessionSubID value = new QuickFix.TradingSessionSubID();
      getField(value); return value; }
    public bool isSet(QuickFix.TradingSessionSubID field)
    { return isSetField(field); }
    public bool isSetTradingSessionSubID()
    { return isSetField(625); }

    public void set(QuickFix.SettlSessID value)
    { setField(value); }
    public QuickFix.SettlSessID get(QuickFix.SettlSessID  value)
    { getField(value); return value; }
    public QuickFix.SettlSessID getSettlSessID()
    { QuickFix.SettlSessID value = new QuickFix.SettlSessID();
      getField(value); return value; }
    public bool isSet(QuickFix.SettlSessID field)
    { return isSetField(field); }
    public bool isSetSettlSessID()
    { return isSetField(716); }

    public void set(QuickFix.SettlSessSubID value)
    { setField(value); }
    public QuickFix.SettlSessSubID get(QuickFix.SettlSessSubID  value)
    { getField(value); return value; }
    public QuickFix.SettlSessSubID getSettlSessSubID()
    { QuickFix.SettlSessSubID value = new QuickFix.SettlSessSubID();
      getField(value); return value; }
    public bool isSet(QuickFix.SettlSessSubID field)
    { return isSetField(field); }
    public bool isSetSettlSessSubID()
    { return isSetField(717); }

    public void set(QuickFix.ClearingBusinessDate value)
    { setField(value); }
    public QuickFix.ClearingBusinessDate get(QuickFix.ClearingBusinessDate  value)
    { getField(value); return value; }
    public QuickFix.ClearingBusinessDate getClearingBusinessDate()
    { QuickFix.ClearingBusinessDate value = new QuickFix.ClearingBusinessDate();
      getField(value); return value; }
    public bool isSet(QuickFix.ClearingBusinessDate field)
    { return isSetField(field); }
    public bool isSetClearingBusinessDate()
    { return isSetField(715); }

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

    public void set(QuickFix.NoExecs value)
    { setField(value); }
    public QuickFix.NoExecs get(QuickFix.NoExecs  value)
    { getField(value); return value; }
    public QuickFix.NoExecs getNoExecs()
    { QuickFix.NoExecs value = new QuickFix.NoExecs();
      getField(value); return value; }
    public bool isSet(QuickFix.NoExecs field)
    { return isSetField(field); }
    public bool isSetNoExecs()
    { return isSetField(124); }

    public class NoExecs: QuickFix.Group
    {
    public NoExecs() : base(124,17,message_order ) {}
    static int[] message_order = new int[] {17,0};
      public void set(QuickFix.ExecID value)
      { setField(value); }
      public QuickFix.ExecID get(QuickFix.ExecID  value)
      { getField(value); return value; }
      public QuickFix.ExecID getExecID()
      { QuickFix.ExecID value = new QuickFix.ExecID();
        getField(value); return value; }
      public bool isSet(QuickFix.ExecID field)
      { return isSetField(field); }
      public bool isSetExecID()
      { return isSetField(17); }

    };
    public void set(QuickFix.NoTrades value)
    { setField(value); }
    public QuickFix.NoTrades get(QuickFix.NoTrades  value)
    { getField(value); return value; }
    public QuickFix.NoTrades getNoTrades()
    { QuickFix.NoTrades value = new QuickFix.NoTrades();
      getField(value); return value; }
    public bool isSet(QuickFix.NoTrades field)
    { return isSetField(field); }
    public bool isSetNoTrades()
    { return isSetField(897); }

    public class NoTrades: QuickFix.Group
    {
    public NoTrades() : base(897,571,message_order ) {}
    static int[] message_order = new int[] {571,818,0};
      public void set(QuickFix.TradeReportID value)
      { setField(value); }
      public QuickFix.TradeReportID get(QuickFix.TradeReportID  value)
      { getField(value); return value; }
      public QuickFix.TradeReportID getTradeReportID()
      { QuickFix.TradeReportID value = new QuickFix.TradeReportID();
        getField(value); return value; }
      public bool isSet(QuickFix.TradeReportID field)
      { return isSetField(field); }
      public bool isSetTradeReportID()
      { return isSetField(571); }

      public void set(QuickFix.SecondaryTradeReportID value)
      { setField(value); }
      public QuickFix.SecondaryTradeReportID get(QuickFix.SecondaryTradeReportID  value)
      { getField(value); return value; }
      public QuickFix.SecondaryTradeReportID getSecondaryTradeReportID()
      { QuickFix.SecondaryTradeReportID value = new QuickFix.SecondaryTradeReportID();
        getField(value); return value; }
      public bool isSet(QuickFix.SecondaryTradeReportID field)
      { return isSetField(field); }
      public bool isSetSecondaryTradeReportID()
      { return isSetField(818); }

    };
    public void set(QuickFix.NoUnderlyings value)
    { setField(value); }
    public QuickFix.NoUnderlyings get(QuickFix.NoUnderlyings  value)
    { getField(value); return value; }
    public QuickFix.NoUnderlyings getNoUnderlyings()
    { QuickFix.NoUnderlyings value = new QuickFix.NoUnderlyings();
      getField(value); return value; }
    public bool isSet(QuickFix.NoUnderlyings field)
    { return isSetField(field); }
    public bool isSetNoUnderlyings()
    { return isSetField(711); }

    public class NoUnderlyings: QuickFix.Group
    {
    public NoUnderlyings() : base(711,311,message_order ) {}
    static int[] message_order = new int[] {311,312,309,305,457,462,463,310,763,313,542,241,242,243,244,245,246,256,595,592,593,594,247,316,941,317,436,435,308,306,362,363,307,364,365,877,878,318,879,810,882,883,884,885,886,0};
      public void set(QuickFix.UnderlyingSymbol value)
      { setField(value); }
      public QuickFix.UnderlyingSymbol get(QuickFix.UnderlyingSymbol  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingSymbol getUnderlyingSymbol()
      { QuickFix.UnderlyingSymbol value = new QuickFix.UnderlyingSymbol();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingSymbol field)
      { return isSetField(field); }
      public bool isSetUnderlyingSymbol()
      { return isSetField(311); }

      public void set(QuickFix.UnderlyingSymbolSfx value)
      { setField(value); }
      public QuickFix.UnderlyingSymbolSfx get(QuickFix.UnderlyingSymbolSfx  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingSymbolSfx getUnderlyingSymbolSfx()
      { QuickFix.UnderlyingSymbolSfx value = new QuickFix.UnderlyingSymbolSfx();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingSymbolSfx field)
      { return isSetField(field); }
      public bool isSetUnderlyingSymbolSfx()
      { return isSetField(312); }

      public void set(QuickFix.UnderlyingSecurityID value)
      { setField(value); }
      public QuickFix.UnderlyingSecurityID get(QuickFix.UnderlyingSecurityID  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingSecurityID getUnderlyingSecurityID()
      { QuickFix.UnderlyingSecurityID value = new QuickFix.UnderlyingSecurityID();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingSecurityID field)
      { return isSetField(field); }
      public bool isSetUnderlyingSecurityID()
      { return isSetField(309); }

      public void set(QuickFix.UnderlyingSecurityIDSource value)
      { setField(value); }
      public QuickFix.UnderlyingSecurityIDSource get(QuickFix.UnderlyingSecurityIDSource  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingSecurityIDSource getUnderlyingSecurityIDSource()
      { QuickFix.UnderlyingSecurityIDSource value = new QuickFix.UnderlyingSecurityIDSource();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingSecurityIDSource field)
      { return isSetField(field); }
      public bool isSetUnderlyingSecurityIDSource()
      { return isSetField(305); }

      public void set(QuickFix.UnderlyingProduct value)
      { setField(value); }
      public QuickFix.UnderlyingProduct get(QuickFix.UnderlyingProduct  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingProduct getUnderlyingProduct()
      { QuickFix.UnderlyingProduct value = new QuickFix.UnderlyingProduct();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingProduct field)
      { return isSetField(field); }
      public bool isSetUnderlyingProduct()
      { return isSetField(462); }

      public void set(QuickFix.UnderlyingCFICode value)
      { setField(value); }
      public QuickFix.UnderlyingCFICode get(QuickFix.UnderlyingCFICode  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingCFICode getUnderlyingCFICode()
      { QuickFix.UnderlyingCFICode value = new QuickFix.UnderlyingCFICode();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingCFICode field)
      { return isSetField(field); }
      public bool isSetUnderlyingCFICode()
      { return isSetField(463); }

      public void set(QuickFix.UnderlyingSecurityType value)
      { setField(value); }
      public QuickFix.UnderlyingSecurityType get(QuickFix.UnderlyingSecurityType  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingSecurityType getUnderlyingSecurityType()
      { QuickFix.UnderlyingSecurityType value = new QuickFix.UnderlyingSecurityType();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingSecurityType field)
      { return isSetField(field); }
      public bool isSetUnderlyingSecurityType()
      { return isSetField(310); }

      public void set(QuickFix.UnderlyingSecuritySubType value)
      { setField(value); }
      public QuickFix.UnderlyingSecuritySubType get(QuickFix.UnderlyingSecuritySubType  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingSecuritySubType getUnderlyingSecuritySubType()
      { QuickFix.UnderlyingSecuritySubType value = new QuickFix.UnderlyingSecuritySubType();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingSecuritySubType field)
      { return isSetField(field); }
      public bool isSetUnderlyingSecuritySubType()
      { return isSetField(763); }

      public void set(QuickFix.UnderlyingMaturityMonthYear value)
      { setField(value); }
      public QuickFix.UnderlyingMaturityMonthYear get(QuickFix.UnderlyingMaturityMonthYear  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingMaturityMonthYear getUnderlyingMaturityMonthYear()
      { QuickFix.UnderlyingMaturityMonthYear value = new QuickFix.UnderlyingMaturityMonthYear();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingMaturityMonthYear field)
      { return isSetField(field); }
      public bool isSetUnderlyingMaturityMonthYear()
      { return isSetField(313); }

      public void set(QuickFix.UnderlyingMaturityDate value)
      { setField(value); }
      public QuickFix.UnderlyingMaturityDate get(QuickFix.UnderlyingMaturityDate  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingMaturityDate getUnderlyingMaturityDate()
      { QuickFix.UnderlyingMaturityDate value = new QuickFix.UnderlyingMaturityDate();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingMaturityDate field)
      { return isSetField(field); }
      public bool isSetUnderlyingMaturityDate()
      { return isSetField(542); }

      public void set(QuickFix.UnderlyingCouponPaymentDate value)
      { setField(value); }
      public QuickFix.UnderlyingCouponPaymentDate get(QuickFix.UnderlyingCouponPaymentDate  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingCouponPaymentDate getUnderlyingCouponPaymentDate()
      { QuickFix.UnderlyingCouponPaymentDate value = new QuickFix.UnderlyingCouponPaymentDate();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingCouponPaymentDate field)
      { return isSetField(field); }
      public bool isSetUnderlyingCouponPaymentDate()
      { return isSetField(241); }

      public void set(QuickFix.UnderlyingIssueDate value)
      { setField(value); }
      public QuickFix.UnderlyingIssueDate get(QuickFix.UnderlyingIssueDate  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingIssueDate getUnderlyingIssueDate()
      { QuickFix.UnderlyingIssueDate value = new QuickFix.UnderlyingIssueDate();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingIssueDate field)
      { return isSetField(field); }
      public bool isSetUnderlyingIssueDate()
      { return isSetField(242); }

      public void set(QuickFix.UnderlyingRepoCollateralSecurityType value)
      { setField(value); }
      public QuickFix.UnderlyingRepoCollateralSecurityType get(QuickFix.UnderlyingRepoCollateralSecurityType  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingRepoCollateralSecurityType getUnderlyingRepoCollateralSecurityType()
      { QuickFix.UnderlyingRepoCollateralSecurityType value = new QuickFix.UnderlyingRepoCollateralSecurityType();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingRepoCollateralSecurityType field)
      { return isSetField(field); }
      public bool isSetUnderlyingRepoCollateralSecurityType()
      { return isSetField(243); }

      public void set(QuickFix.UnderlyingRepurchaseTerm value)
      { setField(value); }
      public QuickFix.UnderlyingRepurchaseTerm get(QuickFix.UnderlyingRepurchaseTerm  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingRepurchaseTerm getUnderlyingRepurchaseTerm()
      { QuickFix.UnderlyingRepurchaseTerm value = new QuickFix.UnderlyingRepurchaseTerm();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingRepurchaseTerm field)
      { return isSetField(field); }
      public bool isSetUnderlyingRepurchaseTerm()
      { return isSetField(244); }

      public void set(QuickFix.UnderlyingRepurchaseRate value)
      { setField(value); }
      public QuickFix.UnderlyingRepurchaseRate get(QuickFix.UnderlyingRepurchaseRate  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingRepurchaseRate getUnderlyingRepurchaseRate()
      { QuickFix.UnderlyingRepurchaseRate value = new QuickFix.UnderlyingRepurchaseRate();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingRepurchaseRate field)
      { return isSetField(field); }
      public bool isSetUnderlyingRepurchaseRate()
      { return isSetField(245); }

      public void set(QuickFix.UnderlyingFactor value)
      { setField(value); }
      public QuickFix.UnderlyingFactor get(QuickFix.UnderlyingFactor  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingFactor getUnderlyingFactor()
      { QuickFix.UnderlyingFactor value = new QuickFix.UnderlyingFactor();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingFactor field)
      { return isSetField(field); }
      public bool isSetUnderlyingFactor()
      { return isSetField(246); }

      public void set(QuickFix.UnderlyingCreditRating value)
      { setField(value); }
      public QuickFix.UnderlyingCreditRating get(QuickFix.UnderlyingCreditRating  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingCreditRating getUnderlyingCreditRating()
      { QuickFix.UnderlyingCreditRating value = new QuickFix.UnderlyingCreditRating();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingCreditRating field)
      { return isSetField(field); }
      public bool isSetUnderlyingCreditRating()
      { return isSetField(256); }

      public void set(QuickFix.UnderlyingInstrRegistry value)
      { setField(value); }
      public QuickFix.UnderlyingInstrRegistry get(QuickFix.UnderlyingInstrRegistry  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingInstrRegistry getUnderlyingInstrRegistry()
      { QuickFix.UnderlyingInstrRegistry value = new QuickFix.UnderlyingInstrRegistry();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingInstrRegistry field)
      { return isSetField(field); }
      public bool isSetUnderlyingInstrRegistry()
      { return isSetField(595); }

      public void set(QuickFix.UnderlyingCountryOfIssue value)
      { setField(value); }
      public QuickFix.UnderlyingCountryOfIssue get(QuickFix.UnderlyingCountryOfIssue  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingCountryOfIssue getUnderlyingCountryOfIssue()
      { QuickFix.UnderlyingCountryOfIssue value = new QuickFix.UnderlyingCountryOfIssue();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingCountryOfIssue field)
      { return isSetField(field); }
      public bool isSetUnderlyingCountryOfIssue()
      { return isSetField(592); }

      public void set(QuickFix.UnderlyingStateOrProvinceOfIssue value)
      { setField(value); }
      public QuickFix.UnderlyingStateOrProvinceOfIssue get(QuickFix.UnderlyingStateOrProvinceOfIssue  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingStateOrProvinceOfIssue getUnderlyingStateOrProvinceOfIssue()
      { QuickFix.UnderlyingStateOrProvinceOfIssue value = new QuickFix.UnderlyingStateOrProvinceOfIssue();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingStateOrProvinceOfIssue field)
      { return isSetField(field); }
      public bool isSetUnderlyingStateOrProvinceOfIssue()
      { return isSetField(593); }

      public void set(QuickFix.UnderlyingLocaleOfIssue value)
      { setField(value); }
      public QuickFix.UnderlyingLocaleOfIssue get(QuickFix.UnderlyingLocaleOfIssue  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingLocaleOfIssue getUnderlyingLocaleOfIssue()
      { QuickFix.UnderlyingLocaleOfIssue value = new QuickFix.UnderlyingLocaleOfIssue();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingLocaleOfIssue field)
      { return isSetField(field); }
      public bool isSetUnderlyingLocaleOfIssue()
      { return isSetField(594); }

      public void set(QuickFix.UnderlyingRedemptionDate value)
      { setField(value); }
      public QuickFix.UnderlyingRedemptionDate get(QuickFix.UnderlyingRedemptionDate  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingRedemptionDate getUnderlyingRedemptionDate()
      { QuickFix.UnderlyingRedemptionDate value = new QuickFix.UnderlyingRedemptionDate();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingRedemptionDate field)
      { return isSetField(field); }
      public bool isSetUnderlyingRedemptionDate()
      { return isSetField(247); }

      public void set(QuickFix.UnderlyingStrikePrice value)
      { setField(value); }
      public QuickFix.UnderlyingStrikePrice get(QuickFix.UnderlyingStrikePrice  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingStrikePrice getUnderlyingStrikePrice()
      { QuickFix.UnderlyingStrikePrice value = new QuickFix.UnderlyingStrikePrice();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingStrikePrice field)
      { return isSetField(field); }
      public bool isSetUnderlyingStrikePrice()
      { return isSetField(316); }

      public void set(QuickFix.UnderlyingStrikeCurrency value)
      { setField(value); }
      public QuickFix.UnderlyingStrikeCurrency get(QuickFix.UnderlyingStrikeCurrency  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingStrikeCurrency getUnderlyingStrikeCurrency()
      { QuickFix.UnderlyingStrikeCurrency value = new QuickFix.UnderlyingStrikeCurrency();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingStrikeCurrency field)
      { return isSetField(field); }
      public bool isSetUnderlyingStrikeCurrency()
      { return isSetField(941); }

      public void set(QuickFix.UnderlyingOptAttribute value)
      { setField(value); }
      public QuickFix.UnderlyingOptAttribute get(QuickFix.UnderlyingOptAttribute  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingOptAttribute getUnderlyingOptAttribute()
      { QuickFix.UnderlyingOptAttribute value = new QuickFix.UnderlyingOptAttribute();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingOptAttribute field)
      { return isSetField(field); }
      public bool isSetUnderlyingOptAttribute()
      { return isSetField(317); }

      public void set(QuickFix.UnderlyingContractMultiplier value)
      { setField(value); }
      public QuickFix.UnderlyingContractMultiplier get(QuickFix.UnderlyingContractMultiplier  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingContractMultiplier getUnderlyingContractMultiplier()
      { QuickFix.UnderlyingContractMultiplier value = new QuickFix.UnderlyingContractMultiplier();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingContractMultiplier field)
      { return isSetField(field); }
      public bool isSetUnderlyingContractMultiplier()
      { return isSetField(436); }

      public void set(QuickFix.UnderlyingCouponRate value)
      { setField(value); }
      public QuickFix.UnderlyingCouponRate get(QuickFix.UnderlyingCouponRate  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingCouponRate getUnderlyingCouponRate()
      { QuickFix.UnderlyingCouponRate value = new QuickFix.UnderlyingCouponRate();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingCouponRate field)
      { return isSetField(field); }
      public bool isSetUnderlyingCouponRate()
      { return isSetField(435); }

      public void set(QuickFix.UnderlyingSecurityExchange value)
      { setField(value); }
      public QuickFix.UnderlyingSecurityExchange get(QuickFix.UnderlyingSecurityExchange  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingSecurityExchange getUnderlyingSecurityExchange()
      { QuickFix.UnderlyingSecurityExchange value = new QuickFix.UnderlyingSecurityExchange();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingSecurityExchange field)
      { return isSetField(field); }
      public bool isSetUnderlyingSecurityExchange()
      { return isSetField(308); }

      public void set(QuickFix.UnderlyingIssuer value)
      { setField(value); }
      public QuickFix.UnderlyingIssuer get(QuickFix.UnderlyingIssuer  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingIssuer getUnderlyingIssuer()
      { QuickFix.UnderlyingIssuer value = new QuickFix.UnderlyingIssuer();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingIssuer field)
      { return isSetField(field); }
      public bool isSetUnderlyingIssuer()
      { return isSetField(306); }

      public void set(QuickFix.EncodedUnderlyingIssuerLen value)
      { setField(value); }
      public QuickFix.EncodedUnderlyingIssuerLen get(QuickFix.EncodedUnderlyingIssuerLen  value)
      { getField(value); return value; }
      public QuickFix.EncodedUnderlyingIssuerLen getEncodedUnderlyingIssuerLen()
      { QuickFix.EncodedUnderlyingIssuerLen value = new QuickFix.EncodedUnderlyingIssuerLen();
        getField(value); return value; }
      public bool isSet(QuickFix.EncodedUnderlyingIssuerLen field)
      { return isSetField(field); }
      public bool isSetEncodedUnderlyingIssuerLen()
      { return isSetField(362); }

      public void set(QuickFix.EncodedUnderlyingIssuer value)
      { setField(value); }
      public QuickFix.EncodedUnderlyingIssuer get(QuickFix.EncodedUnderlyingIssuer  value)
      { getField(value); return value; }
      public QuickFix.EncodedUnderlyingIssuer getEncodedUnderlyingIssuer()
      { QuickFix.EncodedUnderlyingIssuer value = new QuickFix.EncodedUnderlyingIssuer();
        getField(value); return value; }
      public bool isSet(QuickFix.EncodedUnderlyingIssuer field)
      { return isSetField(field); }
      public bool isSetEncodedUnderlyingIssuer()
      { return isSetField(363); }

      public void set(QuickFix.UnderlyingSecurityDesc value)
      { setField(value); }
      public QuickFix.UnderlyingSecurityDesc get(QuickFix.UnderlyingSecurityDesc  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingSecurityDesc getUnderlyingSecurityDesc()
      { QuickFix.UnderlyingSecurityDesc value = new QuickFix.UnderlyingSecurityDesc();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingSecurityDesc field)
      { return isSetField(field); }
      public bool isSetUnderlyingSecurityDesc()
      { return isSetField(307); }

      public void set(QuickFix.EncodedUnderlyingSecurityDescLen value)
      { setField(value); }
      public QuickFix.EncodedUnderlyingSecurityDescLen get(QuickFix.EncodedUnderlyingSecurityDescLen  value)
      { getField(value); return value; }
      public QuickFix.EncodedUnderlyingSecurityDescLen getEncodedUnderlyingSecurityDescLen()
      { QuickFix.EncodedUnderlyingSecurityDescLen value = new QuickFix.EncodedUnderlyingSecurityDescLen();
        getField(value); return value; }
      public bool isSet(QuickFix.EncodedUnderlyingSecurityDescLen field)
      { return isSetField(field); }
      public bool isSetEncodedUnderlyingSecurityDescLen()
      { return isSetField(364); }

      public void set(QuickFix.EncodedUnderlyingSecurityDesc value)
      { setField(value); }
      public QuickFix.EncodedUnderlyingSecurityDesc get(QuickFix.EncodedUnderlyingSecurityDesc  value)
      { getField(value); return value; }
      public QuickFix.EncodedUnderlyingSecurityDesc getEncodedUnderlyingSecurityDesc()
      { QuickFix.EncodedUnderlyingSecurityDesc value = new QuickFix.EncodedUnderlyingSecurityDesc();
        getField(value); return value; }
      public bool isSet(QuickFix.EncodedUnderlyingSecurityDesc field)
      { return isSetField(field); }
      public bool isSetEncodedUnderlyingSecurityDesc()
      { return isSetField(365); }

      public void set(QuickFix.UnderlyingCPProgram value)
      { setField(value); }
      public QuickFix.UnderlyingCPProgram get(QuickFix.UnderlyingCPProgram  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingCPProgram getUnderlyingCPProgram()
      { QuickFix.UnderlyingCPProgram value = new QuickFix.UnderlyingCPProgram();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingCPProgram field)
      { return isSetField(field); }
      public bool isSetUnderlyingCPProgram()
      { return isSetField(877); }

      public void set(QuickFix.UnderlyingCPRegType value)
      { setField(value); }
      public QuickFix.UnderlyingCPRegType get(QuickFix.UnderlyingCPRegType  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingCPRegType getUnderlyingCPRegType()
      { QuickFix.UnderlyingCPRegType value = new QuickFix.UnderlyingCPRegType();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingCPRegType field)
      { return isSetField(field); }
      public bool isSetUnderlyingCPRegType()
      { return isSetField(878); }

      public void set(QuickFix.UnderlyingCurrency value)
      { setField(value); }
      public QuickFix.UnderlyingCurrency get(QuickFix.UnderlyingCurrency  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingCurrency getUnderlyingCurrency()
      { QuickFix.UnderlyingCurrency value = new QuickFix.UnderlyingCurrency();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingCurrency field)
      { return isSetField(field); }
      public bool isSetUnderlyingCurrency()
      { return isSetField(318); }

      public void set(QuickFix.UnderlyingQty value)
      { setField(value); }
      public QuickFix.UnderlyingQty get(QuickFix.UnderlyingQty  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingQty getUnderlyingQty()
      { QuickFix.UnderlyingQty value = new QuickFix.UnderlyingQty();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingQty field)
      { return isSetField(field); }
      public bool isSetUnderlyingQty()
      { return isSetField(879); }

      public void set(QuickFix.UnderlyingPx value)
      { setField(value); }
      public QuickFix.UnderlyingPx get(QuickFix.UnderlyingPx  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingPx getUnderlyingPx()
      { QuickFix.UnderlyingPx value = new QuickFix.UnderlyingPx();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingPx field)
      { return isSetField(field); }
      public bool isSetUnderlyingPx()
      { return isSetField(810); }

      public void set(QuickFix.UnderlyingDirtyPrice value)
      { setField(value); }
      public QuickFix.UnderlyingDirtyPrice get(QuickFix.UnderlyingDirtyPrice  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingDirtyPrice getUnderlyingDirtyPrice()
      { QuickFix.UnderlyingDirtyPrice value = new QuickFix.UnderlyingDirtyPrice();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingDirtyPrice field)
      { return isSetField(field); }
      public bool isSetUnderlyingDirtyPrice()
      { return isSetField(882); }

      public void set(QuickFix.UnderlyingEndPrice value)
      { setField(value); }
      public QuickFix.UnderlyingEndPrice get(QuickFix.UnderlyingEndPrice  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingEndPrice getUnderlyingEndPrice()
      { QuickFix.UnderlyingEndPrice value = new QuickFix.UnderlyingEndPrice();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingEndPrice field)
      { return isSetField(field); }
      public bool isSetUnderlyingEndPrice()
      { return isSetField(883); }

      public void set(QuickFix.UnderlyingStartValue value)
      { setField(value); }
      public QuickFix.UnderlyingStartValue get(QuickFix.UnderlyingStartValue  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingStartValue getUnderlyingStartValue()
      { QuickFix.UnderlyingStartValue value = new QuickFix.UnderlyingStartValue();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingStartValue field)
      { return isSetField(field); }
      public bool isSetUnderlyingStartValue()
      { return isSetField(884); }

      public void set(QuickFix.UnderlyingCurrentValue value)
      { setField(value); }
      public QuickFix.UnderlyingCurrentValue get(QuickFix.UnderlyingCurrentValue  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingCurrentValue getUnderlyingCurrentValue()
      { QuickFix.UnderlyingCurrentValue value = new QuickFix.UnderlyingCurrentValue();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingCurrentValue field)
      { return isSetField(field); }
      public bool isSetUnderlyingCurrentValue()
      { return isSetField(885); }

      public void set(QuickFix.UnderlyingEndValue value)
      { setField(value); }
      public QuickFix.UnderlyingEndValue get(QuickFix.UnderlyingEndValue  value)
      { getField(value); return value; }
      public QuickFix.UnderlyingEndValue getUnderlyingEndValue()
      { QuickFix.UnderlyingEndValue value = new QuickFix.UnderlyingEndValue();
        getField(value); return value; }
      public bool isSet(QuickFix.UnderlyingEndValue field)
      { return isSetField(field); }
      public bool isSetUnderlyingEndValue()
      { return isSetField(886); }

      public void set(QuickFix.NoUnderlyingSecurityAltID value)
      { setField(value); }
      public QuickFix.NoUnderlyingSecurityAltID get(QuickFix.NoUnderlyingSecurityAltID  value)
      { getField(value); return value; }
      public QuickFix.NoUnderlyingSecurityAltID getNoUnderlyingSecurityAltID()
      { QuickFix.NoUnderlyingSecurityAltID value = new QuickFix.NoUnderlyingSecurityAltID();
        getField(value); return value; }
      public bool isSet(QuickFix.NoUnderlyingSecurityAltID field)
      { return isSetField(field); }
      public bool isSetNoUnderlyingSecurityAltID()
      { return isSetField(457); }

      public class NoUnderlyingSecurityAltID: QuickFix.Group
      {
      public NoUnderlyingSecurityAltID() : base(457,458,message_order ) {}
      static int[] message_order = new int[] {458,459,0};
        public void set(QuickFix.UnderlyingSecurityAltID value)
        { setField(value); }
        public QuickFix.UnderlyingSecurityAltID get(QuickFix.UnderlyingSecurityAltID  value)
        { getField(value); return value; }
        public QuickFix.UnderlyingSecurityAltID getUnderlyingSecurityAltID()
        { QuickFix.UnderlyingSecurityAltID value = new QuickFix.UnderlyingSecurityAltID();
          getField(value); return value; }
        public bool isSet(QuickFix.UnderlyingSecurityAltID field)
        { return isSetField(field); }
        public bool isSetUnderlyingSecurityAltID()
        { return isSetField(458); }

        public void set(QuickFix.UnderlyingSecurityAltIDSource value)
        { setField(value); }
        public QuickFix.UnderlyingSecurityAltIDSource get(QuickFix.UnderlyingSecurityAltIDSource  value)
        { getField(value); return value; }
        public QuickFix.UnderlyingSecurityAltIDSource getUnderlyingSecurityAltIDSource()
        { QuickFix.UnderlyingSecurityAltIDSource value = new QuickFix.UnderlyingSecurityAltIDSource();
          getField(value); return value; }
        public bool isSet(QuickFix.UnderlyingSecurityAltIDSource field)
        { return isSetField(field); }
        public bool isSetUnderlyingSecurityAltIDSource()
        { return isSetField(459); }

      };
    };
    public void set(QuickFix.NoMiscFees value)
    { setField(value); }
    public QuickFix.NoMiscFees get(QuickFix.NoMiscFees  value)
    { getField(value); return value; }
    public QuickFix.NoMiscFees getNoMiscFees()
    { QuickFix.NoMiscFees value = new QuickFix.NoMiscFees();
      getField(value); return value; }
    public bool isSet(QuickFix.NoMiscFees field)
    { return isSetField(field); }
    public bool isSetNoMiscFees()
    { return isSetField(136); }

    public class NoMiscFees: QuickFix.Group
    {
    public NoMiscFees() : base(136,137,message_order ) {}
    static int[] message_order = new int[] {137,138,139,891,0};
      public void set(QuickFix.MiscFeeAmt value)
      { setField(value); }
      public QuickFix.MiscFeeAmt get(QuickFix.MiscFeeAmt  value)
      { getField(value); return value; }
      public QuickFix.MiscFeeAmt getMiscFeeAmt()
      { QuickFix.MiscFeeAmt value = new QuickFix.MiscFeeAmt();
        getField(value); return value; }
      public bool isSet(QuickFix.MiscFeeAmt field)
      { return isSetField(field); }
      public bool isSetMiscFeeAmt()
      { return isSetField(137); }

      public void set(QuickFix.MiscFeeCurr value)
      { setField(value); }
      public QuickFix.MiscFeeCurr get(QuickFix.MiscFeeCurr  value)
      { getField(value); return value; }
      public QuickFix.MiscFeeCurr getMiscFeeCurr()
      { QuickFix.MiscFeeCurr value = new QuickFix.MiscFeeCurr();
        getField(value); return value; }
      public bool isSet(QuickFix.MiscFeeCurr field)
      { return isSetField(field); }
      public bool isSetMiscFeeCurr()
      { return isSetField(138); }

      public void set(QuickFix.MiscFeeType value)
      { setField(value); }
      public QuickFix.MiscFeeType get(QuickFix.MiscFeeType  value)
      { getField(value); return value; }
      public QuickFix.MiscFeeType getMiscFeeType()
      { QuickFix.MiscFeeType value = new QuickFix.MiscFeeType();
        getField(value); return value; }
      public bool isSet(QuickFix.MiscFeeType field)
      { return isSetField(field); }
      public bool isSetMiscFeeType()
      { return isSetField(139); }

      public void set(QuickFix.MiscFeeBasis value)
      { setField(value); }
      public QuickFix.MiscFeeBasis get(QuickFix.MiscFeeBasis  value)
      { getField(value); return value; }
      public QuickFix.MiscFeeBasis getMiscFeeBasis()
      { QuickFix.MiscFeeBasis value = new QuickFix.MiscFeeBasis();
        getField(value); return value; }
      public bool isSet(QuickFix.MiscFeeBasis field)
      { return isSetField(field); }
      public bool isSetMiscFeeBasis()
      { return isSetField(891); }

    };
  };

}

