/*******************************************************************************
 * Copyright (c) 2000, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Common Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/cpl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *******************************************************************************/
package org.rubypeople.rdt.internal.core;

import java.text.NumberFormat;
import java.util.HashMap;
import java.util.Map;

import org.rubypeople.rdt.core.IRubyElement;

/**
 * The cache of java elements to their respective info.
 */
public class RubyModelCache {

    public static final int CACHE_RATIO = 20;

    /**
     * Active Ruby Model Info
     */
    protected RubyModelInfo modelInfo;

    /**
     * Cache of open projects.
     */
    protected HashMap projectCache;
    
	/**
	 * Cache of open source folders
	 */
	protected ElementCache folderCache;
	
	/**
	 * Cache of open source folder roots.
	 */
	protected ElementCache rootCache;

	/**
	 * Cache of open ruby script files
	 */
	protected ElementCache openableCache;

    /**
     * Cache of open children of openable Ruby Model Ruby elements
     */
    protected Map childrenCache;
    
	public static final int DEFAULT_PROJECT_SIZE = 5;  // average 25552 bytes per project.
	public static final int DEFAULT_ROOT_SIZE = 50; // average 2590 bytes per root -> maximum size : 25900*BASE_VALUE bytes
	public static final int DEFAULT_FOLDER_SIZE = 500; // average 1782 bytes per pkg -> maximum size : 178200*BASE_VALUE bytes
	public static final int DEFAULT_OPENABLE_SIZE = 500; // average 6629 bytes per openable (includes children) -> maximum size : 662900*BASE_VALUE bytes
	public static final int DEFAULT_CHILDREN_SIZE = 500*20; // average 20 children per openable
	
    /*
	 * The memory ratio that should be applied to the above constants.
	 */
	protected double memoryRatio = -1;

    public RubyModelCache() {
//    	 set the size of the caches in function of the maximum amount of memory available
    	double ratio = getMemoryRatio();
    	this.rootCache = new ElementCache((int) (DEFAULT_ROOT_SIZE * ratio));
    	this.projectCache = new HashMap(DEFAULT_PROJECT_SIZE); // NB: Don't use a LRUCache for projects as they are constantly reopened (e.g. during delta processing)
        this.openableCache = new ElementCache((int) (DEFAULT_OPENABLE_SIZE * ratio));
        this.folderCache = new ElementCache((int) (DEFAULT_FOLDER_SIZE * ratio));
        this.childrenCache = new HashMap((int) (DEFAULT_CHILDREN_SIZE * ratio));        
    }
    
    protected double getMemoryRatio() {
    	if (this.memoryRatio == -1) {
    		long maxMemory = Runtime.getRuntime().maxMemory();		
    		// if max memory is infinite, set the ratio to 4d which corresponds to the 256MB that Eclipse defaults to
    		// (see https://bugs.eclipse.org/bugs/show_bug.cgi?id=111299)
    		this.memoryRatio = maxMemory == Long.MAX_VALUE ? 4d : ((double) maxMemory) / (64 * 0x100000); // 64MB is the base memory for most JVM	
    	}
    	return this.memoryRatio;
    }

    /**
     * Returns the info for the element.
     */
    public Object getInfo(IRubyElement element) {
        switch (element.getElementType()) {
        case IRubyElement.RUBY_MODEL:
            return this.modelInfo;
        case IRubyElement.RUBY_PROJECT:
            return this.projectCache.get(element);
        case IRubyElement.SOURCE_FOLDER_ROOT:
			return this.rootCache.get(element);
		case IRubyElement.SOURCE_FOLDER:
			return this.folderCache.get(element);
        case IRubyElement.SCRIPT:
            return this.openableCache.get(element);
        default:
            return this.childrenCache.get(element);
        }
    }

    /**
     * Returns the info for this element without disturbing the cache ordering.
     */
    protected Object peekAtInfo(IRubyElement element) {
        switch (element.getElementType()) {
        case IRubyElement.RUBY_MODEL:
            return this.modelInfo;
        case IRubyElement.RUBY_PROJECT:
            return this.projectCache.get(element);
        case IRubyElement.SOURCE_FOLDER_ROOT:
			return this.rootCache.peek(element);
        case IRubyElement.SOURCE_FOLDER:
			return this.folderCache.peek(element);
        case IRubyElement.SCRIPT:
            return this.openableCache.peek(element);
        default:
            return this.childrenCache.get(element);
        }
    }

    /**
     * Remember the info for the element.
     */
    protected void putInfo(IRubyElement element, Object info) {
    	switch (element.getElementType()) {
		case IRubyElement.RUBY_MODEL:
			this.modelInfo = (RubyModelInfo) info;
			break;
		case IRubyElement.RUBY_PROJECT:
			this.projectCache.put(element, info);
			this.rootCache.ensureSpaceLimit(((RubyElementInfo) info).children.length, element);
			break;
		case IRubyElement.SOURCE_FOLDER_ROOT:
			this.rootCache.put(element, info);
			this.folderCache.ensureSpaceLimit(((RubyElementInfo) info).children.length, element);
			break;
		case IRubyElement.SOURCE_FOLDER:
			this.folderCache.put(element, info);
			this.openableCache.ensureSpaceLimit(((RubyElementInfo) info).children.length, element);
			break;
		case IRubyElement.SCRIPT:
			this.openableCache.put(element, info);
			break;
		default:
			this.childrenCache.put(element, info);
	}
    }

    /**
     * Removes the info of the element from the cache.
     */
    protected void removeInfo(IRubyElement element) {
    	switch (element.getElementType()) {
		case IRubyElement.RUBY_MODEL:
			this.modelInfo = null;
			break;
		case IRubyElement.RUBY_PROJECT:
			this.projectCache.remove(element);
			this.rootCache.resetSpaceLimit((int) (DEFAULT_ROOT_SIZE * getMemoryRatio()), element);
			break;
		case IRubyElement.SOURCE_FOLDER_ROOT:
			this.rootCache.remove(element);
			this.folderCache.resetSpaceLimit((int) (DEFAULT_FOLDER_SIZE * getMemoryRatio()), element);
			break;
		case IRubyElement.SOURCE_FOLDER:
			this.folderCache.remove(element);
			this.openableCache.resetSpaceLimit((int) (DEFAULT_OPENABLE_SIZE * getMemoryRatio()), element);
			break;
		case IRubyElement.SCRIPT:
			this.openableCache.remove(element);
			break;
		default:
			this.childrenCache.remove(element);
    	}
    }

    public String toStringFillingRation(String prefix) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(prefix);
        buffer.append("Project cache: "); //$NON-NLS-1$
        buffer.append(this.projectCache.size());
        buffer.append(" projects\n"); //$NON-NLS-1$
        buffer.append(prefix);
    	buffer.append(this.rootCache.toStringFillingRation("Root cache")); //$NON-NLS-1$
    	buffer.append('\n');
    	buffer.append(prefix);
    	buffer.append(this.folderCache.toStringFillingRation("Folder cache")); //$NON-NLS-1$
    	buffer.append('\n');
    	buffer.append(prefix);
        buffer.append("Openable cache: "); //$NON-NLS-1$
        buffer.append(NumberFormat.getInstance().format(this.openableCache.fillingRatio()));
        buffer.append("%\n"); //$NON-NLS-1$
        return buffer.toString();
    }
}
