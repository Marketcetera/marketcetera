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

#ifdef _MSC_VER
#include "stdafx.h"
#else
#include "config.h"
#endif
#include "CallStack.h"

#include "ThreadedSocketConnection.h"
#include "ThreadedSocketAcceptor.h"
#include "ThreadedSocketInitiator.h"
#include "Session.h"
#include "Utility.h"

namespace FIX
{
ThreadedSocketConnection::ThreadedSocketConnection
( int s, Sessions sessions, Application& application, Log& log )
: m_socket( s ), m_application( application ), m_log( log ),
  m_sessions( sessions ), m_pSession( 0 ),
  m_disconnect( false )
{
  FD_ZERO( &m_fds );
  FD_SET( m_socket, &m_fds );
}

ThreadedSocketConnection::ThreadedSocketConnection
( const SessionID& sessionID, int s, Application& application, Log& log )
  : m_socket( s ), m_application( application ), m_log( log ),
  m_pSession( Session::lookupSession( sessionID ) ),
  m_disconnect( false )
{
  FD_ZERO( &m_fds );
  FD_SET( m_socket, &m_fds );
  if ( m_pSession ) m_pSession->setResponder( this );
}

ThreadedSocketConnection::~ThreadedSocketConnection()
{
  if ( m_pSession )
  {
    m_pSession->setResponder( 0 );
    Session::unregisterSession( m_pSession->getSessionID() );
  }
}

bool ThreadedSocketConnection::send( const std::string& msg )
{ QF_STACK_PUSH(ThreadedSocketConnection::send)
  return socket_send( m_socket, msg.c_str(), msg.length() ) >= 0;
  QF_STACK_POP
}

void ThreadedSocketConnection::disconnect()
{ QF_STACK_PUSH(ThreadedSocketConnection::disconnect)
  
  m_disconnect = true;
  socket_close( m_socket );

  QF_STACK_POP
}

bool ThreadedSocketConnection::read()
{ QF_STACK_PUSH(ThreadedSocketConnection::read)

  struct timeval timeout = { 1, 0 };
  fd_set readset = m_fds;

  try
  {
    // Wait for input (1 second timeout)
    int result = select( 1 + m_socket, &readset, 0, 0, &timeout );

    if( result > 0 ) // Something to read
    {
      // We can read without blocking
      int size = recv( m_socket, m_buffer, sizeof(m_buffer), 0 );
      if ( size <= 0 ) { throw SocketRecvFailed( size ); }
      m_parser.addToStream( m_buffer, size );
    }
    else if( result == 0 && m_pSession ) // Timeout
    {
      m_pSession->next();
    }
    else if( result < 0 ) // Error
    {
      throw SocketRecvFailed( result );
    }

    processStream();
    return true;
  }
  catch ( SocketRecvFailed& e )
  {
    if( m_disconnect )
      return false;

    if( m_pSession )
    {
      m_pSession->getLog()->onEvent( e.what() );
      m_pSession->disconnect();
    }
    else
    {
      disconnect();
    }

    return false;
  }

  QF_STACK_POP
}

bool ThreadedSocketConnection::readMessage( std::string& msg )
throw( SocketRecvFailed )
{ QF_STACK_PUSH(ThreadedSocketConnection::readMessage)

  try
  {
    return m_parser.readFixMessage( msg );
  }
  catch ( MessageParseError& ) {}
  return true;

  QF_STACK_POP
}

void ThreadedSocketConnection::processStream()
{ QF_STACK_PUSH(ThreadedSocketConnection::processStream)

  std::string msg;
  while( readMessage(msg) )
  {
    if ( !m_pSession )
    {
      if ( !setSession( msg ) )
      { disconnect(); continue; }
    }
    try
    {
      m_pSession->next( msg );
    }
    catch( InvalidMessage& )
    {
      if( !m_pSession->isLoggedOn() )
      {
        disconnect();
        return;
      }
    }
  }

  QF_STACK_POP
}

bool ThreadedSocketConnection::setSession( const std::string& msg )
{ QF_STACK_PUSH(ThreadedSocketConnection::setSession)

  m_pSession = Session::lookupSession( msg, true );
  if ( !m_pSession ) 
  {
    m_log.onEvent( "Session not found for incoming message: " + msg );
    m_log.onIncoming( msg );
    return false;
  }

  SessionID sessionID = m_pSession->getSessionID();
  m_pSession = 0;

  // see if the session frees up within 5 seconds
  for( int i = 1; i <= 5; i++ )
  {
    if( !Session::isSessionRegistered( sessionID ) )
      m_pSession = Session::registerSession( sessionID );
    if( m_pSession ) break;
    process_sleep( 1 );
  }

  if ( !m_pSession ) 
    return false;
  if ( m_sessions.find(m_pSession->getSessionID()) == m_sessions.end() )
    return false;

  m_pSession->setResponder( this );
  return true;

  QF_STACK_POP
}

} // namespace FIX
