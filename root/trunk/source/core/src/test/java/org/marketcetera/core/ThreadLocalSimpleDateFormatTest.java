package org.marketcetera.core;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import junit.framework.TestCase;

public class ThreadLocalSimpleDateFormatTest extends TestCase {

	public void testThreadLocalSimpleDateFormatString() throws ParseException, InterruptedException {
		new ExpectedTestFailure(NullPointerException.class){
			@Override
			protected void execute() throws Throwable {
				new ThreadLocalSimpleDateFormat(null);
			}
		}.run();
		new ExpectedTestFailure(IllegalArgumentException.class){
			@Override
			protected void execute() throws Throwable {
				new ThreadLocalSimpleDateFormat("xxx");
			}
		}.run();
		
		final ThreadLocalSimpleDateFormat tlsdf = new ThreadLocalSimpleDateFormat("yyyyMMdd");
		SimpleDateFormat mainThreadFormat = tlsdf.get();
		assertEquals("20080910", mainThreadFormat.format(mainThreadFormat.parse("20080910")));
		
		final SimpleDateFormat [] otherThreadFormat = new SimpleDateFormat[1];
		Thread aThread = new Thread(){
			@Override
			public void run() {
				otherThreadFormat[0] = tlsdf.get();
			}
		};
		aThread.start();
		aThread.join();
		
		assertNotSame(mainThreadFormat, otherThreadFormat);
	}

	public void testThreadLocalSimpleDateFormatStringLocale() throws ParseException {
		final ThreadLocalSimpleDateFormat tlsdf = new ThreadLocalSimpleDateFormat("MMMM", Locale.FRANCE);
		SimpleDateFormat mainThreadFormat = tlsdf.get();
		SimpleDateFormat testFormat = new SimpleDateFormat("yyyyMMdd");
		assertEquals("mars", mainThreadFormat.format(testFormat.parse("20080301"))); // note this may break with future versions of Java, or if France changes the word for March...
		
	}

	public void testSetTimeZone() {
		final ThreadLocalSimpleDateFormat tlsdf = new ThreadLocalSimpleDateFormat("HH:mm");
		tlsdf.setTimeZone(TimeZone.getTimeZone("MST")); // mountain standard
		SimpleDateFormat mainThreadFormat = tlsdf.get();
		SimpleDateFormat testFormat = new SimpleDateFormat("HH:mm");
		testFormat.setTimeZone(TimeZone.getTimeZone("MST"));
		Date testDate = new Date();
		
		assertEquals(testFormat.format(testDate), mainThreadFormat.format(testDate));
		
		
	}

}
