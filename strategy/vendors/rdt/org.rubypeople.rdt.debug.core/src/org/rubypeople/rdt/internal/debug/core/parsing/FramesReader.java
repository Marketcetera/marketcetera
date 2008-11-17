package org.rubypeople.rdt.internal.debug.core.parsing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.rubypeople.rdt.internal.debug.core.RdtDebugCorePlugin;
import org.rubypeople.rdt.internal.debug.core.model.RubyStackFrame;
import org.rubypeople.rdt.internal.debug.core.model.RubyThread;
import org.xmlpull.v1.XmlPullParser;


public class FramesReader extends XmlStreamReader {
	
	private RubyThread thread;
	private List<RubyStackFrame> frames;
	
	public FramesReader(XmlPullParser xpp) {
		super(xpp);
	}

	public FramesReader(AbstractReadStrategy readStrategy) {
		super(readStrategy);
	}
	
	public RubyStackFrame[] readFrames(RubyThread thread) {
		
		this.thread = thread;
		this.frames = new ArrayList<RubyStackFrame>();
		try {
			this.read();
		} catch (Exception ex) {
			RdtDebugCorePlugin.log(ex);	
			return new RubyStackFrame[0];
		}
		Collections.sort(frames, new Comparator<RubyStackFrame>() {		
			public int compare(RubyStackFrame one, RubyStackFrame two) {
				return Integer.valueOf(one.getIndex()).compareTo(Integer.valueOf(two.getIndex()));
			}
		
		});
		RubyStackFrame[] frameArray = new RubyStackFrame[frames.size()];
		frames.toArray(frameArray);
		thread.setStackFrames(frameArray);
		return frameArray;
	}


	protected boolean processStartElement(XmlPullParser xpp) {
		
		String name = xpp.getName();		
		if (name.equals("frames")) {
			return true;			
		}
		if (name.equals("frame")) {
			int line = Integer.parseInt(xpp.getAttributeValue("", "line"));	
			int index = Integer.parseInt(xpp.getAttributeValue("", "no"));	
			String file = xpp.getAttributeValue("", "file");
			this.frames.add(new RubyStackFrame(thread, file, line, index));			
			return true;
		}
		return false;
	}
	
	protected boolean processEndElement(XmlPullParser xpp) {		
		return xpp.getName().equals("frames");
	}
}
