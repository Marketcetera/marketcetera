--------------------------------------------------------
--  File created - Wednesday-June-26-2019   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for Sequence HIBERNATE_SEQUENCE
--------------------------------------------------------

   CREATE SEQUENCE  "HIBERNATE_SEQUENCE"  MINVALUE 1 MAXVALUE 9999999999999999999999999999 INCREMENT BY 1 START WITH 121 CACHE 20 NOORDER  NOCYCLE  NOKEEP  NOSCALE  GLOBAL ;
--------------------------------------------------------
--  DDL for Table EXEC_REPORTS
--------------------------------------------------------

  CREATE TABLE "EXEC_REPORTS" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "ACCOUNT" VARCHAR2(255 CHAR), 
    "AVG_PRICE" NUMBER(17,7), 
    "CUM_QTY" NUMBER(17,7), 
    "EFF_CUM_QTY" NUMBER(17,7), 
    "EXEC_TYPE" NUMBER(10,0), 
    "EXPIRY" VARCHAR2(255 CHAR), 
    "LAST_PRICE" NUMBER(17,7), 
    "LAST_QTY" NUMBER(17,7), 
    "OPTION_TYPE" NUMBER(10,0), 
    "ORDER_ID" VARCHAR2(255 CHAR), 
    "ORD_STATUS" NUMBER(10,0), 
    "ORIG_ORDER_ID" VARCHAR2(255 CHAR), 
    "ROOT_ORDER_ID" VARCHAR2(255 CHAR), 
    "SECURITY_TYPE" NUMBER(10,0), 
    "SEND_TIME" TIMESTAMP (6), 
    "SIDE" NUMBER(10,0), 
    "STRIKE_PRICE" NUMBER(17,7), 
    "SYMBOL" VARCHAR2(255 CHAR), 
    "ACTOR_ID" NUMBER(19,0), 
    "REPORT_ID" NUMBER(19,0), 
    "VIEWER_ID" NUMBER(19,0)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table FIX_MESSAGES
--------------------------------------------------------

  CREATE TABLE "FIX_MESSAGES" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "MESSAGE" CLOB
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" 
 LOB ("MESSAGE") STORE AS SECUREFILE (
  TABLESPACE "USERS" ENABLE STORAGE IN ROW CHUNK 8192
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES ) ;
--------------------------------------------------------
--  DDL for Table FIX_SESSIONS
--------------------------------------------------------

  CREATE TABLE "FIX_SESSIONS" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "AFFINITY" NUMBER(10,0), 
    "BROKER_ID" VARCHAR2(255 CHAR), 
    "DESCRIPTION" VARCHAR2(255 CHAR), 
    "HOST" VARCHAR2(255 CHAR), 
    "ACCEPTOR" NUMBER(1,0), 
    "DELETED" NUMBER(1,0), 
    "ENABLED" NUMBER(1,0), 
    "NAME" VARCHAR2(255 CHAR), 
    "PORT" NUMBER(10,0), 
    "SESSION_ID" VARCHAR2(255 CHAR)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table FIX_SESSION_ATTRIBUTES
--------------------------------------------------------

  CREATE TABLE "FIX_SESSION_ATTRIBUTES" 
   (    "FIX_SESSION_ID" NUMBER(19,0), 
    "VALUE" VARCHAR2(255 CHAR), 
    "NAME" VARCHAR2(255 CHAR)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table FIX_SESSION_ATTR_DSCRPTRS
--------------------------------------------------------

  CREATE TABLE "FIX_SESSION_ATTR_DSCRPTRS" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "ADVICE" VARCHAR2(255 CHAR), 
    "DEFAULT_VALUE" VARCHAR2(255 CHAR), 
    "DESCRIPTION" VARCHAR2(1024 CHAR), 
    "NAME" VARCHAR2(255 CHAR), 
    "PATTERN" VARCHAR2(255 CHAR), 
    "REQUIRED" NUMBER(1,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ID_REPOSITORY
--------------------------------------------------------

  CREATE TABLE "ID_REPOSITORY" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "NEXT_ID" NUMBER(19,0)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table INCOMING_FIX_MESSAGES
--------------------------------------------------------

  CREATE TABLE "INCOMING_FIX_MESSAGES" 
   (    "ID" NUMBER(19,0), 
    "CLORDID" VARCHAR2(255 CHAR), 
    "EXECID" VARCHAR2(255 CHAR), 
    "MESSAGE" VARCHAR2(4000 CHAR), 
    "MSG_SEQ_NUM" NUMBER(10,0), 
    "MSG_TYPE" VARCHAR2(255 CHAR), 
    "SENDING_TIME" TIMESTAMP (6), 
    "FIX_SESSION" VARCHAR2(255 CHAR)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table MESSAGE_STORE_MESSAGES
--------------------------------------------------------

  CREATE TABLE "MESSAGE_STORE_MESSAGES" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "SESSION_ID" VARCHAR2(255 CHAR), 
    "MESSAGE" LONG, 
    "MSG_SEQ_NUM" NUMBER(10,0)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table MESSAGE_STORE_SESSIONS
--------------------------------------------------------

  CREATE TABLE "MESSAGE_STORE_SESSIONS" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "SESSION_ID" VARCHAR2(255 CHAR), 
    "CREATION_TIME" TIMESTAMP (6), 
    "SENDER_SEQ_NUM" NUMBER(10,0), 
    "TARGET_SEQ_NUM" NUMBER(10,0)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ORDER_STATUS
--------------------------------------------------------

  CREATE TABLE "ORDER_STATUS" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "ACCOUNT" VARCHAR2(255 CHAR), 
    "AVG_PX" NUMBER(17,7), 
    "BROKER_ID" VARCHAR2(255 CHAR), 
    "CUM_QTY" NUMBER(17,7), 
    "EXPIRY" VARCHAR2(255 CHAR), 
    "LAST_PX" NUMBER(17,7), 
    "LAST_QTY" NUMBER(17,7), 
    "LEAVES_QTY" NUMBER(17,7), 
    "OPTION_TYPE" NUMBER(10,0), 
    "ORDER_ID" VARCHAR2(255 CHAR), 
    "ORDER_PX" NUMBER(17,7), 
    "ORDER_QTY" NUMBER(17,7), 
    "ORD_STATUS" VARCHAR2(255 CHAR), 
    "ROOT_ORDER_ID" VARCHAR2(255 CHAR), 
    "SECURITY_TYPE" NUMBER(10,0), 
    "SENDING_TIME" TIMESTAMP (6), 
    "SIDE" NUMBER(10,0), 
    "STRIKE_PRICE" NUMBER(17,7), 
    "SYMBOL" VARCHAR2(255 CHAR), 
    "EXECUTION_TIME" TIMESTAMP (6), 
    "ACTOR_ID" NUMBER(19,0), 
    "REPORT_ID" NUMBER(19,0), 
    "VIEWER_ID" NUMBER(19,0)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table OUTGOING_MESSAGES
--------------------------------------------------------

  CREATE TABLE "OUTGOING_MESSAGES" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "BROKER_ID" VARCHAR2(255 CHAR), 
    "MESSAGE_TYPE" VARCHAR2(255 CHAR), 
    "MSG_SEQ_NUM" NUMBER(10,0), 
    "ORDER_ID" VARCHAR2(255 CHAR), 
    "SENDER_COMP_ID" VARCHAR2(255 CHAR), 
    "SESSION_ID" VARCHAR2(255 CHAR), 
    "TARGET_COMP_ID" VARCHAR2(255 CHAR), 
    "ACTOR_ID" NUMBER(19,0), 
    "FIX_MESSAGE_ID" NUMBER(19,0)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table PERMISSIONS
--------------------------------------------------------

  CREATE TABLE "PERMISSIONS" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "DESCRIPTION" VARCHAR2(255 CHAR), 
    "NAME" VARCHAR2(255 CHAR)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table REPORTS
--------------------------------------------------------

  CREATE TABLE "REPORTS" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "BROKER_ID" VARCHAR2(255 CHAR), 
    "HIERARCHY" NUMBER(10,0), 
    "ORIGINATOR" NUMBER(10,0), 
    "REPORT_TYPE" NUMBER(10,0), 
    "MSG_SEQ_NUM" NUMBER(10,0), 
    "ORDER_ID" VARCHAR2(255 CHAR), 
    "REPORT_ID" NUMBER(19,0), 
    "SEND_TIME" TIMESTAMP (6), 
    "SESSION_ID" VARCHAR2(255 CHAR), 
    "ACTOR_ID" NUMBER(19,0), 
    "FIX_MESSAGE_ID" NUMBER(19,0), 
    "VIEWER_ID" NUMBER(19,0)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ROLES
--------------------------------------------------------

  CREATE TABLE "ROLES" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "DESCRIPTION" VARCHAR2(255 CHAR), 
    "NAME" VARCHAR2(255 CHAR)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ROLES_PERMISSIONS
--------------------------------------------------------

  CREATE TABLE "ROLES_PERMISSIONS" 
   (    "ROLES_ID" NUMBER(19,0), 
    "PERMISSIONS_ID" NUMBER(19,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table ROLES_USERS
--------------------------------------------------------

  CREATE TABLE "ROLES_USERS" 
   (    "ROLE_ID" NUMBER(19,0), 
    "SUBJECTS_ID" NUMBER(19,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table SUPERVISOR_PERMISSIONS
--------------------------------------------------------

  CREATE TABLE "SUPERVISOR_PERMISSIONS" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "DESCRIPTION" VARCHAR2(255 CHAR), 
    "NAME" VARCHAR2(255 CHAR), 
    "USER_ID" NUMBER(19,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table SUPERVISOR_PERMISSIONS_PERMISSIONS
--------------------------------------------------------

  CREATE TABLE "SUPERVISOR_PERMISSIONS_PERMISSIONS" 
   (    "SUPERVISORPERMISSION_ID" NUMBER(19,0), 
    "PERMISSIONS_ID" NUMBER(19,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table SUPERVISOR_PERMISSIONS_USERS
--------------------------------------------------------

  CREATE TABLE "SUPERVISOR_PERMISSIONS_USERS" 
   (    "SUPERVISORPERMISSION_ID" NUMBER(19,0), 
    "SUBJECTS_ID" NUMBER(19,0)
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Table USERS
--------------------------------------------------------

  CREATE TABLE "USERS" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "DESCRIPTION" VARCHAR2(255 CHAR), 
    "NAME" VARCHAR2(255 CHAR), 
    "IS_ACTIVE" NUMBER(1,0), 
    "PASSWORD" VARCHAR2(255 CHAR), 
    "IS_SUPERUSER" NUMBER(1,0), 
    "USER_DATA" CLOB
   ) SEGMENT CREATION IMMEDIATE 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" 
 LOB ("USER_DATA") STORE AS SECUREFILE (
  TABLESPACE "USERS" ENABLE STORAGE IN ROW CHUNK 8192
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES 
  STORAGE(INITIAL 106496 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)) ;
--------------------------------------------------------
--  DDL for Table USER_ATTRIBUTES
--------------------------------------------------------

  CREATE TABLE "USER_ATTRIBUTES" 
   (    "ID" NUMBER(19,0), 
    "LAST_UPDATED" TIMESTAMP (6), 
    "UPDATE_COUNT" NUMBER(10,0), 
    "ATTRIBUTE" CLOB, 
    "USER_ATTRIBUTE_TYPE" NUMBER(10,0), 
    "USER_ID" NUMBER(19,0)
   ) SEGMENT CREATION DEFERRED 
  PCTFREE 10 PCTUSED 40 INITRANS 1 MAXTRANS 255 
 NOCOMPRESS LOGGING
  TABLESPACE "USERS" 
 LOB ("ATTRIBUTE") STORE AS SECUREFILE (
  TABLESPACE "USERS" ENABLE STORAGE IN ROW CHUNK 8192
  NOCACHE LOGGING  NOCOMPRESS  KEEP_DUPLICATES ) ;
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (44,to_timestamp('26-JUN-19 08.41.29.602000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'(Optional) Your subID as associated with this FIX session','SenderSubID',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (45,to_timestamp('26-JUN-19 08.41.29.614000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'(Optional) Your locationID as associated with this FIX session','SenderLocationID',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (46,to_timestamp('26-JUN-19 08.41.29.617000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'(Optional) counterparty''s subID as associated with this FIX session','TargetSubID',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (47,to_timestamp('26-JUN-19 08.41.29.620000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'(Optional) counterparty''s locationID as associated with this FIX session','TargetLocationID',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (48,to_timestamp('26-JUN-19 08.41.29.623000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Additional qualifier to disambiguate otherwise identical sessions. This can only be used with initiator sessions.Note: See Special notes for Oracle.','SessionQualifier',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (49,to_timestamp('26-JUN-19 08.41.29.626000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Required only for FIXT 1.1 (and newer). Ignored for earlier transport versions. Specifies the default application version ID for the session. This can either be the ApplVerID enum (see the ApplVerID field) the beginString for the default version.','DefaultApplVerID',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (50,to_timestamp('26-JUN-19 08.41.29.630000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Determines if milliseconds should be added to timestamps. Only available for FIX.4.2 and greater.','MillisecondsInTimeStamp',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (51,to_timestamp('26-JUN-19 08.41.29.633000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Use actual end of sequence gap for resend requests rather than using ''''infinity'''' as the end sequence of the gap. Not recommended by the FIX specification, but needed for some counterparties.','ClosedResendInterval',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (52,to_timestamp('26-JUN-19 08.41.29.636000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Tell session whether or not to expect a data dictionary. You should always use a DataDictionary if you are using repeating groups.','UseDataDictionary',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (53,to_timestamp('26-JUN-19 08.41.29.639000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'FIX42.xml','XML definition file for validating incoming FIX messages. If no DataDictionary is supplied, only basic message validation will be done. This setting should only be used with FIX transport versions old than FIXT 1.1. See TransportDataDictionary and ApplicationDataDictionary for FIXT 1.1 settings.','DataDictionary',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (54,to_timestamp('26-JUN-19 08.41.29.643000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'XML definition file for validating admin (transport) messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information.','TransportDataDictionary',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (55,to_timestamp('26-JUN-19 08.41.29.646000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'XML definition file for validating application messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information. This setting supports the possibility of a custom application data dictionary for each session. This setting would only be used with FIXT 1.1 and new transport protocols. This setting can be used as a prefix to specify multiple application dictionaries for the FIXT transport. For example: DefaultApplVerID=FIX.4.2 # For default application version ID AppDataDictionary=FIX42.xml # For nondefault application version ID # Use beginString suffix for app version AppDataDictionary.FIX.4.4=FIX44.xml This would use FIX42.xml for the default application version ID and FIX44.xml for any FIX 4.4 messages.','AppDataDictionary',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (56,to_timestamp('26-JUN-19 08.41.29.650000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','If set to N, fields that are out of order (i.e. body fields in the header, or header fields in the body) will not be rejected. Useful for connecting to systems which do not properly order fields.','ValidateFieldsOutOfOrder',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (57,to_timestamp('26-JUN-19 08.41.29.653000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','If set to N, fields without values (empty) will not be rejected. Useful for connecting to systems which improperly send empty tags.','ValidateFieldsHaveValues',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (58,to_timestamp('26-JUN-19 08.41.29.655000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','If set to N, user defined fields will not be rejected if they are not defined in the data dictionary, or are present in messages they do not belong to.','ValidateUserDefinedFields',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (59,to_timestamp('26-JUN-19 08.41.29.658000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Session validation setting for enabling whether field ordering is * validated. Values are ''''Y'''' or ''''N''''. Default is ''''Y''''.','ValidateUnorderedGroupFields',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (60,to_timestamp('26-JUN-19 08.41.29.662000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Allow to bypass the message validation (against the dictionary). Default is ''''Y''''.','ValidateIncomingMessage',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (61,to_timestamp('26-JUN-19 08.41.29.664000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Check the next expected target SeqNum against the received SeqNum. Default is ''''Y''''. If enabled and a mismatch is detected, apply the following logic:if lower than expected SeqNum , logout if higher, send a resend request If not enabled and a mismatch is detected, nothing is done. Must be enabled for EnableNextExpectedMsgSeqNum to work.','ValidateSequenceNumbers',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (62,to_timestamp('26-JUN-19 08.41.29.667000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Allow unknown fields in messages. This is intended for unknown fields with tags lt 5000 (not user defined fields). Use ValidateUserDefinedFields for controlling validation of tags ge 5000.','AllowUnknownMsgFields',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (63,to_timestamp('26-JUN-19 08.41.29.669000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','If set to Y, messages must be received from the counterparty with the correct SenderCompID and TargetCompID. Some systems will send you different CompIDs by design, so you must set this to N.','CheckCompID',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (64,to_timestamp('26-JUN-19 08.41.29.672000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','If set to Y, messages must be received from the counterparty within a defined number of seconds (see MaxLatency). It is useful to turn this off if a system uses localtime for its timestamps instead of GMT.','CheckLatency',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (65,to_timestamp('26-JUN-19 08.41.29.674000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'120','If CheckLatency is set to Y, this defines the number of seconds latency allowed for a message to be processed.','MaxLatency',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (66,to_timestamp('26-JUN-19 08.41.29.676000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','If RejectInvalidMessage is set to N, only a warning will be logged on reception of message that fails data dictionary validation.','RejectInvalidMessage',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (67,to_timestamp('26-JUN-19 08.41.29.679000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','If this configuration is enabled, an uncaught Exception or Error in the application''s message processing will lead to a (BusinessMessage)Reject being sent to the counterparty and the incoming message sequence number will be inc--ented. If disabled (default), the problematic incoming message is discarded and the message sequence number is not inc--ented. Processing of the next valid message will cause detection of a sequence gap and a ResendRequest will be generated.','RejectMessageOnUnhandledException',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (68,to_timestamp('26-JUN-19 08.41.29.682000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','If RequiresOrigSendingTime is set to N, PossDup messages lacking that field will not be rejected.','RequiresOrigSendingTime',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (69,to_timestamp('26-JUN-19 08.41.29.685000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'30','Time between reconnection attempts in seconds. Only used for initiators','ReconnectInterval',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (70,to_timestamp('26-JUN-19 08.41.29.688000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'30','Heartbeat interval in seconds. Only used for initiators.','HeartBtInt',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (71,to_timestamp('26-JUN-19 08.41.29.690000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'10','Number of seconds to wait for a logon response before disconnecting.','LogonTimeout',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (72,to_timestamp('26-JUN-19 08.41.29.693000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'2','Number of seconds to wait for a logout response before disconnecting.','LogoutTimeout',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (73,to_timestamp('26-JUN-19 08.41.29.695000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'TCP','Specifies the initiator communication protocol. The SocketConnectHost is not used with the VM_PIPE protocol, but the SocketConnectPort is significant and must match the acceptor configuration.','SocketConnectProtocol',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (74,to_timestamp('26-JUN-19 08.41.29.699000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Bind the local socket to this port. Only used with a SocketInitiator. If unset the socket will be bound to a free port from the ephemeral port range.','SocketLocalPort',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (75,to_timestamp('26-JUN-19 08.41.29.701000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Bind the local socket to this host. Only used with a SocketAcceptor. If unset the socket will be bound to all local interfaces.','SocketLocalHost',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (76,to_timestamp('26-JUN-19 08.41.29.704000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'TCP','Specifies the acceptor communication protocol. The SocketAcceptAddress is not used with the VM_PIPE protocol, but the SocketAcceptPort is significant and must match the initiator configuration.','SocketAcceptProtocol',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (77,to_timestamp('26-JUN-19 08.41.29.707000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Enter ''Y'' or ''N''','Y','Refresh the session state when a logon is received. This allows a simple form of failover when the message store data is persistent. The option will be ignored for message stores that are not persistent (e.g., MemoryStore).','RefreshOnLogon','^(Y|N){1}$',0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (78,to_timestamp('26-JUN-19 08.41.29.710000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Enables SSL usage for QFJ acceptor or initiator.','SocketUseSSL',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (79,to_timestamp('26-JUN-19 08.41.29.713000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'KeyStore to use with SSL','SocketKeyStore',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (80,to_timestamp('26-JUN-19 08.41.29.715000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'KeyStore password','SocketKeyStorePassword',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (81,to_timestamp('26-JUN-19 08.41.29.718000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'When the keepalive option is set for a TCP socket and no data has been exchanged across the socket in either direction for 2 hours (NOTE: the actual value is implementation dependent), TCP automatically sends a keepalive probe to the peer. This probe is a TCP segment to which the peer must respond. One of three responses is expected: The peer responds with the expected ACK. The application is not notified (since everything is OK). TCP will send another probe following another 2 hours of inactivity. The peer responds with an RST, which tells the local TCP that the peer host has crashed and rebooted. The socket is closed. There is no response from the peer. The socket is closed. The purpose of this option is to detect if the peer host crashes.','SocketKeepAlive',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (82,to_timestamp('26-JUN-19 08.41.29.721000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'When the OOBINLINE option is set, any TCP urgent data received on the socket will be received through the socket input stream. When the option is disabled (which is the default) urgent data is silently discarded.','SocketOobInline',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (83,to_timestamp('26-JUN-19 08.41.29.723000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Set a hint the size of the underlying buffers used by the platform for incoming network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be received over the socket.','SocketReceiveBufferSize',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (84,to_timestamp('26-JUN-19 08.41.29.725000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Sets SO_REUSEADDR for a socket. This is used only for MulticastSockets in java, and it is set by default for MulticastSockets.','SocketReuseAddress',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (85,to_timestamp('26-JUN-19 08.41.29.727000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Set a hint the size of the underlying buffers used by the platform for outgoing network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be sent over the socket.','SocketSendBufferSize',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (86,to_timestamp('26-JUN-19 08.41.29.729000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Specify a linger-on-close timeout. This option disables/enables immediate return from a close() of a TCP Socket. Enabling this option with a non-zero Integer timeout means that a close() will block pending the transmission and acknowledgement of all data written to the peer, at which point the socket is closed gracefully. Upon reaching the linger timeout, the socket is closed forcefully, with a TCP RST. Enabling the option with a timeout of zero does a forceful close immediately. If the specified timeout value exceeds 65,535 it will be reduced to 65,535.','SocketLinger',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (87,to_timestamp('26-JUN-19 08.41.29.732000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Disable Nagle''s algorithm for this connection. Written data to the network is not buffered pending acknowledgement of previously written data.','SocketTcpNoDelay',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (88,to_timestamp('26-JUN-19 08.41.29.734000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Sets traffic class or type-of-service octet in the IP header for packets sent from this Socket. As the underlying network implementation may ignore this value applications should consider it a hint. The tc must be in the range 0 = tc = 255 or an IllegalArgumentException will be thrown. Notes: for Internet Protocol v4 the value consists of an octet with precedence and TOS fields as detailed in RFC 1349. The TOS field is bitset created by bitwise-or''ing values such the following :- IPTOS_LOWCOST (0x02) IPTOS_RELIABILITY (0x04) IPTOS_THROUGHPUT (0x08) IPTOS_LOWDELAY (0x10) The last low order bit is always ignored as this corresponds to the MBZ (must be zero) bit. Setting bits in the precedence field may result in a SocketException indicating that the operation is not permitted.','SocketTrafficClass',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (89,to_timestamp('26-JUN-19 08.41.29.737000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Write messages synchronously. This is not generally recommended as it may result in performance degradation. The MINA communication layer is asynchronous by design, but this option will override that behavior if needed.','SocketSynchronousWrites',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (90,to_timestamp('26-JUN-19 08.41.29.740000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'30000','The time in milliseconds to wait for a write to complete.','SocketSynchronousWriteTimeout',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (91,to_timestamp('26-JUN-19 08.41.29.742000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','If set to N, no messages will be persisted. This will force QFJ to always send GapFills instead of resending messages. Use this if you know you never want to resend a message. Useful for market data streams.','PersistMessages',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (92,to_timestamp('26-JUN-19 08.41.29.747000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Controls whether milliseconds are included in log time stamps.','FileIncludeMilliseconds',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (93,to_timestamp('26-JUN-19 08.41.29.750000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Controls whether time stamps are included on message log entries.','FileIncludeTimestampForMessages',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (94,to_timestamp('26-JUN-19 08.41.29.753000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'quickfixj.event','Log category for logged events.','SLF4JLogEventCategory',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (95,to_timestamp('26-JUN-19 08.41.29.755000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'quickfixj.msg.incoming','Log category for incoming messages.','SLF4JLogIncomingMessageCategory',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (96,to_timestamp('26-JUN-19 08.41.29.757000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'quickfixj.msg.outgoing','Log category for outgoing messages.','SLF4JLogOutgoingMessageCategory',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (97,to_timestamp('26-JUN-19 08.41.29.759000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Controls whether session ID is prepended to log message.','SLF4JLogPrependSessionID',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (98,to_timestamp('26-JUN-19 08.41.29.762000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Controls whether heartbeats are logged.','SLF4JLogHeartbeats',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (99,to_timestamp('26-JUN-19 08.41.29.764000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Log events to screen.','ScreenLogEvents',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (100,to_timestamp('26-JUN-19 08.41.29.766000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Log incoming messages to screen.','ScreenLogShowIncoming',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (101,to_timestamp('26-JUN-19 08.41.29.769000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'Y','Log outgoing messages to screen.','ScreenLogShowOutgoing',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (102,to_timestamp('26-JUN-19 08.41.29.772000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Filter heartbeats from output (both incoming and outgoing)','ScreenLogShowHeartbeats',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (103,to_timestamp('26-JUN-19 08.41.29.775000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Determines if sequence numbers should be reset before sending/receiving a logon request.','ResetOnLogon',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (104,to_timestamp('26-JUN-19 08.41.29.777000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Determines if sequence numbers should be reset to 1 after a normal logout termination.','ResetOnLogout',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (105,to_timestamp('26-JUN-19 08.41.29.780000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Determines if sequence numbers should be reset to 1 after an abnormal termination.','ResetOnDisconnect',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (106,to_timestamp('26-JUN-19 08.41.29.782000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Session setting for doing an automatic reset when an error occurs. A reset means disconnect, sequence numbers reset, store cleaned and reconnect, as for a daily reset.','ResetOnError',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (107,to_timestamp('26-JUN-19 08.41.29.784000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Session setting for doing an automatic disconnect when an error occurs.','DisconnectOnError',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (108,to_timestamp('26-JUN-19 08.41.29.786000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Add tag LastMsgSeqNumProcessed in the header (optional tag 369).','EnableLastMsgSeqNumProcessed',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (109,to_timestamp('26-JUN-19 08.41.29.789000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Add tag NextExpectedMsgSeqNum (optional tag 789) on the sent Logon message and use value of tag 789 on received Logon message to synchronize session. This should not be enabled for FIX versions lt 4.4. Only works when ValidateSequenceNumbers is enabled.','EnableNextExpectedMsgSeqNum',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (110,to_timestamp('26-JUN-19 08.41.29.791000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'0','Setting to limit the size of a resend request in case of missing messages. This is useful when the --ote FIX engine does not allow to ask for more than n message for a ResendRequest. E.g. if the ResendRequestChunkSize is set to 5 and a gap of 7 messages is detected, a first resend request will be sent for 5 messages. When this gap has been filled, another resend request for 2 messages will be sent. If the ResendRequestChunkSize is set to 0, only one ResendRequest for all the missing messages will be sent.','ResendRequestChunkSize',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (111,to_timestamp('26-JUN-19 08.41.29.793000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Continue initializing sessions if an error occurs.','ContinueInitializationOnError',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (112,to_timestamp('26-JUN-19 08.41.29.795000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Allows sending of redundant resend requests.','SendRedundantResendRequests',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (113,to_timestamp('26-JUN-19 08.41.29.798000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'0.5','Fraction of the heartbeat interval which defines the additional time to wait if a TestRequest sent after a missing heartbeat times out.','TestRequestDelayMultiplier',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (114,to_timestamp('26-JUN-19 08.41.29.800000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Heartbeat detection is disabled. A disconnect due to a missing heartbeat will never occur.','DisableHeartBeatCheck',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (115,to_timestamp('26-JUN-19 08.41.29.803000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,'N','Fill in heartbeats on resend when reading from message store fails.','ForceResendWhenCorruptedStore',null,0);
Insert into FIX_SESSION_ATTR_DSCRPTRS (ID,LAST_UPDATED,UPDATE_COUNT,ADVICE,DEFAULT_VALUE,DESCRIPTION,NAME,PATTERN,REQUIRED) values (116,to_timestamp('26-JUN-19 08.41.29.805000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,null,null,'Name of the session modifiers to apply to this session','org.marketcetera.sessioncustomization',null,0);
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (4,to_timestamp('26-JUN-19 08.41.27.586000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to Add Session action','AddSessionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (5,to_timestamp('26-JUN-19 08.41.27.604000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to Delete Session action','DeleteSessionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (6,to_timestamp('26-JUN-19 08.41.27.610000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to disable session action','DisableSessionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (7,to_timestamp('26-JUN-19 08.41.27.617000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to edit session action','EditSessionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (8,to_timestamp('26-JUN-19 08.41.27.623000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to enable session action','EnableSessionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (9,to_timestamp('26-JUN-19 08.41.27.629000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to update sequence numbers action','UpdateSequenceAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (10,to_timestamp('26-JUN-19 08.41.27.636000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to start session action','StartSessionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (11,to_timestamp('26-JUN-19 08.41.27.642000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to stop session action','StopSessionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (12,to_timestamp('26-JUN-19 08.41.27.647000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to view session action','ViewSessionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (13,to_timestamp('26-JUN-19 08.41.27.652000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to read instance data action','ReadInstanceDataAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (14,to_timestamp('26-JUN-19 08.41.27.659000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to read FIX session attribute descriptors action','ReadFixSessionAttributeDescriptorsAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (15,to_timestamp('26-JUN-19 08.41.27.666000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to create user action','CreateUserAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (16,to_timestamp('26-JUN-19 08.41.27.672000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to read user action','ReadUserAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (17,to_timestamp('26-JUN-19 08.41.27.683000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to update user action','UpdateUserAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (18,to_timestamp('26-JUN-19 08.41.27.689000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to delete user action','DeleteUserAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (19,to_timestamp('26-JUN-19 08.41.27.694000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to change user password action','ChangeUserPasswordAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (20,to_timestamp('26-JUN-19 08.41.27.699000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to read user permissions action','ReadUserPermisionsAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (21,to_timestamp('26-JUN-19 08.41.27.704000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to create permission action','CreatePermissionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (22,to_timestamp('26-JUN-19 08.41.27.710000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to read permission action','ReadPermissionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (23,to_timestamp('26-JUN-19 08.41.27.715000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to update permission action','UpdatePermissionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (24,to_timestamp('26-JUN-19 08.41.27.719000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to delete permission action','DeletePermissionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (25,to_timestamp('26-JUN-19 08.41.27.724000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to create role action','CreateRoleAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (26,to_timestamp('26-JUN-19 08.41.27.728000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to read role action','ReadRoleAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (27,to_timestamp('26-JUN-19 08.41.27.733000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to update role action','UpdateRoleAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (28,to_timestamp('26-JUN-19 08.41.27.738000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to delete role action','DeleteRoleAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (29,to_timestamp('26-JUN-19 08.41.27.743000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to view broker status action','ViewBrokerStatusAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (30,to_timestamp('26-JUN-19 08.41.27.748000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to view open orders action','ViewOpenOrdersAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (31,to_timestamp('26-JUN-19 08.41.27.752000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to view reports action','ViewReportAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (32,to_timestamp('26-JUN-19 08.41.27.757000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to view positions action','ViewPositionAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (33,to_timestamp('26-JUN-19 08.41.27.761000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to send new orders action','SendOrderAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (34,to_timestamp('26-JUN-19 08.41.27.765000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to view user data action','ViewUserDataAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (35,to_timestamp('26-JUN-19 08.41.27.769000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to write user data action','WriteUserDataAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (36,to_timestamp('26-JUN-19 08.41.27.774000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to manually add new reports action','AddReportAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (37,to_timestamp('26-JUN-19 08.41.27.778000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to manually delete reports action','DeleteReportAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (38,to_timestamp('26-JUN-19 08.41.27.783000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to read a user attribute action','ReadUserAttributeAction');
Insert into PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (39,to_timestamp('26-JUN-19 08.41.27.788000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Access to write a user attribute action','WriteUserAttributeAction');
Insert into ROLES (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (40,to_timestamp('26-JUN-19 08.41.27.877000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin role','Admin');
Insert into ROLES (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (41,to_timestamp('26-JUN-19 08.41.27.964000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Trader role','Trader');
Insert into ROLES (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME) values (42,to_timestamp('26-JUN-19 08.41.28.008000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Trader Admin role','TraderAdmin');
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,4);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,5);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,6);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,7);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,8);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,9);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,10);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,11);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,12);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,13);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,14);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,15);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,16);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,17);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,18);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,19);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,20);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,21);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,22);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,23);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,24);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,25);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,26);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,27);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,28);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,29);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,34);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,35);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,38);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (40,39);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,29);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,30);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,31);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,32);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,33);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,34);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,35);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,36);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,38);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (41,39);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,29);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,30);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,31);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,32);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,33);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,34);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,35);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,36);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,37);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,38);
Insert into ROLES_PERMISSIONS (ROLES_ID,PERMISSIONS_ID) values (42,39);
Insert into ROLES_USERS (ROLE_ID,SUBJECTS_ID) values (40,3);
Insert into ROLES_USERS (ROLE_ID,SUBJECTS_ID) values (41,1);
Insert into ROLES_USERS (ROLE_ID,SUBJECTS_ID) values (42,2);
Insert into SUPERVISOR_PERMISSIONS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME,USER_ID) values (43,to_timestamp('26-JUN-19 08.41.28.045000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Trader supervisor role','TraderSupervisor',2);
Insert into SUPERVISOR_PERMISSIONS_PERMISSIONS (SUPERVISORPERMISSION_ID,PERMISSIONS_ID) values (43,29);
Insert into SUPERVISOR_PERMISSIONS_PERMISSIONS (SUPERVISORPERMISSION_ID,PERMISSIONS_ID) values (43,30);
Insert into SUPERVISOR_PERMISSIONS_PERMISSIONS (SUPERVISORPERMISSION_ID,PERMISSIONS_ID) values (43,31);
Insert into SUPERVISOR_PERMISSIONS_PERMISSIONS (SUPERVISORPERMISSION_ID,PERMISSIONS_ID) values (43,32);
Insert into SUPERVISOR_PERMISSIONS_PERMISSIONS (SUPERVISORPERMISSION_ID,PERMISSIONS_ID) values (43,34);
Insert into SUPERVISOR_PERMISSIONS_USERS (SUPERVISORPERMISSION_ID,SUBJECTS_ID) values (43,1);
Insert into USERS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME,IS_ACTIVE,PASSWORD,IS_SUPERUSER) values (1,to_timestamp('26-JUN-19 08.41.27.497000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Trader user','trader',1,'2zg91043ou3eki4ysbejwwgkci37e6j',0);
Insert into USERS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME,IS_ACTIVE,PASSWORD,IS_SUPERUSER) values (2,to_timestamp('26-JUN-19 08.41.27.564000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Trader Admin user','traderAdmin',1,'210ui1dyyf6voajrad4gmpt3vgvvm9o',0);
Insert into USERS (ID,LAST_UPDATED,UPDATE_COUNT,DESCRIPTION,NAME,IS_ACTIVE,PASSWORD,IS_SUPERUSER) values (3,to_timestamp('26-JUN-19 08.41.27.573000000 AM','DD-MON-RR HH.MI.SSXFF AM'),0,'Admin user','admin',1,'6anqbgybi82pveayzrkt3egjkwfwdg5',1);
--------------------------------------------------------
--  DDL for Index UK_IMDQ099U0QA8OB9TT5LJM6F7U
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK_IMDQ099U0QA8OB9TT5LJM6F7U" ON "EXEC_REPORTS" ("REPORT_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UK_GB3X058KYH5S4FXW1FI67BYSC
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK_GB3X058KYH5S4FXW1FI67BYSC" ON "FIX_SESSION_ATTR_DSCRPTRS" ("NAME") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UK_HXVC6VRTMW1SWIK69WXT0DRLC
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK_HXVC6VRTMW1SWIK69WXT0DRLC" ON "OUTGOING_MESSAGES" ("FIX_MESSAGE_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UK3G1J96G94XPK3LPXL2QBL985X
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK3G1J96G94XPK3LPXL2QBL985X" ON "USERS" ("NAME") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UK_PNVTWLIIS6P05PN6I3NDJRQT2
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK_PNVTWLIIS6P05PN6I3NDJRQT2" ON "PERMISSIONS" ("NAME") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UK_H8V9N38CYDUSMK1D0YYA6ND2D
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK_H8V9N38CYDUSMK1D0YYA6ND2D" ON "ORDER_STATUS" ("REPORT_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UK_AELY7CHRVTQWV4XFM76XUJ5BH
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK_AELY7CHRVTQWV4XFM76XUJ5BH" ON "REPORTS" ("REPORT_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UK_CA90A4KKDYCPON22YNLI3D6OI
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK_CA90A4KKDYCPON22YNLI3D6OI" ON "REPORTS" ("FIX_MESSAGE_ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UK_OFX66KERUAPI6VYQPV6F2OR37
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK_OFX66KERUAPI6VYQPV6F2OR37" ON "ROLES" ("NAME") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index UK_4RD5TOWBSHLB1V1HV6W00SF6B
--------------------------------------------------------

  CREATE UNIQUE INDEX "UK_4RD5TOWBSHLB1V1HV6W00SF6B" ON "SUPERVISOR_PERMISSIONS" ("NAME") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  Constraints for Table USER_ATTRIBUTES
--------------------------------------------------------

  ALTER TABLE "USER_ATTRIBUTES" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "USER_ATTRIBUTES" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "USER_ATTRIBUTES" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "USER_ATTRIBUTES" MODIFY ("ATTRIBUTE" NOT NULL ENABLE);
  ALTER TABLE "USER_ATTRIBUTES" MODIFY ("USER_ATTRIBUTE_TYPE" NOT NULL ENABLE);
  ALTER TABLE "USER_ATTRIBUTES" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table MESSAGE_STORE_MESSAGES
--------------------------------------------------------

  ALTER TABLE "MESSAGE_STORE_MESSAGES" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_MESSAGES" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_MESSAGES" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_MESSAGES" MODIFY ("SESSION_ID" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_MESSAGES" MODIFY ("MESSAGE" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_MESSAGES" MODIFY ("MSG_SEQ_NUM" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_MESSAGES" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table MESSAGE_STORE_SESSIONS
--------------------------------------------------------

  ALTER TABLE "MESSAGE_STORE_SESSIONS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_SESSIONS" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_SESSIONS" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_SESSIONS" MODIFY ("SESSION_ID" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_SESSIONS" MODIFY ("CREATION_TIME" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_SESSIONS" MODIFY ("SENDER_SEQ_NUM" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_SESSIONS" MODIFY ("TARGET_SEQ_NUM" NOT NULL ENABLE);
  ALTER TABLE "MESSAGE_STORE_SESSIONS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table SUPERVISOR_PERMISSIONS_USERS
--------------------------------------------------------

  ALTER TABLE "SUPERVISOR_PERMISSIONS_USERS" MODIFY ("SUPERVISORPERMISSION_ID" NOT NULL ENABLE);
  ALTER TABLE "SUPERVISOR_PERMISSIONS_USERS" MODIFY ("SUBJECTS_ID" NOT NULL ENABLE);
  ALTER TABLE "SUPERVISOR_PERMISSIONS_USERS" ADD PRIMARY KEY ("SUPERVISORPERMISSION_ID", "SUBJECTS_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table FIX_SESSION_ATTR_DSCRPTRS
--------------------------------------------------------

  ALTER TABLE "FIX_SESSION_ATTR_DSCRPTRS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSION_ATTR_DSCRPTRS" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSION_ATTR_DSCRPTRS" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSION_ATTR_DSCRPTRS" MODIFY ("NAME" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSION_ATTR_DSCRPTRS" MODIFY ("REQUIRED" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSION_ATTR_DSCRPTRS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "FIX_SESSION_ATTR_DSCRPTRS" ADD CONSTRAINT "UK_GB3X058KYH5S4FXW1FI67BYSC" UNIQUE ("NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ID_REPOSITORY
--------------------------------------------------------

  ALTER TABLE "ID_REPOSITORY" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "ID_REPOSITORY" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "ID_REPOSITORY" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "ID_REPOSITORY" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ORDER_STATUS
--------------------------------------------------------

  ALTER TABLE "ORDER_STATUS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("AVG_PX" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("CUM_QTY" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("LAST_PX" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("LAST_QTY" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("LEAVES_QTY" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("ORDER_ID" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("ORDER_QTY" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("ORD_STATUS" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("ROOT_ORDER_ID" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("SECURITY_TYPE" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("SENDING_TIME" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("SIDE" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("SYMBOL" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" MODIFY ("REPORT_ID" NOT NULL ENABLE);
  ALTER TABLE "ORDER_STATUS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ORDER_STATUS" ADD CONSTRAINT "UK_H8V9N38CYDUSMK1D0YYA6ND2D" UNIQUE ("REPORT_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table SUPERVISOR_PERMISSIONS_PERMISSIONS
--------------------------------------------------------

  ALTER TABLE "SUPERVISOR_PERMISSIONS_PERMISSIONS" MODIFY ("SUPERVISORPERMISSION_ID" NOT NULL ENABLE);
  ALTER TABLE "SUPERVISOR_PERMISSIONS_PERMISSIONS" MODIFY ("PERMISSIONS_ID" NOT NULL ENABLE);
  ALTER TABLE "SUPERVISOR_PERMISSIONS_PERMISSIONS" ADD PRIMARY KEY ("SUPERVISORPERMISSION_ID", "PERMISSIONS_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table FIX_MESSAGES
--------------------------------------------------------

  ALTER TABLE "FIX_MESSAGES" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "FIX_MESSAGES" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "FIX_MESSAGES" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "FIX_MESSAGES" MODIFY ("MESSAGE" NOT NULL ENABLE);
  ALTER TABLE "FIX_MESSAGES" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ROLES_PERMISSIONS
--------------------------------------------------------

  ALTER TABLE "ROLES_PERMISSIONS" MODIFY ("ROLES_ID" NOT NULL ENABLE);
  ALTER TABLE "ROLES_PERMISSIONS" MODIFY ("PERMISSIONS_ID" NOT NULL ENABLE);
  ALTER TABLE "ROLES_PERMISSIONS" ADD PRIMARY KEY ("ROLES_ID", "PERMISSIONS_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table INCOMING_FIX_MESSAGES
--------------------------------------------------------

  ALTER TABLE "INCOMING_FIX_MESSAGES" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "INCOMING_FIX_MESSAGES" MODIFY ("MESSAGE" NOT NULL ENABLE);
  ALTER TABLE "INCOMING_FIX_MESSAGES" MODIFY ("MSG_SEQ_NUM" NOT NULL ENABLE);
  ALTER TABLE "INCOMING_FIX_MESSAGES" MODIFY ("MSG_TYPE" NOT NULL ENABLE);
  ALTER TABLE "INCOMING_FIX_MESSAGES" MODIFY ("SENDING_TIME" NOT NULL ENABLE);
  ALTER TABLE "INCOMING_FIX_MESSAGES" MODIFY ("FIX_SESSION" NOT NULL ENABLE);
  ALTER TABLE "INCOMING_FIX_MESSAGES" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ROLES
--------------------------------------------------------

  ALTER TABLE "ROLES" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "ROLES" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "ROLES" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "ROLES" MODIFY ("NAME" NOT NULL ENABLE);
  ALTER TABLE "ROLES" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "ROLES" ADD CONSTRAINT "UK_OFX66KERUAPI6VYQPV6F2OR37" UNIQUE ("NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table FIX_SESSION_ATTRIBUTES
--------------------------------------------------------

  ALTER TABLE "FIX_SESSION_ATTRIBUTES" MODIFY ("FIX_SESSION_ID" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSION_ATTRIBUTES" MODIFY ("NAME" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSION_ATTRIBUTES" ADD PRIMARY KEY ("FIX_SESSION_ID", "NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table FIX_SESSIONS
--------------------------------------------------------

  ALTER TABLE "FIX_SESSIONS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("AFFINITY" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("BROKER_ID" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("HOST" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("ACCEPTOR" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("DELETED" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("ENABLED" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("NAME" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("PORT" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" MODIFY ("SESSION_ID" NOT NULL ENABLE);
  ALTER TABLE "FIX_SESSIONS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PERMISSIONS
--------------------------------------------------------

  ALTER TABLE "PERMISSIONS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "PERMISSIONS" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "PERMISSIONS" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "PERMISSIONS" MODIFY ("NAME" NOT NULL ENABLE);
  ALTER TABLE "PERMISSIONS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "PERMISSIONS" ADD CONSTRAINT "UK_PNVTWLIIS6P05PN6I3NDJRQT2" UNIQUE ("NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table OUTGOING_MESSAGES
--------------------------------------------------------

  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("BROKER_ID" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("MESSAGE_TYPE" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("SENDER_COMP_ID" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("SESSION_ID" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("TARGET_COMP_ID" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("ACTOR_ID" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" MODIFY ("FIX_MESSAGE_ID" NOT NULL ENABLE);
  ALTER TABLE "OUTGOING_MESSAGES" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "OUTGOING_MESSAGES" ADD CONSTRAINT "UK_HXVC6VRTMW1SWIK69WXT0DRLC" UNIQUE ("FIX_MESSAGE_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table SUPERVISOR_PERMISSIONS
--------------------------------------------------------

  ALTER TABLE "SUPERVISOR_PERMISSIONS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "SUPERVISOR_PERMISSIONS" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "SUPERVISOR_PERMISSIONS" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "SUPERVISOR_PERMISSIONS" MODIFY ("NAME" NOT NULL ENABLE);
  ALTER TABLE "SUPERVISOR_PERMISSIONS" MODIFY ("USER_ID" NOT NULL ENABLE);
  ALTER TABLE "SUPERVISOR_PERMISSIONS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "SUPERVISOR_PERMISSIONS" ADD CONSTRAINT "UK_4RD5TOWBSHLB1V1HV6W00SF6B" UNIQUE ("NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table EXEC_REPORTS
--------------------------------------------------------

  ALTER TABLE "EXEC_REPORTS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("AVG_PRICE" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("CUM_QTY" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("EFF_CUM_QTY" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("EXEC_TYPE" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("ORDER_ID" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("ORD_STATUS" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("ROOT_ORDER_ID" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("SECURITY_TYPE" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("SEND_TIME" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("SIDE" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("SYMBOL" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" MODIFY ("REPORT_ID" NOT NULL ENABLE);
  ALTER TABLE "EXEC_REPORTS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "EXEC_REPORTS" ADD CONSTRAINT "UK_IMDQ099U0QA8OB9TT5LJM6F7U" UNIQUE ("REPORT_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table REPORTS
--------------------------------------------------------

  ALTER TABLE "REPORTS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" MODIFY ("REPORT_TYPE" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" MODIFY ("MSG_SEQ_NUM" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" MODIFY ("ORDER_ID" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" MODIFY ("REPORT_ID" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" MODIFY ("SEND_TIME" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" MODIFY ("SESSION_ID" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" MODIFY ("FIX_MESSAGE_ID" NOT NULL ENABLE);
  ALTER TABLE "REPORTS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "REPORTS" ADD CONSTRAINT "UK_CA90A4KKDYCPON22YNLI3D6OI" UNIQUE ("FIX_MESSAGE_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "REPORTS" ADD CONSTRAINT "UK_AELY7CHRVTQWV4XFM76XUJ5BH" UNIQUE ("REPORT_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table ROLES_USERS
--------------------------------------------------------

  ALTER TABLE "ROLES_USERS" MODIFY ("ROLE_ID" NOT NULL ENABLE);
  ALTER TABLE "ROLES_USERS" MODIFY ("SUBJECTS_ID" NOT NULL ENABLE);
  ALTER TABLE "ROLES_USERS" ADD PRIMARY KEY ("ROLE_ID", "SUBJECTS_ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table USERS
--------------------------------------------------------

  ALTER TABLE "USERS" MODIFY ("ID" NOT NULL ENABLE);
  ALTER TABLE "USERS" MODIFY ("LAST_UPDATED" NOT NULL ENABLE);
  ALTER TABLE "USERS" MODIFY ("UPDATE_COUNT" NOT NULL ENABLE);
  ALTER TABLE "USERS" MODIFY ("NAME" NOT NULL ENABLE);
  ALTER TABLE "USERS" MODIFY ("IS_ACTIVE" NOT NULL ENABLE);
  ALTER TABLE "USERS" MODIFY ("PASSWORD" NOT NULL ENABLE);
  ALTER TABLE "USERS" MODIFY ("IS_SUPERUSER" NOT NULL ENABLE);
  ALTER TABLE "USERS" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
  ALTER TABLE "USERS" ADD CONSTRAINT "UK3G1J96G94XPK3LPXL2QBL985X" UNIQUE ("NAME")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1
  BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table EXEC_REPORTS
--------------------------------------------------------

  ALTER TABLE "EXEC_REPORTS" ADD CONSTRAINT "FKFN47LJ607GHTT1LFIE8ADEVXC" FOREIGN KEY ("ACTOR_ID")
      REFERENCES "USERS" ("ID") ENABLE;
  ALTER TABLE "EXEC_REPORTS" ADD CONSTRAINT "FKN47TA2B6E9WIH8B97OUBXVD3S" FOREIGN KEY ("REPORT_ID")
      REFERENCES "REPORTS" ("ID") ENABLE;
  ALTER TABLE "EXEC_REPORTS" ADD CONSTRAINT "FK1CELHYPJ9VINT37EOBSN22S1B" FOREIGN KEY ("VIEWER_ID")
      REFERENCES "USERS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table FIX_SESSION_ATTRIBUTES
--------------------------------------------------------

  ALTER TABLE "FIX_SESSION_ATTRIBUTES" ADD CONSTRAINT "FK3LRQYAMU7790PIE2IVJH8VFQ5" FOREIGN KEY ("FIX_SESSION_ID")
      REFERENCES "FIX_SESSIONS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ORDER_STATUS
--------------------------------------------------------

  ALTER TABLE "ORDER_STATUS" ADD CONSTRAINT "FKLXKG9IL8Q8K9448O5NGGIBNBS" FOREIGN KEY ("ACTOR_ID")
      REFERENCES "USERS" ("ID") ENABLE;
  ALTER TABLE "ORDER_STATUS" ADD CONSTRAINT "FKJQTX22V71DOD89THT0FYWAV7" FOREIGN KEY ("REPORT_ID")
      REFERENCES "REPORTS" ("ID") ENABLE;
  ALTER TABLE "ORDER_STATUS" ADD CONSTRAINT "FKT8IYSRML49HNMEMBW8PKV5G3N" FOREIGN KEY ("VIEWER_ID")
      REFERENCES "USERS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table OUTGOING_MESSAGES
--------------------------------------------------------

  ALTER TABLE "OUTGOING_MESSAGES" ADD CONSTRAINT "FKPKYA7XSUMLSBFM4B125K07KE8" FOREIGN KEY ("ACTOR_ID")
      REFERENCES "USERS" ("ID") ENABLE;
  ALTER TABLE "OUTGOING_MESSAGES" ADD CONSTRAINT "FK7AESWC52COXK8SSPDT9UA5E15" FOREIGN KEY ("FIX_MESSAGE_ID")
      REFERENCES "FIX_MESSAGES" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table REPORTS
--------------------------------------------------------

  ALTER TABLE "REPORTS" ADD CONSTRAINT "FKH0R6PPU75BYN1Y7Y0UITEEL8Q" FOREIGN KEY ("ACTOR_ID")
      REFERENCES "USERS" ("ID") ENABLE;
  ALTER TABLE "REPORTS" ADD CONSTRAINT "FK98BMVK76E2GP10MUHEOG0J1WA" FOREIGN KEY ("FIX_MESSAGE_ID")
      REFERENCES "FIX_MESSAGES" ("ID") ENABLE;
  ALTER TABLE "REPORTS" ADD CONSTRAINT "FKSFC0WDPJFEROHMPYLYGFF4URS" FOREIGN KEY ("VIEWER_ID")
      REFERENCES "USERS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ROLES_PERMISSIONS
--------------------------------------------------------

  ALTER TABLE "ROLES_PERMISSIONS" ADD CONSTRAINT "FK570WUY6SACDNRW8WDQJFH7J0Q" FOREIGN KEY ("PERMISSIONS_ID")
      REFERENCES "PERMISSIONS" ("ID") ENABLE;
  ALTER TABLE "ROLES_PERMISSIONS" ADD CONSTRAINT "FKB9GQC5KVLA3IJOVNIHSBB816E" FOREIGN KEY ("ROLES_ID")
      REFERENCES "ROLES" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table ROLES_USERS
--------------------------------------------------------

  ALTER TABLE "ROLES_USERS" ADD CONSTRAINT "FKJDAU0SN88GJ3B7OIYM39QAYMK" FOREIGN KEY ("SUBJECTS_ID")
      REFERENCES "USERS" ("ID") ENABLE;
  ALTER TABLE "ROLES_USERS" ADD CONSTRAINT "FKRXA1KWVAC3VQ2P3A4AUS28M3P" FOREIGN KEY ("ROLE_ID")
      REFERENCES "ROLES" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SUPERVISOR_PERMISSIONS
--------------------------------------------------------

  ALTER TABLE "SUPERVISOR_PERMISSIONS" ADD CONSTRAINT "FKF7MXSACK9D04S94A2AHA7JYR9" FOREIGN KEY ("USER_ID")
      REFERENCES "USERS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SUPERVISOR_PERMISSIONS_PERMISSIONS
--------------------------------------------------------

  ALTER TABLE "SUPERVISOR_PERMISSIONS_PERMISSIONS" ADD CONSTRAINT "FKTKCTB5N3GGMU727F5MWLMUTK3" FOREIGN KEY ("PERMISSIONS_ID")
      REFERENCES "PERMISSIONS" ("ID") ENABLE;
  ALTER TABLE "SUPERVISOR_PERMISSIONS_PERMISSIONS" ADD CONSTRAINT "FK6B5T61SOYNLXLYRMB8Y59Y6TF" FOREIGN KEY ("SUPERVISORPERMISSION_ID")
      REFERENCES "SUPERVISOR_PERMISSIONS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table SUPERVISOR_PERMISSIONS_USERS
--------------------------------------------------------

  ALTER TABLE "SUPERVISOR_PERMISSIONS_USERS" ADD CONSTRAINT "FKL8HK9CJ6MAVQ8OQVUN76XL3AG" FOREIGN KEY ("SUBJECTS_ID")
      REFERENCES "USERS" ("ID") ENABLE;
  ALTER TABLE "SUPERVISOR_PERMISSIONS_USERS" ADD CONSTRAINT "FK16N73Q253VU1UNEMUPOBM3EKJ" FOREIGN KEY ("SUPERVISORPERMISSION_ID")
      REFERENCES "SUPERVISOR_PERMISSIONS" ("ID") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table USER_ATTRIBUTES
--------------------------------------------------------

  ALTER TABLE "USER_ATTRIBUTES" ADD CONSTRAINT "FKSKW1X6G2KT3G0I9507K4A4TQW" FOREIGN KEY ("USER_ID")
      REFERENCES "USERS" ("ID") ENABLE;
