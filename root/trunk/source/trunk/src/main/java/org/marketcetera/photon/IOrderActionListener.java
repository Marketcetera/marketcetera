package org.marketcetera.photon;

import org.marketcetera.core.ClassVersion;

import quickfix.Message;

@ClassVersion("$Id$")
public interface IOrderActionListener {
    void orderActionTaken(Message fixMessage);
}
