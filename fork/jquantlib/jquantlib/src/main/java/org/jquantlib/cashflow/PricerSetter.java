package org.jquantlib.cashflow;

import java.util.List;

import org.jquantlib.QL;
import org.jquantlib.lang.annotation.QualityAssurance;
import org.jquantlib.lang.annotation.QualityAssurance.Quality;
import org.jquantlib.lang.annotation.QualityAssurance.Version;
import org.jquantlib.lang.exceptions.LibraryException;
import org.jquantlib.util.TypedVisitor;
import org.jquantlib.util.Visitor;

/**
 * Coupon selector to PricerSetter
 *
 * @author Richard Gomes
 */
@QualityAssurance(quality=Quality.Q0_UNFINISHED, version=Version.V097, reviewers="Richard Gomes")
public class PricerSetter implements TypedVisitor<Object> {

    private static final String INCOMPATIBLE_PRICER = "incompatible pricer";
    private static final String UNKNOWN_VISITABLE = "unknown visitable";


    //
    // private fields
    //

    private final FloatingRateCouponPricer pricer;


    // private constructors
    //

    public PricerSetter(final FloatingRateCouponPricer pricer) {
        this.pricer = pricer;
    }


    //
    // static public methods
    //

    static public void setCouponPricer(final Leg leg, final FloatingRateCouponPricer pricer) {
        final PricerSetter setter = new PricerSetter(pricer);
        for (int i=0; i<leg.size(); i++) {
            leg.get(i).accept(setter);
        }
    }

    static public void setCouponPricers(final Leg leg, final List<FloatingRateCouponPricer> pricers) {
        QL.require(leg.size()>0 , "no cashflows");
        QL.require(leg.size() == pricers.size() , "mismatch between leg size and number of pricers");
        final int nCashFlows = leg.size();
        final int nPricers = pricers.size();
        for (int i=0; i<nCashFlows; i++) {
            final PricerSetter setter = new PricerSetter(i<nPricers ? pricers.get(i) : pricers.get(nPricers-1));
            leg.get(i).accept(setter);
        }
    }


    //
    // implements TypedVisitor
    //

    @Override
    public Visitor<Object> getVisitor(final Class<? extends Object> klass) {
        if (klass==CashFlow.class ) {
            return new CashFlowVisitor();
        }
        if ( klass == SimpleCashFlow.class) {
            return new CashFlowVisitor();
        }
        if (klass==Coupon.class) {
            return new CouponVisitor();
        }
        if (klass==IborCoupon.class) {
            return new IborCouponVisitor();
        }

//        if (klass == CmsCoupon.class)
//            return new CmsCouponVisitor();
//        if (klass == CappedFlooredIborCoupon.class)
//            return new CappedFlooredIborCouponVisitor();
//        if (klass == CappedFlooredCmsCoupon.class)
//            return new CappedFlooredCmsCouponVisitor();
//        if (klass == DigitalIborCoupon.class)
//            return new DigitalIborCouponVisitor();
//        if (klass == DigitalCmsCoupon.class)
//            return new DigitalCmsCouponVisitor();
//        if (klass == RangeAccrualFloatersCoupon.class)
//            return new RangeAccrualFloatersCouponVisitor();
//        if (klass == SubPeriodsCoupon.class)
//            return new SubPeriodsCouponVisitor();


        throw new LibraryException(UNKNOWN_VISITABLE); // QA:[RG]::verified
    }


    //
    // private inner classes
    //

    private class CashFlowVisitor implements Visitor<Object> {
        @Override
        public void visit(final Object o) {
            // nothing
        }
    }
    private class SimpleCashFlowVisitor implements Visitor<Object> {
        @Override
        public void visit(final Object o) {
            // nothing
        }
    }

    private class CouponVisitor implements Visitor<Object> {
        @Override
        public void visit(final Object o) {
            // nothing
        }
    }

    private class IborCouponVisitor implements Visitor<Object> {
        @Override
        public void visit(final Object o) {
            if (IborCouponPricer.class.isAssignableFrom(pricer.getClass())) {
                final IborCoupon c = (IborCoupon) o;
                c.setPricer(pricer);
            } else {
                throw new LibraryException(INCOMPATIBLE_PRICER); // QA:[RG]::verified
            }
        }
    }

//    private class CmsCouponVisitor implements Visitor<Object> {
//        @Override
//        public void visit(final Object o) {
//            if (CmsCouponPricer.class.isAssignableFrom(pricer.getClass())) {
//                final CmsCoupon c = (CmsCoupon) o;
//                c.setPricer((CmsCouponPricer)pricer);
//            } else {
//                throw new LibraryException(INCOMPATIBLE_PRICER); // QA:[RG]::verified
//            }
//        }
//    }
//
//    private class CappedFlooredIborCouponVisitor implements Visitor<Object> {
//        @Override
//        public void visit(final Object o) {
//            if (CappedFlooredIborCouponPricer.class.isAssignableFrom(pricer.getClass())) {
//                final CappedFlooredIborCoupon c = (CappedFlooredIborCoupon) o;
//                c.setPricer((CappedFlooredIborCouponPricer)pricer);
//            } else {
//                throw new LibraryException(INCOMPATIBLE_PRICER); // QA:[RG]::verified
//            }
//        }
//    }
//
//    private class CappedFlooredCmsCouponVisitor implements Visitor<Object> {
//        @Override
//        public void visit(final Object o) {
//            if (CappedFlooredCmsCouponPricer.class.isAssignableFrom(pricer.getClass())) {
//                final CappedFlooredCmsCoupon c = (CappedFlooredCmsCoupon) o;
//                c.setPricer((CappedFlooredCmsCouponPricer)pricer);
//            } else {
//                throw new LibraryException(INCOMPATIBLE_PRICER); // QA:[RG]::verified
//            }
//        }
//    }
//
//    private class DigitalIborCouponVisitor implements Visitor<Object> {
//        @Override
//        public void visit(final Object o) {
//            if (DigitalIborCouponPricer.class.isAssignableFrom(pricer.getClass())) {
//                final DigitalIborCoupon c = (DigitalIborCoupon) o;
//                c.setPricer((DigitalIborCouponPricer)pricer);
//            } else {
//                throw new LibraryException(INCOMPATIBLE_PRICER); // QA:[RG]::verified
//            }
//        }
//    }
//
//    private class DigitalCmsCouponVisitor implements Visitor<Object> {
//        @Override
//        public void visit(final Object o) {
//            if (DigitalCmsCouponPricer.class.isAssignableFrom(pricer.getClass())) {
//                final DigitalCmsCoupon c = (DigitalCmsCoupon) o;
//                c.setPricer((DigitalCmsCouponPricer)pricer);
//            } else {
//                throw new LibraryException(INCOMPATIBLE_PRICER); // QA:[RG]::verified
//            }
//        }
//    }
//
//    private class RangeAccrualFloatersCouponVisitor implements Visitor<Object> {
//        @Override
//        public void visit(final Object o) {
//            if (RangeAccrualFloatersCouponPricer.class.isAssignableFrom(pricer.getClass())) {
//                final RangeAccrualFloatersCoupon c = (RangeAccrualFloatersCoupon) o;
//                c.setPricer((RangeAccrualFloatersCouponPricer)pricer);
//            } else {
//                throw new LibraryException(INCOMPATIBLE_PRICER); // QA:[RG]::verified
//            }
//        }
//    }
//
//    private class SubPeriodsCouponVisitor implements Visitor<Object> {
//        @Override
//        public void visit(final Object o) {
//            if (SubPeriodsCouponPricer.class.isAssignableFrom(pricer.getClass())) {
//                final SubPeriodsCoupon c = (SubPeriodsCoupon) o;
//                c.setPricer((SubPeriodsCouponPricer)pricer);
//            } else {
//                throw new LibraryException(INCOMPATIBLE_PRICER); // QA:[RG]::verified
//            }
//        }
//    }

}
