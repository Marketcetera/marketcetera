package org.marketcetera.db;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class Manager
{
    @PostConstruct
    public void start()
    {
        Test test = new Test();
        test.setName("test-" + System.nanoTime());
        testRepository.save(test);
        Iterable<Test> all = testRepository.findAll();
        System.out.println("Found " + all);
    }
    @Autowired
    private TestRepository testRepository;
}
