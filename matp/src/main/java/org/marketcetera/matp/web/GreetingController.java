package org.marketcetera.matp.web;

import java.util.concurrent.atomic.AtomicLong;

import org.marketcetera.matp.domain.Greeting;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
public class GreetingController
{
    @RequestMapping("/greeting")
    public Greeting greeting(@RequestParam(value="name",required=false,defaultValue="World")String inName)
    {
        return new Greeting(counter.incrementAndGet(),
                            String.format(template,
                                          inName));
    }
    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();
}
