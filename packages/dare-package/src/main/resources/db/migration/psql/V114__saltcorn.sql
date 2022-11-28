--
-- Name: _sc_config; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_config (
    key text NOT NULL,
    value jsonb NOT NULL
);


--
-- Name: _sc_errors; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_errors (
    id integer NOT NULL,
    stack text NOT NULL,
    message text NOT NULL,
    occur_at timestamp without time zone NOT NULL,
    tenant text NOT NULL,
    user_id integer,
    url text NOT NULL,
    headers jsonb NOT NULL,
    body jsonb
);


--
-- Name: _sc_errors_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_errors_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_errors_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_errors_id_seq OWNED BY _sc_errors.id;


--
-- Name: _sc_event_log; Type: TABLE; Schema: public; Owner: -
--

CREATE UNLOGGED TABLE _sc_event_log (
    id integer NOT NULL,
    event_type text NOT NULL,
    channel text,
    occur_at timestamp without time zone NOT NULL,
    user_id integer,
    payload jsonb
);


--
-- Name: _sc_event_log_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_event_log_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_event_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_event_log_id_seq OWNED BY _sc_event_log.id;


--
-- Name: _sc_fields; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_fields (
    id integer NOT NULL,
    table_id integer NOT NULL,
    name text NOT NULL,
    label text,
    type text,
    reftable_name text,
    attributes jsonb,
    required boolean DEFAULT false NOT NULL,
    is_unique boolean DEFAULT false NOT NULL,
    calculated boolean DEFAULT false NOT NULL,
    stored boolean DEFAULT false NOT NULL,
    expression text,
    primary_key boolean,
    refname text,
    reftype text,
    description text DEFAULT ''::text
);


--
-- Name: _sc_fields_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_fields_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_fields_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_fields_id_seq OWNED BY _sc_fields.id;


--
-- Name: _sc_files; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_files (
    id integer NOT NULL,
    filename text NOT NULL,
    location text NOT NULL,
    uploaded_at timestamp without time zone NOT NULL,
    size_kb integer NOT NULL,
    user_id integer,
    mime_super text NOT NULL,
    mime_sub text NOT NULL,
    min_role_read integer NOT NULL,
    s3_store boolean
);


--
-- Name: _sc_files_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_files_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_files_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_files_id_seq OWNED BY _sc_files.id;


--
-- Name: _sc_library; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_library (
    id integer NOT NULL,
    name text NOT NULL,
    icon text,
    layout jsonb
);


--
-- Name: _sc_library_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_library_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_library_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_library_id_seq OWNED BY _sc_library.id;


--
-- Name: _sc_migrations; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_migrations (
    migration text NOT NULL
);


--
-- Name: _sc_pages; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_pages (
    id integer NOT NULL,
    name text NOT NULL,
    title text NOT NULL,
    description text DEFAULT ''::text NOT NULL,
    min_role integer NOT NULL,
    layout jsonb NOT NULL,
    fixed_states jsonb NOT NULL
);


--
-- Name: _sc_pages_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_pages_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_pages_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_pages_id_seq OWNED BY _sc_pages.id;


--
-- Name: _sc_plugins; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_plugins (
    id integer NOT NULL,
    name character varying(128),
    source character varying(128),
    location character varying(128),
    version text DEFAULT 'latest'::text,
    configuration jsonb,
    deploy_private_key text
);


--
-- Name: _sc_plugins_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_plugins_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_plugins_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_plugins_id_seq OWNED BY _sc_plugins.id;


--
-- Name: _sc_roles; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_roles (
    id integer NOT NULL,
    role character varying(50)
);


--
-- Name: _sc_roles_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_roles_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_roles_id_seq OWNED BY _sc_roles.id;


--
-- Name: _sc_session; Type: TABLE; Schema: public; Owner: -
--

CREATE UNLOGGED TABLE _sc_session (
    sid character varying NOT NULL,
    sess json NOT NULL,
    expire timestamp(6) without time zone NOT NULL
);


--
-- Name: _sc_snapshots; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_snapshots (
    id integer NOT NULL,
    created timestamp with time zone NOT NULL,
    pack jsonb,
    hash text DEFAULT ''::text NOT NULL
);


--
-- Name: _sc_snapshots_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_snapshots_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_snapshots_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_snapshots_id_seq OWNED BY _sc_snapshots.id;


--
-- Name: _sc_table_constraints; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_table_constraints (
    id integer NOT NULL,
    table_id integer NOT NULL,
    configuration jsonb NOT NULL,
    type text NOT NULL
);


--
-- Name: _sc_table_constraints_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_table_constraints_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_table_constraints_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_table_constraints_id_seq OWNED BY _sc_table_constraints.id;


--
-- Name: _sc_tables; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_tables (
    id integer NOT NULL,
    name text NOT NULL,
    min_role_read integer DEFAULT 1 NOT NULL,
    min_role_write integer DEFAULT 1 NOT NULL,
    versioned boolean DEFAULT false NOT NULL,
    ownership_field_id integer,
    description text DEFAULT ''::text,
    ownership_formula text
);


--
-- Name: _sc_tables_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_tables_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_tables_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_tables_id_seq OWNED BY _sc_tables.id;


--
-- Name: _sc_tag_entries; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_tag_entries (
    id integer NOT NULL,
    tag_id integer NOT NULL,
    table_id integer,
    view_id integer,
    page_id integer,
    trigger_id integer
);


--
-- Name: _sc_tag_entries_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_tag_entries_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_tag_entries_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_tag_entries_id_seq OWNED BY _sc_tag_entries.id;


--
-- Name: _sc_tags; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_tags (
    id integer NOT NULL,
    name text NOT NULL
);


--
-- Name: _sc_tags_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_tags_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_tags_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_tags_id_seq OWNED BY _sc_tags.id;


--
-- Name: _sc_tenants; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_tenants (
    subdomain text NOT NULL,
    email text NOT NULL,
    description text DEFAULT ''::text,
    template text,
    created timestamp without time zone
);


--
-- Name: _sc_triggers; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_triggers (
    id integer NOT NULL,
    action text NOT NULL,
    table_id integer,
    configuration jsonb NOT NULL,
    when_trigger text NOT NULL,
    name text,
    description text DEFAULT ''::text,
    channel text,
    min_role integer
);


--
-- Name: _sc_triggers_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_triggers_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_triggers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_triggers_id_seq OWNED BY _sc_triggers.id;


--
-- Name: _sc_views; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE _sc_views (
    id integer NOT NULL,
    viewtemplate text NOT NULL,
    name text NOT NULL,
    table_id integer,
    configuration jsonb NOT NULL,
    min_role integer DEFAULT 10 NOT NULL,
    default_render_page text,
    exttable_name text,
    description text DEFAULT ''::text,
    slug jsonb
);


--
-- Name: _sc_views_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE _sc_views_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: _sc_views_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE _sc_views_id_seq OWNED BY _sc_views.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: -
--

CREATE TABLE users (
    id integer NOT NULL,
    email character varying(128) NOT NULL,
    password character varying(60),
    role_id integer NOT NULL,
    reset_password_token text,
    reset_password_expiry timestamp without time zone,
    language text,
    disabled boolean DEFAULT false NOT NULL,
    api_token text,
    _attributes jsonb,
    verification_token text,
    verified_on timestamp without time zone,
    last_mobile_login timestamp without time zone
);


--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: -
--

CREATE SEQUENCE users_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: -
--

ALTER SEQUENCE users_id_seq OWNED BY users.id;


--
-- Name: _sc_errors id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_errors ALTER COLUMN id SET DEFAULT nextval('_sc_errors_id_seq'::regclass);


--
-- Name: _sc_event_log id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_event_log ALTER COLUMN id SET DEFAULT nextval('_sc_event_log_id_seq'::regclass);


--
-- Name: _sc_fields id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_fields ALTER COLUMN id SET DEFAULT nextval('_sc_fields_id_seq'::regclass);


--
-- Name: _sc_files id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_files ALTER COLUMN id SET DEFAULT nextval('_sc_files_id_seq'::regclass);


--
-- Name: _sc_library id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_library ALTER COLUMN id SET DEFAULT nextval('_sc_library_id_seq'::regclass);


--
-- Name: _sc_pages id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_pages ALTER COLUMN id SET DEFAULT nextval('_sc_pages_id_seq'::regclass);


--
-- Name: _sc_plugins id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_plugins ALTER COLUMN id SET DEFAULT nextval('_sc_plugins_id_seq'::regclass);


--
-- Name: _sc_roles id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_roles ALTER COLUMN id SET DEFAULT nextval('_sc_roles_id_seq'::regclass);


--
-- Name: _sc_snapshots id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_snapshots ALTER COLUMN id SET DEFAULT nextval('_sc_snapshots_id_seq'::regclass);


--
-- Name: _sc_table_constraints id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_table_constraints ALTER COLUMN id SET DEFAULT nextval('_sc_table_constraints_id_seq'::regclass);


--
-- Name: _sc_tables id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tables ALTER COLUMN id SET DEFAULT nextval('_sc_tables_id_seq'::regclass);


--
-- Name: _sc_tag_entries id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tag_entries ALTER COLUMN id SET DEFAULT nextval('_sc_tag_entries_id_seq'::regclass);


--
-- Name: _sc_tags id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tags ALTER COLUMN id SET DEFAULT nextval('_sc_tags_id_seq'::regclass);


--
-- Name: _sc_triggers id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_triggers ALTER COLUMN id SET DEFAULT nextval('_sc_triggers_id_seq'::regclass);


--
-- Name: _sc_views id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_views ALTER COLUMN id SET DEFAULT nextval('_sc_views_id_seq'::regclass);


--
-- Name: users id; Type: DEFAULT; Schema: public; Owner: -
--

ALTER TABLE ONLY users ALTER COLUMN id SET DEFAULT nextval('users_id_seq'::regclass);


--
-- Data for Name: _sc_config; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO _sc_config VALUES ('legacy_file_id_locations', '{"v": {}}');
INSERT INTO _sc_config VALUES ('latest_npm_version', '{"v": {"@saltcorn/cli": {"time": "2022-11-28T20:31:05.367Z", "version": "0.8.0-beta.2"}}}');
INSERT INTO _sc_config VALUES ('available_packs', '{"v": [{"name": "Todo list", "description": "Simple todo list"}, {"name": "Issue  tracker", "description": "Track bugs or feedback"}, {"name": "Project management", "description": "Assignable tasks, projects, kanban board"}, {"name": "Address book", "description": "Contact list with meeting notes"}, {"name": "Blog", "description": "Blog with rich-text post and comments"}, {"name": "Project-employee assignment", "description": "Example of building interfaces based on many to many relationships"}, {"name": "Saltcorn store", "description": "The plugin and pack store running on store.saltcorn.com"}, {"name": "Software directory", "description": "Open-Source No-Code Directory"}, {"name": "Location display and search", "description": "Location-based search with map display"}, {"name": "Link sharing", "description": "Hacker news clone:  link sharing and discussion"}, {"name": "File transfer service", "description": "Allow anonymous users to upload files"}, {"name": "French Vocabulary", "description": "Flash cards with French-English dictionary"}, {"name": "Roadmap", "description": "Roadmap in a Kanban board"}, {"name": "Landing Page", "description": "Landing page with the lead capture"}, {"name": "Forum", "description": "Discussion forum"}, {"name": "Wiki", "description": "Wiki application"}]}');
INSERT INTO _sc_config VALUES ('available_packs_fetched_at', '{"v": "2022-11-27T18:10:57.809Z"}');
INSERT INTO _sc_config VALUES ('available_plugins', '{"v": [{"id": 1, "name": "json", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/json", "has_theme": false, "description": "JSON data type", "documentation_link": null}, {"id": 2, "name": "markdown", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/markdown", "has_theme": false, "description": "Markdown data type", "documentation_link": null}, {"id": 3, "name": "kanban", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/kanban", "has_theme": false, "description": "Kanban board view template", "documentation_link": null}, {"id": 6, "name": "visualize", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/visualize", "has_theme": false, "description": "Bar, pie and donut charts", "documentation_link": null}, {"id": 10, "name": "html", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/html", "has_theme": false, "description": "HTML field type", "documentation_link": null}, {"id": 11, "name": "quill-editor", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/quill-editor", "has_theme": false, "description": "Quill Rich text editor", "documentation_link": null}, {"id": 12, "name": "leaflet-map", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/leaflet-map", "has_theme": false, "description": "Maps with LeafletJS", "documentation_link": null}, {"id": 7, "name": "adminlte", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/adminlte", "has_theme": true, "description": "AdminLTE 3 theme", "documentation_link": null}, {"id": 8, "name": "tabler", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/tabler", "has_theme": true, "description": "Tabler theme", "documentation_link": null}, {"id": 9, "name": "any-bootstrap-theme", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/any-bootstrap-theme", "has_theme": true, "description": "Configurable Bootstrap themes", "documentation_link": null}, {"id": 14, "name": "sbadmin2", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/sbadmin2", "has_theme": true, "description": "Theme based on SBAdmin2", "documentation_link": null}, {"id": 15, "name": "carousel", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/carousel", "has_theme": false, "description": "Carousel slider ", "documentation_link": null}, {"id": 17, "name": "fullcalendar", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/fullcalendar", "has_theme": false, "description": "Calendar based on FullCalendar. Week and month views", "documentation_link": null}, {"id": 18, "name": "flatpickr-date", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/flatpickr-date", "has_theme": false, "description": "Date/time picker for Date fields", "documentation_link": null}, {"id": 19, "name": "stepper", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/stepper", "has_theme": false, "description": "Step through a set of rows", "documentation_link": null}, {"id": 20, "name": "nominatim-geocode", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/nominatim-geocode", "has_theme": false, "description": "Geocoding with Nominatim from OpenStreetMap", "documentation_link": "https://github.com/saltcorn/nominatim-geocode/blob/main/README.md"}, {"id": 21, "name": "comment-tree", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/comment-tree", "has_theme": false, "description": "Display rows with parent field in a tree", "documentation_link": null}, {"id": 22, "name": "badges", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/badges", "has_theme": false, "description": "Show badges depending on relationships", "documentation_link": null}, {"id": 23, "name": "geosearch", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/geosearch", "has_theme": false, "description": "Search by location", "documentation_link": null}, {"id": 24, "name": "material-design", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/material-design", "has_theme": true, "description": "Material Design theme based on mdbootstrap", "documentation_link": ""}, {"id": 25, "name": "twitter-auth", "source": "npm", "unsafe": false, "has_auth": true, "location": "@saltcorn/twitter-auth", "has_theme": false, "description": "Authentication with Twitter", "documentation_link": ""}, {"id": 26, "name": "github-auth", "source": "npm", "unsafe": false, "has_auth": true, "location": "@saltcorn/github-auth", "has_theme": false, "description": "Authentication with GitHub", "documentation_link": ""}, {"id": 48, "name": "shared-files", "source": "npm", "unsafe": true, "has_auth": false, "location": "@saltcorn/shared-files", "has_theme": false, "description": "File browser and links to files on shared drive", "documentation_link": null}, {"id": 27, "name": "google-auth", "source": "npm", "unsafe": false, "has_auth": true, "location": "@saltcorn/google-auth", "has_theme": false, "description": "Authentication with Google accounts", "documentation_link": ""}, {"id": 28, "name": "ldap-auth", "source": "npm", "unsafe": false, "has_auth": true, "location": "@saltcorn/ldap-auth", "has_theme": false, "description": "Authentication with LDAP/Active Directory", "documentation_link": ""}, {"id": 29, "name": "flash-cards", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/flash-cards", "has_theme": false, "description": "Flash card (back and front)", "documentation_link": null}, {"id": 30, "name": "oauth2-auth", "source": "npm", "unsafe": false, "has_auth": true, "location": "@saltcorn/oauth2-auth", "has_theme": false, "description": "Authentication with any OAuth 2.0 provider", "documentation_link": ""}, {"id": 31, "name": "system-info", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/system-info", "has_theme": false, "description": "System information: cpu/ram/disk usage", "documentation_link": "https://github.com/saltcorn/system-info/blob/main/README.md"}, {"id": 32, "name": "stripe", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/stripe", "has_theme": false, "description": "Billing with Stripe", "documentation_link": "https://github.com/saltcorn/stripe/blob/main/README.md"}, {"id": 33, "name": "saltcorn-gantt", "source": "npm", "unsafe": false, "has_auth": false, "location": "saltcorn-gantt", "has_theme": false, "description": "Gantt charts", "documentation_link": null}, {"id": 49, "name": "sql-list", "source": "npm", "unsafe": true, "has_auth": false, "location": "@saltcorn/sql-list", "has_theme": false, "description": "List view by entering SQL", "documentation_link": null}, {"id": 13, "name": "summernote", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/summernote", "has_theme": false, "description": "Summernote editor for HTML fields", "documentation_link": null}, {"id": 34, "name": "ckeditor4", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/ckeditor4", "has_theme": false, "description": "CKEditor4 editor for HTML fields", "documentation_link": null}, {"id": 35, "name": "uuid-type", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/uuid-type", "has_theme": false, "description": "UUID field type", "documentation_link": null}, {"id": 36, "name": "statistics", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/statistics", "has_theme": false, "description": "Statistic views", "documentation_link": null}, {"id": 37, "name": "filter-button-group", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/filter-button-group", "has_theme": false, "description": "Button groups as filters ", "documentation_link": null}, {"id": 38, "name": "reservable", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/reservable", "has_theme": false, "description": "Reservation system for online bookings", "documentation_link": null}, {"id": 39, "name": "mqtt", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/mqtt", "has_theme": false, "description": "MQTT event source and publish to MQTT broker", "documentation_link": null}, {"id": 40, "name": "twilio-verify-sms", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/twilio-verify-sms", "has_theme": false, "description": "Verify user accounts using SMS with Twilio API", "documentation_link": null}, {"id": 41, "name": "page-to-pdf", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/page-to-pdf", "has_theme": false, "description": "Generate PDF files from pages", "documentation_link": null}, {"id": 42, "name": "toc", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/toc", "has_theme": false, "description": "Table of contents from rows", "documentation_link": null}, {"id": 43, "name": "select2", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/select2", "has_theme": false, "description": "Typeahead fieldviews based on select2", "documentation_link": null}, {"id": 44, "name": "tabulator", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/tabulator", "has_theme": false, "description": "Grid edit view based on Tabulator", "documentation_link": null}, {"id": 45, "name": "colors", "source": "npm", "unsafe": false, "has_auth": false, "location": "saltcorn-colors", "has_theme": false, "description": "Adds more color fieldviews", "documentation_link": null}, {"id": 46, "name": "svelte-gantt", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/svelte-gantt", "has_theme": false, "description": "Gantt charts based on svelte-gantt", "documentation_link": null}, {"id": 47, "name": "daterangepicker", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/daterangepicker", "has_theme": false, "description": "Date Ranger Picker (Filter)", "documentation_link": null}, {"id": 50, "name": "colorpick", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/colorpick", "has_theme": false, "description": "Color editor based on ColorPick.js", "documentation_link": null}, {"id": 51, "name": "xncolorpicker", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/xncolorpicker", "has_theme": false, "description": "Color editor based on XN Color Picker", "documentation_link": null}, {"id": 52, "name": "selectize", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/selectize", "has_theme": false, "description": "Typeahead fieldviews based on selectize", "documentation_link": null}, {"id": 53, "name": "js-code-view", "source": "npm", "unsafe": true, "has_auth": false, "location": "@saltcorn/js-code-view", "has_theme": false, "description": "Generate Saltcorn view output from JavaScript code", "documentation_link": "https://github.com/saltcorn/js-code-view"}, {"id": 54, "name": "pivottable", "source": "npm", "unsafe": false, "has_auth": false, "location": "@saltcorn/pivottable", "has_theme": false, "description": "Editable Pivot tables based on pivottable.js", "documentation_link": "https://pivottable.js.org/examples/"}]}');
INSERT INTO _sc_config VALUES ('available_plugins_fetched_at', '{"v": "2022-11-27T18:10:57.739Z"}');
INSERT INTO _sc_config VALUES ('base_url', '{"v": "http://marketcetera.saltcorn.com/"}');
INSERT INTO _sc_config VALUES ('favicon_id', '{"v": ""}');
INSERT INTO _sc_config VALUES ('next_weekly_event', '{"v": "2022-12-03T16:12:19.204Z"}');
INSERT INTO _sc_config VALUES ('site_logo_id', '{"v": ""}');
INSERT INTO _sc_config VALUES ('site_name', '{"v": "Marketcetera Automated Trading Platform"}');
INSERT INTO _sc_config VALUES ('next_hourly_event', '{"v": "2022-11-28T21:34:43.727Z"}');
INSERT INTO _sc_config VALUES ('next_daily_event', '{"v": "2022-11-29T03:12:19.204Z"}');
INSERT INTO _sc_config VALUES ('menu_items', '{"v": []}');
INSERT INTO _sc_config VALUES ('unrolled_menu_items', '{"v": []}');


--
-- Data for Name: _sc_errors; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_event_log; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_fields; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO _sc_fields VALUES (1, 1, 'email', 'Email', 'String', NULL, '{}', true, true, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (2, 1, 'id', 'ID', 'Integer', NULL, '{}', true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (63, 7, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (64, 7, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (65, 7, 'account', 'account', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (66, 7, 'avg_price', 'avg_price', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (67, 7, 'cum_qty', 'cum_qty', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (68, 7, 'eff_cum_qty', 'eff_cum_qty', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (69, 7, 'expiry', 'expiry', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (70, 7, 'last_price', 'last_price', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (71, 7, 'last_qty', 'last_qty', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (72, 7, 'option_type', 'option_type', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (73, 7, 'order_id', 'order_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (74, 7, 'orig_order_id', 'orig_order_id', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (75, 7, 'root_order_id', 'root_order_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (76, 7, 'strike_price', 'strike_price', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (77, 7, 'symbol', 'symbol', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (78, 7, 'actor_id', 'actor_id', 'Key', 'metc_users', NULL, false, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (79, 7, 'report_id', 'report_id', 'Key', 'reports', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (80, 7, 'viewer_id', 'viewer_id', 'Key', 'metc_users', NULL, false, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (81, 7, 'broker_order_id', 'broker_order_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (82, 7, 'exec_id', 'exec_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (83, 7, 'exec_type', 'exec_type', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (84, 7, 'ord_status', 'ord_status', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (85, 7, 'security_type', 'security_type', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (86, 7, 'side', 'side', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (87, 7, 'leaves_qty', 'leaves_qty', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (88, 7, 'order_qty', 'order_qty', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (89, 7, 'order_type', 'order_type', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (90, 7, 'price', 'price', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (91, 7, 'tif', 'tif', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (92, 8, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (93, 8, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (94, 8, 'message', 'message', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (95, 9, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (96, 9, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (97, 9, 'advice', 'advice', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (98, 9, 'default_value', 'default_value', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (99, 9, 'description', 'description', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (100, 9, 'name', 'name', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (101, 9, 'pattern', 'pattern', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (102, 9, 'required', 'required', 'Bool', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (103, 10, 'fix_session_id', 'fix_session_id', 'Key', 'fix_sessions', NULL, true, true, false, false, NULL, true, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (104, 10, 'value', 'value', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (105, 10, 'name', 'name', 'String', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (106, 11, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (107, 11, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (108, 11, 'affinity', 'affinity', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (109, 11, 'broker_id', 'broker_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (110, 11, 'description', 'description', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (111, 11, 'host', 'host', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (112, 11, 'acceptor', 'acceptor', 'Bool', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (113, 11, 'deleted', 'deleted', 'Bool', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (114, 11, 'enabled', 'enabled', 'Bool', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (115, 11, 'name', 'name', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (116, 11, 'port', 'port', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (117, 11, 'session_id', 'session_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (118, 11, 'mapped_broker_id', 'mapped_broker_id', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (119, 12, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (120, 12, 'clordid', 'clordid', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (121, 12, 'execid', 'execid', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (122, 12, 'message', 'message', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (123, 12, 'msg_seq_num', 'msg_seq_num', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (124, 12, 'msg_type', 'msg_type', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (125, 12, 'fix_session', 'fix_session', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (126, 13, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (127, 13, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (128, 13, 'session_id', 'session_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (129, 13, 'sender_seq_num', 'sender_seq_num', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (130, 13, 'target_seq_num', 'target_seq_num', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (131, 14, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (132, 14, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (133, 14, 'description', 'description', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (134, 14, 'name', 'name', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (135, 15, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (136, 15, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (137, 15, 'description', 'description', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (138, 15, 'name', 'name', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (139, 16, 'roles_id', 'roles_id', 'Key', 'metc_roles', NULL, true, true, false, false, NULL, true, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (140, 16, 'permissions_id', 'permissions_id', 'Key', 'metc_permissions', NULL, true, true, false, false, NULL, true, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (141, 17, 'role_id', 'role_id', 'Key', 'metc_roles', NULL, true, true, false, false, NULL, true, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (142, 17, 'subjects_id', 'subjects_id', 'Key', 'metc_users', NULL, true, true, false, false, NULL, true, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (143, 18, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (144, 18, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (145, 18, 'description', 'description', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (146, 18, 'name', 'name', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (147, 18, 'user_id', 'user_id', 'Key', 'metc_users', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (148, 19, 'supervisor_permission_id', 'supervisor_permission_id', 'Key', 'metc_supervisor_permissions', NULL, true, true, false, false, NULL, true, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (149, 19, 'permissions_id', 'permissions_id', 'Key', 'metc_permissions', NULL, true, true, false, false, NULL, true, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (150, 20, 'supervisor_permission_id', 'supervisor_permission_id', 'Key', 'metc_supervisor_permissions', NULL, true, true, false, false, NULL, true, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (151, 20, 'subjects_id', 'subjects_id', 'Key', 'metc_users', NULL, true, true, false, false, NULL, true, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (152, 21, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (153, 21, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (154, 21, 'attribute', 'attribute', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (155, 21, 'user_attribute_type', 'user_attribute_type', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (156, 21, 'user_id', 'user_id', 'Key', 'metc_users', NULL, false, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (157, 22, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (158, 22, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (159, 22, 'description', 'description', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (160, 22, 'name', 'name', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (161, 22, 'is_active', 'is_active', 'Bool', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (162, 22, 'password', 'password', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (163, 22, 'is_superuser', 'is_superuser', 'Bool', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (164, 22, 'user_data', 'user_data', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (165, 23, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (166, 23, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (167, 23, 'count', 'count', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (168, 23, 'duration_unit', 'duration_unit', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (169, 23, 'hour', 'hour', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (170, 23, 'm1', 'm1', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (171, 23, 'm15', 'm15', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (172, 23, 'm5', 'm5', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (173, 23, 'max', 'max', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (174, 23, 'mean', 'mean', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (175, 23, 'mean_rate', 'mean_rate', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (176, 23, 'median', 'median', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (177, 23, 'millis', 'millis', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (178, 23, 'min', 'min', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (179, 23, 'minute', 'minute', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (180, 23, 'name', 'name', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (181, 23, 'p75', 'p75', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (182, 23, 'p95', 'p95', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (183, 23, 'p98', 'p98', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (184, 23, 'p99', 'p99', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (185, 23, 'p999', 'p999', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (186, 23, 'rate_unit', 'rate_unit', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (187, 23, 'second', 'second', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (188, 23, 'std_dev', 'std_dev', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (189, 23, 'type', 'type', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (190, 23, 'value', 'value', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (191, 24, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (192, 24, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (193, 24, 'account', 'account', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (194, 24, 'avg_px', 'avg_px', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (195, 24, 'broker_id', 'broker_id', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (196, 24, 'cum_qty', 'cum_qty', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (197, 24, 'expiry', 'expiry', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (198, 24, 'last_px', 'last_px', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (199, 24, 'last_qty', 'last_qty', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (200, 24, 'leaves_qty', 'leaves_qty', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (201, 24, 'option_type', 'option_type', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (202, 24, 'order_id', 'order_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (203, 24, 'order_px', 'order_px', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (204, 24, 'order_qty', 'order_qty', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (205, 24, 'ord_status', 'ord_status', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (206, 24, 'root_order_id', 'root_order_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (207, 24, 'security_type', 'security_type', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (208, 24, 'side', 'side', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (209, 24, 'strike_price', 'strike_price', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (210, 24, 'symbol', 'symbol', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (211, 24, 'actor_id', 'actor_id', 'Key', 'metc_users', NULL, false, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (212, 24, 'report_id', 'report_id', 'Key', 'reports', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (213, 24, 'viewer_id', 'viewer_id', 'Key', 'metc_users', NULL, false, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (214, 25, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (215, 25, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (216, 25, 'broker_id', 'broker_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (217, 25, 'message_type', 'message_type', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (218, 25, 'msg_seq_num', 'msg_seq_num', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (219, 25, 'order_id', 'order_id', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (220, 25, 'sender_comp_id', 'sender_comp_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (221, 25, 'session_id', 'session_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (222, 25, 'target_comp_id', 'target_comp_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (223, 25, 'actor_id', 'actor_id', 'Key', 'metc_users', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (224, 25, 'fix_message_id', 'fix_message_id', 'Key', 'fix_messages', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (225, 26, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (226, 26, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (227, 26, 'expiry', 'expiry', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (228, 26, 'option_type', 'option_type', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (229, 26, 'position', 'position', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (230, 26, 'realized_gain', 'realized_gain', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (231, 26, 'security_type', 'security_type', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (232, 26, 'strike_price', 'strike_price', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (233, 26, 'symbol', 'symbol', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (234, 26, 'unrealized_gain', 'unrealized_gain', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (235, 26, 'weighted_average_cost', 'weighted_average_cost', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (236, 26, 'user_id', 'user_id', 'Key', 'metc_users', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (237, 27, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (238, 27, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (239, 27, 'allocated_quantity', 'allocated_quantity', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (240, 27, 'basis_price', 'basis_price', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (241, 27, 'gain', 'gain', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (242, 27, 'quantity', 'quantity', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (243, 27, 'trade_price', 'trade_price', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (244, 27, 'position_id', 'position_id', 'Key', 'pnl_positions', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (245, 27, 'trade_id', 'trade_id', 'Key', 'trades', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (246, 27, 'user_id', 'user_id', 'Key', 'metc_users', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (247, 28, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (248, 28, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (249, 28, 'expiry', 'expiry', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (250, 28, 'option_type', 'option_type', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (251, 28, 'position', 'position', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (252, 28, 'realized_gain', 'realized_gain', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (253, 28, 'security_type', 'security_type', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (254, 28, 'strike_price', 'strike_price', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (255, 28, 'symbol', 'symbol', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (256, 28, 'unrealized_gain', 'unrealized_gain', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (257, 28, 'weighted_average_cost', 'weighted_average_cost', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (258, 28, 'user_id', 'user_id', 'Key', 'metc_users', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (259, 29, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (260, 29, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (261, 29, 'basis_price', 'basis_price', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (262, 29, 'expiry', 'expiry', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (263, 29, 'option_type', 'option_type', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (264, 29, 'position', 'position', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (265, 29, 'realized_gain', 'realized_gain', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (266, 29, 'security_type', 'security_type', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (267, 29, 'strike_price', 'strike_price', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (268, 29, 'symbol', 'symbol', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (269, 29, 'unrealized_gain', 'unrealized_gain', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (270, 29, 'user_id', 'user_id', 'Key', 'metc_users', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (271, 30, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (272, 30, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (273, 30, 'broker_id', 'broker_id', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (274, 30, 'hierarchy', 'hierarchy', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (275, 30, 'originator', 'originator', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (276, 30, 'report_type', 'report_type', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (277, 30, 'msg_seq_num', 'msg_seq_num', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (278, 30, 'order_id', 'order_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (279, 30, 'report_id', 'report_id', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (280, 30, 'session_id', 'session_id', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (281, 30, 'actor_id', 'actor_id', 'Key', 'metc_users', NULL, false, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (282, 30, 'fix_message_id', 'fix_message_id', 'Key', 'fix_messages', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (283, 30, 'viewer_id', 'viewer_id', 'Key', 'metc_users', NULL, false, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (284, 30, 'text', 'text', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (285, 31, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (286, 31, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (287, 31, 'order_id', 'order_id', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (288, 31, 'expiry', 'expiry', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (289, 31, 'option_type', 'option_type', 'Integer', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (290, 31, 'price', 'price', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (291, 31, 'quantity', 'quantity', 'Float', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (292, 31, 'security_type', 'security_type', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (293, 31, 'strike_price', 'strike_price', 'Float', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (294, 31, 'symbol', 'symbol', 'String', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (295, 32, 'id', 'id', 'Integer', NULL, NULL, true, true, false, false, NULL, true, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (296, 32, 'update_count', 'update_count', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (297, 32, 'order_id', 'order_id', 'String', NULL, NULL, false, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (298, 32, 'side', 'side', 'Integer', NULL, NULL, true, false, false, false, NULL, NULL, NULL, NULL, '');
INSERT INTO _sc_fields VALUES (299, 32, 'pnl_id', 'pnl_id', 'Key', 'profit_and_loss', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (300, 32, 'trade_id', 'trade_id', 'Key', 'trades', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');
INSERT INTO _sc_fields VALUES (301, 32, 'user_id', 'user_id', 'Key', 'metc_users', NULL, true, false, false, false, NULL, NULL, 'id', 'Integer', '');


--
-- Data for Name: _sc_files; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_library; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_migrations; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO _sc_migrations VALUES ('202005141503');
INSERT INTO _sc_migrations VALUES ('202005241712');
INSERT INTO _sc_migrations VALUES ('202005251037');
INSERT INTO _sc_migrations VALUES ('202005282134');
INSERT INTO _sc_migrations VALUES ('202006022156');
INSERT INTO _sc_migrations VALUES ('202006051507');
INSERT INTO _sc_migrations VALUES ('202006240906');
INSERT INTO _sc_migrations VALUES ('202007091707');
INSERT INTO _sc_migrations VALUES ('202007202144');
INSERT INTO _sc_migrations VALUES ('202008031500');
INSERT INTO _sc_migrations VALUES ('202008051415');
INSERT INTO _sc_migrations VALUES ('202008121149');
INSERT INTO _sc_migrations VALUES ('202009112140');
INSERT INTO _sc_migrations VALUES ('202009181655');
INSERT INTO _sc_migrations VALUES ('202009221105');
INSERT INTO _sc_migrations VALUES ('202009231331');
INSERT INTO _sc_migrations VALUES ('202009301531');
INSERT INTO _sc_migrations VALUES ('202010231444');
INSERT INTO _sc_migrations VALUES ('202010251412');
INSERT INTO _sc_migrations VALUES ('202011021749');
INSERT INTO _sc_migrations VALUES ('202011051353');
INSERT INTO _sc_migrations VALUES ('202011111127');
INSERT INTO _sc_migrations VALUES ('202012011203');
INSERT INTO _sc_migrations VALUES ('202012100841');
INSERT INTO _sc_migrations VALUES ('202012281835');
INSERT INTO _sc_migrations VALUES ('202101061051');
INSERT INTO _sc_migrations VALUES ('202101141128');
INSERT INTO _sc_migrations VALUES ('202102091312');
INSERT INTO _sc_migrations VALUES ('202102101624');
INSERT INTO _sc_migrations VALUES ('202102172148');
INSERT INTO _sc_migrations VALUES ('202102261650');
INSERT INTO _sc_migrations VALUES ('202106102347');
INSERT INTO _sc_migrations VALUES ('202106112120');
INSERT INTO _sc_migrations VALUES ('202106120012');
INSERT INTO _sc_migrations VALUES ('202106120220');
INSERT INTO _sc_migrations VALUES ('202106121701');
INSERT INTO _sc_migrations VALUES ('202106121703');
INSERT INTO _sc_migrations VALUES ('202106251126');
INSERT INTO _sc_migrations VALUES ('202107281619');
INSERT INTO _sc_migrations VALUES ('202107302158');
INSERT INTO _sc_migrations VALUES ('202108022257');
INSERT INTO _sc_migrations VALUES ('202109201624');
INSERT INTO _sc_migrations VALUES ('202109301031');
INSERT INTO _sc_migrations VALUES ('202111290253');
INSERT INTO _sc_migrations VALUES ('202112282254');
INSERT INTO _sc_migrations VALUES ('202207022002');
INSERT INTO _sc_migrations VALUES ('202207252150');
INSERT INTO _sc_migrations VALUES ('202207261221');
INSERT INTO _sc_migrations VALUES ('202208101144');
INSERT INTO _sc_migrations VALUES ('202210051058');
INSERT INTO _sc_migrations VALUES ('202210101540');
INSERT INTO _sc_migrations VALUES ('202211040031');


--
-- Data for Name: _sc_pages; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_plugins; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO _sc_plugins VALUES (1, 'base', 'npm', '@saltcorn/base-plugin', 'latest', NULL, NULL);
INSERT INTO _sc_plugins VALUES (2, 'sbadmin2', 'npm', '@saltcorn/sbadmin2', 'latest', NULL, NULL);


--
-- Data for Name: _sc_roles; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO _sc_roles VALUES (1, 'admin');
INSERT INTO _sc_roles VALUES (10, 'public');
INSERT INTO _sc_roles VALUES (8, 'user');
INSERT INTO _sc_roles VALUES (4, 'staff');


--
-- Data for Name: _sc_session; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO _sc_session VALUES ('t-UMalTYL7-TlNafBPoVpVI_on4xnQ7A', '{"cookie":{"originalMaxAge":false,"expires":false,"httpOnly":true,"path":"/"},"csrfSecret":"dgNMLOSlyqFpsvi8VNLT6vpU","flash":{},"passport":{"user":{"email":"colin@marketcetera.com","id":1,"role_id":1,"language":null,"tenant":"public","last_mobile_login":null}}}', '2022-11-29 12:45:06');


--
-- Data for Name: _sc_snapshots; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_table_constraints; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_tables; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO _sc_tables VALUES (1, 'users', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (7, 'exec_reports', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (8, 'fix_messages', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (9, 'fix_session_attr_dscrptrs', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (10, 'fix_session_attributes', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (11, 'fix_sessions', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (12, 'incoming_fix_messages', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (13, 'message_store_sessions', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (14, 'metc_permissions', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (15, 'metc_roles', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (16, 'metc_roles_permissions', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (17, 'metc_roles_users', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (18, 'metc_supervisor_permissions', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (19, 'metc_supervisor_permissions_permissions', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (20, 'metc_supervisor_permissions_users', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (21, 'metc_user_attributes', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (22, 'metc_users', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (23, 'metrics', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (24, 'order_status', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (25, 'outgoing_messages', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (26, 'pnl_current_positions', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (27, 'pnl_lots', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (28, 'pnl_positions', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (29, 'profit_and_loss', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (30, 'reports', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (31, 'trades', 1, 1, false, NULL, '', NULL);
INSERT INTO _sc_tables VALUES (32, 'user_trades', 1, 1, false, NULL, '', NULL);


--
-- Data for Name: _sc_tag_entries; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_tags; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_tenants; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_triggers; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: _sc_views; Type: TABLE DATA; Schema: public; Owner: -
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: -
--

INSERT INTO users VALUES (1, 'colin@marketcetera.com', '$2a$10$4VMWBpegKszVcJio5IzrWeHQK2jBMnaDByqO8HPldOwUmiQ88OsFC', 1, NULL, NULL, NULL, false, NULL, NULL, NULL, NULL, NULL);


--
-- Name: _sc_errors_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_errors_id_seq', 1, false);


--
-- Name: _sc_event_log_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_event_log_id_seq', 1, false);


--
-- Name: _sc_fields_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_fields_id_seq', 301, true);


--
-- Name: _sc_files_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_files_id_seq', 1, false);


--
-- Name: _sc_library_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_library_id_seq', 1, false);


--
-- Name: _sc_pages_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_pages_id_seq', 2, true);


--
-- Name: _sc_plugins_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_plugins_id_seq', 2, true);


--
-- Name: _sc_roles_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_roles_id_seq', 1, false);


--
-- Name: _sc_snapshots_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_snapshots_id_seq', 1, false);


--
-- Name: _sc_table_constraints_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_table_constraints_id_seq', 1, false);


--
-- Name: _sc_tables_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_tables_id_seq', 32, true);


--
-- Name: _sc_tag_entries_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_tag_entries_id_seq', 1, false);


--
-- Name: _sc_tags_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_tags_id_seq', 1, false);


--
-- Name: _sc_triggers_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_triggers_id_seq', 1, false);


--
-- Name: _sc_views_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('_sc_views_id_seq', 1, true);


--
-- Name: hibernate_sequence; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('hibernate_sequence', 124, true);


--
-- Name: users_id_seq; Type: SEQUENCE SET; Schema: public; Owner: -
--

SELECT pg_catalog.setval('users_id_seq', 2, false);


--
-- Name: _sc_config _sc_config_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_config
    ADD CONSTRAINT _sc_config_pkey PRIMARY KEY (key);


--
-- Name: _sc_errors _sc_errors_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_errors
    ADD CONSTRAINT _sc_errors_pkey PRIMARY KEY (id);


--
-- Name: _sc_event_log _sc_event_log_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_event_log
    ADD CONSTRAINT _sc_event_log_pkey PRIMARY KEY (id);


--
-- Name: _sc_fields _sc_fields_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_fields
    ADD CONSTRAINT _sc_fields_pkey PRIMARY KEY (id);


--
-- Name: _sc_files _sc_files_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_files
    ADD CONSTRAINT _sc_files_pkey PRIMARY KEY (id);


--
-- Name: _sc_library _sc_library_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_library
    ADD CONSTRAINT _sc_library_name_key UNIQUE (name);


--
-- Name: _sc_library _sc_library_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_library
    ADD CONSTRAINT _sc_library_pkey PRIMARY KEY (id);


--
-- Name: _sc_migrations _sc_migrations_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_migrations
    ADD CONSTRAINT _sc_migrations_pkey PRIMARY KEY (migration);


--
-- Name: _sc_pages _sc_pages_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_pages
    ADD CONSTRAINT _sc_pages_pkey PRIMARY KEY (id);


--
-- Name: _sc_plugins _sc_plugins_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_plugins
    ADD CONSTRAINT _sc_plugins_pkey PRIMARY KEY (id);


--
-- Name: _sc_roles _sc_roles_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_roles
    ADD CONSTRAINT _sc_roles_pkey PRIMARY KEY (id);


--
-- Name: _sc_session _sc_session_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_session
    ADD CONSTRAINT _sc_session_pkey PRIMARY KEY (sid);


--
-- Name: _sc_snapshots _sc_snapshots_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_snapshots
    ADD CONSTRAINT _sc_snapshots_pkey PRIMARY KEY (id);


--
-- Name: _sc_table_constraints _sc_table_constraints_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_table_constraints
    ADD CONSTRAINT _sc_table_constraints_pkey PRIMARY KEY (id);


--
-- Name: _sc_tables _sc_tables_name_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tables
    ADD CONSTRAINT _sc_tables_name_key UNIQUE (name);


--
-- Name: _sc_tables _sc_tables_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tables
    ADD CONSTRAINT _sc_tables_pkey PRIMARY KEY (id);


--
-- Name: _sc_tag_entries _sc_tag_entries_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tag_entries
    ADD CONSTRAINT _sc_tag_entries_pkey PRIMARY KEY (id);


--
-- Name: _sc_tags _sc_tags_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tags
    ADD CONSTRAINT _sc_tags_pkey PRIMARY KEY (id);


--
-- Name: _sc_tenants _sc_tenants_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tenants
    ADD CONSTRAINT _sc_tenants_pkey PRIMARY KEY (subdomain);


--
-- Name: _sc_triggers _sc_triggers_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_triggers
    ADD CONSTRAINT _sc_triggers_pkey PRIMARY KEY (id);


--
-- Name: _sc_views _sc_views_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_views
    ADD CONSTRAINT _sc_views_pkey PRIMARY KEY (id);


--
-- Name: users users_email_key; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_email_key UNIQUE (email);


--
-- Name: users users_pkey; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: users users_unique_email; Type: CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_unique_email UNIQUE (email);


--
-- Name: _sc_IDX_session_expire; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX "_sc_IDX_session_expire" ON _sc_session USING btree (expire);


--
-- Name: _sc_idx_field_table; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX _sc_idx_field_table ON _sc_fields USING btree (table_id);


--
-- Name: _sc_idx_table_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX _sc_idx_table_name ON _sc_tables USING btree (name);


--
-- Name: _sc_idx_view_name; Type: INDEX; Schema: public; Owner: -
--

CREATE INDEX _sc_idx_view_name ON _sc_views USING btree (name);

--
-- Name: _sc_fields _sc_fields_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_fields
    ADD CONSTRAINT _sc_fields_table_id_fkey FOREIGN KEY (table_id) REFERENCES _sc_tables(id);


--
-- Name: _sc_files _sc_files_min_role_read_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_files
    ADD CONSTRAINT _sc_files_min_role_read_fkey FOREIGN KEY (min_role_read) REFERENCES _sc_roles(id);


--
-- Name: _sc_files _sc_files_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_files
    ADD CONSTRAINT _sc_files_user_id_fkey FOREIGN KEY (user_id) REFERENCES users(id);


--
-- Name: _sc_pages _sc_pages_min_role_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_pages
    ADD CONSTRAINT _sc_pages_min_role_fkey FOREIGN KEY (min_role) REFERENCES _sc_roles(id);


--
-- Name: _sc_table_constraints _sc_table_constraints_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_table_constraints
    ADD CONSTRAINT _sc_table_constraints_table_id_fkey FOREIGN KEY (table_id) REFERENCES _sc_tables(id) ON DELETE CASCADE;


--
-- Name: _sc_tables _sc_tables_min_role_read_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tables
    ADD CONSTRAINT _sc_tables_min_role_read_fkey FOREIGN KEY (min_role_read) REFERENCES _sc_roles(id);


--
-- Name: _sc_tables _sc_tables_min_role_write_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tables
    ADD CONSTRAINT _sc_tables_min_role_write_fkey FOREIGN KEY (min_role_write) REFERENCES _sc_roles(id);


--
-- Name: _sc_tables _sc_tables_ownership_field_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tables
    ADD CONSTRAINT _sc_tables_ownership_field_id_fkey FOREIGN KEY (ownership_field_id) REFERENCES _sc_fields(id);


--
-- Name: _sc_tag_entries _sc_tag_entries_page_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tag_entries
    ADD CONSTRAINT _sc_tag_entries_page_id_fkey FOREIGN KEY (page_id) REFERENCES _sc_pages(id);


--
-- Name: _sc_tag_entries _sc_tag_entries_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tag_entries
    ADD CONSTRAINT _sc_tag_entries_table_id_fkey FOREIGN KEY (table_id) REFERENCES _sc_tables(id);


--
-- Name: _sc_tag_entries _sc_tag_entries_tag_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tag_entries
    ADD CONSTRAINT _sc_tag_entries_tag_id_fkey FOREIGN KEY (tag_id) REFERENCES _sc_tags(id);


--
-- Name: _sc_tag_entries _sc_tag_entries_trigger_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tag_entries
    ADD CONSTRAINT _sc_tag_entries_trigger_id_fkey FOREIGN KEY (trigger_id) REFERENCES _sc_triggers(id);


--
-- Name: _sc_tag_entries _sc_tag_entries_view_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_tag_entries
    ADD CONSTRAINT _sc_tag_entries_view_id_fkey FOREIGN KEY (view_id) REFERENCES _sc_views(id);


--
-- Name: _sc_triggers _sc_triggers_min_role_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_triggers
    ADD CONSTRAINT _sc_triggers_min_role_fkey FOREIGN KEY (min_role) REFERENCES _sc_roles(id);


--
-- Name: _sc_triggers _sc_triggers_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_triggers
    ADD CONSTRAINT _sc_triggers_table_id_fkey FOREIGN KEY (table_id) REFERENCES _sc_tables(id);


--
-- Name: _sc_views _sc_views_min_role_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_views
    ADD CONSTRAINT _sc_views_min_role_fkey FOREIGN KEY (min_role) REFERENCES _sc_roles(id);


--
-- Name: _sc_views _sc_views_table_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY _sc_views
    ADD CONSTRAINT _sc_views_table_id_fkey FOREIGN KEY (table_id) REFERENCES _sc_tables(id);


--
-- Name: users users_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: -
--

ALTER TABLE ONLY users
    ADD CONSTRAINT users_role_id_fkey FOREIGN KEY (role_id) REFERENCES _sc_roles(id);
