package org.marketcetera.photon.notification;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.marketcetera.util.misc.ClassVersion;

/* $License$ */

/**
 * Plays a wave file.
 * 
 * Modified from
 * http://www.anyexample.com/programming/java/java_play_wav_sound_file.xml
 * 
 * TODO: improve error handling, maybe provide feedback to what went wrong, make
 * mockable for unit testing
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 0.8.0
 */
@ClassVersion("$Id$")//$NON-NLS-1$
public class PlayWave extends Thread {

	/**
	 * buffer to hold audio data being transferred from input stream to output
	 * stream
	 */
	private static final int EXTERNAL_BUFFER_SIZE = 524288; // 128Kb

	/**
	 * sound clip file
	 */
	private String mFilename;

	/**
	 * Constructor.
	 * 
	 * @param filename
	 *            wav file path
	 */
	public PlayWave(String filename) {
		this.mFilename = filename;
	}

	@Override
	public void run() {
		SLF4JLoggerProxy.debug(this, "Playing audio file: \"{0}\"", mFilename); //$NON-NLS-1$
		File soundFile = new File(mFilename);
		if (!soundFile.exists()) {
			Messages.AUDIO_CANNOT_FIND_FILE.error(this, mFilename);
			return;
		}

		AudioInputStream audioInputStream = null;
		try {
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
		} catch (UnsupportedAudioFileException e) {
			SLF4JLoggerProxy.warn(this, e);
			return;
		} catch (IOException e) {
			SLF4JLoggerProxy.warn(this, e);
			return;
		}

		AudioFormat format = audioInputStream.getFormat();
		SourceDataLine auline = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);

		try {
			auline = (SourceDataLine) AudioSystem.getLine(info);
			auline.open(format);
		} catch (LineUnavailableException e) {
			SLF4JLoggerProxy.warn(this, e);
			return;
		}

		auline.start();
		int nBytesRead = 0;
		byte[] abData = new byte[EXTERNAL_BUFFER_SIZE];

		try {
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(abData, 0, abData.length);
				if (nBytesRead >= 0)
					auline.write(abData, 0, nBytesRead);
			}
		} catch (IOException e) {
			SLF4JLoggerProxy.warn(this, e);
			return;
		} finally {
			auline.drain();
			auline.close();
		}

	}
}
