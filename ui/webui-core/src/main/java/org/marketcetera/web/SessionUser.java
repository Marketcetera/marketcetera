package org.marketcetera.web;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.springframework.security.core.GrantedAuthority;

import com.google.common.collect.Sets;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.UI;

/* $License$ */

/**
 * Indicates the currently validated session user.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SessionUser
{
    /**
     * Get the current user.
     *
     * @return a <code>SessionUser</code> value
     */
    public static SessionUser getCurrentUser()
    {
        return VaadinSession.getCurrent().getAttribute(SessionUser.class);
    }
    /**
     * Create a new SessionUser instance.
     *
     * @param inUsername a <code>String</code> value
     * @param inPassword a <code>String</code> value
     */
    public SessionUser(String inUsername,
                       String inPassword)
    {
        username = inUsername;
        password = inPassword;
    }
    /**
     * Get the username value.
     *
     * @return a <code>String</code> value
     */
    public String getUsername()
    {
        return username;
    }
    /**
     * Get the loggedIn value.
     *
     * @return a <code>Date</code> value
     */
    public Date getLoggedIn()
    {
        return loggedIn;
    }
    /**
     * Get the password value.
     *
     * @return a <code>String</code> value
     */
    public String getPassword()
    {
        return password;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        return username;
    }
    /**
     * Get the permissions value.
     *
     * @return a <code>Set&lt;GrantedAuthority&gt;</code> value
     */
    public Set<GrantedAuthority> getPermissions()
    {
        return permissions;
    }
    /**
     * Gets a stored attribute value. If a value has been stored for the
     * session, that value is returned. If no value is stored for the name,
     * <code>null</code> is returned.
     * <p>
     * The fully qualified name of the type is used as the name when getting the
     * value. The outcome of calling this method is thus the same as if
     * calling<br />
     * <br />
     * <code>getAttribute(type.getName());</code>
     *
     * @see #setAttribute(Class, Object)
     * @see #getAttribute(String)
     *
     * @param type
     *            the type of the value to get, can not be <code>null</code>.
     * @return the value, or <code>null</code> if no value has been stored or if
     *         it has been set to null.
     */
    public <T> T getAttribute(Class<T> type) {
        if (type == null) {
            throw new IllegalArgumentException("type can not be null");
        }
        Object value = getAttribute(type.getName());
        if (value == null) {
            return null;
        } else {
            return type.cast(value);
        }
    }
    /**
     * Gets a stored attribute value. If a value has been stored for the
     * session, that value is returned. If no value is stored for the name,
     * <code>null</code> is returned.
     *
     * @see #setAttribute(String, Object)
     *
     * @param name
     *            the name of the value to get, can not be <code>null</code>.
     * @return the value, or <code>null</code> if no value has been stored or if
     *         it has been set to null.
     */
    public Object getAttribute(String name) {
        if (name == null) {
            throw new IllegalArgumentException("name can not be null");
        }
        return attributes.get(name);
    }
    /**
     * Stores a value in this service session. This can be used to associate
     * data with the current user so that it can be retrieved at a later point
     * from some other part of the application. Setting the value to
     * <code>null</code> clears the stored value.
     * <p>
     * The fully qualified name of the type is used as the name when storing the
     * value. The outcome of calling this method is thus the same as if
     * calling<br />
     * <br />
     * <code>setAttribute(type.getName(), value);</code>
     *
     * @see #getAttribute(Class)
     * @see #setAttribute(String, Object)
     *
     * @param type
     *            the type that the stored value represents, can not be null
     * @param value
     *            the value to associate with the type, or <code>null</code> to
     *            remove a previous association.
     */
    public <T> void setAttribute(Class<T> type, T value) {
        if (type == null) {
            throw new IllegalArgumentException("type can not be null");
        }
        if (value != null && !type.isInstance(value)) {
            throw new IllegalArgumentException("value of type " + type.getName()
            + " expected but got " + value.getClass().getName());
        }
        setAttribute(type.getName(), value);
    }
    /**
     * Stores a value in this service session. This can be used to associate
     * data with the current user so that it can be retrieved at a later point
     * from some other part of the application. Setting the value to
     * <code>null</code> clears the stored value.
     *
     * @see #getAttribute(String)
     *
     * @param name
     *            the name to associate the value with, can not be
     *            <code>null</code>
     * @param value
     *            the value to associate with the name, or <code>null</code> to
     *            remove a previous association.
     */
    public void setAttribute(String name, Object value) {
        if (name == null) {
            throw new IllegalArgumentException("name can not be null");
        }
        if (value != null) {
            attributes.put(name, value);
        } else {
            attributes.remove(name);
        }
    }
    /**
     * Establish the time update for the given user.
     *
     * @param inConsumer a <code>Consumer&lt;String&gt;</code> value
     */
    public void establishTimeUpdate(final Consumer<String> inConsumer)
    {
        cancelTimeUpdate();
        setAttribute(TimeUpdateToken.class,
                     new TimeUpdateToken(timeUpdateService.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run()
            {
                try {
                    UI currentUi = UI.getCurrent();
                    if(currentUi == null) {
                        return;
                    }
                    currentUi.access(new Runnable() {
                        @Override
                        public void run()
                        {
                            try {
                                inConsumer.accept(TIME_FORMAT.print(new DateTime()));
                            } catch (Exception ignored) {}
                        }}
                    );
                } catch (Exception ignored) {}
            }},1,1,TimeUnit.SECONDS)));
    }
    /**
     * Cancel the time update for the given user.
     */
    public void cancelTimeUpdate()
    {
        TimeUpdateToken token = getAttribute(TimeUpdateToken.class);
        if(token == null) {
            return;
        }
        if(token.getTimeUpdateToken() != null) {
            token.getTimeUpdateToken().cancel(true);
            token.setTimeUpdateToken(null);
        }
        setAttribute(TimeUpdateToken.class,
                     null);
    }
    /**
     * Provides a unique wrapper for the ScheduledFuture for the time update service to be stored in session attributes.
     *
     * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
     * @version $Id$
     * @since $Release$
     */
    private static class TimeUpdateToken
    {
        /**
         * Get the timeUpdateToken value.
         *
         * @return a <code>ScheduledFuture&lt;?&gt;</code> value
         */
        private ScheduledFuture<?> getTimeUpdateToken()
        {
            return timeUpdateToken;
        }
        /**
         * Sets the timeUpdateToken value.
         *
         * @param inTimeUpdateToken a <code>ScheduledFuture&lt;?&gt;</code> value
         */
        private void setTimeUpdateToken(ScheduledFuture<?> inTimeUpdateToken)
        {
            timeUpdateToken = inTimeUpdateToken;
        }
        /**
         * Create a new TimeUpdateToken instance.
         *
         * @param inTimeUpdateToken a <code>ScheduledFuture&lt;?&gt;</code> value
         */
        private TimeUpdateToken(ScheduledFuture<?> inTimeUpdateToken)
        {
            timeUpdateToken = inTimeUpdateToken;
        }
        /**
         * holds the token for the time update service, if any
         */
        private ScheduledFuture<?> timeUpdateToken;
    }
    /**
     * provides a single service to update all users' time receivers
     */
    private static final ScheduledExecutorService timeUpdateService = Executors.newSingleThreadScheduledExecutor();
    /**
     * time format for time receivers
     */
    private static final DateTimeFormatter TIME_FORMAT = new DateTimeFormatterBuilder().appendMonthOfYearShortText().appendLiteral(' ').appendDayOfMonth(2).appendLiteral(' ')
            .appendHourOfDay(2).appendLiteral(':').appendMinuteOfHour(2).appendLiteral(':').appendSecondOfMinute(2).appendLiteral(' ').appendTimeZoneShortName().toFormatter();
    /**
     * holds the attributes for the user
     */
    private final Map<String,Object> attributes = new HashMap<String, Object>();
    /**
     * holds permissions for this user
     */
    private final Set<GrantedAuthority> permissions = Sets.newHashSet();
    /**
     * username value
     */
    private final String username;
    /**
     * password value
     */
    private final String password;
    /**
     * indicates when the user was logged in
     */
    private final Date loggedIn = new Date();
}
