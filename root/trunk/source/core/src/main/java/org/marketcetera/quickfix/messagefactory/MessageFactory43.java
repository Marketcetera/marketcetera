package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.fix43.MessageFactory;
import quickfix.Group;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class MessageFactory43 extends MessageFactory implements MyMessageFactory {
    public Group create(String beginString, String msgType, int correspondingFieldID) {

      if("A".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMsgTypes.FIELD:
               return new quickfix.fix43.Logon.NoMsgTypes();

        }
      }

      if("6".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoIOIQualifiers.FIELD:
               return new quickfix.fix43.IndicationOfInterest.NoIOIQualifiers();

           case quickfix.field.NoRoutingIDs.FIELD:
               return new quickfix.fix43.IndicationOfInterest.NoRoutingIDs();

        }
      }

      if("B".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRoutingIDs.FIELD:
               return new quickfix.fix43.News.NoRoutingIDs();

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix43.News.NoRelatedSym();

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix43.News.LinesOfText();

        }
      }

      if("C".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRoutingIDs.FIELD:
               return new quickfix.fix43.Email.NoRoutingIDs();

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix43.Email.NoRelatedSym();

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix43.Email.LinesOfText();

        }
      }

      if("R".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix43.QuoteRequest.NoRelatedSym();

        }
      }

      if("AG".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix43.QuoteRequestReject.NoRelatedSym();

        }
      }

      if("AH".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix43.RFQRequest.NoRelatedSym();

        }
      }

      if("Z".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteEntries.FIELD:
               return new quickfix.fix43.QuoteCancel.NoQuoteEntries();

        }
      }

      if("i".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteSets.FIELD:
               return new quickfix.fix43.MassQuote.NoQuoteSets();

           case quickfix.field.NoQuoteEntries.FIELD:
               return new quickfix.fix43.MassQuote.NoQuoteSets.NoQuoteEntries();

        }
      }

      if("b".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteSets.FIELD:
               return new quickfix.fix43.MassQuoteAcknowledgement.NoQuoteSets();

           case quickfix.field.NoQuoteEntries.FIELD:
               return new quickfix.fix43.MassQuoteAcknowledgement.NoQuoteSets.NoQuoteEntries();

        }
      }

      if("V".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMDEntryTypes.FIELD:
               return new quickfix.fix43.MarketDataRequest.NoMDEntryTypes();

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix43.MarketDataRequest.NoRelatedSym();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix43.MarketDataRequest.NoTradingSessions();

        }
      }

      if("W".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMDEntries.FIELD:
               return new quickfix.fix43.MarketDataSnapshotFullRefresh.NoMDEntries();

        }
      }

      if("X".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMDEntries.FIELD:
               return new quickfix.fix43.MarketDataIncrementalRefresh.NoMDEntries();

        }
      }

      if("c".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix43.SecurityDefinitionRequest.NoLegs();

        }
      }

      if("d".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix43.SecurityDefinition.NoLegs();

        }
      }

      if("w".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSecurityTypes.FIELD:
               return new quickfix.fix43.SecurityTypes.NoSecurityTypes();

        }
      }

      if("y".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix43.SecurityList.NoRelatedSym();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix43.SecurityList.NoRelatedSym.NoLegs();

        }
      }

      if("AA".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix43.DerivativeSecurityList.NoRelatedSym();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix43.DerivativeSecurityList.NoRelatedSym.NoLegs();

        }
      }

      if("D".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix43.NewOrderSingle.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix43.NewOrderSingle.NoTradingSessions();

        }
      }

      if("8".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoContraBrokers.FIELD:
               return new quickfix.fix43.ExecutionReport.NoContraBrokers();

           case quickfix.field.NoContAmts.FIELD:
               return new quickfix.fix43.ExecutionReport.NoContAmts();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix43.ExecutionReport.NoLegs();

        }
      }

      if("G".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix43.OrderCancelReplaceRequest.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix43.OrderCancelReplaceRequest.NoTradingSessions();

        }
      }

      if("r".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAffectedOrders.FIELD:
               return new quickfix.fix43.OrderMassCancelReport.NoAffectedOrders();

        }
      }

      if("s".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSides.FIELD:
               return new quickfix.fix43.NewOrderCross.NoSides();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix43.NewOrderCross.NoSides.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix43.NewOrderCross.NoTradingSessions();

        }
      }

      if("t".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSides.FIELD:
               return new quickfix.fix43.CrossOrderCancelReplaceRequest.NoSides();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix43.CrossOrderCancelReplaceRequest.NoSides.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix43.CrossOrderCancelReplaceRequest.NoTradingSessions();

        }
      }

      if("u".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSides.FIELD:
               return new quickfix.fix43.CrossOrderCancelRequest.NoSides();

        }
      }

      if("AB".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix43.NewOrderMultileg.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix43.NewOrderMultileg.NoTradingSessions();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix43.NewOrderMultileg.NoLegs();

        }
      }

      if("AC".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix43.MultilegOrderCancelReplaceRequest.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix43.MultilegOrderCancelReplaceRequest.NoTradingSessions();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix43.MultilegOrderCancelReplaceRequest.NoLegs();

        }
      }

      if("k".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoBidDescriptors.FIELD:
               return new quickfix.fix43.BidRequest.NoBidDescriptors();

           case quickfix.field.NoBidComponents.FIELD:
               return new quickfix.fix43.BidRequest.NoBidComponents();

        }
      }

      if("l".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoBidComponents.FIELD:
               return new quickfix.fix43.BidResponse.NoBidComponents();

        }
      }

      if("E".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix43.NewOrderList.NoOrders();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix43.NewOrderList.NoOrders.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix43.NewOrderList.NoOrders.NoTradingSessions();

        }
      }

      if("m".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoStrikes.FIELD:
               return new quickfix.fix43.ListStrikePrice.NoStrikes();

        }
      }

      if("N".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix43.ListStatus.NoOrders();

        }
      }

      if("J".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix43.Allocation.NoOrders();

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix43.Allocation.NoExecs();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix43.Allocation.NoAllocs();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix43.Allocation.NoAllocs.NoMiscFees();

        }
      }

      if("AD".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoDates.FIELD:
               return new quickfix.fix43.TradeCaptureReportRequest.NoDates();

        }
      }

      if("AE".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSides.FIELD:
               return new quickfix.fix43.TradeCaptureReport.NoSides();

           case quickfix.field.NoClearingInstructions.FIELD:
               return new quickfix.fix43.TradeCaptureReport.NoSides.NoClearingInstructions();

           case quickfix.field.NoContAmts.FIELD:
               return new quickfix.fix43.TradeCaptureReport.NoSides.NoContAmts();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix43.TradeCaptureReport.NoSides.NoMiscFees();

        }
      }

      if("o".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRegistDtls.FIELD:
               return new quickfix.fix43.RegistrationInstructions.NoRegistDtls();

           case quickfix.field.NoDistribInsts.FIELD:
               return new quickfix.fix43.RegistrationInstructions.NoDistribInsts();

        }
      }

       return null;
    }
}
