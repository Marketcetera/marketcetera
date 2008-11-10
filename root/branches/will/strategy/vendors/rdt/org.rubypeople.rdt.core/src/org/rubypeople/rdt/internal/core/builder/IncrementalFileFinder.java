/*
?* Author: David Corbin
?*
?* Copyright (c) 2005 RubyPeople.
?*
?* This file is part of the Ruby Development Tools (RDT) plugin for eclipse. 
 * RDT is subject to the "Common Public License (CPL) v 1.0". You may not use
 * RDT except in compliance with the License. For further information see 
 * org.rubypeople.rdt/rdt.license.
?*/


package org.rubypeople.rdt.internal.core.builder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;

class IncrementalFileFinder implements IFileProvider {
    private final IResourceDelta delta;

    IncrementalFileFinder(IResourceDelta delta) {
        super();
        this.delta = delta;
    }

    public List findFiles() {
        return getAffectedFiles(delta.getAffectedChildren());
    }

    private List getAffectedFiles(IResourceDelta[] deltas) {
        List files = new ArrayList();
        for (int i = 0; i < deltas.length; i++) {
            IResourceDelta curDelta = deltas[i];
            
            // Skip removals, we don't want to parse those
            if (curDelta.getKind() == IResourceDelta.REMOVED) 
                continue;
            
            IResource resource = curDelta.getResource();
            if (isContainer(resource)) {
                files.addAll(getAffectedFiles(curDelta.getAffectedChildren()));
                continue;
            }
            
            if (isFile(resource)) 
                continue;

            // FIXME This uses the Util class to check if teh filename looks like a ruby file, use behavior like RubyFileMatcher
            if (!isRubyResource(resource)) 
                continue;
            files.add(resource);
        }
        
        
        return files;
    }   
    
    private boolean isContainer(IResource resource) {
        return resource.getType() == IResource.FOLDER || resource.getType() == IResource.PROJECT;
    }

    private boolean isFile(IResource resource) {
        return resource.getType() != IResource.FILE;
    }

    private boolean isRubyResource(IResource resource) {
        return org.rubypeople.rdt.internal.core.util.Util.isRubyLikeFileName(resource.getName());
    }


}