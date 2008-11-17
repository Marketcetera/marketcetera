/*
 * Created on Feb 18, 2005
 *
 */
package org.rubypeople.rdt.internal.core.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.rubypeople.rdt.core.RubyCore;

/**
 * @author Chris
 * 
 */
public class TaskParser {
// TODO fix so it only recognizes Task tags in comments
	private boolean fCaseSensitive = false;
	private String[] fTags;
	private int[] fPriorities;
	private List<TaskTag> tasks;

	public TaskParser(Map preferences) {
		String caseSensitive = getString(preferences, RubyCore.COMPILER_TASK_CASE_SENSITIVE, RubyCore.ENABLED);
		if (caseSensitive.equals(RubyCore.ENABLED)) fCaseSensitive = true;
		String tags = getString(preferences, RubyCore.COMPILER_TASK_TAGS, RubyCore.DEFAULT_TASK_TAGS);
		String priorities = getString(preferences, RubyCore.COMPILER_TASK_PRIORITIES, RubyCore.DEFAULT_TASK_PRIORITIES);
		fTags = tokenize(tags, ",");
		fPriorities = convertPriorities(tokenize(priorities, ","));
		tasks = new ArrayList<TaskTag>();
	}

	private String getString(Map preferences, String key, String def) {
        if (preferences == null) return def;
        String answer = (String) preferences.get(key);
        if (answer == null) return def;
        return answer;
    }

    private int[] convertPriorities(String[] stringPriorities) {
		int priorities[] = new int[stringPriorities.length];
		for (int i = 0; i < stringPriorities.length; i++) {
			String priority = stringPriorities[i];
			if (priority.equals(RubyCore.COMPILER_TASK_PRIORITY_LOW)) {
				priorities[i] = IMarker.PRIORITY_LOW;
			} else if (priority.equals(RubyCore.COMPILER_TASK_PRIORITY_HIGH)) {
				priorities[i] = IMarker.PRIORITY_HIGH;
			} else {
				priorities[i] = IMarker.PRIORITY_NORMAL;
			}
		}
		return priorities;
	}

	private String[] tokenize(String tags, String delim) {
		String[] tokens;
		StringTokenizer tokenizer = new StringTokenizer(tags, delim);
		tokens = new String[tokenizer.countTokens()];
		int i = 0;
		while (tokenizer.hasMoreTokens()) {
			tokens[i++] = tokenizer.nextToken();
		}
		return tokens;
	}

    public void parse(Reader reader) throws IOException {
        String loadFromReader = loadFromReader(reader);
        parse(loadFromReader);
    }

    public void parse(String contents) {
        try {
            if (fTags.length <= 0)
                return;
            int offset = 0;
            int lineNum = 0;
            String line = null;

            while ((line = findNextLine(contents, offset)) != null) {
                processLine(line, offset, lineNum);
                lineNum++;
                offset += line.length();
            }
        } catch (CoreException e) {
            RubyCore.log(e);
        }
	}

	private String findNextLine(String contents, int offset) {
        if (offset >= contents.length())
            return null;
        
        int crPos = contents.indexOf('\r', offset);
        int nlPos = contents.indexOf('\n', offset);
        int eolPos = crPos;
        if (crPos == -1)
            eolPos = nlPos;
        if (nlPos == -1 && crPos == -1)
            return contents.substring(offset);
        if (crPos + 1 == nlPos && crPos >= 0) {
                eolPos ++;
            }
        return contents.substring(offset, eolPos+1);
    }

	private void processLine(String line, int offset, int lineNum) throws CoreException {
		if (!fCaseSensitive) line = line.toLowerCase();
		for (int i = 0; i < fTags.length; i++) {
			String tag = fTags[i];
			int priority = fPriorities[i];
			if (!fCaseSensitive) tag = tag.toLowerCase();
			if (line.matches(".*#.*" + tag + ".*[\\n\\r]*")) {
				int index = line.indexOf(tag);
				String message = line.substring(index).trim();
				createTaskTag(priority, message, lineNum + 1, offset + index, offset + index + message.length());
			}
		}
	}

	private void createTaskTag(int priority, String message, int lineNumber, int start, int end) throws CoreException {
		TaskTag task = new TaskTag(message, priority, lineNumber, start, end);
		tasks.add(task);
	}

	private String loadFromReader(Reader reader) throws IOException {
		StringBuffer contents = new StringBuffer();
		char[] buffer = new char[4096];
		while (true) {
			int bytesRead = reader.read(buffer);
			if (bytesRead == -1)
				return contents.toString();
			contents.append(buffer, 0, bytesRead);
		}
	}

	public List<TaskTag> getTasks() {
		return Collections.unmodifiableList(tasks);
	}

	public void clear() {
		tasks.clear();		
	}
}
