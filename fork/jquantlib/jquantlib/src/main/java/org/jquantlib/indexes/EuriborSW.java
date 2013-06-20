package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>1-week Euribor index </p>    
 */
public class EuriborSW extends Euribor {


    //
    // public methods
    //

    public EuriborSW(final Handle < YieldTermStructure > h) {
        super(new Period(1, TimeUnit.Weeks), h);
    }

}
