package org.marketcetera.algo;

import static org.junit.Assert.*;

import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.CoreException;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.Validator;
import org.marketcetera.module.ExpectedFailure;
import org.marketcetera.trade.Factory;
import org.marketcetera.trade.NewOrReplaceOrder;
import org.marketcetera.trade.OrderSingle;
import org.marketcetera.util.test.EqualityAssert;

/* $License$ */

/**
 * Tests broker algorithm classes.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since 2.4.0
 */
public class BrokerAlgoTest
{
    /**
     * Runs once before all tests.
     *
     * @throws Exception if an unexpected error occurs
     */
    @BeforeClass
    public static void once()
            throws Exception
    {
        LoggerConfiguration.logSetup();
        context = JAXBContext.newInstance(BrokerAlgoTagSpec.class,BrokerAlgoTag.class,BrokerAlgo.class,BrokerAlgoSpec.class);
        marshaller = context.createMarshaller();
        unmarshaller = context.createUnmarshaller();
        unmarshaller.setEventHandler(new ValidationEventHandler() {
            @Override
            public boolean handleEvent(ValidationEvent inEvent)
            {
                throw new RuntimeException(inEvent.getMessage(),
                                           inEvent.getLinkedException());
            }
        });
    }
    /**
     * Tests serialization/unserialization and marshalling/unmarshalling.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testSerializeAndMarshal()
            throws Exception
    {
        // generate a test algo
        BrokerAlgoSpec testSpec = generateAlgoSpec();
        // bind the test spec with values
        Set<BrokerAlgoTag> boundTags = generateTagsFor(testSpec);
        BrokerAlgo boundAlgo = new BrokerAlgo(testSpec,
                                              boundTags);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream testStream = new ObjectOutputStream(output);
        // serialize the algo
        testStream.writeObject(boundAlgo);
        // unserialize and compare
        ByteArrayInputStream input = new ByteArrayInputStream(output.toByteArray());
        ObjectInputStream inputStream = new ObjectInputStream(input);
        BrokerAlgo algoCopy = (BrokerAlgo)inputStream.readObject();
        assertEquals(boundAlgo,
                     algoCopy);
        // check validators
        assertNotNull(boundAlgo.getAlgoSpec().getValidator());
        assertNull(algoCopy.getAlgoSpec().getValidator());
        for(BrokerAlgoTagSpec tagSpec : boundAlgo.getAlgoSpec().getAlgoTagSpecs()) {
            assertNotNull(tagSpec.getValidator());
        }
        for(BrokerAlgoTagSpec tagSpec : algoCopy.getAlgoSpec().getAlgoTagSpecs()) {
            assertNull(tagSpec.getValidator());
        }
        // marshal
        StringWriter outputWriter = new StringWriter();
        marshaller.marshal(boundAlgo,
                           outputWriter);
        // unmarshal and compare
        algoCopy = (BrokerAlgo)unmarshaller.unmarshal(new InputStreamReader(new ByteArrayInputStream(outputWriter.toString().getBytes())));
        assertEquals(boundAlgo,
                     algoCopy);
    }
    /**
     * Tests getters and setters.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testTagSpecGettersAndSetters()
            throws Exception
    {
        final BrokerAlgoTagSpec testSpec = new BrokerAlgoTagSpec();
        verifyTagSpec(0,
                      null,
                      null,
                      null,
                      false,
                      null,
                      null,
                      testSpec);
        testSpec.setTag(Integer.MIN_VALUE);
        verifyTagSpec(Integer.MIN_VALUE,
                      null,
                      null,
                      null,
                      false,
                      null,
                      null,
                      testSpec);
        testSpec.setTag(Integer.MAX_VALUE);
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      null,
                      null,
                      false,
                      null,
                      null,
                      testSpec);
        testSpec.setDescription(null);
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      null,
                      null,
                      false,
                      null,
                      null,
                      testSpec);
        testSpec.setDescription("");
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      null,
                      null,
                      false,
                      null,
                      null,
                      testSpec);
        testSpec.setDescription("    ");
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      null,
                      null,
                      false,
                      null,
                      null,
                      testSpec);
        testSpec.setDescription("  description  ");
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      null,
                      false,
                      null,
                      null,
                      testSpec);
        testSpec.setIsMandatory(true);
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      null,
                      true,
                      null,
                      null,
                      testSpec);
        testSpec.setPattern(null);
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      null,
                      true,
                      null,
                      null,
                      testSpec);
        testSpec.setPattern("");
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      null,
                      true,
                      null,
                      null,
                      testSpec);
        testSpec.setPattern("    ");
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      null,
                      true,
                      null,
                      null,
                      testSpec);
        new ExpectedFailure<IllegalArgumentException>() {
            @Override
            protected void run()
                    throws Exception
            {
                testSpec.setPattern("(not a valid pattern");
            }
        };
        testSpec.setPattern("  \\d*  ");
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      "\\d*",
                      true,
                      null,
                      null,
                      testSpec);
        testSpec.setValidator(null);
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      "\\d*",
                      true,
                      null,
                      null,
                      testSpec);
        testSpec.setValidator(tagValidator);
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      "\\d*",
                      true,
                      null,
                      tagValidator,
                      testSpec);
        testSpec.setLabel(null);
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      "\\d*",
                      true,
                      null,
                      tagValidator,
                      testSpec);
        testSpec.setLabel("");
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      "\\d*",
                      true,
                      null,
                      tagValidator,
                      testSpec);
        testSpec.setLabel("    ");
        verifyTagSpec(Integer.MAX_VALUE,
                      null,
                      "description",
                      "\\d*",
                      true,
                      null,
                      tagValidator,
                      testSpec);
        testSpec.setLabel("    label    ");
        verifyTagSpec(Integer.MAX_VALUE,
                      "label",
                      "description",
                      "\\d*",
                      true,
                      null,
                      tagValidator,
                      testSpec);
        testSpec.setOptions(null);
        verifyTagSpec(Integer.MAX_VALUE,
                      "label",
                      "description",
                      "\\d*",
                      true,
                      null,
                      tagValidator,
                      testSpec);
        Map<String,String> options = new HashMap<String,String>();
        for(int i=0;i<3;i++) {
            options.put("Option " + counter.incrementAndGet(),
                        "value-" + counter.incrementAndGet());
        }
        testSpec.setOptions(options);
        verifyTagSpec(Integer.MAX_VALUE,
                      "label",
                      "description",
                      "\\d*",
                      true,
                      options,
                      tagValidator,
                      testSpec);
    }
    /**
     * Tests <code>BrokerAlgoTagSpec</code> hashcode and equals.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testTagSpecHashcodeAndEquals()
            throws Exception
    {
        BrokerAlgoTagSpec testSpec = generateTagSpec();
        BrokerAlgoTagSpec otherSpec = generateTagSpec();
        otherSpec.setTag(testSpec.getTag());
        otherSpec.setIsMandatory(!testSpec.getIsMandatory());
        otherSpec.setValidator(null);
        otherSpec.setPattern(null);
        assertNotNull(testSpec.getValidator());
        assertFalse(testSpec.getDescription().equals(otherSpec.getDescription()));
        assertFalse(testSpec.getPattern().equals(otherSpec.getPattern()));
        assertFalse(testSpec.getIsMandatory() == otherSpec.getIsMandatory());
        assertFalse(testSpec.getValidator().equals(otherSpec.getValidator()));
        EqualityAssert.assertEquality(testSpec,
                                      otherSpec,
                                      null,
                                      this);
    }
    /**
     * Tests comparable behavior of <code>BrokerAlgoTagSpec</code>.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testTagSpecComparable()
            throws Exception
    {
        BrokerAlgoTagSpec testSpec = generateTagSpec();
        BrokerAlgoTagSpec otherSpec = generateTagSpec();
        assertTrue(testSpec.getTag() < otherSpec.getTag());
        assertEquals(-1,
                     testSpec.compareTo(otherSpec));
        assertEquals(1,
                     otherSpec.compareTo(testSpec));
        otherSpec.setTag(testSpec.getTag());
        assertEquals(0,
                     otherSpec.compareTo(testSpec));
    }
    /**
     * Tests constructors for <code>BrokerAlgoTag</code>.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testTagConstructors()
            throws Exception
    {
        BrokerAlgoTag testObject = new BrokerAlgoTag();
        verifyTag(null,
                  null,
                  testObject);
        BrokerAlgoTagSpec testSpec = generateTagSpec();
        testObject = new BrokerAlgoTag(testSpec);
        verifyTag(testSpec,
                  null,
                  testObject);
        testObject = new BrokerAlgoTag(testSpec,
                                       "value");
        verifyTag(testSpec,
                  "value",
                  testObject);
    }
    /**
     * Tests <code>BrokerAlgoTag</code> getters and setters.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testTagGettersAndSetters()
            throws Exception
    {
        BrokerAlgoTag testObject = new BrokerAlgoTag();
        verifyTag(null,
                  null,
                  testObject);
        BrokerAlgoTagSpec testSpec = generateTagSpec();
        testObject.setTagSpec(testSpec);
        verifyTag(testSpec,
                  null,
                  testObject);
        testObject.setTagSpec(null);
        verifyTag(null,
                  null,
                  testObject);
        testObject.setValue("");
        verifyTag(null,
                  null,
                  testObject);
        testObject.setValue("    ");
        verifyTag(null,
                  null,
                  testObject);
        testObject.setValue("  value  ");
        verifyTag(null,
                  "value",
                  testObject);
    }
    /**
     * Tests <code>BrokerAlgoTag</code> equals and hashcode.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testTagEquality()
            throws Exception
    {
        BrokerAlgoTagSpec testSpec1 = generateTagSpec();
        BrokerAlgoTagSpec testSpec2 = generateTagSpec();
        assertFalse(testSpec1.equals(testSpec2));
        BrokerAlgoTag testTag = new BrokerAlgoTag(testSpec1,
                                                  "value");
        BrokerAlgoTag equalTag = new BrokerAlgoTag(testSpec1,
                                                   "value");
        BrokerAlgoTag unequalTag1 = new BrokerAlgoTag();
        BrokerAlgoTag unequalTag2 = new BrokerAlgoTag(testSpec1);
        BrokerAlgoTag unequalTag3 = new BrokerAlgoTag(testSpec1,
                                                      "othervalue");
        BrokerAlgoTag unequalTag4 = new BrokerAlgoTag(testSpec1,
                                                      null);
        BrokerAlgoTag unequalTag5 = new BrokerAlgoTag(testSpec2,
                                                      "value");
        EqualityAssert.assertEquality(testTag,
                                      equalTag,
                                      null,
                                      this,
                                      unequalTag1,
                                      unequalTag2,
                                      unequalTag3,
                                      unequalTag4,
                                      unequalTag5);
    }
    /**
     * Tests <code>BrokerAlgoTag</code> validation.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testTagValidate()
            throws Exception
    {
        BrokerAlgoTagSpec testSpec = generateTagSpec();
        testSpec.setValidator(null);
        testSpec.setPattern(null);
        testSpec.setIsMandatory(false);
        testSpec.setLabel("some label");
        final BrokerAlgoTag testTag = new BrokerAlgoTag(testSpec);
        assertNull(testTag.getValue());
        testTag.validate();
        testSpec.setIsMandatory(true);
        new ExpectedFailure<CoreException>(Messages.ALGO_TAG_VALUE_REQUIRED,
                                           testSpec.getLabel()) {
            @Override
            protected void run()
                    throws Exception
            {
                testTag.validate();
            }
        };
        testTag.setValue("value");
        testTag.validate();
        testSpec.setPattern("\\d*");
        new ExpectedFailure<CoreException>(Messages.ALGO_TAG_VALUE_PATTERN_MISMATCH,
                                           testSpec.getLabel(),
                                           testTag.getValue()) {
            @Override
            protected void run()
                    throws Exception
            {
                testTag.validate();
            }
        };
        testSpec.setPattern("\\w*");
        testTag.validate();
        testSpec.setValidator(tagValidator);
        testTag.validate();
        testSpec.setValidator(new Validator<BrokerAlgoTag>() {
            @Override
            public void validate(BrokerAlgoTag inData)
            {
                throw new CoreException();
            }
        });
        new ExpectedFailure<CoreException>() {
            @Override
            protected void run()
                    throws Exception
            {
                testTag.validate();
            }
        };
    }
    /**
     * Tests getters and setters of <code>BrokerAlgoSpec</code>.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAlgoSpecGettersAndSetters()
            throws Exception
    {
        BrokerAlgoSpec testSpec = new BrokerAlgoSpec();
        verifyAlgoSpec(null,
                       null,
                       null,
                       testSpec);
        testSpec.setName(null);
        verifyAlgoSpec(null,
                       null,
                       null,
                       testSpec);
        testSpec.setName("");
        verifyAlgoSpec(null,
                       null,
                       null,
                       testSpec);
        testSpec.setName("    ");
        verifyAlgoSpec(null,
                       null,
                       null,
                       testSpec);
        testSpec.setName("   name   ");
        verifyAlgoSpec("name",
                       null,
                       null,
                       testSpec);
        BrokerAlgoTagSpec spec1 = generateTagSpec();
        BrokerAlgoTagSpec spec2 = generateTagSpec();
        BrokerAlgoTagSpec spec3 = generateTagSpec();
        Set<BrokerAlgoTagSpec> specs = new HashSet<BrokerAlgoTagSpec>();
        specs.add(spec1);
        specs.add(spec2);
        specs.add(spec3);
        testSpec.setAlgoTagSpecs(specs);
        verifyAlgoSpec("name",
                       specs,
                       null,
                       testSpec);
        testSpec.setValidator(null);
        verifyAlgoSpec("name",
                       specs,
                       null,
                       testSpec);
        testSpec.setValidator(algoValidator);
        verifyAlgoSpec("name",
                       specs,
                       algoValidator,
                       testSpec);
    }
    /**
     * Tests <code>BrokerAlgoSpec</code> equals and hashcode.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAlgoSpecEquality()
            throws Exception
    {
        BrokerAlgoSpec testSpec = generateAlgoSpec();
        BrokerAlgoSpec equalSpec = generateAlgoSpec();
        assertFalse(testSpec.getAlgoTagSpecs().equals(equalSpec.getAlgoTagSpecs()));
        equalSpec.setName(testSpec.getName());
        EqualityAssert.assertEquality(testSpec,
                                      equalSpec,
                                      null,
                                      this);
    }
    /**
     * Tests <code>BrokerAlgoSpec</code> comparable behavior.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAlgoComparable()
            throws Exception
    {
        BrokerAlgoSpec testSpec = generateAlgoSpec();
        BrokerAlgoSpec otherSpec = generateAlgoSpec();
        assertFalse(testSpec.getName().equals(otherSpec.getName()));
        assertEquals(testSpec.getName().compareTo(otherSpec.getName()),
                     testSpec.compareTo(otherSpec));
        assertEquals(otherSpec.getName().compareTo(testSpec.getName()),
                     otherSpec.compareTo(testSpec));
        assertEquals(0,
                     testSpec.compareTo(testSpec));
    }
    /**
     * Tests <code>BrokerAlgo</code> constructors.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAlgoConstructors()
            throws Exception
    {
        BrokerAlgo testAlgo = new BrokerAlgo();
        verifyAlgo(null,
                   null,
                   testAlgo);
        BrokerAlgoSpec algoSpec = generateAlgoSpec();
        testAlgo = new BrokerAlgo(algoSpec);
        verifyAlgo(algoSpec,
                   null,
                   testAlgo);
        Set<BrokerAlgoTag> boundTags = generateTagsFor(algoSpec);
        testAlgo = new BrokerAlgo(algoSpec,
                                  boundTags);
        verifyAlgo(algoSpec,
                   boundTags,
                   testAlgo);
    }
    /**
     * Tests getters and setters for <code>BrokerAlgo</code>.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAlgoGettersAndSetters()
            throws Exception
    {
        BrokerAlgo testAlgo = new BrokerAlgo();
        verifyAlgo(null,
                   null,
                   testAlgo);
        BrokerAlgoSpec algoSpec = generateAlgoSpec();
        testAlgo.setAlgoSpec(algoSpec);
        verifyAlgo(algoSpec,
                   null,
                   testAlgo);
        Set<BrokerAlgoTag> boundTags = generateTagsFor(algoSpec);
        testAlgo.setAlgoTags(boundTags);
        verifyAlgo(algoSpec,
                   boundTags,
                   testAlgo);
    }
    /**
     * Tests <code>BrokerAlgo</code> hashcode and equals.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAlgoEquality()
            throws Exception
    {
        BrokerAlgoSpec testSpec1 = generateAlgoSpec();
        BrokerAlgoSpec testSpec2 = generateAlgoSpec();
        Set<BrokerAlgoTag> boundTags1 = generateTagsFor(testSpec1);
        Set<BrokerAlgoTag> boundTags2 = generateTagsFor(testSpec2);
        assertFalse(testSpec1.equals(testSpec2));
        BrokerAlgo testAlgo = new BrokerAlgo(testSpec1,
                                             boundTags1);
        BrokerAlgo equalAlgo = new BrokerAlgo(testSpec1,
                                              boundTags1);
        BrokerAlgo unequalAlgo1 = new BrokerAlgo();
        BrokerAlgo unequalAlgo2 = new BrokerAlgo(testSpec1);
        BrokerAlgo unequalAlgo3 = new BrokerAlgo(testSpec1,
                                                 boundTags2);
        BrokerAlgo unequalAlgo4 = new BrokerAlgo(testSpec2,
                                                 boundTags1);
        EqualityAssert.assertEquality(testAlgo,
                                      equalAlgo,
                                      null,
                                      this,
                                      unequalAlgo1,
                                      unequalAlgo2,
                                      unequalAlgo3,
                                      unequalAlgo4);
    }
    /**
     * Tests {@link BrokerAlgo#applyTo(org.marketcetera.trade.NewOrReplaceOrder)}
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testAlgoApplyToOrder()
            throws Exception
    {
        BrokerAlgoSpec testSpec = generateAlgoSpec();
        final BrokerAlgo testAlgo = new BrokerAlgo();
        testAlgo.applyTo(null); // nothing happens
        Set<BrokerAlgoTag> boundTags = generateTagsFor(testSpec);
        testAlgo.setAlgoSpec(testSpec);
        testAlgo.applyTo(null); // still, nothing happens
        testAlgo.setAlgoTags(boundTags);
        // now, with bound tags, bad things happen with a null order
        new ExpectedFailure<NullPointerException>() {
            @Override
            protected void run()
                    throws Exception
            {
                testAlgo.applyTo(null);
            }
        };
        // test with empty but non-null bound tags
        testAlgo.setAlgoTags(new HashSet<BrokerAlgoTag>());
        doOneApplyTest(testAlgo,
                       Factory.getInstance().createOrderSingle());
        // test with non-empty bound tags
        testAlgo.setAlgoTags(boundTags);
        doOneApplyTest(testAlgo,
                       Factory.getInstance().createOrderSingle());
        // repeat with replace order
        doOneApplyTest(testAlgo,
                       Factory.getInstance().createOrderReplace(null));
        // create an order with pre-existing custom tags, make sure these are retained
        OrderSingle order = Factory.getInstance().createOrderSingle();
        Map<String,String> extraTags = new HashMap<String,String>();
        // make sure there is no overlap between my tags and the algo tags
        extraTags.put(String.valueOf(counter.incrementAndGet()),
                      "custom-value-" + counter.incrementAndGet());
        extraTags.put(String.valueOf(counter.incrementAndGet()),
                      "custom-value-" + counter.incrementAndGet());
        order.setCustomFields(extraTags);
        doOneApplyTest(testAlgo,
                       order);
        // run again, this time make sure there *is* overlap
        extraTags.put(String.valueOf(boundTags.iterator().next().getTagSpec().getTag()),
                      "some-new-value-" + counter.incrementAndGet());
        order.setCustomFields(extraTags);
        doOneApplyTest(testAlgo,
                       order);
    }
    /**
     * Tests {@link BrokerAlgo#mapValidatorsFrom(BrokerAlgoSpec)}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBrokerAlgoValidatorMapping()
            throws Exception
    {
        BrokerAlgoSpec testAlgoSpec = generateAlgoSpec();
        assertNotNull(testAlgoSpec.getValidator());
        for(BrokerAlgoTagSpec tagSpec : testAlgoSpec.getAlgoTagSpecs()) {
            assertNotNull(tagSpec.getValidator());
        }
        // the algo spec above has a non-null validator and each of its tag specs has a non-null validator
        // now, generate an algo based on this spec
        Set<BrokerAlgoTag> boundTags = generateTagsFor(testAlgoSpec);
        BrokerAlgo testAlgo = new BrokerAlgo(testAlgoSpec,
                                             boundTags);
        // at this time, this algo also has non-null validators, so mapping it isn't necessary
        // run it through a marshal/unmarshal cycle, which will strip its validators
        StringWriter outputWriter = new StringWriter();
        marshaller.marshal(testAlgo,
                           outputWriter);
        // unmarshal
        final BrokerAlgo strippedAlgo = (BrokerAlgo)unmarshaller.unmarshal(new InputStreamReader(new ByteArrayInputStream(outputWriter.toString().getBytes())));
        // verify that the validators are gone
        assertNull(strippedAlgo.getAlgoSpec().getValidator());
        for(BrokerAlgoTag tag : strippedAlgo.getAlgoTags()) {
            assertNull(tag.getTagSpec().getValidator());
        }
        // first, map the wrong spec
        final BrokerAlgoSpec wrongAlgoSpec = generateAlgoSpec();
        assertFalse(testAlgoSpec.equals(wrongAlgoSpec));
        new ExpectedFailure<CoreException>(Messages.ALGO_SPEC_MISMATCH,
                                           testAlgoSpec.getName(),
                                           wrongAlgoSpec.getName()) {
            @Override
            protected void run()
                    throws Exception
            {
                strippedAlgo.mapValidatorsFrom(wrongAlgoSpec);
            }
        };
        assertNull(strippedAlgo.getAlgoSpec().getValidator());
        for(BrokerAlgoTag tag : strippedAlgo.getAlgoTags()) {
            assertNull(tag.getTagSpec().getValidator());
        }
        // does nothing if the algo spec is null
        strippedAlgo.setAlgoSpec(null);
        strippedAlgo.mapValidatorsFrom(testAlgoSpec);
        for(BrokerAlgoTag tag : strippedAlgo.getAlgoTags()) {
            assertNull(tag.getTagSpec().getValidator());
        }
        // put the algo spec back
        strippedAlgo.setAlgoSpec(testAlgoSpec);
        // remove the algo tags
        Set<BrokerAlgoTag> unmappedBoundTags = strippedAlgo.getAlgoTags();
        strippedAlgo.setAlgoTags(null);
        // map again
        strippedAlgo.mapValidatorsFrom(testAlgoSpec);
        // top-level validator is there, but no validators for the (non-existent) tag specs
        assertSame(testAlgoSpec.getValidator(),
                   strippedAlgo.getAlgoSpec().getValidator());
        assertNull(strippedAlgo.getAlgoTags());
        // put the tags back
        strippedAlgo.setAlgoTags(unmappedBoundTags);
        // reset for the next test
        strippedAlgo.getAlgoSpec().setValidator(null);
        // remove the spec tags
        Set<BrokerAlgoTagSpec> tagSpecs = testAlgoSpec.getAlgoTagSpecs();
        testAlgoSpec.setAlgoTagSpecs(null);
        // map again
        strippedAlgo.mapValidatorsFrom(testAlgoSpec);
        // top-level validator is there, no validators for the tag specs
        assertSame(testAlgoSpec.getValidator(),
                   strippedAlgo.getAlgoSpec().getValidator());
        for(BrokerAlgoTag tag : strippedAlgo.getAlgoTags()) {
            assertNull(tag.getTagSpec().getValidator());
        }
        // put the tag specs back
        testAlgoSpec.setAlgoTagSpecs(tagSpecs);
        // reset for the next test
        strippedAlgo.getAlgoSpec().setValidator(null);
        // set a single tag spec validator to null for completeness
        testAlgoSpec.getAlgoTagSpecs().iterator().next().setValidator(null);
        // do the successful, complete mapping
        strippedAlgo.mapValidatorsFrom(testAlgoSpec);
        verifyAlgo(testAlgoSpec,
                   boundTags,
                   strippedAlgo);
    }
    /**
     * Tests {@link BrokerAlgo#validate()}.
     *
     * @throws Exception if an unexpected error occurs
     */
    @Test
    public void testBrokerAlgoValidation()
            throws Exception
    {
        final BrokerAlgo testAlgo = new BrokerAlgo();
        assertNull(testAlgo.getAlgoSpec());
        assertNull(testAlgo.getAlgoTags());
        // validate with no spec or tags (so, no validators)
        testAlgo.validate();
        BrokerAlgoSpec testAlgoSpec = generateAlgoSpec();
        Set<BrokerAlgoTag> boundTags = generateTagsFor(testAlgoSpec);
        testAlgo.setAlgoTags(boundTags);
        assertNotNull(testAlgoSpec.getValidator());
        testAlgo.setAlgoSpec(testAlgoSpec);
        // validate with top-level no-op validator and no-op validators for each tag spec
        testAlgo.validate();
        // validate with null top-level validator and no-op validators for each tag spec
        testAlgoSpec.setValidator(null);
        testAlgo.validate();
        // set the top-level validator to fail
        testAlgoSpec.setValidator(new Validator<BrokerAlgo>() {
            @Override
            public void validate(BrokerAlgo inData)
            {
                throw new RuntimeException("this exception is expected");
            }});
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                testAlgo.validate();
            }
        };
        testAlgoSpec.setValidator(null);
        // verify each validator gets called
        final AtomicBoolean topLevelValidatorCalled = new AtomicBoolean(false);
        AtomicBoolean[] tagValidatorsCalled = new AtomicBoolean[testAlgoSpec.getAlgoTagSpecs().size()];
        for(int i=0;i<tagValidatorsCalled.length;i++) {
            tagValidatorsCalled[i] = new AtomicBoolean(false);
        }
        testAlgoSpec.setValidator(new Validator<BrokerAlgo>() {
            @Override
            public void validate(BrokerAlgo inData)
            {
                if(inData == null) {
                    return;
                }
                topLevelValidatorCalled.set(true);
            }
        });
        int counter = 0;
        for(BrokerAlgoTagSpec tagSpec : testAlgoSpec.getAlgoTagSpecs()) {
            final AtomicBoolean flag = tagValidatorsCalled[counter++];
            tagSpec.setValidator(new Validator<BrokerAlgoTag>() {
                @Override
                public void validate(BrokerAlgoTag inData)
                {
                    if(inData == null) {
                        return;
                    }
                    flag.set(true);
                }
            });
        }
        testAlgo.validate();
        assertTrue(topLevelValidatorCalled.get());
        counter = 0;
        for(AtomicBoolean flag : tagValidatorsCalled) {
            assertTrue(flag.get());
        }
        // set one tag validator to fail
        testAlgoSpec.getAlgoTagSpecs().iterator().next().setValidator(new Validator<BrokerAlgoTag>() {
            @Override
            public void validate(BrokerAlgoTag inData)
            {
                throw new RuntimeException("this exception is expected");
            }
        });
        new ExpectedFailure<RuntimeException>() {
            @Override
            protected void run()
                    throws Exception
            {
                testAlgo.validate();
            }
        };
    }
    /**
     * Executes a single iteration of a test for {@link BrokerAlgo#applyTo(NewOrReplaceOrder)}.
     *
     * @param inTestAlgo a <code>BrokerAlgo</code> value
     * @param inOrder a <code>NewOrReplaceOrder</code> value
     */
    private void doOneApplyTest(BrokerAlgo inTestAlgo,
                                NewOrReplaceOrder inOrder)
    {
        Map<String,String> customFields = inOrder.getCustomFields();
        inTestAlgo.applyTo(inOrder);
        Map<String,String> overlappingTags = new HashMap<String,String>();
        for(BrokerAlgoTag tag : inTestAlgo.getAlgoTags()) {
            assertEquals(inOrder.getCustomFields().get(String.valueOf(tag.getTagSpec().getTag())),
                         tag.getValue());
            if(customFields != null && customFields.containsKey(String.valueOf(tag.getTagSpec().getTag()))) {
                overlappingTags.put(String.valueOf(tag.getTagSpec().getTag()),
                                    tag.getValue());
            }
        }
        if(customFields != null) {
            // make sure every pre-existing tag is still there
            for(Map.Entry<String,String> entry : customFields.entrySet()) {
                assertTrue(entry.getKey() + " missing from " + inOrder.getCustomFields(),
                           inOrder.getCustomFields().containsKey(entry.getKey()));
            }
        }
        // if there was overlap, make sure that the algo tag takes precedence
        for(Map.Entry<String,String> overlappingTag : overlappingTags.entrySet()) {
            assertEquals(inOrder.getCustomFields().get(overlappingTag.getKey()),
                         overlappingTag.getValue());
        }
    }
    /**
     * Generates bound broker algo tags for the given algo spec.
     *
     * @param inAlgoSpec a <code>BrokerAlgoSpec</code> value
     * @return a <code>Set&lt;BrokerAlgoTag&gt;</code> value
     */
    private Set<BrokerAlgoTag> generateTagsFor(BrokerAlgoSpec inAlgoSpec)
    {
        if(inAlgoSpec == null || inAlgoSpec.getAlgoTagSpecs() == null) {
            return null;
        }
        Set<BrokerAlgoTag> tags = new HashSet<BrokerAlgoTag>();
        for(BrokerAlgoTagSpec tagSpec : inAlgoSpec.getAlgoTagSpecs()) {
            String value;
            if(tagSpec.getOptions() != null && !tagSpec.getOptions().isEmpty()) {
                value = tagSpec.getOptions().entrySet().iterator().next().getValue();
            } else {
                value = "value-" + counter.incrementAndGet();
            }
            BrokerAlgoTag tag = new BrokerAlgoTag(tagSpec,
                                                  value);
            tags.add(tag);
        }
        return tags;
    }
    /**
     * Verifies that the given <code>BrokerAlgo</code> matches the given expected attributes.
     *
     * @param inExpectedAlgoSpec a <code>BrokerAlgoSpec</code> value
     * @param inExpectedAlgoTags a <code>Set&lt;BrokerAlgoTag&gt;</code> value
     * @param inActualAlgo a <code>BrokerAlgoTag</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyAlgo(BrokerAlgoSpec inExpectedAlgoSpec,
                            Set<BrokerAlgoTag> inExpectedAlgoTags,
                            BrokerAlgo inActualAlgo)
            throws Exception
    {
        assertNotNull(inActualAlgo.toString());
        assertEquals(inExpectedAlgoSpec,
                     inActualAlgo.getAlgoSpec());
        assertEquals(inExpectedAlgoTags,
                     inActualAlgo.getAlgoTags());
        if(inExpectedAlgoSpec != null) {
            verifyAlgoSpec(inExpectedAlgoSpec.getName(),
                           inExpectedAlgoSpec.getAlgoTagSpecs(),
                           inExpectedAlgoSpec.getValidator(),
                           inActualAlgo.getAlgoSpec());
        }
    }
    /**
     * Verifies that the given <code>BrokerAlgoSpec</code> matches the given expected attributes.
     *
     * @param inExpectedName a <code>String</code> value
     * @param inExpectedTagSpecs a <code>Set&lt;BrokerAlgoTagSpec&gt;</code> value
     * @param inExpectedValidator a <code>Validator&lt;BrokerAlgo&gt;</code> value
     * @param inActualSpec a <code>BrokerAlgoSpec</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyAlgoSpec(String inExpectedName,
                                Set<BrokerAlgoTagSpec> inExpectedTagSpecs,
                                Validator<BrokerAlgo> inExpectedValidator,
                                BrokerAlgoSpec inActualSpec)
            throws Exception
    {
        assertNotNull(inActualSpec.toString());
        assertEquals(inExpectedName,
                     inActualSpec.getName());
        assertEquals(inExpectedTagSpecs,
                     inActualSpec.getAlgoTagSpecs());
        assertSame(inExpectedValidator,
                   inActualSpec.getValidator());
    }
    /**
     * Verifies that the given <code>BrokerAlgoTag</code> matches the given expected attributes.
     *
     * @param inExpectedTagSpec a <code>BrokerAlgoTagSpec</code> value
     * @param inExpectedValue a <code>String</code> value
     * @param inActualTag a <code>BrokerAlgoTag</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyTag(BrokerAlgoTagSpec inExpectedTagSpec,
                           String inExpectedValue,
                           BrokerAlgoTag inActualTag)
            throws Exception
    {
        assertNotNull(inActualTag.toString());
        assertEquals(inExpectedTagSpec,
                     inActualTag.getTagSpec());
        assertEquals(inExpectedValue,
                     inActualTag.getValue());
        if(inExpectedTagSpec != null) {
            verifyTagSpec(inExpectedTagSpec.getTag(),
                          inExpectedTagSpec.getLabel(),
                          inExpectedTagSpec.getDescription(),
                          inExpectedTagSpec.getPattern(),
                          inExpectedTagSpec.getIsMandatory(),
                          inExpectedTagSpec.getOptions(),
                          inExpectedTagSpec.getValidator(),
                          inActualTag.getTagSpec());
        }
    }
    /**
     * Verifies that the given <code>BrokerAlgoTagSpec</code> matches the given expected attributes.
     *
     * @param inExpectedTag an <code>int</code> value
     * @param inExpectedLabel a <code>String</code> value
     * @param inExpectedDescription a <code>String</code> value
     * @param inExpectedPattern a <code>String</code> value
     * @param inExpectedMandatory a <code>boolean</code> value
     * @param inExpectedOptions a <code>Map&lt;String,String&gt;</code> value
     * @param inExpectedValidator a <code>Validator&lt;BrokerAlgoTag&gt;</code> value
     * @param inActualTagSpec a <code>BrokerAlgoTagSpec</code> value
     * @throws Exception if an unexpected error occurs
     */
    private void verifyTagSpec(int inExpectedTag,
                               String inExpectedLabel,
                               String inExpectedDescription,
                               String inExpectedPattern,
                               boolean inExpectedMandatory,
                               Map<String,String> inExpectedOptions,
                               Validator<BrokerAlgoTag> inExpectedValidator,
                               BrokerAlgoTagSpec inActualTagSpec)
            throws Exception
    {
        assertNotNull(inActualTagSpec.toString());
        assertEquals(inExpectedLabel,
                     inActualTagSpec.getLabel());
        assertEquals(inExpectedTag,
                     inActualTagSpec.getTag());
        assertEquals(inExpectedDescription,
                     inActualTagSpec.getDescription());
        assertEquals(inExpectedPattern,
                     inActualTagSpec.getPattern());
        assertEquals(inExpectedMandatory,
                     inActualTagSpec.getIsMandatory());
        assertEquals(inExpectedOptions,
                     inActualTagSpec.getOptions());
        assertSame(inExpectedValidator,
                   inActualTagSpec.getValidator());
    }
    /**
     * Generates a test <code>BrokerAlgoSpec</code> value.
     *
     * @return a <code>BrokerAlgoSpec</code> value
     */
    public static BrokerAlgoSpec generateAlgoSpec()
    {
        Set<BrokerAlgoTagSpec> tagSpecs = new HashSet<BrokerAlgoTagSpec>();
        Map<String,String> options = new HashMap<String,String>();
        for(int i=0;i<3;i++) {
            tagSpecs.add(generateTagSpec());
            options.put("Option " + counter.incrementAndGet(),
                        "value-" + counter.incrementAndGet());
        }
        tagSpecs.add(generateTagSpec(tagValidator,
                                     options));
        return generateAlgoSpec(tagSpecs.toArray(new BrokerAlgoTagSpec[0]));
    }
    /**
     * Generates a test <code>BrokerAlgoSpec</code> value.
     *
     * @param inTagSpecs a <code>BrokerAlgoTagSpec[]</code> value
     * @return a <code>BrokerAlgoSpec</code> value
     */
    public static BrokerAlgoSpec generateAlgoSpec(BrokerAlgoTagSpec...inTagSpecs)
    {
        BrokerAlgoSpec spec = new BrokerAlgoSpec();
        spec.setName("Test broker algo-" + counter.incrementAndGet());
        if(inTagSpecs != null) {
            spec.setAlgoTagSpecs(new HashSet<BrokerAlgoTagSpec>(Arrays.asList(inTagSpecs)));
        }
        spec.setValidator(algoValidator);
        return spec;
    }
    /**
     * Generates a <code>BrokerAlgoTestSpec</code> value with default values.
     *
     * @return a <code>BrokerAlgoTagSpec</code> value
     */
    public static BrokerAlgoTagSpec generateTagSpec()
    {
        return generateTagSpec(tagValidator,
                               null);
    }
    /**
     * Generates a <code>BrokerAlgoTagSpec</code> value with default values and the given validator.
     *
     * @param inValidator a <code>Validator&lt;BrokerAlgoTag&gt;</code> value
     * @param inOptions a <code>Map&lt;String,String&gt;</code> value
     * @return a <code>BrokerAlgoTagSpec</code> value
     */
    public static BrokerAlgoTagSpec generateTagSpec(Validator<BrokerAlgoTag> inValidator,
                                                    Map<String,String> inOptions)
    {
        BrokerAlgoTagSpec spec = new BrokerAlgoTagSpec();
        spec.setDescription("Test broker algo tag spec-" + counter.incrementAndGet());
        spec.setTag(counter.incrementAndGet());
        spec.setIsMandatory(true);
        spec.setPattern("^.*$");
        spec.setValidator(inValidator);
        spec.setOptions(inOptions);
        return spec;
    }
    /**
     * no-op broker algo validator
     */
    private static Validator<BrokerAlgo> algoValidator = new Validator<BrokerAlgo>() {
        @Override
        public void validate(BrokerAlgo inData)
        {
        }
    };
    /**
     * no-op broker algo tag validator
     */
    private static Validator<BrokerAlgoTag> tagValidator = new Validator<BrokerAlgoTag>() {
        @Override
        public void validate(BrokerAlgoTag inData)
        {
        }
    };
    /**
     * used to uniquely identify test values
     */
    private static final AtomicInteger counter = new AtomicInteger(0);
    /**
     * test XML context for marshalling and unmarshalling
     */
    private static JAXBContext context;
    /**
     * marshals objects to XML
     */
    private static Marshaller marshaller;
    /**
     * unmarshals objects from XML
     */
    private static Unmarshaller unmarshaller;
}
