-- MySQL dump 10.13  Distrib 5.7.24, for Linux (x86_64)
--
-- Host: localhost    Database: metc
-- ------------------------------------------------------
-- Server version	5.7.24-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `exec_reports`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `exec_reports` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `account` varchar(255) DEFAULT NULL,
  `avg_price` decimal(17,7) NOT NULL,
  `cum_qty` decimal(17,7) NOT NULL,
  `eff_cum_qty` decimal(17,7) NOT NULL,
  `exec_type` int(11) NOT NULL,
  `expiry` varchar(255) DEFAULT NULL,
  `last_price` decimal(17,7) DEFAULT NULL,
  `last_qty` decimal(17,7) DEFAULT NULL,
  `option_type` int(11) DEFAULT NULL,
  `order_id` varchar(255) NOT NULL,
  `ord_status` int(11) NOT NULL,
  `orig_order_id` varchar(255) DEFAULT NULL,
  `root_order_id` varchar(255) NOT NULL,
  `security_type` int(11) NOT NULL,
  `send_time` timestamp(3) NOT NULL DEFAULT '0000-00-00 00:00:00.000',
  `side` int(11) NOT NULL,
  `strike_price` decimal(17,7) DEFAULT NULL,
  `symbol` varchar(255) NOT NULL,
  `actor_id` bigint(20) DEFAULT NULL,
  `report_id` bigint(20) NOT NULL,
  `viewer_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_imdq099u0qa8ob9tt5ljm6f7u` (`report_id`),
  KEY `FKfn47lj607ghtt1lfie8adevxc` (`actor_id`),
  KEY `FK1celhypj9vint37eobsn22s1b` (`viewer_id`),
  CONSTRAINT `FK1celhypj9vint37eobsn22s1b` FOREIGN KEY (`viewer_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKfn47lj607ghtt1lfie8adevxc` FOREIGN KEY (`actor_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKn47ta2b6e9wih8b97oubxvd3s` FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `exec_reports`
--

LOCK TABLES `exec_reports` WRITE;
/*!40000 ALTER TABLE `exec_reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `exec_reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fix_messages`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `fix_messages` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `message` longtext NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fix_messages`
--

LOCK TABLES `fix_messages` WRITE;
/*!40000 ALTER TABLE `fix_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `fix_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fix_session_attr_dscrptrs`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `fix_session_attr_dscrptrs` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `advice` varchar(255) DEFAULT NULL,
  `default_value` varchar(255) DEFAULT NULL,
  `description` varchar(1024) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `pattern` varchar(255) DEFAULT NULL,
  `required` bit(1) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_gb3x058kyh5s4fxw1fi67bysc` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fix_session_attr_dscrptrs`
--

LOCK TABLES `fix_session_attr_dscrptrs` WRITE;
/*!40000 ALTER TABLE `fix_session_attr_dscrptrs` DISABLE KEYS */;
INSERT INTO `fix_session_attr_dscrptrs` (`id`, `last_updated`, `update_count`, `advice`, `default_value`, `description`, `name`, `pattern`, `required`) VALUES (46,'2018-11-13 14:33:12.248',0,NULL,'','(Optional) Your subID as associated with this FIX session','SenderSubID','',_binary '\0'),(47,'2018-11-13 14:33:12.256',0,NULL,'','(Optional) Your locationID as associated with this FIX session','SenderLocationID','',_binary '\0'),(48,'2018-11-13 14:33:12.265',0,NULL,'','(Optional) counterparty\'s subID as associated with this FIX session','TargetSubID','',_binary '\0'),(49,'2018-11-13 14:33:12.271',0,NULL,'','(Optional) counterparty\'s locationID as associated with this FIX session','TargetLocationID','',_binary '\0'),(50,'2018-11-13 14:33:12.278',0,NULL,'','Additional qualifier to disambiguate otherwise identical sessions. This can only be used with initiator sessions.Note: See Special notes for Oracle.','SessionQualifier','',_binary '\0'),(51,'2018-11-13 14:33:12.284',0,NULL,'','Required only for FIXT 1.1 (and newer). Ignored for earlier transport versions. Specifies the default application version ID for the session. This can either be the ApplVerID enum (see the ApplVerID field) the beginString for the default version.','DefaultApplVerID','',_binary '\0'),(52,'2018-11-13 14:33:12.290',0,NULL,'Y','Determines if milliseconds should be added to timestamps. Only available for FIX.4.2 and greater.','MillisecondsInTimeStamp','',_binary '\0'),(53,'2018-11-13 14:33:12.295',0,NULL,'N','Use actual end of sequence gap for resend requests rather than using \'\'infinity\'\' as the end sequence of the gap. Not recommended by the FIX specification, but needed for some counterparties.','ClosedResendInterval','',_binary '\0'),(54,'2018-11-13 14:33:12.301',0,NULL,'Y','Tell session whether or not to expect a data dictionary. You should always use a DataDictionary if you are using repeating groups.','UseDataDictionary','',_binary '\0'),(55,'2018-11-13 14:33:12.308',0,NULL,'FIX42.xml','XML definition file for validating incoming FIX messages. If no DataDictionary is supplied, only basic message validation will be done. This setting should only be used with FIX transport versions old than FIXT 1.1. See TransportDataDictionary and ApplicationDataDictionary for FIXT 1.1 settings.','DataDictionary','',_binary '\0'),(56,'2018-11-13 14:33:12.314',0,NULL,'','XML definition file for validating admin (transport) messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information.','TransportDataDictionary','',_binary '\0'),(57,'2018-11-13 14:33:12.320',0,NULL,'','XML definition file for validating application messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information. This setting supports the possibility of a custom application data dictionary for each session. This setting would only be used with FIXT 1.1 and new transport protocols. This setting can be used as a prefix to specify multiple application dictionaries for the FIXT transport. For example: DefaultApplVerID=FIX.4.2 # For default application version ID AppDataDictionary=FIX42.xml # For nondefault application version ID # Use beginString suffix for app version AppDataDictionary.FIX.4.4=FIX44.xml This would use FIX42.xml for the default application version ID and FIX44.xml for any FIX 4.4 messages.','AppDataDictionary','',_binary '\0'),(58,'2018-11-13 14:33:12.325',0,NULL,'Y','If set to N, fields that are out of order (i.e. body fields in the header, or header fields in the body) will not be rejected. Useful for connecting to systems which do not properly order fields.','ValidateFieldsOutOfOrder','',_binary '\0'),(59,'2018-11-13 14:33:12.330',0,NULL,'Y','If set to N, fields without values (empty) will not be rejected. Useful for connecting to systems which improperly send empty tags.','ValidateFieldsHaveValues','',_binary '\0'),(60,'2018-11-13 14:33:12.337',0,NULL,'Y','If set to N, user defined fields will not be rejected if they are not defined in the data dictionary, or are present in messages they do not belong to.','ValidateUserDefinedFields','',_binary '\0'),(61,'2018-11-13 14:33:12.342',0,NULL,'Y','Session validation setting for enabling whether field ordering is * validated. Values are \'\'Y\'\' or \'\'N\'\'. Default is \'\'Y\'\'.','ValidateUnorderedGroupFields','',_binary '\0'),(62,'2018-11-13 14:33:12.348',0,NULL,'Y','Allow to bypass the message validation (against the dictionary). Default is \'\'Y\'\'.','ValidateIncomingMessage','',_binary '\0'),(63,'2018-11-13 14:33:12.353',0,NULL,'Y','Check the next expected target SeqNum against the received SeqNum. Default is \'\'Y\'\'. If enabled and a mismatch is detected, apply the following logic:if lower than expected SeqNum , logout if higher, send a resend request If not enabled and a mismatch is detected, nothing is done. Must be enabled for EnableNextExpectedMsgSeqNum to work.','ValidateSequenceNumbers','',_binary '\0'),(64,'2018-11-13 14:33:12.359',0,NULL,'N','Allow unknown fields in messages. This is intended for unknown fields with tags lt 5000 (not user defined fields). Use ValidateUserDefinedFields for controlling validation of tags ge 5000.','AllowUnknownMsgFields','',_binary '\0'),(65,'2018-11-13 14:33:12.367',0,NULL,'Y','If set to Y, messages must be received from the counterparty with the correct SenderCompID and TargetCompID. Some systems will send you different CompIDs by design, so you must set this to N.','CheckCompID','',_binary '\0'),(66,'2018-11-13 14:33:12.373',0,NULL,'Y','If set to Y, messages must be received from the counterparty within a defined number of seconds (see MaxLatency). It is useful to turn this off if a system uses localtime for its timestamps instead of GMT.','CheckLatency','',_binary '\0'),(67,'2018-11-13 14:33:12.378',0,NULL,'120','If CheckLatency is set to Y, this defines the number of seconds latency allowed for a message to be processed.','MaxLatency','',_binary '\0'),(68,'2018-11-13 14:33:12.387',0,NULL,'Y','If RejectInvalidMessage is set to N, only a warning will be logged on reception of message that fails data dictionary validation.','RejectInvalidMessage','',_binary '\0'),(69,'2018-11-13 14:33:12.394',0,NULL,'N','If this configuration is enabled, an uncaught Exception or Error in the application\'s message processing will lead to a (BusinessMessage)Reject being sent to the counterparty and the incoming message sequence number will be incremented. If disabled (default), the problematic incoming message is discarded and the message sequence number is not incremented. Processing of the next valid message will cause detection of a sequence gap and a ResendRequest will be generated.','RejectMessageOnUnhandledException','',_binary '\0'),(70,'2018-11-13 14:33:12.401',0,NULL,'Y','If RequiresOrigSendingTime is set to N, PossDup messages lacking that field will not be rejected.','RequiresOrigSendingTime','',_binary '\0'),(71,'2018-11-13 14:33:12.406',0,NULL,'30','Time between reconnection attempts in seconds. Only used for initiators','ReconnectInterval','',_binary '\0'),(72,'2018-11-13 14:33:12.413',0,NULL,'30','Heartbeat interval in seconds. Only used for initiators.','HeartBtInt','',_binary '\0'),(73,'2018-11-13 14:33:12.420',0,NULL,'10','Number of seconds to wait for a logon response before disconnecting.','LogonTimeout','',_binary '\0'),(74,'2018-11-13 14:33:12.427',0,NULL,'2','Number of seconds to wait for a logout response before disconnecting.','LogoutTimeout','',_binary '\0'),(75,'2018-11-13 14:33:12.434',0,NULL,'TCP','Specifies the initiator communication protocol. The SocketConnectHost is not used with the VM_PIPE protocol, but the SocketConnectPort is significant and must match the acceptor configuration.','SocketConnectProtocol','',_binary '\0'),(76,'2018-11-13 14:33:12.441',0,NULL,'','Bind the local socket to this port. Only used with a SocketInitiator. If unset the socket will be bound to a free port from the ephemeral port range.','SocketLocalPort','',_binary '\0'),(77,'2018-11-13 14:33:12.447',0,NULL,'','Bind the local socket to this host. Only used with a SocketAcceptor. If unset the socket will be bound to all local interfaces.','SocketLocalHost','',_binary '\0'),(78,'2018-11-13 14:33:12.453',0,NULL,'TCP','Specifies the acceptor communication protocol. The SocketAcceptAddress is not used with the VM_PIPE protocol, but the SocketAcceptPort is significant and must match the initiator configuration.','SocketAcceptProtocol','',_binary '\0'),(79,'2018-11-13 14:33:12.458',0,'Enter \'Y\' or \'N\'','Y','Refresh the session state when a logon is received. This allows a simple form of failover when the message store data is persistent. The option will be ignored for message stores that are not persistent (e.g., MemoryStore).','RefreshOnLogon','^(Y|N){1}$',_binary '\0'),(80,'2018-11-13 14:33:12.464',0,NULL,'N','Enables SSL usage for QFJ acceptor or initiator.','SocketUseSSL','',_binary '\0'),(81,'2018-11-13 14:33:12.470',0,NULL,'','KeyStore to use with SSL','SocketKeyStore','',_binary '\0'),(82,'2018-11-13 14:33:12.479',0,NULL,'','KeyStore password','SocketKeyStorePassword','',_binary '\0'),(83,'2018-11-13 14:33:12.486',0,NULL,'','When the keepalive option is set for a TCP socket and no data has been exchanged across the socket in either direction for 2 hours (NOTE: the actual value is implementation dependent), TCP automatically sends a keepalive probe to the peer. This probe is a TCP segment to which the peer must respond. One of three responses is expected: The peer responds with the expected ACK. The application is not notified (since everything is OK). TCP will send another probe following another 2 hours of inactivity. The peer responds with an RST, which tells the local TCP that the peer host has crashed and rebooted. The socket is closed. There is no response from the peer. The socket is closed. The purpose of this option is to detect if the peer host crashes.','SocketKeepAlive','',_binary '\0'),(84,'2018-11-13 14:33:12.492',0,NULL,'','When the OOBINLINE option is set, any TCP urgent data received on the socket will be received through the socket input stream. When the option is disabled (which is the default) urgent data is silently discarded.','SocketOobInline','',_binary '\0'),(85,'2018-11-13 14:33:12.498',0,NULL,'','Set a hint the size of the underlying buffers used by the platform for incoming network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be received over the socket.','SocketReceiveBufferSize','',_binary '\0'),(86,'2018-11-13 14:33:12.504',0,NULL,'','Sets SO_REUSEADDR for a socket. This is used only for MulticastSockets in java, and it is set by default for MulticastSockets.','SocketReuseAddress','',_binary '\0'),(87,'2018-11-13 14:33:12.509',0,NULL,'','Set a hint the size of the underlying buffers used by the platform for outgoing network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be sent over the socket.','SocketSendBufferSize','',_binary '\0'),(88,'2018-11-13 14:33:12.515',0,NULL,'','Specify a linger-on-close timeout. This option disables/enables immediate return from a close() of a TCP Socket. Enabling this option with a non-zero Integer timeout means that a close() will block pending the transmission and acknowledgement of all data written to the peer, at which point the socket is closed gracefully. Upon reaching the linger timeout, the socket is closed forcefully, with a TCP RST. Enabling the option with a timeout of zero does a forceful close immediately. If the specified timeout value exceeds 65,535 it will be reduced to 65,535.','SocketLinger','',_binary '\0'),(89,'2018-11-13 14:33:12.522',0,NULL,'Y','Disable Nagle\'s algorithm for this connection. Written data to the network is not buffered pending acknowledgement of previously written data.','SocketTcpNoDelay','',_binary '\0'),(90,'2018-11-13 14:33:12.529',0,NULL,'','Sets traffic class or type-of-service octet in the IP header for packets sent from this Socket. As the underlying network implementation may ignore this value applications should consider it a hint. The tc must be in the range 0 = tc = 255 or an IllegalArgumentException will be thrown. Notes: for Internet Protocol v4 the value consists of an octet with precedence and TOS fields as detailed in RFC 1349. The TOS field is bitset created by bitwise-or\'ing values such the following :- IPTOS_LOWCOST (0x02) IPTOS_RELIABILITY (0x04) IPTOS_THROUGHPUT (0x08) IPTOS_LOWDELAY (0x10) The last low order bit is always ignored as this corresponds to the MBZ (must be zero) bit. Setting bits in the precedence field may result in a SocketException indicating that the operation is not permitted.','SocketTrafficClass','',_binary '\0'),(91,'2018-11-13 14:33:12.538',0,NULL,'N','Write messages synchronously. This is not generally recommended as it may result in performance degradation. The MINA communication layer is asynchronous by design, but this option will override that behavior if needed.','SocketSynchronousWrites','',_binary '\0'),(92,'2018-11-13 14:33:12.545',0,NULL,'30000','The time in milliseconds to wait for a write to complete.','SocketSynchronousWriteTimeout','',_binary '\0'),(93,'2018-11-13 14:33:12.552',0,NULL,'Y','If set to N, no messages will be persisted. This will force QFJ to always send GapFills instead of resending messages. Use this if you know you never want to resend a message. Useful for market data streams.','PersistMessages','',_binary '\0'),(94,'2018-11-13 14:33:12.559',0,NULL,'N','Controls whether milliseconds are included in log time stamps.','FileIncludeMilliseconds','',_binary '\0'),(95,'2018-11-13 14:33:12.566',0,NULL,'N','Controls whether time stamps are included on message log entries.','FileIncludeTimestampForMessages','',_binary '\0'),(96,'2018-11-13 14:33:12.573',0,NULL,'quickfixj.event','Log category for logged events.','SLF4JLogEventCategory','',_binary '\0'),(97,'2018-11-13 14:33:12.580',0,NULL,'quickfixj.msg.incoming','Log category for incoming messages.','SLF4JLogIncomingMessageCategory','',_binary '\0'),(98,'2018-11-13 14:33:12.586',0,NULL,'quickfixj.msg.outgoing','Log category for outgoing messages.','SLF4JLogOutgoingMessageCategory','',_binary '\0'),(99,'2018-11-13 14:33:12.595',0,NULL,'Y','Controls whether session ID is prepended to log message.','SLF4JLogPrependSessionID','',_binary '\0'),(100,'2018-11-13 14:33:12.601',0,NULL,'N','Controls whether heartbeats are logged.','SLF4JLogHeartbeats','',_binary '\0'),(101,'2018-11-13 14:33:12.606',0,NULL,'Y','Log events to screen.','ScreenLogEvents','',_binary '\0'),(102,'2018-11-13 14:33:12.613',0,NULL,'Y','Log incoming messages to screen.','ScreenLogShowIncoming','',_binary '\0'),(103,'2018-11-13 14:33:12.619',0,NULL,'Y','Log outgoing messages to screen.','ScreenLogShowOutgoing','',_binary '\0'),(104,'2018-11-13 14:33:12.626',0,NULL,'N','Filter heartbeats from output (both incoming and outgoing)','ScreenLogShowHeartbeats','',_binary '\0'),(105,'2018-11-13 14:33:12.632',0,NULL,'N','Determines if sequence numbers should be reset before sending/receiving a logon request.','ResetOnLogon','',_binary '\0'),(106,'2018-11-13 14:33:12.638',0,NULL,'N','Determines if sequence numbers should be reset to 1 after a normal logout termination.','ResetOnLogout','',_binary '\0'),(107,'2018-11-13 14:33:12.644',0,NULL,'N','Determines if sequence numbers should be reset to 1 after an abnormal termination.','ResetOnDisconnect','',_binary '\0'),(108,'2018-11-13 14:33:12.651',0,NULL,'N','Session setting for doing an automatic reset when an error occurs. A reset means disconnect, sequence numbers reset, store cleaned and reconnect, as for a daily reset.','ResetOnError','',_binary '\0'),(109,'2018-11-13 14:33:12.658',0,NULL,'N','Session setting for doing an automatic disconnect when an error occurs.','DisconnectOnError','',_binary '\0'),(110,'2018-11-13 14:33:12.664',0,NULL,'N','Add tag LastMsgSeqNumProcessed in the header (optional tag 369).','EnableLastMsgSeqNumProcessed','',_binary '\0'),(111,'2018-11-13 14:33:12.669',0,NULL,'N','Add tag NextExpectedMsgSeqNum (optional tag 789) on the sent Logon message and use value of tag 789 on received Logon message to synchronize session. This should not be enabled for FIX versions lt 4.4. Only works when ValidateSequenceNumbers is enabled.','EnableNextExpectedMsgSeqNum','',_binary '\0'),(112,'2018-11-13 14:33:12.676',0,NULL,'0','Setting to limit the size of a resend request in case of missing messages. This is useful when the remote FIX engine does not allow to ask for more than n message for a ResendRequest. E.g. if the ResendRequestChunkSize is set to 5 and a gap of 7 messages is detected, a first resend request will be sent for 5 messages. When this gap has been filled, another resend request for 2 messages will be sent. If the ResendRequestChunkSize is set to 0, only one ResendRequest for all the missing messages will be sent.','ResendRequestChunkSize','',_binary '\0'),(113,'2018-11-13 14:33:12.683',0,NULL,'N','Continue initializing sessions if an error occurs.','ContinueInitializationOnError','',_binary '\0'),(114,'2018-11-13 14:33:12.689',0,NULL,'N','Allows sending of redundant resend requests.','SendRedundantResendRequests','',_binary '\0'),(115,'2018-11-13 14:33:12.696',0,NULL,'0.5','Fraction of the heartbeat interval which defines the additional time to wait if a TestRequest sent after a missing heartbeat times out.','TestRequestDelayMultiplier','',_binary '\0'),(116,'2018-11-13 14:33:12.701',0,NULL,'N','Heartbeat detection is disabled. A disconnect due to a missing heartbeat will never occur.','DisableHeartBeatCheck','',_binary '\0'),(117,'2018-11-13 14:33:12.710',0,NULL,'N','Fill in heartbeats on resend when reading from message store fails.','ForceResendWhenCorruptedStore','',_binary '\0'),(118,'2018-11-13 14:33:12.717',0,NULL,'','Name of the session modifiers to apply to this session','org.marketcetera.sessioncustomization','',_binary '\0');
/*!40000 ALTER TABLE `fix_session_attr_dscrptrs` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fix_session_attributes`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `fix_session_attributes` (
  `fix_session_id` bigint(20) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`fix_session_id`,`name`),
  CONSTRAINT `FK3lrqyamu7790pie2ivjh8vfq5` FOREIGN KEY (`fix_session_id`) REFERENCES `fix_sessions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fix_session_attributes`
--

LOCK TABLES `fix_session_attributes` WRITE;
/*!40000 ALTER TABLE `fix_session_attributes` DISABLE KEYS */;
INSERT INTO `fix_session_attributes` (`fix_session_id`, `value`, `name`) VALUES (45,'FIX50SP2.xml','AppDataDictionary'),(45,'FIXT.1.1','BeginString'),(45,'initiator','ConnectionType'),(45,'9','DefaultApplVerID'),(45,'22:45:00','EndTime'),(45,'30','HeartBtInt'),(45,'sessionCustomizationAlgoTagsOnly','org.marketcetera.sessioncustomization'),(45,'30','ReconnectInterval'),(45,'Y','RefreshOnLogon'),(45,'N','ResetOnDisconnect'),(45,'N','ResetOnError'),(45,'Y','ResetOnLogon'),(45,'N','ResetOnLogout'),(45,'colin-core-europa','SenderCompID'),(45,'N','SLF4JLogHeartbeats'),(45,'00:00:00','StartTime'),(45,'MRKTC-EXCH','TargetCompID'),(45,'US/Pacific','TimeZone'),(45,'FIXT11.xml','TransportDataDictionary'),(45,'Y','UseDataDictionary');
/*!40000 ALTER TABLE `fix_session_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fix_sessions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `fix_sessions` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `affinity` int(11) NOT NULL,
  `broker_id` varchar(255) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `host` varchar(255) NOT NULL,
  `acceptor` bit(1) NOT NULL,
  `deleted` bit(1) NOT NULL,
  `enabled` bit(1) NOT NULL,
  `name` varchar(255) NOT NULL,
  `port` int(11) NOT NULL,
  `session_id` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fix_sessions`
--

LOCK TABLES `fix_sessions` WRITE;
/*!40000 ALTER TABLE `fix_sessions` DISABLE KEYS */;
INSERT INTO `fix_sessions` (`id`, `last_updated`, `update_count`, `affinity`, `broker_id`, `description`, `host`, `acceptor`, `deleted`, `enabled`, `name`, `port`, `session_id`) VALUES (45,'2018-11-13 14:33:12.156',1,1,'exsim',NULL,'exchange.marketcetera.com',_binary '\0',_binary '\0',_binary '','MATP Exchange Simulator',7001,'FIXT.1.1:colin-core-europa->MRKTC-EXCH');
/*!40000 ALTER TABLE `fix_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hibernate_sequence`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `hibernate_sequence` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hibernate_sequence`
--

LOCK TABLES `hibernate_sequence` WRITE;
/*!40000 ALTER TABLE `hibernate_sequence` DISABLE KEYS */;
INSERT INTO `hibernate_sequence` (`next_val`) VALUES (121),(121),(121),(121),(121),(121),(121),(121),(121),(121),(121),(121),(121),(121),(121),(121),(121);
/*!40000 ALTER TABLE `hibernate_sequence` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `id_repository`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `id_repository` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `next_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `id_repository`
--

LOCK TABLES `id_repository` WRITE;
/*!40000 ALTER TABLE `id_repository` DISABLE KEYS */;
/*!40000 ALTER TABLE `id_repository` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `incoming_fix_messages`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `incoming_fix_messages` (
  `id` bigint(20) NOT NULL,
  `clordid` varchar(255) DEFAULT NULL,
  `execid` varchar(255) DEFAULT NULL,
  `message` varchar(4000) NOT NULL,
  `msg_seq_num` int(11) NOT NULL,
  `msg_type` varchar(255) NOT NULL,
  `sending_time` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `fix_session` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `incoming_fix_messages`
--

LOCK TABLES `incoming_fix_messages` WRITE;
/*!40000 ALTER TABLE `incoming_fix_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `incoming_fix_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message_store_messages`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `message_store_messages` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `session_id` varchar(255) NOT NULL,
  `message` varchar(8192) NOT NULL,
  `msg_seq_num` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message_store_messages`
--

LOCK TABLES `message_store_messages` WRITE;
/*!40000 ALTER TABLE `message_store_messages` DISABLE KEYS */;
INSERT INTO `message_store_messages` (`id`, `last_updated`, `update_count`, `session_id`, `message`, `msg_seq_num`) VALUES (120,'2018-11-13 14:33:16.454',0,'FIXT.1.1:colin-core-europa->MRKTC-EXCH','8=FIXT.1.19=9535=A34=149=colin-core-europa52=20181113-14:33:16.35256=MRKTC-EXCH98=0108=30141=Y1137=910=057',1);
/*!40000 ALTER TABLE `message_store_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `message_store_sessions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `message_store_sessions` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `session_id` varchar(255) NOT NULL,
  `creation_time` timestamp(3) NOT NULL DEFAULT '0000-00-00 00:00:00.000',
  `sender_seq_num` int(11) NOT NULL,
  `target_seq_num` int(11) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `message_store_sessions`
--

LOCK TABLES `message_store_sessions` WRITE;
/*!40000 ALTER TABLE `message_store_sessions` DISABLE KEYS */;
INSERT INTO `message_store_sessions` (`id`, `last_updated`, `update_count`, `session_id`, `creation_time`, `sender_seq_num`, `target_seq_num`) VALUES (119,'2018-11-13 14:33:16.504',4,'FIXT.1.1:colin-core-europa->MRKTC-EXCH','2018-11-13 14:33:16.430',2,2);
/*!40000 ALTER TABLE `message_store_sessions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `order_status`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `order_status` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `account` varchar(255) DEFAULT NULL,
  `avg_px` decimal(17,7) NOT NULL,
  `broker_id` varchar(255) DEFAULT NULL,
  `cum_qty` decimal(17,7) NOT NULL,
  `expiry` varchar(255) DEFAULT NULL,
  `last_px` decimal(17,7) NOT NULL,
  `last_qty` decimal(17,7) NOT NULL,
  `leaves_qty` decimal(17,7) NOT NULL,
  `option_type` int(11) DEFAULT NULL,
  `order_id` varchar(255) NOT NULL,
  `order_px` decimal(17,7) DEFAULT NULL,
  `order_qty` decimal(17,7) NOT NULL,
  `ord_status` varchar(255) NOT NULL,
  `root_order_id` varchar(255) NOT NULL,
  `security_type` int(11) NOT NULL,
  `sending_time` timestamp(3) NOT NULL DEFAULT '0000-00-00 00:00:00.000',
  `side` int(11) NOT NULL,
  `strike_price` decimal(17,7) DEFAULT NULL,
  `symbol` varchar(255) NOT NULL,
  `execution_time` timestamp(3) NOT NULL DEFAULT '0000-00-00 00:00:00.000',
  `actor_id` bigint(20) DEFAULT NULL,
  `report_id` bigint(20) NOT NULL,
  `viewer_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_h8v9n38cydusmk1d0yya6nd2d` (`report_id`),
  KEY `FKlxkg9il8q8k9448o5nggibnbs` (`actor_id`),
  KEY `FKt8iysrml49hnmembw8pkv5g3n` (`viewer_id`),
  CONSTRAINT `FKjqtx22v71dod89tht0fywav7` FOREIGN KEY (`report_id`) REFERENCES `reports` (`id`),
  CONSTRAINT `FKlxkg9il8q8k9448o5nggibnbs` FOREIGN KEY (`actor_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKt8iysrml49hnmembw8pkv5g3n` FOREIGN KEY (`viewer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `order_status`
--

LOCK TABLES `order_status` WRITE;
/*!40000 ALTER TABLE `order_status` DISABLE KEYS */;
/*!40000 ALTER TABLE `order_status` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `outgoing_messages`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `outgoing_messages` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `broker_id` varchar(255) NOT NULL,
  `message_type` varchar(255) NOT NULL,
  `msg_seq_num` int(11) DEFAULT NULL,
  `order_id` varchar(255) DEFAULT NULL,
  `sender_comp_id` varchar(255) NOT NULL,
  `session_id` varchar(255) NOT NULL,
  `target_comp_id` varchar(255) NOT NULL,
  `actor_id` bigint(20) NOT NULL,
  `fix_message_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_hxvc6vrtmw1swik69wxt0drlc` (`fix_message_id`),
  KEY `FKpkya7xsumlsbfm4b125k07ke8` (`actor_id`),
  CONSTRAINT `FK7aeswc52coxk8sspdt9ua5e15` FOREIGN KEY (`fix_message_id`) REFERENCES `fix_messages` (`id`),
  CONSTRAINT `FKpkya7xsumlsbfm4b125k07ke8` FOREIGN KEY (`actor_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `outgoing_messages`
--

LOCK TABLES `outgoing_messages` WRITE;
/*!40000 ALTER TABLE `outgoing_messages` DISABLE KEYS */;
/*!40000 ALTER TABLE `outgoing_messages` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `permissions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `permissions` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_pnvtwliis6p05pn6i3ndjrqt2` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `permissions`
--

LOCK TABLES `permissions` WRITE;
/*!40000 ALTER TABLE `permissions` DISABLE KEYS */;
INSERT INTO `permissions` (`id`, `last_updated`, `update_count`, `description`, `name`) VALUES (5,'2018-11-13 14:33:09.517',0,'Access to Add Session action','AddSessionAction'),(6,'2018-11-13 14:33:09.543',0,'Access to Delete Session action','DeleteSessionAction'),(7,'2018-11-13 14:33:09.562',0,'Access to disable session action','DisableSessionAction'),(8,'2018-11-13 14:33:09.575',0,'Access to edit session action','EditSessionAction'),(9,'2018-11-13 14:33:09.599',0,'Access to enable session action','EnableSessionAction'),(10,'2018-11-13 14:33:09.615',0,'Access to update sequence numbers action','UpdateSequenceAction'),(11,'2018-11-13 14:33:09.633',0,'Access to start session action','StartSessionAction'),(12,'2018-11-13 14:33:09.657',0,'Access to stop session action','StopSessionAction'),(13,'2018-11-13 14:33:09.673',0,'Access to view session action','ViewSessionAction'),(14,'2018-11-13 14:33:09.699',0,'Access to read instance data action','ReadInstanceDataAction'),(15,'2018-11-13 14:33:09.721',0,'Access to read FIX session attribute descriptors action','ReadFixSessionAttributeDescriptorsAction'),(16,'2018-11-13 14:33:09.749',0,'Access to create user action','CreateUserAction'),(17,'2018-11-13 14:33:09.766',0,'Access to read user action','ReadUserAction'),(18,'2018-11-13 14:33:09.792',0,'Access to update user action','UpdateUserAction'),(19,'2018-11-13 14:33:09.832',0,'Access to delete user action','DeleteUserAction'),(20,'2018-11-13 14:33:09.862',0,'Access to change user password action','ChangeUserPasswordAction'),(21,'2018-11-13 14:33:09.881',0,'Access to read user permissions action','ReadUserPermisionsAction'),(22,'2018-11-13 14:33:09.908',0,'Access to create permission action','CreatePermissionAction'),(23,'2018-11-13 14:33:09.944',0,'Access to read permission action','ReadPermissionAction'),(24,'2018-11-13 14:33:09.973',0,'Access to update permission action','UpdatePermissionAction'),(25,'2018-11-13 14:33:09.985',0,'Access to delete permission action','DeletePermissionAction'),(26,'2018-11-13 14:33:10.002',0,'Access to create role action','CreateRoleAction'),(27,'2018-11-13 14:33:10.028',0,'Access to read role action','ReadRoleAction'),(28,'2018-11-13 14:33:10.042',0,'Access to update role action','UpdateRoleAction'),(29,'2018-11-13 14:33:10.070',0,'Access to delete role action','DeleteRoleAction'),(30,'2018-11-13 14:33:10.081',0,'Access to view broker status action','ViewBrokerStatusAction'),(31,'2018-11-13 14:33:10.096',0,'Access to view open orders action','ViewOpenOrdersAction'),(32,'2018-11-13 14:33:10.108',0,'Access to view reports action','ViewReportAction'),(33,'2018-11-13 14:33:10.124',0,'Access to view positions action','ViewPositionAction'),(34,'2018-11-13 14:33:10.133',0,'Access to send new orders action','SendOrderAction'),(35,'2018-11-13 14:33:10.143',0,'Access to view user data action','ViewUserDataAction'),(36,'2018-11-13 14:33:10.157',0,'Access to write user data action','WriteUserDataAction'),(37,'2018-11-13 14:33:10.170',0,'Access to manually add new reports action','AddReportAction'),(38,'2018-11-13 14:33:10.179',0,'Access to manually delete reports action','DeleteReportAction'),(39,'2018-11-13 14:33:10.193',0,'Access to read a user attribute action','ReadUserAttributeAction'),(40,'2018-11-13 14:33:10.202',0,'Access to write a user attribute action','WriteUserAttributeAction');
/*!40000 ALTER TABLE `permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reports`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `reports` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `broker_id` varchar(255) DEFAULT NULL,
  `hierarchy` int(11) DEFAULT NULL,
  `originator` int(11) DEFAULT NULL,
  `report_type` int(11) NOT NULL,
  `msg_seq_num` int(11) NOT NULL,
  `order_id` varchar(255) NOT NULL,
  `report_id` bigint(20) NOT NULL,
  `send_time` timestamp(3) NOT NULL DEFAULT '0000-00-00 00:00:00.000',
  `session_id` varchar(255) NOT NULL,
  `actor_id` bigint(20) DEFAULT NULL,
  `fix_message_id` bigint(20) NOT NULL,
  `viewer_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ca90a4kkdycpon22ynli3d6oi` (`fix_message_id`),
  UNIQUE KEY `UK_aely7chrvtqwv4xfm76xuj5bh` (`report_id`),
  KEY `FKh0r6ppu75byn1y7y0uiteel8q` (`actor_id`),
  KEY `FKsfc0wdpjferohmpylygff4urs` (`viewer_id`),
  CONSTRAINT `FK98bmvk76e2gp10muheog0j1wa` FOREIGN KEY (`fix_message_id`) REFERENCES `fix_messages` (`id`),
  CONSTRAINT `FKh0r6ppu75byn1y7y0uiteel8q` FOREIGN KEY (`actor_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKsfc0wdpjferohmpylygff4urs` FOREIGN KEY (`viewer_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reports`
--

LOCK TABLES `reports` WRITE;
/*!40000 ALTER TABLE `reports` DISABLE KEYS */;
/*!40000 ALTER TABLE `reports` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `roles` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` (`id`, `last_updated`, `update_count`, `description`, `name`) VALUES (41,'2018-11-13 14:33:10.337',0,'Admin role','Admin'),(42,'2018-11-13 14:33:10.443',0,'Trader role','Trader'),(43,'2018-11-13 14:33:10.522',0,'Trader Admin role','TraderAdmin');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles_permissions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `roles_permissions` (
  `roles_id` bigint(20) NOT NULL,
  `permissions_id` bigint(20) NOT NULL,
  PRIMARY KEY (`roles_id`,`permissions_id`),
  KEY `FK570wuy6sacdnrw8wdqjfh7j0q` (`permissions_id`),
  CONSTRAINT `FK570wuy6sacdnrw8wdqjfh7j0q` FOREIGN KEY (`permissions_id`) REFERENCES `permissions` (`id`),
  CONSTRAINT `FKb9gqc5kvla3ijovnihsbb816e` FOREIGN KEY (`roles_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles_permissions`
--

LOCK TABLES `roles_permissions` WRITE;
/*!40000 ALTER TABLE `roles_permissions` DISABLE KEYS */;
INSERT INTO `roles_permissions` (`roles_id`, `permissions_id`) VALUES (41,5),(41,6),(41,7),(41,8),(41,9),(41,10),(41,11),(41,12),(41,13),(41,14),(41,15),(41,16),(41,17),(41,18),(41,19),(41,20),(41,21),(41,22),(41,23),(41,24),(41,25),(41,26),(41,27),(41,28),(41,29),(41,30),(42,30),(43,30),(42,31),(43,31),(42,32),(43,32),(42,33),(43,33),(42,34),(43,34),(41,35),(42,35),(43,35),(41,36),(42,36),(43,36),(42,37),(43,37),(43,38),(41,39),(42,39),(43,39),(41,40),(42,40),(43,40);
/*!40000 ALTER TABLE `roles_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles_users`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `roles_users` (
  `Role_id` bigint(20) NOT NULL,
  `subjects_id` bigint(20) NOT NULL,
  PRIMARY KEY (`Role_id`,`subjects_id`),
  KEY `FKjdau0sn88gj3b7oiym39qaymk` (`subjects_id`),
  CONSTRAINT `FKjdau0sn88gj3b7oiym39qaymk` FOREIGN KEY (`subjects_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKrxa1kwvac3vq2p3a4aus28m3p` FOREIGN KEY (`Role_id`) REFERENCES `roles` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles_users`
--

LOCK TABLES `roles_users` WRITE;
/*!40000 ALTER TABLE `roles_users` DISABLE KEYS */;
INSERT INTO `roles_users` (`Role_id`, `subjects_id`) VALUES (41,1),(42,3),(43,4);
/*!40000 ALTER TABLE `roles_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supervisor_permissions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `supervisor_permissions` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `user_id` bigint(20) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_4rd5towbshlb1v1hv6w00sf6b` (`name`),
  KEY `FKf7mxsack9d04s94a2aha7jyr9` (`user_id`),
  CONSTRAINT `FKf7mxsack9d04s94a2aha7jyr9` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supervisor_permissions`
--

LOCK TABLES `supervisor_permissions` WRITE;
/*!40000 ALTER TABLE `supervisor_permissions` DISABLE KEYS */;
INSERT INTO `supervisor_permissions` (`id`, `last_updated`, `update_count`, `description`, `name`, `user_id`) VALUES (44,'2018-11-13 14:33:10.576',0,'Trader supervisor role','TraderSupervisor',4);
/*!40000 ALTER TABLE `supervisor_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supervisor_permissions_permissions`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `supervisor_permissions_permissions` (
  `SupervisorPermission_id` bigint(20) NOT NULL,
  `permissions_id` bigint(20) NOT NULL,
  PRIMARY KEY (`SupervisorPermission_id`,`permissions_id`),
  KEY `FKtkctb5n3ggmu727f5mwlmutk3` (`permissions_id`),
  CONSTRAINT `FK6b5t61soynlxlyrmb8y59y6tf` FOREIGN KEY (`SupervisorPermission_id`) REFERENCES `supervisor_permissions` (`id`),
  CONSTRAINT `FKtkctb5n3ggmu727f5mwlmutk3` FOREIGN KEY (`permissions_id`) REFERENCES `permissions` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supervisor_permissions_permissions`
--

LOCK TABLES `supervisor_permissions_permissions` WRITE;
/*!40000 ALTER TABLE `supervisor_permissions_permissions` DISABLE KEYS */;
INSERT INTO `supervisor_permissions_permissions` (`SupervisorPermission_id`, `permissions_id`) VALUES (44,30),(44,31),(44,32),(44,33),(44,35);
/*!40000 ALTER TABLE `supervisor_permissions_permissions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `supervisor_permissions_users`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `supervisor_permissions_users` (
  `SupervisorPermission_id` bigint(20) NOT NULL,
  `subjects_id` bigint(20) NOT NULL,
  PRIMARY KEY (`SupervisorPermission_id`,`subjects_id`),
  KEY `FKl8hk9cj6mavq8oqvun76xl3ag` (`subjects_id`),
  CONSTRAINT `FK16n73q253vu1unemupobm3ekj` FOREIGN KEY (`SupervisorPermission_id`) REFERENCES `supervisor_permissions` (`id`),
  CONSTRAINT `FKl8hk9cj6mavq8oqvun76xl3ag` FOREIGN KEY (`subjects_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `supervisor_permissions_users`
--

LOCK TABLES `supervisor_permissions_users` WRITE;
/*!40000 ALTER TABLE `supervisor_permissions_users` DISABLE KEYS */;
INSERT INTO `supervisor_permissions_users` (`SupervisorPermission_id`, `subjects_id`) VALUES (44,3);
/*!40000 ALTER TABLE `supervisor_permissions_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_info`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `system_info` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `value` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_457m1gi0j3jft2b5wq33iccxk` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_info`
--

LOCK TABLES `system_info` WRITE;
/*!40000 ALTER TABLE `system_info` DISABLE KEYS */;
INSERT INTO `system_info` (`id`, `last_updated`, `update_count`, `description`, `name`, `value`) VALUES (2,'2018-11-13 14:33:01.909',0,'indicates current database schema version','schema version','7');
/*!40000 ALTER TABLE `system_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_attributes`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `user_attributes` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `attribute` longtext NOT NULL,
  `user_attribute_type` int(11) NOT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKskw1x6g2kt3g0i9507k4a4tqw` (`user_id`),
  CONSTRAINT `FKskw1x6g2kt3g0i9507k4a4tqw` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_attributes`
--

LOCK TABLES `user_attributes` WRITE;
/*!40000 ALTER TABLE `user_attributes` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `users` (
  `id` bigint(20) NOT NULL,
  `last_updated` timestamp(3) NOT NULL DEFAULT CURRENT_TIMESTAMP(3) ON UPDATE CURRENT_TIMESTAMP(3),
  `update_count` int(11) NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `is_active` bit(1) NOT NULL,
  `password` varchar(255) NOT NULL,
  `is_superuser` bit(1) NOT NULL,
  `user_data` longtext,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK3g1j96g94xpk3lpxl2qbl985x` (`name`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` (`id`, `last_updated`, `update_count`, `description`, `name`, `is_active`, `password`, `is_superuser`, `user_data`) VALUES (1,'2018-11-13 14:33:01.844',0,NULL,'admin',_binary '','6anqbgybi82pveayzrkt3egjkwfwdg5',_binary '',NULL),(3,'2018-11-13 14:33:09.437',0,'Trader user','trader',_binary '','2zg91043ou3eki4ysbejwwgkci37e6j',_binary '\0',NULL),(4,'2018-11-13 14:33:09.487',0,'Trader Admin user','traderAdmin',_binary '','210ui1dyyf6voajrad4gmpt3vgvvm9o',_binary '\0',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2018-11-13  6:37:19
