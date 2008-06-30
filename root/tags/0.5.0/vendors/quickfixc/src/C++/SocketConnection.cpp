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

#include "SocketConnection.h"
#include "SocketAcceptor.h"
#include "SocketConnector.h"
#include "SocketInitiator.h"
#include "Session.h"
#include "Utility.h"

namespace FIX
{
SocketConnection::SocketConnection( int s, Sessions sessions,
                                    SocketMonitor* pMonitor )
: m_socket( s ), m_sendLength( 0 ),
  m_sessions(sessions), m_pSession( 0 ), m_pMonitor( pMonitor )
{
  FD_ZERO( &m_fds );
  FD_SET( m_socket, &m_fds );
}

SocketConnection::SocketConnection( SocketInitiator& i,
                                    const SessionID& sessionID, int s,
                                    SocketMonitor* pMonitor )
: m_socket( s ), m_sendLength( 0 ),
  m_pSession( i.getSession( sessionID, *this ) ),
  m_pMonitor( pMonitor ) 
{
  FD_ZERO( &m_fds );
  FD_SET( m_socket, &m_fds );
  m_sessions.insert( sessionID );
}

SocketConnection::~SocketConnection()
{
  if ( m_pSession )
    Session::unregisterSession( m_pSession->getSessionID() );
}

bool SocketConnection::send( const std::string& msg )
{ QF_STACK_PUSH(SocketConnection::send)

  Locker l( m_mutex );

  m_sendQueue.push_back( msg );
  processQueue();
  signal();
  return true;

  QF_STACK_POP
}

bool SocketConnection::processQueue()
{ QF_STACK_PUSH(SocketConnection::processQueue)

  Locker l( m_mutex );

  if( !m_sendQueue.size() ) return true;

  struct timeval timeout = { 0, 0 };
  fd_set writeset = m_fds;
  if( select( 1 + m_socket, 0, &writeset, 0, &timeout ) <= 0 )
    return false;
    
  const std::string& msg = m_sendQueue.front();

  int result = socket_send
    ( m_socket, msg.c_str() + m_sendLength, msg.length() - m_sendLength );

  if( result > 0 )
    m_sendLength += result;

  if( m_sendLength == msg.length() )
  {
    m_sendLength = 0;
    m_sendQueue.pop_front();
  }

  return !m_sendQueue.size();

  QF_STACK_POP
}

void SocketConnection::disconnect()
{ QF_STACK_PUSH(SocketConnection::disconnect)

  if ( m_pMonitor )
    m_pMonitor->drop( m_socket );

  QF_STACK_POP
}

bool SocketConnection::read( SocketConnector& s )
{ QF_STACK_PUSH(SocketConnection::read)

  if ( !m_pSession ) return false;

  try
  {
    readFromSocket();
    readMessages( s.getMonitor() );
  }
  catch( SocketRecvFailed& e )
  {
    m_pSession->getLog()->onEvent( e.what() );
    return false;
  }
  return true;

  QF_STACK_POP
}

bool SocketConnection::read( SocketAcceptor& a, SocketServer& s )
{ QF_STACK_PUSH(SocketConnection::read)

  std::string msg;
  try
  {
    readFromSocket();

    if ( !m_pSession )
    {
      if ( !readMessage( msg ) ) return false;
      m_pSession = Session::lookupSession( msg, true );
      if( !isValidSession() )
      {
        m_pSession = 0;
        a.onEvent( "Session not found for incoming message: " + msg );
        a.onIncoming( msg );
      }
      if( m_pSession )
        m_pSession = a.getSession( msg, *this );
      if( m_pSession )
        m_pSession->next( msg );
      if( !m_pSession )
      {
        s.getMonitor().drop( m_socket );
        return false;
      }

      Session::registerSession( m_pSession->getSessionID() );
    }

    readMessages( s.getMonitor() );
    return true;
  }
  catch ( SocketRecvFailed& e )
  {
    if( m_pSession )
      m_pSession->getLog()->onEvent( e.what() );
    s.getMonitor().drop( m_socket );
  }
  catch ( InvalidMessage& )
  {
    s.getMonitor().drop( m_socket );
  }
  return false;

  QF_STACK_POP
}

bool SocketConnection::isValidSession()
{ QF_STACK_PUSH(SocketConnection::isValidSession)

  if( m_pSession == 0 )
    return false;
  SessionID sessionID = m_pSession->getSessionID();
  if( Session::isSessionRegistered(sessionID) )
    return false;
  return !( m_sessions.find(sessionID) == m_sessions.end() );

  QF_STACK_POP
}

void SocketConnection::readFromSocket()
throw( SocketRecvFailed )
{ QF_STACK_PUSH(SocketConnection::readFromSocket)

  int size = recv( m_socket, m_buffer, sizeof(m_buffer), 0 );
  if( size <= 0 ) throw SocketRecvFailed( size );
  m_parser.addToStream( m_buffer, size );

  QF_STACK_POP
}

bool SocketConnection::readMessage( std::string& msg )
{ QF_STACK_PUSH(SocketConnection::readMessage)

  try
  {
    return m_parser.readFixMessage( msg );
  }
  catch ( MessageParseError& ) {}
  return true;

  QF_STACK_POP
}

void SocketConnection::readMessages( SocketMonitor& s )
{
  if( !m_pSession ) return;

  std::string msg;
  while( readMessage( msg ) )
  {
    try
    {
      m_pSession->next( msg );
    }
    catch ( InvalidMessage& )
    {
      if( !m_pSession->isLoggedOn() )
        s.drop( m_socket );
    }
  }
}

void SocketConnection::onTimeout()
{ QF_STACK_PUSH(SocketConnection::onTimeout)
  if ( m_pSession ) m_pSession->next();
  QF_STACK_POP
}
} // namespace FIX
