package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.io.File;
import java.net.URL;
import java.util.Properties;

/* $License$ */
/**
 * A factory that is used to test data conversions
 * during JMX invocations.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class JMXTestModuleFactory extends ModuleFactory
        implements JMXTestFactoryMXBean {
    @Override
    public JMXTestModule create(Object... parameters)
            throws ModuleCreationException {
        mNumInstancesCreated++;
        int i = 0;
        JMXTestModule module = new JMXTestModule((ModuleURN) parameters[i++]);
        module.setBoolean((Boolean) parameters[i++]);
        module.setPrimBoolean((Boolean)parameters[i++]);
        module.setByte((Byte) parameters[i++]);
        module.setPrimByte((Byte)parameters[i++]);
        module.setCharacter((Character) parameters[i++]);
        module.setPrimCharacter((Character) parameters[i++]);
        module.setShort((Short) parameters[i++]);
        module.setPrimShort((Short) parameters[i++]);
        module.setInt((Integer) parameters[i++]);
        module.setPrimInt((Integer) parameters[i++]);
        module.setFloat((Float) parameters[i++]);
        module.setPrimFloat((Float) parameters[i++]);
        module.setLong((Long) parameters[i++]);
        module.setPrimLong((Long) parameters[i++]);
        module.setDouble((Double) parameters[i++]);
        module.setPrimDouble((Double) parameters[i++]);
        module.setString((String) parameters[i++]);
        module.setDecimal((BigDecimal) parameters[i++]);
        module.setInteger((BigInteger) parameters[i++]);
        module.setFile(parameters[i++].toString());
        module.setURL((parameters[i++]).toString());
        module.setProperties((Properties) parameters[i]);
        module.setFactoryAnnotation(getNewInstanceAnnotation());
        return module;
    }

    public JMXTestModuleFactory() {
        super(PROVIDER_URN, TestMessages.MULTIPLE_3_PROVIDER, true, false,
                ModuleURN.class,
                Boolean.class, Boolean.TYPE,
                Byte.class, Byte.TYPE,
                Character.class, Character.TYPE,
                Short.class, Short.TYPE,
                Integer.class, Integer.TYPE,
                Float.class, Float.TYPE,
                Long.class, Long.TYPE,
                Double.class, Double.TYPE,
                String.class,
                BigDecimal.class,
                BigInteger.class,
                File.class,
                URL.class,
                Properties.class);
    }

    @Override
    public int getNumInstancesCreated() {
        return mNumInstancesCreated;
    }

    @Override
    public void resetNumInstancesCreated() {
        mNumInstancesCreated = 0;
    }

    @Override
    public String getNewInstanceAnnotation() {
        return mNewInstanceAnnotation;
    }

    @Override
    public void setNewInstanceAnnotation(String inNewInstanceAnnotation) {
        mNewInstanceAnnotation = inNewInstanceAnnotation;
    }

    private int mNumInstancesCreated = 0;
    private String mNewInstanceAnnotation = "default";
    static final ModuleURN PROVIDER_URN = new ModuleURN("metc:test:multiple3");
}
