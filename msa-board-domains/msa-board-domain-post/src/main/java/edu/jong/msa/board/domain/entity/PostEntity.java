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
@Table(name = "tb_post")
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class PostEntity extends BaseEntity {

	@Id
	@Builder.Default
	@Column(name = "post_id",
			columnDefinition = "BINARY(16)")
	private UUID id = UUID.randomUUID();
	
	@Setter
	@Column(name = "post_title",
			length = 300,
			nullable = false)
	private String title;
	
	@Setter
	@Column(name = "post_content",
			columnDefinition = "TEXT",
			nullable = false)
	private String content;
	
	@Builder.Default
	@Column(name = "post_views",
			nullable = false)
	private int views = 0;

	@Column(name = "writer_id",
			columnDefinition = "BINARY(16)",
			nullable = false)
	private UUID writerId;

	public PostEntity increaseViews() {
		this.views = this.views + 1;
		return this;
	}

}
