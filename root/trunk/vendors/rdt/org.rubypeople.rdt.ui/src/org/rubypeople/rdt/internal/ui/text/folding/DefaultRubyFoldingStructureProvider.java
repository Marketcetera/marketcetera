/*
 * Created on Jan 12, 2005
 */
package org.rubypeople.rdt.internal.ui.text.folding;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.Assert;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.IProjectionPosition;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.rubypeople.rdt.core.ElementChangedEvent;
import org.rubypeople.rdt.core.IElementChangedListener;
import org.rubypeople.rdt.core.IMember;
import org.rubypeople.rdt.core.IParent;
import org.rubypeople.rdt.core.IRubyElement;
import org.rubypeople.rdt.core.IRubyElementDelta;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.core.ISourceRange;
import org.rubypeople.rdt.core.ISourceReference;
import org.rubypeople.rdt.core.IType;
import org.rubypeople.rdt.core.RubyCore;
import org.rubypeople.rdt.core.RubyModelException;
import org.rubypeople.rdt.internal.corext.util.RDocUtil;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyAbstractEditor;
import org.rubypeople.rdt.internal.ui.rubyeditor.RubyEditor;
import org.rubypeople.rdt.ui.IWorkingCopyManager;
import org.rubypeople.rdt.ui.PreferenceConstants;
import org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider;
import org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProviderExtension;

/**
 * @author cawilliams
 */
public class DefaultRubyFoldingStructureProvider implements IProjectionListener,
        IRubyFoldingStructureProvider, IRubyFoldingStructureProviderExtension {

    private ITextEditor fEditor;
    private ProjectionViewer fViewer;
    private IDocument fCachedDocument;
    private ProjectionAnnotationModel fCachedModel;
    private boolean fAllowCollapsing;
    private IRubyElement fInput;
    private IElementChangedListener fElementListener;
    private boolean fCollapseInnerTypes;
    private boolean fCollapseRubydoc;
    private boolean fCollapseMethods;

    /*
     * (non-Javadoc)
     * 
     * @see org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider#install(org.eclipse.ui.texteditor.ITextEditor,
     *      org.eclipse.jface.text.source.projection.ProjectionViewer)
     */
    public void install(ITextEditor editor, ProjectionViewer viewer) {
        if (editor instanceof RubyAbstractEditor) {
            fEditor = editor;
            fViewer = viewer;
            fViewer.addProjectionListener(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider#uninstall()
     */
    public void uninstall() {
        if (isInstalled()) {
            projectionDisabled();
            fViewer.removeProjectionListener(this);
            fViewer = null;
            fEditor = null;
        }
    }

    protected boolean isInstalled() {
        return fEditor != null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.rubypeople.rdt.ui.text.folding.IRubyFoldingStructureProvider#initialize()
     */
    public void initialize() {
        if (!isInstalled()) return;

        initializePreferences();
        try {
            IDocumentProvider provider = fEditor.getDocumentProvider();
            fCachedDocument = provider.getDocument(fEditor.getEditorInput());
            fAllowCollapsing = true;

            if (fEditor instanceof RubyEditor) {
                IWorkingCopyManager manager = RubyPlugin.getDefault().getWorkingCopyManager();
                fInput = manager.getWorkingCopy(fEditor.getEditorInput());
            }

            if (fInput != null) {
                ProjectionAnnotationModel model = (ProjectionAnnotationModel) fEditor
                        .getAdapter(ProjectionAnnotationModel.class);
                if (model != null) {
                    fCachedModel = model;
                    if (fInput instanceof IRubyScript) {
                        IRubyScript unit = (IRubyScript) fInput;
                        synchronized (unit) {
                            try {
                                unit.reconcile();
                            } catch (RubyModelException e) {
                            }
                        }
                    }
                    Map additions = computeAdditions((IParent) fInput);
                    /*
                     * Minimize the events being sent out - as this happens in
                     * the UI thread merge everything into one call.
                     */
                    List removals = new LinkedList();
                    Iterator existing = model.getAnnotationIterator();
                    while (existing.hasNext())
                        removals.add(existing.next());
                    model.replaceAnnotations((Annotation[]) removals
                            .toArray(new Annotation[removals.size()]), additions);
                }
            }

        } finally {
            fCachedDocument = null;
            fAllowCollapsing = false;
            fCachedModel = null;
        }
    }

    /**
     * @param input
     * @return
     */
    private Map computeAdditions(IParent parent) {
        Map map = new HashMap();
        try {
            computeAdditions(parent.getChildren(), map);
        } catch (RubyModelException x) {
            RubyPlugin.log(x);
        }
        return map;
    }

    private void computeAdditions(IRubyElement[] elements, Map map) throws RubyModelException {
        for (int i = 0; i < elements.length; i++) {
            IRubyElement element = elements[i];
            computeAdditions(element, map);

            if (element instanceof IParent) {
                IParent parent = (IParent) element;
                computeAdditions(parent.getChildren(), map);
            }
        }
    }

    /**
     * @param element
     * @param map
     */
    private void computeAdditions(IRubyElement element, Map map) {
        boolean createProjection = false;

        boolean collapse = false;
        switch (element.getElementType()) {
        case IRubyElement.TYPE:
            collapse = fAllowCollapsing && fCollapseInnerTypes && isInnerType((IType) element);
            createProjection = true;
            break;
        case IRubyElement.METHOD:
            collapse = fAllowCollapsing && fCollapseMethods;
            createProjection = true;
            break;
        }

        if (createProjection) {
            IRegion[] regions = computeProjectionRanges(element);
            if (regions != null) {
                // comments
                for (int i = 0; i < regions.length - 1; i++) {
                    Position position = createProjectionPosition(regions[i]);
                    if (position != null)
                        map.put(new RubyProjectionAnnotation(element, fAllowCollapsing
                                && fCollapseRubydoc, true), position);
                }
                // code
                Position position = createProjectionPosition(regions[regions.length - 1]);
                if (position != null)
                    map.put(new RubyProjectionAnnotation(element, collapse, false), position);
            }
        }
    }

    private void initializePreferences() {
        IPreferenceStore store = RubyPlugin.getDefault().getPreferenceStore();
        fCollapseInnerTypes = store.getBoolean(PreferenceConstants.EDITOR_FOLDING_INNERTYPES);
        fCollapseRubydoc = store.getBoolean(PreferenceConstants.EDITOR_FOLDING_RDOC);
        fCollapseMethods = store.getBoolean(PreferenceConstants.EDITOR_FOLDING_METHODS);
    }

    private boolean isInnerType(IType type) {
        IRubyElement parent = type.getParent();
        if (parent != null) {
            int parentType = parent.getElementType();
            return (parentType != IRubyElement.SCRIPT);
        }
        return false;
    }

    private IRegion[] computeProjectionRanges(IRubyElement element) {
        try {
            if (element instanceof ISourceReference) {
                ISourceReference reference = (ISourceReference) element;
                ISourceRange range = reference.getSourceRange();
                
                String contents = reference.getSource();
                if (contents == null) return null;

                List regions = new ArrayList();
                int shift = range.getOffset();
                int start = shift;
                
                IRegion region = null;
                if (element instanceof IMember)
                	region = RDocUtil.getDocumentationRegion((IMember) element);
                if (region != null)
                	regions.add(region);
                regions.add(new Region(start, range.getOffset() + range.getLength() - start));

                if (regions.size() > 0) {
                    IRegion[] result = new IRegion[regions.size()];
                    regions.toArray(result);
                    return result;
                }
            }
        } catch (RubyModelException e) {
        }

        return null;
    }

    private Position createProjectionPosition(IRegion region) {
        if (fCachedDocument == null) return null;

        try {
            int start = fCachedDocument.getLineOfOffset(region.getOffset());
            int end = fCachedDocument.getLineOfOffset(region.getOffset() + region.getLength());
            if (start != end) {
                int offset = fCachedDocument.getLineOffset(start);
                int endOffset = -1;
                if ((end + 1) == fCachedDocument.getNumberOfLines()) {                	
                	endOffset = fCachedDocument.getLength();
                } else {
                	endOffset = fCachedDocument.getLineOffset(end + 1);
                }
                return new Position(offset, endOffset - offset);
            }
        } catch (BadLocationException x) {
        }

        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionEnabled()
     */
    public void projectionEnabled() {
        // http://home.ott.oti.com/teams/wswb/anon/out/vms/index.html
        // projectionEnabled messages are not always paired with
        // projectionDisabled
        // i.e. multiple enabled messages may be sent out.
        // we have to make sure that we disable first when getting an enable
        // message.
        projectionDisabled();

        if (fEditor instanceof RubyAbstractEditor) {
            initialize();
            fElementListener = new ElementChangedListener();
            RubyCore.addElementChangedListener(fElementListener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.eclipse.jface.text.source.projection.IProjectionListener#projectionDisabled()
     */
    public void projectionDisabled() {
        fCachedDocument = null;
        if (fElementListener != null) {
            RubyCore.removeElementChangedListener(fElementListener);
            fElementListener = null;
        }
    }

    protected void processDelta(IRubyElementDelta delta) {

        if (!isInstalled()) return;

        if ((delta.getFlags() & (IRubyElementDelta.F_CONTENT | IRubyElementDelta.F_CHILDREN)) == 0)
            return;

        ProjectionAnnotationModel model = (ProjectionAnnotationModel) fEditor
                .getAdapter(ProjectionAnnotationModel.class);
        if (model == null) return;

        try {

            IDocumentProvider provider = fEditor.getDocumentProvider();
            fCachedDocument = provider.getDocument(fEditor.getEditorInput());
            fCachedModel = model;
            fAllowCollapsing = false;

            Map additions = new HashMap();
            List deletions = new ArrayList();
            List updates = new ArrayList();

            Map updated = computeAdditions((IParent) fInput);
            Map previous = createAnnotationMap(model);

            Iterator e = updated.keySet().iterator();
            while (e.hasNext()) {
                RubyProjectionAnnotation newAnnotation = (RubyProjectionAnnotation) e.next();
                IRubyElement element = newAnnotation.getElement();
                Position newPosition = (Position) updated.get(newAnnotation);

                List annotations = (List) previous.get(element);
                if (annotations == null) {

                    additions.put(newAnnotation, newPosition);

                } else {
                    Iterator x = annotations.iterator();
                    boolean matched = false;
                    while (x.hasNext()) {
                        Tuple tuple = (Tuple) x.next();
                        RubyProjectionAnnotation existingAnnotation = tuple.annotation;
                        Position existingPosition = tuple.position;
                        if (newAnnotation.isComment() == existingAnnotation.isComment()) {
                            if (existingPosition != null && (!newPosition.equals(existingPosition))) {
                                existingPosition.setOffset(newPosition.getOffset());
                                existingPosition.setLength(newPosition.getLength());
                                updates.add(existingAnnotation);
                            }
                            matched = true;
                            x.remove();
                            break;
                        }
                    }
                    if (!matched) additions.put(newAnnotation, newPosition);

                    if (annotations.isEmpty()) previous.remove(element);
                }
            }

            e = previous.values().iterator();
            while (e.hasNext()) {
                List list = (List) e.next();
                int size = list.size();
                for (int i = 0; i < size; i++)
                    deletions.add(((Tuple) list.get(i)).annotation);
            }

            match(deletions, additions, updates);

            Annotation[] removals = new Annotation[deletions.size()];
            deletions.toArray(removals);
            Annotation[] changes = new Annotation[updates.size()];
            updates.toArray(changes);
            model.modifyAnnotations(removals, additions, changes);

        } finally {
            fCachedDocument = null;
            fAllowCollapsing = true;
            fCachedModel = null;
        }
    }

    private Map createAnnotationMap(IAnnotationModel model) {
        Map map = new HashMap();
        Iterator e = model.getAnnotationIterator();
        while (e.hasNext()) {
            Object annotation = e.next();
            if (annotation instanceof RubyProjectionAnnotation) {
                RubyProjectionAnnotation ruby = (RubyProjectionAnnotation) annotation;
                Position position = model.getPosition(ruby);
                Assert.isNotNull(position);
                List list = (List) map.get(ruby.getElement());
                if (list == null) {
                    list = new ArrayList(2);
                    map.put(ruby.getElement(), list);
                }
                list.add(new Tuple(ruby, position));
            }
        }

        Comparator comparator = new Comparator() {

            public int compare(Object o1, Object o2) {
                return ((Tuple) o1).position.getOffset() - ((Tuple) o2).position.getOffset();
            }
        };
        for (Iterator it = map.values().iterator(); it.hasNext();) {
            List list = (List) it.next();
            Collections.sort(list, comparator);
        }
        return map;
    }

    /**
     * Matches deleted annotations to changed or added ones. A deleted
     * annotation/position tuple that has a matching addition / change is
     * updated and marked as changed. The matching tuple is not added (for
     * additions) or marked as deletion instead (for changes). The result is
     * that more annotations are changed and fewer get deleted/re-added.
     */
    private void match(List deletions, Map additions, List changes) {
        if (deletions.isEmpty() || (additions.isEmpty() && changes.isEmpty())) return;

        List newDeletions = new ArrayList();
        List newChanges = new ArrayList();

        Iterator deletionIterator = deletions.iterator();
        while (deletionIterator.hasNext()) {
            RubyProjectionAnnotation deleted = (RubyProjectionAnnotation) deletionIterator.next();
            Position deletedPosition = fCachedModel.getPosition(deleted);
            if (deletedPosition == null) continue;

            Tuple deletedTuple = new Tuple(deleted, deletedPosition);

            Tuple match = findMatch(deletedTuple, changes, null);
            boolean addToDeletions = true;
            if (match == null) {
                match = findMatch(deletedTuple, additions.keySet(), additions);
                addToDeletions = false;
            }

            if (match != null) {
                IRubyElement element = match.annotation.getElement();
                deleted.setElement(element);
                deletedPosition.setLength(match.position.getLength());
                if (deletedPosition instanceof RubyElementPosition && element instanceof IMember) {
                    RubyElementPosition jep = (RubyElementPosition) deletedPosition;
                    jep.setMember((IMember) element);
                }

                deletionIterator.remove();
                newChanges.add(deleted);

                if (addToDeletions) newDeletions.add(match.annotation);
            }
        }

        deletions.addAll(newDeletions);
        changes.addAll(newChanges);
    }

    /**
     * Finds a match for <code>tuple</code> in a collection of annotations.
     * The positions for the <code>JavaProjectionAnnotation</code> instances
     * in <code>annotations</code> can be found in the passed
     * <code>positionMap</code> or <code>fCachedModel</code> if
     * <code>positionMap</code> is <code>null</code>.
     * <p>
     * A tuple is said to match another if their annotations have the same
     * comment flag and their position offsets are equal.
     * </p>
     * <p>
     * If a match is found, the annotation gets removed from
     * <code>annotations</code>.
     * </p>
     * 
     * @param tuple
     *            the tuple for which we want to find a match
     * @param annotations
     *            collection of <code>JavaProjectionAnnotation</code>
     * @param positionMap
     *            a <code>Map&lt;Annotation, Position&gt;</code> or
     *            <code>null</code>
     * @return a matching tuple or <code>null</code> for no match
     */
    private Tuple findMatch(Tuple tuple, Collection annotations, Map positionMap) {
        Iterator it = annotations.iterator();
        while (it.hasNext()) {
            RubyProjectionAnnotation annotation = (RubyProjectionAnnotation) it.next();
            if (tuple.annotation.isComment() == annotation.isComment()) {
                Position position = positionMap == null ? fCachedModel.getPosition(annotation)
                        : (Position) positionMap.get(annotation);
                if (position == null) continue;

                if (tuple.position.getOffset() == position.getOffset()) {
                    it.remove();
                    return new Tuple(annotation, position);
                }
            }
        }

        return null;
    }

    private static final class Tuple {

        RubyProjectionAnnotation annotation;
        Position position;

        Tuple(RubyProjectionAnnotation annotation, Position position) {
            this.annotation = annotation;
            this.position = position;
        }
    }

    private class ElementChangedListener implements IElementChangedListener {

        /*
         * @see org.eclipse.jdt.core.IElementChangedListener#elementChanged(org.eclipse.jdt.core.ElementChangedEvent)
         */
        public void elementChanged(ElementChangedEvent e) {
            IRubyElementDelta delta = findElement(fInput, e.getDelta());            
            if (delta != null) {
            	if (delta.getRubyScriptAST() == null) return;
            	processDelta(delta);
            }
        }

        private IRubyElementDelta findElement(IRubyElement target, IRubyElementDelta delta) {

            if (delta == null || target == null) return null;

            IRubyElement element = delta.getElement();

            if (element.getElementType() > IRubyElement.SCRIPT) return null;

            if (target.equals(element)) return delta;

            IRubyElementDelta[] children = delta.getAffectedChildren();

            for (int i = 0; i < children.length; i++) {
                IRubyElementDelta d = findElement(target, children[i]);
                if (d != null) return d;
            }

            return null;
        }
    }

    private static class RubyProjectionAnnotation extends ProjectionAnnotation {

        private IRubyElement fRubyElement;
        private boolean fIsComment;

        public RubyProjectionAnnotation(IRubyElement element, boolean isCollapsed, boolean isComment) {
            super(isCollapsed);
            fRubyElement = element;
            fIsComment = isComment;
        }

        public IRubyElement getElement() {
            return fRubyElement;
        }

        public void setElement(IRubyElement element) {
            fRubyElement = element;
        }

        public boolean isComment() {
            return fIsComment;
        }

        public void setIsComment(boolean isComment) {
            fIsComment = isComment;
        }
    }

    /**
     * Projection position that will return two foldable regions: one folding
     * away the lines before the one containing the simple name of the ruby
     * element, one folding away any lines after the caption.
     * 
     * @since 0.7.0
     */
    private static final class RubyElementPosition extends Position implements IProjectionPosition {

        private IMember fMember;

        public RubyElementPosition(int offset, int length, IMember member) {
            super(offset, length);
            Assert.isNotNull(member);
            fMember = member;
        }

        public void setMember(IMember member) {
            Assert.isNotNull(member);
            fMember = member;
        }

        /*
         * @see org.eclipse.jface.text.source.projection.IProjectionPosition#computeFoldingRegions(org.eclipse.jface.text.IDocument)
         */
        public IRegion[] computeProjectionRegions(IDocument document) throws BadLocationException {
            int nameStart = offset;
            try {
                /*
                 * The member's name range may not be correct. However,
                 * reconciling would trigger another element delta which would
                 * lead to reentrant situations. Therefore, we optimistically
                 * assume that the name range is correct, but double check the
                 * received lines below.
                 */
                ISourceRange nameRange = fMember.getNameRange();
                if (nameRange != null) nameStart = nameRange.getOffset();

            } catch (RubyModelException e) {
                // ignore and use default
            }

            int firstLine = document.getLineOfOffset(offset);
            int captionLine = document.getLineOfOffset(nameStart);
            int lastLine = document.getLineOfOffset(offset + length);

            /*
             * see comment above - adjust the caption line to be inside the
             * entire folded region, and rely on later element deltas to correct
             * the name range.
             */
            if (captionLine < firstLine) captionLine = firstLine;
            if (captionLine > lastLine) captionLine = lastLine;

            IRegion preRegion;
            if (firstLine < captionLine) {
                int preOffset = document.getLineOffset(firstLine);
                IRegion preEndLineInfo = document.getLineInformation(captionLine);
                int preEnd = preEndLineInfo.getOffset();
                preRegion = new Region(preOffset, preEnd - preOffset);
            } else {
                preRegion = null;
            }

            if (captionLine < lastLine) {
                int postOffset = document.getLineOffset(captionLine + 1);
                IRegion postRegion = new Region(postOffset, offset + length - postOffset);

                if (preRegion == null) return new IRegion[] { postRegion};

                return new IRegion[] { preRegion, postRegion};
            }

            if (preRegion != null) return new IRegion[] { preRegion};

            return null;
        }

        /*
         * @see org.eclipse.jface.text.source.projection.IProjectionPosition#computeCaptionOffset(org.eclipse.jface.text.IDocument)
         */
        public int computeCaptionOffset(IDocument document) throws BadLocationException {
            int nameStart = offset;
            try {
                // need a reconcile here?
                ISourceRange nameRange = fMember.getNameRange();
                if (nameRange != null) nameStart = nameRange.getOffset();
            } catch (RubyModelException e) {
                // ignore and use default
            }

            return nameStart - offset;
        }

    }
    
	/* filters */
    /**
	 * Filter for annotations.
	 * @since 0.9.0
	 */
	private static interface Filter {
		boolean match(RubyProjectionAnnotation annotation);
	}
	
	private static final class RubyElementSetFilter implements Filter {
		private final Set fSet;
		private final boolean fMatchCollapsed;

		private RubyElementSetFilter(Set set, boolean matchCollapsed) {
			fSet= set;
			fMatchCollapsed= matchCollapsed;
		}

		public boolean match(RubyProjectionAnnotation annotation) {
			boolean stateMatch= fMatchCollapsed == annotation.isCollapsed();
			if (stateMatch && !annotation.isComment() && !annotation.isMarkedDeleted()) {
				IRubyElement element= annotation.getElement();
				if (fSet.contains(element)) {
					return true;
				}
			}
			return false;
		}
	}
    /**
	 * Member filter, matches nested members (but not top-level types).
	 * @since 0.9.0
	 */
	private final Filter fMemberFilter = new Filter() {
		public boolean match(RubyProjectionAnnotation annotation) {
			if (!annotation.isCollapsed() && !annotation.isComment() && !annotation.isMarkedDeleted()) {
				IRubyElement element= annotation.getElement();
				if (element instanceof IMember) {
					if (element.getElementType() != IRubyElement.TYPE || ((IMember) element).getDeclaringType() != null) {
						return true;
					}
				}
			}
			return false;
		}
	};
	
	/**
	 * Comment filter, matches comments.
	 * @since 0.9.0
	 */
	private final Filter fCommentFilter = new Filter() {
		public boolean match(RubyProjectionAnnotation annotation) {
			if (!annotation.isCollapsed() && annotation.isComment() && !annotation.isMarkedDeleted()) {
				return true;
			}
			return false;
		}
	};

	public void collapseMembers() {
		modifyFiltered(fMemberFilter, false);		
	}

	public void collapseComments() {
		modifyFiltered(fCommentFilter, false);		
	}

	public void collapseElements(IRubyElement[] elements) {
		Set set= new HashSet(Arrays.asList(elements));
		modifyFiltered(new RubyElementSetFilter(set, false), false);
	}

	public void expandElements(IRubyElement[] elements) {
		Set set= new HashSet(Arrays.asList(elements));
		modifyFiltered(new RubyElementSetFilter(set, true), true);
	}
	
	/**
	 * Collapses all annotations matched by the passed filter.
	 * 
	 * @param filter the filter to use to select which annotations to collapse
	 * @param expand <code>true</code> to expand the matched annotations, <code>false</code> to
	 *        collapse them
	 * @since 0.9.0
	 */
	private void modifyFiltered(Filter filter, boolean expand) {
		if (!isInstalled())
			return;

		ProjectionAnnotationModel model= (ProjectionAnnotationModel) fEditor.getAdapter(ProjectionAnnotationModel.class);
		if (model == null)
			return;
		
		List modified= new ArrayList();
		Iterator iter= model.getAnnotationIterator();
		while (iter.hasNext()) {
			Object annotation= iter.next();
			if (annotation instanceof RubyProjectionAnnotation) {
				RubyProjectionAnnotation java= (RubyProjectionAnnotation) annotation;
				
				if (filter.match(java)) {
					if (expand)
						java.markExpanded();
					else
						java.markCollapsed();
					modified.add(java);
				}

			}
		}
		
		model.modifyAnnotations(null, null, (Annotation[]) modified.toArray(new Annotation[modified.size()]));
	}
}
