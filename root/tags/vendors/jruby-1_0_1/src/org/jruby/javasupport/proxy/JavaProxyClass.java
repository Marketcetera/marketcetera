/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Kresten Krab Thorup <krab@gnu.org>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.jruby.javasupport.proxy;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jruby.Ruby;
import org.jruby.RubyArray;
import org.jruby.RubyClass;
import org.jruby.RubyFixnum;
import org.jruby.RubyModule;
import org.jruby.RubyObject;
import org.jruby.RubyNil;
import org.jruby.RubyString;
import org.jruby.exceptions.RaiseException;
import org.jruby.javasupport.JavaClass;
import org.jruby.javasupport.JavaObject;
import org.jruby.javasupport.JavaUtil;
import org.jruby.runtime.CallbackFactory;
import org.jruby.runtime.ObjectAllocator;
import org.jruby.runtime.builtin.IRubyObject;

/**
 * Generalized proxy for classes and interfaces.
 * 
 * API looks a lot like java.lang.reflect.Proxy, except that you can specify a
 * super class in addition to a set of interfaces.
 * 
 * The main implication for users of this class is to handle the case where a
 * proxy method overrides an existing method, because in this case the
 * invocation handler should "default" to calling the super implementation
 * {JavaProxyMethod.invokeSuper}.
 * 
 * 
 * @author krab@trifork.com
 * @see java.lang.reflect.Proxy
 * 
 */
public class JavaProxyClass extends JavaProxyReflectionObject {
    static ThreadLocal runtimeTLS = new ThreadLocal();
    private final Class proxyClass;
    private ArrayList methods = new ArrayList();
    private HashMap methodMap = new HashMap();

    /* package scope */
    JavaProxyClass(Class proxyClass) {
        super(getThreadLocalRuntime(), 
                (RubyClass) getThreadLocalRuntime().getModule("Java").getClass("JavaProxyClass"));
        
        this.proxyClass = proxyClass;
    }

    public Object getValue() {
        return this;
    }

    private static Ruby getThreadLocalRuntime() {
        return (Ruby) runtimeTLS.get();
    }

    public static JavaProxyClass getProxyClass(Ruby runtime, Class superClass,
            Class[] interfaces, Set names) throws InvocationTargetException {
        Object save = runtimeTLS.get();
        runtimeTLS.set(runtime);
        try {
            ClassLoader loader = runtime.getJavaSupport().getJavaClassLoader();

            return JavaProxyClassFactory.newProxyClass(loader, null, superClass, interfaces, names);
        } finally {
            runtimeTLS.set(save);
        }
    }

    public static JavaProxyClass getProxyClass(Ruby runtime, Class superClass,
            Class[] interfaces) throws InvocationTargetException {
        return getProxyClass(runtime,superClass,interfaces,null);
    }
    
    public static Object newProxyInstance(Ruby runtime, Class superClass, Class[] interfaces, 
            Class[] constructorParameters, Object[] constructorArgs, 
            JavaProxyInvocationHandler handler) throws IllegalArgumentException, 
            InstantiationException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {
        JavaProxyClass jpc = getProxyClass(runtime, superClass, interfaces);
        JavaProxyConstructor cons = jpc.getConstructor(constructorParameters == null ? 
                new Class[0] : constructorParameters);
        
        return cons.newInstance(constructorArgs, handler);

    }

    public Class getSuperclass() {
        return proxyClass.getSuperclass();
    }

    public Class[] getInterfaces() {
        Class[] ifaces = proxyClass.getInterfaces();
        Class[] result = new Class[ifaces.length - 1];
        int pos = 0;
        for (int i = 0; i < ifaces.length; i++) {
            if (ifaces[i] != InternalJavaProxy.class) {
                result[pos++] = ifaces[i];
            }
        }
        return result;
    }

    public JavaProxyConstructor[] getConstructors() {
        Constructor[] cons = proxyClass.getConstructors();
        JavaProxyConstructor[] result = new JavaProxyConstructor[cons.length];
        for (int i = 0; i < cons.length; i++) {
            result[i] = new JavaProxyConstructor(getRuntime(), this, cons[i]);
        }
        return result;
    }

    public JavaProxyConstructor getConstructor(Class[] args)
            throws SecurityException, NoSuchMethodException {

        Class[] realArgs = new Class[args.length + 1];
        System.arraycopy(args, 0, realArgs, 0, args.length);
        realArgs[args.length] = JavaProxyInvocationHandler.class;

        Constructor constructor = proxyClass.getConstructor(realArgs);
        return new JavaProxyConstructor(getRuntime(), this, constructor);
    }

    public JavaProxyMethod[] getMethods() {
        return (JavaProxyMethod[]) methods.toArray(new JavaProxyMethod[methods.size()]);
    }

    public JavaProxyMethod getMethod(String name, Class[] parameterTypes) {
        List methods = (List)methodMap.get(name);
        if (methods != null) {
            for (int i = methods.size(); --i >= 0; ) {
                ProxyMethodImpl jpm = (ProxyMethodImpl) methods.get(i);
                if (jpm.matches(name, parameterTypes)) return jpm;
            }
        }
        return null;
    }

    /** return the class of instances of this proxy class */
    Class getProxyClass() {
        return proxyClass;
    }

    public static class ProxyMethodImpl extends JavaProxyReflectionObject
            implements JavaProxyMethod {
        private final Method m;

        private Object state;

        private final Method sm;
        private final Class[] parameterTypes;

        private final JavaProxyClass clazz;

        public ProxyMethodImpl(Ruby runtime, JavaProxyClass clazz, Method m,
                Method sm) {
            super(runtime, runtime.getModule("Java")
                    .getClass("JavaProxyMethod"));
            this.m = m;
            this.parameterTypes = m.getParameterTypes();
            this.sm = sm;
            this.clazz = clazz;
        }

        public Method getMethod() {
            return m;
        }

        public Method getSuperMethod() {
            return sm;
        }

        public int getModifiers() {
            return m.getModifiers();
        }

        public String getName() {
            return m.getName();
        }

        public Class[] getExceptionTypes() {
            return m.getExceptionTypes();
        }

        public Class[] getParameterTypes() {
            return parameterTypes;
        }

        public Object getState() {
            return state;
        }

        public boolean hasSuperImplementation() {
            return sm != null;
        }

        public Object invoke(Object proxy, Object[] args) throws IllegalArgumentException, 
            IllegalAccessException, InvocationTargetException, NoSuchMethodException {
            
            if (!hasSuperImplementation()) throw new NoSuchMethodException();

            return sm.invoke(proxy, args);
        }

        public void setState(Object state) {
            this.state = state;
        }

        public String toString() {
            return m.toString();
        }

        public Object defaultResult() {
            Class rt = m.getReturnType();
            
            if (rt == Void.TYPE) return null;
            if (rt == Boolean.TYPE) return Boolean.FALSE;
            if (rt == Byte.TYPE) return new Byte((byte) 0);
            if (rt == Short.TYPE) return new Short((short) 0);
            if (rt == Integer.TYPE) return new Integer(0);
            if (rt == Long.TYPE) return new Long(0L);
            if (rt == Float.TYPE) return new Float(0.0f);
            if (rt == Double.TYPE) return new Double(0.0);

            return null;
        }

        public boolean matches(String name, Class[] parameterTypes) {
            return m.getName().equals(name) && Arrays.equals(this.parameterTypes, parameterTypes);
        }

        public Class getReturnType() {
            return m.getReturnType();
        }
        
        public static RubyClass createJavaProxyMethodClass(Ruby runtime, RubyModule javaProxyModule) {
            RubyClass result = javaProxyModule.defineClassUnder("JavaProxyMethod", 
                    runtime.getObject(), ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);

            CallbackFactory callbackFactory = 
                runtime.callbackFactory(JavaProxyClass.ProxyMethodImpl.class);

            JavaProxyReflectionObject.registerRubyMethods(runtime, result);

            result.defineFastMethod("argument_types", callbackFactory.getFastMethod("argument_types"));
            result.defineFastMethod("declaring_class", callbackFactory.getFastMethod("getDeclaringClass"));
            result.defineFastMethod("super?", callbackFactory.getFastMethod("super_p"));
            result.defineFastMethod("arity", callbackFactory.getFastMethod("arity"));
            result.defineFastMethod("name", callbackFactory.getFastMethod("name"));
            result.defineFastMethod("inspect", callbackFactory.getFastMethod("inspect"));
            result.defineFastMethod("invoke", callbackFactory.getFastOptMethod("do_invoke"));

            return result;
        }

        public RubyObject name() {
            return getRuntime().newString(getName());
        }

        public JavaProxyClass getDeclaringClass() {
            return clazz;
        }

        public RubyArray argument_types() {
            return buildRubyArray(getParameterTypes());
        }

        public IRubyObject super_p() {
            return hasSuperImplementation() ? getRuntime().getTrue() : getRuntime().getFalse();
        }

        public RubyFixnum arity() {
            return getRuntime().newFixnum(getArity());
        }

        protected String nameOnInspection() {
            return getDeclaringClass().nameOnInspection() + "/" + getName();
        }

        public IRubyObject inspect() {
            StringBuffer result = new StringBuffer();
            result.append(nameOnInspection());
            result.append("(");
            Class[] parameterTypes = getParameterTypes();
            for (int i = 0; i < parameterTypes.length; i++) {
                result.append(parameterTypes[i].getName());
                if (i < parameterTypes.length - 1) {
                    result.append(',');
                }
            }
            result.append(")>");
            return getRuntime().newString(result.toString());
        }

        public IRubyObject do_invoke(IRubyObject[] nargs) {
            if (nargs.length != 1 + getArity()) {
                throw getRuntime().newArgumentError(nargs.length, 1 + getArity());
            }

            IRubyObject invokee = nargs[0];
            if (!(invokee instanceof JavaObject)) {
                throw getRuntime().newTypeError("invokee not a java object");
            }
            Object receiver_value = ((JavaObject) invokee).getValue();
            Object[] arguments = new Object[nargs.length - 1];
            System.arraycopy(nargs, 1, arguments, 0, arguments.length);

            Class[] parameterTypes = getParameterTypes();
            for (int i = 0; i < arguments.length; i++) {
                arguments[i] = 
                    JavaUtil.convertRubyToJava((IRubyObject) arguments[i], parameterTypes[i]);
            }

            try {
                Object javaResult = sm.invoke(receiver_value, arguments);
                return JavaUtil.convertJavaToRuby(getRuntime(), javaResult, getReturnType());
            } catch (IllegalArgumentException e) {
                throw getRuntime().newTypeError("expected " + argument_types().inspect());
            } catch (IllegalAccessException iae) {
                throw getRuntime().newTypeError("illegal access on '" + sm.getName() + "': " + 
                        iae.getMessage());
            } catch (InvocationTargetException ite) {
                ite.getTargetException().printStackTrace();
                getRuntime().getJavaSupport().handleNativeException(ite.getTargetException());
                // This point is only reached if there was an exception handler
                // installed.
                return getRuntime().getNil();
            }
        }

        private int getArity() {
            return getParameterTypes().length;
        }

    }

    JavaProxyMethod initMethod(String name, String desc, boolean hasSuper) {
        Class proxy = proxyClass;
        try {
            Class[] parms = parse(proxy.getClassLoader(), desc);
            Method m = proxy.getDeclaredMethod(name, parms);
            Method sm = null;
            if (hasSuper) {
                sm = proxy.getDeclaredMethod("__super$" + name, parms);
            }

            JavaProxyMethod jpm = new ProxyMethodImpl(getRuntime(), this, m, sm);
            methods.add(jpm);
            List methodsWithName = (List)methodMap.get(name);
            if (methodsWithName == null) {
                methodsWithName = new ArrayList(2);
                methodMap.put(name,methodsWithName);
            }
            methodsWithName.add(jpm);
            
            return jpm;
        } catch (ClassNotFoundException e) {
            throw new InternalError(e.getMessage());
        } catch (SecurityException e) {
            throw new InternalError(e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.getMessage());
        }
    }

    private static Class[] parse(final ClassLoader loader, String desc)
            throws ClassNotFoundException {
        List al = new ArrayList();
        int idx = 1;
        while (desc.charAt(idx) != ')') {

            int arr = 0;
            while (desc.charAt(idx) == '[') {
                idx += 1;
                arr += 1;
            }

            Class type;

            switch (desc.charAt(idx)) {
            case 'L':
                int semi = desc.indexOf(';', idx);
                final String name = desc.substring(idx + 1, semi);
                idx = semi;
                try {
                    type = (Class) AccessController
                            .doPrivileged(new PrivilegedExceptionAction() {
                                public Object run()
                                        throws ClassNotFoundException {
                                    return Class.forName(
                                            name.replace('/', '.'), false,
                                            loader);
                                }
                            });
                } catch (PrivilegedActionException e) {
                    throw (ClassNotFoundException) e.getException();
                }
                break;

            case 'B': type = Byte.TYPE; break;
            case 'C': type = Character.TYPE; break;
            case 'Z': type = Boolean.TYPE; break;
            case 'S': type = Short.TYPE; break;
            case 'I': type = Integer.TYPE; break;
            case 'J': type = Long.TYPE; break;
            case 'F': type = Float.TYPE; break;
            case 'D': type = Double.TYPE; break;
            default:
                throw new InternalError("cannot parse " + desc + "[" + idx + "]");
            }

            idx += 1;

            if (arr != 0) {
                type = Array.newInstance(type, new int[arr]).getClass();
            }

            al.add(type);
        }

        return (Class[]) al.toArray(new Class[al.size()]);
    }

    //
    // Ruby-level methods
    //
        
    public static RubyClass createJavaProxyClassClass(Ruby runtime, RubyModule javaModule) {
        RubyClass result = javaModule.defineClassUnder("JavaProxyClass",
                runtime.getObject(),ObjectAllocator.NOT_ALLOCATABLE_ALLOCATOR);
        CallbackFactory callbackFactory = runtime.callbackFactory(JavaProxyClass.class);

        JavaProxyReflectionObject.registerRubyMethods(runtime, result);

        result.defineFastMethod("constructors", callbackFactory.getFastMethod("constructors"));
        result.defineFastMethod("superclass", callbackFactory.getFastMethod("superclass"));
        result.defineFastMethod("interfaces", callbackFactory.getFastMethod("interfaces"));
        result.defineFastMethod("methods", callbackFactory.getFastMethod("methods"));

        result.getMetaClass().defineFastMethod("get", 
                callbackFactory.getFastSingletonMethod("get", JavaClass.class));
        result.getMetaClass().defineFastMethod("get_with_class", 
                callbackFactory.getFastSingletonMethod("get_with_class", RubyClass.class));

        return result;
    }

    public static RubyObject get(IRubyObject recv, JavaClass type) {
        try {
            return getProxyClass(recv.getRuntime(), (Class) type.getValue(), new Class[0]);
        } catch (Error e) {
            RaiseException ex = recv.getRuntime().newArgumentError("unable to create proxy class for " + type.getValue());
            ex.initCause(e);
            throw ex;
        } catch (InvocationTargetException e) {
            RaiseException ex = recv.getRuntime().newArgumentError("unable to create proxy class for " + type.getValue());
            ex.initCause(e);
            throw ex;
        }
    }
    
    private static final HashSet EXCLUDE_MODULES = new HashSet();
    static {
        EXCLUDE_MODULES.add("Kernel");
        EXCLUDE_MODULES.add("Java");
        EXCLUDE_MODULES.add("JavaProxyMethods");
        EXCLUDE_MODULES.add("Enumerable");
    }

    private static final HashSet EXCLUDE_METHODS = new HashSet();
    static {
        EXCLUDE_METHODS.add("class");
        EXCLUDE_METHODS.add("finalize");
        EXCLUDE_METHODS.add("initialize");
        EXCLUDE_METHODS.add("java_class");
        EXCLUDE_METHODS.add("java_object");
        EXCLUDE_METHODS.add("__jcreate!");
        EXCLUDE_METHODS.add("__jsend!");
    }

    public static RubyObject get_with_class(IRubyObject recv, RubyClass clazz) {
        Ruby runtime = recv.getRuntime();
        
        // Let's only generate methods for those the user may actually 
        // intend to override.  That includes any defined in the current
        // class, and any ancestors that are also JavaProxyClasses (but none
        // from any other ancestor classes). Methods defined in mixins will
        // be considered intentionally overridden, except those from Kernel,
        // Java, and JavaProxyMethods, as well as Enumerable. 
        // TODO: may want to exclude other common mixins?

        JavaClass javaClass = null;
        Set names = new HashSet(); // need names ordered for key generation later
        List interfaceList = new ArrayList();

        List ancestors = clazz.getAncestorList();
        boolean skipRemainingClasses = false;
        for (Iterator iter = ancestors.iterator(); iter.hasNext(); ) {
            RubyModule ancestor = (RubyModule)iter.next();
            if (ancestor instanceof RubyClass) {
                if (skipRemainingClasses) continue;
                Map vars = ancestor.getInstanceVariables();
                // we only collect methods and interfaces for 
                // user-defined proxy classes.
                if (!vars.containsKey("@java_proxy_class")) {
                    skipRemainingClasses = true;
                    continue;
                }

                // get JavaClass if this is the new proxy class; verify it
                // matches if this is a superclass proxy.
                IRubyObject var = (IRubyObject)vars.get("@java_class");
                if (var == null) {
                    throw runtime.newTypeError(
                            "no java_class defined for proxy (or ancestor): " + ancestor);
                } else if (!(var instanceof JavaClass)) {
                    throw runtime.newTypeError(
                            "invalid java_class defined for proxy (or ancestor): " +
                            ancestor + ": " + var);
                }
                if (javaClass == null) {
                    javaClass = (JavaClass)var;
                } else if (javaClass != var) {
                    throw runtime.newTypeError(
                            "java_class defined for " + clazz + " (" + javaClass +
                            ") does not match java_class for ancestor " + ancestor +
                            " (" + var + ")");
                }
                // get any included interfaces
                var = (IRubyObject)vars.get("@java_interfaces");
                if (var != null && !(var instanceof RubyNil)) {
                    if (!(var instanceof RubyArray)) {
                        throw runtime.newTypeError(
                                "invalid java_interfaces defined for proxy (or ancestor): " +
                                ancestor + ": " + var);
                    }
                    RubyArray ifcArray = (RubyArray)var;
                    int size = ifcArray.size();
                    for (int i = size; --i >= 0; ) {
                        IRubyObject ifc = ifcArray.eltInternal(i);
                        if (!(ifc instanceof JavaClass)) {
                            throw runtime.newTypeError(
                                "invalid java interface defined for proxy (or ancestor): " +
                                ancestor + ": " + ifc);
                        }
                        Class interfaceClass = ((JavaClass)ifc).javaClass();
                        if (!interfaceClass.isInterface()) {
                            throw runtime.newTypeError(
                                    "invalid java interface defined for proxy (or ancestor): " +
                                    ancestor + ": " + ifc + " (not an interface)");
                        }
                        if (!interfaceList.contains(interfaceClass)) {
                            interfaceList.add(interfaceClass);
                        }
                    }
                }
                // set this class's method names in var @__java_ovrd_methods if this
                // is the new class; otherwise, get method names from there if this is
                // a proxy superclass.
                var = (IRubyObject)vars.get("@__java_ovrd_methods");
                if (var == null) {
                    // lock in the overridden methods for the new class, and any as-yet
                    // uninstantiated ancestor class.
                    Map methods;
                    RubyArray methodNames;
                    synchronized(methods = ancestor.getMethods()) {
                        methodNames = RubyArray.newArrayLight(runtime,methods.size());
                        for (Iterator meths = methods.keySet().iterator(); meths.hasNext(); ) {
                            String methodName = (String)meths.next();
                            if (!EXCLUDE_METHODS.contains(methodName)) {
                                names.add(methodName);
                                methodNames.add(runtime.newString(methodName));
                            }
                        }
                    }
                    // TODO: OK to just do a put here?
                    ancestor.setInstanceVariable("@__java_ovrd_methods",methodNames);
                } else {
                    if (!(var instanceof RubyArray)) {
                        throw runtime.newTypeError(
                                "invalid @__java_ovrd_methods defined for proxy: " +
                                ancestor + ": " + var);
                    }
                    RubyArray methodNames = (RubyArray)var;
                    int size = methodNames.size();
                    for (int i = size; --i >= 0; ) {
                        IRubyObject methodName = methodNames.eltInternal(i);
                        if (!(methodName instanceof RubyString)) {
                            throw runtime.newTypeError(
                                    "invalid method name defined for proxy (or ancestor): " +
                                    ancestor + ": " + methodName);
                        }
                        names.add(methodName.asSymbol());
                    }
                }
            } else if (!EXCLUDE_MODULES.contains(ancestor.getName())) {
                Map methods;
                synchronized(methods = ancestor.getMethods()) {
                    for (Iterator meths = methods.keySet().iterator(); meths.hasNext(); ) {
                        String methodName = (String)meths.next();
                        if (!EXCLUDE_METHODS.contains(methodName)) {
                            names.add(methodName);
                        }
                    }
                }
            }
        }

        if (javaClass == null) {
            throw runtime.newArgumentError("unable to create proxy class: no java_class defined for " + clazz);
        }
        
        int interfaceCount = interfaceList.size();
        Class[] interfaces = new Class[interfaceCount];
        for (int i = interfaceCount; --i >= 0; ) {
            interfaces[i] = (Class)interfaceList.get(i);
        }
       
        try {
            return getProxyClass(recv.getRuntime(), javaClass.javaClass(), interfaces, names);
        } catch (Error e) {
            RaiseException ex = recv.getRuntime().newArgumentError("unable to create proxy class for " + javaClass.getValue() + " : " + e.getMessage());
            //e.printStackTrace();
            ex.initCause(e);
            throw ex;
        } catch (InvocationTargetException e) {
            RaiseException ex = recv.getRuntime().newArgumentError("unable to create proxy class for " + javaClass.getValue() + " : " + e.getMessage());
            //e.printStackTrace();
            ex.initCause(e);
            throw ex;
        }
    }

    public RubyObject superclass() {
        return JavaClass.get(getRuntime(), getSuperclass());
    }

    public RubyArray methods() {
        return buildRubyArray(getMethods());
    }

    public RubyArray interfaces() {
        return buildRubyArray(getInterfaces());
    }

    public RubyArray constructors() {
        return buildRubyArray(getConstructors());
    }

    public static void createJavaProxyModule(Ruby runtime) {
        // TODO Auto-generated method stub

        RubyModule javaProxyModule = runtime.getModule("Java");
        JavaProxyClass.createJavaProxyClassClass(runtime, javaProxyModule);
        ProxyMethodImpl.createJavaProxyMethodClass(runtime, javaProxyModule);
        JavaProxyConstructor.createJavaProxyConstructorClass(runtime, javaProxyModule);
    }

    public String nameOnInspection() {
        return "[Proxy:" + getSuperclass().getName() + "]";
    }
}
