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

#ifdef HAVE_JAVA

#include "JVM.h"
#include "Conversions.h"
#include "quickfix_ScreenLogFactory.h"
#include <quickfix/Log.h>
#include <quickfix/CallStack.h>

JNIEXPORT void JNICALL Java_quickfix_ScreenLogFactory__1create__Lquickfix_SessionSettings_2
( JNIEnv *pEnv, jobject obj, jobject jsettings )
{ QF_STACK_TRY

  JVM::set( pEnv );
  JVMObject object( obj );
  JVMObject settings( jsettings );

  FIX::SessionSettings* pSettings
  = ( FIX::SessionSettings* ) settings.getLong( "cppPointer" );

  FIX::LogFactory* pFactory = new FIX::ScreenLogFactory
                              ( *pSettings );
  object.setLong( "cppPointer", ( long ) pFactory );

  QF_STACK_CATCH
}

JNIEXPORT void JNICALL Java_quickfix_ScreenLogFactory__1create__ZZZ
( JNIEnv *pEnv, jobject obj, jboolean incoming, jboolean outgoing, jboolean event )
{ QF_STACK_TRY

  JVM::set( pEnv );
  JVMObject object( obj );

  FIX::LogFactory* pFactory = new FIX::ScreenLogFactory
                              ( incoming, outgoing, event );
  object.setLong( "cppPointer", ( long ) pFactory );

  QF_STACK_CATCH
}

JNIEXPORT void JNICALL Java_quickfix_ScreenLogFactory__1destroy
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  JVM::set( pEnv );
  JVMObject jobject( obj );
  FIX::ScreenLogFactory* pFactory
  = ( FIX::ScreenLogFactory* ) jobject.getLong( "cppPointer" );
  delete pFactory;

  QF_STACK_CATCH
}

JNIEXPORT jobject JNICALL Java_quickfix_ScreenLogFactory_create__
( JNIEnv *pEnv, jobject obj )
{ QF_STACK_TRY

  JVM::set( pEnv );
  JVMObject jobject( obj );

  FIX::ScreenLogFactory* pFactory
  = ( FIX::ScreenLogFactory* ) jobject.getLong( "cppPointer" );

  try
  {
    FIX::Log* pLog = pFactory->create();
    JVMClass type( "Lquickfix/ScreenLog;" );
    jmethodID method = pEnv->GetMethodID( type, "<init>", "(J)V" );
    JVMObject result = pEnv->NewObject( type, method, ( jlong ) pLog );
    return result;
  }
  catch ( FIX::ConfigError & e )
  {
    throwNew( "Lquickfix/ConfigError;", e.what() );
  }
  return 0;

  QF_STACK_CATCH
}

JNIEXPORT jobject JNICALL Java_quickfix_ScreenLogFactory_create__Lquickfix_SessionID_2
( JNIEnv * pEnv, jobject obj, jobject sessionID )
{ QF_STACK_TRY

  if( isNullAndThrow(sessionID) ) return 0;

  JVM::set( pEnv );
  JVMObject jobject( obj );
  JVMObject jsession( sessionID );

  FIX::ScreenLogFactory* pFactory
  = ( FIX::ScreenLogFactory* ) jobject.getLong( "cppPointer" );
  FIX::SessionID* pSessionID
  = ( FIX::SessionID* ) jsession.getLong( "cppPointer" );

  try
  {
    FIX::Log* pLog = pFactory->create( *pSessionID );
    JVMClass type( "Lquickfix/ScreenLog;" );
    jmethodID method = pEnv->GetMethodID( type, "<init>", "(J)V" );
    JVMObject result = pEnv->NewObject( type, method, ( jlong ) pLog );
    return result;
  }
  catch ( FIX::ConfigError & e )
  {
    throwNew( "Lquickfix/ConfigError;", e.what() );
  }
  return 0;

  QF_STACK_CATCH
}

#endif
