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
#pragma warning( disable : 4503 4355 4786 )
#include "stdafx.h"
#else
#include "config.h"
#endif

#include "DataDictionaryTestCase.h"
#include "FieldNumbers.h"
#include "Values.h"
#include "fix40/TestRequest.h"
#include "fix42/TestRequest.h"
#include "fix42/NewOrderSingle.h"
#include "fix40/NewOrderSingle.h"
#include "fix44/NewOrderList.h"
#include "fix44/MarketDataRequest.h"
#include "fix44/MarketDataSnapshotFullRefresh.h"
#include <fstream>

namespace FIX
{
USER_DEFINE_STRING( TooHigh, 501 );

void DataDictionaryTestCase::addMsgType::onRun( DataDictionary& object )
{
  assert( !object.isMsgType( "A" ) );
  object.addMsgType( "A" );
  assert( object.isMsgType( "A" ) );
}

void DataDictionaryTestCase::addMsgField::onRun( DataDictionary& object )
{
  assert( !object.isMsgField( "A", 10 ) );
  assert( !object.isMsgField( "Z", 50 ) );
  object.addMsgField( "A", 10 );
  object.addMsgField( "Z", 50 );
  assert( object.isMsgField( "A", 10 ) );
  assert( object.isMsgField( "Z", 50 ) );
  assert( !object.isMsgField( "A", 50 ) );
  assert( !object.isMsgField( "Z", 10 ) );
}

void DataDictionaryTestCase::addHeaderField::onRun( DataDictionary& object )
{
  assert( !object.isHeaderField( 56 ) );
  assert( !object.isHeaderField( 49 ) );
  object.addHeaderField( 56, true );
  object.addHeaderField( 49, true );
  assert( object.isHeaderField( 56 ) );
  assert( object.isHeaderField( 49 ) );
}

void DataDictionaryTestCase::addTrailerField::onRun( DataDictionary& object )
{
  assert( !object.isTrailerField( 10 ) );
  object.addTrailerField( 10, true );
  assert( object.isTrailerField( 10 ) );
}

void DataDictionaryTestCase::addFieldType::onRun( DataDictionary& object )
{
  TYPE::Type type;
  assert( !object.getFieldType( 14, type ) );
  assert( !object.getFieldType( 23, type ) );

  object.addFieldType( 14, TYPE::String );
  object.addFieldType( 23, TYPE::Char );

  assert( object.getFieldType( 14, type ) );
  assert( type == TYPE::String );
  assert( object.getFieldType( 23, type ) );
  assert( type == TYPE::Char );
}

void DataDictionaryTestCase::addRequiredField::onRun( DataDictionary& object )
{
  assert( !object.isRequiredField( "A", 10 ) );
  assert( !object.isRequiredField( "Z", 50 ) );
  object.addRequiredField( "A", 10 );
  object.addRequiredField( "Z", 50 );
  assert( object.isRequiredField( "A", 10 ) );
  assert( object.isRequiredField( "Z", 50 ) );
  assert( !object.isRequiredField( "A", 50 ) );
  assert( !object.isRequiredField( "Z", 10 ) );
}

void DataDictionaryTestCase::addFieldValue::onRun( DataDictionary& object )
{
  assert( !object.isFieldValue( 12, "f" ) );
  assert( !object.isFieldValue( 12, "g" ) );
  assert( !object.isFieldValue( 15, "1" ) );
  assert( !object.isFieldValue( 18, "2" ) );
  assert( !object.isFieldValue( 167, "FUT" ) );

  object.addFieldValue( 12, "f" );
  object.addFieldValue( 12, "g" );
  object.addFieldValue( 15, "1" );
  object.addFieldValue( 18, "2" );
  object.addFieldValue( 167, "FUT" );

  assert( object.isFieldValue( 12, "f" ) );
  assert( object.isFieldValue( 12, "g" ) );
  assert( object.isFieldValue( 15, "1" ) );
  assert( object.isFieldValue( 18, "2" ) );
  assert( object.isFieldValue( 167, "FUT" ) );
}

void DataDictionaryTestCase::addGroup::onRun( DataDictionary& object )
{
  object.setVersion( "FIX.4.2" );

  DataDictionary group1;
  group1.addMsgType( "1" );
  DataDictionary group2;
  group2.addMsgType( "2" );
  DataDictionary group3;
  group3.addMsgType( "3" );

  object.addGroup( "A", 100, 101, group1 );
  object.addGroup( "A", 200, 201, group2 );
  object.addGroup( "A", 300, 301, group3 );

  int delim;
  const DataDictionary* pDD = 0;

  assert( object.getGroup( "A", 100, delim, pDD ) );
  assert( delim == 101 );
  assert( pDD->isMsgType( "1" ) );

  assert( object.getGroup( "A", 200, delim, pDD ) );
  assert( delim == 201 );
  assert( pDD->isMsgType( "2" ) );

  assert( object.getGroup( "A", 300, delim, pDD ) );
  assert( delim == 301 );
  assert( pDD->isMsgType( "3" ) );
}

void DataDictionaryTestCase::addFieldName::onRun( DataDictionary& object )
{
  object.setVersion( "FIX.4.2" );

  object.addFieldName( 1, "Account" );
  object.addFieldName( 11, "ClOrdID" );
  object.addFieldName( 8, "BeginString" );

  std::string name;
  int field;
  assert( object.getFieldName( 1, name ) );
  assert( name == "Account" );
  assert( object.getFieldTag( name, field ) );
  assert( field == 1 );
  assert( object.getFieldName( 11, name ) );
  assert( name == "ClOrdID" );
  assert( object.getFieldTag( name, field ) );
  assert( field == 11 );
  assert( object.getFieldName( 8, name ) );
  assert( name == "BeginString" );
  assert( object.getFieldTag( name, field ) );
  assert( field == 8 );
}

void DataDictionaryTestCase::addValueName::onRun( DataDictionary& object )
{
  object.setVersion( "FIX.4.2" );

  object.addValueName( 12, "0", "VALUE_12_0" );
  object.addValueName( 12, "B", "VALUE_12_B" );
  object.addValueName( 23, "BOO", "VALUE_23_BOO" );

  std::string name;
  assert( object.getValueName( 12, "0", name ) );
  assert( name == "VALUE_12_0" );
  assert( object.getValueName( 12, "B", name ) );
  assert( name == "VALUE_12_B" );
  assert( object.getValueName( 23, "BOO", name ) );
  assert( name == "VALUE_23_BOO" );
}

bool DataDictionaryTestCase::checkValidTagNumber::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary;
  pObject->setVersion( BeginString_FIX40 );
  pObject->addField( FIELD::BeginString );
  pObject->addField( FIELD::BodyLength );
  pObject->addField( FIELD::MsgType );
  pObject->addField( FIELD::CheckSum );
  pObject->addField( FIELD::TestReqID );
  pObject->addMsgType( MsgType_TestRequest );
  pObject->addMsgField( MsgType_TestRequest, FIELD::TestReqID );
  return true;
}

void DataDictionaryTestCase::checkValidTagNumber::onRun
( DataDictionary& object )
{
  TestReqID testReqID( "1" );
  FIX40::TestRequest message( testReqID );
  message.setField( TooHigh( "value" ) );

  try{ object.validate( message ); assert( false ); }
  catch ( InvalidTagNumber& ) {}

  object.addField( 501 );
  object.addMsgField( MsgType_TestRequest, 501 );
  try{ object.validate( message ); }
  catch ( InvalidTagNumber& ) { assert( false ); }

  message.setField( FIELD::UserMin, "value" );
  try{ object.validate( message ); assert( false ); }
  catch ( InvalidTagNumber& ) {}

  object.checkUserDefinedFields( false );
  try{ object.validate( message ); }
  catch ( InvalidTagNumber& ) { assert( false ); }

}

bool DataDictionaryTestCase::checkHasValue::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary;
  return true;
}

void DataDictionaryTestCase::checkHasValue::onRun
( DataDictionary& object )
{
  Message testReqID( "8=FIX.4.2\0019=12\00135=1\001112=\00110=007\001", false );
  FIX42::TestRequest message( testReqID );

  try{ object.validate( message ); assert( false ); }
  catch ( NoTagValue& ) {}
}

bool DataDictionaryTestCase::checkIsInMessage::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary;
  pObject->setVersion( BeginString_FIX40 );
  pObject->addField( FIELD::BeginString );
  pObject->addField( FIELD::BodyLength );
  pObject->addField( FIELD::MsgType );
  pObject->addField( FIELD::CheckSum );
  pObject->addField( FIELD::TestReqID );
  pObject->addField( FIELD::Symbol );
  pObject->addMsgType( MsgType_TestRequest );
  pObject->addMsgField( MsgType_TestRequest, FIELD::TestReqID );
  return true;
}

void DataDictionaryTestCase::checkIsInMessage::onRun
( DataDictionary& object )
{
  TestReqID testReqID( "1" );

  FIX40::TestRequest message( testReqID );
  try{ object.validate( message ); }
  catch ( TagNotDefinedForMessage& ) { assert( false ); }

  message.setField( Symbol( "MSFT" ) );
  try{ object.validate( message ); assert( false ); }
  catch ( TagNotDefinedForMessage& ) {}
}

bool DataDictionaryTestCase::checkHasRequired::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary;
  pObject->setVersion( BeginString_FIX40 );
  pObject->addField( FIELD::BeginString );
  pObject->addField( FIELD::BodyLength );
  pObject->addField( FIELD::MsgType );
  pObject->addField( FIELD::SenderCompID );
  pObject->addField( FIELD::TargetCompID );
  pObject->addHeaderField( FIELD::SenderCompID, true );
  pObject->addHeaderField( FIELD::TargetCompID, false );
  pObject->addField( FIELD::CheckSum );
  pObject->addField( FIELD::TestReqID );
  pObject->addMsgType( MsgType_TestRequest );
  pObject->addMsgField( MsgType_TestRequest, FIELD::TestReqID );
  pObject->addRequiredField( MsgType_TestRequest, FIELD::TestReqID );
  return true;
}

void DataDictionaryTestCase::checkHasRequired::onRun
( DataDictionary& object )
{
  FIX40::TestRequest message;
  try{ object.validate( message ); assert( false ); }
  catch ( RequiredTagMissing& ) {}

  message.getHeader().setField( SenderCompID( "SENDER" ) );
  try{ object.validate( message ); assert( false ); }
  catch ( RequiredTagMissing& ) {}

  message.setField( TestReqID( "1" ) );
  try{ object.validate( message ); }
  catch ( TagNotDefinedForMessage& ) { assert( false ); }

  message.getHeader().removeField( FIELD::SenderCompID );
  message.setField( SenderCompID( "SENDER" ) );
  try{ object.validate( message ); assert( false ); }
  catch ( RequiredTagMissing& ) {}
}

bool DataDictionaryTestCase::checkValidFormat::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary;
  pObject->setVersion( BeginString_FIX40 );
  pObject->addField( FIELD::BeginString );
  pObject->addField( FIELD::BodyLength );
  pObject->addField( FIELD::MsgType );
  pObject->addField( FIELD::CheckSum );
  pObject->addField( FIELD::TestReqID );
  pObject->addMsgType( MsgType_TestRequest );
  pObject->addMsgField( MsgType_TestRequest, FIELD::TestReqID );
  pObject->addFieldType( FIELD::TestReqID, TYPE::Int );
  return true;
}

void DataDictionaryTestCase::checkValidFormat::onRun
( DataDictionary& object )
{
  FIX40::TestRequest message;
  message.setField( TestReqID( "+200" ) );
  try{ object.validate( message ); assert( false ); }
  catch ( IncorrectDataFormat& ) {}}

bool DataDictionaryTestCase::checkValue::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary;
  pObject->setVersion( BeginString_FIX40 );
  pObject->addField( FIELD::BeginString );
  pObject->addField( FIELD::BodyLength );
  pObject->addField( FIELD::MsgType );
  pObject->addField( FIELD::CheckSum );
  pObject->addField( FIELD::OrdType );
  pObject->addField( FIELD::OrderRestrictions );
  pObject->addMsgType( MsgType_NewOrderSingle );
  pObject->addMsgField( MsgType_NewOrderSingle, FIELD::OrdType );
  pObject->addMsgField( MsgType_NewOrderSingle, FIELD::OrderRestrictions );
  pObject->addFieldType( FIELD::OrdType, TYPE::Char );
  pObject->addFieldValue( FIELD::OrdType, "1" );
  pObject->addFieldType( FIELD::OrderRestrictions, TYPE::MultipleValueString );
  pObject->addFieldValue( FIELD::OrderRestrictions, "1" );
  pObject->addFieldValue( FIELD::OrderRestrictions, "2" );
  pObject->addFieldValue( FIELD::OrderRestrictions, "3" );
  return true;
}

void DataDictionaryTestCase::checkValue::onRun
( DataDictionary& object )
{
  FIX40::NewOrderSingle message;
  message.setField( OrdType( '1' ) );
  try{ object.validate( message ); }
  catch ( IncorrectTagValue& ) { assert( false ); }

  message.setField( OrdType( '2' ) );
  try{ object.validate( message ); assert( false ); }
  catch ( IncorrectTagValue& ) {}

  message.setField( OrdType( '1' ) );
  message.setField( OrderRestrictions("1 2 3") );
  try{ object.validate( message ); }
  catch ( IncorrectTagValue& ) { assert(false); }

  message.setField( OrderRestrictions("1 4 3") );
  try{ object.validate( message ); assert(false); }
  catch ( IncorrectTagValue& ) {}
}

bool DataDictionaryTestCase::checkRepeatedTag::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary;
  return true;
}

void DataDictionaryTestCase::checkRepeatedTag::onRun
( DataDictionary& object )
{
  FIX40::NewOrderSingle message;
  message.setField( OrdType('1') );
  message.setField( OrdType('1'), false );
  try{ object.validate( message ); assert(false); }
  catch ( RepeatedTag& ) {}
}

bool DataDictionaryTestCase::checkGroupCount::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary;
  pObject->setVersion( BeginString_FIX42 );
  pObject->addField( FIELD::BeginString );
  pObject->addField( FIELD::BodyLength );
  pObject->addField( FIELD::MsgType );
  pObject->addField( FIELD::CheckSum );
  pObject->addField( FIELD::NoAllocs );
  DataDictionary groupDD;
  groupDD.addField( FIELD::AllocAccount );
  pObject->addGroup( "D", FIELD::NoAllocs, FIELD::AllocAccount, groupDD );
  pObject->addMsgType( MsgType_NewOrderSingle );
  pObject->addMsgField( MsgType_NewOrderSingle, FIELD::NoAllocs );
  return true;
}

void DataDictionaryTestCase::checkGroupCount::onRun
( DataDictionary& object )
{
  FIX42::NewOrderSingle message;
  FIX42::NewOrderSingle::NoAllocs group;
  group.setField( AllocAccount("account") );
  message.addGroup( group );
  message.set( NoAllocs(2) );
  try{ object.validate( message ); assert(false); }
  catch ( RepeatingGroupCountMismatch& ) {}
}

bool DataDictionaryTestCase::checkGroupRequiredFields::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary( "../spec/FIX44.xml" );
  return true;
}

void DataDictionaryTestCase::checkGroupRequiredFields::onRun
( DataDictionary& object )
{
  FIX44::NewOrderList newOrderList;
  newOrderList.setString("8=FIX.4.49=18635=E49=FIXTEST56=TW128=SS134=252=20050225-16:54:3266=WMListOrID000000362394=368=173=111=SE102354=155=IBM67=163=021=381=060=20050225-16:54:3238=1000040=115=USD10=119", false, &object);
  try{ object.validate( newOrderList ); }
  catch ( RequiredTagMissing& ) { assert(false); }

  newOrderList.setString("8=FIX.4.49=15835=E49=FIXTEST56=TW128=SS134=252=20050225-16:54:3266=WMListOrID000000362394=368=173=163=021=381=060=20050225-16:54:3238=1000040=115=USD10=036", false, &object);
  try{ object.validate( newOrderList ); assert(false); }
  catch ( RequiredTagMissing& ) {}

  newOrderList.setString("8=FIX.4.49=26935=E49=FIXTEST56=TW128=SS134=252=20050225-16:54:3266=WMListOrID000000362394=368=173=211=SE102354=155=IBM67=163=021=381=060=20050225-16:54:3238=1000040=115=USD11=SE104555=MSFT67=163=021=381=060=20050225-16:54:3238=1000040=115=USD47=A10=109", false, &object);
  try{ object.validate( newOrderList ); assert(false); }
  catch ( RequiredTagMissing& ) {}

  FIX44::MarketDataRequest marketDataRequest(
    MDReqID("1"),
    SubscriptionRequestType( SubscriptionRequestType_SNAPSHOT_PLUS_UPDATES ),
    MarketDepth( 9999 ) );

  marketDataRequest.set( MDUpdateType( MDUpdateType_INCREMENTAL_REFRESH ) );
  marketDataRequest.set( AggregatedBook( true ) );
  marketDataRequest.set( MDImplicitDelete( true ) );

  FIX44::MarketDataRequest::NoRelatedSym noRelatedSym;

  noRelatedSym.set( Symbol( "QQQQ" ) );
  marketDataRequest.addGroup( noRelatedSym );

  FIX44::MarketDataRequest::NoMDEntryTypes noMDEntryTypes;

  noMDEntryTypes.set( MDEntryType( MDEntryType_BID ) );
  marketDataRequest.addGroup( noMDEntryTypes );

  noMDEntryTypes.set( MDEntryType( MDEntryType_OFFER ) );
  marketDataRequest.addGroup( noMDEntryTypes );

  noMDEntryTypes.set( MDEntryType( MDEntryType_TRADE ) );
  marketDataRequest.addGroup( noMDEntryTypes );

  try{ object.validate( marketDataRequest ); }
  catch ( RequiredTagMissing& ) { assert(false); }

  noMDEntryTypes.removeField( FIELD::MDEntryType );
  marketDataRequest.addGroup( noMDEntryTypes );
  try{ object.validate( marketDataRequest ); assert(false); }
  catch ( RequiredTagMissing& ) {}

  FIX44::MarketDataSnapshotFullRefresh md;
  md.set( MDReqID("1") );
  md.set( Symbol("QQQQ") );

  FIX44::MarketDataSnapshotFullRefresh::NoMDEntries entry;

  entry.set( MDEntryType( MDEntryType_OFFER ) );
  entry.set( MDEntryPx( 41.48 ) );
  entry.set( MDEntrySize( 500 ) );
  md.addGroup( entry );

  entry.set( MDEntryType( MDEntryType_BID ) );
  entry.set( MDEntryPx( 41.2 ) );
  entry.set( MDEntrySize( 300 ) );
  md.addGroup( entry );

  Message message( md.toString(), object );
  object.validate( message );
  //object.validate( md );
}

bool DataDictionaryTestCase::readFromFile::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary( "../spec/FIX43.xml" );
  return true;
}

void DataDictionaryTestCase::readFromFile::onRun
( DataDictionary& object )
{
  assert( object.isHeaderField( 56 ) );
  assert( !object.isHeaderField( 38 ) );
  assert( !object.isHeaderField( 10 ) );

  assert( object.isTrailerField( 10 ) );
  assert( !object.isTrailerField( 38 ) );
  assert( !object.isTrailerField( 56 ) );

  assert( object.isMsgType( "A" ) );
  assert( object.isMsgField( "A", 383 ) );

  TYPE::Type type = TYPE::Unknown;
  assert( object.getFieldType( 383, type ) );
  assert( type == TYPE::Length );

  assert( object.isRequiredField( "A", 108 ) );
  assert( !object.isRequiredField( "A", 383 ) );
  assert( object.isRequiredField( "D", 55 ) );
  assert( !object.isRequiredField( "B", 55 ) );

  assert( object.isFieldValue( 40, "A" ) );
  assert( !object.isFieldValue( 40, "Z" ) );

  std::string name;
  assert( object.getFieldName( 1, name ) );
  assert( name == "Account" );
  assert( object.getFieldName( 11, name ) );
  assert( name == "ClOrdID" );
  assert( object.getFieldName( 8, name ) );
  assert( name == "BeginString" );

  assert( object.getValueName( 18, "1", name ) );
  assert( name == "NOT_HELD" );
  assert( object.getValueName( 18, "2", name ) );
  assert( name == "WORK" );
  assert( object.getValueName( 18, "W", name ) );
  assert( name == "PEG_TO_VWAP" );

  const DataDictionary* pDD = 0;
  int delim = 0;
  assert( object.getGroup( "b", 296, delim, pDD ) );
  assert( delim == 302 );
  assert( pDD->isField( 295 ) );
  assert( pDD->isField( 310 ) );
  assert( !pDD->isField( 55 ) );
  assert( pDD->getGroup( "b", 295, delim, pDD ) );
  assert( delim == 299 );
  assert( pDD->isField( 55 ) );
  assert( !pDD->isField( 310 ) );
  assert( object.getGroup( "8", 453, delim, pDD ) );
  assert( delim == 448 );
  assert( object.getGroup( "y", 146, delim, pDD ) );
  assert( delim == 55 );
}

bool DataDictionaryTestCase::readFromStream::onSetup
( DataDictionary*& pObject )
{
  std::fstream stream( "../spec/FIX43.xml" );
  pObject = new DataDictionary( stream );
  return true;
}

void DataDictionaryTestCase::readFromStream::onRun
( DataDictionary& object )
{
  readFromFile::onRun( object );
}

bool DataDictionaryTestCase::copy::onSetup
( DataDictionary*& pObject )
{
  pObject = new DataDictionary;
  pObject->setVersion( BeginString_FIX40 );
  pObject->addMsgType( MsgType_NewOrderSingle );
  pObject->addMsgField( MsgType_NewOrderSingle, FIELD::OrdType );
  pObject->addFieldType( FIELD::OrdType, TYPE::Char );
  pObject->addFieldValue( FIELD::OrdType, "1" );

  DataDictionary dataDictionary1;
  dataDictionary1.addFieldType( FIELD::HeartBtInt, TYPE::String );
  DataDictionary dataDictionary2;
  dataDictionary2.addFieldType( FIELD::MsgType, TYPE::Char );
  dataDictionary1.addGroup( "A", 1, 2, dataDictionary2 );
  pObject->addGroup( "A", 10, 20, dataDictionary1 );
  return true;
}

void DataDictionaryTestCase::copy::onRun( DataDictionary& object )
{
  DataDictionary dataDictionary = object;
  TYPE::Type type;
  int delim;

  assert( dataDictionary.getVersion() == BeginString_FIX40 );
  assert( dataDictionary.isMsgType( MsgType_NewOrderSingle ) );
  assert( dataDictionary.isMsgField( MsgType_NewOrderSingle, FIELD::OrdType ) );
  assert( dataDictionary.getFieldType( FIELD::OrdType, type ) );
  assert( type == TYPE::Char );
  assert( dataDictionary.isFieldValue( FIELD::OrdType, "1" ) );

  const DataDictionary* pDD = 0;
  assert( dataDictionary.getGroup( "A", 10, delim, pDD ) );
  assert( pDD->getFieldType( FIELD::HeartBtInt, type ) );
  assert( type == TYPE::String );
  assert( delim == 20 );

  assert( pDD->getGroup( "A", 1, delim, pDD ) );
  assert( pDD->getFieldType( FIELD::MsgType, type ) );
  assert( type == TYPE::Char );
  assert( delim == 2 );
}
}
