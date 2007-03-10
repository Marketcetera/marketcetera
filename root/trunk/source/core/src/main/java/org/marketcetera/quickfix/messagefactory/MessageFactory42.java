package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.Group;
import quickfix.fix42.MessageFactory;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class MessageFactory42 extends MessageFactory implements MyMessageFactory {
    public Group create(String beginString, String msgType, int correspondingFieldID) {

      if("A".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMsgTypes.FIELD:
               return new quickfix.fix42.Logon.NoMsgTypes();

        }
      }

      if("6".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoIOIQualifiers.FIELD:
               return new quickfix.fix42.IndicationofInterest.NoIOIQualifiers();

           case quickfix.field.NoRoutingIDs.FIELD:
               return new quickfix.fix42.IndicationofInterest.NoRoutingIDs();

        }
      }

      if("B".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRoutingIDs.FIELD:
               return new quickfix.fix42.News.NoRoutingIDs();

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix42.News.NoRelatedSym();

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix42.News.LinesOfText();

        }
      }

      if("C".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRoutingIDs.FIELD:
               return new quickfix.fix42.Email.NoRoutingIDs();

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix42.Email.NoRelatedSym();

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix42.Email.LinesOfText();

        }
      }

      if("R".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix42.QuoteRequest.NoRelatedSym();

        }
      }

      if("i".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteSets.FIELD:
               return new quickfix.fix42.MassQuote.NoQuoteSets();

           case quickfix.field.NoQuoteEntries.FIELD:
               return new quickfix.fix42.MassQuote.NoQuoteSets.NoQuoteEntries();

        }
      }

      if("Z".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteEntries.FIELD:
               return new quickfix.fix42.QuoteCancel.NoQuoteEntries();

        }
      }

      if("b".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteSets.FIELD:
               return new quickfix.fix42.QuoteAcknowledgement.NoQuoteSets();

           case quickfix.field.NoQuoteEntries.FIELD:
               return new quickfix.fix42.QuoteAcknowledgement.NoQuoteSets.NoQuoteEntries();

        }
      }

      if("V".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMDEntryTypes.FIELD:
               return new quickfix.fix42.MarketDataRequest.NoMDEntryTypes();

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix42.MarketDataRequest.NoRelatedSym();

        }
      }

      if("W".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMDEntries.FIELD:
               return new quickfix.fix42.MarketDataSnapshotFullRefresh.NoMDEntries();

        }
      }

      if("X".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMDEntries.FIELD:
               return new quickfix.fix42.MarketDataIncrementalRefresh.NoMDEntries();

        }
      }

      if("c".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix42.SecurityDefinitionRequest.NoRelatedSym();

        }
      }

      if("d".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix42.SecurityDefinition.NoRelatedSym();

        }
      }

      if("D".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix42.NewOrderSingle.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix42.NewOrderSingle.NoTradingSessions();

        }
      }

      if("8".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoContraBrokers.FIELD:
               return new quickfix.fix42.ExecutionReport.NoContraBrokers();

        }
      }

      if("G".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix42.OrderCancelReplaceRequest.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix42.OrderCancelReplaceRequest.NoTradingSessions();

        }
      }

      if("J".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix42.Allocation.NoOrders();

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix42.Allocation.NoExecs();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix42.Allocation.NoAllocs();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix42.Allocation.NoAllocs.NoMiscFees();

        }
      }

      if("k".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoBidDescriptors.FIELD:
               return new quickfix.fix42.BidRequest.NoBidDescriptors();

           case quickfix.field.NoBidComponents.FIELD:
               return new quickfix.fix42.BidRequest.NoBidComponents();

        }
      }

      if("l".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoBidComponents.FIELD:
               return new quickfix.fix42.BidResponse.NoBidComponents();

        }
      }

      if("E".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix42.NewOrderList.NoOrders();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix42.NewOrderList.NoOrders.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix42.NewOrderList.NoOrders.NoTradingSessions();

        }
      }

      if("m".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoStrikes.FIELD:
               return new quickfix.fix42.ListStrikePrice.NoStrikes();

        }
      }

      if("N".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix42.ListStatus.NoOrders();

        }
      }

       return null;
    }
}
