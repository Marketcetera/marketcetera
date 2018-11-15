SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: exec_reports; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.exec_reports (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    account character varying(255),
    avg_price numeric(17,7) NOT NULL,
    cum_qty numeric(17,7) NOT NULL,
    eff_cum_qty numeric(17,7) NOT NULL,
    exec_type integer NOT NULL,
    expiry character varying(255),
    last_price numeric(17,7),
    last_qty numeric(17,7),
    option_type integer,
    order_id character varying(255) NOT NULL,
    ord_status integer NOT NULL,
    orig_order_id character varying(255),
    root_order_id character varying(255) NOT NULL,
    security_type integer NOT NULL,
    send_time timestamp without time zone NOT NULL,
    side integer NOT NULL,
    strike_price numeric(17,7),
    symbol character varying(255) NOT NULL,
    actor_id bigint,
    report_id bigint NOT NULL,
    viewer_id bigint
);


--
-- Name: fix_messages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.fix_messages (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    message text NOT NULL
);


--
-- Name: fix_session_attr_dscrptrs; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.fix_session_attr_dscrptrs (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    advice character varying(255),
    default_value character varying(255),
    description character varying(1024),
    name character varying(255) NOT NULL,
    pattern character varying(255),
    required boolean NOT NULL
);


--
-- Name: fix_session_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.fix_session_attributes (
    fix_session_id bigint NOT NULL,
    value character varying(255),
    name character varying(255) NOT NULL
);


--
-- Name: fix_sessions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.fix_sessions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    affinity integer NOT NULL,
    broker_id character varying(255) NOT NULL,
    description character varying(255),
    host character varying(255) NOT NULL,
    acceptor boolean NOT NULL,
    deleted boolean NOT NULL,
    enabled boolean NOT NULL,
    name character varying(255) NOT NULL,
    port integer NOT NULL,
    session_id character varying(255) NOT NULL
);


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE public.hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: id_repository; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.id_repository (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    next_id bigint
);


--
-- Name: incoming_fix_messages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.incoming_fix_messages (
    id bigint NOT NULL,
    clordid character varying(255),
    execid character varying(255),
    message character varying(4000) NOT NULL,
    msg_seq_num integer NOT NULL,
    msg_type character varying(255) NOT NULL,
    sending_time timestamp without time zone NOT NULL,
    fix_session character varying(255) NOT NULL
);


--
-- Name: message_store_messages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.message_store_messages (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    session_id character varying(255) NOT NULL,
    message character varying(8192) NOT NULL,
    msg_seq_num integer NOT NULL
);


--
-- Name: message_store_sessions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.message_store_sessions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    session_id character varying(255) NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    sender_seq_num integer NOT NULL,
    target_seq_num integer NOT NULL
);


--
-- Name: order_status; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.order_status (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    account character varying(255),
    avg_px numeric(17,7) NOT NULL,
    broker_id character varying(255),
    cum_qty numeric(17,7) NOT NULL,
    expiry character varying(255),
    last_px numeric(17,7) NOT NULL,
    last_qty numeric(17,7) NOT NULL,
    leaves_qty numeric(17,7) NOT NULL,
    option_type integer,
    order_id character varying(255) NOT NULL,
    order_px numeric(17,7),
    order_qty numeric(17,7) NOT NULL,
    ord_status character varying(255) NOT NULL,
    root_order_id character varying(255) NOT NULL,
    security_type integer NOT NULL,
    sending_time timestamp without time zone NOT NULL,
    side integer NOT NULL,
    strike_price numeric(17,7),
    symbol character varying(255) NOT NULL,
    execution_time timestamp without time zone,
    actor_id bigint,
    report_id bigint NOT NULL,
    viewer_id bigint
);


--
-- Name: outgoing_messages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.outgoing_messages (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    broker_id character varying(255) NOT NULL,
    message_type character varying(255) NOT NULL,
    msg_seq_num integer,
    order_id character varying(255),
    sender_comp_id character varying(255) NOT NULL,
    session_id character varying(255) NOT NULL,
    target_comp_id character varying(255) NOT NULL,
    actor_id bigint NOT NULL,
    fix_message_id bigint NOT NULL
);


--
-- Name: permissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.permissions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL
);


--
-- Name: reports; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.reports (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    broker_id character varying(255),
    hierarchy integer,
    originator integer,
    report_type integer NOT NULL,
    msg_seq_num integer NOT NULL,
    order_id character varying(255) NOT NULL,
    report_id bigint NOT NULL,
    send_time timestamp without time zone NOT NULL,
    session_id character varying(255) NOT NULL,
    actor_id bigint,
    fix_message_id bigint NOT NULL,
    viewer_id bigint
);


--
-- Name: roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.roles (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL
);


--
-- Name: roles_permissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.roles_permissions (
    roles_id bigint NOT NULL,
    permissions_id bigint NOT NULL
);


--
-- Name: roles_users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.roles_users (
    role_id bigint NOT NULL,
    subjects_id bigint NOT NULL
);


--
-- Name: supervisor_permissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.supervisor_permissions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    user_id bigint NOT NULL
);


--
-- Name: supervisor_permissions_permissions; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.supervisor_permissions_permissions (
    supervisorpermission_id bigint NOT NULL,
    permissions_id bigint NOT NULL
);


--
-- Name: supervisor_permissions_users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.supervisor_permissions_users (
    supervisorpermission_id bigint NOT NULL,
    subjects_id bigint NOT NULL
);


--
-- Name: user_attributes; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.user_attributes (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    attribute text NOT NULL,
    user_attribute_type integer NOT NULL,
    user_id bigint
);


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE IF NOT EXISTS public.users (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    is_active boolean NOT NULL,
    password character varying(255) NOT NULL,
    is_superuser boolean NOT NULL,
    user_data text
);


--
-- Data for Name: exec_reports; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: fix_messages; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: fix_session_attr_dscrptrs; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (46, '2018-11-13 08:00:22.901', 0, NULL, '', '(Optional) Your subID as associated with this FIX session', 'SenderSubID', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (47, '2018-11-13 08:00:22.906', 0, NULL, '', '(Optional) Your locationID as associated with this FIX session', 'SenderLocationID', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (48, '2018-11-13 08:00:22.909', 0, NULL, '', '(Optional) counterparty''s subID as associated with this FIX session', 'TargetSubID', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (49, '2018-11-13 08:00:22.914', 0, NULL, '', '(Optional) counterparty''s locationID as associated with this FIX session', 'TargetLocationID', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (50, '2018-11-13 08:00:22.919', 0, NULL, '', 'Additional qualifier to disambiguate otherwise identical sessions. This can only be used with initiator sessions.Note: See Special notes for Oracle.', 'SessionQualifier', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (51, '2018-11-13 08:00:22.922', 0, NULL, '', 'Required only for FIXT 1.1 (and newer). Ignored for earlier transport versions. Specifies the default application version ID for the session. This can either be the ApplVerID enum (see the ApplVerID field) the beginString for the default version.', 'DefaultApplVerID', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (52, '2018-11-13 08:00:22.924', 0, NULL, 'Y', 'Determines if milliseconds should be added to timestamps. Only available for FIX.4.2 and greater.', 'MillisecondsInTimeStamp', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (53, '2018-11-13 08:00:22.927', 0, NULL, 'N', 'Use actual end of sequence gap for resend requests rather than using ''''infinity'''' as the end sequence of the gap. Not recommended by the FIX specification, but needed for some counterparties.', 'ClosedResendInterval', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (54, '2018-11-13 08:00:22.931', 0, NULL, 'Y', 'Tell session whether or not to expect a data dictionary. You should always use a DataDictionary if you are using repeating groups.', 'UseDataDictionary', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (55, '2018-11-13 08:00:22.933', 0, NULL, 'FIX42.xml', 'XML definition file for validating incoming FIX messages. If no DataDictionary is supplied, only basic message validation will be done. This setting should only be used with FIX transport versions old than FIXT 1.1. See TransportDataDictionary and ApplicationDataDictionary for FIXT 1.1 settings.', 'DataDictionary', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (56, '2018-11-13 08:00:22.936', 0, NULL, '', 'XML definition file for validating admin (transport) messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information.', 'TransportDataDictionary', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (57, '2018-11-13 08:00:22.938', 0, NULL, '', 'XML definition file for validating application messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information. This setting supports the possibility of a custom application data dictionary for each session. This setting would only be used with FIXT 1.1 and new transport protocols. This setting can be used as a prefix to specify multiple application dictionaries for the FIXT transport. For example: DefaultApplVerID=FIX.4.2 # For default application version ID AppDataDictionary=FIX42.xml # For nondefault application version ID # Use beginString suffix for app version AppDataDictionary.FIX.4.4=FIX44.xml This would use FIX42.xml for the default application version ID and FIX44.xml for any FIX 4.4 messages.', 'AppDataDictionary', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (58, '2018-11-13 08:00:22.941', 0, NULL, 'Y', 'If set to N, fields that are out of order (i.e. body fields in the header, or header fields in the body) will not be rejected. Useful for connecting to systems which do not properly order fields.', 'ValidateFieldsOutOfOrder', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (59, '2018-11-13 08:00:22.943', 0, NULL, 'Y', 'If set to N, fields without values (empty) will not be rejected. Useful for connecting to systems which improperly send empty tags.', 'ValidateFieldsHaveValues', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (60, '2018-11-13 08:00:22.946', 0, NULL, 'Y', 'If set to N, user defined fields will not be rejected if they are not defined in the data dictionary, or are present in messages they do not belong to.', 'ValidateUserDefinedFields', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (61, '2018-11-13 08:00:22.949', 0, NULL, 'Y', 'Session validation setting for enabling whether field ordering is * validated. Values are ''''Y'''' or ''''N''''. Default is ''''Y''''.', 'ValidateUnorderedGroupFields', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (62, '2018-11-13 08:00:22.951', 0, NULL, 'Y', 'Allow to bypass the message validation (against the dictionary). Default is ''''Y''''.', 'ValidateIncomingMessage', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (63, '2018-11-13 08:00:22.953', 0, NULL, 'Y', 'Check the next expected target SeqNum against the received SeqNum. Default is ''''Y''''. If enabled and a mismatch is detected, apply the following logic:if lower than expected SeqNum , logout if higher, send a resend request If not enabled and a mismatch is detected, nothing is done. Must be enabled for EnableNextExpectedMsgSeqNum to work.', 'ValidateSequenceNumbers', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (64, '2018-11-13 08:00:22.955', 0, NULL, 'N', 'Allow unknown fields in messages. This is intended for unknown fields with tags lt 5000 (not user defined fields). Use ValidateUserDefinedFields for controlling validation of tags ge 5000.', 'AllowUnknownMsgFields', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (65, '2018-11-13 08:00:22.958', 0, NULL, 'Y', 'If set to Y, messages must be received from the counterparty with the correct SenderCompID and TargetCompID. Some systems will send you different CompIDs by design, so you must set this to N.', 'CheckCompID', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (66, '2018-11-13 08:00:22.96', 0, NULL, 'Y', 'If set to Y, messages must be received from the counterparty within a defined number of seconds (see MaxLatency). It is useful to turn this off if a system uses localtime for its timestamps instead of GMT.', 'CheckLatency', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (67, '2018-11-13 08:00:22.962', 0, NULL, '120', 'If CheckLatency is set to Y, this defines the number of seconds latency allowed for a message to be processed.', 'MaxLatency', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (68, '2018-11-13 08:00:22.964', 0, NULL, 'Y', 'If RejectInvalidMessage is set to N, only a warning will be logged on reception of message that fails data dictionary validation.', 'RejectInvalidMessage', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (69, '2018-11-13 08:00:22.967', 0, NULL, 'N', 'If this configuration is enabled, an uncaught Exception or Error in the application''s message processing will lead to a (BusinessMessage)Reject being sent to the counterparty and the incoming message sequence number will be incremented. If disabled (default), the problematic incoming message is discarded and the message sequence number is not incremented. Processing of the next valid message will cause detection of a sequence gap and a ResendRequest will be generated.', 'RejectMessageOnUnhandledException', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (70, '2018-11-13 08:00:22.969', 0, NULL, 'Y', 'If RequiresOrigSendingTime is set to N, PossDup messages lacking that field will not be rejected.', 'RequiresOrigSendingTime', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (71, '2018-11-13 08:00:22.971', 0, NULL, '30', 'Time between reconnection attempts in seconds. Only used for initiators', 'ReconnectInterval', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (72, '2018-11-13 08:00:22.974', 0, NULL, '30', 'Heartbeat interval in seconds. Only used for initiators.', 'HeartBtInt', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (73, '2018-11-13 08:00:22.976', 0, NULL, '10', 'Number of seconds to wait for a logon response before disconnecting.', 'LogonTimeout', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (74, '2018-11-13 08:00:22.978', 0, NULL, '2', 'Number of seconds to wait for a logout response before disconnecting.', 'LogoutTimeout', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (75, '2018-11-13 08:00:22.98', 0, NULL, 'TCP', 'Specifies the initiator communication protocol. The SocketConnectHost is not used with the VM_PIPE protocol, but the SocketConnectPort is significant and must match the acceptor configuration.', 'SocketConnectProtocol', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (76, '2018-11-13 08:00:22.983', 0, NULL, '', 'Bind the local socket to this port. Only used with a SocketInitiator. If unset the socket will be bound to a free port from the ephemeral port range.', 'SocketLocalPort', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (77, '2018-11-13 08:00:22.986', 0, NULL, '', 'Bind the local socket to this host. Only used with a SocketAcceptor. If unset the socket will be bound to all local interfaces.', 'SocketLocalHost', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (78, '2018-11-13 08:00:22.988', 0, NULL, 'TCP', 'Specifies the acceptor communication protocol. The SocketAcceptAddress is not used with the VM_PIPE protocol, but the SocketAcceptPort is significant and must match the initiator configuration.', 'SocketAcceptProtocol', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (79, '2018-11-13 08:00:22.991', 0, 'Enter ''Y'' or ''N''', 'Y', 'Refresh the session state when a logon is received. This allows a simple form of failover when the message store data is persistent. The option will be ignored for message stores that are not persistent (e.g., MemoryStore).', 'RefreshOnLogon', '^(Y|N){1}$', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (80, '2018-11-13 08:00:22.993', 0, NULL, 'N', 'Enables SSL usage for QFJ acceptor or initiator.', 'SocketUseSSL', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (81, '2018-11-13 08:00:22.996', 0, NULL, '', 'KeyStore to use with SSL', 'SocketKeyStore', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (82, '2018-11-13 08:00:22.999', 0, NULL, '', 'KeyStore password', 'SocketKeyStorePassword', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (83, '2018-11-13 08:00:23.002', 0, NULL, '', 'When the keepalive option is set for a TCP socket and no data has been exchanged across the socket in either direction for 2 hours (NOTE: the actual value is implementation dependent), TCP automatically sends a keepalive probe to the peer. This probe is a TCP segment to which the peer must respond. One of three responses is expected: The peer responds with the expected ACK. The application is not notified (since everything is OK). TCP will send another probe following another 2 hours of inactivity. The peer responds with an RST, which tells the local TCP that the peer host has crashed and rebooted. The socket is closed. There is no response from the peer. The socket is closed. The purpose of this option is to detect if the peer host crashes.', 'SocketKeepAlive', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (84, '2018-11-13 08:00:23.004', 0, NULL, '', 'When the OOBINLINE option is set, any TCP urgent data received on the socket will be received through the socket input stream. When the option is disabled (which is the default) urgent data is silently discarded.', 'SocketOobInline', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (85, '2018-11-13 08:00:23.007', 0, NULL, '', 'Set a hint the size of the underlying buffers used by the platform for incoming network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be received over the socket.', 'SocketReceiveBufferSize', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (86, '2018-11-13 08:00:23.009', 0, NULL, '', 'Sets SO_REUSEADDR for a socket. This is used only for MulticastSockets in java, and it is set by default for MulticastSockets.', 'SocketReuseAddress', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (87, '2018-11-13 08:00:23.011', 0, NULL, '', 'Set a hint the size of the underlying buffers used by the platform for outgoing network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be sent over the socket.', 'SocketSendBufferSize', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (88, '2018-11-13 08:00:23.014', 0, NULL, '', 'Specify a linger-on-close timeout. This option disables/enables immediate return from a close() of a TCP Socket. Enabling this option with a non-zero Integer timeout means that a close() will block pending the transmission and acknowledgement of all data written to the peer, at which point the socket is closed gracefully. Upon reaching the linger timeout, the socket is closed forcefully, with a TCP RST. Enabling the option with a timeout of zero does a forceful close immediately. If the specified timeout value exceeds 65,535 it will be reduced to 65,535.', 'SocketLinger', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (89, '2018-11-13 08:00:23.017', 0, NULL, 'Y', 'Disable Nagle''s algorithm for this connection. Written data to the network is not buffered pending acknowledgement of previously written data.', 'SocketTcpNoDelay', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (90, '2018-11-13 08:00:23.02', 0, NULL, '', 'Sets traffic class or type-of-service octet in the IP header for packets sent from this Socket. As the underlying network implementation may ignore this value applications should consider it a hint. The tc must be in the range 0 = tc = 255 or an IllegalArgumentException will be thrown. Notes: for Internet Protocol v4 the value consists of an octet with precedence and TOS fields as detailed in RFC 1349. The TOS field is bitset created by bitwise-or''ing values such the following :- IPTOS_LOWCOST (0x02) IPTOS_RELIABILITY (0x04) IPTOS_THROUGHPUT (0x08) IPTOS_LOWDELAY (0x10) The last low order bit is always ignored as this corresponds to the MBZ (must be zero) bit. Setting bits in the precedence field may result in a SocketException indicating that the operation is not permitted.', 'SocketTrafficClass', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (91, '2018-11-13 08:00:23.022', 0, NULL, 'N', 'Write messages synchronously. This is not generally recommended as it may result in performance degradation. The MINA communication layer is asynchronous by design, but this option will override that behavior if needed.', 'SocketSynchronousWrites', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (92, '2018-11-13 08:00:23.026', 0, NULL, '30000', 'The time in milliseconds to wait for a write to complete.', 'SocketSynchronousWriteTimeout', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (93, '2018-11-13 08:00:23.031', 0, NULL, 'Y', 'If set to N, no messages will be persisted. This will force QFJ to always send GapFills instead of resending messages. Use this if you know you never want to resend a message. Useful for market data streams.', 'PersistMessages', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (94, '2018-11-13 08:00:23.035', 0, NULL, 'N', 'Controls whether milliseconds are included in log time stamps.', 'FileIncludeMilliseconds', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (95, '2018-11-13 08:00:23.038', 0, NULL, 'N', 'Controls whether time stamps are included on message log entries.', 'FileIncludeTimestampForMessages', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (96, '2018-11-13 08:00:23.04', 0, NULL, 'quickfixj.event', 'Log category for logged events.', 'SLF4JLogEventCategory', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (97, '2018-11-13 08:00:23.042', 0, NULL, 'quickfixj.msg.incoming', 'Log category for incoming messages.', 'SLF4JLogIncomingMessageCategory', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (98, '2018-11-13 08:00:23.044', 0, NULL, 'quickfixj.msg.outgoing', 'Log category for outgoing messages.', 'SLF4JLogOutgoingMessageCategory', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (99, '2018-11-13 08:00:23.046', 0, NULL, 'Y', 'Controls whether session ID is prepended to log message.', 'SLF4JLogPrependSessionID', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (100, '2018-11-13 08:00:23.049', 0, NULL, 'N', 'Controls whether heartbeats are logged.', 'SLF4JLogHeartbeats', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (101, '2018-11-13 08:00:23.052', 0, NULL, 'Y', 'Log events to screen.', 'ScreenLogEvents', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (102, '2018-11-13 08:00:23.057', 0, NULL, 'Y', 'Log incoming messages to screen.', 'ScreenLogShowIncoming', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (103, '2018-11-13 08:00:23.06', 0, NULL, 'Y', 'Log outgoing messages to screen.', 'ScreenLogShowOutgoing', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (104, '2018-11-13 08:00:23.063', 0, NULL, 'N', 'Filter heartbeats from output (both incoming and outgoing)', 'ScreenLogShowHeartbeats', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (105, '2018-11-13 08:00:23.066', 0, NULL, 'N', 'Determines if sequence numbers should be reset before sending/receiving a logon request.', 'ResetOnLogon', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (106, '2018-11-13 08:00:23.07', 0, NULL, 'N', 'Determines if sequence numbers should be reset to 1 after a normal logout termination.', 'ResetOnLogout', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (107, '2018-11-13 08:00:23.072', 0, NULL, 'N', 'Determines if sequence numbers should be reset to 1 after an abnormal termination.', 'ResetOnDisconnect', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (108, '2018-11-13 08:00:23.076', 0, NULL, 'N', 'Session setting for doing an automatic reset when an error occurs. A reset means disconnect, sequence numbers reset, store cleaned and reconnect, as for a daily reset.', 'ResetOnError', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (109, '2018-11-13 08:00:23.078', 0, NULL, 'N', 'Session setting for doing an automatic disconnect when an error occurs.', 'DisconnectOnError', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (110, '2018-11-13 08:00:23.081', 0, NULL, 'N', 'Add tag LastMsgSeqNumProcessed in the header (optional tag 369).', 'EnableLastMsgSeqNumProcessed', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (111, '2018-11-13 08:00:23.085', 0, NULL, 'N', 'Add tag NextExpectedMsgSeqNum (optional tag 789) on the sent Logon message and use value of tag 789 on received Logon message to synchronize session. This should not be enabled for FIX versions lt 4.4. Only works when ValidateSequenceNumbers is enabled.', 'EnableNextExpectedMsgSeqNum', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (112, '2018-11-13 08:00:23.089', 0, NULL, '0', 'Setting to limit the size of a resend request in case of missing messages. This is useful when the remote FIX engine does not allow to ask for more than n message for a ResendRequest. E.g. if the ResendRequestChunkSize is set to 5 and a gap of 7 messages is detected, a first resend request will be sent for 5 messages. When this gap has been filled, another resend request for 2 messages will be sent. If the ResendRequestChunkSize is set to 0, only one ResendRequest for all the missing messages will be sent.', 'ResendRequestChunkSize', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (113, '2018-11-13 08:00:23.092', 0, NULL, 'N', 'Continue initializing sessions if an error occurs.', 'ContinueInitializationOnError', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (114, '2018-11-13 08:00:23.094', 0, NULL, 'N', 'Allows sending of redundant resend requests.', 'SendRedundantResendRequests', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (115, '2018-11-13 08:00:23.096', 0, NULL, '0.5', 'Fraction of the heartbeat interval which defines the additional time to wait if a TestRequest sent after a missing heartbeat times out.', 'TestRequestDelayMultiplier', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (116, '2018-11-13 08:00:23.099', 0, NULL, 'N', 'Heartbeat detection is disabled. A disconnect due to a missing heartbeat will never occur.', 'DisableHeartBeatCheck', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (117, '2018-11-13 08:00:23.101', 0, NULL, 'N', 'Fill in heartbeats on resend when reading from message store fails.', 'ForceResendWhenCorruptedStore', '', false);
INSERT INTO public.fix_session_attr_dscrptrs (id, last_updated, update_count, advice, default_value, description, name, pattern, required) VALUES (118, '2018-11-13 08:00:23.103', 0, NULL, '', 'Name of the session modifiers to apply to this session', 'org.marketcetera.sessioncustomization', '', false);


--
-- Data for Name: fix_session_attributes; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'N', 'ResetOnDisconnect');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'FIXT.1.1', 'BeginString');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'colin-core-callisto', 'SenderCompID');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'initiator', 'ConnectionType');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, '22:45:00', 'EndTime');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, '30', 'ReconnectInterval');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'MRKTC-EXCH', 'TargetCompID');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, '00:00:00', 'StartTime');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, '9', 'DefaultApplVerID');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'Y', 'UseDataDictionary');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'FIXT11.xml', 'TransportDataDictionary');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'N', 'ResetOnError');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'US/Pacific', 'TimeZone');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'N', 'ResetOnLogout');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'Y', 'RefreshOnLogon');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'N', 'SLF4JLogHeartbeats');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'FIX50SP2.xml', 'AppDataDictionary');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, '30', 'HeartBtInt');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'Y', 'ResetOnLogon');
INSERT INTO public.fix_session_attributes (fix_session_id, value, name) VALUES (45, 'sessionCustomizationAlgoTagsOnly', 'org.marketcetera.sessioncustomization');


--
-- Data for Name: fix_sessions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.fix_sessions (id, last_updated, update_count, affinity, broker_id, description, host, acceptor, deleted, enabled, name, port, session_id) VALUES (45, '2018-11-13 08:00:22.838', 1, 1, 'exsim', NULL, 'exchange.marketcetera.com', false, false, true, 'MATP Exchange Simulator', 7001, 'FIXT.1.1:colin-core-callisto->MRKTC-EXCH');


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

INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (5, '2018-11-13 08:00:20.531', 0, 'Access to Add Session action', 'AddSessionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (6, '2018-11-13 08:00:20.542', 0, 'Access to Delete Session action', 'DeleteSessionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (7, '2018-11-13 08:00:20.552', 0, 'Access to disable session action', 'DisableSessionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (8, '2018-11-13 08:00:20.564', 0, 'Access to edit session action', 'EditSessionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (9, '2018-11-13 08:00:20.57', 0, 'Access to enable session action', 'EnableSessionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (10, '2018-11-13 08:00:20.577', 0, 'Access to update sequence numbers action', 'UpdateSequenceAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (11, '2018-11-13 08:00:20.59', 0, 'Access to start session action', 'StartSessionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (12, '2018-11-13 08:00:20.599', 0, 'Access to stop session action', 'StopSessionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (13, '2018-11-13 08:00:20.605', 0, 'Access to view session action', 'ViewSessionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (14, '2018-11-13 08:00:20.611', 0, 'Access to read instance data action', 'ReadInstanceDataAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (15, '2018-11-13 08:00:20.617', 0, 'Access to read FIX session attribute descriptors action', 'ReadFixSessionAttributeDescriptorsAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (16, '2018-11-13 08:00:20.624', 0, 'Access to create user action', 'CreateUserAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (17, '2018-11-13 08:00:20.63', 0, 'Access to read user action', 'ReadUserAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (18, '2018-11-13 08:00:20.636', 0, 'Access to update user action', 'UpdateUserAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (19, '2018-11-13 08:00:20.642', 0, 'Access to delete user action', 'DeleteUserAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (20, '2018-11-13 08:00:20.652', 0, 'Access to change user password action', 'ChangeUserPasswordAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (21, '2018-11-13 08:00:20.658', 0, 'Access to read user permissions action', 'ReadUserPermisionsAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (22, '2018-11-13 08:00:20.663', 0, 'Access to create permission action', 'CreatePermissionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (23, '2018-11-13 08:00:20.668', 0, 'Access to read permission action', 'ReadPermissionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (24, '2018-11-13 08:00:20.673', 0, 'Access to update permission action', 'UpdatePermissionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (25, '2018-11-13 08:00:20.678', 0, 'Access to delete permission action', 'DeletePermissionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (26, '2018-11-13 08:00:20.685', 0, 'Access to create role action', 'CreateRoleAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (27, '2018-11-13 08:00:20.691', 0, 'Access to read role action', 'ReadRoleAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (28, '2018-11-13 08:00:20.695', 0, 'Access to update role action', 'UpdateRoleAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (29, '2018-11-13 08:00:20.7', 0, 'Access to delete role action', 'DeleteRoleAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (30, '2018-11-13 08:00:20.704', 0, 'Access to view broker status action', 'ViewBrokerStatusAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (31, '2018-11-13 08:00:20.709', 0, 'Access to view open orders action', 'ViewOpenOrdersAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (32, '2018-11-13 08:00:20.713', 0, 'Access to view reports action', 'ViewReportAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (33, '2018-11-13 08:00:20.718', 0, 'Access to view positions action', 'ViewPositionAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (34, '2018-11-13 08:00:20.722', 0, 'Access to send new orders action', 'SendOrderAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (35, '2018-11-13 08:00:20.726', 0, 'Access to view user data action', 'ViewUserDataAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (36, '2018-11-13 08:00:20.731', 0, 'Access to write user data action', 'WriteUserDataAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (37, '2018-11-13 08:00:20.735', 0, 'Access to manually add new reports action', 'AddReportAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (38, '2018-11-13 08:00:20.74', 0, 'Access to manually delete reports action', 'DeleteReportAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (39, '2018-11-13 08:00:20.744', 0, 'Access to read a user attribute action', 'ReadUserAttributeAction');
INSERT INTO public.permissions (id, last_updated, update_count, description, name) VALUES (40, '2018-11-13 08:00:20.747', 0, 'Access to write a user attribute action', 'WriteUserAttributeAction');


--
-- Data for Name: reports; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.roles (id, last_updated, update_count, description, name) VALUES (41, '2018-11-13 08:00:20.827', 0, 'Admin role', 'Admin');
INSERT INTO public.roles (id, last_updated, update_count, description, name) VALUES (42, '2018-11-13 08:00:20.88', 0, 'Trader role', 'Trader');
INSERT INTO public.roles (id, last_updated, update_count, description, name) VALUES (43, '2018-11-13 08:00:20.911', 0, 'Trader Admin role', 'TraderAdmin');


--
-- Data for Name: roles_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 21);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 11);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 39);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 24);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 9);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 40);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 5);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 6);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 17);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 18);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 29);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 7);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 12);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 23);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 14);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 10);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 35);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 22);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 28);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 19);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 27);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 30);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 26);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 13);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 15);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 16);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 20);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 8);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 25);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (41, 36);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 30);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 36);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 39);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 32);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 31);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 40);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 33);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 34);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 37);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (42, 35);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 38);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 36);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 32);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 31);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 39);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 33);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 30);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 40);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 34);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 35);
INSERT INTO public.roles_permissions (roles_id, permissions_id) VALUES (43, 37);


--
-- Data for Name: roles_users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.roles_users (role_id, subjects_id) VALUES (41, 1);
INSERT INTO public.roles_users (role_id, subjects_id) VALUES (42, 3);
INSERT INTO public.roles_users (role_id, subjects_id) VALUES (43, 4);


--
-- Data for Name: supervisor_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.supervisor_permissions (id, last_updated, update_count, description, name, user_id) VALUES (44, '2018-11-13 08:00:20.934', 0, 'Trader supervisor role', 'TraderSupervisor', 4);


--
-- Data for Name: supervisor_permissions_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.supervisor_permissions_permissions (supervisorpermission_id, permissions_id) VALUES (44, 31);
INSERT INTO public.supervisor_permissions_permissions (supervisorpermission_id, permissions_id) VALUES (44, 33);
INSERT INTO public.supervisor_permissions_permissions (supervisorpermission_id, permissions_id) VALUES (44, 35);
INSERT INTO public.supervisor_permissions_permissions (supervisorpermission_id, permissions_id) VALUES (44, 32);
INSERT INTO public.supervisor_permissions_permissions (supervisorpermission_id, permissions_id) VALUES (44, 30);


--
-- Data for Name: supervisor_permissions_users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.supervisor_permissions_users (supervisorpermission_id, subjects_id) VALUES (44, 3);


--
-- Data for Name: user_attributes; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO public.users (id, last_updated, update_count, description, name, is_active, password, is_superuser, user_data) VALUES (1, '2018-11-13 08:00:12.67', 0, NULL, 'admin', true, '6anqbgybi82pveayzrkt3egjkwfwdg5', true, NULL);
INSERT INTO public.users (id, last_updated, update_count, description, name, is_active, password, is_superuser, user_data) VALUES (3, '2018-11-13 08:00:20.469', 0, 'Trader user', 'trader', true, '2zg91043ou3eki4ysbejwwgkci37e6j', false, NULL);
INSERT INTO public.users (id, last_updated, update_count, description, name, is_active, password, is_superuser, user_data) VALUES (4, '2018-11-13 08:00:20.515', 0, 'Trader Admin user', 'traderAdmin', true, '210ui1dyyf6voajrad4gmpt3vgvvm9o', false, NULL);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('public.hibernate_sequence', 124, true);


--
-- Name: exec_reports exec_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exec_reports
    ADD CONSTRAINT exec_reports_pkey PRIMARY KEY (id);


--
-- Name: fix_messages fix_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fix_messages
    ADD CONSTRAINT fix_messages_pkey PRIMARY KEY (id);


--
-- Name: fix_session_attr_dscrptrs fix_session_attr_dscrptrs_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fix_session_attr_dscrptrs
    ADD CONSTRAINT fix_session_attr_dscrptrs_pkey PRIMARY KEY (id);


--
-- Name: fix_session_attributes fix_session_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fix_session_attributes
    ADD CONSTRAINT fix_session_attributes_pkey PRIMARY KEY (fix_session_id, name);


--
-- Name: fix_sessions fix_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fix_sessions
    ADD CONSTRAINT fix_sessions_pkey PRIMARY KEY (id);


--
-- Name: id_repository id_repository_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.id_repository
    ADD CONSTRAINT id_repository_pkey PRIMARY KEY (id);


--
-- Name: incoming_fix_messages incoming_fix_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.incoming_fix_messages
    ADD CONSTRAINT incoming_fix_messages_pkey PRIMARY KEY (id);


--
-- Name: message_store_messages message_store_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message_store_messages
    ADD CONSTRAINT message_store_messages_pkey PRIMARY KEY (id);


--
-- Name: message_store_sessions message_store_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.message_store_sessions
    ADD CONSTRAINT message_store_sessions_pkey PRIMARY KEY (id);


--
-- Name: order_status order_status_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_status
    ADD CONSTRAINT order_status_pkey PRIMARY KEY (id);


--
-- Name: outgoing_messages outgoing_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.outgoing_messages
    ADD CONSTRAINT outgoing_messages_pkey PRIMARY KEY (id);


--
-- Name: permissions permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);


--
-- Name: reports reports_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (id);


--
-- Name: roles_permissions roles_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles_permissions
    ADD CONSTRAINT roles_permissions_pkey PRIMARY KEY (roles_id, permissions_id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: roles_users roles_users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles_users
    ADD CONSTRAINT roles_users_pkey PRIMARY KEY (role_id, subjects_id);


--
-- Name: supervisor_permissions_permissions supervisor_permissions_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisor_permissions_permissions
    ADD CONSTRAINT supervisor_permissions_permissions_pkey PRIMARY KEY (supervisorpermission_id, permissions_id);


--
-- Name: supervisor_permissions supervisor_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisor_permissions
    ADD CONSTRAINT supervisor_permissions_pkey PRIMARY KEY (id);


--
-- Name: supervisor_permissions_users supervisor_permissions_users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisor_permissions_users
    ADD CONSTRAINT supervisor_permissions_users_pkey PRIMARY KEY (supervisorpermission_id, subjects_id);


--
-- Name: users uk3g1j96g94xpk3lpxl2qbl985x; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT uk3g1j96g94xpk3lpxl2qbl985x UNIQUE (name);


--
-- Name: supervisor_permissions uk_4rd5towbshlb1v1hv6w00sf6b; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisor_permissions
    ADD CONSTRAINT uk_4rd5towbshlb1v1hv6w00sf6b UNIQUE (name);


--
-- Name: reports uk_aely7chrvtqwv4xfm76xuj5bh; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT uk_aely7chrvtqwv4xfm76xuj5bh UNIQUE (report_id);


--
-- Name: reports uk_ca90a4kkdycpon22ynli3d6oi; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT uk_ca90a4kkdycpon22ynli3d6oi UNIQUE (fix_message_id);


--
-- Name: fix_session_attr_dscrptrs uk_gb3x058kyh5s4fxw1fi67bysc; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fix_session_attr_dscrptrs
    ADD CONSTRAINT uk_gb3x058kyh5s4fxw1fi67bysc UNIQUE (name);


--
-- Name: order_status uk_h8v9n38cydusmk1d0yya6nd2d; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_status
    ADD CONSTRAINT uk_h8v9n38cydusmk1d0yya6nd2d UNIQUE (report_id);


--
-- Name: outgoing_messages uk_hxvc6vrtmw1swik69wxt0drlc; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.outgoing_messages
    ADD CONSTRAINT uk_hxvc6vrtmw1swik69wxt0drlc UNIQUE (fix_message_id);


--
-- Name: exec_reports uk_imdq099u0qa8ob9tt5ljm6f7u; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exec_reports
    ADD CONSTRAINT uk_imdq099u0qa8ob9tt5ljm6f7u UNIQUE (report_id);


--
-- Name: roles uk_ofx66keruapi6vyqpv6f2or37; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT uk_ofx66keruapi6vyqpv6f2or37 UNIQUE (name);


--
-- Name: permissions uk_pnvtwliis6p05pn6i3ndjrqt2; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.permissions
    ADD CONSTRAINT uk_pnvtwliis6p05pn6i3ndjrqt2 UNIQUE (name);


--
-- Name: user_attributes user_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_attributes
    ADD CONSTRAINT user_attributes_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: supervisor_permissions_users fk16n73q253vu1unemupobm3ekj; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisor_permissions_users
    ADD CONSTRAINT fk16n73q253vu1unemupobm3ekj FOREIGN KEY (supervisorpermission_id) REFERENCES public.supervisor_permissions(id);


--
-- Name: exec_reports fk1celhypj9vint37eobsn22s1b; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exec_reports
    ADD CONSTRAINT fk1celhypj9vint37eobsn22s1b FOREIGN KEY (viewer_id) REFERENCES public.users(id);


--
-- Name: fix_session_attributes fk3lrqyamu7790pie2ivjh8vfq5; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.fix_session_attributes
    ADD CONSTRAINT fk3lrqyamu7790pie2ivjh8vfq5 FOREIGN KEY (fix_session_id) REFERENCES public.fix_sessions(id);


--
-- Name: roles_permissions fk570wuy6sacdnrw8wdqjfh7j0q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles_permissions
    ADD CONSTRAINT fk570wuy6sacdnrw8wdqjfh7j0q FOREIGN KEY (permissions_id) REFERENCES public.permissions(id);


--
-- Name: supervisor_permissions_permissions fk6b5t61soynlxlyrmb8y59y6tf; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisor_permissions_permissions
    ADD CONSTRAINT fk6b5t61soynlxlyrmb8y59y6tf FOREIGN KEY (supervisorpermission_id) REFERENCES public.supervisor_permissions(id);


--
-- Name: outgoing_messages fk7aeswc52coxk8sspdt9ua5e15; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.outgoing_messages
    ADD CONSTRAINT fk7aeswc52coxk8sspdt9ua5e15 FOREIGN KEY (fix_message_id) REFERENCES public.fix_messages(id);


--
-- Name: reports fk98bmvk76e2gp10muheog0j1wa; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT fk98bmvk76e2gp10muheog0j1wa FOREIGN KEY (fix_message_id) REFERENCES public.fix_messages(id);


--
-- Name: roles_permissions fkb9gqc5kvla3ijovnihsbb816e; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles_permissions
    ADD CONSTRAINT fkb9gqc5kvla3ijovnihsbb816e FOREIGN KEY (roles_id) REFERENCES public.roles(id);


--
-- Name: supervisor_permissions fkf7mxsack9d04s94a2aha7jyr9; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisor_permissions
    ADD CONSTRAINT fkf7mxsack9d04s94a2aha7jyr9 FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: exec_reports fkfn47lj607ghtt1lfie8adevxc; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exec_reports
    ADD CONSTRAINT fkfn47lj607ghtt1lfie8adevxc FOREIGN KEY (actor_id) REFERENCES public.users(id);


--
-- Name: reports fkh0r6ppu75byn1y7y0uiteel8q; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT fkh0r6ppu75byn1y7y0uiteel8q FOREIGN KEY (actor_id) REFERENCES public.users(id);


--
-- Name: roles_users fkjdau0sn88gj3b7oiym39qaymk; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles_users
    ADD CONSTRAINT fkjdau0sn88gj3b7oiym39qaymk FOREIGN KEY (subjects_id) REFERENCES public.users(id);


--
-- Name: order_status fkjqtx22v71dod89tht0fywav7; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_status
    ADD CONSTRAINT fkjqtx22v71dod89tht0fywav7 FOREIGN KEY (report_id) REFERENCES public.reports(id);


--
-- Name: supervisor_permissions_users fkl8hk9cj6mavq8oqvun76xl3ag; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisor_permissions_users
    ADD CONSTRAINT fkl8hk9cj6mavq8oqvun76xl3ag FOREIGN KEY (subjects_id) REFERENCES public.users(id);


--
-- Name: order_status fklxkg9il8q8k9448o5nggibnbs; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_status
    ADD CONSTRAINT fklxkg9il8q8k9448o5nggibnbs FOREIGN KEY (actor_id) REFERENCES public.users(id);


--
-- Name: exec_reports fkn47ta2b6e9wih8b97oubxvd3s; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.exec_reports
    ADD CONSTRAINT fkn47ta2b6e9wih8b97oubxvd3s FOREIGN KEY (report_id) REFERENCES public.reports(id);


--
-- Name: outgoing_messages fkpkya7xsumlsbfm4b125k07ke8; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.outgoing_messages
    ADD CONSTRAINT fkpkya7xsumlsbfm4b125k07ke8 FOREIGN KEY (actor_id) REFERENCES public.users(id);


--
-- Name: roles_users fkrxa1kwvac3vq2p3a4aus28m3p; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.roles_users
    ADD CONSTRAINT fkrxa1kwvac3vq2p3a4aus28m3p FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: reports fksfc0wdpjferohmpylygff4urs; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.reports
    ADD CONSTRAINT fksfc0wdpjferohmpylygff4urs FOREIGN KEY (viewer_id) REFERENCES public.users(id);


--
-- Name: user_attributes fkskw1x6g2kt3g0i9507k4a4tqw; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.user_attributes
    ADD CONSTRAINT fkskw1x6g2kt3g0i9507k4a4tqw FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: order_status fkt8iysrml49hnmembw8pkv5g3n; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.order_status
    ADD CONSTRAINT fkt8iysrml49hnmembw8pkv5g3n FOREIGN KEY (viewer_id) REFERENCES public.users(id);


--
-- Name: supervisor_permissions_permissions fktkctb5n3ggmu727f5mwlmutk3; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY public.supervisor_permissions_permissions
    ADD CONSTRAINT fktkctb5n3ggmu727f5mwlmutk3 FOREIGN KEY (permissions_id) REFERENCES public.permissions(id);


--
-- PostgreSQL database dump complete
--

