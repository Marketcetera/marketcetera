package org.rubypeople.rdt.internal.formatter;

import java.io.StringWriter;

import org.eclipse.text.edits.ReplaceEdit;
import org.eclipse.text.edits.TextEdit;
import org.jruby.ast.Node;
import org.jruby.ast.visitor.rewriter.ReWriteVisitor;
import org.jruby.ast.visitor.rewriter.ReWriterFactory;
import org.jruby.ast.visitor.rewriter.utils.ReWriterContext;
import org.rubypeople.rdt.core.formatter.CodeFormatter;
import org.rubypeople.rdt.core.formatter.EditableFormatHelper;
import org.rubypeople.rdt.internal.core.parser.RubyParser;

public class ASTBasedCodeFormatter extends CodeFormatter {

	@Override
	public TextEdit format(int kind, String source, int offset, int length,
			int indentationLevel, String lineSeparator) {
		StringWriter writer = new StringWriter();
		EditableFormatHelper helper = new EditableFormatHelper();
		helper.setLineDelimeter(lineSeparator);
		helper.setSpacesBeforeAndAfterAssignments(true);
		helper.setAlwaysParanthesizeMethodDefs(true);
		source = source.substring(offset, length);
		ReWriterContext context = new ReWriterContext(writer, source, helper);
		ReWriterFactory factory = new ReWriterFactory(context);
		ReWriteVisitor visitor = factory.createReWriteVisitor();
		RubyParser parser = new RubyParser();
		Node root = parser.parse(source).getAST();
		root.accept(visitor);
		writer.append(lineSeparator);
		String result = writer.getBuffer().toString();
		return new ReplaceEdit(offset, length, result);
	}

}
