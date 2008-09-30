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
 
using System;

namespace QuickFix40
{
  public class MessageCracker 
  {
  public  void onMessage( QuickFix.Message message, QuickFix.SessionID session )
    { throw new QuickFix.UnsupportedMessageType(); }
 public virtual void onMessage( Heartbeat message, QuickFix.SessionID session ) 
    {}
  public virtual void onMessage( Logon message, QuickFix.SessionID session ) 
    {}
  public virtual void onMessage( TestRequest message, QuickFix.SessionID session ) 
    {}
  public virtual void onMessage( ResendRequest message, QuickFix.SessionID session ) 
    {}
  public virtual void onMessage( Reject message, QuickFix.SessionID session ) 
    {}
  public virtual void onMessage( SequenceReset message, QuickFix.SessionID session ) 
    {}
  public virtual void onMessage( Logout message, QuickFix.SessionID session ) 
    {}
  public virtual void onMessage( Advertisement message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( IndicationofInterest message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( News message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( Email message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( QuoteRequest message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( Quote message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( NewOrderSingle message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( ExecutionReport message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( DontKnowTrade message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( OrderCancelReplaceRequest message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( OrderCancelRequest message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( OrderCancelReject message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( OrderStatusRequest message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( Allocation message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( AllocationACK message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( NewOrderList message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( ListStatus message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( ListExecute message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( ListCancelRequest message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
  public virtual void onMessage( ListStatusRequest message, QuickFix.SessionID session ) 
    { throw new QuickFix.UnsupportedMessageType(); }
 
  public  void crack( QuickFix.Message message,
                     QuickFix.SessionID sessionID )
  {
    QuickFix.MsgType msgType = new QuickFix.MsgType();
    message.getHeader().getField(msgType);
    String msgTypeValue = msgType.getValue();

    if( msgTypeValue == "0" )
      onMessage( (Heartbeat)(message), sessionID );
    else
    if( msgTypeValue == "A" )
      onMessage( (Logon)(message), sessionID );
    else
    if( msgTypeValue == "1" )
      onMessage( (TestRequest)(message), sessionID );
    else
    if( msgTypeValue == "2" )
      onMessage( (ResendRequest)(message), sessionID );
    else
    if( msgTypeValue == "3" )
      onMessage( (Reject)(message), sessionID );
    else
    if( msgTypeValue == "4" )
      onMessage( (SequenceReset)(message), sessionID );
    else
    if( msgTypeValue == "5" )
      onMessage( (Logout)(message), sessionID );
    else
    if( msgTypeValue == "7" )
      onMessage( (Advertisement)(message), sessionID );
    else
    if( msgTypeValue == "6" )
      onMessage( (IndicationofInterest)(message), sessionID );
    else
    if( msgTypeValue == "B" )
      onMessage( (News)(message), sessionID );
    else
    if( msgTypeValue == "C" )
      onMessage( (Email)(message), sessionID );
    else
    if( msgTypeValue == "R" )
      onMessage( (QuoteRequest)(message), sessionID );
    else
    if( msgTypeValue == "S" )
      onMessage( (Quote)(message), sessionID );
    else
    if( msgTypeValue == "D" )
      onMessage( (NewOrderSingle)(message), sessionID );
    else
    if( msgTypeValue == "8" )
      onMessage( (ExecutionReport)(message), sessionID );
    else
    if( msgTypeValue == "Q" )
      onMessage( (DontKnowTrade)(message), sessionID );
    else
    if( msgTypeValue == "G" )
      onMessage( (OrderCancelReplaceRequest)(message), sessionID );
    else
    if( msgTypeValue == "F" )
      onMessage( (OrderCancelRequest)(message), sessionID );
    else
    if( msgTypeValue == "9" )
      onMessage( (OrderCancelReject)(message), sessionID );
    else
    if( msgTypeValue == "H" )
      onMessage( (OrderStatusRequest)(message), sessionID );
    else
    if( msgTypeValue == "J" )
      onMessage( (Allocation)(message), sessionID );
    else
    if( msgTypeValue == "P" )
      onMessage( (AllocationACK)(message), sessionID );
    else
    if( msgTypeValue == "E" )
      onMessage( (NewOrderList)(message), sessionID );
    else
    if( msgTypeValue == "N" )
      onMessage( (ListStatus)(message), sessionID );
    else
    if( msgTypeValue == "L" )
      onMessage( (ListExecute)(message), sessionID );
    else
    if( msgTypeValue == "K" )
      onMessage( (ListCancelRequest)(message), sessionID );
    else
    if( msgTypeValue == "M" )
      onMessage( (ListStatusRequest)(message), sessionID );
    else onMessage( message, sessionID );    
  }

  };
}

