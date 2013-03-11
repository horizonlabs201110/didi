CREATE TABLE ofHCMessages (
   id BIGINT NOT NULL AUTO_INCREMENT,
   type INT NOT NULL,
   toUserName VARCHAR(64) NOT NULL,
   fromUserName VARCHAR(64) NOT NULL,
   status INT NOT NULL,
   stanza TEXT NOT NULL,
   lastModified CHAR(15) NOT NULL,
   PRIMARY KEY (id)
);
INSERT INTO ofVersion(name,version) values('hcpushforward', 1);