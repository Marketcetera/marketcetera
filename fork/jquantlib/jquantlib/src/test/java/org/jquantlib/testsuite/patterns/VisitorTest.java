/*
 Copyright (C) 2007 Richard Gomes

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

package org.jquantlib.testsuite.patterns;

import static org.junit.Assert.fail;

import org.jquantlib.QL;
import org.jquantlib.util.TypedVisitable;
import org.jquantlib.util.TypedVisitor;
import org.jquantlib.util.Visitor;
import org.junit.Test;

/**
 *
 * @author Richard Gomes
 */
public class VisitorTest {

	public VisitorTest() {
		QL.info("::::: "+this.getClass().getSimpleName()+" :::::");
	}

	@Test
	public void testTypedVisitorPattern() {

		final NumberTypedVisitable  numberTypedVisitable  = new NumberTypedVisitable();
		final DoubleTypedVisitable  doubleTypedVisitable  = new DoubleTypedVisitable();
		final IntegerTypedVisitable integerTypedVisitable = new IntegerTypedVisitable();

		final NumberTypedVisitor  numberTypedVisitor  = new NumberTypedVisitor();
		final DoubleTypedVisitor  doubleTypedVisitor  = new DoubleTypedVisitor();
		final IntegerTypedVisitor integerTypedVisitor = new IntegerTypedVisitor();

		// these tests must pass
		numberTypedVisitable.accept(numberTypedVisitor);
		doubleTypedVisitable.accept(numberTypedVisitor);
		doubleTypedVisitable.accept(doubleTypedVisitor);
		integerTypedVisitable.accept(numberTypedVisitor);
		integerTypedVisitable.accept(integerTypedVisitor);

		// the following must fail
		try {
			numberTypedVisitable.accept(doubleTypedVisitor);
			fail("numberTypedVisitable.accept(doubleTypedVisitor) should fail!");
		} catch (final Exception e) {
			// OK, as expected
		}

		// the following must fail
		try {
			numberTypedVisitable.accept(integerTypedVisitor);
			fail("numberTypedVisitable.accept(integerTypedVisitor) should fail");
		} catch (final Exception e) {
			// OK, as expected
		}

		// the following must fail
		try {
			doubleTypedVisitable.accept(integerTypedVisitor);
			fail("doubleTypedVisitable.accept(integerTypedVisitor) should fail");
		} catch (final Exception e) {
			// OK, as expected
		}

		// the following must fail
		try {
			integerTypedVisitable.accept(doubleTypedVisitor);
			fail("integerTypedVisitable.accept(doubleTypedVisitor) should fail");
		} catch (final Exception e) {
			// OK, as expected
		}

	}

	private class NumberTypedVisitable implements TypedVisitable<Number> {
		@Override
		public void accept(final TypedVisitor<Number> v) {
			final Visitor<Number> visitor = (v!=null) ? v.getVisitor(Number.class) : null;
			if (visitor!=null)
				visitor.visit(1.0);
			else
				throw new IllegalArgumentException("not a Number visitor");
		}
	}


	private class DoubleTypedVisitable extends NumberTypedVisitable  /* implements TypedVisitable<Double> */ {
		private final double data = 5.6;

		@Override
		public void accept(final TypedVisitor<Number> v) {
			final Visitor<Number> visitor = (v!=null) ? v.getVisitor(Double.class) : null;
			if (visitor!=null)
				visitor.visit(data);
			else
				super.accept(v);
		}
	}


	private class IntegerTypedVisitable extends NumberTypedVisitable  /* implements TypedVisitable<Double> */ {
		private final int data = 3456;

		@Override
		public void accept(final TypedVisitor<Number> v) {
			final Visitor<Number> visitor = (v!=null) ? v.getVisitor(Integer.class) : null;
			if (visitor!=null)
				visitor.visit(data);
			else
				super.accept(v);
		}
	}



	//
	// declare TypedVisitors and corresponding Visitors
	//

	private static class NumberTypedVisitor implements TypedVisitor<Number> {

		@Override
		public Visitor<Number> getVisitor(final Class<? extends Number> klass) {
			return (klass==Number.class) ? numberVisitor : null;
		}

		//
		// composition pattern to an inner Visitor<Number>
		//

		private final NumberVisitor numberVisitor = new NumberVisitor();

		private static class NumberVisitor implements Visitor<Number> {
			@Override
			public void visit(final Number o) {
				QL.info("Number :: "+o);
			}
		}
	}


	private static class DoubleTypedVisitor extends NumberTypedVisitor {

		@Override
		public Visitor<Number> getVisitor(final Class<? extends Number> klass) {
			return (klass==Double.class) ? doubleVisitor : null;
		}

		//
		// composition pattern to an inner Visitor<Double>
		//

		private final DoubleVisitor doubleVisitor = new DoubleVisitor();

		private static class DoubleVisitor implements Visitor<Number> {
			@Override
			public void visit(final Number o) {
				final double obj = (Double)o;
				QL.info("Double :: "+obj);
			}
		}
	}

	private static class IntegerTypedVisitor extends NumberTypedVisitor {

		@Override
		public Visitor<Number> getVisitor(final Class<? extends Number> klass) {
			return (klass==Integer.class) ? integerVisitor : null;
		}

		//
		// composition pattern to an inner Visitor<Double>
		//

		private final IntegerVisitor integerVisitor = new IntegerVisitor();

		private static class IntegerVisitor implements Visitor<Number> {
			@Override
			public void visit(final Number o) {
				final Integer obj = (Integer)o;
				QL.info("Integer :: "+obj);
			}
		}
	}

}
