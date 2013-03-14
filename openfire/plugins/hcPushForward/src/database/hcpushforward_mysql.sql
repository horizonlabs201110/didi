CREATE TABLE ofHCMessages (
   id BIGINT NOT NULL AUTO_INCREMENT,
   type INT NOT NULL,
   to VARCHAR(64) NOT NULL,
   from VARCHAR(64) NOT NULL,
   status INT NOT NULL,
   statusMessage VARCHAR(1024),
   stanza TEXT NOT NULL,
   lastModified BIGINT NOT NULL,
   PRIMARY KEY (id)
);
INSERT INTO ofVersion(name, version) values('hcPushForward', 1);