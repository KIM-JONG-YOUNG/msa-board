CREATE TABLE tb_comment
(
    id                BINARY(16)   NOT NULL,
    created_date_time datetime     NOT NULL,
    updated_date_time datetime     NOT NULL,
    state             SMALLINT     NOT NULL,
    comment_content   VARCHAR(500) NOT NULL,
    comment_writer_id BINARY(16)   NOT NULL,
    comment_post_id   BINARY(16)   NOT NULL,
    comment_parent_id BINARY(16)   NULL,
    CONSTRAINT pk_tb_comment PRIMARY KEY (id)
);

CREATE INDEX idx_comment_writer_id ON tb_comment (comment_writer_id);
CREATE INDEX idx_comment_post_id ON tb_comment (comment_post_id);
CREATE INDEX idx_comment_parent_id ON tb_comment (comment_parent_id);
CREATE INDEX idx_comment_created_date_time ON tb_comment (created_date_time);
CREATE INDEX idx_comment_updated_date_time ON tb_comment (updated_date_time);

ALTER TABLE tb_comment
    ADD CONSTRAINT FK_COMMENT_PARENT_ID FOREIGN KEY (comment_parent_id) REFERENCES tb_comment (id);
ALTER TABLE tb_comment
    ADD CONSTRAINT FK_COMMENT_POST_ID FOREIGN KEY (comment_post_id) REFERENCES tb_post (id);
ALTER TABLE tb_comment
    ADD CONSTRAINT FK_COMMENT_WRITER_ID FOREIGN KEY (comment_writer_id) REFERENCES tb_member (id);

