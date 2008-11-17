package org.rubypeople.rdt.internal.ui.text.correction;

import java.util.Iterator;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.IFileEditorInput;
import org.rubypeople.rdt.core.IRubyScript;
import org.rubypeople.rdt.internal.core.parser.MarkerUtility;
import org.rubypeople.rdt.internal.ui.RubyPlugin;
import org.rubypeople.rdt.internal.ui.RubyPluginImages;
import org.rubypeople.rdt.internal.ui.rubyeditor.EditorUtility;
import org.rubypeople.rdt.internal.ui.rubyeditor.IRubyAnnotation;
import org.rubypeople.rdt.ui.text.ruby.IInvocationContext;
import org.rubypeople.rdt.ui.text.ruby.IProblemLocation;
import org.rubypeople.rdt.ui.text.ruby.IRubyCompletionProposal;

public class IgnoreWarningProposal implements IRubyCompletionProposal {

	private IProblemLocation problem;
	private IInvocationContext context;

	public IgnoreWarningProposal(IInvocationContext context, IProblemLocation problem) {
		this.context = context;
		this.problem = problem;
	}

	public int getRelevance() {
		return 100;
	}

	public void apply(IDocument document) {		
		try {
			IRubyScript script = context.getRubyScript();
			
			// Remove annotations corresponding to the warning
			IFileEditorInput editorInput = (IFileEditorInput) EditorUtility.getEditorInput(script);		
			IAnnotationModel anoteModel = RubyPlugin.getDefault().getRubyDocumentProvider().getAnnotationModel(editorInput);
			Iterator iter = anoteModel.getAnnotationIterator();
			while (iter.hasNext()) {
				Annotation anote = (Annotation) iter.next();
				if (anote instanceof IRubyAnnotation) {
					IRubyAnnotation markerAnote = (IRubyAnnotation) anote;
					if (markerAnote.getId() != problem.getProblemId()) continue;
					Position pos = anoteModel.getPosition(anote);
					if (pos.getOffset() != problem.getOffset()) continue;
					if (pos.getLength() != problem.getLength()) continue;
					anoteModel.removeAnnotation(anote);
					MarkerUtility.ignore(script.getResource(), problem.getProblemId(), problem.getOffset(), problem.getLength());
					// FIXME Need to set this to ignore even though we don't have an IMarker here!
				}
			}
			// Remove underlying markers and rebuild if necessary
			IResource resource = script.getUnderlyingResource();			
			IMarker[] markers =  resource.findMarkers(problem.getMarkerType(), true, IResource.DEPTH_ZERO);
			boolean needToRebuild = false;
			for (int i = 0; i < markers.length; i++) {
				if (!MarkerUtility.markerMatches(problem.getProblemId(), problem.getOffset(), problem.getOffset() + problem.getLength(), markers[i])) continue;				
				MarkerUtility.ignore(markers[i]); // matching marker, add it to the list to ignore
				needToRebuild = true;
			}			
			if (needToRebuild) {
				resource.touch(new NullProgressMonitor()); // force resource to be "re-built"
			}
		} catch (CoreException e) {
			RubyPlugin.log(e);
		}	
	}

	public String getAdditionalProposalInfo() {
		// TODO Auto-generated method stub
		return null;
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getDisplayString() {
		return "Ignore this warning";
	}

	public Image getImage() {
		return RubyPluginImages.get(RubyPluginImages.IMG_OBJS_LIGHTBULB);
	}

	public Point getSelection(IDocument document) {
		// TODO Auto-generated method stub
		return null;
	}

}
