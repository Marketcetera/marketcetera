package org.marketcetera.provisinging.sample;

import java.io.File;
import java.io.IOException;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Component;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Component
public class SampleProvisioningComponent
{
    @PostConstruct
    public void start()
            throws IOException
    {
        System.out.println("COCO: starting " + getClass().getSimpleName());
        String tmpDirPath = System.getProperty("java.io.tmpdir");
        File outputFile = new File(tmpDirPath,
                                   getClass().getSimpleName());
        outputFile.createNewFile();
    }
}
