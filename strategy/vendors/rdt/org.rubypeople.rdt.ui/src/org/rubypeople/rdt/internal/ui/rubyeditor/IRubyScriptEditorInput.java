package org.rubypeople.rdt.internal.ui.rubyeditor;

import org.eclipse.ui.IEditorInput;
import org.rubypeople.rdt.core.IRubyScript;

public interface IRubyScriptEditorInput extends IEditorInput {
  public IRubyScript getRubyScript();
}
