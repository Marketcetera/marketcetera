package org.marketcetera.util.file;

import java.util.Locale;
import org.junit.Test;
import org.marketcetera.util.log.I18NMessageProvider;
import org.marketcetera.util.test.TestCaseBase;

import static org.junit.Assert.*;

public class MessagesTest
    extends TestCaseBase
{
    @Test
    public void messagesExist()
    {
        I18NMessageProvider.setLocale(Locale.US);
        assertEquals
            ("Closing failed",
             Messages.CLOSING_FAILED.getText());
        assertEquals
            ("Cannot determine type of file 'a'",
             Messages.CANNOT_GET_TYPE.getText("a"));
        assertEquals
            ("Cannot delete file 'a'",
             Messages.CANNOT_DELETE.getText("a"));
        assertEquals
            ("Cannot copy from file 'a' to file 'b'",
             Messages.CANNOT_COPY_FILES.getText("a","b"));
        assertEquals
            ("Cannot copy from input stream to file 'a'",
             Messages.CANNOT_COPY_ISTREAM.getText("a"));
        assertEquals
            ("Cannot copy from reader to file 'a'",
             Messages.CANNOT_COPY_READER.getText("a"));
        assertEquals
            ("Cannot copy from file 'a' to output stream",
             Messages.CANNOT_COPY_OSTREAM.getText("a"));
        assertEquals
            ("Cannot copy from file 'a' to writer",
             Messages.CANNOT_COPY_WRITER.getText("a"));
        assertEquals
            ("Cannot copy from memory to file 'a'",
             Messages.CANNOT_COPY_MEMORY_SRC.getText("a"));
        assertEquals
            ("Cannot copy from file 'a' to memory",
             Messages.CANNOT_COPY_MEMORY_DST.getText("a"));
    }
}
