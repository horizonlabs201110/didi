CREATE TABLE `ofhcmessage` (
   `id` BIGINT NOT NULL AUTO_INCREMENT,
   `type` INT NOT NULL,
   `to` VARCHAR(64) NOT NULL,
   `from` VARCHAR(64) NOT NULL,
   `status` INT NOT NULL,
   `statusMessage` VARCHAR(2048) DEFAULT NULL,
   `stanza` TEXT NOT NULL,
   `lastModified` BIGINT NOT NULL,
   PRIMARY KEY (`id`)
);
INSERT INTO `ofversion`(`name`, `version`) values ('hcpushforward', 1);