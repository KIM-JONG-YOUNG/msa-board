package com.jong.msa.board.support.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
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
    name = "tb_post",
    indexes = {
        @Index(name = "idx_post_writer_id", columnList = "post_writer_id"),
        @Index(name = "idx_post_created_date_time", columnList = "created_date_time"),
        @Index(name = "idx_post_updated_date_time", columnList = "updated_date_time")
    })
public class PostEntity extends AbstractBaseEntity<PostEntity> {

    @Setter
    @Column(name = "post_title", length = 300, nullable = false)
    private String title;

    @Setter
    @Column(name = "post_content", columnDefinition = "LONGTEXT", nullable = false)
    private String content;

    @With
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
        name = "post_writer_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_post_writer_id")
    )
    private MemberEntity writer;

    @Column(name = "post_views", nullable = false)
    private int views;

    @PrePersist
    @PreUpdate
    public void beforeSave() {
        this.views = this.views > 0 ? this.views : 0;
    }

    public PostEntity increaseView() {
        this.views = this.views + 1;
        return this;
    }

}
