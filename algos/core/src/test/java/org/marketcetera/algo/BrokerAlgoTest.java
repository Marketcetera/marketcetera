package org.marketcetera.algo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

import javax.xml.bind.*;

import org.junit.BeforeClass;
import org.junit.Test;
import org.marketcetera.core.LoggerConfiguration;
import org.marketcetera.core.Validator;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class BrokerAlgoTest
{
    /**
     * 
     *
     *
     * @throws Exception
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
     * 
     *
     *
     * @throws Exception
     */
    @Test
    public void testSerializeAndMarshal()
            throws Exception
    {
        // generate a test algo
        BrokerAlgoSpec testSpec = generateAlgoSpec();
        // bind the test spec with values
        Set<BrokerAlgoTag> boundTags = new HashSet<BrokerAlgoTag>();
        for(BrokerAlgoTagSpec tagSpec : testSpec.getAlgoTagSpecs()) {
            BrokerAlgoTag boundTagSpec = new BrokerAlgoTag(tagSpec,
                                                           "100" + counter.incrementAndGet());
            boundTags.add(boundTagSpec);
        }
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
        SLF4JLoggerProxy.debug(this,
                               "Marshalled {} for {}",
                               outputWriter,
                               boundAlgo);
        // unmarshal and compare
        algoCopy = (BrokerAlgo)unmarshaller.unmarshal(new InputStreamReader(new ByteArrayInputStream(outputWriter.toString().getBytes())));
        assertEquals(boundAlgo,
                     algoCopy);
    }
    /**
     * 
     *
     *
     * @return
     */
    private BrokerAlgoSpec generateAlgoSpec()
    {
        BrokerAlgoSpec spec = new BrokerAlgoSpec();
        spec.setName("Test broker algo-" + counter.incrementAndGet());
        Set<BrokerAlgoTagSpec> tagSpecs = new HashSet<BrokerAlgoTagSpec>();
        for(int i=0;i<3;i++) {
            tagSpecs.add(generateNoOpTagSpec());
        }
        spec.setAlgoTagSpecs(tagSpecs);
        spec.setValidator(algoValidator);
        return spec;
    }
    /**
     * 
     *
     *
     * @param inTagSpecs
     * @return
     */
    @SuppressWarnings("unused")
    private BrokerAlgoSpec generateAlgoSpec(BrokerAlgoTagSpec...inTagSpecs)
    {
        BrokerAlgoSpec spec = new BrokerAlgoSpec();
        spec.setName("Test broker algo-" + counter.incrementAndGet());
        if(inTagSpecs != null) {
            spec.setAlgoTagSpecs(new HashSet<BrokerAlgoTagSpec>(Arrays.asList(inTagSpecs)));
        }
        return spec;
    }
    /**
     * 
     *
     *
     * @return
     */
    private BrokerAlgoTagSpec generateNoOpTagSpec()
    {
        return generateTagSpec(tagValidator);
    }
    /**
     * 
     *
     *
     * @return
     */
    @SuppressWarnings("unused")
    private BrokerAlgoTagSpec generateTagSpec()
    {
        return generateTagSpec(null);
    }
    /**
     * 
     *
     *
     * @param inValidator
     * @return
     */
    private BrokerAlgoTagSpec generateTagSpec(Validator<BrokerAlgoTag> inValidator)
    {
        BrokerAlgoTagSpec spec = new BrokerAlgoTagSpec();
        spec.setDescription("Test broker algo tag spec-" + counter.incrementAndGet());
        spec.setTag(counter.incrementAndGet());
        spec.setIsMandatory(true);
        spec.setPattern("^\\d*$");
        spec.setValidator(inValidator);
        return spec;
    }
    /**
     * no-op broker algo validator
     */
    private Validator<BrokerAlgo> algoValidator = new Validator<BrokerAlgo>() {
        @Override
        public void validate(BrokerAlgo inData)
        {
        }
    };
    /**
     * no-op broker algo tag validator
     */
    private Validator<BrokerAlgoTag> tagValidator = new Validator<BrokerAlgoTag>() {
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
     * 
     */
    private static JAXBContext context;
    /**
     * 
     */
    private static Marshaller marshaller;
    /**
     * unmarshals objects from XML
     */
    private static Unmarshaller unmarshaller;
}
