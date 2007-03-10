package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.fix41.MessageFactory;
import quickfix.Group;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class MessageFactory41 extends MessageFactory implements MyMessageFactory {
    public Group create(String beginString, String msgType, int correspondingFieldID) {

      if("6".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoIOIQualifiers.FIELD:
               return new quickfix.fix41.IndicationofInterest.NoIOIQualifiers();

        }
      }

      if("B".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix41.News.NoRelatedSym();

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix41.News.LinesOfText();

        }
      }

      if("C".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoRelatedSym.FIELD:
               return new quickfix.fix41.Email.NoRelatedSym();

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix41.Email.LinesOfText();

        }
      }

      if("J".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix41.Allocation.NoOrders();

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix41.Allocation.NoExecs();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix41.Allocation.NoAllocs();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix41.Allocation.NoAllocs.NoMiscFees();

        }
      }

      if("N".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix41.ListStatus.NoOrders();

        }
      }

       return null;
    }
}
