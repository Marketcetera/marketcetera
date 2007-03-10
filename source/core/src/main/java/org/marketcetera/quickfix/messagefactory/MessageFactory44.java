package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.fix44.MessageFactory;
import quickfix.Group;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class MessageFactory44 extends MessageFactory implements MyMessageFactory {
    public Group create(String beginString, String msgType, int correspondingFieldID) {

      if("A".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMsgTypes.FIELD:
               return new quickfix.fix44.Logon.NoMsgTypes();

        }
      }

      if("7".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.Advertisement.NoLegs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.Advertisement.NoUnderlyings();

        }
      }

      if("6".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.IndicationOfInterest.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.IndicationOfInterest.NoLegs();

           case quickfix.field.NoIOIQualifiers.FIELD:
               return new quickfix.fix44.IndicationOfInterest.NoIOIQualifiers();

           case quickfix.field.NoRoutingIDs.FIELD:
               return new quickfix.fix44.IndicationOfInterest.NoRoutingIDs();

        }
      }

      if("B".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRoutingIDs.FIELD:
               return new quickfix.fix44.News.NoRoutingIDs();

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix44.News.NoRelatedSym();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.News.NoLegs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.News.NoUnderlyings();

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix44.News.LinesOfText();

        }
      }

      if("C".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRoutingIDs.FIELD:
               return new quickfix.fix44.Email.NoRoutingIDs();

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix44.Email.NoRelatedSym();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.Email.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.Email.NoLegs();

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix44.Email.LinesOfText();

        }
      }

      if("R".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix44.QuoteRequest.NoRelatedSym();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.QuoteRequest.NoRelatedSym.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.QuoteRequest.NoRelatedSym.NoLegs();

           case quickfix.field.NoQuoteQualifiers.FIELD:
               return new quickfix.fix44.QuoteRequest.NoRelatedSym.NoQuoteQualifiers();

        }
      }

      if("AJ".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteQualifiers.FIELD:
               return new quickfix.fix44.QuoteResponse.NoQuoteQualifiers();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.QuoteResponse.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.QuoteResponse.NoLegs();

        }
      }

      if("AG".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix44.QuoteRequestReject.NoRelatedSym();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.QuoteRequestReject.NoRelatedSym.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.QuoteRequestReject.NoRelatedSym.NoLegs();

           case quickfix.field.NoQuoteQualifiers.FIELD:
               return new quickfix.fix44.QuoteRequestReject.NoQuoteQualifiers();

        }
      }

      if("AH".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix44.RFQRequest.NoRelatedSym();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.RFQRequest.NoRelatedSym.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.RFQRequest.NoRelatedSym.NoLegs();

        }
      }

      if("S".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteQualifiers.FIELD:
               return new quickfix.fix44.Quote.NoQuoteQualifiers();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.Quote.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.Quote.NoLegs();

        }
      }

      if("Z".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteEntries.FIELD:
               return new quickfix.fix44.QuoteCancel.NoQuoteEntries();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.QuoteCancel.NoQuoteEntries.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.QuoteCancel.NoQuoteEntries.NoLegs();

        }
      }

      if("a".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.QuoteStatusRequest.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.QuoteStatusRequest.NoLegs();

        }
      }

      if("AI".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.QuoteStatusReport.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.QuoteStatusReport.NoLegs();

           case quickfix.field.NoQuoteQualifiers.FIELD:
               return new quickfix.fix44.QuoteStatusReport.NoQuoteQualifiers();

        }
      }

      if("i".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteSets.FIELD:
               return new quickfix.fix44.MassQuote.NoQuoteSets();

           case quickfix.field.NoQuoteEntries.FIELD:
               return new quickfix.fix44.MassQuote.NoQuoteSets.NoQuoteEntries();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.MassQuote.NoQuoteSets.NoQuoteEntries.NoLegs();

        }
      }

      if("b".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoQuoteSets.FIELD:
               return new quickfix.fix44.MassQuoteAcknowledgement.NoQuoteSets();

           case quickfix.field.NoQuoteEntries.FIELD:
               return new quickfix.fix44.MassQuoteAcknowledgement.NoQuoteSets.NoQuoteEntries();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.MassQuoteAcknowledgement.NoQuoteSets.NoQuoteEntries.NoLegs();

        }
      }

      if("V".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMDEntryTypes.FIELD:
               return new quickfix.fix44.MarketDataRequest.NoMDEntryTypes();

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix44.MarketDataRequest.NoRelatedSym();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.MarketDataRequest.NoRelatedSym.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.MarketDataRequest.NoRelatedSym.NoLegs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.MarketDataRequest.NoTradingSessions();

        }
      }

      if("W".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.MarketDataSnapshotFullRefresh.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.MarketDataSnapshotFullRefresh.NoLegs();

           case quickfix.field.NoMDEntries.FIELD:
               return new quickfix.fix44.MarketDataSnapshotFullRefresh.NoMDEntries();

        }
      }

      if("X".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMDEntries.FIELD:
               return new quickfix.fix44.MarketDataIncrementalRefresh.NoMDEntries();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.MarketDataIncrementalRefresh.NoMDEntries.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.MarketDataIncrementalRefresh.NoMDEntries.NoLegs();

        }
      }

      if("Y".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAltMDSource.FIELD:
               return new quickfix.fix44.MarketDataRequestReject.NoAltMDSource();

        }
      }

      if("c".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.SecurityDefinitionRequest.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.SecurityDefinitionRequest.NoLegs();

        }
      }

      if("d".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.SecurityDefinition.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.SecurityDefinition.NoLegs();

        }
      }

      if("w".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSecurityTypes.FIELD:
               return new quickfix.fix44.SecurityTypes.NoSecurityTypes();

        }
      }

      if("x".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.SecurityListRequest.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.SecurityListRequest.NoLegs();

        }
      }

      if("y".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix44.SecurityList.NoRelatedSym();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.SecurityList.NoRelatedSym.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.SecurityList.NoRelatedSym.NoLegs();

        }
      }

      if("AA".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix44.DerivativeSecurityList.NoRelatedSym();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.DerivativeSecurityList.NoRelatedSym.NoLegs();

        }
      }

      if("e".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.SecurityStatusRequest.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.SecurityStatusRequest.NoLegs();

        }
      }

      if("f".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.SecurityStatus.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.SecurityStatus.NoLegs();

        }
      }

      if("D".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.NewOrderSingle.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.NewOrderSingle.NoTradingSessions();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.NewOrderSingle.NoUnderlyings();

        }
      }

      if("8".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoContraBrokers.FIELD:
               return new quickfix.fix44.ExecutionReport.NoContraBrokers();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.ExecutionReport.NoUnderlyings();

           case quickfix.field.NoContAmts.FIELD:
               return new quickfix.fix44.ExecutionReport.NoContAmts();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.ExecutionReport.NoLegs();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix44.ExecutionReport.NoMiscFees();

        }
      }

      if("Q".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.DontKnowTrade.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.DontKnowTrade.NoLegs();

        }
      }

      if("G".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.OrderCancelReplaceRequest.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.OrderCancelReplaceRequest.NoTradingSessions();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.OrderCancelReplaceRequest.NoUnderlyings();

        }
      }

      if("F".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.OrderCancelRequest.NoUnderlyings();

        }
      }

      if("H".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.OrderStatusRequest.NoUnderlyings();

        }
      }

      if("r".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAffectedOrders.FIELD:
               return new quickfix.fix44.OrderMassCancelReport.NoAffectedOrders();

        }
      }

      if("s".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSides.FIELD:
               return new quickfix.fix44.NewOrderCross.NoSides();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.NewOrderCross.NoSides.NoAllocs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.NewOrderCross.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.NewOrderCross.NoLegs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.NewOrderCross.NoTradingSessions();

        }
      }

      if("t".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSides.FIELD:
               return new quickfix.fix44.CrossOrderCancelReplaceRequest.NoSides();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.CrossOrderCancelReplaceRequest.NoSides.NoAllocs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.CrossOrderCancelReplaceRequest.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.CrossOrderCancelReplaceRequest.NoLegs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.CrossOrderCancelReplaceRequest.NoTradingSessions();

        }
      }

      if("u".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSides.FIELD:
               return new quickfix.fix44.CrossOrderCancelRequest.NoSides();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.CrossOrderCancelRequest.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.CrossOrderCancelRequest.NoLegs();

        }
      }

      if("AB".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.NewOrderMultileg.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.NewOrderMultileg.NoTradingSessions();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.NewOrderMultileg.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.NewOrderMultileg.NoLegs();

           case quickfix.field.NoLegAllocs.FIELD:
               return new quickfix.fix44.NewOrderMultileg.NoLegs.NoLegAllocs();

        }
      }

      if("AC".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.MultilegOrderCancelReplaceRequest.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.MultilegOrderCancelReplaceRequest.NoTradingSessions();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.MultilegOrderCancelReplaceRequest.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.MultilegOrderCancelReplaceRequest.NoLegs();

           case quickfix.field.NoLegAllocs.FIELD:
               return new quickfix.fix44.MultilegOrderCancelReplaceRequest.NoLegs.NoLegAllocs();

        }
      }

      if("k".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoBidDescriptors.FIELD:
               return new quickfix.fix44.BidRequest.NoBidDescriptors();

           case quickfix.field.NoBidComponents.FIELD:
               return new quickfix.fix44.BidRequest.NoBidComponents();

        }
      }

      if("l".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoBidComponents.FIELD:
               return new quickfix.fix44.BidResponse.NoBidComponents();

        }
      }

      if("E".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix44.NewOrderList.NoOrders();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.NewOrderList.NoOrders.NoAllocs();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.NewOrderList.NoOrders.NoTradingSessions();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.NewOrderList.NoOrders.NoUnderlyings();

        }
      }

      if("m".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoStrikes.FIELD:
               return new quickfix.fix44.ListStrikePrice.NoStrikes();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.ListStrikePrice.NoUnderlyings();

        }
      }

      if("N".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix44.ListStatus.NoOrders();

        }
      }

      if("J".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix44.AllocationInstruction.NoOrders();

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix44.AllocationInstruction.NoExecs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.AllocationInstruction.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.AllocationInstruction.NoLegs();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.AllocationInstruction.NoAllocs();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix44.AllocationInstruction.NoAllocs.NoMiscFees();

        }
      }

      if("P".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.AllocationInstructionAck.NoAllocs();

        }
      }

      if("AS".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix44.AllocationReport.NoOrders();

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix44.AllocationReport.NoExecs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.AllocationReport.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.AllocationReport.NoLegs();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.AllocationReport.NoAllocs();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix44.AllocationReport.NoAllocs.NoMiscFees();

           case quickfix.field.NoClearingInstructions.FIELD:
               return new quickfix.fix44.AllocationReport.NoAllocs.NoClearingInstructions();

        }
      }

      if("AT".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.AllocationReportAck.NoAllocs();

        }
      }

      if("AK".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix44.Confirmation.NoOrders();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.Confirmation.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.Confirmation.NoLegs();

           case quickfix.field.NoCapacities.FIELD:
               return new quickfix.fix44.Confirmation.NoCapacities();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix44.Confirmation.NoMiscFees();

        }
      }

      if("BH".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix44.ConfirmationRequest.NoOrders();

        }
      }

      if("T".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoSettlInst.FIELD:
               return new quickfix.fix44.SettlementInstructions.NoSettlInst();

        }
      }

      if("AD".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.TradeCaptureReportRequest.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.TradeCaptureReportRequest.NoLegs();

           case quickfix.field.NoDates.FIELD:
               return new quickfix.fix44.TradeCaptureReportRequest.NoDates();

        }
      }

      if("AQ".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.TradeCaptureReportRequestAck.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.TradeCaptureReportRequestAck.NoLegs();

        }
      }

      if("AE".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.TradeCaptureReport.NoUnderlyings();

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.TradeCaptureReport.NoLegs();

           case quickfix.field.NoSides.FIELD:
               return new quickfix.fix44.TradeCaptureReport.NoSides();

           case quickfix.field.NoClearingInstructions.FIELD:
               return new quickfix.fix44.TradeCaptureReport.NoSides.NoClearingInstructions();

           case quickfix.field.NoContAmts.FIELD:
               return new quickfix.fix44.TradeCaptureReport.NoSides.NoContAmts();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix44.TradeCaptureReport.NoSides.NoMiscFees();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.TradeCaptureReport.NoSides.NoAllocs();

        }
      }

      if("AR".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.TradeCaptureReportAck.NoLegs();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix44.TradeCaptureReportAck.NoAllocs();

        }
      }

      if("o".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRegistDtls.FIELD:
               return new quickfix.fix44.RegistrationInstructions.NoRegistDtls();

           case quickfix.field.NoDistribInsts.FIELD:
               return new quickfix.fix44.RegistrationInstructions.NoDistribInsts();

        }
      }

      if("AL".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.PositionMaintenanceRequest.NoLegs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.PositionMaintenanceRequest.NoUnderlyings();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.PositionMaintenanceRequest.NoTradingSessions();

        }
      }

      if("AM".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.PositionMaintenanceReport.NoLegs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.PositionMaintenanceReport.NoUnderlyings();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.PositionMaintenanceReport.NoTradingSessions();

        }
      }

      if("AN".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.RequestForPositions.NoLegs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.RequestForPositions.NoUnderlyings();

           case quickfix.field.NoTradingSessions.FIELD:
               return new quickfix.fix44.RequestForPositions.NoTradingSessions();

        }
      }

      if("AO".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.RequestForPositionsAck.NoLegs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.RequestForPositionsAck.NoUnderlyings();

        }
      }

      if("AP".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoLegs.FIELD:
               return new quickfix.fix44.PositionReport.NoLegs();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.PositionReport.NoUnderlyings();

        }
      }

      if("AW".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.AssignmentReport.NoUnderlyings();

        }
      }

      if("AX".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix44.CollateralRequest.NoExecs();

           case quickfix.field.NoTrades.FIELD:
               return new quickfix.fix44.CollateralRequest.NoTrades();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.CollateralRequest.NoUnderlyings();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix44.CollateralRequest.NoMiscFees();

        }
      }

      if("AY".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix44.CollateralAssignment.NoExecs();

           case quickfix.field.NoTrades.FIELD:
               return new quickfix.fix44.CollateralAssignment.NoTrades();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.CollateralAssignment.NoUnderlyings();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix44.CollateralAssignment.NoMiscFees();

        }
      }

      if("AZ".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix44.CollateralResponse.NoExecs();

           case quickfix.field.NoTrades.FIELD:
               return new quickfix.fix44.CollateralResponse.NoTrades();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.CollateralResponse.NoUnderlyings();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix44.CollateralResponse.NoMiscFees();

        }
      }

      if("BA".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix44.CollateralReport.NoExecs();

           case quickfix.field.NoTrades.FIELD:
               return new quickfix.fix44.CollateralReport.NoTrades();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.CollateralReport.NoUnderlyings();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix44.CollateralReport.NoMiscFees();

        }
      }

      if("BB".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoCollInquiryQualifier.FIELD:
               return new quickfix.fix44.CollateralInquiry.NoCollInquiryQualifier();

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix44.CollateralInquiry.NoExecs();

           case quickfix.field.NoTrades.FIELD:
               return new quickfix.fix44.CollateralInquiry.NoTrades();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.CollateralInquiry.NoUnderlyings();

        }
      }

/*
    // not in qfj-1.0.5
      if("BC".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoCompIDs.FIELD:
               return new quickfix.fix44.NetworkStatusRequest.NoCompIDs();

        }
      }

      if("BD".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoCompIDs.FIELD:
               return new quickfix.fix44.NetworkStatusResponse.NoCompIDs();

        }
      }

*/
      if("BG".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoCollInquiryQualifier.FIELD:
               return new quickfix.fix44.CollateralInquiryAck.NoCollInquiryQualifier();

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix44.CollateralInquiryAck.NoExecs();

           case quickfix.field.NoTrades.FIELD:
               return new quickfix.fix44.CollateralInquiryAck.NoTrades();

           case quickfix.field.NoUnderlyings.FIELD:
               return new quickfix.fix44.CollateralInquiryAck.NoUnderlyings();

        }
      }

       return null;
    }

}
