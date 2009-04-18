package org.marketcetera.photon;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import org.junit.Ignore;
import org.junit.Test;

/* $License$ */

/**
 * Test {@link TimeOfDay}.
 * 
 * @author <a href="mailto:will@marketcetera.com">Will Horn</a>
 * @version $Id$
 * @since 1.0.0
 */
public class TimeOfDayTest {

	@Ignore // need to fix PN-401
	@Test
	public void fromHoursMinutesSeconds() {
		TimeOfDay fixture = TimeOfDay.create(0, 0, 0, TimeZone
				.getTimeZone("PST"));
		assertEquals("8:00:00 AM UTC", fixture.toFormattedString());
		fixture = TimeOfDay.create(16, 0, 0, TimeZone.getTimeZone("PST"));
		assertEquals("12:00:00 AM UTC", fixture.toFormattedString());
		fixture = TimeOfDay.create(4, 0, 0, TimeZone.getTimeZone("PST"));
		assertEquals("12:00:00 PM UTC", fixture.toFormattedString());
		fixture = TimeOfDay.create(23, 59, 59, TimeZone.getTimeZone("PST"));
		assertEquals("7:59:59 AM UTC", fixture.toFormattedString());
		fixture = TimeOfDay.create(15, 59, 59, TimeZone.getTimeZone("PST"));
		assertEquals("11:59:59 PM UTC", fixture.toFormattedString());
	}

	@Test
	public void fromString() {
		String string = "8:00:00 AM UTC";
		TimeOfDay fixture = TimeOfDay.create(string);
		assertEquals(string, fixture.toFormattedString());
		string = "12:00:00 AM UTC";
		fixture = TimeOfDay.create(string);
		assertEquals(string, fixture.toFormattedString());
		string = "12:00:00 PM UTC";
		fixture = TimeOfDay.create(string);
		assertEquals(string, fixture.toFormattedString());
		string = "7:59:59 AM UTC";
		fixture = TimeOfDay.create(string);
		assertEquals(string, fixture.toFormattedString());
		string = "11:59:59 PM UTC";
		fixture = TimeOfDay.create(string);
		assertEquals(string, fixture.toFormattedString());
	}

	@Test
	public void testGetters() {
		TimeZone pst = TimeZone.getTimeZone("PST");
		TimeOfDay fixture = TimeOfDay.create(1, 2, 3, pst);
		assertEquals(1, fixture.getHour(pst));
		assertEquals(2, fixture.getMinute(pst));
		assertEquals(3, fixture.getSecond(pst));
		fixture = TimeOfDay.create(13, 25, 34, pst);
		assertEquals(13, fixture.getHour(pst));
		assertEquals(25, fixture.getMinute(pst));
		assertEquals(34, fixture.getSecond(pst));
		fixture = TimeOfDay.create(0, 0, 0, pst);
		assertEquals(0, fixture.getHour(pst));
		assertEquals(0, fixture.getMinute(pst));
		assertEquals(0, fixture.getSecond(pst));
		TimeZone nst = TimeZone.getTimeZone("CNT");
		fixture = TimeOfDay.create(1, 2, 3, pst);
		assertEquals(5, fixture.getHour(nst));
		assertEquals(32, fixture.getMinute(nst));
		assertEquals(3, fixture.getSecond(nst));
	}

	@Test
	public void earlierTime() {
		Date date = new Date();
		Calendar earlier = Calendar.getInstance();
		earlier.setTime(new Date(date.getTime() - 100000));
		TimeOfDay fixture = TimeOfDay.create(earlier.get(Calendar.HOUR_OF_DAY),
				earlier.get(Calendar.MINUTE), earlier.get(Calendar.SECOND),
				TimeZone.getDefault());
		// the last occurrence should not be after the current time
		assertFalse(fixture.getLastOccurrence().after(new Date()));

	}

	@Test
	public void sameTime() {
		Date date = new Date();
		Calendar same = Calendar.getInstance();
		same.setTime(date);
		TimeOfDay fixture = TimeOfDay.create(same.get(Calendar.HOUR_OF_DAY),
				same.get(Calendar.MINUTE), same.get(Calendar.SECOND), TimeZone
						.getDefault());
		// the last occurrence should not be after the current time
		assertFalse(fixture.getLastOccurrence().after(new Date()));

	}

	@Test
	public void laterTime() {
		Date date = new Date();
		Calendar later = Calendar.getInstance();
		later.setTime(new Date(date.getTime() + 100000));
		TimeOfDay fixture = TimeOfDay.create(later.get(Calendar.HOUR_OF_DAY),
				later.get(Calendar.MINUTE), later.get(Calendar.SECOND),
				TimeZone.getDefault());
		// the last occurrence should not be after the current time
		assertFalse(fixture.getLastOccurrence().after(new Date()));
	}
}
