/*
 Copyright (C) 2008 Srinivas Hasti

 This source code is release under the BSD License.

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the JQuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.

 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */

package org.jquantlib.indexes;

import java.util.concurrent.ConcurrentHashMap;

import org.jquantlib.time.TimeSeries;
import org.jquantlib.util.Observable;
import org.jquantlib.util.ObservableValue;

//FIXME: code review
public class IndexManager extends ConcurrentHashMap<String, TimeSeries<Double>> {

    private static final long serialVersionUID = -9204254124065694863L;

    /**
     * @see <a href="http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html">The "Double-Checked Locking is Broken" Declaration </a>
     */
    private static volatile IndexManager instance;

	private IndexManager() {
	    // noone can directly instantiate
	}

	public static IndexManager getInstance() {
		if (instance == null) {
			synchronized (IndexManager.class) {
				if (instance == null) {
					instance = new IndexManager();
				}
			}
		}
		return instance;
	}

	public Observable notifier(final String name) {
	    TimeSeries<Double> value = super.get(name);
		if (value == null){
			value = new TimeSeries<Double>(){ /* anonymous class */ };
			super.put(name, value);
		}
		return new ObservableValue<TimeSeries<Double>>(value);
	}

	public void clearHistory(final String name){
		super.remove(name);
	}

	public void clearHistories(){
		super.clear();
	}

}
