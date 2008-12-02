DROP TABLE IF EXISTS id_repository;

CREATE TABLE id_repository (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nextAllowedID BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);
