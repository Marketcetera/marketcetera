package org.rubypeople.rdt.internal.launching;

import java.util.ArrayList;
import java.util.List;


public class ArgumentSplitter {

    private List<String> args;
    private StringBuffer currentArg;
    private boolean inQuotedArg;
    private char expectedCloseQuote;
    
    public static List<String> split(String input) {
        return new ArgumentSplitter().internalSplit(input);
    }
    
    private ArgumentSplitter() {}
    
    private List<String> internalSplit(String input) {
        currentArg = new StringBuffer();
        args = new ArrayList<String>();

        for (int i=0; i< input.length(); i++) {
            char c = input.charAt(i);
            if (isEnddingQuote(c)) {
                recordArg(true);
                continue;
            } 

            if (isStartingQuote(c)) {
                startQuotedArgument(c);
                continue;
            }

            if (isArgumentSeparator(c)) {
                recordArg(false);
                continue;
            } 
            
            currentArg.append(c);
        }
        recordArg(false);
        
        return args;
    }

    private void startQuotedArgument(char c) {
        inQuotedArg = true;
        expectedCloseQuote = c;
    }

    private boolean isArgumentSeparator(char c) {
        return c == ' ' && !inQuotedArg;
    }

    private boolean isStartingQuote(char c) {
        return isQuote(c) && !inQuotedArg;
    }

    private boolean isEnddingQuote(char c) {
        return isQuote(c) && inQuotedArg && c == expectedCloseQuote;
    }

    private boolean isQuote(char c) {
        return c == '\'' || c == '\"';
    }

    private void recordArg(boolean allowEmptyArg) {
        String arg = currentArg.toString();
        if (allowEmptyArg || arg.length() > 0)
            args.add(arg);
        currentArg = new StringBuffer();
        inQuotedArg = false;
    }
}
