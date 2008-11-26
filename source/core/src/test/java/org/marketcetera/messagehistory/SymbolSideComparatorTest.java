package org.marketcetera.messagehistory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import junit.framework.TestCase;

import org.marketcetera.messagehistory.MessageHolder;
import org.marketcetera.messagehistory.SymbolSideComparator;

import quickfix.CharField;
import quickfix.Field;
import quickfix.FieldNotFound;
import quickfix.Message;
import quickfix.StringField;
import quickfix.field.Account;
import quickfix.field.Side;
import quickfix.field.Symbol;

public class SymbolSideComparatorTest extends TestCase {

    private SymbolSideComparator comparator;

    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        comparator = new SymbolSideComparator();
    }

    public void testEmptyMessages()
    {
        Message m1 = new Message();
        Message m2 = new Message();
        MessageHolder mh1 = new MessageHolder(m1);
        MessageHolder mh2 = new MessageHolder(m2);
        
        assertTrue(comparator.compare(mh1, mh2) == 0);
        assertTrue(comparator.compare(mh2, mh1) == 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);
    }

    private void fieldTesterHelper(StringField field1, StringField field2 )
    {
        Message m1 = new Message();
        Message m2 = new Message();
        MessageHolder mh1 = new MessageHolder(m1);
        MessageHolder mh2 = new MessageHolder(m2);

        // set symbol on m1
        m1.setField(field1);
        
        assertTrue(comparator.compare(mh1, mh2) > 0);
        assertTrue(comparator.compare(mh2, mh1) < 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);
        
        // now set field on m2 that's different
        m2.setField(field2);
        
        assertTrue(comparator.compare(mh1, mh2) > 0);
        assertTrue(comparator.compare(mh2, mh1) < 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);
        
        // now set field on m2 that's same
        m2.setField(field1);
        
        assertTrue(comparator.compare(mh1, mh2) == 0);
        assertTrue(comparator.compare(mh2, mh1) == 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);
        
        // check that empty string works too 
        // now set field on m2 that's different
        m2.setField(new StringField(field1.getField(), "")); //$NON-NLS-1$
        
        assertTrue(comparator.compare(mh1, mh2) > 0);
        assertTrue(comparator.compare(mh2, mh1) < 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);
    }
    
    private void fieldTesterHelper(CharField field1, CharField field2 )
    {
        Message m1 = new Message();
        Message m2 = new Message();
        MessageHolder mh1 = new MessageHolder(m1);
        MessageHolder mh2 = new MessageHolder(m2);

        // set symbol on m1
        m1.setField(field1);
        
        assertTrue(comparator.compare(mh1, mh2) > 0);
        assertTrue(comparator.compare(mh2, mh1) < 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);
        
        // now set field on m2 that's different
        m2.setField(field2);
        
        assertTrue(comparator.compare(mh1, mh2) > 0);
        assertTrue(comparator.compare(mh2, mh1) < 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);
        
        // now set field on m2 that's same
        m2.setField(field1);
        
        assertTrue(comparator.compare(mh1, mh2) == 0);
        assertTrue(comparator.compare(mh2, mh1) == 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);
        
        // check that empty string works too 
        // now set field on m2 that's different
        m2.setField(new CharField(field1.getField(), '\0'));
        
        assertTrue(comparator.compare(mh1, mh2) > 0);
        assertTrue(comparator.compare(mh2, mh1) < 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);
    }

    public void testSingleField()
    {
        fieldTesterHelper(new Symbol("toli"), new Symbol("bob")); //$NON-NLS-1$ //$NON-NLS-2$
        fieldTesterHelper(new Side(Side.SELL), new Side(Side.BUY));
        fieldTesterHelper(new Account("toli"), new Account("bob")); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    public void testOrdering()
    {
        MessageHolder [] messages = new MessageHolder[]{
                new MessageHolder(createMessage()),
                new MessageHolder(createMessage()),
                new MessageHolder(createMessage(new Symbol("IBM"))), //$NON-NLS-1$
                new MessageHolder(createMessage(new Symbol("IBM"))), //$NON-NLS-1$
                new MessageHolder(createMessage(new Symbol("IBM"), new Account("12340"))), //$NON-NLS-1$ //$NON-NLS-2$
                new MessageHolder(createMessage(new Symbol("IBM"), new Account("12340"))), //$NON-NLS-1$ //$NON-NLS-2$
                new MessageHolder(createMessage(new Symbol("IBM"), new Account("12341"))), //$NON-NLS-1$ //$NON-NLS-2$
                new MessageHolder(createMessage(new Symbol("IBM"), new Side(Side.BUY))), //$NON-NLS-1$
                new MessageHolder(createMessage(new Symbol("IBM"), new Side(Side.BUY), new Account("12340"))), //$NON-NLS-1$ //$NON-NLS-2$
                new MessageHolder(createMessage(new Symbol("IBM"), new Side(Side.SELL))), //$NON-NLS-1$
                new MessageHolder(createMessage(new Symbol("IBM"), new Side(Side.SELL), new Account("12340"))), //$NON-NLS-1$ //$NON-NLS-2$
                new MessageHolder(createMessage(new Symbol("MSFT"))), //$NON-NLS-1$
                new MessageHolder(createMessage(new Symbol("MSFT"), new Account("12341"))), //$NON-NLS-1$ //$NON-NLS-2$
                new MessageHolder(createMessage(new Symbol("MSFT"), new Side(Side.BUY))), //$NON-NLS-1$
        };
        Random rand = new Random(24);
        MessageHolder [] copyOfMessages = new MessageHolder[messages.length];
        System.arraycopy(messages, 0, copyOfMessages, 0, messages.length);
        List<MessageHolder> testList = Arrays.asList(copyOfMessages);
        Collections.shuffle(testList, rand);
        Collections.sort(testList, comparator);
        for (int i = 0; i < testList.size(); i++){
            assertMessagesEqual(messages[i], testList.get(i), i);
        }
    }
    
    private void assertMessagesEqual(MessageHolder holder, MessageHolder holder2, int arrayIndex) {
        Message m1 = holder.getMessage();
        Message m2 = holder2.getMessage();
        assertTrue("Index: "+"arrayIndex"+" comparing symbols"+m1+" "+m2, areFieldsEqual(m1, m2, Symbol.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertTrue("Index: "+"arrayIndex"+" comparing sides"+m1+" "+m2, areFieldsEqual(m1, m2, Side.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        assertTrue("Index: "+"arrayIndex"+" comparing accounts"+m1+" "+m2, areFieldsEqual(m1, m2, Account.FIELD)); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    private boolean areFieldsEqual(Message m1, Message m2, int field) {
        if (m1.isSetField(field)){
            try {
                String fieldValue1 = m1.getString(field);
                String fieldValue2 = m2.getString(field);
                return fieldValue1.equals(fieldValue2);
            } catch (FieldNotFound e) {
                return false;
            }
        } else {
            return !m2.isSetField(field);
        }
    }

    private Message createMessage(Field ... fields) {
        Message message = new MyMessage();
        for (Field field : fields) {
            if (field instanceof StringField) {
                StringField stringField = (StringField) field;
                message.setField(stringField);
            } else if (field instanceof CharField) {
                CharField charField = (CharField) field;
                message.setField(charField);
            }
        }
        return message;
    }

    public void testCompare() {
        Message m1 = new Message();
        Message m2 = new Message();
        MessageHolder mh1 = new MessageHolder(m1);
        MessageHolder mh2 = new MessageHolder(m2);
        
        assertTrue(comparator.compare(mh1, mh2) == 0);
        assertTrue(comparator.compare(mh2, mh1) == 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);

        m1.setField(new Symbol("asdf")); //$NON-NLS-1$
        m2.setField(new Symbol("qwer")); //$NON-NLS-1$

        assertTrue(comparator.compare(mh1, mh2) < 0);
        assertTrue(comparator.compare(mh2, mh1) > 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);

        m2.setField(new Side(Side.BUY));
        m1.setField(new Side(Side.BUY));

        assertTrue(comparator.compare(mh1, mh2) < 0);
        assertTrue(comparator.compare(mh2, mh1) > 0);
        assertTrue(comparator.compare(mh1, mh1) == 0);
        assertTrue(comparator.compare(mh2, mh2) == 0);

        
    }
    
    class MyMessage extends Message
    {

        @Override
        public String toString() {
            Symbol symbol = new Symbol();
            Account account = new Account();
            Side side = new Side();
            try {
                getField(symbol);
            } catch (FieldNotFound e) {
            }
            try {
                getField(account);
            } catch (FieldNotFound e) {
            }
            try {
                getField(side);
            } catch (FieldNotFound e) {
            }
            return "["+symbol+" "+side+" "+account+"]"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        
    }

}
