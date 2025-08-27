package com.jong.msa.board.support.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.With;
import lombok.experimental.Accessors;

@Entity
@Getter
@Builder
@Accessors(chain = true)
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
    name = "tb_comment",
    indexes = {
        @Index(name = "idx_comment_writer_id", columnList = "comment_writer_id"),
        @Index(name = "idx_comment_post_id", columnList = "comment_post_id"),
        @Index(name = "idx_comment_parent_id", columnList = "comment_parent_id"),
        @Index(name = "idx_comment_created_date_time", columnList = "created_date_time"),
        @Index(name = "idx_comment_updated_date_time", columnList = "updated_date_time")
    })
public class CommentEntity extends AbstractBaseEntity<CommentEntity> {

    @Setter
    @Column(name = "comment_content", length = 500, nullable = false)
    private String content;

    @With
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "comment_writer_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_comment_writer_id")
    )
    private MemberEntity writer;

    @With
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "comment_post_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_comment_post_id")
    )
    private PostEntity post;

    @With
    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(
        name = "comment_parent_id",
        foreignKey = @ForeignKey(name = "fk_comment_parent_id"))
    private CommentEntity parent;

}
