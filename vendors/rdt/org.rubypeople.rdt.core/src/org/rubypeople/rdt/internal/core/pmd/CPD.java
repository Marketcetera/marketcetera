package org.rubypeople.rdt.internal.core.pmd;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;

public class CPD {
	
	private Map<String, SourceCode> source = new HashMap<String, SourceCode>();
    private int minimumTileSize;
	private Language language = new RubyLanguage();
	private MatchAlgorithm matchAlgorithm;
	private Tokens tokens = new Tokens();
	private CPDListener listener = new CPDNullListener();    
    private Set<String> current = new HashSet<String>();

	private CPD(int minimumTileSize) {
        this.minimumTileSize = minimumTileSize;
    }
	
	public static Iterator<Match> findMatches(List<IFile> files) throws IOException {
         int minimumTokens = 5; // TODO Make this configurable in a preference page
         CPD cpd = new CPD(minimumTokens);
		 cpd.add(files);
         cpd.go();
         return cpd.getMatches();
	}
	
    private void go() {
        TokenEntry.clearImages();
        matchAlgorithm = new MatchAlgorithm(source, tokens, minimumTileSize, listener);
        matchAlgorithm.findMatches();
    }
       
    private Iterator<Match> getMatches() {
        return matchAlgorithm.matches();
    }
    
    private void add(List<IFile> files) throws IOException {
    	for (IFile file : files) {
			add(files.size(), file);
		}
    }
    
    private void add(int fileCount, IFile file) throws IOException {    
    	File realFile = file.getLocation().toFile();
        // TODO refactor this thing into a separate class
        String signature = realFile.getName() + '_' + realFile.length();
        if (current.contains(signature)) { // skip duplicates
            return;
        }
        current.add(signature);
        
        if (!realFile.getCanonicalPath().equals(realFile.getAbsolutePath())) { // skip symlinks
            return;
        }

        listener.addedFile(fileCount, realFile);
// FIXME We need to compensate all our token offsets by the end-of-line characters!
        SourceCode sourceCode = new SourceCode(new SourceCode.FileCodeLoader(realFile));
        language.getTokenizer().tokenize(sourceCode, tokens);
        source.put(sourceCode.getFileName(), sourceCode);
    }

}
