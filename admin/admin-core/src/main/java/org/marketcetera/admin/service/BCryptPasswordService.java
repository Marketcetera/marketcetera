package org.marketcetera.admin.service;

import java.security.SecureRandom;

import javax.annotation.PostConstruct;

import org.marketcetera.core.PlatformServices;
import org.marketcetera.util.log.SLF4JLoggerProxy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder.BCryptVersion;

/* $License$ */

/**
 * Provides a BCrypt {@link PasswordService} implementation.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@EnableAutoConfiguration
public class BCryptPasswordService
        implements PasswordService
{
    /**
     * Validate and start the object.
     */
    @PostConstruct
    public void start()
    {
        SLF4JLoggerProxy.info(this,
                              "Starting {} with strength {} and value {}",
                              PlatformServices.getServiceName(getClass()),
                              strength,
                              bCryptVersionValue);
        bCryptVersion = BCryptVersion.valueOf(bCryptVersionValue);
        encoder = new BCryptPasswordEncoder(bCryptVersion,
                                            strength,
                                            new SecureRandom());
    }
    /* (non-Javadoc)
     * @see org.marketcetera.admin.service.PasswordService#getHash(java.lang.String)
     */
    @Override
    public String getHash(String inValue)
    {
        return encoder.encode(inValue);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.security.PasswordService#matches(java.lang.String, java.lang.String)
     */
    @Override
    public boolean matches(String inRawPassword,
                           String inHashedPassword)
    {
        return encoder.matches(inRawPassword,
                               inHashedPassword);
    }
    /**
     * encoder value
     */
    private BCryptPasswordEncoder encoder;
    /**
     * indicates the bcrypt version to use
     */
    private BCryptVersion bCryptVersion;
    /**
     * indicates the bcrypt strength to use
     */
    @Value("${metc.security.bcrypt.strength:10}")
    private int strength;
    /**
     * indicates the value of the {@link BCryptVersion} to use
     */
    @Value("${metc.security.bcrypt.version:$2A}")
    private String bCryptVersionValue;
}
