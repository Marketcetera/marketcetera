package org.marketcetera.persist;

/* $License$ */

/**
 * Indicates the direction to sort.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public enum SortDirection
{
    ASCENDING(org.springframework.data.domain.Sort.Direction.ASC),
    DESCENDING(org.springframework.data.domain.Sort.Direction.DESC);
    /**
     * Get the corresponding Spring data domain value.
     *
     * @return a <code>org.springframework.data.domain.Sort.Direction</code> value
     */
    public org.springframework.data.domain.Sort.Direction getSpringSortDirection()
    {
        return springSortDirection;
    }
    /**
     * Create a new SortDirection instance.
     *
     * @param inSpringSortDirection
     */
    private SortDirection(org.springframework.data.domain.Sort.Direction inSpringSortDirection)
    {
        springSortDirection = inSpringSortDirection;
    }
    /**
     * spring direction value
     */
    private final org.springframework.data.domain.Sort.Direction springSortDirection;
}
