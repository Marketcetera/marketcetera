/*
 Copyright (C) 2008 Srinivas Hasti

 This file is part of JQuantLib, a free-software/open-source library
 for financial quantitative analysts and developers - http://jquantlib.org/

 JQuantLib is free software: you can redistribute it and/or modify it
 under the terms of the QuantLib license.  You should have received a
 copy of the license along with this program; if not, please email
 <jquant-devel@lists.sourceforge.net>. The license is also available online at
 <http://www.jquantlib.org/index.php/LICENSE.TXT>.

 This program is distributed in the hope that it will be useful, but WITHOUT
 ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 FOR A PARTICULAR PURPOSE.  See the license for more details.

 JQuantLib is based on QuantLib. http://quantlib.org/
 When applicable, the original copyright notice follows this notice.
 */
package org.jquantlib.pricingengines.vanilla.finitedifferences;

import org.jquantlib.methods.finitedifferences.AmericanCondition;
import org.jquantlib.processes.GeneralizedBlackScholesProcess;

/**
 * This class conditionally extends other classes
 * <p>
 * <pre>
 * FDDividendEngine------------|
 * FDDividendEngineMerton73----|
 * FDDividenEngineShiftScale---|
 * FDStepConditionEngine-------|o---FDAmericanCondition
 * </pre>
 *
 * @see <a href="http://bugs.jquantlib.org/view.php?id=425">issue 425</a>
 *
 * @author Srinivas Hasti
 * @author Richard Gomes
 */
//TODO: http://bugs.jquantlib.org/view.php?id=425
public class FDAmericanCondition
        <T                            /* TODO: should be:: T extends FDDividendEngineBase & FDStepConditionEngine */ >
        extends FDStepConditionEngine /* TODO: should be:: implements FDDividendEngineBase, FDStepConditionEngine */ {

    public FDAmericanCondition(final GeneralizedBlackScholesProcess process) {
        this(process, 100, 100);
    }

    public FDAmericanCondition(
            final GeneralizedBlackScholesProcess process,
            final int timeSteps,
            final int gridPoints) {
        this(process, timeSteps, gridPoints, false);
    }

    public FDAmericanCondition(
            final GeneralizedBlackScholesProcess process,
            final int timeSteps,
            final int gridPoints,
            final boolean value) {
        super(process, timeSteps, gridPoints, value);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jquantlib.pricingengines.vanilla.finitedifferences.FDStepConditionEngine
     * #initializeStepCondition()
     */
    @Override
    protected void initializeStepCondition() {
        stepCondition = new AmericanCondition(intrinsicValues.values());
    }

}
