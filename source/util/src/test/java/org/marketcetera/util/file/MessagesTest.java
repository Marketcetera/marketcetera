package org.marketcetera.util.file;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class MessagesTest
	extends TestCaseBase
{
    @Test
    public void messagesExist()
    {
        Messages.PROVIDER.setLocale(Locale.US);
        assertEquals
            ("Closing failed",
             Messages.PROVIDER.getText
             (Messages.CLOSING_FAILED));
        assertEquals
            ("Cannot determine type of file 'a'",
             Messages.PROVIDER.getText
             (Messages.CANNOT_GET_TYPE,"a"));
        assertEquals
            ("Cannot delete file 'a'",
             Messages.PROVIDER.getText
             (Messages.CANNOT_DELETE,"a"));
        assertEquals
            ("Cannot copy from file 'a' to file 'b'",
             Messages.PROVIDER.getText
             (Messages.CANNOT_COPY_FILES,"a","b"));
        assertEquals
            ("Cannot copy from input stream to file 'a'",
             Messages.PROVIDER.getText
             (Messages.CANNOT_COPY_ISTREAM,"a"));
        assertEquals
            ("Cannot copy from reader to file 'a'",
             Messages.PROVIDER.getText
             (Messages.CANNOT_COPY_READER,"a"));
        assertEquals
            ("Cannot copy from file 'a' to output stream",
             Messages.PROVIDER.getText
             (Messages.CANNOT_COPY_OSTREAM,"a"));
        assertEquals
            ("Cannot copy from file 'a' to writer",
             Messages.PROVIDER.getText
             (Messages.CANNOT_COPY_WRITER,"a"));
        assertEquals
            ("Cannot copy from memory to file 'a'",
             Messages.PROVIDER.getText
             (Messages.CANNOT_COPY_MEMORY_SRC,"a"));
        assertEquals
            ("Cannot copy from file 'a' to memory",
             Messages.PROVIDER.getText
             (Messages.CANNOT_COPY_MEMORY_DST,"a"));
    }
}
