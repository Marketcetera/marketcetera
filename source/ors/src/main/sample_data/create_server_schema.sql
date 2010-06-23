DROP TABLE IF EXISTS id_repository;

DROP TABLE IF EXISTS execreports;

DROP TABLE IF EXISTS reports;

DROP TABLE IF EXISTS ors_users;

CREATE TABLE ors_users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lastUpdated DATETIME,
    updateCount INTEGER NOT NULL,
    description VARCHAR(255),
    name VARCHAR(255) NOT NULL,
    active BIT NOT NULL,
    hashedPassword VARCHAR(255) NOT NULL,
    superuser BIT NOT NULL,
    userdata text,
    systemdata text,
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE reports (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lastUpdated DATETIME,
    updateCount INTEGER NOT NULL,
    brokerID VARCHAR(255),
    reportID BIGINT NOT NULL,
    fixMessage TEXT NOT NULL,
    originator INTEGER,
    reportType INTEGER NOT NULL,
    sendingTime DATETIME NOT NULL,
    orderID VARCHAR(255) NOT NULL,
    viewer_id BIGINT,
    actor_id BIGINT,
    PRIMARY KEY (id),
    INDEX idx_sendingTime (sendingTime),
    INDEX idx_orderID (orderID),
    INDEX idx_viewer_id (viewer_id),
    CONSTRAINT fk_reports_actor_id FOREIGN KEY (actor_id)
     REFERENCES ors_users(id),
    CONSTRAINT fk_reports_viewer_id FOREIGN KEY (viewer_id)
     REFERENCES ors_users(id)
);

CREATE TABLE execreports (
    id BIGINT NOT NULL AUTO_INCREMENT,
    lastUpdated DATETIME,
    updateCount INTEGER NOT NULL,
    avgPrice NUMERIC(15,5) NOT NULL,
    cumQuantity NUMERIC(15,5) NOT NULL,
    lastPrice NUMERIC(15,5),
    lastQuantity NUMERIC(15,5),
    orderID VARCHAR(255) NOT NULL,
    viewer_id BIGINT,
    orderStatus INTEGER NOT NULL,
    origOrderID VARCHAR(255),
    rootID VARCHAR(255) NOT NULL,
    sendingTime DATETIME NOT NULL,
    side INTEGER NOT NULL,
    securityType INTEGER,
    symbol VARCHAR(255) NOT NULL,
    expiry VARCHAR(255),
    strikePrice NUMERIC(15,5),
    optionType INTEGER,
    account VARCHAR(255),
    report_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    INDEX idx_report_id (report_id),
    INDEX idx_securityType (securityType),
    INDEX idx_symbol (symbol),
    INDEX idx_expiry (expiry),
    INDEX idx_strikePrice (strikePrice),
    INDEX idx_optionType (optionType),
    INDEX idx_sendingTime (sendingTime),
    INDEX idx_orderID (orderID),
    INDEX idx_viewer_id (viewer_id),
    INDEX idx_rootID (rootID),
    CONSTRAINT fk_execreports_viewer_id FOREIGN KEY (viewer_id)
     REFERENCES ors_users(id),
    CONSTRAINT fk_execreports_report_id FOREIGN KEY (report_id)
     REFERENCES reports(id)
);

CREATE TABLE id_repository (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nextAllowedID BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
