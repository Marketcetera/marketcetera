package org.jquantlib.lang.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import org.jquantlib.QL;

/**
 * TypeTokenTree is a helper class intended to return a {@link TypeNode} root.
 * <p>
 * A typical usage consists on obtaining a {@link TypeNode} root which can be used to traverse a hierarchy of generic parameters.
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
 * @see TypeToken
 * @see TypeReference
 * @see TypeNode
 *
 * @see <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">Super Type Tokens</a>
 * @see <a href="http://gafter.blogspot.com/2007/05/limitation-of-super-type-tokens.html">A Limitation of Super Type Tokens</a>
 * @see <a href="http://java.sun.com/j2se/1.5/pdf/generics-tutorial.pdf">Generics Tutorial</a>
 * @see <a href="http://www.jquantlib.org/index.php/Using_TypeTokens_to_retrieve_generic_parameters">Using TypeTokens to retrieve generic parameters</a>
 *
 * @author Richard Gomes
 */
public class TypeTokenTree {

    //
    // private fields
    //

    private final TypeNode root;


    //
    // public constructors
    //

    public TypeTokenTree() {
        this.root = retrieve(getClass());
    }

    public TypeTokenTree(final Class<?> klass) {
        this.root = retrieve(klass);
    }

    /**
     * Returns the Class of a generic parameter
     *
     * @param pos represents the position of parameter, first is zero
     * @return the Class of a generic parameter
     */
    public Class<?> getElement(final int pos) {
        if (root==null) {
            return null;
        }
        final TypeNode typeNode = root.get(pos);
        if (typeNode==null) {
            return null;
        }
        return typeNode.getElement();
    }

    //
    // public methods
    //

    public TypeNode getRoot() {
        return root;
    }


    //
    // private methods
    //

    private TypeNode retrieve(final Class<?> klass) {
        final Type superclass = klass.getGenericSuperclass();
        QL.require(!(superclass instanceof Class) , ReflectConstants.SHOULD_BE_ANONYMOUS_OR_EXTENDED); // QA:[RG]::verified
        final TypeNode node = new TypeNode(klass);
        for (final Type t : ((ParameterizedType) superclass).getActualTypeArguments() ) {
            node.add(retrieve(t));
        }
        return node;
    }

    private TypeNode retrieve(final Type type) {
        final TypeNode node;
        if (type instanceof Class<?>) {
            node = new TypeNode((Class<?>)type);
        } else if (type instanceof ParameterizedType) {
            final Type rawType = ((ParameterizedType) type).getRawType();
            node = retrieve(rawType);
            for (final Type arg : ((ParameterizedType) type).getActualTypeArguments()) {
                node.add(retrieve(arg));
                //
                //TODO: code review
                //
                // More test cases need to be done, specially related to Monte Carlo needs on generic parameters
                //
                //        } else if (type instanceof TypeVariable) {
                //            GenericDeclaration declaration = ((TypeVariable) type).getGenericDeclaration();
                //            node = new TypeNode(declaration);
                //            for (Type arg : ((TypeVariable) type).getBounds()) {
                //                node.add(retrieve(arg));
                //            }
                //
            }
        } else {
            throw new IllegalArgumentException(ReflectConstants.ILLEGAL_TYPE_PARAMETER);
        }
        return node;
    }

}
