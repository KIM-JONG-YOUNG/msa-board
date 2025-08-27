CREATE TABLE tb_member
(
    id                BINARY(16)  NOT NULL,
    created_date_time datetime    NOT NULL,
    updated_date_time datetime    NOT NULL,
    state             SMALLINT    NOT NULL,
    member_username   VARCHAR(30) NOT NULL,
    member_password   VARCHAR(60) NOT NULL,
    member_name       VARCHAR(30) NOT NULL,
    member_gender     CHAR(1)     NOT NULL,
    member_email      VARCHAR(60) NOT NULL,
    member_group      SMALLINT    NOT NULL,
    CONSTRAINT pk_tb_member PRIMARY KEY (id)
);

CREATE INDEX idx_member_created_date_time ON tb_member (created_date_time);
CREATE INDEX idx_member_updated_date_time ON tb_member (updated_date_time);

ALTER TABLE tb_member
    ADD CONSTRAINT uk_member_username UNIQUE (member_username);

