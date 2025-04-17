#!/bin/bash
# Script to fix UTF-8 encoding in SlackNotificationExecutorMethod

# Update the SlackNotificationExecutorMethod.java file
sed -i '' 's/"UTF-8"/java.nio.charset.StandardCharsets.UTF_8/g' /Users/colin/marketcetera/workspaces/sixer-java11/code/marketcetera/core/src/main/java/org/marketcetera/core/notifications/SlackNotificationExecutorMethod.java

echo "Fixed UTF-8 encoding in SlackNotificationExecutorMethod"