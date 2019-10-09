package com.marketcetera.colin.backend.client;

import org.marketcetera.admin.AdminClientFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@Service
public class AdminClientService
{
    
    @Autowired
    private AdminClientFactory adminClientFactory;
}
