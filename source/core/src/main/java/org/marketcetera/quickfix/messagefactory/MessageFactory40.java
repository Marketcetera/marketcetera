package org.marketcetera.quickfix.messagefactory;

import org.marketcetera.core.ClassVersion;
import quickfix.fix40.MessageFactory;
import quickfix.Group;

/**
 * @author toli
 * @version $Id$
 */

@ClassVersion("$Id$")
public class MessageFactory40 extends MessageFactory implements MyMessageFactory {
    public Group create(String beginString, String msgType, int correspondingFieldID) {

      if("B".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix40.News.LinesOfText();

        }
      }

      if("C".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.LinesOfText.FIELD:
               return new quickfix.fix40.Email.LinesOfText();

        }
      }

      if("8".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix40.ExecutionReport.NoMiscFees();

        }
      }

      if("J".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix40.Allocation.NoOrders();

           case quickfix.field.NoExecs.FIELD:
               return new quickfix.fix40.Allocation.NoExecs();

           case quickfix.field.NoMiscFees.FIELD:
               return new quickfix.fix40.Allocation.NoMiscFees();

           case quickfix.field.NoAllocs.FIELD:
               return new quickfix.fix40.Allocation.NoAllocs();

           case quickfix.field.NoDlvyInst.FIELD:
               return new quickfix.fix40.Allocation.NoAllocs.NoDlvyInst();

        }
      }

      if("N".equals(msgType)) {
        switch(correspondingFieldID) {

           case quickfix.field.NoOrders.FIELD:
               return new quickfix.fix40.ListStatus.NoOrders();

        }
      }

       return null;
    }

}
