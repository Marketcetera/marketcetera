package org.marketcetera.trade;

import java.util.HashSet;
import java.util.Set;

/* $License$ */

/**
 *
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class MockAlgoSpec
        implements AlgoSpec
{
    /* (non-Javadoc)
     * @see org.marketcetera.trade.AlgoSpec#getName()
     */
    @Override
    public String getName()
    {
        return "VWAP";
    }
    /* (non-Javadoc)
     * @see org.marketcetera.trade.AlgoSpec#getAlgoTags()
     */
    @Override
    public Set<AlgoTagSpec> getAlgoTagSpecs()
    {
        Set<AlgoTagSpec> tags = new HashSet<AlgoTagSpec>();
        AlgoTagSpec maxParticipation = new AlgoTagSpec() {
            @Override
            public String getDescription()
            {
                return "Max Participation";
            }
            @Override
            public int getTag()
            {
                return 6000;
            }
            @Override
            public String getPattern()
            {
                return "%d*";
            }
            @Override
            public boolean isMandatory()
            {
                return true;
            }
        };
        tags.add(maxParticipation);
        return tags;
    }
}
