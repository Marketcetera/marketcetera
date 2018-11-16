CREATE TABLE METC.EXEC_REPORTS (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	ACCOUNT VARCHAR(255),
	AVG_PRICE NUMERIC(17,7) NOT NULL,
	CUM_QTY NUMERIC(17,7) NOT NULL,
	EFF_CUM_QTY NUMERIC(17,7) NOT NULL,
	EXEC_TYPE INTEGER NOT NULL,
	EXPIRY VARCHAR(255),
	LAST_PRICE NUMERIC(17,7),
	LAST_QTY NUMERIC(17,7),
	OPTION_TYPE INTEGER,
	ORDER_ID VARCHAR(255) NOT NULL,
	ORD_STATUS INTEGER NOT NULL,
	ORIG_ORDER_ID VARCHAR(255),
	ROOT_ORDER_ID VARCHAR(255) NOT NULL,
	SECURITY_TYPE INTEGER NOT NULL,
	SEND_TIME TIMESTAMP NOT NULL,
	SIDE INTEGER NOT NULL,
	STRIKE_PRICE NUMERIC(17,7),
	SYMBOL VARCHAR(255) NOT NULL,
	ACTOR_ID BIGINT,
	REPORT_ID BIGINT NOT NULL,
	VIEWER_ID BIGINT,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.FIX_MESSAGES (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	MESSAGE CLOB(8192) NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.FIX_SESSION_ATTR_DSCRPTRS (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	ADVICE VARCHAR(255),
	DEFAULT_VALUE VARCHAR(255),
	DESCRIPTION VARCHAR(1024),
	NAME VARCHAR(255) NOT NULL,
	PATTERN VARCHAR(255),
	REQUIRED BOOLEAN NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.FIX_SESSION_ATTRIBUTES (
	FIX_SESSION_ID BIGINT NOT NULL,
	VALUE VARCHAR(255),
	NAME VARCHAR(255) NOT NULL,
	PRIMARY KEY (FIX_SESSION_ID,NAME)
);
CREATE TABLE METC.FIX_SESSIONS (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	AFFINITY INTEGER NOT NULL,
	BROKER_ID VARCHAR(255) NOT NULL,
	DESCRIPTION VARCHAR(255),
	HOST VARCHAR(255) NOT NULL,
	ACCEPTOR BOOLEAN NOT NULL,
	DELETED BOOLEAN NOT NULL,
	ENABLED BOOLEAN NOT NULL,
	NAME VARCHAR(255) NOT NULL,
	PORT INTEGER NOT NULL,
	SESSION_ID VARCHAR(255) NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.ID_REPOSITORY (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	NEXT_ID BIGINT,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.INCOMING_FIX_MESSAGES (
	ID BIGINT NOT NULL,
	CLORDID VARCHAR(255),
	EXECID VARCHAR(255),
	MESSAGE VARCHAR(4000) NOT NULL,
	MSG_SEQ_NUM INTEGER NOT NULL,
	MSG_TYPE VARCHAR(255) NOT NULL,
	SENDING_TIME TIMESTAMP NOT NULL,
	FIX_SESSION VARCHAR(255) NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.MESSAGE_STORE_MESSAGES (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	SESSION_ID VARCHAR(255) NOT NULL,
	MESSAGE VARCHAR(8192) NOT NULL,
	MSG_SEQ_NUM INTEGER NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.MESSAGE_STORE_SESSIONS (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	SESSION_ID VARCHAR(255) NOT NULL,
	CREATION_TIME TIMESTAMP NOT NULL,
	SENDER_SEQ_NUM INTEGER NOT NULL,
	TARGET_SEQ_NUM INTEGER NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.ORDER_STATUS (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	ACCOUNT VARCHAR(255),
	AVG_PX NUMERIC(17,7) NOT NULL,
	BROKER_ID VARCHAR(255),
	CUM_QTY NUMERIC(17,7) NOT NULL,
	EXPIRY VARCHAR(255),
	LAST_PX NUMERIC(17,7) NOT NULL,
	LAST_QTY NUMERIC(17,7) NOT NULL,
	LEAVES_QTY NUMERIC(17,7) NOT NULL,
	OPTION_TYPE INTEGER,
	ORDER_ID VARCHAR(255) NOT NULL,
	ORDER_PX NUMERIC(17,7),
	ORDER_QTY NUMERIC(17,7) NOT NULL,
	ORD_STATUS VARCHAR(255) NOT NULL,
	ROOT_ORDER_ID VARCHAR(255) NOT NULL,
	SECURITY_TYPE INTEGER NOT NULL,
	SENDING_TIME TIMESTAMP NOT NULL,
	SIDE INTEGER NOT NULL,
	STRIKE_PRICE NUMERIC(17,7),
	SYMBOL VARCHAR(255) NOT NULL,
	EXECUTION_TIME TIMESTAMP,
	ACTOR_ID BIGINT,
	REPORT_ID BIGINT NOT NULL,
	VIEWER_ID BIGINT,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.OUTGOING_MESSAGES (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	BROKER_ID VARCHAR(255) NOT NULL,
	MESSAGE_TYPE VARCHAR(255) NOT NULL,
	MSG_SEQ_NUM INTEGER,
	ORDER_ID VARCHAR(255),
	SENDER_COMP_ID VARCHAR(255) NOT NULL,
	SESSION_ID VARCHAR(255) NOT NULL,
	TARGET_COMP_ID VARCHAR(255) NOT NULL,
	ACTOR_ID BIGINT NOT NULL,
	FIX_MESSAGE_ID BIGINT NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.PERMISSIONS (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	DESCRIPTION VARCHAR(255),
	NAME VARCHAR(255) NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.REPORTS (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	BROKER_ID VARCHAR(255),
	HIERARCHY INTEGER,
	ORIGINATOR INTEGER,
	REPORT_TYPE INTEGER NOT NULL,
	MSG_SEQ_NUM INTEGER NOT NULL,
	ORDER_ID VARCHAR(255) NOT NULL,
	REPORT_ID BIGINT NOT NULL,
	SEND_TIME TIMESTAMP NOT NULL,
	SESSION_ID VARCHAR(255) NOT NULL,
	ACTOR_ID BIGINT,
	FIX_MESSAGE_ID BIGINT NOT NULL,
	VIEWER_ID BIGINT,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.ROLES (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	DESCRIPTION VARCHAR(255),
	NAME VARCHAR(255) NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.ROLES_PERMISSIONS (
	ROLES_ID BIGINT NOT NULL,
	PERMISSIONS_ID BIGINT NOT NULL,
	PRIMARY KEY (PERMISSIONS_ID,ROLES_ID)
);
CREATE TABLE METC.ROLES_USERS (
	ROLE_ID BIGINT NOT NULL,
	SUBJECTS_ID BIGINT NOT NULL,
	PRIMARY KEY (ROLE_ID,SUBJECTS_ID)
);
CREATE TABLE METC.SUPERVISOR_PERMISSIONS (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	DESCRIPTION VARCHAR(255),
	NAME VARCHAR(255) NOT NULL,
	USER_ID BIGINT NOT NULL,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.SUPERVISOR_PERMISSIONS_PERMISSIONS (
	SUPERVISORPERMISSION_ID BIGINT NOT NULL,
	PERMISSIONS_ID BIGINT NOT NULL,
	PRIMARY KEY (PERMISSIONS_ID,SUPERVISORPERMISSION_ID)
);
CREATE TABLE METC.SUPERVISOR_PERMISSIONS_USERS (
	SUPERVISORPERMISSION_ID BIGINT NOT NULL,
	SUBJECTS_ID BIGINT NOT NULL,
	PRIMARY KEY (SUBJECTS_ID,SUPERVISORPERMISSION_ID)
);
CREATE TABLE METC.USER_ATTRIBUTES (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	ATTRIBUTE CLOB(262144) NOT NULL,
	USER_ATTRIBUTE_TYPE INTEGER NOT NULL,
	USER_ID BIGINT,
	PRIMARY KEY (ID)
);
CREATE TABLE METC.USERS (
	ID BIGINT NOT NULL,
	LAST_UPDATED TIMESTAMP NOT NULL,
	UPDATE_COUNT INTEGER NOT NULL,
	DESCRIPTION VARCHAR(255),
	NAME VARCHAR(255) NOT NULL,
	IS_ACTIVE BOOLEAN NOT NULL,
	PASSWORD VARCHAR(255) NOT NULL,
	IS_SUPERUSER BOOLEAN NOT NULL,
	USER_DATA CLOB(8096),
	PRIMARY KEY (ID)
);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (45, '2018-11-16 10:25:11.41', 0, null, '', '(Optional) Your subID as associated with this FIX session', 'SenderSubID', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (46, '2018-11-16 10:25:11.423', 0, null, '', '(Optional) Your locationID as associated with this FIX session', 'SenderLocationID', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (47, '2018-11-16 10:25:11.428', 0, null, '', '(Optional) counterparty''s subID as associated with this FIX session', 'TargetSubID', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (48, '2018-11-16 10:25:11.433', 0, null, '', '(Optional) counterparty''s locationID as associated with this FIX session', 'TargetLocationID', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (49, '2018-11-16 10:25:11.439', 0, null, '', 'Additional qualifier to disambiguate otherwise identical sessions. This can only be used with initiator sessions.Note: See Special notes for Oracle.', 'SessionQualifier', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (50, '2018-11-16 10:25:11.444', 0, null, '', 'Required only for FIXT 1.1 (and newer). Ignored for earlier transport versions. Specifies the default application version ID for the session. This can either be the ApplVerID enum (see the ApplVerID field) the beginString for the default version.', 'DefaultApplVerID', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (51, '2018-11-16 10:25:11.45', 0, null, 'Y', 'Determines if milliseconds should be added to timestamps. Only available for FIX.4.2 and greater.', 'MillisecondsInTimeStamp', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (52, '2018-11-16 10:25:11.455', 0, null, 'N', 'Use actual end of sequence gap for resend requests rather than using ''''infinity'''' as the end sequence of the gap. Not recommended by the FIX specification, but needed for some counterparties.', 'ClosedResendInterval', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (53, '2018-11-16 10:25:11.46', 0, null, 'Y', 'Tell session whether or not to expect a data dictionary. You should always use a DataDictionary if you are using repeating groups.', 'UseDataDictionary', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (54, '2018-11-16 10:25:11.466', 0, null, 'FIX42.xml', 'XML definition file for validating incoming FIX messages. If no DataDictionary is supplied, only basic message validation will be done. This setting should only be used with FIX transport versions old than FIXT 1.1. See TransportDataDictionary and ApplicationDataDictionary for FIXT 1.1 settings.', 'DataDictionary', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (55, '2018-11-16 10:25:11.471', 0, null, '', 'XML definition file for validating admin (transport) messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information.', 'TransportDataDictionary', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (56, '2018-11-16 10:25:11.476', 0, null, '', 'XML definition file for validating application messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information. This setting supports the possibility of a custom application data dictionary for each session. This setting would only be used with FIXT 1.1 and new transport protocols. This setting can be used as a prefix to specify multiple application dictionaries for the FIXT transport. For example: DefaultApplVerID=FIX.4.2 # For default application version ID AppDataDictionary=FIX42.xml # For nondefault application version ID # Use beginString suffix for app version AppDataDictionary.FIX.4.4=FIX44.xml This would use FIX42.xml for the default application version ID and FIX44.xml for any FIX 4.4 messages.', 'AppDataDictionary', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (57, '2018-11-16 10:25:11.482', 0, null, 'Y', 'If set to N, fields that are out of order (i.e. body fields in the header, or header fields in the body) will not be rejected. Useful for connecting to systems which do not properly order fields.', 'ValidateFieldsOutOfOrder', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (58, '2018-11-16 10:25:11.487', 0, null, 'Y', 'If set to N, fields without values (empty) will not be rejected. Useful for connecting to systems which improperly send empty tags.', 'ValidateFieldsHaveValues', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (59, '2018-11-16 10:25:11.492', 0, null, 'Y', 'If set to N, user defined fields will not be rejected if they are not defined in the data dictionary, or are present in messages they do not belong to.', 'ValidateUserDefinedFields', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (60, '2018-11-16 10:25:11.497', 0, null, 'Y', 'Session validation setting for enabling whether field ordering is * validated. Values are ''''Y'''' or ''''N''''. Default is ''''Y''''.', 'ValidateUnorderedGroupFields', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (61, '2018-11-16 10:25:11.503', 0, null, 'Y', 'Allow to bypass the message validation (against the dictionary). Default is ''''Y''''.', 'ValidateIncomingMessage', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (62, '2018-11-16 10:25:11.508', 0, null, 'Y', 'Check the next expected target SeqNum against the received SeqNum. Default is ''''Y''''. If enabled and a mismatch is detected, apply the following logic:if lower than expected SeqNum , logout if higher, send a resend request If not enabled and a mismatch is detected, nothing is done. Must be enabled for EnableNextExpectedMsgSeqNum to work.', 'ValidateSequenceNumbers', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (63, '2018-11-16 10:25:11.514', 0, null, 'N', 'Allow unknown fields in messages. This is intended for unknown fields with tags lt 5000 (not user defined fields). Use ValidateUserDefinedFields for controlling validation of tags ge 5000.', 'AllowUnknownMsgFields', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (64, '2018-11-16 10:25:11.519', 0, null, 'Y', 'If set to Y, messages must be received from the counterparty with the correct SenderCompID and TargetCompID. Some systems will send you different CompIDs by design, so you must set this to N.', 'CheckCompID', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (65, '2018-11-16 10:25:11.525', 0, null, 'Y', 'If set to Y, messages must be received from the counterparty within a defined number of seconds (see MaxLatency). It is useful to turn this off if a system uses localtime for its timestamps instead of GMT.', 'CheckLatency', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (66, '2018-11-16 10:25:11.53', 0, null, '120', 'If CheckLatency is set to Y, this defines the number of seconds latency allowed for a message to be processed.', 'MaxLatency', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (67, '2018-11-16 10:25:11.535', 0, null, 'Y', 'If RejectInvalidMessage is set to N, only a warning will be logged on reception of message that fails data dictionary validation.', 'RejectInvalidMessage', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (68, '2018-11-16 10:25:11.541', 0, null, 'N', 'If this configuration is enabled, an uncaught Exception or Error in the application''s message processing will lead to a (BusinessMessage)Reject being sent to the counterparty and the incoming message sequence number will be incremented. If disabled (default), the problematic incoming message is discarded and the message sequence number is not incremented. Processing of the next valid message will cause detection of a sequence gap and a ResendRequest will be generated.', 'RejectMessageOnUnhandledException', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (69, '2018-11-16 10:25:11.546', 0, null, 'Y', 'If RequiresOrigSendingTime is set to N, PossDup messages lacking that field will not be rejected.', 'RequiresOrigSendingTime', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (70, '2018-11-16 10:25:11.552', 0, null, '30', 'Time between reconnection attempts in seconds. Only used for initiators', 'ReconnectInterval', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (71, '2018-11-16 10:25:11.557', 0, null, '30', 'Heartbeat interval in seconds. Only used for initiators.', 'HeartBtInt', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (72, '2018-11-16 10:25:11.563', 0, null, '10', 'Number of seconds to wait for a logon response before disconnecting.', 'LogonTimeout', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (73, '2018-11-16 10:25:11.569', 0, null, '2', 'Number of seconds to wait for a logout response before disconnecting.', 'LogoutTimeout', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (74, '2018-11-16 10:25:11.575', 0, null, 'TCP', 'Specifies the initiator communication protocol. The SocketConnectHost is not used with the VM_PIPE protocol, but the SocketConnectPort is significant and must match the acceptor configuration.', 'SocketConnectProtocol', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (75, '2018-11-16 10:25:11.58', 0, null, '', 'Bind the local socket to this port. Only used with a SocketInitiator. If unset the socket will be bound to a free port from the ephemeral port range.', 'SocketLocalPort', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (76, '2018-11-16 10:25:11.584', 0, null, '', 'Bind the local socket to this host. Only used with a SocketAcceptor. If unset the socket will be bound to all local interfaces.', 'SocketLocalHost', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (77, '2018-11-16 10:25:11.589', 0, null, 'TCP', 'Specifies the acceptor communication protocol. The SocketAcceptAddress is not used with the VM_PIPE protocol, but the SocketAcceptPort is significant and must match the initiator configuration.', 'SocketAcceptProtocol', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (78, '2018-11-16 10:25:11.594', 0, 'Enter ''Y'' or ''N''', 'Y', 'Refresh the session state when a logon is received. This allows a simple form of failover when the message store data is persistent. The option will be ignored for message stores that are not persistent (e.g., MemoryStore).', 'RefreshOnLogon', '^(Y|N){1}$', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (79, '2018-11-16 10:25:11.6', 0, null, 'N', 'Enables SSL usage for QFJ acceptor or initiator.', 'SocketUseSSL', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (80, '2018-11-16 10:25:11.606', 0, null, '', 'KeyStore to use with SSL', 'SocketKeyStore', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (81, '2018-11-16 10:25:11.611', 0, null, '', 'KeyStore password', 'SocketKeyStorePassword', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (82, '2018-11-16 10:25:11.617', 0, null, '', 'When the keepalive option is set for a TCP socket and no data has been exchanged across the socket in either direction for 2 hours (NOTE: the actual value is implementation dependent), TCP automatically sends a keepalive probe to the peer. This probe is a TCP segment to which the peer must respond. One of three responses is expected: The peer responds with the expected ACK. The application is not notified (since everything is OK). TCP will send another probe following another 2 hours of inactivity. The peer responds with an RST, which tells the local TCP that the peer host has crashed and rebooted. The socket is closed. There is no response from the peer. The socket is closed. The purpose of this option is to detect if the peer host crashes.', 'SocketKeepAlive', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (83, '2018-11-16 10:25:11.623', 0, null, '', 'When the OOBINLINE option is set, any TCP urgent data received on the socket will be received through the socket input stream. When the option is disabled (which is the default) urgent data is silently discarded.', 'SocketOobInline', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (84, '2018-11-16 10:25:11.628', 0, null, '', 'Set a hint the size of the underlying buffers used by the platform for incoming network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be received over the socket.', 'SocketReceiveBufferSize', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (85, '2018-11-16 10:25:11.633', 0, null, '', 'Sets SO_REUSEADDR for a socket. This is used only for MulticastSockets in java, and it is set by default for MulticastSockets.', 'SocketReuseAddress', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (86, '2018-11-16 10:25:11.638', 0, null, '', 'Set a hint the size of the underlying buffers used by the platform for outgoing network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be sent over the socket.', 'SocketSendBufferSize', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (87, '2018-11-16 10:25:11.642', 0, null, '', 'Specify a linger-on-close timeout. This option disables/enables immediate return from a close() of a TCP Socket. Enabling this option with a non-zero Integer timeout means that a close() will block pending the transmission and acknowledgement of all data written to the peer, at which point the socket is closed gracefully. Upon reaching the linger timeout, the socket is closed forcefully, with a TCP RST. Enabling the option with a timeout of zero does a forceful close immediately. If the specified timeout value exceeds 65,535 it will be reduced to 65,535.', 'SocketLinger', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (88, '2018-11-16 10:25:11.647', 0, null, 'Y', 'Disable Nagle''s algorithm for this connection. Written data to the network is not buffered pending acknowledgement of previously written data.', 'SocketTcpNoDelay', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (89, '2018-11-16 10:25:11.652', 0, null, '', 'Sets traffic class or type-of-service octet in the IP header for packets sent from this Socket. As the underlying network implementation may ignore this value applications should consider it a hint. The tc must be in the range 0 = tc = 255 or an IllegalArgumentException will be thrown. Notes: for Internet Protocol v4 the value consists of an octet with precedence and TOS fields as detailed in RFC 1349. The TOS field is bitset created by bitwise-or''ing values such the following :- IPTOS_LOWCOST (0x02) IPTOS_RELIABILITY (0x04) IPTOS_THROUGHPUT (0x08) IPTOS_LOWDELAY (0x10) The last low order bit is always ignored as this corresponds to the MBZ (must be zero) bit. Setting bits in the precedence field may result in a SocketException indicating that the operation is not permitted.', 'SocketTrafficClass', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (90, '2018-11-16 10:25:11.657', 0, null, 'N', 'Write messages synchronously. This is not generally recommended as it may result in performance degradation. The MINA communication layer is asynchronous by design, but this option will override that behavior if needed.', 'SocketSynchronousWrites', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (91, '2018-11-16 10:25:11.661', 0, null, '30000', 'The time in milliseconds to wait for a write to complete.', 'SocketSynchronousWriteTimeout', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (92, '2018-11-16 10:25:11.666', 0, null, 'Y', 'If set to N, no messages will be persisted. This will force QFJ to always send GapFills instead of resending messages. Use this if you know you never want to resend a message. Useful for market data streams.', 'PersistMessages', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (93, '2018-11-16 10:25:11.671', 0, null, 'N', 'Controls whether milliseconds are included in log time stamps.', 'FileIncludeMilliseconds', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (94, '2018-11-16 10:25:11.676', 0, null, 'N', 'Controls whether time stamps are included on message log entries.', 'FileIncludeTimestampForMessages', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (95, '2018-11-16 10:25:11.681', 0, null, 'quickfixj.event', 'Log category for logged events.', 'SLF4JLogEventCategory', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (96, '2018-11-16 10:25:11.685', 0, null, 'quickfixj.msg.incoming', 'Log category for incoming messages.', 'SLF4JLogIncomingMessageCategory', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (97, '2018-11-16 10:25:11.69', 0, null, 'quickfixj.msg.outgoing', 'Log category for outgoing messages.', 'SLF4JLogOutgoingMessageCategory', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (98, '2018-11-16 10:25:11.702', 0, null, 'Y', 'Controls whether session ID is prepended to log message.', 'SLF4JLogPrependSessionID', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (99, '2018-11-16 10:25:11.707', 0, null, 'N', 'Controls whether heartbeats are logged.', 'SLF4JLogHeartbeats', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (100, '2018-11-16 10:25:11.712', 0, null, 'Y', 'Log events to screen.', 'ScreenLogEvents', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (101, '2018-11-16 10:25:11.717', 0, null, 'Y', 'Log incoming messages to screen.', 'ScreenLogShowIncoming', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (102, '2018-11-16 10:25:11.726', 0, null, 'Y', 'Log outgoing messages to screen.', 'ScreenLogShowOutgoing', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (103, '2018-11-16 10:25:11.73', 0, null, 'N', 'Filter heartbeats from output (both incoming and outgoing)', 'ScreenLogShowHeartbeats', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (104, '2018-11-16 10:25:11.735', 0, null, 'N', 'Determines if sequence numbers should be reset before sending/receiving a logon request.', 'ResetOnLogon', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (105, '2018-11-16 10:25:11.74', 0, null, 'N', 'Determines if sequence numbers should be reset to 1 after a normal logout termination.', 'ResetOnLogout', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (106, '2018-11-16 10:25:11.744', 0, null, 'N', 'Determines if sequence numbers should be reset to 1 after an abnormal termination.', 'ResetOnDisconnect', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (107, '2018-11-16 10:25:11.749', 0, null, 'N', 'Session setting for doing an automatic reset when an error occurs. A reset means disconnect, sequence numbers reset, store cleaned and reconnect, as for a daily reset.', 'ResetOnError', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (108, '2018-11-16 10:25:11.754', 0, null, 'N', 'Session setting for doing an automatic disconnect when an error occurs.', 'DisconnectOnError', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (109, '2018-11-16 10:25:11.759', 0, null, 'N', 'Add tag LastMsgSeqNumProcessed in the header (optional tag 369).', 'EnableLastMsgSeqNumProcessed', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (110, '2018-11-16 10:25:11.763', 0, null, 'N', 'Add tag NextExpectedMsgSeqNum (optional tag 789) on the sent Logon message and use value of tag 789 on received Logon message to synchronize session. This should not be enabled for FIX versions lt 4.4. Only works when ValidateSequenceNumbers is enabled.', 'EnableNextExpectedMsgSeqNum', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (111, '2018-11-16 10:25:11.768', 0, null, '0', 'Setting to limit the size of a resend request in case of missing messages. This is useful when the remote FIX engine does not allow to ask for more than n message for a ResendRequest. E.g. if the ResendRequestChunkSize is set to 5 and a gap of 7 messages is detected, a first resend request will be sent for 5 messages. When this gap has been filled, another resend request for 2 messages will be sent. If the ResendRequestChunkSize is set to 0, only one ResendRequest for all the missing messages will be sent.', 'ResendRequestChunkSize', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (112, '2018-11-16 10:25:11.773', 0, null, 'N', 'Continue initializing sessions if an error occurs.', 'ContinueInitializationOnError', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (113, '2018-11-16 10:25:11.777', 0, null, 'N', 'Allows sending of redundant resend requests.', 'SendRedundantResendRequests', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (114, '2018-11-16 10:25:11.782', 0, null, '0.5', 'Fraction of the heartbeat interval which defines the additional time to wait if a TestRequest sent after a missing heartbeat times out.', 'TestRequestDelayMultiplier', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (115, '2018-11-16 10:25:11.793', 0, null, 'N', 'Heartbeat detection is disabled. A disconnect due to a missing heartbeat will never occur.', 'DisableHeartBeatCheck', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (116, '2018-11-16 10:25:11.798', 0, null, 'N', 'Fill in heartbeats on resend when reading from message store fails.', 'ForceResendWhenCorruptedStore', '', false);
INSERT INTO METC.FIX_SESSION_ATTR_DSCRPTRS(ID, LAST_UPDATED, UPDATE_COUNT, ADVICE, DEFAULT_VALUE, DESCRIPTION, NAME, PATTERN, REQUIRED) VALUES (117, '2018-11-16 10:25:11.803', 0, null, '', 'Name of the session modifiers to apply to this session', 'org.marketcetera.sessioncustomization', '', false);
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'N', 'ResetOnDisconnect');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'FIXT.1.1', 'BeginString');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'colin-core-callisto', 'SenderCompID');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'initiator', 'ConnectionType');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, '22:45:00', 'EndTime');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, '30', 'ReconnectInterval');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'MRKTC-EXCH', 'TargetCompID');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, '00:00:00', 'StartTime');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, '9', 'DefaultApplVerID');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'Y', 'UseDataDictionary');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'FIXT11.xml', 'TransportDataDictionary');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'N', 'ResetOnError');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'US/Pacific', 'TimeZone');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'N', 'ResetOnLogout');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'Y', 'RefreshOnLogon');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'N', 'SLF4JLogHeartbeats');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'FIX50SP2.xml', 'AppDataDictionary');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, '30', 'HeartBtInt');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'Y', 'ResetOnLogon');
INSERT INTO METC.FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID, VALUE, NAME) VALUES (44, 'sessionCustomizationAlgoTagsOnly', 'org.marketcetera.sessioncustomization');
INSERT INTO METC.FIX_SESSIONS(ID, LAST_UPDATED, UPDATE_COUNT, AFFINITY, BROKER_ID, DESCRIPTION, HOST, ACCEPTOR, DELETED, ENABLED, NAME, PORT, SESSION_ID) VALUES (44, '2018-11-16 10:25:11.333', 1, 1, 'exsim', null, 'exchange.marketcetera.com', false, false, true, 'MATP Exchange Simulator', 7001, 'FIXT.1.1:colin-core-callisto->MRKTC-EXCH');
INSERT INTO METC.MESSAGE_STORE_MESSAGES(ID, LAST_UPDATED, UPDATE_COUNT, SESSION_ID, MESSAGE, MSG_SEQ_NUM) VALUES (119, '2018-11-16 10:25:15.466', 0, 'FIXT.1.1:colin-core-callisto->MRKTC-EXCH', '8=FIXT.1.19=9735=A34=149=colin-core-callisto52=20181116-18:25:15.38756=MRKTC-EXCH98=0108=30141=Y1137=910=025', 1);
INSERT INTO METC.MESSAGE_STORE_SESSIONS(ID, LAST_UPDATED, UPDATE_COUNT, SESSION_ID, CREATION_TIME, SENDER_SEQ_NUM, TARGET_SEQ_NUM) VALUES (118, '2018-11-16 10:25:15.513', 4, 'FIXT.1.1:colin-core-callisto->MRKTC-EXCH', '2018-11-16 10:25:15.449', 2, 2);
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (4, '2018-11-16 10:25:09.036', 0, 'Access to Add Session action', 'AddSessionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (5, '2018-11-16 10:25:09.048', 0, 'Access to Delete Session action', 'DeleteSessionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (6, '2018-11-16 10:25:09.056', 0, 'Access to disable session action', 'DisableSessionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (7, '2018-11-16 10:25:09.063', 0, 'Access to edit session action', 'EditSessionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (8, '2018-11-16 10:25:09.072', 0, 'Access to enable session action', 'EnableSessionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (9, '2018-11-16 10:25:09.079', 0, 'Access to update sequence numbers action', 'UpdateSequenceAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (10, '2018-11-16 10:25:09.087', 0, 'Access to start session action', 'StartSessionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (11, '2018-11-16 10:25:09.095', 0, 'Access to stop session action', 'StopSessionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (12, '2018-11-16 10:25:09.102', 0, 'Access to view session action', 'ViewSessionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (13, '2018-11-16 10:25:09.11', 0, 'Access to read instance data action', 'ReadInstanceDataAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (14, '2018-11-16 10:25:09.118', 0, 'Access to read FIX session attribute descriptors action', 'ReadFixSessionAttributeDescriptorsAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (15, '2018-11-16 10:25:09.125', 0, 'Access to create user action', 'CreateUserAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (16, '2018-11-16 10:25:09.134', 0, 'Access to read user action', 'ReadUserAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (17, '2018-11-16 10:25:09.142', 0, 'Access to update user action', 'UpdateUserAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (18, '2018-11-16 10:25:09.15', 0, 'Access to delete user action', 'DeleteUserAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (19, '2018-11-16 10:25:09.159', 0, 'Access to change user password action', 'ChangeUserPasswordAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (20, '2018-11-16 10:25:09.168', 0, 'Access to read user permissions action', 'ReadUserPermisionsAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (21, '2018-11-16 10:25:09.176', 0, 'Access to create permission action', 'CreatePermissionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (22, '2018-11-16 10:25:09.184', 0, 'Access to read permission action', 'ReadPermissionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (23, '2018-11-16 10:25:09.191', 0, 'Access to update permission action', 'UpdatePermissionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (24, '2018-11-16 10:25:09.198', 0, 'Access to delete permission action', 'DeletePermissionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (25, '2018-11-16 10:25:09.207', 0, 'Access to create role action', 'CreateRoleAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (26, '2018-11-16 10:25:09.214', 0, 'Access to read role action', 'ReadRoleAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (27, '2018-11-16 10:25:09.222', 0, 'Access to update role action', 'UpdateRoleAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (28, '2018-11-16 10:25:09.229', 0, 'Access to delete role action', 'DeleteRoleAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (29, '2018-11-16 10:25:09.236', 0, 'Access to view broker status action', 'ViewBrokerStatusAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (30, '2018-11-16 10:25:09.244', 0, 'Access to view open orders action', 'ViewOpenOrdersAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (31, '2018-11-16 10:25:09.251', 0, 'Access to view reports action', 'ViewReportAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (32, '2018-11-16 10:25:09.258', 0, 'Access to view positions action', 'ViewPositionAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (33, '2018-11-16 10:25:09.266', 0, 'Access to send new orders action', 'SendOrderAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (34, '2018-11-16 10:25:09.273', 0, 'Access to view user data action', 'ViewUserDataAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (35, '2018-11-16 10:25:09.28', 0, 'Access to write user data action', 'WriteUserDataAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (36, '2018-11-16 10:25:09.287', 0, 'Access to manually add new reports action', 'AddReportAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (37, '2018-11-16 10:25:09.296', 0, 'Access to manually delete reports action', 'DeleteReportAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (38, '2018-11-16 10:25:09.304', 0, 'Access to read a user attribute action', 'ReadUserAttributeAction');
INSERT INTO METC.PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (39, '2018-11-16 10:25:09.31', 0, 'Access to write a user attribute action', 'WriteUserAttributeAction');
INSERT INTO METC.ROLES(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (40, '2018-11-16 10:25:09.384', 0, 'Admin role', 'Admin');
INSERT INTO METC.ROLES(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (41, '2018-11-16 10:25:09.467', 0, 'Trader role', 'Trader');
INSERT INTO METC.ROLES(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME) VALUES (42, '2018-11-16 10:25:09.505', 0, 'Trader Admin role', 'TraderAdmin');
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 4);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 5);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 6);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 7);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 8);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 9);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 10);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 11);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 12);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 13);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 14);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 15);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 16);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 17);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 18);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 19);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 20);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 21);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 22);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 23);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 24);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 25);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 26);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 27);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 28);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 29);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 34);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 35);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 38);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (40, 39);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 29);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 30);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 31);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 32);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 33);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 34);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 35);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 36);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 38);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (41, 39);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 29);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 30);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 31);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 32);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 33);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 34);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 35);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 36);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 37);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 38);
INSERT INTO METC.ROLES_PERMISSIONS(ROLES_ID, PERMISSIONS_ID) VALUES (42, 39);
INSERT INTO METC.ROLES_USERS(ROLE_ID, SUBJECTS_ID) VALUES (40, 3);
INSERT INTO METC.ROLES_USERS(ROLE_ID, SUBJECTS_ID) VALUES (41, 1);
INSERT INTO METC.ROLES_USERS(ROLE_ID, SUBJECTS_ID) VALUES (42, 2);
INSERT INTO METC.SUPERVISOR_PERMISSIONS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME, USER_ID) VALUES (43, '2018-11-16 10:25:09.542', 0, 'Trader supervisor role', 'TraderSupervisor', 2);
INSERT INTO METC.SUPERVISOR_PERMISSIONS_PERMISSIONS(SUPERVISORPERMISSION_ID, PERMISSIONS_ID) VALUES (43, 29);
INSERT INTO METC.SUPERVISOR_PERMISSIONS_PERMISSIONS(SUPERVISORPERMISSION_ID, PERMISSIONS_ID) VALUES (43, 30);
INSERT INTO METC.SUPERVISOR_PERMISSIONS_PERMISSIONS(SUPERVISORPERMISSION_ID, PERMISSIONS_ID) VALUES (43, 31);
INSERT INTO METC.SUPERVISOR_PERMISSIONS_PERMISSIONS(SUPERVISORPERMISSION_ID, PERMISSIONS_ID) VALUES (43, 32);
INSERT INTO METC.SUPERVISOR_PERMISSIONS_PERMISSIONS(SUPERVISORPERMISSION_ID, PERMISSIONS_ID) VALUES (43, 34);
INSERT INTO METC.SUPERVISOR_PERMISSIONS_USERS(SUPERVISORPERMISSION_ID, SUBJECTS_ID) VALUES (43, 1);
INSERT INTO METC.USERS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME, IS_ACTIVE, PASSWORD, IS_SUPERUSER, USER_DATA) VALUES (1, '2018-11-16 10:25:08.946', 0, 'Trader user', 'trader', true, '2zg91043ou3eki4ysbejwwgkci37e6j', false, null);
INSERT INTO METC.USERS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME, IS_ACTIVE, PASSWORD, IS_SUPERUSER, USER_DATA) VALUES (2, '2018-11-16 10:25:09.01', 0, 'Trader Admin user', 'traderAdmin', true, '210ui1dyyf6voajrad4gmpt3vgvvm9o', false, null);
INSERT INTO METC.USERS(ID, LAST_UPDATED, UPDATE_COUNT, DESCRIPTION, NAME, IS_ACTIVE, PASSWORD, IS_SUPERUSER, USER_DATA) VALUES (3, '2018-11-16 10:25:09.019', 0, 'Admin user', 'admin', true, '6anqbgybi82pveayzrkt3egjkwfwdg5', true, null);
ALTER TABLE METC.EXEC_REPORTS
	ADD FOREIGN KEY (REPORT_ID) 
	REFERENCES METC.REPORTS (ID);

ALTER TABLE METC.EXEC_REPORTS
	ADD FOREIGN KEY (VIEWER_ID) 
	REFERENCES METC.USERS (ID);

ALTER TABLE METC.EXEC_REPORTS
	ADD FOREIGN KEY (ACTOR_ID) 
	REFERENCES METC.USERS (ID);


ALTER TABLE METC.FIX_SESSION_ATTRIBUTES
	ADD FOREIGN KEY (FIX_SESSION_ID) 
	REFERENCES METC.FIX_SESSIONS (ID);


ALTER TABLE METC.ORDER_STATUS
	ADD FOREIGN KEY (REPORT_ID) 
	REFERENCES METC.REPORTS (ID);

ALTER TABLE METC.ORDER_STATUS
	ADD FOREIGN KEY (VIEWER_ID) 
	REFERENCES METC.USERS (ID);

ALTER TABLE METC.ORDER_STATUS
	ADD FOREIGN KEY (ACTOR_ID) 
	REFERENCES METC.USERS (ID);


ALTER TABLE METC.OUTGOING_MESSAGES
	ADD FOREIGN KEY (FIX_MESSAGE_ID) 
	REFERENCES METC.FIX_MESSAGES (ID);

ALTER TABLE METC.OUTGOING_MESSAGES
	ADD FOREIGN KEY (ACTOR_ID) 
	REFERENCES METC.USERS (ID);


ALTER TABLE METC.REPORTS
	ADD FOREIGN KEY (FIX_MESSAGE_ID) 
	REFERENCES METC.FIX_MESSAGES (ID);

ALTER TABLE METC.REPORTS
	ADD FOREIGN KEY (VIEWER_ID) 
	REFERENCES METC.USERS (ID);

ALTER TABLE METC.REPORTS
	ADD FOREIGN KEY (ACTOR_ID) 
	REFERENCES METC.USERS (ID);


ALTER TABLE METC.ROLES_PERMISSIONS
	ADD FOREIGN KEY (PERMISSIONS_ID) 
	REFERENCES METC.PERMISSIONS (ID);

ALTER TABLE METC.ROLES_PERMISSIONS
	ADD FOREIGN KEY (ROLES_ID) 
	REFERENCES METC.ROLES (ID);


ALTER TABLE METC.ROLES_USERS
	ADD FOREIGN KEY (ROLE_ID) 
	REFERENCES METC.ROLES (ID);

ALTER TABLE METC.ROLES_USERS
	ADD FOREIGN KEY (SUBJECTS_ID) 
	REFERENCES METC.USERS (ID);


ALTER TABLE METC.SUPERVISOR_PERMISSIONS
	ADD FOREIGN KEY (USER_ID) 
	REFERENCES METC.USERS (ID);


ALTER TABLE METC.SUPERVISOR_PERMISSIONS_PERMISSIONS
	ADD FOREIGN KEY (PERMISSIONS_ID) 
	REFERENCES METC.PERMISSIONS (ID);

ALTER TABLE METC.SUPERVISOR_PERMISSIONS_PERMISSIONS
	ADD FOREIGN KEY (SUPERVISORPERMISSION_ID) 
	REFERENCES METC.SUPERVISOR_PERMISSIONS (ID);


ALTER TABLE METC.SUPERVISOR_PERMISSIONS_USERS
	ADD FOREIGN KEY (SUPERVISORPERMISSION_ID) 
	REFERENCES METC.SUPERVISOR_PERMISSIONS (ID);

ALTER TABLE METC.SUPERVISOR_PERMISSIONS_USERS
	ADD FOREIGN KEY (SUBJECTS_ID) 
	REFERENCES METC.USERS (ID);


ALTER TABLE METC.USER_ATTRIBUTES
	ADD FOREIGN KEY (USER_ID) 
	REFERENCES METC.USERS (ID);


