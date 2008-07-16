package org.marketcetera.persist;

import org.marketcetera.core.ClassVersion;
import org.marketcetera.util.file.CopyUtils;
import org.marketcetera.util.except.I18NException;

import javax.persistence.*;
import java.math.BigInteger;
import java.math.BigDecimal;
import java.sql.*;
import java.io.*;
import java.net.URL;
import java.util.List;
import java.util.LinkedList;

/* $License$ */
/**
 * An entity to test persistence of various JPA supported data types.
 * It doesn't include attribute types: string, long & date as they are already
 * present in EntityBase as id and the last updated timestamp
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
@Entity
@Table(name = "test_datatypes", uniqueConstraints={@UniqueConstraint(columnNames={"name"})})
@NamedNativeQueries(
        value={
        @NamedNativeQuery(name="getVersion",
            query="select version() as data",resultSetMapping = "stringValue"),
        @NamedNativeQuery(name="getDbCharset",
            query="select @@character_set_database as data",resultSetMapping = "stringValue"),
        @NamedNativeQuery(name="getConnCharset",
            query="select @@character_set_connection as data",resultSetMapping = "stringValue"),
        @NamedNativeQuery(name="getClientCharset",
            query="select @@character_set_client as data",resultSetMapping = "stringValue"),
        @NamedNativeQuery(name="getLocale",
            query="select @@lc_time_names as data",resultSetMapping = "stringValue"),
        @NamedNativeQuery(name="getDbTimezone",
            query="select @@system_time_zone as data",resultSetMapping = "stringValue"),
        @NamedNativeQuery(name="getConnectionTimezone",
            query="select @@session.time_zone as data",resultSetMapping = "stringValue"),
        @NamedNativeQuery(name="getConnectionCollation",
            query="select @@collation_connection as data",resultSetMapping = "stringValue"),
        @NamedNativeQuery(name="getDbCollation",
            query="select @@collation_database as data",resultSetMapping = "stringValue")
        }
)
@SqlResultSetMapping(name="stringValue",columns=@ColumnResult(name="data"))
public class DataTypes extends NDEntityBase implements SummaryDataType {
    private static final long serialVersionUID = 5065543034578768245L;

    public DataTypes() throws PersistenceException {
        charLob = VendorUtils.initClob();
        binaryLob = VendorUtils.initBlob();
    }

    /**
     * Fetches the underlying database's version number for unit-testing
     * 
     * @return the underlying database's version number
     *
     * @throws PersistenceException if there was an error
     *
     */
    public String fetchDBVersion() throws PersistenceException {
        return execStringQuery("getVersion");
    }
    /**
     * Fetches the underlying database's charset
     *
     * @return the underlying database's charset
     *
     * @throws PersistenceException if there was an error
     */
    public String fetchDBCharset() throws PersistenceException {
        return execStringQuery("getDbCharset");
    }
    /**
     * Fetches the underlying connection charset
     *
     * @return the underlying connection charset
     *
     * @throws PersistenceException if there was an error
     */
    public String fetchConnCharset() throws PersistenceException {
        return execStringQuery("getConnCharset");
    }
    /**
     * Fetches the client's charset
     *
     * @return the client's charset
     *
     * @throws PersistenceException if there was an error
     */
    public String fetchClientCharset() throws PersistenceException {
        return execStringQuery("getClientCharset");
    }
    /**
     * Fetches the underlying database's locale for unit-testing
     *
     * @return the underlying database's locale
     *
     * @throws PersistenceException if there was an error
     */
    public String fetchDBLocale() throws PersistenceException {
        return execStringQuery("getLocale");
    }
    /**
     * Fetches the underlying database's system timezone for unit-testing
     *
     * @return the underlying database's system timezone
     *
     * @throws PersistenceException if there was an error
     */
    public String fetchDBTz() throws PersistenceException {
        return execStringQuery("getDbTimezone");
    }
    /**
     * Fetches the underlying database's connection timezone for unit-testing
     *
     * @return the underlying database's connection timezone
     *
     * @throws PersistenceException if there was an error
     */
    public String fetchDBConnTz() throws PersistenceException {
        return execStringQuery("getConnectionTimezone");
    }
    /**
     * Fetches the underlying database's system collation for unit-testing
     *
     * @return the underlying database's system collation
     *
     * @throws PersistenceException if there was an error
     */
    public String fetchDBCollation() throws PersistenceException {
        return execStringQuery("getDbCollation");
    }
    /**
     * Fetches the underlying database's connection collation for unit-testing
     *
     * @return the underlying database's connection collation
     *
     * @throws PersistenceException if there was an error
     */
    public String fetchDBConnCollation() throws PersistenceException {
        return execStringQuery("getConnectionCollation");
    }

    /**
     * Save the entity
     *
     * @throws PersistenceException if there was an error
     * saving the object
     */
    public void save() throws PersistenceException {
        saveRemote(null);
    }

    /**
     * Delete the entity
     *
     * @throws PersistenceException if there was an error deleting
     * the object
     */
    public void delete() throws PersistenceException {
        deleteRemote(null);
    }

    /**
     * Saves the instance and writes the contents
     * of the supplied URL to the Clob
     *
     * @param url the url whose contents needs to be written to the clob
     *
     * @throws PersistenceException if there's an exception saving the clob
     */
    public void saveAndWriteClob(URL url) throws PersistenceException {
        saveRemote(new LobContext(false,true,url));
    }

    /**
     * Read the contents of the clob out to the specified URL
     *
     * @param url the url
     *
     * @throws PersistenceException if there's an exception saving the blob
     */
    public void readFromClob(URL url) throws PersistenceException {
        executeRemote(new ReadLobTxn(getId()),new LobContext(true,true,url));
    }
    /**
     * Saves the instance and writes the contents
     * of the supplied URL to the Blob
     *
     * @param url the url whose contents needs to be written to the blob
     *
     * @throws PersistenceException if there was an error saving the blob
     */
    public void saveAndWriteBlob(URL url) throws PersistenceException {
        saveRemote(new LobContext(false,false,url));
    }

    /**
     * Read the contents of the blob out to the specified URL
     *
     * @param url the url
     *
     * @throws PersistenceException if there was an error reading the blobl
     */
    public void readFromBlob(URL url) throws PersistenceException {
        executeRemote(new ReadLobTxn(getId()),new LobContext(true,false,url));
    }

    /**
     * Saves multiple instances within a single transaction.
     *
     * @param dataTypes the list of data types to save.
     *
     * @return the list of saved types.
     * 
     * @throws PersistenceException if there was an error running the
     * transaction
     */
    public static List<DataTypes> saveMultiple(final DataTypes... dataTypes)
            throws PersistenceException {
        return executeRemote(new Transaction<List<DataTypes>>(){
            private static final long serialVersionUID = 3733752391156129595L;

            public List<DataTypes> execute(EntityManager em,
                                           PersistContext context)
                    throws PersistenceException {
                LinkedList<DataTypes> l = new LinkedList<DataTypes>();
                for(DataTypes d: dataTypes) {
                    d.save();
                    l.add(d);
                }
                return l;
            }
        },null);
    }

    public int getNumInt() {
        return numInt;
    }

    public void setNumInt(int numInt) {
        this.numInt = numInt;
    }
    @Column(precision = 10, scale = 5)
    public float getNumFloat() {
        return numFloat;
    }

    public void setNumFloat(float numFloat) {
        this.numFloat = numFloat;
    }

    @Column(precision = 20, scale = 10)
    public double getNumDouble() {
        return numDouble;
    }

    public void setNumDouble(double numDouble) {
        this.numDouble = numDouble;
    }

    public BigInteger getBigInteger() {
        return bigInteger;
    }

    public void setBigInteger(BigInteger bigInteger) {
        this.bigInteger = bigInteger;
    }

    @Column(precision = 20, scale = 10)
    public BigDecimal getBigDecimal() {
        return bigDecimal;
    }

    public void setBigDecimal(BigDecimal bigDecimal) {
        this.bigDecimal = bigDecimal;
    }

    public java.sql.Date getDate() {
        return date;
    }

    public void setDate(java.sql.Date date) {
        this.date = date;
    }

    public Time getTime() {
        return time;
    }

    public void setTime(Time time) {
        this.time = time;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public TestEnum getTestEnum() {
        return testEnum;
    }

    public void setTestEnum(TestEnum testEnum) {
        this.testEnum = testEnum;
    }

    public SerializedTester getSerialTester() {
        return serialTester;
    }

    public void setSerialTester(SerializedTester serialTester) {
        this.serialTester = serialTester;
    }

    protected void postSaveLocal(EntityManager em,
                                 EntityBase merged,
                                 PersistContext context) {
        //Carry out lob operations if the context is supplied
        if(context != null) {
            DataTypes d = (DataTypes) merged;
            LobContext lc = (LobContext) context;
            try {
                if (!lc.isRead()) {
                    if(lc.isClob()) {
                        CopyUtils.copyChars(new InputStreamReader(
                                lc.getUrl().openStream(),"UTF-8"),
                                d.getCharLob().setCharacterStream(1));
                    } else {
                        CopyUtils.copyBytes(lc.getUrl().openStream(),
                                d.getBinaryLob().setBinaryStream(1));
                    }
                }
            } catch (I18NException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Executes the supplied named query that produces a string result and
     * returns string value
     * @param queryName the query name
     * @return the string result value
     * @throws PersistenceException if there was an error
     */
    private String execStringQuery(final String queryName) throws PersistenceException {
        return executeRemote(new Transaction<String>(){
            private static final long serialVersionUID = -3608086096520638798L;

            public String execute(EntityManager em, PersistContext context) {
                return (String)em.createNamedQuery(queryName).getSingleResult();
            }
        },null);
    }

    private Clob getCharLob() {
        return charLob;
    }

    private void setCharLob(Clob charLob) {
        this.charLob = charLob;
    }

    private Blob getBinaryLob() {
        return binaryLob;
    }

    private void setBinaryLob(Blob binaryLob) {
        this.binaryLob = binaryLob;
    }

    private int numInt;
    private float numFloat;
    private double numDouble;
    private BigInteger bigInteger;
    private BigDecimal bigDecimal;
    private java.sql.Date date;
    private Time time;
    private Timestamp timestamp;
    private byte[] bytes;
    private TestEnum testEnum;
    private SerializedTester serialTester;
    private Clob charLob;
    private Blob binaryLob;

    /**
     * An inner class to communicate lob operation information
     * during persistence operations
     */
    private static class LobContext implements PersistContext {
        private static final long serialVersionUID = 4700648792529629679L;

        private LobContext(boolean read, boolean clob, URL url) {
            isRead = read;
            isClob = clob;
            this.url = url;
        }

        public boolean isRead() {
            return isRead;
        }

        public boolean isClob() {
            return isClob;
        }

        public URL getUrl() {
            return url;
        }

        private boolean isRead;
        private boolean isClob;
        private URL url;
    }

    /**
     * A transaction to read off lob without making any changes to the entity.
     */
    private static class ReadLobTxn implements Transaction<Object> {
        private static final long serialVersionUID = 323657917400120276L;

        private ReadLobTxn(long id) {
            this.id = id;
        }
        public Object execute(EntityManager em, PersistContext context) {
            //retrieve the entity
            DataTypes d = em.find(DataTypes.class,id);
            LobContext lc = (LobContext) context;
            try {
                if(lc.isRead()) {
                    if(lc.isClob()) {
                        CopyUtils.copyChars(d.getCharLob().getCharacterStream(),
                                new FileWriter(lc.getUrl().getPath()));
                    } else {
                        CopyUtils.copyBytes(d.getBinaryLob().getBinaryStream(),
                                new FileOutputStream(lc.getUrl().getPath()));
                    }
                }
            } catch (I18NException e) {
                throw new RuntimeException(e);
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return null;
        }
        private final long id;
    }

    /**
     * Class to test serialized attribute types
     */
    public static class SerializedTester implements Serializable {
        private static final long serialVersionUID = 6103639391657648712L;

        public SerializedTester(String value) {
            this.value = value;
        }

        public String toString() {
            return value;
        }

        private String value;

        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            SerializedTester that = (SerializedTester) o;

            if (value != null ? !value.equals(that.value) : that.value != null) return false;

            return true;
        }

        public int hashCode() {
            return (value != null ? value.hashCode() : 0);
        }
    }

    /**
     * Enum for testing enum attribute types.
     */
    public static enum TestEnum{First,Second,Third,Fourth}
    /**
     * The entity name as is used in various JPQL Queries
     */
    static final String ENTITY_NAME = "DataTypes";
}
