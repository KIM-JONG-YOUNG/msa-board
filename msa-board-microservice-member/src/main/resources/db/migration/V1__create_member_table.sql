CREATE TABLE `tb_member`
(
    `id`                CHAR(36)    NOT NULL,
    `username`          VARCHAR(30) NOT NULL,
    `password`          VARCHAR(60) NOT NULL,
    `name`              VARCHAR(30) NOT NULL,
    `gender`            CHAR(1)     NOT NULL,
    `email`             VARCHAR(60) NOT NULL,
    `group`             TINYINT     NOT NULL,
    `created_date_time` datetime    NOT NULL,
    `updated_date_time` datetime    NOT NULL,
    `status`            TINYINT     NOT NULL,
    CONSTRAINT `pk_tb_member` PRIMARY KEY (`id`)
);

ALTER TABLE `tb_member`
    ADD CONSTRAINT `uc_tb_member_username` UNIQUE (`username`);
