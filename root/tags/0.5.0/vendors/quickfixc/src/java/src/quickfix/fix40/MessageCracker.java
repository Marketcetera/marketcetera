/* -*- C++ -*- */
 
/****************************************************************************
** Copyright (c) quickfixengine.org  All rights reserved.
**
** This file is part of the QuickFIX FIX Engine
**
** This file may be distributed under the terms of the quickfixengine.org
** license as defined by quickfixengine.org and appearing in the file
** LICENSE included in the packaging of this file.
**
** This file is provided AS IS with NO WARRANTY OF ANY KIND, INCLUDING THE
** WARRANTY OF DESIGN, MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE.
**
** See http://www.quickfixengine.org/LICENSE for licensing information.
**
** Contact ask@quickfixengine.org if any conditions of this licensing are
** not clear to you.
**
****************************************************************************/
 
package quickfix.fix40;

import quickfix.*;
import quickfix.field.*;

public class MessageCracker 
{
public void onMessage( quickfix.Message message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
  { throw new UnsupportedMessageType(); }
 public void onMessage( Heartbeat message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    {}
  public void onMessage( Logon message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    {}
  public void onMessage( TestRequest message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    {}
  public void onMessage( ResendRequest message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    {}
  public void onMessage( Reject message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    {}
  public void onMessage( SequenceReset message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    {}
  public void onMessage( Logout message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    {}
  public void onMessage( Advertisement message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( IndicationofInterest message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( News message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( Email message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( QuoteRequest message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( Quote message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( NewOrderSingle message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( ExecutionReport message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( DontKnowTrade message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( OrderCancelReplaceRequest message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( OrderCancelRequest message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( OrderCancelReject message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( OrderStatusRequest message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( Allocation message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( AllocationACK message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( NewOrderList message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( ListStatus message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( ListExecute message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( ListCancelRequest message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
  public void onMessage( ListStatusRequest message, SessionID sessionID ) throws FieldNotFound, UnsupportedMessageType, IncorrectTagValue
    { throw new UnsupportedMessageType(); }
 
  public void crack( quickfix.Message message, SessionID sessionID )
    throws UnsupportedMessageType, FieldNotFound, IncorrectTagValue
  { crack40((Message)message, sessionID); }

  public void crack40( Message message, SessionID sessionID ) 
    throws UnsupportedMessageType, FieldNotFound, IncorrectTagValue
  {
    MsgType msgType = new MsgType();
    message.getHeader().getField(msgType);
    String msgTypeValue = msgType.getValue();

    if( msgTypeValue.equals("0") )
      onMessage( (Heartbeat)message, sessionID );
    else
    if( msgTypeValue.equals("A") )
      onMessage( (Logon)message, sessionID );
    else
    if( msgTypeValue.equals("1") )
      onMessage( (TestRequest)message, sessionID );
    else
    if( msgTypeValue.equals("2") )
      onMessage( (ResendRequest)message, sessionID );
    else
    if( msgTypeValue.equals("3") )
      onMessage( (Reject)message, sessionID );
    else
    if( msgTypeValue.equals("4") )
      onMessage( (SequenceReset)message, sessionID );
    else
    if( msgTypeValue.equals("5") )
      onMessage( (Logout)message, sessionID );
    else
    if( msgTypeValue.equals("7") )
      onMessage( (Advertisement)message, sessionID );
    else
    if( msgTypeValue.equals("6") )
      onMessage( (IndicationofInterest)message, sessionID );
    else
    if( msgTypeValue.equals("B") )
      onMessage( (News)message, sessionID );
    else
    if( msgTypeValue.equals("C") )
      onMessage( (Email)message, sessionID );
    else
    if( msgTypeValue.equals("R") )
      onMessage( (QuoteRequest)message, sessionID );
    else
    if( msgTypeValue.equals("S") )
      onMessage( (Quote)message, sessionID );
    else
    if( msgTypeValue.equals("D") )
      onMessage( (NewOrderSingle)message, sessionID );
    else
    if( msgTypeValue.equals("8") )
      onMessage( (ExecutionReport)message, sessionID );
    else
    if( msgTypeValue.equals("Q") )
      onMessage( (DontKnowTrade)message, sessionID );
    else
    if( msgTypeValue.equals("G") )
      onMessage( (OrderCancelReplaceRequest)message, sessionID );
    else
    if( msgTypeValue.equals("F") )
      onMessage( (OrderCancelRequest)message, sessionID );
    else
    if( msgTypeValue.equals("9") )
      onMessage( (OrderCancelReject)message, sessionID );
    else
    if( msgTypeValue.equals("H") )
      onMessage( (OrderStatusRequest)message, sessionID );
    else
    if( msgTypeValue.equals("J") )
      onMessage( (Allocation)message, sessionID );
    else
    if( msgTypeValue.equals("P") )
      onMessage( (AllocationACK)message, sessionID );
    else
    if( msgTypeValue.equals("E") )
      onMessage( (NewOrderList)message, sessionID );
    else
    if( msgTypeValue.equals("N") )
      onMessage( (ListStatus)message, sessionID );
    else
    if( msgTypeValue.equals("L") )
      onMessage( (ListExecute)message, sessionID );
    else
    if( msgTypeValue.equals("K") )
      onMessage( (ListCancelRequest)message, sessionID );
    else
    if( msgTypeValue.equals("M") )
      onMessage( (ListStatusRequest)message, sessionID );
    else onMessage( message, sessionID );
  }

  };


