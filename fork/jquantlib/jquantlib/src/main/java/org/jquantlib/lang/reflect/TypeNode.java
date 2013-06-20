package org.jquantlib.lang.reflect;

import java.util.AbstractSequentialList;
import java.util.LinkedList;

import org.jquantlib.lang.annotation.PackagePrivate;

/**
 * TypeNode keeps a Class information and a list of children classes.
 * <p>
 * A typical usage consists on obtain a {@link TypeNode} root which can be used to traverse a hierarchy of generic parameters.
 * Each {@link TypeNode} holds a Class information and contains a list of children nodes. A typical usage is in the context of
 * retrieving actual generic parameters. The example below shows how a generic class can verify at instantiation time
 * which actual generic parameters where passed by the caller.
 * <code>
 * public class TimeSeries<T> {
 *
 *     public TimeSeries() {
 *         this.klass = new TypeTokenTree(this.getClass()).getRoot().get(0).getElement();
 *         if (Double.class.isAssignableFrom(klass)) {
 *             this.delegate = new SeriesDouble<T>();
 *         } else if (IntervalPrice.class.isAssignableFrom(klass)) {
 *             this.delegate = new SeriesIntervalPrice<T>();
 *         } else {
 *             throw new UnsupportedOperationException("only Double and IntervalPrice are supported");
 *         }
 *     }
 * }
 * </code>
 * Below you can see how the caller code looks like:
 * <code>
 * TimeSeries<Double> ts = new TimeSeries<Double>() { }
 * </code>
 * Notice that <code>ts</code> is an anonymous class, denoted by <code>{  }</code>.
 *
 * @note It's important to remember to instantiate an anonymous class in order to avoid <i>type erasure</i>.
 * Doing so, type information will be kept and can be retrieved at runtime.
 *
 * @see <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">Super Type Tokens</a>
 * @see <a href="http://gafter.blogspot.com/2007/05/limitation-of-super-type-tokens.html">A Limitation of Super Type Tokens</a>
 * @see <a href="http://java.sun.com/j2se/1.5/pdf/generics-tutorial.pdf">Generics Tutorial</a>
 * @see <a href="http://www.jquantlib.org/index.php/Using_TypeTokens_to_retrieve_generic_parameters">Using TypeTokens to retrieve generic parameters</a>
 * @see TypeNodeTree
 *
 * @author Richard Gomes
 */
public class TypeNode {

    //
    // private fields
    //

    private final Class<?> element;
    private final AbstractSequentialList<TypeNode> children;


    //
    // public constructors
    //

    public TypeNode(final Class<?> klass) {
        this.element = klass;
        this.children = new LinkedList<TypeNode>();
    }


    //
    // public methods
    //

    /**
     * @return the contents of this TypeNode
     */
    public Class<?> getElement() {
        return element;
    }

    public TypeNode get(final int index) {
        return children.get(index);
    }

    public Iterable<TypeNode> children() {
        return children;
    }


    //
    // package protected methods
    //

    @PackagePrivate TypeNode add(final Class<?> klass) {
        final TypeNode node = new TypeNode(klass);
        children.add(node);
        return node;
    }

    @PackagePrivate TypeNode add(final TypeNode node) {
        children.add(node);
        return node;
    }

}
