package org.jquantlib.indexes;

import org.jquantlib.quotes.Handle;
import org.jquantlib.termstructures.YieldTermStructure;
import org.jquantlib.time.Period;
import org.jquantlib.time.TimeUnit;

/**
 * <p>2-weeks Euribor index </p>    
 */
public class Euribor2W extends Euribor {


    //
    // public methods
    //

    public Euribor2W(final Handle < YieldTermStructure > h) {
        super(new Period(2, TimeUnit.Weeks), h);
    }

}
