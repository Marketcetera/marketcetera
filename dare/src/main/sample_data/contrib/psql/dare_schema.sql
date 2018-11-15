--
-- Name: exec_reports; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE exec_reports (
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


ALTER TABLE exec_reports OWNER TO metc;

--
-- Name: fix_messages; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE fix_messages (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    message text NOT NULL
);


ALTER TABLE fix_messages OWNER TO metc;

--
-- Name: fix_session_attr_dscrptrs; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE fix_session_attr_dscrptrs (
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


ALTER TABLE fix_session_attr_dscrptrs OWNER TO metc;

--
-- Name: fix_session_attributes; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE fix_session_attributes (
    fix_session_id bigint NOT NULL,
    value character varying(255),
    name character varying(255) NOT NULL
);


ALTER TABLE fix_session_attributes OWNER TO metc;

--
-- Name: fix_sessions; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE fix_sessions (
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


ALTER TABLE fix_sessions OWNER TO metc;

--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema: public; Owner: metc
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE hibernate_sequence OWNER TO metc;

--
-- Name: id_repository; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE id_repository (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    next_id bigint
);


ALTER TABLE id_repository OWNER TO metc;

--
-- Name: incoming_fix_messages; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE incoming_fix_messages (
    id bigint NOT NULL,
    clordid character varying(255),
    execid character varying(255),
    message character varying(4000) NOT NULL,
    msg_seq_num integer NOT NULL,
    msg_type character varying(255) NOT NULL,
    sending_time timestamp without time zone NOT NULL,
    fix_session character varying(255) NOT NULL
);


ALTER TABLE incoming_fix_messages OWNER TO metc;

--
-- Name: message_store_messages; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE message_store_messages (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    session_id character varying(255) NOT NULL,
    message character varying(8192) NOT NULL,
    msg_seq_num integer NOT NULL
);


ALTER TABLE message_store_messages OWNER TO metc;

--
-- Name: message_store_sessions; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE message_store_sessions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    session_id character varying(255) NOT NULL,
    creation_time timestamp without time zone NOT NULL,
    sender_seq_num integer NOT NULL,
    target_seq_num integer NOT NULL
);


ALTER TABLE message_store_sessions OWNER TO metc;

--
-- Name: order_status; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE order_status (
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


ALTER TABLE order_status OWNER TO metc;

--
-- Name: outgoing_messages; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE outgoing_messages (
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


ALTER TABLE outgoing_messages OWNER TO metc;

--
-- Name: permissions; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE permissions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL
);


ALTER TABLE permissions OWNER TO metc;

--
-- Name: reports; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE reports (
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


ALTER TABLE reports OWNER TO metc;

--
-- Name: roles; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE roles (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL
);


ALTER TABLE roles OWNER TO metc;

--
-- Name: roles_permissions; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE roles_permissions (
    roles_id bigint NOT NULL,
    permissions_id bigint NOT NULL
);


ALTER TABLE roles_permissions OWNER TO metc;

--
-- Name: roles_users; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE roles_users (
    roles_id bigint NOT NULL,
    subjects_id bigint NOT NULL
);


ALTER TABLE roles_users OWNER TO metc;

--
-- Name: supervisor_permissions; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE supervisor_permissions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    user_id bigint NOT NULL
);


ALTER TABLE supervisor_permissions OWNER TO metc;

--
-- Name: supervisor_permissions_permissions; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE supervisor_permissions_permissions (
    supervisor_permissions_id bigint NOT NULL,
    permissions_id bigint NOT NULL
);


ALTER TABLE supervisor_permissions_permissions OWNER TO metc;

--
-- Name: supervisor_permissions_users; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE supervisor_permissions_users (
    supervisor_permissions_id bigint NOT NULL,
    subjects_id bigint NOT NULL
);


ALTER TABLE supervisor_permissions_users OWNER TO metc;

--
-- Name: system_info; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE system_info (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    value character varying(255)
);


ALTER TABLE system_info OWNER TO metc;

--
-- Name: user_attributes; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE user_attributes (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    attribute text NOT NULL,
    user_attribute_type integer NOT NULL,
    user_id bigint
);


ALTER TABLE user_attributes OWNER TO metc;

--
-- Name: users; Type: TABLE; Schema: public; Owner: metc
--

CREATE TABLE users (
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


ALTER TABLE users OWNER TO metc;

--
-- Name: exec_reports_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT exec_reports_pkey PRIMARY KEY (id);


--
-- Name: fix_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY fix_messages
    ADD CONSTRAINT fix_messages_pkey PRIMARY KEY (id);


--
-- Name: fix_session_attr_dscrptrs_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY fix_session_attr_dscrptrs
    ADD CONSTRAINT fix_session_attr_dscrptrs_pkey PRIMARY KEY (id);


--
-- Name: fix_session_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY fix_session_attributes
    ADD CONSTRAINT fix_session_attributes_pkey PRIMARY KEY (fix_session_id, name);


--
-- Name: fix_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY fix_sessions
    ADD CONSTRAINT fix_sessions_pkey PRIMARY KEY (id);


--
-- Name: id_repository_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY id_repository
    ADD CONSTRAINT id_repository_pkey PRIMARY KEY (id);


--
-- Name: incoming_fix_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY incoming_fix_messages
    ADD CONSTRAINT incoming_fix_messages_pkey PRIMARY KEY (id);


--
-- Name: message_store_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY message_store_messages
    ADD CONSTRAINT message_store_messages_pkey PRIMARY KEY (id);


--
-- Name: message_store_sessions_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY message_store_sessions
    ADD CONSTRAINT message_store_sessions_pkey PRIMARY KEY (id);


--
-- Name: order_status_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT order_status_pkey PRIMARY KEY (id);


--
-- Name: outgoing_messages_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY outgoing_messages
    ADD CONSTRAINT outgoing_messages_pkey PRIMARY KEY (id);


--
-- Name: permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);


--
-- Name: reports_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (id);


--
-- Name: roles_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY roles_permissions
    ADD CONSTRAINT roles_permissions_pkey PRIMARY KEY (roles_id, permissions_id);


--
-- Name: roles_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: roles_users_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY roles_users
    ADD CONSTRAINT roles_users_pkey PRIMARY KEY (roles_id, subjects_id);


--
-- Name: supervisor_permissions_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY supervisor_permissions_permissions
    ADD CONSTRAINT supervisor_permissions_permissions_pkey PRIMARY KEY (supervisor_permissions_id, permissions_id);


--
-- Name: supervisor_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY supervisor_permissions
    ADD CONSTRAINT supervisor_permissions_pkey PRIMARY KEY (id);


--
-- Name: supervisor_permissions_users_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY supervisor_permissions_users
    ADD CONSTRAINT supervisor_permissions_users_pkey PRIMARY KEY (supervisor_permissions_id, subjects_id);


--
-- Name: system_info_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY system_info
    ADD CONSTRAINT system_info_pkey PRIMARY KEY (id);


--
-- Name: uk_3g1j96g94xpk3lpxl2qbl985x; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY users
    ADD CONSTRAINT uk_3g1j96g94xpk3lpxl2qbl985x UNIQUE (name);


--
-- Name: uk_457m1gi0j3jft2b5wq33iccxk; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY system_info
    ADD CONSTRAINT uk_457m1gi0j3jft2b5wq33iccxk UNIQUE (name);


--
-- Name: uk_4rd5towbshlb1v1hv6w00sf6b; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY supervisor_permissions
    ADD CONSTRAINT uk_4rd5towbshlb1v1hv6w00sf6b UNIQUE (name);


--
-- Name: uk_aely7chrvtqwv4xfm76xuj5bh; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT uk_aely7chrvtqwv4xfm76xuj5bh UNIQUE (report_id);


--
-- Name: uk_ca90a4kkdycpon22ynli3d6oi; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT uk_ca90a4kkdycpon22ynli3d6oi UNIQUE (fix_message_id);


--
-- Name: uk_gb3x058kyh5s4fxw1fi67bysc; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY fix_session_attr_dscrptrs
    ADD CONSTRAINT uk_gb3x058kyh5s4fxw1fi67bysc UNIQUE (name);


--
-- Name: uk_h8v9n38cydusmk1d0yya6nd2d; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT uk_h8v9n38cydusmk1d0yya6nd2d UNIQUE (report_id);


--
-- Name: uk_hxvc6vrtmw1swik69wxt0drlc; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY outgoing_messages
    ADD CONSTRAINT uk_hxvc6vrtmw1swik69wxt0drlc UNIQUE (fix_message_id);


--
-- Name: uk_imdq099u0qa8ob9tt5ljm6f7u; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT uk_imdq099u0qa8ob9tt5ljm6f7u UNIQUE (report_id);


--
-- Name: uk_ofx66keruapi6vyqpv6f2or37; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT uk_ofx66keruapi6vyqpv6f2or37 UNIQUE (name);


--
-- Name: uk_pnvtwliis6p05pn6i3ndjrqt2; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT uk_pnvtwliis6p05pn6i3ndjrqt2 UNIQUE (name);


--
-- Name: user_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY user_attributes
    ADD CONSTRAINT user_attributes_pkey PRIMARY KEY (id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: fk_11ryyv1uhwgx0sxbfqbqoaqhe; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY roles_users
    ADD CONSTRAINT fk_11ryyv1uhwgx0sxbfqbqoaqhe FOREIGN KEY (subjects_id) REFERENCES users(id);


--
-- Name: fk_1k9arjj9199v6opklugylv1t6; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT fk_1k9arjj9199v6opklugylv1t6 FOREIGN KEY (viewer_id) REFERENCES users(id);


--
-- Name: fk_3hqcr3muvdu6hf6y3vqy2arku; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT fk_3hqcr3muvdu6hf6y3vqy2arku FOREIGN KEY (actor_id) REFERENCES users(id);


--
-- Name: fk_49nn48rb06nt9dg8lgjx5yn1r; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT fk_49nn48rb06nt9dg8lgjx5yn1r FOREIGN KEY (viewer_id) REFERENCES users(id);


--
-- Name: fk_4nu5m103ocolophawmsdpf3me; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT fk_4nu5m103ocolophawmsdpf3me FOREIGN KEY (actor_id) REFERENCES users(id);


--
-- Name: fk_7dh3h2qr6vweciu8flfan8ofk; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY supervisor_permissions_permissions
    ADD CONSTRAINT fk_7dh3h2qr6vweciu8flfan8ofk FOREIGN KEY (supervisor_permissions_id) REFERENCES supervisor_permissions(id);


--
-- Name: fk_81y61ljbfom597wqpplp34aum; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY supervisor_permissions_users
    ADD CONSTRAINT fk_81y61ljbfom597wqpplp34aum FOREIGN KEY (subjects_id) REFERENCES users(id);


--
-- Name: fk_bo952s3qelvl845jfx8t5g9x; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY supervisor_permissions_permissions
    ADD CONSTRAINT fk_bo952s3qelvl845jfx8t5g9x FOREIGN KEY (permissions_id) REFERENCES permissions(id);


--
-- Name: fk_ca90a4kkdycpon22ynli3d6oi; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT fk_ca90a4kkdycpon22ynli3d6oi FOREIGN KEY (fix_message_id) REFERENCES fix_messages(id);


--
-- Name: fk_du6spiyxdaype4cpfp2s7e3n1; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY supervisor_permissions
    ADD CONSTRAINT fk_du6spiyxdaype4cpfp2s7e3n1 FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: fk_enmnqh22bc88jqj9vuag3xaqf; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY fix_session_attributes
    ADD CONSTRAINT fk_enmnqh22bc88jqj9vuag3xaqf FOREIGN KEY (fix_session_id) REFERENCES fix_sessions(id);


--
-- Name: fk_h8v9n38cydusmk1d0yya6nd2d; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT fk_h8v9n38cydusmk1d0yya6nd2d FOREIGN KEY (report_id) REFERENCES reports(id);


--
-- Name: fk_hxvc6vrtmw1swik69wxt0drlc; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY outgoing_messages
    ADD CONSTRAINT fk_hxvc6vrtmw1swik69wxt0drlc FOREIGN KEY (fix_message_id) REFERENCES fix_messages(id);


--
-- Name: fk_imdq099u0qa8ob9tt5ljm6f7u; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT fk_imdq099u0qa8ob9tt5ljm6f7u FOREIGN KEY (report_id) REFERENCES reports(id);


--
-- Name: fk_n55hu8kwnbjgoaqeq7r4mow81; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY roles_permissions
    ADD CONSTRAINT fk_n55hu8kwnbjgoaqeq7r4mow81 FOREIGN KEY (roles_id) REFERENCES roles(id);


--
-- Name: fk_obp6blf7vlhf1onpa596dwo8u; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY supervisor_permissions_users
    ADD CONSTRAINT fk_obp6blf7vlhf1onpa596dwo8u FOREIGN KEY (supervisor_permissions_id) REFERENCES supervisor_permissions(id);


--
-- Name: fk_oll9subcln0cdjt31bp72a3uv; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY roles_permissions
    ADD CONSTRAINT fk_oll9subcln0cdjt31bp72a3uv FOREIGN KEY (permissions_id) REFERENCES permissions(id);


--
-- Name: fk_qqm4ut1c66c8xunnin4oj1scg; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY roles_users
    ADD CONSTRAINT fk_qqm4ut1c66c8xunnin4oj1scg FOREIGN KEY (roles_id) REFERENCES roles(id);


--
-- Name: fk_qu1p51gbysswhig9dock4vtj; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT fk_qu1p51gbysswhig9dock4vtj FOREIGN KEY (viewer_id) REFERENCES users(id);


--
-- Name: fk_rs4oceq551s8u68psn6n6i8ix; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT fk_rs4oceq551s8u68psn6n6i8ix FOREIGN KEY (actor_id) REFERENCES users(id);


--
-- Name: fk_rsgpog8wog0v3yt6i3gbk9s08; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY outgoing_messages
    ADD CONSTRAINT fk_rsgpog8wog0v3yt6i3gbk9s08 FOREIGN KEY (actor_id) REFERENCES users(id);


--
-- Name: fk_rxjxddvp1lgfcuw74vq01d4j; Type: FK CONSTRAINT; Schema: public; Owner: metc
--

ALTER TABLE ONLY user_attributes
    ADD CONSTRAINT fk_rxjxddvp1lgfcuw74vq01d4j FOREIGN KEY (user_id) REFERENCES users(id);
--
-- Data for Name: fix_messages; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO users VALUES (1, '2017-03-22 05:46:17.934', 0, NULL, 'admin', true, '6anqbgybi82pveayzrkt3egjkwfwdg5', true, NULL);
INSERT INTO users VALUES (3, '2017-03-22 05:46:24.269', 0, 'Trader user', 'trader', true, '2zg91043ou3eki4ysbejwwgkci37e6j', false, NULL);
INSERT INTO users VALUES (4, '2017-03-22 05:46:24.292', 0, 'Trader Admin user', 'traderAdmin', true, '210ui1dyyf6voajrad4gmpt3vgvvm9o', false, NULL);


--
-- Data for Name: reports; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: exec_reports; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: fix_session_attr_dscrptrs; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO fix_session_attr_dscrptrs VALUES (45, '2017-03-22 05:46:26.363', 0, NULL, '', '(Optional) Your subID as associated with this FIX session', 'SenderSubID', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (46, '2017-03-22 05:46:26.369', 0, NULL, '', '(Optional) Your locationID as associated with this FIX session', 'SenderLocationID', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (47, '2017-03-22 05:46:26.373', 0, NULL, '', '(Optional) counterparty''s subID as associated with this FIX session', 'TargetSubID', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (48, '2017-03-22 05:46:26.379', 0, NULL, '', '(Optional) counterparty''s locationID as associated with this FIX session', 'TargetLocationID', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (49, '2017-03-22 05:46:26.383', 0, NULL, '', 'Additional qualifier to disambiguate otherwise identical sessions. This can only be used with initiator sessions.Note: See Special notes for Oracle.', 'SessionQualifier', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (50, '2017-03-22 05:46:26.388', 0, NULL, '', 'Required only for FIXT 1.1 (and newer). Ignored for earlier transport versions. Specifies the default application version ID for the session. This can either be the ApplVerID enum (see the ApplVerID field) the beginString for the default version.', 'DefaultApplVerID', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (51, '2017-03-22 05:46:26.392', 0, NULL, 'Y', 'Determines if milliseconds should be added to timestamps. Only available for FIX.4.2 and greater.', 'MillisecondsInTimeStamp', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (52, '2017-03-22 05:46:26.398', 0, NULL, 'N', 'Use actual end of sequence gap for resend requests rather than using ''''infinity'''' as the end sequence of the gap. Not recommended by the FIX specification, but needed for some counterparties.', 'ClosedResendInterval', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (53, '2017-03-22 05:46:26.402', 0, NULL, 'Y', 'Tell session whether or not to expect a data dictionary. You should always use a DataDictionary if you are using repeating groups.', 'UseDataDictionary', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (54, '2017-03-22 05:46:26.407', 0, NULL, 'FIX42.xml', 'XML definition file for validating incoming FIX messages. If no DataDictionary is supplied, only basic message validation will be done. This setting should only be used with FIX transport versions old than FIXT 1.1. See TransportDataDictionary and ApplicationDataDictionary for FIXT 1.1 settings.', 'DataDictionary', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (55, '2017-03-22 05:46:26.411', 0, NULL, '', 'XML definition file for validating admin (transport) messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information.', 'TransportDataDictionary', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (56, '2017-03-22 05:46:26.417', 0, NULL, '', 'XML definition file for validating application messages. This setting is only valid for the FIXT 1.1 (or newer) sessions. See DataDictionary for older transport versions (FIX 4.0-4.4) and for additional information. This setting supports the possibility of a custom application data dictionary for each session. This setting would only be used with FIXT 1.1 and new transport protocols. This setting can be used as a prefix to specify multiple application dictionaries for the FIXT transport. For example: DefaultApplVerID=FIX.4.2 # For default application version ID AppDataDictionary=FIX42.xml # For nondefault application version ID # Use beginString suffix for app version AppDataDictionary.FIX.4.4=FIX44.xml This would use FIX42.xml for the default application version ID and FIX44.xml for any FIX 4.4 messages.', 'AppDataDictionary', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (57, '2017-03-22 05:46:26.421', 0, NULL, 'Y', 'If set to N, fields that are out of order (i.e. body fields in the header, or header fields in the body) will not be rejected. Useful for connecting to systems which do not properly order fields.', 'ValidateFieldsOutOfOrder', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (58, '2017-03-22 05:46:26.427', 0, NULL, 'Y', 'If set to N, fields without values (empty) will not be rejected. Useful for connecting to systems which improperly send empty tags.', 'ValidateFieldsHaveValues', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (59, '2017-03-22 05:46:26.435', 0, NULL, 'Y', 'If set to N, user defined fields will not be rejected if they are not defined in the data dictionary, or are present in messages they do not belong to.', 'ValidateUserDefinedFields', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (60, '2017-03-22 05:46:26.441', 0, NULL, 'Y', 'Session validation setting for enabling whether field ordering is * validated. Values are ''''Y'''' or ''''N''''. Default is ''''Y''''.', 'ValidateUnorderedGroupFields', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (61, '2017-03-22 05:46:26.447', 0, NULL, 'Y', 'Allow to bypass the message validation (against the dictionary). Default is ''''Y''''.', 'ValidateIncomingMessage', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (62, '2017-03-22 05:46:26.451', 0, NULL, 'Y', 'Check the next expected target SeqNum against the received SeqNum. Default is ''''Y''''. If enabled and a mismatch is detected, apply the following logic:if lower than expected SeqNum , logout if higher, send a resend request If not enabled and a mismatch is detected, nothing is done. Must be enabled for EnableNextExpectedMsgSeqNum to work.', 'ValidateSequenceNumbers', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (63, '2017-03-22 05:46:26.457', 0, NULL, 'N', 'Allow unknown fields in messages. This is intended for unknown fields with tags lt 5000 (not user defined fields). Use ValidateUserDefinedFields for controlling validation of tags ge 5000.', 'AllowUnknownMsgFields', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (64, '2017-03-22 05:46:26.468', 0, NULL, 'Y', 'If set to Y, messages must be received from the counterparty with the correct SenderCompID and TargetCompID. Some systems will send you different CompIDs by design, so you must set this to N.', 'CheckCompID', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (65, '2017-03-22 05:46:26.474', 0, NULL, 'Y', 'If set to Y, messages must be received from the counterparty within a defined number of seconds (see MaxLatency). It is useful to turn this off if a system uses localtime for its timestamps instead of GMT.', 'CheckLatency', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (66, '2017-03-22 05:46:26.478', 0, NULL, '120', 'If CheckLatency is set to Y, this defines the number of seconds latency allowed for a message to be processed.', 'MaxLatency', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (67, '2017-03-22 05:46:26.484', 0, NULL, 'Y', 'If RejectInvalidMessage is set to N, only a warning will be logged on reception of message that fails data dictionary validation.', 'RejectInvalidMessage', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (68, '2017-03-22 05:46:26.489', 0, NULL, 'N', 'If this configuration is enabled, an uncaught Exception or Error in the application''s message processing will lead to a (BusinessMessage)Reject being sent to the counterparty and the incoming message sequence number will be incremented. If disabled (default), the problematic incoming message is discarded and the message sequence number is not incremented. Processing of the next valid message will cause detection of a sequence gap and a ResendRequest will be generated.', 'RejectMessageOnUnhandledException', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (69, '2017-03-22 05:46:26.495', 0, NULL, 'Y', 'If RequiresOrigSendingTime is set to N, PossDup messages lacking that field will not be rejected.', 'RequiresOrigSendingTime', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (70, '2017-03-22 05:46:26.5', 0, NULL, '30', 'Time between reconnection attempts in seconds. Only used for initiators', 'ReconnectInterval', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (71, '2017-03-22 05:46:26.506', 0, NULL, '30', 'Heartbeat interval in seconds. Only used for initiators.', 'HeartBtInt', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (72, '2017-03-22 05:46:26.511', 0, NULL, '10', 'Number of seconds to wait for a logon response before disconnecting.', 'LogonTimeout', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (73, '2017-03-22 05:46:26.517', 0, NULL, '2', 'Number of seconds to wait for a logout response before disconnecting.', 'LogoutTimeout', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (74, '2017-03-22 05:46:26.522', 0, NULL, 'TCP', 'Specifies the initiator communication protocol. The SocketConnectHost is not used with the VM_PIPE protocol, but the SocketConnectPort is significant and must match the acceptor configuration.', 'SocketConnectProtocol', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (75, '2017-03-22 05:46:26.527', 0, NULL, '', 'Bind the local socket to this port. Only used with a SocketInitiator. If unset the socket will be bound to a free port from the ephemeral port range.', 'SocketLocalPort', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (76, '2017-03-22 05:46:26.532', 0, NULL, '', 'Bind the local socket to this host. Only used with a SocketAcceptor. If unset the socket will be bound to all local interfaces.', 'SocketLocalHost', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (77, '2017-03-22 05:46:26.538', 0, NULL, 'TCP', 'Specifies the acceptor communication protocol. The SocketAcceptAddress is not used with the VM_PIPE protocol, but the SocketAcceptPort is significant and must match the initiator configuration.', 'SocketAcceptProtocol', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (78, '2017-03-22 05:46:26.542', 0, 'Enter ''Y'' or ''N''', 'Y', 'Refresh the session state when a logon is received. This allows a simple form of failover when the message store data is persistent. The option will be ignored for message stores that are not persistent (e.g., MemoryStore).', 'RefreshOnLogon', '^(Y|N){1}$', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (79, '2017-03-22 05:46:26.548', 0, NULL, 'N', 'Enables SSL usage for QFJ acceptor or initiator.', 'SocketUseSSL', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (80, '2017-03-22 05:46:26.553', 0, NULL, '', 'KeyStore to use with SSL', 'SocketKeyStore', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (81, '2017-03-22 05:46:26.558', 0, NULL, '', 'KeyStore password', 'SocketKeyStorePassword', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (82, '2017-03-22 05:46:26.563', 0, NULL, '', 'When the keepalive option is set for a TCP socket and no data has been exchanged across the socket in either direction for 2 hours (NOTE: the actual value is implementation dependent), TCP automatically sends a keepalive probe to the peer. This probe is a TCP segment to which the peer must respond. One of three responses is expected: The peer responds with the expected ACK. The application is not notified (since everything is OK). TCP will send another probe following another 2 hours of inactivity. The peer responds with an RST, which tells the local TCP that the peer host has crashed and rebooted. The socket is closed. There is no response from the peer. The socket is closed. The purpose of this option is to detect if the peer host crashes.', 'SocketKeepAlive', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (83, '2017-03-22 05:46:26.569', 0, NULL, '', 'When the OOBINLINE option is set, any TCP urgent data received on the socket will be received through the socket input stream. When the option is disabled (which is the default) urgent data is silently discarded.', 'SocketOobInline', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (84, '2017-03-22 05:46:26.574', 0, NULL, '', 'Set a hint the size of the underlying buffers used by the platform for incoming network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be received over the socket.', 'SocketReceiveBufferSize', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (85, '2017-03-22 05:46:26.579', 0, NULL, '', 'Sets SO_REUSEADDR for a socket. This is used only for MulticastSockets in java, and it is set by default for MulticastSockets.', 'SocketReuseAddress', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (86, '2017-03-22 05:46:26.584', 0, NULL, '', 'Set a hint the size of the underlying buffers used by the platform for outgoing network I/O. When used in set, this is a suggestion to the kernel from the application about the size of buffers to use for the data to be sent over the socket.', 'SocketSendBufferSize', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (87, '2017-03-22 05:46:26.589', 0, NULL, '', 'Specify a linger-on-close timeout. This option disables/enables immediate return from a close() of a TCP Socket. Enabling this option with a non-zero Integer timeout means that a close() will block pending the transmission and acknowledgement of all data written to the peer, at which point the socket is closed gracefully. Upon reaching the linger timeout, the socket is closed forcefully, with a TCP RST. Enabling the option with a timeout of zero does a forceful close immediately. If the specified timeout value exceeds 65,535 it will be reduced to 65,535.', 'SocketLinger', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (88, '2017-03-22 05:46:26.594', 0, NULL, 'Y', 'Disable Nagle''s algorithm for this connection. Written data to the network is not buffered pending acknowledgement of previously written data.', 'SocketTcpNoDelay', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (89, '2017-03-22 05:46:26.602', 0, NULL, '', 'Sets traffic class or type-of-service octet in the IP header for packets sent from this Socket. As the underlying network implementation may ignore this value applications should consider it a hint. The tc must be in the range 0 = tc = 255 or an IllegalArgumentException will be thrown. Notes: for Internet Protocol v4 the value consists of an octet with precedence and TOS fields as detailed in RFC 1349. The TOS field is bitset created by bitwise-or''ing values such the following :- IPTOS_LOWCOST (0x02) IPTOS_RELIABILITY (0x04) IPTOS_THROUGHPUT (0x08) IPTOS_LOWDELAY (0x10) The last low order bit is always ignored as this corresponds to the MBZ (must be zero) bit. Setting bits in the precedence field may result in a SocketException indicating that the operation is not permitted.', 'SocketTrafficClass', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (90, '2017-03-22 05:46:26.606', 0, NULL, 'N', 'Write messages synchronously. This is not generally recommended as it may result in performance degradation. The MINA communication layer is asynchronous by design, but this option will override that behavior if needed.', 'SocketSynchronousWrites', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (91, '2017-03-22 05:46:26.611', 0, NULL, '30000', 'The time in milliseconds to wait for a write to complete.', 'SocketSynchronousWriteTimeout', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (92, '2017-03-22 05:46:26.616', 0, NULL, 'Y', 'If set to N, no messages will be persisted. This will force QFJ to always send GapFills instead of resending messages. Use this if you know you never want to resend a message. Useful for market data streams.', 'PersistMessages', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (93, '2017-03-22 05:46:26.621', 0, NULL, 'N', 'Controls whether milliseconds are included in log time stamps.', 'FileIncludeMilliseconds', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (94, '2017-03-22 05:46:26.625', 0, NULL, 'N', 'Controls whether time stamps are included on message log entries.', 'FileIncludeTimestampForMessages', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (95, '2017-03-22 05:46:26.631', 0, NULL, 'quickfixj.event', 'Log category for logged events.', 'SLF4JLogEventCategory', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (96, '2017-03-22 05:46:26.635', 0, NULL, 'quickfixj.msg.incoming', 'Log category for incoming messages.', 'SLF4JLogIncomingMessageCategory', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (97, '2017-03-22 05:46:26.64', 0, NULL, 'quickfixj.msg.outgoing', 'Log category for outgoing messages.', 'SLF4JLogOutgoingMessageCategory', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (98, '2017-03-22 05:46:26.645', 0, NULL, 'Y', 'Controls whether session ID is prepended to log message.', 'SLF4JLogPrependSessionID', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (99, '2017-03-22 05:46:26.65', 0, NULL, 'N', 'Controls whether heartbeats are logged.', 'SLF4JLogHeartbeats', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (100, '2017-03-22 05:46:26.654', 0, NULL, 'Y', 'Log events to screen.', 'ScreenLogEvents', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (101, '2017-03-22 05:46:26.659', 0, NULL, 'Y', 'Log incoming messages to screen.', 'ScreenLogShowIncoming', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (102, '2017-03-22 05:46:26.663', 0, NULL, 'Y', 'Log outgoing messages to screen.', 'ScreenLogShowOutgoing', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (103, '2017-03-22 05:46:26.668', 0, NULL, 'N', 'Filter heartbeats from output (both incoming and outgoing)', 'ScreenLogShowHeartbeats', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (104, '2017-03-22 05:46:26.672', 0, NULL, 'N', 'Determines if sequence numbers should be reset before sending/receiving a logon request.', 'ResetOnLogon', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (105, '2017-03-22 05:46:26.677', 0, NULL, 'N', 'Determines if sequence numbers should be reset to 1 after a normal logout termination.', 'ResetOnLogout', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (106, '2017-03-22 05:46:26.681', 0, NULL, 'N', 'Determines if sequence numbers should be reset to 1 after an abnormal termination.', 'ResetOnDisconnect', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (107, '2017-03-22 05:46:26.686', 0, NULL, 'N', 'Session setting for doing an automatic reset when an error occurs. A reset means disconnect, sequence numbers reset, store cleaned and reconnect, as for a daily reset.', 'ResetOnError', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (108, '2017-03-22 05:46:26.691', 0, NULL, 'N', 'Session setting for doing an automatic disconnect when an error occurs.', 'DisconnectOnError', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (109, '2017-03-22 05:46:26.696', 0, NULL, 'N', 'Add tag LastMsgSeqNumProcessed in the header (optional tag 369).', 'EnableLastMsgSeqNumProcessed', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (110, '2017-03-22 05:46:26.701', 0, NULL, 'N', 'Add tag NextExpectedMsgSeqNum (optional tag 789) on the sent Logon message and use value of tag 789 on received Logon message to synchronize session. This should not be enabled for FIX versions lt 4.4. Only works when ValidateSequenceNumbers is enabled.', 'EnableNextExpectedMsgSeqNum', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (111, '2017-03-22 05:46:26.705', 0, NULL, '0', 'Setting to limit the size of a resend request in case of missing messages. This is useful when the remote FIX engine does not allow to ask for more than n message for a ResendRequest. E.g. if the ResendRequestChunkSize is set to 5 and a gap of 7 messages is detected, a first resend request will be sent for 5 messages. When this gap has been filled, another resend request for 2 messages will be sent. If the ResendRequestChunkSize is set to 0, only one ResendRequest for all the missing messages will be sent.', 'ResendRequestChunkSize', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (112, '2017-03-22 05:46:26.71', 0, NULL, 'N', 'Continue initializing sessions if an error occurs.', 'ContinueInitializationOnError', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (113, '2017-03-22 05:46:26.715', 0, NULL, 'N', 'Allows sending of redundant resend requests.', 'SendRedundantResendRequests', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (114, '2017-03-22 05:46:26.721', 0, NULL, '0.5', 'Fraction of the heartbeat interval which defines the additional time to wait if a TestRequest sent after a missing heartbeat times out.', 'TestRequestDelayMultiplier', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (115, '2017-03-22 05:46:26.725', 0, NULL, 'N', 'Heartbeat detection is disabled. A disconnect due to a missing heartbeat will never occur.', 'DisableHeartBeatCheck', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (116, '2017-03-22 05:46:26.731', 0, NULL, 'N', 'Fill in heartbeats on resend when reading from message store fails.', 'ForceResendWhenCorruptedStore', '', false);
INSERT INTO fix_session_attr_dscrptrs VALUES (117, '2017-03-22 05:46:26.737', 0, NULL, '', 'Name of the session modifiers to apply to this session', 'org.marketcetera.sessioncustomization', '', false);


--
-- Data for Name: fix_sessions; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: fix_session_attributes; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('hibernate_sequence', 117, true);


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

INSERT INTO permissions VALUES (5, '2017-03-22 05:46:24.31', 0, 'Access to Add Session action', 'AddSessionAction');
INSERT INTO permissions VALUES (6, '2017-03-22 05:46:24.318', 0, 'Access to Delete Session action', 'DeleteSessionAction');
INSERT INTO permissions VALUES (7, '2017-03-22 05:46:24.324', 0, 'Access to disable session action', 'DisableSessionAction');
INSERT INTO permissions VALUES (8, '2017-03-22 05:46:24.331', 0, 'Access to edit session action', 'EditSessionAction');
INSERT INTO permissions VALUES (9, '2017-03-22 05:46:24.337', 0, 'Access to enable session action', 'EnableSessionAction');
INSERT INTO permissions VALUES (10, '2017-03-22 05:46:24.344', 0, 'Access to update sequence numbers action', 'UpdateSequenceAction');
INSERT INTO permissions VALUES (11, '2017-03-22 05:46:24.35', 0, 'Access to start session action', 'StartSessionAction');
INSERT INTO permissions VALUES (12, '2017-03-22 05:46:24.357', 0, 'Access to stop session action', 'StopSessionAction');
INSERT INTO permissions VALUES (13, '2017-03-22 05:46:24.363', 0, 'Access to view session action', 'ViewSessionAction');
INSERT INTO permissions VALUES (14, '2017-03-22 05:46:24.369', 0, 'Access to read instance data action', 'ReadInstanceDataAction');
INSERT INTO permissions VALUES (15, '2017-03-22 05:46:24.375', 0, 'Access to read FIX session attribute descriptors action', 'ReadFixSessionAttributeDescriptorsAction');
INSERT INTO permissions VALUES (16, '2017-03-22 05:46:24.381', 0, 'Access to create user action', 'CreateUserAction');
INSERT INTO permissions VALUES (17, '2017-03-22 05:46:24.387', 0, 'Access to read user action', 'ReadUserAction');
INSERT INTO permissions VALUES (18, '2017-03-22 05:46:24.393', 0, 'Access to update user action', 'UpdateUserAction');
INSERT INTO permissions VALUES (19, '2017-03-22 05:46:24.4', 0, 'Access to delete user action', 'DeleteUserAction');
INSERT INTO permissions VALUES (20, '2017-03-22 05:46:24.406', 0, 'Access to change user password action', 'ChangeUserPasswordAction');
INSERT INTO permissions VALUES (21, '2017-03-22 05:46:24.413', 0, 'Access to read user permissions action', 'ReadUserPermisionsAction');
INSERT INTO permissions VALUES (22, '2017-03-22 05:46:24.419', 0, 'Access to create permission action', 'CreatePermissionAction');
INSERT INTO permissions VALUES (23, '2017-03-22 05:46:24.425', 0, 'Access to read permission action', 'ReadPermissionAction');
INSERT INTO permissions VALUES (24, '2017-03-22 05:46:24.431', 0, 'Access to update permission action', 'UpdatePermissionAction');
INSERT INTO permissions VALUES (25, '2017-03-22 05:46:24.437', 0, 'Access to delete permission action', 'DeletePermissionAction');
INSERT INTO permissions VALUES (26, '2017-03-22 05:46:24.444', 0, 'Access to create role action', 'CreateRoleAction');
INSERT INTO permissions VALUES (27, '2017-03-22 05:46:24.451', 0, 'Access to read role action', 'ReadRoleAction');
INSERT INTO permissions VALUES (28, '2017-03-22 05:46:24.456', 0, 'Access to update role action', 'UpdateRoleAction');
INSERT INTO permissions VALUES (29, '2017-03-22 05:46:24.463', 0, 'Access to delete role action', 'DeleteRoleAction');
INSERT INTO permissions VALUES (30, '2017-03-22 05:46:24.468', 0, 'Access to view broker status action', 'ViewBrokerStatusAction');
INSERT INTO permissions VALUES (31, '2017-03-22 05:46:24.474', 0, 'Access to view open orders action', 'ViewOpenOrdersAction');
INSERT INTO permissions VALUES (32, '2017-03-22 05:46:24.479', 0, 'Access to view reports action', 'ViewReportAction');
INSERT INTO permissions VALUES (33, '2017-03-22 05:46:24.488', 0, 'Access to view positions action', 'ViewPositionAction');
INSERT INTO permissions VALUES (34, '2017-03-22 05:46:24.493', 0, 'Access to send new orders action', 'SendOrderAction');
INSERT INTO permissions VALUES (35, '2017-03-22 05:46:24.499', 0, 'Access to view user data action', 'ViewUserDataAction');
INSERT INTO permissions VALUES (36, '2017-03-22 05:46:24.504', 0, 'Access to write user data action', 'WriteUserDataAction');
INSERT INTO permissions VALUES (37, '2017-03-22 05:46:24.511', 0, 'Access to manually add new reports action', 'AddReportAction');
INSERT INTO permissions VALUES (38, '2017-03-22 05:46:24.516', 0, 'Access to manually delete reports action', 'DeleteReportAction');
INSERT INTO permissions VALUES (39, '2017-03-22 05:46:24.522', 0, 'Access to read a user attribute action', 'ReadUserAttributeAction');
INSERT INTO permissions VALUES (40, '2017-03-22 05:46:24.527', 0, 'Access to write a user attribute action', 'WriteUserAttributeAction');


--
-- Data for Name: roles; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO roles VALUES (41, '2017-03-22 05:46:24.587', 0, 'Admin role', 'Admin');
INSERT INTO roles VALUES (42, '2017-03-22 05:46:24.635', 0, 'Trader role', 'Trader');
INSERT INTO roles VALUES (43, '2017-03-22 05:46:24.666', 0, 'Trader Admin role', 'TraderAdmin');


--
-- Data for Name: roles_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO roles_permissions VALUES (41, 6);
INSERT INTO roles_permissions VALUES (41, 16);
INSERT INTO roles_permissions VALUES (41, 12);
INSERT INTO roles_permissions VALUES (41, 8);
INSERT INTO roles_permissions VALUES (41, 13);
INSERT INTO roles_permissions VALUES (41, 11);
INSERT INTO roles_permissions VALUES (41, 35);
INSERT INTO roles_permissions VALUES (41, 28);
INSERT INTO roles_permissions VALUES (41, 26);
INSERT INTO roles_permissions VALUES (41, 5);
INSERT INTO roles_permissions VALUES (41, 7);
INSERT INTO roles_permissions VALUES (41, 14);
INSERT INTO roles_permissions VALUES (41, 29);
INSERT INTO roles_permissions VALUES (41, 27);
INSERT INTO roles_permissions VALUES (41, 36);
INSERT INTO roles_permissions VALUES (41, 30);
INSERT INTO roles_permissions VALUES (41, 10);
INSERT INTO roles_permissions VALUES (41, 15);
INSERT INTO roles_permissions VALUES (41, 20);
INSERT INTO roles_permissions VALUES (41, 39);
INSERT INTO roles_permissions VALUES (41, 24);
INSERT INTO roles_permissions VALUES (41, 40);
INSERT INTO roles_permissions VALUES (41, 21);
INSERT INTO roles_permissions VALUES (41, 23);
INSERT INTO roles_permissions VALUES (41, 9);
INSERT INTO roles_permissions VALUES (41, 19);
INSERT INTO roles_permissions VALUES (41, 17);
INSERT INTO roles_permissions VALUES (41, 22);
INSERT INTO roles_permissions VALUES (41, 18);
INSERT INTO roles_permissions VALUES (41, 25);
INSERT INTO roles_permissions VALUES (42, 30);
INSERT INTO roles_permissions VALUES (42, 34);
INSERT INTO roles_permissions VALUES (42, 37);
INSERT INTO roles_permissions VALUES (42, 31);
INSERT INTO roles_permissions VALUES (42, 33);
INSERT INTO roles_permissions VALUES (42, 39);
INSERT INTO roles_permissions VALUES (42, 36);
INSERT INTO roles_permissions VALUES (42, 40);
INSERT INTO roles_permissions VALUES (42, 32);
INSERT INTO roles_permissions VALUES (42, 35);
INSERT INTO roles_permissions VALUES (43, 36);
INSERT INTO roles_permissions VALUES (43, 35);
INSERT INTO roles_permissions VALUES (43, 38);
INSERT INTO roles_permissions VALUES (43, 37);
INSERT INTO roles_permissions VALUES (43, 33);
INSERT INTO roles_permissions VALUES (43, 30);
INSERT INTO roles_permissions VALUES (43, 39);
INSERT INTO roles_permissions VALUES (43, 32);
INSERT INTO roles_permissions VALUES (43, 34);
INSERT INTO roles_permissions VALUES (43, 40);
INSERT INTO roles_permissions VALUES (43, 31);


--
-- Data for Name: roles_users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO roles_users VALUES (41, 1);
INSERT INTO roles_users VALUES (42, 3);
INSERT INTO roles_users VALUES (43, 4);


--
-- Data for Name: supervisor_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO supervisor_permissions VALUES (44, '2017-03-22 05:46:24.692', 0, 'Trader supervisor role', 'TraderSupervisor', 4);


--
-- Data for Name: supervisor_permissions_permissions; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO supervisor_permissions_permissions VALUES (44, 31);
INSERT INTO supervisor_permissions_permissions VALUES (44, 30);
INSERT INTO supervisor_permissions_permissions VALUES (44, 35);
INSERT INTO supervisor_permissions_permissions VALUES (44, 32);
INSERT INTO supervisor_permissions_permissions VALUES (44, 33);


--
-- Data for Name: supervisor_permissions_users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO supervisor_permissions_users VALUES (44, 3);


--
-- Data for Name: system_info; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO system_info VALUES (2, '2017-03-22 05:46:17.983', 0, 'indicates current database schema version', 'schema version', '7');


--
-- Data for Name: user_attributes; Type: TABLE DATA; Schema: public; Owner: -
--
