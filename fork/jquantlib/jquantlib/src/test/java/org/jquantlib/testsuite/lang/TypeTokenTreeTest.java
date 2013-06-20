package org.jquantlib.testsuite.lang;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeMap;

import org.jquantlib.QL;
import org.jquantlib.cashflow.Leg;
import org.jquantlib.daycounters.DayCounter;
import org.jquantlib.lang.reflect.TypeNode;
import org.jquantlib.lang.reflect.TypeTokenTree;
import org.jquantlib.math.interpolations.factories.Linear;
import org.jquantlib.quotes.Handle;
import org.jquantlib.quotes.Quote;
import org.jquantlib.termstructures.IterativeBootstrap;
import org.jquantlib.termstructures.RateHelper;
import org.jquantlib.termstructures.yieldcurves.Discount;
import org.jquantlib.termstructures.yieldcurves.PiecewiseYieldCurve;
import org.jquantlib.time.Date;
import org.junit.Ignore;
import org.junit.Test;

public class TypeTokenTreeTest {

    private final MyClass       myClass;
    private final AnotherClass  anotherClass;
    private final ListClass     listClass;
    private final MapClass      mapClass;

    public TypeTokenTreeTest() {
        QL.info("::::: "+this.getClass().getSimpleName()+" :::::");

        // notice the usage of an anonymous class denoted by "{ }"

        this.myClass      = new MyClass<HashMap<String, Double>, TreeMap<String, LinkedList<List<Double>>>, List<Integer>>() { };
        this.anotherClass = new AnotherClass<HashMap<String, Double>, TreeMap<String, LinkedList<List<Double>>>, List<Integer>>() { };
        this.listClass    = new ListClass<HashMap<String, Double>, TreeMap<String, LinkedList<List<Double>>>, List<Integer>>() { };
        this.mapClass     = new MapClass<HashMap<String, Double>, TreeMap<String, LinkedList<List<Double>>>, List<Integer>>() { };
    }

    private void testFirstGenericParameter(final TypeNodeTester testClass) {
        final TypeNode node = testClass.getTypeNode(0);
        assertTrue("First generic parameter should be a HashMap", node.getElement().isAssignableFrom(HashMap.class));

        TypeNode subnode;
        subnode = testClass.getTypeNode(node, 0);
        assertTrue("Inner first generic parameter should be a String", subnode.getElement().isAssignableFrom(String.class));

        subnode = testClass.getTypeNode(node, 1);
        assertTrue("Inner second generic parameter should be a Double", subnode.getElement().isAssignableFrom(Double.class));
    }


    private void testSecondGenericParameter(final TypeNodeTester testClass) {
        final TypeNode node = testClass.getTypeNode(1);
        assertTrue("First generic parameter should be a TreeMap", node.getElement().isAssignableFrom(TreeMap.class));

        TypeNode subnode;
        subnode = testClass.getTypeNode(node, 0);
        assertTrue("Inner first generic parameter should be a String", subnode.getElement().isAssignableFrom(String.class));

        subnode = testClass.getTypeNode(node, 1);
        assertTrue("Inner second generic parameter should be a LinkedList", subnode.getElement().isAssignableFrom(LinkedList.class));
        subnode = testClass.getTypeNode(subnode, 0);
        assertTrue("Inner generic parameter should be a List", subnode.getElement().isAssignableFrom(List.class));
        subnode = testClass.getTypeNode(subnode, 0);
        assertTrue("Inner generic parameter should be a Double", subnode.getElement().isAssignableFrom(Double.class));
    }

    private void testThirdGenericParameter(final TypeNodeTester testClass) {
        final TypeNode node = testClass.getTypeNode(2);
        assertTrue("First generic parameter should be a List", node.getElement().isAssignableFrom(List.class));

        TypeNode subnode;
        subnode = testClass.getTypeNode(node, 0);
        assertTrue("Inner first generic parameter should be a Integer", subnode.getElement().isAssignableFrom(Integer.class));
    }


    @Test
    public void testFirstGenericParameter() {
        testFirstGenericParameter(myClass);
        testFirstGenericParameter(anotherClass);
        testFirstGenericParameter(listClass);
        testFirstGenericParameter(mapClass);
    }


    @Test
    public void testSecondGenericParameter() {
        testSecondGenericParameter(myClass);
        testSecondGenericParameter(anotherClass);
        testSecondGenericParameter(listClass);
        testSecondGenericParameter(mapClass);
    }

    @Test
    public void testThirdGenericParameter() {
        testThirdGenericParameter(myClass);
        testThirdGenericParameter(anotherClass);
        testThirdGenericParameter(listClass);
        testThirdGenericParameter(mapClass);
    }

    @Ignore
    @Test
    public void testPiecewiseYieldCurve() {
        final Date d = new Date();
        final RateHelper[] instruments = null;
        final DayCounter dc = new DayCounter();
        final Handle<Quote> jumps[] = null;
        final Date[] jumpDates = null;
        final double accuracy = 0;
        final org.jquantlib.math.interpolations.Interpolation.Interpolator i = new Linear();
        final org.jquantlib.termstructures.Bootstrap b = new IterativeBootstrap(PiecewiseYieldCurve.class);


        final PiecewiseYieldCurve<Discount, Linear, IterativeBootstrap> pyc = new PiecewiseYieldCurve<Discount, Linear, IterativeBootstrap>(
                d, instruments, dc, jumps, jumpDates, accuracy, i, b) { /* anonymous */ };
        if (pyc == null) {
            throw new NullPointerException();
        }
    }

//    public PiecewiseYieldCurve(
//                final Date referenceDate,
//                final RateHelper[] instruments,
//                final DayCounter dayCounter,
//                final Handle<Quote>[] jumps,
//                final Date[] jumpDates,
//                final /*@Real*/ double accuracy, //TODO: default value: 1.0e-12
//                final I interpolator, //TODO: default value: Interpolator()
//                final B bootstrap) { //TODO: default value: Bootstrap<this_curve>()
//
//
//
//    }


    private interface TypeNodeTester {
        public TypeNode getTypeNode(final int index);
        public TypeNode getTypeNode(final TypeNode node, final int index);
    }


    private class MyClass<X, Y, Z> implements TypeNodeTester {

        private final TypeNode root;

        public MyClass() {
            this.root = new TypeTokenTree(this.getClass()).getRoot();
        }

        public TypeNode getTypeNode(final int index) {
            return root.get(index);
        }

        public TypeNode getTypeNode(final TypeNode node, final int index) {
            return node.get(index);
        }

    }

    private class AnotherClass<X, Y, Z> extends Leg implements TypeNodeTester {

        private final TypeNode root;

        public AnotherClass() {
            this.root = new TypeTokenTree(this.getClass()).getRoot();
        }

        public TypeNode getTypeNode(final int index) {
            return root.get(index);
        }

        public TypeNode getTypeNode(final TypeNode node, final int index) {
            return node.get(index);
        }

    }

    private class ListClass<X, Y, Z> extends ArrayList<X> implements TypeNodeTester {

        private final TypeNode root;

        public ListClass() {
            this.root = new TypeTokenTree(this.getClass()).getRoot();
        }

        public TypeNode getTypeNode(final int index) {
            return root.get(index);
        }

        public TypeNode getTypeNode(final TypeNode node, final int index) {
            return node.get(index);
        }

    }

    private class MapClass<X, Y, Z> extends HashMap<X,Y> implements TypeNodeTester {

        private final TypeNode root;

        public MapClass() {
            this.root = new TypeTokenTree(this.getClass()).getRoot();
        }

        public TypeNode getTypeNode(final int index) {
            return root.get(index);
        }

        public TypeNode getTypeNode(final TypeNode node, final int index) {
            return node.get(index);
        }

    }

}
