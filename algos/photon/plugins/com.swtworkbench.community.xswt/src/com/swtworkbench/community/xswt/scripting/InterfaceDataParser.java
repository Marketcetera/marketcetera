package com.swtworkbench.community.xswt.scripting;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.omg.CORBA.portable.InvokeHandler;

import com.swtworkbench.community.xswt.XSWTException;
import com.swtworkbench.community.xswt.dataparser.IDataParserContext;
import com.swtworkbench.community.xswt.dataparser.NonDisposableDataParser;

public class InterfaceDataParser extends NonDisposableDataParser {

	private static class FunctionProxy implements InvocationHandler {

		protected Function fun;
		
		public FunctionProxy(Function fun) {
			this.fun = fun;
		}

		protected Object invoke(Method method, Object[] args) throws Throwable {
			if (fun.arity() != args.length) {
				throw new IllegalArgumentException(fun.getName() + " requires " + fun.arity() + " arguments but received " + args.length);
			}
			for (int i = 0; i < args.length; i++) {
				if (args[i] != null && (! fun.argumentType(i).isInstance(args[i]))) {
					throw new IllegalArgumentException(fun.getName() + "'s " + i + ". argument must be of " + fun.argumentType(i) + " but was " + args[i] + " of " + args[i].getClass());
				}
			}
			return fun.invoke(args);
		}
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			return invoke(method, args);
		}
	}
	
	private static class BindingsProxy extends FunctionProxy {
		
		private Bindings bindings;
		
		public BindingsProxy(Bindings bindings) {
			super(null);
			this.bindings = bindings;
		}
		
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			Object o = bindings.get(method.getName());
			if (o == null) {
				return null;
			}
			if (! (o instanceof Function)) {
				throw new IllegalArgumentException(o + " is not a Function");
			}
			fun = (Function)o;
			return invoke(method, args);
		}
	}
	
	private ClassLoader cl;
	
	public void setClassLoader(ClassLoader cl) {
		this.cl = cl;
	}
	
	public Object parse(String source, Class klass, IDataParserContext context) throws XSWTException {
		InvocationHandler handler = null;
		Bindings bindings = null;
		try {
			bindings = (Bindings)context.parse(source, Bindings.class);
		} catch (XSWTException e) {
		}
		if (bindings != null) {
			handler = new BindingsProxy(bindings);
		} else {
			Function fun = null;
			try {
				fun = (Function)context.parse(source, Function.class);
			} catch (XSWTException e) {
			}
			if (fun != null) {
				handler = new FunctionProxy(fun);
			}
		}
		if (handler == null) {
			return null;
		}
		ClassLoader cl = this.cl != null ? this.cl : getClass().getClassLoader();
		return Proxy.newProxyInstance(cl, new Class[]{klass}, handler);
	}
}
