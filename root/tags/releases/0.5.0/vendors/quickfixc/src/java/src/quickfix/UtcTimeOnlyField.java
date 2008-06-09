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

package quickfix;

import java.util.Date;

public class UtcTimeOnlyField extends DateField {
	private boolean showMilliseconds = false;

	public UtcTimeOnlyField(int field) {
        super(field);
    }

    public UtcTimeOnlyField(int field, boolean showMilliseconds) {
        super(field);
        this.showMilliseconds = showMilliseconds;
    }

    public UtcTimeOnlyField(int field, Date data) {
        super(field, data);
    }

    public UtcTimeOnlyField(int field, Date data, boolean showMilliseconds) {
        super(field, data);
        this.showMilliseconds = showMilliseconds;
    }

    boolean showMilliseconds() {
        return showMilliseconds;
    }
}

