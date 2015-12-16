package org.marketcetera.core.notifications;

import java.io.IOException;

import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONObject;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Sends notifications via <a href="https://www.pagerduty.com">Pager Duty</a>.
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class PagerDutyNotificationExecutorMethod
        extends AbstractNotificationExecutorMethod
{
    /**
     * Get the pagerDutyUrl value.
     *
     * @return a <code>String</code> value
     */
    public String getPagerDutyUrl()
    {
        return pagerDutyUrl;
    }
    /**
     * Sets the pagerDutyUrl value.
     *
     * @param a <code>String</code> value
     */
    public void setPagerDutyUrl(String inPagerDutyUrl)
    {
        pagerDutyUrl = inPagerDutyUrl;
    }
    /**
     * Get the pagerDutyServiceKey value.
     *
     * @return a <code>String</code> value
     */
    public String getPagerDutyServiceKey()
    {
        return pagerDutyServiceKey;
    }
    /**
     * Sets the pagerDutyServiceKey value.
     *
     * @param a <code>String</code> value
     */
    public void setPagerDutyServiceKey(String inPagerDutyServiceKey)
    {
        pagerDutyServiceKey = inPagerDutyServiceKey;
    }
    /**
     * Get the pagerDutyEventType value.
     *
     * @return a <code>String</code> value
     */
    public String getPagerDutyEventType()
    {
        return pagerDutyEventType;
    }
    /**
     * Sets the pagerDutyEventType value.
     *
     * @param a <code>String</code> value
     */
    public void setPagerDutyEventType(String inPagerDutyEventType)
    {
        pagerDutyEventType = inPagerDutyEventType;
    }
    /**
     * Get the pagerDutyClient value.
     *
     * @return a <code>String</code> value
     */
    public String getPagerDutyClient()
    {
        return pagerDutyClient;
    }
    /**
     * Sets the pagerDutyClient value.
     *
     * @param a <code>String</code> value
     */
    public void setPagerDutyClient(String inPagerDutyClient)
    {
        pagerDutyClient = inPagerDutyClient;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.AbstractNotificationExecutorMethod#verifySeverityThreshold(org.marketcetera.core.notifications.INotification)
     */
    @Override
    protected boolean verifySeverityThreshold(INotification inNotification)
    {
        return inNotification instanceof PagerDutyNotification || super.verifySeverityThreshold(inNotification);
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.AbstractNotificationExecutorMethod#doNotify(org.marketcetera.core.notifications.INotification)
     */
    @Override
    protected void doNotify(INotification inNotification)
            throws Exception
    {
        notifyPagerDuty(inNotification);
    }
    /**
     * Notifies pager duty, if necessary.
     *
     * @param inNotification an <code>INotification</code> value
     * @throws IOException if an error occurs during notification
     */
    private void notifyPagerDuty(INotification inNotification)
            throws IOException
    {
        if(pagerDutyUrl != null) {
            try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
                HttpPost postRequest = null;
                postRequest = new HttpPost(pagerDutyUrl);
                String subject = getSubject(inNotification);
                String body = getBody(inNotification);
                JSONObject payloadBuilder = new JSONObject();
                if(inNotification instanceof PagerDutyNotification) {
                    PagerDutyNotification pdNotification = (PagerDutyNotification)inNotification;
                    payloadBuilder.put("service_key",pagerDutyServiceKey)
                    .put("event_type", pdNotification.getEventType() == null?pagerDutyEventType:pdNotification.getEventType().name().toLowerCase())
                    .put("description", pdNotification.getSubject())
                    .put("incident_key", pdNotification.getIncidentKey())
                    .put("details", pdNotification.getBody())
                    .put("client",pagerDutyClient);
                } else {
                    payloadBuilder.put("service_key",pagerDutyServiceKey)
                    .put("event_type",pagerDutyEventType)
                    .put("description", subject)
                    .put("incident_key",subject)
                    .put("details", body)
                    .put("client",pagerDutyClient);
                }
                SLF4JLoggerProxy.debug(this,
                                       "Pager duty payload is {}",
                                       payloadBuilder);
                postRequest.setEntity(new StringEntity(payloadBuilder.toString()));
                try(CloseableHttpResponse response = httpclient.execute(postRequest)) {
                    if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        SLF4JLoggerProxy.warn(this,
                                              "Pager duty did not succeed: {}",
                                              response.getStatusLine());
                    }
                }
            }
        }
    }
    /**
     * URL used to identify the pager duty site, may be <code>null</code>, indicating no pager duty notification
     */
    private String pagerDutyUrl;
    /**
     * pager duty service key value
     */
    private String pagerDutyServiceKey;
    /**
     * pager duty event type value
     */
    private String pagerDutyEventType;
    /**
     * pager duty client value
     */
    private String pagerDutyClient;
}
