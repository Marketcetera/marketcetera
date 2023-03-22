package org.marketcetera.ui.fix.view;

import java.util.Objects;

import org.marketcetera.fix.ActiveFixSession;
import org.marketcetera.fix.FixSessionStatus;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/* $License$ */

/**
 * Provides a display POJO version of an <code>ActiveFixSession</code>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class DisplayFixSession
{
    /**
     * Create a new DisplayFixSession instance.
     *
     * @param inFixSession an <code>ActiveFixSession</code> value
     */
    public DisplayFixSession(ActiveFixSession inFixSession)
    {
        nameProperty.set(inFixSession.getFixSession().getName());
        descriptionProperty.set(inFixSession.getFixSession().getDescription());
        sessionIdProperty.set(inFixSession.getFixSession().getSessionId());
        brokerIdProperty.set(inFixSession.getFixSession().getBrokerId());
        hostIdProperty.set(inFixSession.getClusterData().toString());
        statusProperty.set(inFixSession.getStatus());
        targetSeqNumProperty.set(inFixSession.getTargetSequenceNumber());
        senderSeqNumProperty.set(inFixSession.getSenderSequenceNumber());
        sourceProperty.set(inFixSession);
    }
    /**
     * Get the source value.
     *
     * @return a <code>ReadOnlyObjectProperty&lt;ActiveFixSession&gt;</code> value
     */
    public ReadOnlyObjectProperty<ActiveFixSession> sourceProperty()
    {
        return sourceProperty;
    }
    /**
     * Get the name value.
     *
     * @return a <code>StringProperty</code> value
     */
    public StringProperty nameProperty()
    {
        return nameProperty;
    }
    /**
     * Get the description value.
     *
     * @return a <code>StringProperty</code> value
     */
    public StringProperty descriptionProperty()
    {
        return descriptionProperty;
    }
    /**
     * Get the sessionId value.
     *
     * @return a <code>StringProperty</code> value
     */
    public StringProperty sessionIdProperty()
    {
        return sessionIdProperty;
    }
    /**
     * Get the brokerId value.
     *
     * @return a <code>StringProperty</code> value
     */
    public StringProperty brokerIdProperty()
    {
        return brokerIdProperty;
    }
    /**
     * Get the hostId value.
     *
     * @return a <code>StringProperty</code> value
     */
    public StringProperty hostIdProperty()
    {
        return hostIdProperty;
    }
    /**
     * Get the status value.
     *
     * @return an <code>ObjectProperty&lt;FixSessionStatus&gt;</code> value
     */
    public ObjectProperty<FixSessionStatus> statusProperty()
    {
        return statusProperty;
    }
    /**
     * Get the targetSeqNum value.
     *
     * @return an <code>IntegerProperty</code> value
     */
    public IntegerProperty targetSeqNumProperty()
    {
        return targetSeqNumProperty;
    }
    /**
     * Get the senderSeqNum value.
     *
     * @return an <code>IntegerProperty</code> value
     */
    public IntegerProperty senderSeqNumProperty()
    {
        return senderSeqNumProperty;
    }
    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return Objects.hash(brokerIdProperty.get(),
                            descriptionProperty.get(),
                            hostIdProperty.get(),
                            nameProperty.get(),
                            senderSeqNumProperty.get(),
                            sessionIdProperty.get(),
                            statusProperty.get(),
                            targetSeqNumProperty.get());
    }
    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DisplayFixSession)) {
            return false;
        }
        DisplayFixSession other = (DisplayFixSession) obj;
        return Objects.equals(brokerIdProperty.get(),
                              other.brokerIdProperty.get())
                && Objects.equals(descriptionProperty.get(),
                                  other.descriptionProperty.get())
                && Objects.equals(hostIdProperty.get(),
                                  other.hostIdProperty.get())
                && Objects.equals(nameProperty.get(),
                                  other.nameProperty.get())
                && senderSeqNumProperty.get() == other.senderSeqNumProperty.get() && Objects.equals(sessionIdProperty.get(),
                                                                        other.sessionIdProperty.get())
                && statusProperty.get() == other.statusProperty.get() && targetSeqNumProperty.get() == other.targetSeqNumProperty.get();
    }
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append("DisplayFixSession [name=").append(nameProperty.get()).append(", description=").append(descriptionProperty.get())
                .append(", sessionId=").append(sessionIdProperty.get()).append(", brokerId=").append(brokerIdProperty.get()).append(", hostId=")
                .append(hostIdProperty.get()).append(", status=").append(statusProperty.get()).append(", targetSeqNum=").append(targetSeqNumProperty.get())
                .append(", senderSeqNum=").append(senderSeqNumProperty.get()).append("]");
        return builder.toString();
    }
    /**
     * holds the name of the FIX session
     */
    private final StringProperty nameProperty = new SimpleStringProperty();
    /**
     * holds the optional description of the FIX session
     */
    private final StringProperty descriptionProperty = new SimpleStringProperty();
    /**
     * holds the session id of the FIX session
     */
    private final StringProperty sessionIdProperty = new SimpleStringProperty();
    /**
     * holds the broker id of the FIX session
     */
    private final StringProperty brokerIdProperty = new SimpleStringProperty();
    /**
     * holds the host id of the FIX session
     */
    private final StringProperty hostIdProperty = new SimpleStringProperty();
    /**
     * holds the current session status
     */
    private final ObjectProperty<FixSessionStatus> statusProperty = new SimpleObjectProperty<>();
    /**
     * holds the current target sequence number
     */
    private final IntegerProperty targetSeqNumProperty = new SimpleIntegerProperty();
    /**
     * holds the current sender sequence number
     */
    private final IntegerProperty senderSeqNumProperty = new SimpleIntegerProperty();
    /**
     * holds the FIX session source object
     */
    private final ObjectProperty<ActiveFixSession> sourceProperty = new SimpleObjectProperty<>();
}
