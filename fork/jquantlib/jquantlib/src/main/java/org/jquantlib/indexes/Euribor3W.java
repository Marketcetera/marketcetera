package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>3-weeks Euribor index </p>    
 */
public class Euribor3W extends Euribor {


    //
    // public methods
    //

    public Euribor3W(final Handle < YieldTermStructure > h) {
        super(new Period(3, TimeUnit.Weeks), h);
    }

}
