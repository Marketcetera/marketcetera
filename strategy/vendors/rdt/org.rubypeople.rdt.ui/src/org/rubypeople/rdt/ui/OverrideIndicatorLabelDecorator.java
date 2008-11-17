/*******************************************************************************
 * Copyright (c) 2000, 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.ui;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.rubypeople.rdt.core.IMethod;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.ITypeHierarchy;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.MethodOverrideTester;
import org.rubypeople.rdt.internal.corext.util.RubyModelUtil;
import org.rubypeople.rdt.internal.corext.util.SuperTypeHierarchyCache;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.ui.viewsupport.ImageDescriptorRegistry;
import org.rubypeople.rdt.ui.viewsupport.ImageImageDescriptor;

/**
 * LabelDecorator that decorates an method's image with override or implements overlays.
 * The viewer using this decorator is responsible for updating the images on element changes.
 * 
 * <p>
 * This class may be instantiated; it is not intended to be subclassed.
 * </p>
 * 
 * @since 2.0
 */
public class OverrideIndicatorLabelDecorator implements ILabelDecorator, ILightweightLabelDecorator {

	private ImageDescriptorRegistry fRegistry;
	private boolean fUseNewRegistry= false;

	/**
	 * Creates a decorator. The decorator creates an own image registry to cache
	 * images. 
	 */
	public OverrideIndicatorLabelDecorator() {
		this(null);
		fUseNewRegistry= true;
	}	

	/*
	 * Creates decorator with a shared image registry.
	 * 
	 * @param registry The registry to use or <code>null</code> to use the Ruby plugin's
	 * image registry.
	 */	
	/**
	 * Note: This constructor is for internal use only. Clients should not call this constructor.
	 * @param registry The registry to use.
	 */
	public OverrideIndicatorLabelDecorator(ImageDescriptorRegistry registry) {
		fRegistry= registry;
	}
	
	private ImageDescriptorRegistry getRegistry() {
		if (fRegistry == null) {
			fRegistry= fUseNewRegistry ? new ImageDescriptorRegistry() : RubyPlugin.getImageDescriptorRegistry();
		}
		return fRegistry;
	}	
	
	
	/* (non-Javadoc)
	 * @see ILabelDecorator#decorateText(String, Object)
	 */
	public String decorateText(String text, Object element) {
		return text;
	}	

	/* (non-Javadoc)
	 * @see ILabelDecorator#decorateImage(Image, Object)
	 */
	public Image decorateImage(Image image, Object element) {
		int adornmentFlags= computeAdornmentFlags(element);
		if (adornmentFlags != 0) {
			ImageDescriptor baseImage= new ImageImageDescriptor(image);
			Rectangle bounds= image.getBounds();
			return getRegistry().get(new RubyElementImageDescriptor(baseImage, adornmentFlags, new Point(bounds.width, bounds.height)));
		}
		return image;
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * @param element The element to decorate
	 * @return Resulting decorations (combination of RubyElementImageDescriptor.IMPLEMENTS
	 * and RubyElementImageDescriptor.OVERRIDES)
	 */
	public int computeAdornmentFlags(Object element) {
		if (element instanceof IMethod) {
			try {
				IMethod method= (IMethod) element;
				if (!method.getRubyProject().isOnLoadpath(method)) {
					return 0;
				}
				if (!method.isConstructor() && method.getVisibility() != IMethod.PRIVATE && !method.isSingleton()) {
					int res= getOverrideIndicators(method);
					return res;
				}
			} catch (RubyModelException e) {
				if (!e.isDoesNotExist()) {
					RubyPlugin.log(e);
				}
			}
		}
		return 0;
	}
	
	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * @param method The element to decorate
	 * @return Resulting decorations (combination of RubyElementImageDescriptor.IMPLEMENTS
	 * and RubyElementImageDescriptor.OVERRIDES)
	 * @throws RubyModelException
	 */
	protected int getOverrideIndicators(IMethod method) throws RubyModelException {
//		Node astRoot= RubyPlugin.getDefault().getASTProvider().getAST((IRubyElement) method.getOpenable(), ASTProvider.WAIT_NO, null);
//		if (astRoot != null) {
//			int res= findInHierarchyWithAST(astRoot, method);
//			if (res != -1) {
//				return res;
//			}
//		}
		
		IType type= method.getDeclaringType();
		if (type == null) {
			// TODO grab Object?
			return 0;
		}
		MethodOverrideTester methodOverrideTester= SuperTypeHierarchyCache.getMethodOverrideTester(type);
		IMethod defining= methodOverrideTester.findOverriddenMethod(method, true);
		if (defining != null) {
			return RubyElementImageDescriptor.OVERRIDES;
 		}
		return 0;
	}
	
//	private int findInHierarchyWithAST(Node astRoot, IMethod method) throws RubyModelException {
//		Node methodNode = ClosestSpanningNodeLocator.Instance().findClosestSpanner(astRoot, method.getNameRange().getOffset(), new INodeAcceptor() {
//		
//			public boolean doesAccept(Node node) {
//				return node instanceof MethodDefNode;
//			}
//		
//		});
//		
//		Node node= NodeFinder.perform(astRoot, method.getNameRange());
//		if (node instanceof SimpleName && node.getParent() instanceof MethodDeclaration) {
//			IMethodBinding binding= ((MethodDeclaration) node.getParent()).resolveBinding();
//			if (binding != null) {
//				IMethodBinding defining= Bindings.findOverriddenMethod(binding, true);
//				if (defining != null) {
//					return RubyElementImageDescriptor.OVERRIDES;
//				}
//				return 0;
//			}
//		}		
//		return -1;
//	}

	/**
	 * Note: This method is for internal use only. Clients should not call this method.
	 * @param type The declaring type of the method to decorate.
	 * @param hierarchy The type hierarchy of the declaring type.
	 * @param name The name of the method to find.
	 * @param paramTypes The parameter types of the method to find.
	 * @return The resulting decoration.
	 * @throws RubyModelException
	 * @deprecated Not used anymore. This method is not accurate for methods in generic types.
	 */
	protected int findInHierarchy(IType type, ITypeHierarchy hierarchy, String name, String[] paramTypes) throws RubyModelException {
		IType superClass= hierarchy.getSuperclass(type);
		if (superClass != null) {
			IMethod res= RubyModelUtil.findMethodInHierarchy(hierarchy, superClass, name, paramTypes, false);
			if (res != null && res.getVisibility() != IMethod.PRIVATE && RubyModelUtil.isVisibleInHierarchy(res, type.getSourceFolder())) {
				return RubyElementImageDescriptor.OVERRIDES;
			}
		}
		IType[] interfaces= hierarchy.getSuperModules(type);
		for (int i= 0; i < interfaces.length; i++) {
			IMethod res= RubyModelUtil.findMethodInHierarchy(hierarchy, interfaces[i], name, paramTypes, false);
			if (res != null) {
				return RubyElementImageDescriptor.OVERRIDES;
			}
		}
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see IBaseLabelProvider#addListener(ILabelProviderListener)
	 */
	public void addListener(ILabelProviderListener listener) {
	}

	/* (non-Javadoc)
	 * @see IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		if (fRegistry != null && fUseNewRegistry) {
			fRegistry.dispose();
		}
	}

	/* (non-Javadoc)
	 * @see IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return true;
	}

	/* (non-Javadoc)
	 * @see IBaseLabelProvider#removeListener(ILabelProviderListener)
	 */
	public void removeListener(ILabelProviderListener listener) {
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.ILightweightLabelDecorator#decorate(java.lang.Object, org.eclipse.jface.viewers.IDecoration)
	 */
	public void decorate(Object element, IDecoration decoration) { 
		int adornmentFlags= computeAdornmentFlags(element);
		if ((adornmentFlags & RubyElementImageDescriptor.IMPLEMENTS) != 0) {
			decoration.addOverlay(RubyPluginImages.DESC_OVR_IMPLEMENTS);
		} else if ((adornmentFlags & RubyElementImageDescriptor.OVERRIDES) != 0) {
			decoration.addOverlay(RubyPluginImages.DESC_OVR_OVERRIDES);
		}
	}

}
