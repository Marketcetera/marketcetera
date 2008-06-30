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

#include "SocketServer.h"
#include "Utility.h"
#include "Exceptions.h"
#ifndef _MSC_VER
#include <unistd.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#endif
#include <exception>

namespace FIX
{
/// Handles events from SocketMonitor for server connections.
class ServerWrapper : public SocketMonitor::Strategy
{
public:
  ServerWrapper( std::set<int> sockets, SocketServer& server,
                 SocketServer::Strategy& strategy )
: m_sockets( sockets ), m_server( server ), m_strategy( strategy ) {}

private:
  void onConnect( SocketMonitor&, int socket )
  { QF_STACK_PUSH(ServerWrapper::onConnect)
    QF_STACK_POP
  }

  void onEvent( SocketMonitor& monitor, int socket )
  { QF_STACK_PUSH(ServerWrapper::onEvent)

    if( m_sockets.find(socket) != m_sockets.end() )
    {
      m_strategy.onConnect( m_server, socket, m_server.accept(socket) );
    }
    else
    {
      if( !m_strategy.onData( m_server, socket ) )
        onError( monitor, socket );
    }

    QF_STACK_POP
  }

  void onWrite( SocketMonitor&, int socket )
  { QF_STACK_PUSH(ServerWrapper::onWrite)

    m_strategy.onWrite( m_server, socket );

    QF_STACK_POP
  }

  void onError( SocketMonitor& monitor, int socket )
  { QF_STACK_PUSH(ServerWrapper::onError)

    m_strategy.onDisconnect( m_server, socket );
    monitor.drop( socket );

    QF_STACK_POP
  }

  void onError( SocketMonitor& )
  { QF_STACK_PUSH(ServerWrapper::onError)
    m_strategy.onError( m_server );
    QF_STACK_POP
  }

  void onTimeout( SocketMonitor& )
  { QF_STACK_PUSH(ServerWrapper::onTimeout)
    m_strategy.onTimeout( m_server );
    QF_STACK_POP
  };

  typedef std::set<int>
    Sockets;

  Sockets m_sockets;
  SocketServer& m_server;
  SocketServer::Strategy& m_strategy;
};

SocketServer::SocketServer( int timeout )
: m_monitor( timeout ) {}

int SocketServer::add( int port, bool reuse, bool noDelay )
  throw( SocketException& )
{
  if( m_portToInfo.find(port) != m_portToInfo.end() )
    return m_portToInfo[port].m_socket;

  int socket = socket_createAcceptor( port, reuse );
  if( socket < 0 )
    throw SocketException();
  if( noDelay )
    socket_setsockopt( socket, TCP_NODELAY );
  m_monitor.addRead( socket );

  SocketInfo info( socket, port, noDelay );
  m_socketToInfo[socket] = info;
  m_portToInfo[port] = info;
  return socket;
}

int SocketServer::accept( int socket )
{ QF_STACK_PUSH(SocketServer::accept)

  SocketInfo info = m_socketToInfo[socket];

  int result = socket_accept( socket );
  if( info.m_noDelay )
    socket_setsockopt( result, TCP_NODELAY );
  if ( result >= 0 )
    m_monitor.addConnect( result );
  return result;

  QF_STACK_POP
}

void SocketServer::close()
{ QF_STACK_PUSH(SocketServer::close)

  SocketToInfo::iterator i = m_socketToInfo.begin();
  for( ; i != m_socketToInfo.end(); ++i )
  {
    int s = i->first;
    socket_close( s );
    socket_invalidate( s );
  }

  QF_STACK_POP
}

bool SocketServer::block( Strategy& strategy, bool poll )
{ QF_STACK_PUSH(SocketServer::block)

  std::set<int> sockets;
  SocketToInfo::iterator i = m_socketToInfo.begin();
  for( ; i != m_socketToInfo.end(); ++i )
  {
    if( !socket_isValid(i->first) )
      return false;
    sockets.insert( i->first );
  }

  ServerWrapper wrapper( sockets, *this, strategy );
  m_monitor.block( wrapper, poll );
  return true;

  QF_STACK_POP
}

int SocketServer::socketToPort( int socket )
{
  SocketToInfo::iterator find = m_socketToInfo.find( socket );
  if( find == m_socketToInfo.end() ) return 0;
  return find->second.m_port;
}
 
int SocketServer::portToSocket( int port )
{
  SocketToInfo::iterator find = m_portToInfo.find( port );
  if( find == m_portToInfo.end() ) return 0;
  return find->second.m_socket;
}
}
