package org.marketcetera.quotefeed;

import quickfix.Message;


public interface ILevel2Listener {
	void level2Updated(Message aMessage);
}
