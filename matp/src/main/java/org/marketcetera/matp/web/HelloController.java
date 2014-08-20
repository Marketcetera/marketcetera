package org.marketcetera.matp.web;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
@RestController
public class HelloController
{
    @RequestMapping("/")
    public String index()
    {
        return "Greetings from Spring Boot!";
    }
}
