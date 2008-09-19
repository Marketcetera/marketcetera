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

#ifndef FIX_FILEUTILITIESTESTCASE_H
#define FIX_FILEUTILITIESTESTCASE_H

#include <CPPTest/TestCase.h>
#include "Utility.h"

namespace FIX
{
class FileUtilitiesTestCase : public CPPTest::TestCase < int >
{
public:
  FileUtilitiesTestCase()
  {
    add( &m_separator );
    add( &m_appendpath );
  }

private:
  typedef CPPTest::SimpleTest< int > Test;

class separator : public Test
  {
    void onRun( int& );
  } m_separator;

class appendpath : public Test
  {
    void onRun( int& );
  } m_appendpath;
};
}

#endif //FIX_FILEUTILITIESTESTCASE_H
