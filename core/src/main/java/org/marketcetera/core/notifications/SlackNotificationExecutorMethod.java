package org.marketcetera.core.notifications;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.marketcetera.util.log.SLF4JLoggerProxy;

/* $License$ */

/**
 * Sends notifications via <a href="https://slack.com">Slack</a>
 *
 * @author <a href="mailto:colin@marketcetera.com">Colin DuPlantis</a>
 * @version $Id$
 * @since $Release$
 */
public class SlackNotificationExecutorMethod
        extends AbstractNotificationExecutorMethod
{
    /**
     * Get the slackWebHookUrl value.
     *
     * @return a <code>String</code> value
     */
    public String getSlackWebHookUrl()
    {
        return slackWebHookUrl;
    }
    /**
     * Sets the slackWebHookUrl value.
     *
     * @param a <code>String</code> value
     */
    public void setSlackWebHookUrl(String inSlackWebHookUrl)
    {
        slackWebHookUrl = inSlackWebHookUrl;
    }
    /**
     * Get the slackWebHookParams value.
     *
     * @return a <code>String</code> value
     */
    public String getSlackWebHookParams()
    {
        return slackWebHookParams;
    }
    /**
     * Sets the slackWebHookParams value.
     *
     * @param a <code>String</code> value
     */
    public void setSlackWebHookParams(String inSlackWebHookParams)
    {
        slackWebHookParams = inSlackWebHookParams;
    }
    /* (non-Javadoc)
     * @see org.marketcetera.core.notifications.AbstractNotificationExecutorMethod#doNotify(org.marketcetera.core.notifications.INotification)
     */
    @Override
    protected void doNotify(INotification inNotification)
            throws Exception
    {
        notifySlackWebhook(inNotification);
    }
    /**
     * Notifies via slack webhook if necessary.
     *
     * @param inNotification an <code>INotification</code> value
     * @throws IOException if an error occurs during notification
     */
    private void notifySlackWebhook(INotification inNotification)
            throws IOException
    {
        if(slackWebHookUrl != null) {
            try(CloseableHttpClient httpclient = HttpClients.createDefault()) {
                HttpPost postRequest = null;
                postRequest = new HttpPost(slackWebHookUrl);
                StringBuilder payloadBuilder = new StringBuilder();
                payloadBuilder.append("{\"text\":");
                // prepare notification subject and body
                String subject = getSubject(inNotification);
                payloadBuilder.append("\"");
                payloadBuilder.append(subject);
                payloadBuilder.append("\\n");
                payloadBuilder.append("```");
                String body = getBody(inNotification);
                body = body.replaceAll("\n","\\n");
                payloadBuilder.append(body);
                payloadBuilder.append("```\"");
                if(slackWebHookParams != null) {
                    payloadBuilder.append(',').append(slackWebHookParams);
                }
                payloadBuilder.append("}");
                SLF4JLoggerProxy.debug(this,
                                       "Slack webhook payload is {}",
                                       payloadBuilder);
                List<NameValuePair> nvps = new ArrayList<NameValuePair>(1);
                nvps.add(new BasicNameValuePair("payload",
                                                payloadBuilder.toString()));
                postRequest.setEntity(new UrlEncodedFormEntity(nvps,
                                                               "UTF-8"));
                try(CloseableHttpResponse response = httpclient.execute(postRequest)) {
                    if(response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                        SLF4JLoggerProxy.warn(this,
                                              "Slack webhook did not succeed: {}",
                                              response.getStatusLine());
                    }
                }
            }
        }
    }
    /**
     * URL used to identify the slack web hook site, may be <code>null</code>, indicating no slack notification
     */
    private String slackWebHookUrl;
    /**
     * extra, optional params that are used for the slack webhook, may be <code>null</code>, indicating not used
     */
    private String slackWebHookParams;
}
