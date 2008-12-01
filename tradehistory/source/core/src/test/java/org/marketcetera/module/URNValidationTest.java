package org.marketcetera.module;

import org.marketcetera.util.misc.ClassVersion;
import org.marketcetera.util.log.I18NMessage;
import org.marketcetera.util.log.I18NMessage1P;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

/* $License$ */
/**
 * Tests module URNs and its methods. And validations within
 * URNUtils.
 *
 * @author anshul@marketcetera.com
 */
@ClassVersion("$Id$")
public class URNValidationTest extends ModuleTestBase {
    /**
     * Tests module URN construction and string parsing.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void moduleURN() throws Exception {
        new ExpectedFailure<NullPointerException>(null){
            protected void run() throws Exception {
                new ModuleURN(null);
            }
        };
        invalidURN("");
        invalidURN(" ");
        invalidURN("   ");
        invalidURN(":");
        invalidURN(" :  ");
        invalidURN("::");
        invalidURN(" :  : ");
        invalidURN(":::");
        invalidURN(" : : : ");
        invalidURN("::::");
        invalidURN("::::blah");
        invalidURN(" : : : : ");
        invalidURN(":::::::");
        checkURN("blah", "blah",null,null,null);
        checkURN(" blah ", "blah",null,null,null);
        checkURN("blah;", "blah;",null,null,null);
        checkURN("blah:", "blah",null,null,null);
        checkURN("blah::", "blah",null,null,null);
        checkURN("blah:::", "blah",null,null,null);
        checkURN("blah::::::", "blah",null,null,null);
        checkURN("blah:a", "blah","a",null,null);
        checkURN(" blah : a ", "blah","a",null,null);
        checkURN("blah:a:b", "blah","a","b",null);
        checkURN(" blah : a : b ", "blah","a","b",null);
        checkURN("blah:a:b:c", "blah", "a", "b", "c");
        checkURN("blah:a:b:c", "blah", "a", "b", "c");
        checkURN("blah:a:b:c", "blah", "a", "b", "c");
        checkURN(" blah : a : b : c ", "blah", "a", "b", "c");
        checkURN("blah::b:c", "blah", null, "b", "c");
        checkURN("blah: :b:c", "blah", null, "b", "c");
        checkURN("blah:::c", "blah", null, null, "c");
        checkURN("blah: : :c", "blah", null, null, "c");
        checkURN(":::c", null, null, null, "c");
        checkURN(" : : : c", null, null, null, "c");
        checkURN(":bell:well:", null, "bell", "well", null);
        checkURN(" : bell : well : ", null, "bell", "well", null);
        checkURN("::well:", null, null, "well", null);
        checkURN(" : : well : ", null, null, "well", null);
        checkURN(":bell::", null, "bell", null, null);
        checkURN(" : bell : : ", null, "bell", null, null);
        ModuleURN mu = new ModuleURN("bleh:meh");
        assertFalse(mu.equals(null));
        assertFalse(mu.equals(new Object()));
        assertNull(mu.parent());
        mu = new ModuleURN(null,null,null);
        assertEquals("metc",mu.toString());
    }

    /**
     * Tests provider URN validation
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void providerURNs() throws Exception {
        validateCommonFailures(ProviderURNValidator.INSTANCE,
                Messages.INCOMPLETE_PROVIDER_URN);

        validateURNFail(ProviderURNValidator.INSTANCE,
                "metc:a2:s3:i",
                Messages.PROVIDER_URN_HAS_INSTANCE,
                "metc:a2:s3:i");

        checkProviderURN("metc:a2:s3", "a2", "s3");
        checkProviderURN("metc:type:name", "type", "name");
        checkProviderURN(" metc : type : name ", "type", "name");

        checkIsParent("metc:type","metc:type",false);
        checkIsParent("metc:type:name","metc:type:name",false);
        checkIsParent("metc:type","metc:type:name",true);
        checkIsParent("metc","metc::name",false);
        checkIsParent("metc:type","metc::name",false);
    }

    /**
     * Tests instance URN validation.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void instanceURNs() throws Exception {
        InstanceURNValidator validator = InstanceURNValidator.INSTANCE;
        validateCommonFailures(validator, Messages.INCOMPLETE_INSTANCE_URN);

        validateURNFail(validator, "metc:a2:s3",Messages.INCOMPLETE_INSTANCE_URN,"metc:a2:s3");
        validateURNFail(validator, "metc:a2:s3:5a",Messages.INVALID_INSTANCE_URN,"metc:a2:s3:5a","5a");
        validateURNFail(validator, "metc:a2:s3:a^",Messages.INVALID_INSTANCE_URN,"metc:a2:s3:a^","a^");
        validateURNFail(validator, "metc:a2:s3:b&",Messages.INVALID_INSTANCE_URN,"metc:a2:s3:b&","b&");
        validateURNFail(validator, "metc:a2:s3:this",Messages.INVALID_INSTANCE_URN,"metc:a2:s3:this","this");

        checkInstanceURN("metc:a2:s3:c4", "a2","s3","c4");
        checkInstanceURN(" metc : a2 : s3 : c4", "a2","s3","c4");
        checkInstanceURN("metc:type:name:instance", "type","name","instance");
        checkInstanceURN(" metc : type : name : instance ", "type","name","instance");

        checkIsParent("metc:type:name","metc:type:name:instance",true);
        checkIsParent("metc::name","metc::name:instance",true);
        checkIsParent("metc::name","metc:type:name:instance",false);
        checkIsParent("metc:type:name:instance","metc:type:name:instance",false);
        checkIsParent("metc:type:name","metc:type:name",false);
        checkIsParent("metc:type","metc:type:name:instance", false);
        checkIsParent("metc:type:name","metc:type::instance", false);
        checkIsParent("metc:type","metc:type::instance", false);
        validateURNFail(new URNValidator() {
            public void validate(ModuleURN inURN) throws InvalidURNException {
                ModuleURN p = new ModuleURN("metc:mytype:green");
                URNUtils.validateInstanceURN(inURN, p);
            }
        },"metc:mytype:yellow:blue", Messages.INSTANCE_PROVIDER_URN_MISMATCH,
                "metc:mytype:yellow:blue","metc:mytype:green");
    }

    /**
     * Tests construction of child URNs using a parent URN.
     *
     * @throws Exception if there were unexpected errors
     */
    @Test
    public void childURNs() throws Exception {
        ModuleURN parent = new ModuleURN("metc");
        assertFields(new ModuleURN(parent,"blah"),"metc","blah",null,null);
        assertFields(new ModuleURN(parent,""),"metc",null,null,null);
        assertFields(new ModuleURN(parent," \t "),"metc",null,null,null);
        assertFields(new ModuleURN(parent,"blah:what"),"metc","blah",null,null);
        assertFields(new ModuleURN(parent,"blah : what"),"metc","blah",null,null);
        assertFields(new ModuleURN(parent," blah : what "),"metc","blah",null,null);
        assertFields(new ModuleURN(parent," blah : what : why : when :"),"metc","blah",null,null);
        assertFields(new ModuleURN(parent,":blah"),"metc",null,null,null);
        assertFields(new ModuleURN(parent,"  : blah"),"metc",null,null,null);

        parent = new ModuleURN("metc:type");
        assertFields(new ModuleURN(parent,"blah"),"metc","type","blah",null);
        assertFields(new ModuleURN(parent,""),"metc","type",null,null);
        assertFields(new ModuleURN(parent," \t "),"metc","type",null,null);
        assertFields(new ModuleURN(parent,"blah:what"),"metc","type","blah",null);
        assertFields(new ModuleURN(parent,"blah : what"),"metc","type","blah",null);
        assertFields(new ModuleURN(parent," blah : what "),"metc","type","blah",null);
        assertFields(new ModuleURN(parent," blah : what : why : when :"),"metc","type","blah",null);
        assertFields(new ModuleURN(parent,":blah"),"metc","type",null,null);
        assertFields(new ModuleURN(parent,"  : blah"),"metc","type",null,null);

        parent = new ModuleURN("metc:type:name");
        assertFields(new ModuleURN(parent,"blah"),"metc","type","name","blah");
        assertFields(new ModuleURN(parent,""),"metc","type","name",null);
        assertFields(new ModuleURN(parent," \t "),"metc","type","name",null);
        assertFields(new ModuleURN(parent,"blah:what"),"metc","type","name","blah");
        assertFields(new ModuleURN(parent,"blah : what"),"metc","type","name","blah");
        assertFields(new ModuleURN(parent," blah : what "),"metc","type","name","blah");
        assertFields(new ModuleURN(parent," blah : what : why : when :"),"metc","type","name","blah");
        assertFields(new ModuleURN(parent,":blah"),"metc","type","name",null);
        assertFields(new ModuleURN(parent,"  : blah"),"metc","type","name",null);

        parent = new ModuleURN("metc:type:name:blah");
        assertFields(new ModuleURN(parent,"blah"),"metc","type","name","blah");
        assertFields(new ModuleURN(parent,""),"metc","type","name","blah");
        assertFields(new ModuleURN(parent," \t "),"metc","type","name","blah");
        assertFields(new ModuleURN(parent,"blah:what"),"metc","type","name","blah");
        assertFields(new ModuleURN(parent,"blah : what"),"metc","type","name","blah");
        assertFields(new ModuleURN(parent," blah : what "),"metc","type","name","blah");
        assertFields(new ModuleURN(parent," blah : what : why : when :"),"metc","type","name","blah");
        assertFields(new ModuleURN(parent,":blah"),"metc","type","name","blah");
        assertFields(new ModuleURN(parent,"  : blah"),"metc","type","name","blah");
    }

    /**
     * Tests processing of URNs that substitutes 'this' keyword with
     * requesting module URN's respective field value.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void processURN() throws Exception {
        checkProcessURN("metc:blah:blah:blah",null,"metc:blah:blah:blah");
        checkProcessURN("metc:this:this:this",null,"metc:this:this:this");

        checkProcessURN("metc:blah:blah:blah","metc:yes:no:false","metc:blah:blah:blah");

        checkProcessURN("metc:blah:blah:this","metc:yes:no:false","metc:blah:blah:false");
        checkProcessURN("metc:blah:this:blah","metc:yes:no:false","metc:blah:no:blah");
        checkProcessURN("metc:this:blah:blah","metc:yes:no:false","metc:yes:blah:blah");

        checkProcessURN("metc:this:this:blah","metc:yes:no:false","metc:yes:no:blah");
        checkProcessURN("metc:this:blah:this","metc:yes:no:false","metc:yes:blah:false");
        checkProcessURN("metc:blah:this:this","metc:yes:no:false","metc:blah:no:false");

        checkProcessURN("metc:this:this:this","metc:yes:no:false","metc:yes:no:false");
        checkProcessURN("metc:::this","metc:yes:no:false","metc:::false");
        checkProcessURN("metc::this","metc:yes:no:false","metc::no");
        checkProcessURN("metc:this","metc:yes:no:false","metc:yes");

        checkProcessURN("metc:this:this:this","metc:yes","metc:yes");
        checkProcessURN("metc:this:this:this","metc:yes:no","metc:yes:no");
        checkProcessURN("metc:this:this:this","metc:yes::false","metc:yes::false");

        processURNFailure("blah",null,Messages.INVALID_URN_SCHEME,"blah","blah",ModuleURN.SCHEME);
        processURNFailure("this","metc:what",Messages.INVALID_URN_SCHEME,"this","this",ModuleURN.SCHEME);
        processURNFailure("metc","metc:yes:no:false",Messages.INCOMPLETE_INSTANCE_URN,"metc");
        processURNFailure("metc:this:this:this","metc",Messages.INCOMPLETE_INSTANCE_URN,"metc:this:this:this");
    }

    /**
     * Tests conversion of a URN into its equivalent JMX Object Name.
     *
     * @throws Exception if there were unexpected errors.
     */
    @Test
    public void objectName() throws Exception {
        checkObjectName("metc:my:game:kong","org.marketcetera.module:type=my,provider=game,name=kong");
        checkObjectName("metc:::kong","org.marketcetera.module:name=kong");
        checkObjectName("metc::same:","org.marketcetera.module:provider=same");
        checkObjectName("metc:what::","org.marketcetera.module:type=what");
        checkObjectName("metc:strategy:java","org.marketcetera.module:type=strategy,provider=java");
        checkObjectName("metc:strategy::billion","org.marketcetera.module:type=strategy,name=billion");
        checkObjectName("metc::java:billion","org.marketcetera.module:provider=java,name=billion");
        new ExpectedFailure<MXBeanOperationException>(null){
            protected void run() throws Exception {
                new ModuleURN("metc").toObjectName();
            }
        };
    }
    private void checkObjectName(String inURN, String inObjectName)
            throws MXBeanOperationException {
        ModuleURN u = new ModuleURN(inURN);
        assertEquals(inObjectName, u.toObjectName().toString());
    }
    private void checkProcessURN(String inURN, String inRequester,
                              String inExpected)
            throws InvalidURNException {
        ModuleURN u = new ModuleURN(inURN);
        ModuleURN req = null;
        if(inRequester != null) {
            req = new ModuleURN(inRequester);
        }
        ModuleURN urn = URNUtils.processURN(req, u);
        assertEquals(new ModuleURN(inExpected), urn);
    }
    private void processURNFailure(final String inURN,
                                   final String inRequester,
                                   I18NMessage inMsg,
                                   Object... inParams) throws Exception {
        new ExpectedFailure<InvalidURNException>(inMsg,inParams){
            protected void run() throws Exception {
                checkProcessURN(inURN, inRequester, null);
            }
        };
    }

    private void invalidURN(final String s) throws Exception {
        new ExpectedFailure<IllegalArgumentException>(
                Messages.EMPTY_URN.getText(s)){
            protected void run() throws Exception {
                new ModuleURN(s);
            }
        };
    }

    private void checkURN(String urn, String scheme, String type,
                          String name, String instance) {
        ModuleURN u = new ModuleURN(urn);
        assertFields(u, scheme, type, name, instance);

    }
    private void assertFields(ModuleURN inURN, String scheme,
                              String type, String name,
                              String instance) {
        assertEquals(scheme, inURN.scheme());
        assertEquals(type, inURN.providerType());
        assertEquals(name, inURN.providerName());
        assertEquals(instance, inURN.instanceName());
        StringBuilder sb = new StringBuilder();
        if(instance != null) {
            sb.insert(0, instance);
            sb.insert(0, ":");
        }
        if(name != null) {
            sb.insert(0, name);
        }
        if(name != null || instance != null) {
            sb.insert(0, ":");
        }
        if(type != null) {
            sb.insert(0, type);
        }
        if(name != null || instance != null || type != null) {
            sb.insert(0, ":");
        }
        if(scheme != null) {
            sb.insert(0,scheme);
        }
        ModuleURN u = new ModuleURN(sb.toString());
        assertEquals(inURN, u);
        assertEquals(inURN.hashCode(), u.hashCode());
        assertEquals(inURN.getValue(), u.getValue());
        assertEquals(inURN.toString(), u.toString());
    }
    private void checkIsParent(String inParent, String inChild, boolean inExpected) {
        assertEquals(inExpected, new ModuleURN(inParent).parentOf(new ModuleURN(inChild)));
    }

    private void validateCommonFailures(URNValidator inValidator, I18NMessage1P inIncompleteURNMessage) throws Exception {
        validateURNFail(inValidator,null, Messages.EMPTY_URN, "");
        validateURNFail(inValidator,"blah", Messages.INVALID_URN_SCHEME, "blah", "blah",ModuleURN.SCHEME);
        validateURNFail(inValidator,"blah:", Messages.INVALID_URN_SCHEME, "blah", "blah", ModuleURN.SCHEME);
        validateURNFail(inValidator,"metc:", inIncompleteURNMessage, "metc");
        validateURNFail(inValidator,"metc:2a", Messages.INVALID_PROVIDER_TYPE,"metc:2a","2a");
        validateURNFail(inValidator,"metc:a-", Messages.INVALID_PROVIDER_TYPE,"metc:a-","a-");
        validateURNFail(inValidator,"metc:a!", Messages.INVALID_PROVIDER_TYPE,"metc:a!","a!");
        validateURNFail(inValidator,"metc:a2:", inIncompleteURNMessage,"metc:a2");
        validateURNFail(inValidator,"metc:a2:3s", Messages.INVALID_PROVIDER_NAME,"metc:a2:3s","3s");
        validateURNFail(inValidator,"metc:a2:s#", Messages.INVALID_PROVIDER_NAME,"metc:a2:s#","s#");
        validateURNFail(inValidator,"metc:a2:s%", Messages.INVALID_PROVIDER_NAME,"metc:a2:s%","s%");
        validateURNFail(inValidator, "metc:this:a2",Messages.INVALID_PROVIDER_TYPE,"metc:this:a2","this");
        validateURNFail(inValidator, "metc:a2:this",Messages.INVALID_PROVIDER_NAME,"metc:a2:this","this");
    }

    private void checkProviderURN(String urn,
                                  String provType,
                                  String provName)
            throws InvalidURNException {
        ModuleURN u = new ModuleURN(urn);
        URNUtils.validateProviderURN(u);
        assertEquals(provType, u.providerType());
        assertEquals(provName, u.providerName());
        assertFalse(u.instanceURN());
        assertEquals(u, new ModuleURN(provType, provName, null));
        ModuleURN p = new ModuleURN(provType, null, null);
        assertTrue(p.parentOf(u));
        assertEquals(p, u.parent());
    }
    private void checkInstanceURN(String inURN, String provType,
                                  String provName, String instanceName)
            throws InvalidURNException {
        ModuleURN u = new ModuleURN(inURN);
        URNUtils.validateInstanceURN(u);
        assertEquals(provType, u.providerType());
        assertEquals(provName, u.providerName());
        assertEquals(instanceName, u.instanceName());
        assertTrue(u.instanceURN());
        assertEquals(u, new ModuleURN(provType, provName, instanceName));
        ModuleURN p = new ModuleURN(provType, provName, null);
        assertTrue(p.parentOf(u));
        assertEquals(p, u.parent());
        assertFalse(p.instanceURN());
        URNUtils.validateInstanceURN(u, p);
        URNUtils.validateProviderURN(p);
    }

    private void validateURNFail(final URNValidator inValidator,
                                 final String inURN,
                                 I18NMessage inMsg,
                                 Object... params) throws Exception {
        new ExpectedFailure<InvalidURNException>(inMsg, params){
            protected void run() throws Exception {
                ModuleURN urn = null;
                if (inURN != null) {
                    urn = new ModuleURN(inURN);
                }
                inValidator.validate(urn);
            }
        };
    }
    private static interface URNValidator {
        public void validate(ModuleURN inURN) throws InvalidURNException;
    }
    private static class ProviderURNValidator implements URNValidator {
        public void validate(ModuleURN inURN) throws InvalidURNException {
            URNUtils.validateProviderURN(inURN);
        }
        static final ProviderURNValidator INSTANCE = new ProviderURNValidator();
    }
    private static class InstanceURNValidator implements URNValidator {
        public void validate(ModuleURN inURN) throws InvalidURNException {
            URNUtils.validateInstanceURN(inURN);
        }
        static final InstanceURNValidator INSTANCE = new InstanceURNValidator();
    }
}
