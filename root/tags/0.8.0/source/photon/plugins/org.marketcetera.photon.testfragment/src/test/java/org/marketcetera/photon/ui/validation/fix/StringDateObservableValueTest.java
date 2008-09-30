package org.marketcetera.photon.ui.validation.fix;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import junit.framework.Test;

import org.eclipse.core.databinding.observable.Realm;
import org.marketcetera.core.FIXVersionTestSuite;
import org.marketcetera.core.FIXVersionedTestCase;
import org.marketcetera.quickfix.FIXVersion;

import quickfix.DataDictionary;
import quickfix.Message;
import quickfix.field.MaturityMonthYear;

public class StringDateObservableValueTest extends FIXVersionedTestCase {

	
	public StringDateObservableValueTest(String inName, FIXVersion version) {
		super(inName, version);
	}

	public static Test suite()
	{
        return  new FIXVersionTestSuite(StringDateObservableValueTest.class,
                    FIXVersionTestSuite.ALL_VERSIONS);
	}
	
	public void testMaturityMonthYear() throws Exception {
		Message message = new Message();
		MockStringDateObservableValue month = new MockStringDateObservableValue(Calendar.MONTH, Realm.getDefault(), 
				message, MaturityMonthYear.FIELD, fixDD.getDictionary());
		MockStringDateObservableValue year = new MockStringDateObservableValue(Calendar.YEAR, Realm.getDefault(), 
				message, MaturityMonthYear.FIELD, fixDD.getDictionary());
		StringToDateCustomConverter monthConverter = new StringToDateCustomConverter(DateToStringCustomConverter.MONTH_FORMAT);
		StringToDateCustomConverter yearConverter = new StringToDateCustomConverter(DateToStringCustomConverter.LONG_YEAR_FORMAT);
		month.doSetValue(monthConverter.convert("SEP"));
		Calendar cal = new GregorianCalendar();
		int curYear = cal.get(Calendar.YEAR);
		assertEquals(curYear+"09", message.getString(MaturityMonthYear.FIELD));
		year.doSetValue(yearConverter.convert("2008"));
		assertEquals("200809", message.getString(MaturityMonthYear.FIELD));
	}
	
	private class MockStringDateObservableValue extends StringDateObservableValue
	{

		public MockStringDateObservableValue(int calendarField, Realm realm, Message message,
				int fieldNumber, DataDictionary dataDictionary) {
			super(calendarField, realm, message, fieldNumber, dataDictionary);
		}

		public void doSetValue(Object obj) {
			super.doSetValue(obj);
		}
		
	}
}
