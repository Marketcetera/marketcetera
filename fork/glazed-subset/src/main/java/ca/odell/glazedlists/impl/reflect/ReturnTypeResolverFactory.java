/* Glazed Lists                                                 (c) 2003-2006 */
/* http://publicobject.com/glazedlists/                      publicobject.com,*/
/*                                                     O'Dell Engineering Ltd.*/
package ca.odell.glazedlists.impl.reflect;

public interface ReturnTypeResolverFactory {

    /** The ReturnTypeResolver factory for this JVM. */
    public static final ReturnTypeResolverFactory DEFAULT = new DelegateReturnTypeResolverFactory();

    /**
     * Create a {@link ca.odell.glazedlists.impl.reflect.ReturnTypeResolver}.
     */
    public ReturnTypeResolver createReturnTypeResolver();
}

