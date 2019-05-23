--
-- PostgreSQL database dump
--

--
-- Name: exec_reports; Type: TABLE; Schema:  Owner: -
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


--
-- Name: fix_messages; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE fix_messages (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    message text NOT NULL
);


--
-- Name: fix_session_attr_dscrptrs; Type: TABLE; Schema:  Owner: -
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


--
-- Name: fix_session_attributes; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE fix_session_attributes (
    fix_session_id bigint NOT NULL,
    value character varying(255),
    name character varying(255) NOT NULL
);


--
-- Name: fix_sessions; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE fix_sessions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    affinity integer NOT NULL,
    broker_id character varying(255) NOT NULL,
    host character varying(255) NOT NULL,
    acceptor boolean NOT NULL,
    deleted boolean NOT NULL,
    enabled boolean NOT NULL,
    mapped_broker_id character varying(255),
    port integer NOT NULL,
    session_id character varying(255) NOT NULL
);


--
-- Name: hibernate_sequence; Type: SEQUENCE; Schema:  Owner: -
--

CREATE SEQUENCE hibernate_sequence
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: id_repository; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE id_repository (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    next_id bigint
);


--
-- Name: incoming_fix_messages; Type: TABLE; Schema:  Owner: -
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


--
-- Name: message_store_messages; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE message_store_messages (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    session_id character varying(255) NOT NULL,
    message character varying(8192) NOT NULL,
    msg_seq_num integer NOT NULL
);


--
-- Name: message_store_sessions; Type: TABLE; Schema:  Owner: -
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


--
-- Name: order_status; Type: TABLE; Schema:  Owner: -
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


--
-- Name: outgoing_messages; Type: TABLE; Schema:  Owner: -
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


--
-- Name: permissions; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE permissions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL
);


--
-- Name: reports; Type: TABLE; Schema:  Owner: -
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


--
-- Name: roles; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE roles (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL
);


--
-- Name: roles_permissions; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE roles_permissions (
    roles_id bigint NOT NULL,
    permissions_id bigint NOT NULL
);


--
-- Name: roles_subjects; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE roles_subjects (
    role_id bigint NOT NULL,
    subjects_id bigint NOT NULL
);


--
-- Name: supervisor_permissions; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE supervisor_permissions (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    description character varying(255),
    name character varying(255) NOT NULL,
    user_id bigint NOT NULL
);


--
-- Name: supervisor_permissions_permissions; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE supervisor_permissions_permissions (
    supervisor_permission_id bigint NOT NULL,
    permissions_id bigint NOT NULL
);


--
-- Name: supervisor_permissions_subjects; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE supervisor_permissions_subjects (
    supervisor_permission_id bigint NOT NULL,
    subjects_id bigint NOT NULL
);


--
-- Name: user_attributes; Type: TABLE; Schema:  Owner: -
--

CREATE TABLE user_attributes (
    id bigint NOT NULL,
    last_updated timestamp without time zone NOT NULL,
    update_count integer NOT NULL,
    attribute text NOT NULL,
    user_attribute_type integer NOT NULL,
    user_id bigint
);


--
-- Name: users; Type: TABLE; Schema:  Owner: -
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


--
-- Name: exec_reports exec_reports_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT exec_reports_pkey PRIMARY KEY (id);


--
-- Name: fix_messages fix_messages_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY fix_messages
    ADD CONSTRAINT fix_messages_pkey PRIMARY KEY (id);


--
-- Name: fix_session_attr_dscrptrs fix_session_attr_dscrptrs_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY fix_session_attr_dscrptrs
    ADD CONSTRAINT fix_session_attr_dscrptrs_pkey PRIMARY KEY (id);


--
-- Name: fix_session_attributes fix_session_attributes_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY fix_session_attributes
    ADD CONSTRAINT fix_session_attributes_pkey PRIMARY KEY (fix_session_id, name);


--
-- Name: fix_sessions fix_sessions_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY fix_sessions
    ADD CONSTRAINT fix_sessions_pkey PRIMARY KEY (id);


--
-- Name: id_repository id_repository_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY id_repository
    ADD CONSTRAINT id_repository_pkey PRIMARY KEY (id);


--
-- Name: incoming_fix_messages incoming_fix_messages_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY incoming_fix_messages
    ADD CONSTRAINT incoming_fix_messages_pkey PRIMARY KEY (id);


--
-- Name: message_store_messages message_store_messages_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY message_store_messages
    ADD CONSTRAINT message_store_messages_pkey PRIMARY KEY (id);


--
-- Name: message_store_sessions message_store_sessions_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY message_store_sessions
    ADD CONSTRAINT message_store_sessions_pkey PRIMARY KEY (id);


--
-- Name: order_status order_status_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT order_status_pkey PRIMARY KEY (id);


--
-- Name: outgoing_messages outgoing_messages_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY outgoing_messages
    ADD CONSTRAINT outgoing_messages_pkey PRIMARY KEY (id);


--
-- Name: permissions permissions_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT permissions_pkey PRIMARY KEY (id);


--
-- Name: reports reports_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT reports_pkey PRIMARY KEY (id);


--
-- Name: roles_permissions roles_permissions_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY roles_permissions
    ADD CONSTRAINT roles_permissions_pkey PRIMARY KEY (roles_id, permissions_id);


--
-- Name: roles roles_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: roles_subjects roles_subjects_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY roles_subjects
    ADD CONSTRAINT roles_subjects_pkey PRIMARY KEY (role_id, subjects_id);


--
-- Name: supervisor_permissions_permissions supervisor_permissions_permissions_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY supervisor_permissions_permissions
    ADD CONSTRAINT supervisor_permissions_permissions_pkey PRIMARY KEY (supervisor_permission_id, permissions_id);


--
-- Name: supervisor_permissions supervisor_permissions_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY supervisor_permissions
    ADD CONSTRAINT supervisor_permissions_pkey PRIMARY KEY (id);


--
-- Name: supervisor_permissions_subjects supervisor_permissions_subjects_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY supervisor_permissions_subjects
    ADD CONSTRAINT supervisor_permissions_subjects_pkey PRIMARY KEY (supervisor_permission_id, subjects_id);


--
-- Name: users uk3g1j96g94xpk3lpxl2qbl985x; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT uk3g1j96g94xpk3lpxl2qbl985x UNIQUE (name);


--
-- Name: fix_sessions uk_27g5lmkiqxliyc4uthpee84y0; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY fix_sessions
    ADD CONSTRAINT uk_27g5lmkiqxliyc4uthpee84y0 UNIQUE (name);


--
-- Name: supervisor_permissions uk_4rd5towbshlb1v1hv6w00sf6b; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY supervisor_permissions
    ADD CONSTRAINT uk_4rd5towbshlb1v1hv6w00sf6b UNIQUE (name);


--
-- Name: reports uk_aely7chrvtqwv4xfm76xuj5bh; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT uk_aely7chrvtqwv4xfm76xuj5bh UNIQUE (report_id);


--
-- Name: reports uk_ca90a4kkdycpon22ynli3d6oi; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT uk_ca90a4kkdycpon22ynli3d6oi UNIQUE (fix_message_id);


--
-- Name: fix_session_attr_dscrptrs uk_gb3x058kyh5s4fxw1fi67bysc; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY fix_session_attr_dscrptrs
    ADD CONSTRAINT uk_gb3x058kyh5s4fxw1fi67bysc UNIQUE (name);


--
-- Name: order_status uk_h8v9n38cydusmk1d0yya6nd2d; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT uk_h8v9n38cydusmk1d0yya6nd2d UNIQUE (report_id);


--
-- Name: outgoing_messages uk_hxvc6vrtmw1swik69wxt0drlc; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY outgoing_messages
    ADD CONSTRAINT uk_hxvc6vrtmw1swik69wxt0drlc UNIQUE (fix_message_id);


--
-- Name: exec_reports uk_imdq099u0qa8ob9tt5ljm6f7u; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT uk_imdq099u0qa8ob9tt5ljm6f7u UNIQUE (report_id);


--
-- Name: roles uk_ofx66keruapi6vyqpv6f2or37; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY roles
    ADD CONSTRAINT uk_ofx66keruapi6vyqpv6f2or37 UNIQUE (name);


--
-- Name: permissions uk_pnvtwliis6p05pn6i3ndjrqt2; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY permissions
    ADD CONSTRAINT uk_pnvtwliis6p05pn6i3ndjrqt2 UNIQUE (name);


--
-- Name: user_attributes user_attributes_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY user_attributes
    ADD CONSTRAINT user_attributes_pkey PRIMARY KEY (id);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: exec_reports fk1celhypj9vint37eobsn22s1b; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT fk1celhypj9vint37eobsn22s1b FOREIGN KEY (viewer_id) REFERENCES users(id);


--
-- Name: fix_session_attributes fk3lrqyamu7790pie2ivjh8vfq5; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY fix_session_attributes
    ADD CONSTRAINT fk3lrqyamu7790pie2ivjh8vfq5 FOREIGN KEY (fix_session_id) REFERENCES fix_sessions(id);


--
-- Name: supervisor_permissions_permissions fk52r0b731gg196j62f1hobv1ja; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY supervisor_permissions_permissions
    ADD CONSTRAINT fk52r0b731gg196j62f1hobv1ja FOREIGN KEY (supervisor_permission_id) REFERENCES supervisor_permissions(id);


--
-- Name: roles_permissions fk570wuy6sacdnrw8wdqjfh7j0q; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY roles_permissions
    ADD CONSTRAINT fk570wuy6sacdnrw8wdqjfh7j0q FOREIGN KEY (permissions_id) REFERENCES permissions(id);


--
-- Name: roles_subjects fk5nf9c8w732ua95ybhatvlnq77; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY roles_subjects
    ADD CONSTRAINT fk5nf9c8w732ua95ybhatvlnq77 FOREIGN KEY (role_id) REFERENCES roles(id);


--
-- Name: outgoing_messages fk7aeswc52coxk8sspdt9ua5e15; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY outgoing_messages
    ADD CONSTRAINT fk7aeswc52coxk8sspdt9ua5e15 FOREIGN KEY (fix_message_id) REFERENCES fix_messages(id);


--
-- Name: reports fk98bmvk76e2gp10muheog0j1wa; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT fk98bmvk76e2gp10muheog0j1wa FOREIGN KEY (fix_message_id) REFERENCES fix_messages(id);


--
-- Name: roles_permissions fkb9gqc5kvla3ijovnihsbb816e; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY roles_permissions
    ADD CONSTRAINT fkb9gqc5kvla3ijovnihsbb816e FOREIGN KEY (roles_id) REFERENCES roles(id);


--
-- Name: supervisor_permissions fkf7mxsack9d04s94a2aha7jyr9; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY supervisor_permissions
    ADD CONSTRAINT fkf7mxsack9d04s94a2aha7jyr9 FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: exec_reports fkfn47lj607ghtt1lfie8adevxc; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT fkfn47lj607ghtt1lfie8adevxc FOREIGN KEY (actor_id) REFERENCES users(id);


--
-- Name: roles_subjects fkgnestjx1e6lrwigtqo26yc8sm; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY roles_subjects
    ADD CONSTRAINT fkgnestjx1e6lrwigtqo26yc8sm FOREIGN KEY (subjects_id) REFERENCES users(id);


--
-- Name: reports fkh0r6ppu75byn1y7y0uiteel8q; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT fkh0r6ppu75byn1y7y0uiteel8q FOREIGN KEY (actor_id) REFERENCES users(id);


--
-- Name: supervisor_permissions_subjects fkjoqsj9hbev2dsnvigbqvu4jh5; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY supervisor_permissions_subjects
    ADD CONSTRAINT fkjoqsj9hbev2dsnvigbqvu4jh5 FOREIGN KEY (supervisor_permission_id) REFERENCES supervisor_permissions(id);


--
-- Name: order_status fkjqtx22v71dod89tht0fywav7; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT fkjqtx22v71dod89tht0fywav7 FOREIGN KEY (report_id) REFERENCES reports(id);


--
-- Name: order_status fklxkg9il8q8k9448o5nggibnbs; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT fklxkg9il8q8k9448o5nggibnbs FOREIGN KEY (actor_id) REFERENCES users(id);


--
-- Name: exec_reports fkn47ta2b6e9wih8b97oubxvd3s; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY exec_reports
    ADD CONSTRAINT fkn47ta2b6e9wih8b97oubxvd3s FOREIGN KEY (report_id) REFERENCES reports(id);


--
-- Name: supervisor_permissions_subjects fkonhic2wv55gy1w4qxk85efo5f; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY supervisor_permissions_subjects
    ADD CONSTRAINT fkonhic2wv55gy1w4qxk85efo5f FOREIGN KEY (subjects_id) REFERENCES users(id);


--
-- Name: outgoing_messages fkpkya7xsumlsbfm4b125k07ke8; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY outgoing_messages
    ADD CONSTRAINT fkpkya7xsumlsbfm4b125k07ke8 FOREIGN KEY (actor_id) REFERENCES users(id);


--
-- Name: reports fksfc0wdpjferohmpylygff4urs; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY reports
    ADD CONSTRAINT fksfc0wdpjferohmpylygff4urs FOREIGN KEY (viewer_id) REFERENCES users(id);


--
-- Name: user_attributes fkskw1x6g2kt3g0i9507k4a4tqw; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY user_attributes
    ADD CONSTRAINT fkskw1x6g2kt3g0i9507k4a4tqw FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: order_status fkt8iysrml49hnmembw8pkv5g3n; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY order_status
    ADD CONSTRAINT fkt8iysrml49hnmembw8pkv5g3n FOREIGN KEY (viewer_id) REFERENCES users(id);


--
-- Name: supervisor_permissions_permissions fktkctb5n3ggmu727f5mwlmutk3; Type: FK CONSTRAINT; Schema:  Owner: -
--

ALTER TABLE ONLY supervisor_permissions_permissions
    ADD CONSTRAINT fktkctb5n3ggmu727f5mwlmutk3 FOREIGN KEY (permissions_id) REFERENCES permissions(id);


--
-- PostgreSQL database dump complete
--

