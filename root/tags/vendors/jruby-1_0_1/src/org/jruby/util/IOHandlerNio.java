/***** BEGIN LICENSE BLOCK *****
 * Version: CPL 1.0/GPL 2.0/LGPL 2.1
 *
 * The contents of this file are subject to the Common Public
 * License Version 1.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.eclipse.org/legal/cpl-v10.html
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * Copyright (C) 2006 Evan Buswell <ebuswell@gmail.com>
 * Copyright (C) 2007 Miguel Covarrubias <mlcovarrubias@gmail.com>
 * 
 * Alternatively, the contents of this file may be used under the terms of
 * either of the GNU General Public License Version 2 or later (the "GPL"),
 * or the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish to allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not to allow others to
 * use your version of this file under the terms of the CPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the CPL, the GPL or the LGPL.
 ***** END LICENSE BLOCK *****/

package org.jruby.util;

import org.jruby.Ruby;
import org.jruby.RubyString;
import org.jruby.RubyIO;

import java.nio.channels.Channel;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.SelectableChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.ByteBuffer;

import java.io.IOException;
import java.io.EOFException;

public class IOHandlerNio extends IOHandler {
    private Channel channel;

    private static final int BLOCK_SIZE = 1024 * 16;
    private ByteBuffer outBuffer;
    private ByteBuffer inBuffer;
    private boolean blocking = true;
    private boolean bufferedIO = false;

    public IOHandlerNio(Ruby runtime, Channel channel) throws IOException {
        super(runtime);
        String mode = "";
        this.channel = channel;
        if (channel instanceof ReadableByteChannel) {
            mode += "r";
            isOpen = true;
        }
        if (channel instanceof WritableByteChannel) {
            mode += "w";
            isOpen = true;
        }
        if ("rw".equals(mode)) {
            modes = new IOModes(runtime, IOModes.RDWR);
            isOpen = true;
        } else {
            if (!isOpen) {
                // Neither stream exists?
                // throw new IOException("Opening nothing?");
	        // Hack to cover the ServerSocketChannel case
                mode = "r";
                isOpen = true;
            }
            modes = new IOModes(runtime, mode);
        }
        fileno = RubyIO.getNewFileno();
        outBuffer = ByteBuffer.allocate(BLOCK_SIZE);
    }

    public Channel getChannel() {
        return channel;
    }

    // this seems wrong...
    public IOHandler cloneIOHandler() throws IOException {
        return new IOHandlerNio(getRuntime(), channel);
    }

    private void checkBuffered() throws IOException {
        if (bufferedIO) {
            throw new IOException("Can't mix buffered and unbuffered IO.");
        }
    }

    /* Unbuffered operations */
    
    public void setBlocking(boolean block) throws IOException {
        if (!(channel instanceof SelectableChannel)) {
            return;
        }
        synchronized (((SelectableChannel) channel).blockingLock()) {
            blocking = block;
            try {
                ((SelectableChannel) channel).configureBlocking(block);
            } catch (IllegalBlockingModeException e) {
                // ignore this; select() will set the correct mode when it is finished
            }
        }
    }

    public boolean getBlocking() {
        return blocking;
    }

    public ByteList sysread(int length) throws EOFException, BadDescriptorException, IOException {
        checkReadable();
        checkBuffered();
	
        ByteBuffer buffer = ByteBuffer.allocate(length);
        int bytes_read = 0;
        bytes_read = ((ReadableByteChannel) channel).read(buffer);
        if (bytes_read < 0) {
            throw new EOFException();
        }

        byte[] ret;
        if (buffer.hasRemaining()) {
            buffer.flip();
            ret = new byte[buffer.remaining()];
            buffer.get(ret);
        } else {
            ret = buffer.array();
        }
        return new ByteList(ret,false);
    }
    
    public int syswrite(ByteList string) throws BadDescriptorException, IOException {
        checkWritable();
        outBuffer.flip();
        flushOutBuffer();
    
        ByteBuffer buffer = ByteBuffer.wrap(string.bytes, string.begin, string.realSize);
        while (buffer.hasRemaining()) {
        if (((WritableByteChannel) channel).write(buffer) < 0) {
            // does this ever happen??
            throw new IOException("write returned less than zero");
        }
        }
        return buffer.capacity();
    }
    
    public int syswrite(int c) throws BadDescriptorException, IOException {
        ByteBuffer buffer = ByteBuffer.allocate(1); // inefficient?
        buffer.put((byte)c);
        
        return syswrite(new ByteList(buffer.array(),false));
    }
    
    public ByteList recv(int length) throws EOFException, BadDescriptorException, IOException {
        return sysread(length);
    }

    /* Buffered operations */
    private void setupBufferedIO() {
        if (bufferedIO) {
            return;
        }
        inBuffer = ByteBuffer.allocate(BLOCK_SIZE);
        flushInBuffer();
        bufferedIO = true;
    }

    int ungotc = -1;

    private ByteList consumeInBuffer(int length) {
        int offset = 0;
        if (ungotc > 0) {
            length--;
            offset = 1;
        }
        length = inBuffer.remaining() < length ? inBuffer.remaining() : length;
        byte[] ret = new byte[length + offset];
        inBuffer.get(ret, offset, length);
        if (ungotc > 0) {
            ret[0] = (byte) (ungotc & 0xff);
            ungotc = -1;
        }
        return new ByteList(ret,false);
    }

    private int fillInBuffer() throws IOException {
        inBuffer.clear();
        int i = ((ReadableByteChannel) channel).read(inBuffer);
        inBuffer.flip();
        return i;
    }

    private void flushInBuffer() {
        inBuffer.position(0);
        inBuffer.limit(0);
        ungotc = -1;
    }

    private void flushOutBuffer() throws IOException {
        while (outBuffer.hasRemaining()) {
            if (((WritableByteChannel) channel).write(outBuffer) < 0) {
                throw new IOException("write returned less than zero");
            }
        }
        outBuffer.clear();
    }
   
    /* unused but looks like it could be at some point
    private int buffer_rindex(ByteBuffer haystack, byte[] needle) {
        search_loop:
        for (int i = haystack.limit() - needle.length; i >= haystack.position(); i--) {
            for (int j = 0; j < needle.length; j++) {
                if (haystack.get(i + j) != needle[j]) {
                    continue search_loop;
                }
            }
            return i;
        }
        return -1;
    }*/

    private int buffer_index(ByteBuffer haystack, ByteList needle) {
        search_loop:
        for (int i = haystack.position(); i + (needle.realSize - 1) < haystack.limit(); i++) {
            for (int j = 0; j < needle.realSize; j++) {
                if (haystack.get(i + j) != needle.bytes[needle.begin + j]) {
                    continue search_loop;
                }
            }
            return i;
        }
        return -1;
    }

    public ByteList readpartial(int length) throws IOException, BadDescriptorException, EOFException {
        checkReadable();
        setupBufferedIO();

        if (!inBuffer.hasRemaining() && length > 0) {
            if (fillInBuffer() < 0) {
                throw new EOFException();
            }
        }
        return consumeInBuffer(length);
    }

    public ByteList read(int length) throws IOException, BadDescriptorException, EOFException {
        setupBufferedIO();
        checkReadable();

        boolean eof = false;
        int remaining = length;
        ByteList ret = new ByteList(length);
        ret.append(consumeInBuffer(remaining));
        remaining -= ret.length();
        while (remaining > 0) {
            int i = fillInBuffer();
            if (i < 0) {
                eof = true;
                break;
            }
            ret.append(consumeInBuffer(remaining));
            remaining -= i;
        }
        if (eof && ret.length() == 0) {
            throw new EOFException();
        }
        return ret;
    }

    public int write(ByteList string) throws IOException, BadDescriptorException {
        return syswrite(string);
    }

    public ByteList gets(ByteList separator) throws IOException, BadDescriptorException, EOFException {
        setupBufferedIO();
        checkReadable();

        ByteList ret = new ByteList();
        boolean eof = false;
        ByteList trigger;
        if (separator != null) {
            trigger = separator;
        } else {
            trigger = ((RubyString) getRuntime().getGlobalVariables().get("$/")).getByteList();
        }

        int idx;
        while ((idx = buffer_index(inBuffer, trigger)) < 0 && !eof) {
            ret.append(consumeInBuffer(BLOCK_SIZE));
            if (fillInBuffer() < 0) {
                eof = true;
	    }
        }
        if (eof && !inBuffer.hasRemaining() && ret.length() == 0) {
            throw new EOFException();
        }
        if (idx >= 0) {
            ret.append(consumeInBuffer((idx + trigger.realSize) - inBuffer.position()));
        } else if (eof) {
            ret.append(consumeInBuffer(BLOCK_SIZE));
        }
        return ret;
    }

    public ByteList getsEntireStream() throws IOException, BadDescriptorException, EOFException {
        checkReadable();
        setupBufferedIO();
        ByteList ret = new ByteList();
        boolean eof;

        while (true) {
            ret.append(consumeInBuffer(BLOCK_SIZE));
            if (fillInBuffer() < 0) {
                eof = true;
                break;
            }
        }
        if (eof && ret.length() == 0) {
            throw new EOFException();
        }
        return ret;
    }

    public int getc() throws IOException, BadDescriptorException, EOFException {
        checkReadable();
        setupBufferedIO();

        if (ungotc > 0) {
            int i = ungotc;
            ungotc = -1;
            return i;
        }

        if (!inBuffer.hasRemaining()) {
            if (fillInBuffer() < 0) {
                throw new EOFException();
            }
        }
        return (int) (inBuffer.get() & 0xff);
    }

    public void ungetc(int c) {
        setupBufferedIO();
        ungotc = c;
    }
    
    public void putc(int c) throws IOException, BadDescriptorException {
        checkWritable();

        if (!outBuffer.hasRemaining()) {
            outBuffer.flip();
            flushOutBuffer();
        }

        outBuffer.put((byte) (c & 0xff));
    }

    public void flush() throws IOException, BadDescriptorException {
        checkWritable();
        outBuffer.flip();
        flushOutBuffer();
    }

    /* FIXME: what is the difference between sync() and flush()?
     * does this difference make sense for nio? */
    public void sync() throws IOException, BadDescriptorException {
        flush();
    }
    
    public boolean isEOF() throws IOException, BadDescriptorException {
        setupBufferedIO();
        checkReadable();

        if (ungotc > 0) {
	    return false;
	}
        if (inBuffer.hasRemaining()) {
            return false;
        }
        if (fillInBuffer() < 0) {
            return true;
	}
        return false;
    }

    /* buffering independent */
    public void close() throws IOException {
	/* flush output buffer before close */
	if (outBuffer.position() > 0) {
	    outBuffer.flip();
	    flushOutBuffer();
	}

        channel.close();
    }
    
    public long pos() throws PipeException, IOException {
        if (channel instanceof FileChannel) {
            if (bufferedIO) {
                return ((FileChannel) channel).position() - (inBuffer.remaining() + (ungotc > 0 ? 1 : 0));
            } else {
                return ((FileChannel) channel).position();
            }
        } else {
            throw new IOHandler.PipeException();
        }
    }

    public void seek(long offset, int type) throws IOException, InvalidValueException, PipeException {
        checkOpen();
        if (channel instanceof FileChannel) {
            if (bufferedIO) {
                flushInBuffer();
            }
            try {
                switch (type) {
                case SEEK_SET:
                    ((FileChannel) channel).position(offset);
                    break;
                case SEEK_CUR:
                    ((FileChannel) channel).position(((FileChannel) channel).position() + offset);
                    break;
                case SEEK_END:
                    ((FileChannel) channel).position(((FileChannel) channel).size() + offset);
                    break;
                }
            } catch (IllegalArgumentException e) {
                throw new InvalidValueException();
            }
        } else {
            throw new IOHandler.PipeException();
        }
    }

    public void resetByModes(IOModes newModes) throws IOException, InvalidValueException {
        if (channel instanceof FileChannel) {
            if (newModes.isAppendable()) {
                try {
                    seek(0L, SEEK_END);
                } catch(PipeException e) {
                } catch(InvalidValueException e) {} // these won't be thrown.
            } else if (newModes.isWritable()) {
                try {
                    rewind();
                } catch(PipeException e) {} // won't be thrown.
            }
        }
    }
    
    public void rewind() throws IOException, PipeException {
        checkOpen();
        checkBuffered();
        if (channel instanceof FileChannel) {
            try {
                seek(0, SEEK_SET);
            } catch(InvalidValueException e) {} // won't be thrown
        } else {
            throw new IOHandler.PipeException();
        }
    }

    public void truncate(long length) throws IOException, PipeException {
        if (channel instanceof FileChannel) {
            ((FileChannel) channel).truncate(length);
        } else {
            throw new IOHandler.PipeException();
        }
    }

    /* invalid operation */
    public int pid() {
        return -1;
    }

    public FileChannel getFileChannel() {
        // FIXME: Satisfied for IOHandler, but this should throw some unsupported operation?
        return null;
    }
    
    public boolean hasPendingBuffered() {
        return ungotc >= 0 || (bufferedIO && inBuffer.remaining() > 0);
    }

    public int ready() throws IOException {
        if (bufferedIO) {
            return inBuffer.remaining();
        } else {
            return ungotc;
        }
    }
}
