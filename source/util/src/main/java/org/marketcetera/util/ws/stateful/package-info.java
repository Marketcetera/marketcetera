/**
 * <p>Web services infrastructure.</p>
 *
 *
 * <h3>Usage</h3>
 *
 * <p>See the test classes of
 * the <code>org.marketcetera.util.ws.sample</code> package.</p>
 *
 *
 * <h3>Limitations/Known issues</h3>
 *
 * <p>Below are limitations/known issues with the current implementation
 * of web services. All limitations are due to the underlying third-party
 * libraries used to implement web services (Apache CXF and JAXB).</p>
 *
 * <ul>
 *
 * <li><p>Every class that needs to be marshalled must have an empty
 * constructor.  Any classes marshalled by an exception (e.g. as
 * properties of an exception) must have a <i>public</i> empty
 * constructor; for all other classes, that empty constructor can be
 * private. For custom exceptions (custom replacements
 * of <code>RemoteException</code>), a
 * single-argument <code>String</code> constructor also works, and is
 * supplied the fault message during unmarshalling. Property getters and
 * setters must be public</p></li>
 *
 * <li><p>A class <code>C</code> that needs to be marshalled and that has
 * a type parameter <code>T</code> may not have <code>T[]</code>
 * properties. Also, in certain instances, the marshaller can get
 * confused when such classes are used in the context of arrays
 * (e.g. use <code>C[]</code> instead of <code>C&lt;?&gt;[]</code>
 * or <code>C&lt;Foo&gt;[]</code>) or nested generics
 * (e.g. use <code>Bar&lt;C&gt;[]</code> instead
 * of <code>Bar&lt;C&lt;?&gt;&gt;[]</code>
 * or <code>Bar&lt;C&lt;Foo&gt;&gt;[]</code>).</p></li>
 *
 * <li><p>Generally, be cautious about generics because the actual type
 * to which <code>T</code> is bound is not available at run-time. For
 * example, assume <code>C</code> has a property of type <code>T</code>,
 * and <code>T</code> has a lower bound of,
 * say, <code>Map&lt;?,?&gt;</code>. You can write code that
 * binds <code>T</code> to a <code>TreeMap&lt;?,?&gt;</code>. However, as
 * discussed below, the marshaller will always send across the wire a
 * tree map as a hash map, and will only create a tree map again if
 * the <i>runtime</i> introspection of <code>C</code> tells the
 * marshaller that the property must be a tree map. But the runtime
 * introspection will yield a <code>Map&lt;?,?&gt;</code> (the lower
 * bound), and so you will get a hash map assigned to a variable which
 * will now execute in the context of code that expects a tree
 * map.</p></li>
 *
 * <li><p>Extending the previous point, consider this pitfall as well: a
 * service has an argument of type <code>A</code>&lt;<code>B</code>&gt;
 * declared as <code>A&lt;T extends BaseT&gt;</code> (and thus
 * <code>B</code> is a descendant of <code>BaseT</code>), then
 * JAXB will not create a support class for <code>B</code>
 * because run-time reflection of <code>A</code> will trigger the
 * creation of a <code>BaseT</code> support class, not a
 * <code>B</code> one. Hence marshalling will encounter problems unless
 * there is some method (or property) anywhere within the same service
 * interface that makes direct use of <code>B</code> which thereby forces
 * JAXB to create a support class for <code>B</code> (this can be
 * accomplished using the <code>@XmlSeeAlso</code> annotation,
 * instead). Note that such errors will occur only at run-time and when
 * the argument above is operated upon in such a manner
 * that <code>B</code> comes into play.</p></li>
 *
 * <li><p><code>@XmlTransient</code>, unlike regular annotations, is
 * inherited. This means that a parent class which has annotated a
 * property as transient will prevent a child class from marshalling a
 * property by the same name.</p></li>
 *
 * <li><p>A class that needs to be marshalled may not have a property
 * whose type is a non-static inner class.</p></li>
 *
 * <li><p>If the marshaller marshals an object of type <code>B</code>
 * (e.g. a class has a property of type <code>B</code>), then the
 * unmarshalled object will <i>always</i> be of type <code>B</code>, even
 * if the actual runtime type of the object originally supplied to the
 * marshaller is of a class derived from <code>B</code>. To enable
 * support of inherited classes, you need to add some method (or
 * property) anywhere within the same service interface that makes direct
 * use of each inherited class (or use the <code>@XmlSeeAlso</code>
 * annotation).</p></li>
 *
 * <li><p>The previous point also applies to exceptions thrown by service
 * methods. However, for exceptions, it is possible to add multiple
 * classes in the throws clause of a method, incl. classes which may be
 * related by super/subclass relationships. In that case, the actual
 * exception class will be unmarshalled if it is one of the classes in
 * the clause. Unlike regular objects, adding an exception
 * subclass <code>D</code> (extending <code>B</code>) in a
 * <i>different</i> method of the same service interface (or using
 * the <code>@XmlSeeAlso</code> annotation) will not enable marshalling
 * of a <code>D</code> exception as a <code>D</code> within a method
 * whose throws clause lists only <code>B</code> explicitly.</p></li>
 *
 * <li><p>Character objects (meaning <code>Character</code>,
 * not <code>char</code> primitives) are sometimes treated as integers,
 * and hence are not marshalled properly; strings (provided they do not
 * contain certain illegal characters like \u0000) and <code>char</code>
 * primitives (of all values) are properly marshalled. Specifically,
 * Character objects as method arguments/results have problems when used
 * in maps.</p></li>
 *
 * <li><p>Date objects are sometimes treated as calendars, and hence
 * are not marshalled properly. Specifically, Date objects as method
 * arguments/results have problems when used in maps. Worse, even
 * outside maps, JAXB does not marshall dates correctly in certain
 * time zones, including GMT: it adds an hour to the marshalled
 * date. Hence, wrap Date objects by a <code>DateWrapper</code> which
 * remedies the above shortcomings.</p></li>
 *
 * <li><p>Collections and maps undergo special handling during
 * marshalling. When a set needs to get marshalled, it is always
 * converted to a <code>HashSet</code>; a collection to
 * an <code>ArrayList</code>; and a map to a <code>HashMap</code>. During
 * unmarshalling, if the desired type is the generic collection/set/map
 * interface, there is no need for further processing. However, if the
 * desired type is, say, a tree set, then an empty tree set is created,
 * and then stuffed with the elements of the
 * transferred <code>HashSet</code>. A drawback of this process is that
 * it is not possible to transfer sorted collections/maps of elements
 * that do not implement <code>Comparable</code> because the comparator
 * itself is not marshalled.</p></li>
 *
 * <li><p>Maps (the <code>Map</code> interface or <code>HashMap</code>
 * or <code>TreeMap</code>) cannot be used as method arguments or results;
 * but they can be used as properties. To pass a map as a method
 * argument/result, use <code>MapWrapper</code>.</p></li>
 *
 * </ul>
 *
 * @author tlerios@marketcetera.com
 * @author anshul@marketcetera.com
 * @since 1.0.0
 * @version $Id$
 */

/* $License$ */

@XmlSchema(namespace="http://marketcetera.org/types/util")
package org.marketcetera.util.ws.stateful;

import javax.xml.bind.annotation.XmlSchema;
