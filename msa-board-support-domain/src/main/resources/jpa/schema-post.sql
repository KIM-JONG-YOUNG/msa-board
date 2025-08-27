CREATE TABLE tb_post
(
    id                BINARY(16)   NOT NULL,
    created_date_time datetime     NOT NULL,
    updated_date_time datetime     NOT NULL,
    state             SMALLINT     NOT NULL,
    post_title        VARCHAR(300) NOT NULL,
    post_content      LONGTEXT     NOT NULL,
    post_writer_id    BINARY(16)   NOT NULL,
    post_views        INT          NOT NULL,
    CONSTRAINT pk_tb_post PRIMARY KEY (id)
);

CREATE INDEX idx_post_writer_id ON tb_post (post_writer_id);
CREATE INDEX idx_post_created_date_time ON tb_post (created_date_time);
CREATE INDEX idx_post_updated_date_time ON tb_post (updated_date_time);

ALTER TABLE tb_post
    ADD CONSTRAINT FK_POST_WRITER_ID FOREIGN KEY (post_writer_id) REFERENCES tb_member (id);

