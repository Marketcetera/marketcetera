package org.rubypeople.rdt.internal.core.pmd;

import java.io.IOException;

public interface Tokenizer {
	void tokenize(SourceCode tokens, Tokens tokenEntries) throws IOException;
}
