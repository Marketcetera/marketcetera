SET DATABASE UNIQUE NAME HSQLDB87B91689FB;
SET DATABASE GC 0;
SET DATABASE DEFAULT RESULT MEMORY ROWS 0;
SET DATABASE EVENT LOG LEVEL 0;
SET DATABASE TRANSACTION CONTROL LOCKS;
SET DATABASE DEFAULT ISOLATION LEVEL READ COMMITTED;
SET DATABASE TRANSACTION ROLLBACK ON CONFLICT TRUE;
SET DATABASE TEXT TABLE DEFAULTS '';
SET DATABASE SQL NAMES FALSE;
SET DATABASE SQL REFERENCES FALSE;
SET DATABASE SQL SIZE TRUE;
SET DATABASE SQL TYPES FALSE;
SET DATABASE SQL TDC DELETE TRUE;
SET DATABASE SQL TDC UPDATE TRUE;
SET DATABASE SQL CONCAT NULLS TRUE;
SET DATABASE SQL UNIQUE NULLS TRUE;
SET DATABASE SQL CONVERT TRUNCATE TRUE;
SET DATABASE SQL AVG SCALE 0;
SET DATABASE SQL DOUBLE NAN TRUE;
SET FILES WRITE DELAY 500 MILLIS;
SET FILES BACKUP INCREMENT TRUE;
SET FILES CACHE SIZE 10000;
SET FILES CACHE ROWS 50000;
SET FILES SCALE 32;
SET FILES LOB SCALE 32;
SET FILES DEFRAG 0;
SET FILES NIO TRUE;
SET FILES NIO SIZE 256;
SET FILES LOG TRUE;
SET FILES LOG SIZE 50;
SET FILES CHECK 1082;
SET DATABASE COLLATION "SQL_TEXT" PAD SPACE;
ALTER USER "metc" SET LOCAL TRUE;
CREATE SEQUENCE PUBLIC.HIBERNATE_SEQUENCE AS INTEGER START WITH 1;
CREATE MEMORY TABLE PUBLIC.METC_EXEC_REPORTS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,ACCOUNT VARCHAR(255),AVG_PRICE NUMERIC(17,7) NOT NULL,BROKER_ORDER_ID VARCHAR(255) NOT NULL,CUM_QTY NUMERIC(17,7) NOT NULL,EFF_CUM_QTY NUMERIC(17,7) NOT NULL,EXEC_TYPE VARCHAR(255) NOT NULL,EXEC_ID VARCHAR(255) NOT NULL,EXPIRY VARCHAR(255),LAST_PRICE NUMERIC(17,7),LAST_QTY NUMERIC(17,7),LEAVES_QTY NUMERIC(17,7) NOT NULL,OPTION_TYPE INTEGER,ORDER_ID VARCHAR(255) NOT NULL,ORDER_QTY NUMERIC(17,7) NOT NULL,ORD_STATUS VARCHAR(255) NOT NULL,ORDER_TYPE VARCHAR(255),ORIG_ORDER_ID VARCHAR(255),PRICE NUMERIC(17,7),ROOT_ORDER_ID VARCHAR(255) NOT NULL,SECURITY_TYPE VARCHAR(255) NOT NULL,SEND_TIME TIMESTAMP NOT NULL,SIDE VARCHAR(255) NOT NULL,STRIKE_PRICE NUMERIC(17,7),SYMBOL VARCHAR(255) NOT NULL,TIF VARCHAR(255),ACTOR_ID BIGINT,REPORT_ID BIGINT NOT NULL,VIEWER_ID BIGINT,CONSTRAINT UK_2CJC85K3O398O2MRVMHCVO7AU UNIQUE(REPORT_ID));
CREATE MEMORY TABLE PUBLIC.METC_FIX_MESSAGES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,MESSAGE VARCHAR(8192) NOT NULL);
CREATE MEMORY TABLE PUBLIC.METC_FIX_SESSION_ATTR_DSCRPTRS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,ADVICE VARCHAR(255),DEFAULT_VALUE VARCHAR(255),DESCRIPTION VARCHAR(1024),NAME VARCHAR(255) NOT NULL,PATTERN VARCHAR(255),REQUIRED BOOLEAN NOT NULL,CONSTRAINT UK_FQYA0HOUXOKT0MPTMT1B345NI UNIQUE(NAME));
CREATE MEMORY TABLE PUBLIC.METC_FIX_SESSION_ATTRIBUTES(FIX_SESSION_ID BIGINT NOT NULL,VALUE VARCHAR(255),NAME VARCHAR(255) NOT NULL,PRIMARY KEY(FIX_SESSION_ID,NAME));
CREATE MEMORY TABLE PUBLIC.METC_FIX_SESSIONS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,DESCRIPTION VARCHAR(255),NAME VARCHAR(255) NOT NULL,AFFINITY INTEGER NOT NULL,BROKER_ID VARCHAR(255) NOT NULL,HOST VARCHAR(255) NOT NULL,ACCEPTOR BOOLEAN NOT NULL,DELETED BOOLEAN NOT NULL,ENABLED BOOLEAN NOT NULL,MAPPED_BROKER_ID VARCHAR(255),PORT INTEGER NOT NULL,SESSION_ID VARCHAR(255) NOT NULL,CONSTRAINT UK_DLYM3TUB9TRLEPS3F1308P76Y UNIQUE(NAME));
CREATE MEMORY TABLE PUBLIC.METC_ID_REPOSITORY(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,NEXT_ID BIGINT);
CREATE MEMORY TABLE PUBLIC.METC_INCOMING_FIX_MESSAGES(ID BIGINT NOT NULL PRIMARY KEY,CLORDID VARCHAR(255),EXECID VARCHAR(255),MESSAGE VARCHAR(4000) NOT NULL,MSG_SEQ_NUM INTEGER NOT NULL,MSG_TYPE VARCHAR(255) NOT NULL,SENDING_TIME TIMESTAMP NOT NULL,FIX_SESSION VARCHAR(255) NOT NULL);
CREATE MEMORY TABLE PUBLIC.METC_MESSAGE_STORE_MESSAGES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,SESSION_ID VARCHAR(255) NOT NULL,MESSAGE VARCHAR(8192) NOT NULL,MSG_SEQ_NUM INTEGER NOT NULL);
CREATE MEMORY TABLE PUBLIC.METC_MESSAGE_STORE_SESSIONS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,SESSION_ID VARCHAR(255) NOT NULL,CREATION_TIME TIMESTAMP NOT NULL,SENDER_SEQ_NUM INTEGER NOT NULL,TARGET_SEQ_NUM INTEGER NOT NULL);
CREATE MEMORY TABLE PUBLIC.METC_METRICS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,COUNT BIGINT,DURATION_UNIT VARCHAR(255),HOUR INTEGER NOT NULL,M1 NUMERIC(19,2),M15 NUMERIC(19,2),M5 NUMERIC(19,2),MAX NUMERIC(19,2),MEAN NUMERIC(19,2),MEAN_RATE NUMERIC(19,2),MEDIAN NUMERIC(19,2),MILLIS INTEGER NOT NULL,MIN NUMERIC(19,2),MINUTE INTEGER NOT NULL,NAME VARCHAR(255) NOT NULL,P75 NUMERIC(19,2),P95 NUMERIC(19,2),P98 NUMERIC(19,2),P99 NUMERIC(19,2),P999 NUMERIC(19,2),RATE_UNIT VARCHAR(255),SECOND INTEGER NOT NULL,STD_DEV NUMERIC(19,2),METRIC_TIMESTAMP TIMESTAMP NOT NULL,TYPE VARCHAR(255) NOT NULL,VALUE VARCHAR(255));
CREATE MEMORY TABLE PUBLIC.METC_ORDER_SUMMARIES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,ACCOUNT VARCHAR(255),AVG_PX NUMERIC(17,7) NOT NULL,BROKER_ID VARCHAR(255),CUM_QTY NUMERIC(17,7) NOT NULL,EXPIRY VARCHAR(255),LAST_PX NUMERIC(17,7) NOT NULL,LAST_QTY NUMERIC(17,7) NOT NULL,LEAVES_QTY NUMERIC(17,7) NOT NULL,OPTION_TYPE INTEGER,ORDER_ID VARCHAR(255) NOT NULL,ORDER_PX NUMERIC(17,7),ORDER_QTY NUMERIC(17,7) NOT NULL,ORD_STATUS VARCHAR(255) NOT NULL,ROOT_ORDER_ID VARCHAR(255) NOT NULL,SECURITY_TYPE INTEGER NOT NULL,SENDING_TIME TIMESTAMP NOT NULL,SIDE INTEGER NOT NULL,STRIKE_PRICE NUMERIC(17,7),SYMBOL VARCHAR(255) NOT NULL,EXECUTION_TIME TIMESTAMP,ACTOR_ID BIGINT,REPORT_ID BIGINT NOT NULL,VIEWER_ID BIGINT,CONSTRAINT UK_4CRHMNVE1OPMO542EFGPMXF55 UNIQUE(REPORT_ID));
CREATE MEMORY TABLE PUBLIC.METC_OUTGOING_MESSAGES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,BROKER_ID VARCHAR(255) NOT NULL,MESSAGE_TYPE VARCHAR(255) NOT NULL,MSG_SEQ_NUM INTEGER,ORDER_ID VARCHAR(255),SENDER_COMP_ID VARCHAR(255) NOT NULL,SESSION_ID VARCHAR(255) NOT NULL,TARGET_COMP_ID VARCHAR(255) NOT NULL,ACTOR_ID BIGINT NOT NULL,FIX_MESSAGE_ID BIGINT NOT NULL,CONSTRAINT UK_PUYVYEPSSYEOY7NLTWQVOQV1R UNIQUE(FIX_MESSAGE_ID),CONSTRAINT FKN0LW9NX1ECH40TXK87BJMKBTN FOREIGN KEY(FIX_MESSAGE_ID) REFERENCES PUBLIC.METC_FIX_MESSAGES(ID));
CREATE MEMORY TABLE PUBLIC.METC_PERMISSIONS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,DESCRIPTION VARCHAR(255),NAME VARCHAR(255) NOT NULL,CONSTRAINT UK_7PLEC27873XQQ4LNJJWSYE6O9 UNIQUE(NAME));
CREATE MEMORY TABLE PUBLIC.METC_PNL_CURRENT_POSITIONS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,EXPIRY VARCHAR(255),OPTION_TYPE INTEGER,POSITION NUMERIC(17,7) NOT NULL,REALIZED_GAIN NUMERIC(17,7) NOT NULL,SECURITY_TYPE INTEGER NOT NULL,STRIKE_PRICE NUMERIC(17,7) NOT NULL,SYMBOL VARCHAR(255) NOT NULL,UNREALIZED_GAIN NUMERIC(17,7) NOT NULL,WEIGHTED_AVERAGE_COST NUMERIC(17,7) NOT NULL,USER_ID BIGINT NOT NULL);
CREATE MEMORY TABLE PUBLIC.METC_PNL_LOTS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,ALLOCATED_QUANTITY NUMERIC(17,7) NOT NULL,BASIS_PRICE NUMERIC(17,7) NOT NULL,EFFECTIVE_DATE TIMESTAMP NOT NULL,GAIN NUMERIC(17,7) NOT NULL,QUANTITY NUMERIC(17,7) NOT NULL,TRADE_PRICE NUMERIC(17,7) NOT NULL,POSITION_ID BIGINT NOT NULL,TRADE_ID BIGINT NOT NULL,USER_ID BIGINT NOT NULL);
CREATE MEMORY TABLE PUBLIC.METC_PNL_POSITIONS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,EFFECTIVE_DATE TIMESTAMP NOT NULL,EXPIRY VARCHAR(255),OPTION_TYPE INTEGER,POSITION NUMERIC(17,7) NOT NULL,REALIZED_GAIN NUMERIC(17,7) NOT NULL,SECURITY_TYPE INTEGER NOT NULL,STRIKE_PRICE NUMERIC(17,7) NOT NULL,SYMBOL VARCHAR(255) NOT NULL,UNREALIZED_GAIN NUMERIC(17,7) NOT NULL,WEIGHTED_AVERAGE_COST NUMERIC(17,7) NOT NULL,USER_ID BIGINT NOT NULL);
CREATE MEMORY TABLE PUBLIC.METC_PROFIT_AND_LOSS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,BASIS_PRICE NUMERIC(17,7),EXPIRY VARCHAR(255),OPTION_TYPE INTEGER,POSITION NUMERIC(17,7),REALIZED_GAIN NUMERIC(17,7),SECURITY_TYPE INTEGER NOT NULL,STRIKE_PRICE NUMERIC(17,7) NOT NULL,SYMBOL VARCHAR(255) NOT NULL,UNREALIZED_GAIN NUMERIC(17,7),USER_ID BIGINT NOT NULL);
CREATE MEMORY TABLE PUBLIC.METC_REPORTS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,BROKER_ID VARCHAR(255),HIERARCHY INTEGER,ORIGINATOR INTEGER,REPORT_TYPE INTEGER NOT NULL,MSG_SEQ_NUM INTEGER NOT NULL,ORDER_ID VARCHAR(255) NOT NULL,REPORT_ID BIGINT NOT NULL,SEND_TIME TIMESTAMP NOT NULL,SESSION_ID VARCHAR(255) NOT NULL,TEXT VARCHAR(255),TRANSACT_TIME TIMESTAMP NOT NULL,ACTOR_ID BIGINT,FIX_MESSAGE_ID BIGINT NOT NULL,VIEWER_ID BIGINT,CONSTRAINT UK_SCP9VE9SP44F3V4XL4DCP1C3M UNIQUE(FIX_MESSAGE_ID),CONSTRAINT UK_MX14Y70TJX9RU0KWPWBIO0C6L UNIQUE(REPORT_ID),CONSTRAINT FKG1KMFNQKP0HHQD0JX6GB5U8NC FOREIGN KEY(FIX_MESSAGE_ID) REFERENCES PUBLIC.METC_FIX_MESSAGES(ID));
CREATE MEMORY TABLE PUBLIC.METC_ROLES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,DESCRIPTION VARCHAR(255),NAME VARCHAR(255) NOT NULL,CONSTRAINT UK_LSAHVCRXGK76J87MEBJ0C1Q6L UNIQUE(NAME));
CREATE MEMORY TABLE PUBLIC.METC_ROLES_PERMISSIONS(ROLES_ID BIGINT NOT NULL,PERMISSIONS_ID BIGINT NOT NULL,PRIMARY KEY(ROLES_ID,PERMISSIONS_ID),CONSTRAINT FK6X8U6TOKVKU4JBHFEUA24HIHB FOREIGN KEY(PERMISSIONS_ID) REFERENCES PUBLIC.METC_PERMISSIONS(ID),CONSTRAINT FKB13BFPV1LGYAITG3FQRPY4ABC FOREIGN KEY(ROLES_ID) REFERENCES PUBLIC.METC_ROLES(ID));
CREATE MEMORY TABLE PUBLIC.METC_ROLES_USERS(ROLE_ID BIGINT NOT NULL,SUBJECTS_ID BIGINT NOT NULL,PRIMARY KEY(ROLE_ID,SUBJECTS_ID),CONSTRAINT FKODB87NL6E2I81GEMXBR0E0BHH FOREIGN KEY(ROLE_ID) REFERENCES PUBLIC.METC_ROLES(ID));
CREATE MEMORY TABLE PUBLIC.METC_STRATEGY_INSTANCES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,FILENAME VARCHAR(255),HASH VARCHAR(255),NAME VARCHAR(255),NONCE VARCHAR(255),STARTED TIMESTAMP,STATUS VARCHAR(255),USER_ID BIGINT);
CREATE MEMORY TABLE PUBLIC.METC_STRATEGY_MESSAGES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,MESSAGE VARCHAR(255),MESSAGE_TIMESTAMP TIMESTAMP,SEVERITY VARCHAR(255),STRATEGY_MESSAGE_ID BIGINT NOT NULL,STRATEGY_INSTANCE_ID BIGINT,CONSTRAINT UK_EHWSHS455MW0DCUI1X6RA8A3Y UNIQUE(STRATEGY_MESSAGE_ID),CONSTRAINT FK66JHYNR1A7R5X55L5C0X0HASX FOREIGN KEY(STRATEGY_INSTANCE_ID) REFERENCES PUBLIC.METC_STRATEGY_INSTANCES(ID));
CREATE MEMORY TABLE PUBLIC.METC_SUPERVISOR_PERMISSIONS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,DESCRIPTION VARCHAR(255),NAME VARCHAR(255) NOT NULL,USER_ID BIGINT NOT NULL,CONSTRAINT UK_LH4CJ38Q3D4M3DUSTM5RUVLEM UNIQUE(NAME));
CREATE MEMORY TABLE PUBLIC.METC_SUPERVISOR_PERMISSIONS_PERMISSIONS(SUPERVISOR_PERMISSION_ID BIGINT NOT NULL,PERMISSIONS_ID BIGINT NOT NULL,PRIMARY KEY(SUPERVISOR_PERMISSION_ID,PERMISSIONS_ID),CONSTRAINT FKEO14J1FFTT55UXJEENX72SW6M FOREIGN KEY(PERMISSIONS_ID) REFERENCES PUBLIC.METC_PERMISSIONS(ID),CONSTRAINT FKECV6YDV8VNBFJ53QNDBKKSWRP FOREIGN KEY(SUPERVISOR_PERMISSION_ID) REFERENCES PUBLIC.METC_SUPERVISOR_PERMISSIONS(ID));
CREATE MEMORY TABLE PUBLIC.METC_SUPERVISOR_PERMISSIONS_USERS(SUPERVISOR_PERMISSION_ID BIGINT NOT NULL,SUBJECTS_ID BIGINT NOT NULL,PRIMARY KEY(SUPERVISOR_PERMISSION_ID,SUBJECTS_ID),CONSTRAINT FK471BLXWYI5SWRDSJNGVP3TLLA FOREIGN KEY(SUPERVISOR_PERMISSION_ID) REFERENCES PUBLIC.METC_SUPERVISOR_PERMISSIONS(ID));
CREATE MEMORY TABLE PUBLIC.METC_TRADES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,ORDER_ID VARCHAR(255),EXPIRY VARCHAR(255),OPTION_TYPE INTEGER,PRICE NUMERIC(17,7),QUANTITY NUMERIC(17,7),SECURITY_TYPE INTEGER NOT NULL,STRIKE_PRICE NUMERIC(17,7) NOT NULL,SYMBOL VARCHAR(255) NOT NULL,TRANSACTION_TIME TIMESTAMP);
CREATE MEMORY TABLE PUBLIC.METC_USER_ATTRIBUTES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,ATTRIBUTE VARCHAR(262144) NOT NULL,USER_ATTRIBUTE_TYPE INTEGER NOT NULL,USER_ID BIGINT);
CREATE MEMORY TABLE PUBLIC.METC_USER_TRADES(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,ORDER_ID VARCHAR(255),SIDE INTEGER NOT NULL,PNL_ID BIGINT NOT NULL,TRADE_ID BIGINT NOT NULL,USER_ID BIGINT NOT NULL,CONSTRAINT UK_K4PICQRERN0HLW962XRWIDEYE UNIQUE(PNL_ID),CONSTRAINT FKLBMHUH87M7KYKV9I6G2K64S2Y FOREIGN KEY(PNL_ID) REFERENCES PUBLIC.METC_PROFIT_AND_LOSS(ID),CONSTRAINT FKD9M2R0IR6XAEX0DCS1A4HL3WY FOREIGN KEY(TRADE_ID) REFERENCES PUBLIC.METC_TRADES(ID));
CREATE MEMORY TABLE PUBLIC.METC_USERS(ID BIGINT NOT NULL PRIMARY KEY,LAST_UPDATED TIMESTAMP NOT NULL,UPDATE_COUNT INTEGER NOT NULL,DESCRIPTION VARCHAR(255),NAME VARCHAR(255) NOT NULL,IS_ACTIVE BOOLEAN NOT NULL,PASSWORD VARCHAR(255) NOT NULL,IS_SUPERUSER BOOLEAN NOT NULL,USER_DATA VARCHAR(8096),CONSTRAINT UK2P03GLY8XI76GOIKMJCB8WED1 UNIQUE(NAME));

--
-- Data for Name: exec_reports; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: fix_messages; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: fix_session_attr_dscrptrs; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (46, '2018-11-13 08:00:22.901', 0, NULL, '', '(Optional) Your subID as associated with this FIX session', 'SenderSubID', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (47, '2018-11-13 08:00:22.906', 0, NULL, '', '(Optional) Your locationID as associated with this FIX session', 'SenderLocationID', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (48, '2018-11-13 08:00:22.909', 0, NULL, '', '(Optional) counterparty''s subID as associated with this FIX session', 'TargetSubID', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (49, '2018-11-13 08:00:22.914', 0, NULL, '', '(Optional) counterparty''s locationID as associated with this FIX session', 'TargetLocationID', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (50, '2018-11-13 08:00:22.919', 0, NULL, '', 'Additional qualifier to disambiguate otherwise identical sessions. This can only be used with initiator sessions.Note: See Special notes for Oracle.', 'SessionQualifier', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (51, '2018-11-13 08:00:22.922', 0, NULL, '', 'Required only for FIXT 1.1 (and newer). Ignored for earlier transport versions. Specifies the default application version ID for the session. This can either be the ApplVerID enum (see the ApplVerID field) the beginString for the default version.', 'DefaultApplVerID', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (52, '2018-11-13 08:00:22.924', 0, NULL, 'Y', 'Determines if milliseconds should be added to timestamps. Only available for FIX.4.2 and greater.', 'MillisecondsInTimeStamp', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (53, '2018-11-13 08:00:22.927', 0, NULL, 'N', 'Use actual end of sequence gap for resend requests rather than using ''''infinity'''' as the end sequence of the gap. Not recommended by the FIX specification, but needed for some counterparties.', 'ClosedResendInterval', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (54, '2018-11-13 08:00:22.931', 0, NULL, 'Y', 'Tell session whether or not to expect a data dictionary. You should always use a DataDictionary if you are using repeating groups.', 'UseDataDictionary', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (55, '2018-11-13 08:00:22.933', 0, NULL, 'FIX42.xml', 'XML definition file for validating incoming FIX messages. If no DataDictionary is supplied, only basic message validation will be done. This setting should only be used with FIX transport versions old than FIXT 1.1. See TransportDataDictionary and ApplicationDataDictionary for FIXT 1.1 settings.', 'DataDictionary', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (56, '2018-11-13 08:00:22.936', 0, NULL, '', 'XML definition file for validating admin (transport) messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information.', 'TransportDataDictionary', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (57, '2018-11-13 08:00:22.938', 0, NULL, '', 'XML definition file for validating application messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information. This setting supports the possibility of a custom application data dictionary for each session. This setting would only be used with FIXT 1.1 and new transport protocols. This setting can be used as a prefix to specify multiple application dictionaries for the FIXT transport. For example: DefaultApplVerID=FIX.4.2 # For default application version ID AppDataDictionary=FIX42.xml # For nondefault application version ID # Use beginString suffix for app version AppDataDictionary.FIX.4.4=FIX44.xml This would use FIX42.xml for the default application version ID and FIX44.xml for any FIX 4.4 messages.', 'AppDataDictionary', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (58, '2018-11-13 08:00:22.941', 0, NULL, 'Y', 'If set to N, fields that are out of order (i.e. body fields in the header, or header fields in the body) will not be rejected. Useful for connecting to systems which do not properly order fields.', 'ValidateFieldsOutOfOrder', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (59, '2018-11-13 08:00:22.943', 0, NULL, 'Y', 'If set to N, fields without values (empty) will not be rejected. Useful for connecting to systems which improperly send empty tags.', 'ValidateFieldsHaveValues', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (60, '2018-11-13 08:00:22.946', 0, NULL, 'Y', 'If set to N, user defined fields will not be rejected if they are not defined in the data dictionary, or are present in messages they do not belong to.', 'ValidateUserDefinedFields', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (61, '2018-11-13 08:00:22.949', 0, NULL, 'Y', 'Session validation setting for enabling whether field ordering is * validated. Values are ''''Y'''' or ''''N''''. Default is ''''Y''''.', 'ValidateUnorderedGroupFields', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (62, '2018-11-13 08:00:22.951', 0, NULL, 'Y', 'Allow to bypass the message validation (against the dictionary). Default is ''''Y''''.', 'ValidateIncomingMessage', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (63, '2018-11-13 08:00:22.953', 0, NULL, 'Y', 'Check the next expected target SeqNum against the received SeqNum. Default is ''''Y''''. If enabled and a mismatch is detected, apply the following logic:if lower than expected SeqNum , logout if higher, send a resend request If not enabled and a mismatch is detected, nothing is done. Must be enabled for EnableNextExpectedMsgSeqNum to work.', 'ValidateSequenceNumbers', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (64, '2018-11-13 08:00:22.955', 0, NULL, 'N', 'Allow unknown fields in messages. This is intended for unknown fields with tags lt 5000 (not user defined fields). Use ValidateUserDefinedFields for controlling validation of tags ge 5000.', 'AllowUnknownMsgFields', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (65, '2018-11-13 08:00:22.958', 0, NULL, 'Y', 'If set to Y, messages must be received from the counterparty with the correct SenderCompID and TargetCompID. Some systems will send you different CompIDs by design, so you must set this to N.', 'CheckCompID', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (66, '2018-11-13 08:00:22.96', 0, NULL, 'Y', 'If set to Y, messages must be received from the counterparty within a defined number of seconds (see MaxLatency). It is useful to turn this off if a system uses localtime for its timestamps instead of GMT.', 'CheckLatency', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (67, '2018-11-13 08:00:22.962', 0, NULL, '120', 'If CheckLatency is set to Y, this defines the number of seconds latency allowed for a message to be processed.', 'MaxLatency', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (68, '2018-11-13 08:00:22.964', 0, NULL, 'Y', 'If RejectInvalidMessage is set to N, only a warning will be logged on reception of message that fails data dictionary validation.', 'RejectInvalidMessage', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (69, '2018-11-13 08:00:22.967', 0, NULL, 'N', 'If this configuration is enabled, an uncaught Exception or Error in the application''s message processing will lead to a (BusinessMessage)Reject being sent to the counterparty and the incoming message sequence number will be incremented. If disabled (default), the problematic incoming message is discarded and the message sequence number is not incremented. Processing of the next valid message will cause detection of a sequence gap and a ResendRequest will be generated.', 'RejectMessageOnUnhandledException', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (70, '2018-11-13 08:00:22.969', 0, NULL, 'Y', 'If RequiresOrigSendingTime is set to N, PossDup messages lacking that field will not be rejected.', 'RequiresOrigSendingTime', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (71, '2018-11-13 08:00:22.971', 0, NULL, '30', 'Time between reconnection attempts in seconds. Only used for initiators', 'ReconnectInterval', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (72, '2018-11-13 08:00:22.974', 0, NULL, '30', 'Heartbeat interval in seconds. Only used for initiators.', 'HeartBtInt', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (73, '2018-11-13 08:00:22.976', 0, NULL, '10', 'Number of seconds to wait for a logon response before disconnecting.', 'LogonTimeout', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (74, '2018-11-13 08:00:22.978', 0, NULL, '2', 'Number of seconds to wait for a logout response before disconnecting.', 'LogoutTimeout', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (75, '2018-11-13 08:00:22.98', 0, NULL, 'TCP', 'Specifies the initiator communication protocol. The SocketConnectHost is not used with the VM_PIPE protocol, but the SocketConnectPort is significant and must match the acceptor configuration.', 'SocketConnectProtocol', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (76, '2018-11-13 08:00:22.983', 0, NULL, '', 'Bind the local socket to this port. Only used with a SocketInitiator. If unset the socket will be bound to a free port from the ephemeral port range.', 'SocketLocalPort', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (77, '2018-11-13 08:00:22.986', 0, NULL, '', 'Bind the local socket to this host. Only used with a SocketAcceptor. If unset the socket will be bound to all local interfaces.', 'SocketLocalHost', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (78, '2018-11-13 08:00:22.988', 0, NULL, 'TCP', 'Specifies the acceptor communication protocol. The SocketAcceptAddress is not used with the VM_PIPE protocol, but the SocketAcceptPort is significant and must match the initiator configuration.', 'SocketAcceptProtocol', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (79, '2018-11-13 08:00:22.991', 0, 'Enter ''Y'' or ''N''', 'Y', 'Refresh the session state when a logon is received. This allows a simple form of failover when the message store data is persistent. The option will be ignored for message stores that are not persistent (e.g., MemoryStore).', 'RefreshOnLogon', '^(Y|N){1}$', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (80, '2018-11-13 08:00:22.993', 0, NULL, 'N', 'Enables SSL usage for QFJ acceptor or initiator.', 'SocketUseSSL', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (81, '2018-11-13 08:00:22.996', 0, NULL, '', 'KeyStore to use with SSL', 'SocketKeyStore', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (82, '2018-11-13 08:00:22.999', 0, NULL, '', 'KeyStore password', 'SocketKeyStorePassword', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (83, '2018-11-13 08:00:23.002', 0, NULL, '', 'When the keepalive option is set for a TCP socket and no data has been exchanged across the socket in either direction for 2 hours (NOTE: the actual value is implementation dependent), TCP automatically sends a keepalive probe to the peer. This probe is a TCP segment to which the peer must respond. One of three responses is expected: The peer responds with the expected ACK. The application is not notified (since everything is OK). TCP will send another probe following another 2 hours of inactivity. The peer responds with an RST, which tells the local TCP that the peer host has crashed and rebooted. The socket is closed. There is no response from the peer. The socket is closed. The purpose of this option is to detect if the peer host crashes.', 'SocketKeepAlive', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (84, '2018-11-13 08:00:23.004', 0, NULL, '', 'When the OOBINLINE option is set, any TCP urgent data received on the socket will be received through the socket input stream. When the option is disabled (which is the default) urgent data is silently discarded.', 'SocketOobInline', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (85, '2018-11-13 08:00:23.007', 0, NULL, '', 'Set a hint the size of the underlying buffers used by the platform for incoming network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be received over the socket.', 'SocketReceiveBufferSize', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (86, '2018-11-13 08:00:23.009', 0, NULL, '', 'Sets SO_REUSEADDR for a socket. This is used only for MulticastSockets in java, and it is set by default for MulticastSockets.', 'SocketReuseAddress', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (87, '2018-11-13 08:00:23.011', 0, NULL, '', 'Set a hint the size of the underlying buffers used by the platform for outgoing network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be sent over the socket.', 'SocketSendBufferSize', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (88, '2018-11-13 08:00:23.014', 0, NULL, '', 'Specify a linger-on-close timeout. This option disables/enables immediate return from a close() of a TCP Socket. Enabling this option with a non-zero Integer timeout means that a close() will block pending the transmission and acknowledgement of all data written to the peer, at which point the socket is closed gracefully. Upon reaching the linger timeout, the socket is closed forcefully, with a TCP RST. Enabling the option with a timeout of zero does a forceful close immediately. If the specified timeout value exceeds 65,535 it will be reduced to 65,535.', 'SocketLinger', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (89, '2018-11-13 08:00:23.017', 0, NULL, 'Y', 'Disable Nagle''s algorithm for this connection. Written data to the network is not buffered pending acknowledgement of previously written data.', 'SocketTcpNoDelay', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (90, '2018-11-13 08:00:23.02', 0, NULL, '', 'Sets traffic class or type-of-service octet in the IP header for packets sent from this Socket. As the underlying network implementation may ignore this value applications should consider it a hint. The tc must be in the range 0 = tc = 255 or an IllegalArgumentException will be thrown. Notes: for Internet Protocol v4 the value consists of an octet with precedence and TOS fields as detailed in RFC 1349. The TOS field is bitset created by bitwise-or''ing values such the following :- IPTOS_LOWCOST (0x02) IPTOS_RELIABILITY (0x04) IPTOS_THROUGHPUT (0x08) IPTOS_LOWDELAY (0x10) The last low order bit is always ignored as this corresponds to the MBZ (must be zero) bit. Setting bits in the precedence field may result in a SocketException indicating that the operation is not permitted.', 'SocketTrafficClass', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (91, '2018-11-13 08:00:23.022', 0, NULL, 'N', 'Write messages synchronously. This is not generally recommended as it may result in performance degradation. The MINA communication layer is asynchronous by design, but this option will override that behavior if needed.', 'SocketSynchronousWrites', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (92, '2018-11-13 08:00:23.026', 0, NULL, '30000', 'The time in milliseconds to wait for a write to complete.', 'SocketSynchronousWriteTimeout', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (93, '2018-11-13 08:00:23.031', 0, NULL, 'Y', 'If set to N, no messages will be persisted. This will force QFJ to always send GapFills instead of resending messages. Use this if you know you never want to resend a message. Useful for market data streams.', 'PersistMessages', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (94, '2018-11-13 08:00:23.035', 0, NULL, 'N', 'Controls whether milliseconds are included in log time stamps.', 'FileIncludeMilliseconds', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (95, '2018-11-13 08:00:23.038', 0, NULL, 'N', 'Controls whether time stamps are included on message log entries.', 'FileIncludeTimestampForMessages', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (96, '2018-11-13 08:00:23.04', 0, NULL, 'quickfixj.event', 'Log category for logged events.', 'SLF4JLogEventCategory', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (97, '2018-11-13 08:00:23.042', 0, NULL, 'quickfixj.msg.incoming', 'Log category for incoming messages.', 'SLF4JLogIncomingMessageCategory', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (98, '2018-11-13 08:00:23.044', 0, NULL, 'quickfixj.msg.outgoing', 'Log category for outgoing messages.', 'SLF4JLogOutgoingMessageCategory', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (99, '2018-11-13 08:00:23.046', 0, NULL, 'Y', 'Controls whether session ID is prepended to log message.', 'SLF4JLogPrependSessionID', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (100, '2018-11-13 08:00:23.049', 0, NULL, 'N', 'Controls whether heartbeats are logged.', 'SLF4JLogHeartbeats', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (101, '2018-11-13 08:00:23.052', 0, NULL, 'Y', 'Log events to screen.', 'ScreenLogEvents', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (102, '2018-11-13 08:00:23.057', 0, NULL, 'Y', 'Log incoming messages to screen.', 'ScreenLogShowIncoming', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (103, '2018-11-13 08:00:23.06', 0, NULL, 'Y', 'Log outgoing messages to screen.', 'ScreenLogShowOutgoing', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (104, '2018-11-13 08:00:23.063', 0, NULL, 'N', 'Filter heartbeats from output (both incoming and outgoing)', 'ScreenLogShowHeartbeats', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (105, '2018-11-13 08:00:23.066', 0, NULL, 'N', 'Determines if sequence numbers should be reset before sending/receiving a logon request.', 'ResetOnLogon', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (106, '2018-11-13 08:00:23.07', 0, NULL, 'N', 'Determines if sequence numbers should be reset to 1 after a normal logout termination.', 'ResetOnLogout', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (107, '2018-11-13 08:00:23.072', 0, NULL, 'N', 'Determines if sequence numbers should be reset to 1 after an abnormal termination.', 'ResetOnDisconnect', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (108, '2018-11-13 08:00:23.076', 0, NULL, 'N', 'Session setting for doing an automatic reset when an error occurs. A reset means disconnect, sequence numbers reset, store cleaned and reconnect, as for a daily reset.', 'ResetOnError', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (109, '2018-11-13 08:00:23.078', 0, NULL, 'N', 'Session setting for doing an automatic disconnect when an error occurs.', 'DisconnectOnError', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (110, '2018-11-13 08:00:23.081', 0, NULL, 'N', 'Add tag LastMsgSeqNumProcessed in the header (optional tag 369).', 'EnableLastMsgSeqNumProcessed', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (111, '2018-11-13 08:00:23.085', 0, NULL, 'N', 'Add tag NextExpectedMsgSeqNum (optional tag 789) on the sent Logon message and use value of tag 789 on received Logon message to synchronize session. This should not be enabled for FIX versions lt 4.4. Only works when ValidateSequenceNumbers is enabled.', 'EnableNextExpectedMsgSeqNum', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (112, '2018-11-13 08:00:23.089', 0, NULL, '0', 'Setting to limit the size of a resend request in case of missing messages. This is useful when the remote FIX engine does not allow to ask for more than n message for a ResendRequest. E.g. if the ResendRequestChunkSize is set to 5 and a gap of 7 messages is detected, a first resend request will be sent for 5 messages. When this gap has been filled, another resend request for 2 messages will be sent. If the ResendRequestChunkSize is set to 0, only one ResendRequest for all the missing messages will be sent.', 'ResendRequestChunkSize', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (113, '2018-11-13 08:00:23.092', 0, NULL, 'N', 'Continue initializing sessions if an error occurs.', 'ContinueInitializationOnError', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (114, '2018-11-13 08:00:23.094', 0, NULL, 'N', 'Allows sending of redundant resend requests.', 'SendRedundantResendRequests', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (115, '2018-11-13 08:00:23.096', 0, NULL, '0.5', 'Fraction of the heartbeat interval which defines the additional time to wait if a TestRequest sent after a missing heartbeat times out.', 'TestRequestDelayMultiplier', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (116, '2018-11-13 08:00:23.099', 0, NULL, 'N', 'Heartbeat detection is disabled. A disconnect due to a missing heartbeat will never occur.', 'DisableHeartBeatCheck', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (117, '2018-11-13 08:00:23.101', 0, NULL, 'N', 'Fill in heartbeats on resend when reading from message store fails.', 'ForceResendWhenCorruptedStore', '', false);
INSERT INTO PUBLIC.METC_fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (118, '2018-11-13 08:00:23.103', 0, NULL, '', 'Name of the session modifiers to apply to this session', 'org.marketcetera.sessioncustomization', '', false);

--
-- Data for Name: id_repository; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: incoming_fix_messages; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: message_store_messages; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: message_store_sessions; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: order_status; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: outgoing_messages; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (5, '2018-11-13 08:00:20.531', 0, 'Access to Add Session action', 'AddSessionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (6, '2018-11-13 08:00:20.542', 0, 'Access to Delete Session action', 'DeleteSessionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (7, '2018-11-13 08:00:20.552', 0, 'Access to disable session action', 'DisableSessionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (8, '2018-11-13 08:00:20.564', 0, 'Access to edit session action', 'EditSessionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (9, '2018-11-13 08:00:20.57', 0, 'Access to enable session action', 'EnableSessionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (10, '2018-11-13 08:00:20.577', 0, 'Access to update sequence numbers action', 'UpdateSequenceAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (11, '2018-11-13 08:00:20.59', 0, 'Access to start session action', 'StartSessionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (12, '2018-11-13 08:00:20.599', 0, 'Access to stop session action', 'StopSessionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (13, '2018-11-13 08:00:20.605', 0, 'Access to view session action', 'ViewSessionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (14, '2018-11-13 08:00:20.611', 0, 'Access to read instance data action', 'ReadInstanceDataAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (15, '2018-11-13 08:00:20.617', 0, 'Access to read FIX session attribute descriptors action', 'ReadFixSessionAttributeDescriptorsAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (16, '2018-11-13 08:00:20.624', 0, 'Access to create user action', 'CreateUserAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (17, '2018-11-13 08:00:20.63', 0, 'Access to read user action', 'ReadUserAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (18, '2018-11-13 08:00:20.636', 0, 'Access to update user action', 'UpdateUserAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (19, '2018-11-13 08:00:20.642', 0, 'Access to delete user action', 'DeleteUserAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (20, '2018-11-13 08:00:20.652', 0, 'Access to change user password action', 'ChangeUserPasswordAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (21, '2018-11-13 08:00:20.658', 0, 'Access to read user permissions action', 'ReadUserPermisionsAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (22, '2018-11-13 08:00:20.663', 0, 'Access to create permission action', 'CreatePermissionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (23, '2018-11-13 08:00:20.668', 0, 'Access to read permission action', 'ReadPermissionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (24, '2018-11-13 08:00:20.673', 0, 'Access to update permission action', 'UpdatePermissionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (25, '2018-11-13 08:00:20.678', 0, 'Access to delete permission action', 'DeletePermissionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (26, '2018-11-13 08:00:20.685', 0, 'Access to create role action', 'CreateRoleAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (27, '2018-11-13 08:00:20.691', 0, 'Access to read role action', 'ReadRoleAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (28, '2018-11-13 08:00:20.695', 0, 'Access to update role action', 'UpdateRoleAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (29, '2018-11-13 08:00:20.7', 0, 'Access to delete role action', 'DeleteRoleAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (30, '2018-11-13 08:00:20.704', 0, 'Access to view broker status action', 'ViewBrokerStatusAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (31, '2018-11-13 08:00:20.709', 0, 'Access to view open orders action', 'ViewOpenOrdersAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (32, '2018-11-13 08:00:20.713', 0, 'Access to view reports action', 'ViewReportAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (33, '2018-11-13 08:00:20.718', 0, 'Access to view positions action', 'ViewPositionAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (34, '2018-11-13 08:00:20.722', 0, 'Access to send new orders action', 'SendOrderAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (35, '2018-11-13 08:00:20.726', 0, 'Access to view user data action', 'ViewUserDataAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (36, '2018-11-13 08:00:20.731', 0, 'Access to write user data action', 'WriteUserDataAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (37, '2018-11-13 08:00:20.735', 0, 'Access to manually add new reports action', 'AddReportAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (38, '2018-11-13 08:00:20.74', 0, 'Access to manually delete reports action', 'DeleteReportAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (39, '2018-11-13 08:00:20.744', 0, 'Access to read a user attribute action', 'ReadUserAttributeAction');
INSERT INTO PUBLIC.METC_permissions (id, last_updated, update_count, description, name) VALUES (40, '2018-11-13 08:00:20.747', 0, 'Access to write a user attribute action', 'WriteUserAttributeAction');


--
-- Data for Name: reports; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO PUBLIC.METC_roles (id, last_updated, update_count, description, name) VALUES (41, '2018-11-13 08:00:20.827', 0, 'Admin role', 'Admin');
INSERT INTO PUBLIC.METC_roles (id, last_updated, update_count, description, name) VALUES (42, '2018-11-13 08:00:20.88', 0, 'Trader role', 'Trader');
INSERT INTO PUBLIC.METC_roles (id, last_updated, update_count, description, name) VALUES (43, '2018-11-13 08:00:20.911', 0, 'Trader Admin role', 'TraderAdmin');


--
-- Data for Name: roles_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 21);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 11);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 39);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 24);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 9);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 40);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 5);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 6);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 17);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 18);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 29);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 7);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 12);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 23);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 14);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 10);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 35);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 22);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 28);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 19);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 27);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 30);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 26);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 13);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 15);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 16);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 20);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 8);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 25);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (41, 36);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 30);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 36);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 39);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 32);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 31);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 40);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 33);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 34);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 37);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (42, 35);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 38);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 36);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 32);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 31);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 39);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 33);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 30);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 40);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 34);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 35);
INSERT INTO PUBLIC.METC_roles_permissions (roles_id, permissions_id) VALUES (43, 37);


--
-- Data for Name: roles_users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO PUBLIC.METC_roles_users (role_id, subjects_id) VALUES (41, 1);
INSERT INTO PUBLIC.METC_roles_users (role_id, subjects_id) VALUES (42, 3);
INSERT INTO PUBLIC.METC_roles_users (role_id, subjects_id) VALUES (43, 4);


--
-- Data for Name: supervisor_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO PUBLIC.METC_supervisor_permissions (id, last_updated, update_count, description, name, user_id) VALUES (44, '2018-11-13 08:00:20.934', 0, 'Trader supervisor role', 'TraderSupervisor', 4);


--
-- Data for Name: supervisor_permissions_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO PUBLIC.METC_supervisor_permissions_permissions (supervisor_permission_id, permissions_id) VALUES (44, 31);
INSERT INTO PUBLIC.METC_supervisor_permissions_permissions (supervisor_permission_id, permissions_id) VALUES (44, 33);
INSERT INTO PUBLIC.METC_supervisor_permissions_permissions (supervisor_permission_id, permissions_id) VALUES (44, 35);
INSERT INTO PUBLIC.METC_supervisor_permissions_permissions (supervisor_permission_id, permissions_id) VALUES (44, 32);
INSERT INTO PUBLIC.METC_supervisor_permissions_permissions (supervisor_permission_id, permissions_id) VALUES (44, 30);


--
-- Data for Name: supervisor_permissions_users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO PUBLIC.METC_supervisor_permissions_users (supervisor_permission_id, subjects_id) VALUES (44, 3);


--
-- Data for Name: user_attributes; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO PUBLIC.METC_users (id, last_updated, update_count, description, name, is_active, password, is_superuser, user_data) VALUES (1, '2018-11-13 08:00:12.67', 0, NULL, 'admin', true, '$2a$10$kfVP4tGnFJrZf1pcjo5pVe7cbyVUguMYoz4tgP12gubXlbNwRJuP.', true, NULL);
INSERT INTO PUBLIC.METC_users (id, last_updated, update_count, description, name, is_active, password, is_superuser, user_data) VALUES (3, '2018-11-13 08:00:20.469', 0, 'Trader user', 'trader', true, '$2a$10$ISknot6OPe/PI1dQdKmTluxSrRyJwOVI5ex31XdqGPHlWl6vuTseW', false, NULL);
INSERT INTO PUBLIC.METC_users (id, last_updated, update_count, description, name, is_active, password, is_superuser, user_data) VALUES (4, '2018-11-13 08:00:20.515', 0, 'Trader Admin user', 'traderAdmin', true, '$2a$10$N6ADcu9ZdD3tFik0lkMRJO7PO0XFbxAjNMhujBjp6F0n3iJyVoGgG', false, NULL);


ALTER TABLE PUBLIC.METC_EXEC_REPORTS ADD CONSTRAINT FKHPHORR3UKX5A4OSBGFJHBRU27 FOREIGN KEY(ACTOR_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_EXEC_REPORTS ADD CONSTRAINT FKDWSI5079D6I3XAKR3K69M1HWF FOREIGN KEY(REPORT_ID) REFERENCES PUBLIC.METC_REPORTS(ID);
ALTER TABLE PUBLIC.METC_EXEC_REPORTS ADD CONSTRAINT FKLE9I7BL8YHN0F1OQ515MDRF3O FOREIGN KEY(VIEWER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_FIX_SESSION_ATTRIBUTES ADD CONSTRAINT FKHNBM41TVBTSLKF3IANQCKCXOI FOREIGN KEY(FIX_SESSION_ID) REFERENCES PUBLIC.METC_FIX_SESSIONS(ID);
ALTER TABLE PUBLIC.METC_ORDER_SUMMARIES ADD CONSTRAINT FKTDVMAWMT1HW68MP7EE0VL7LRJ FOREIGN KEY(ACTOR_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_ORDER_SUMMARIES ADD CONSTRAINT FKG5O6MB08Q2QBQE8KQC8TCE3YB FOREIGN KEY(REPORT_ID) REFERENCES PUBLIC.METC_REPORTS(ID);
ALTER TABLE PUBLIC.METC_ORDER_SUMMARIES ADD CONSTRAINT FKAQ0PYAOH7UDM4P77XAIBXAN9K FOREIGN KEY(VIEWER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_OUTGOING_MESSAGES ADD CONSTRAINT FKL0YD9TSANLOPWSKML2R2T6OJA FOREIGN KEY(ACTOR_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_PNL_CURRENT_POSITIONS ADD CONSTRAINT FKNOLJ1YHXJYLRD1WHBH3FTK2AC FOREIGN KEY(USER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_PNL_LOTS ADD CONSTRAINT FKDHYVSGC20RJQRCC528X9E2NEY FOREIGN KEY(POSITION_ID) REFERENCES PUBLIC.METC_PNL_POSITIONS(ID);
ALTER TABLE PUBLIC.METC_PNL_LOTS ADD CONSTRAINT FKBL0LPKAU4PJQQCCVTQ6DUC7AE FOREIGN KEY(TRADE_ID) REFERENCES PUBLIC.METC_TRADES(ID);
ALTER TABLE PUBLIC.METC_PNL_LOTS ADD CONSTRAINT FKQRRSXO6QYSBLLAAO4MPDNEUFQ FOREIGN KEY(USER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_PNL_POSITIONS ADD CONSTRAINT FKIYO24FA80L2IP7ATQ977MPDVL FOREIGN KEY(USER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_PROFIT_AND_LOSS ADD CONSTRAINT FK2K48AHRSFIJE46UY5P7WCRY2C FOREIGN KEY(USER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_REPORTS ADD CONSTRAINT FKRBYR9UO7EI1SYN57T9QVSSY93 FOREIGN KEY(ACTOR_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_REPORTS ADD CONSTRAINT FKF4N10SCGH7V0VCWW8K34AEJBM FOREIGN KEY(VIEWER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_ROLES_USERS ADD CONSTRAINT FK3M2C6T41DOF62MQSJ78KBLAIC FOREIGN KEY(SUBJECTS_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_STRATEGY_INSTANCES ADD CONSTRAINT FKDD09QEWNDXTDYQXYBYDXR6EXR FOREIGN KEY(USER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_SUPERVISOR_PERMISSIONS ADD CONSTRAINT FKBMVCIKRI3YDS2LI36ASV8GBHI FOREIGN KEY(USER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_SUPERVISOR_PERMISSIONS_USERS ADD CONSTRAINT FKHQ8D8XD5AGE7NFFHKJPTF7JX2 FOREIGN KEY(SUBJECTS_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_USER_ATTRIBUTES ADD CONSTRAINT FK73NAMH8JR9HNUG6RQ2EQ713G1 FOREIGN KEY(USER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER TABLE PUBLIC.METC_USER_TRADES ADD CONSTRAINT FKBIVM5LXG16VTRYBGWRIEOL9OU FOREIGN KEY(USER_ID) REFERENCES PUBLIC.METC_USERS(ID);
ALTER SEQUENCE SYSTEM_LOBS.LOB_ID RESTART WITH 1;
ALTER SEQUENCE PUBLIC.HIBERNATE_SEQUENCE RESTART WITH 1000;
SET DATABASE DEFAULT INITIAL SCHEMA PUBLIC;
GRANT USAGE ON DOMAIN INFORMATION_SCHEMA.CARDINAL_NUMBER TO PUBLIC;
GRANT USAGE ON DOMAIN INFORMATION_SCHEMA.YES_OR_NO TO PUBLIC;
GRANT USAGE ON DOMAIN INFORMATION_SCHEMA.CHARACTER_DATA TO PUBLIC;
GRANT USAGE ON DOMAIN INFORMATION_SCHEMA.SQL_IDENTIFIER TO PUBLIC;
GRANT USAGE ON DOMAIN INFORMATION_SCHEMA.TIME_STAMP TO PUBLIC;
GRANT DBA TO "metc";
