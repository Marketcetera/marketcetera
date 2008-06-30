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

#ifndef FIX_ACCEPTOR_H
#define FIX_ACCEPTOR_H

#ifdef _MSC_VER
#pragma warning( disable : 4503 4355 4786 4290 )
#endif

#include "Application.h"
#include "MessageStore.h"
#include "Log.h"
#include "Responder.h"
#include "SessionSettings.h"
#include "Exceptions.h"
#include <map>
#include <string>

namespace FIX
{
class Client;
class Session;

/**
 * Base for classes which act as an acceptor for incoming connections.
 *
 * Most users will not need to implement one of these.  The default
 * SocketAcceptor implementation will be used in most cases.
 */
class Acceptor : public Log
{
public:
  Acceptor( Application&, MessageStoreFactory&,
            const SessionSettings& ) throw( ConfigError );
  Acceptor( Application&, MessageStoreFactory&,
            const SessionSettings&, LogFactory& ) throw( ConfigError );

  virtual ~Acceptor();

  /// Start acceptor.
  void start() throw ( ConfigError, RuntimeError );
  /// Block on the acceptor
  void block() throw ( ConfigError, RuntimeError );
  /// Poll the acceptor
  bool poll() throw ( ConfigError, RuntimeError );

  /// Stop acceptor.
  void stop( bool force = false );

  /// Check to see if any sessions are currently logged on
  bool isLoggedOn();

  Session* getSession( const std::string& msg, Responder& );
  const std::set<SessionID> getSessions() const { return m_sessionIDs; }

  bool has( const SessionID& id )
  { return m_sessions.find( id ) != m_sessions.end(); }

  bool isStopped() { return m_stop; }

  Application& getApplication() { return m_application; }
  MessageStoreFactory& getMessageStoreFactory()
  { return m_messageStoreFactory; }

public:
  void onEvent( const std::string& text )
  { if( m_pLog ) m_pLog->onEvent( text ); }
  void onIncoming( const std::string& text )
  { if( m_pLog ) m_pLog->onIncoming( text ); }
  void onOutgoing( const std::string& text )
  { if( m_pLog ) m_pLog->onOutgoing( text ); }
  void clear()
  { if( m_pLog ) m_pLog->clear(); }

private:
  void initialize() throw ( ConfigError );

  /// Implemented to configure acceptor
  virtual void onConfigure( const SessionSettings& ) throw ( ConfigError ) {};
  /// Implemented to initialize acceptor
  virtual void onInitialize( const SessionSettings& ) throw ( RuntimeError ) {};
  /// Implemented to start listening for connections.
  virtual void onStart() = 0;
  /// Implemented to connect and poll for events.
  virtual bool onPoll() = 0;
  /// Implemented to stop a running acceptor.
  virtual void onStop() = 0;

  static THREAD_PROC startThread( void* p );

  typedef std::set < SessionID > SessionIDs;
  typedef std::map < SessionID, Session* > Sessions;

  unsigned m_threadid;
  Sessions m_sessions;
  SessionIDs m_sessionIDs;
  Application& m_application;
  MessageStoreFactory& m_messageStoreFactory;
  SessionSettings m_settings;
  LogFactory* m_pLogFactory;
  Log* m_pLog;
  bool m_firstPoll;
  bool m_stop;
};
/*! @} */
}

#endif // FIX_ACCEPTOR_H
