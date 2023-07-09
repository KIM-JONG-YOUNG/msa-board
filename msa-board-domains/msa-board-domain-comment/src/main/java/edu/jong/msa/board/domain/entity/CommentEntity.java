package edu.jong.msa.board.domain.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Builder
@Table(name = "tb_comment")
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CommentEntity extends BaseEntity {

	@Id
	@Builder.Default
	@Column(name = "comment_id",
			columnDefinition = "BINARY(16)")
	private UUID id = UUID.randomUUID();
	
	@Setter
	@Column(name = "comment_content",
			length = 500,
			nullable = false)
	private String content;
	
	@Column(name = "parent_id",
			columnDefinition = "BINARY(16)",
			nullable = false)
	private UUID parentId;

	@Column(name = "post_id",
			columnDefinition = "BINARY(16)",
			nullable = true)
	private UUID postId;

	@Column(name = "writer_id",
			columnDefinition = "BINARY(16)",
			nullable = true)
	private UUID writerId;
	
}
