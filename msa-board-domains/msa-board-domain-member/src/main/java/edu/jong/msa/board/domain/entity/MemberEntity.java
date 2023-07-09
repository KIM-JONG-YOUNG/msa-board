package edu.jong.msa.board.domain.entity;

import java.util.UUID;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Converter;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import edu.jong.msa.board.common.type.DBCodeEnum.Gender;
import edu.jong.msa.board.common.type.DBCodeEnum.Group;
import edu.jong.msa.board.domain.converter.DBCodeAttributeConverter;
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
@Table(name = "tb_member")
@ToString(callSuper = true)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberEntity extends BaseEntity {

	@Id
	@Builder.Default
	@Column(name = "member_id",
			columnDefinition = "BINARY(16)")
	private UUID id = UUID.randomUUID();
	
	@Column(name = "member_username",
			length = 30,
			unique = true,
			nullable = false)
	private String username;
	
	@Setter
	@Column(name = "member_password",
			length = 60,
			nullable = false)
	private String password;
	
	@Setter
	@Column(name = "member_name",
			length = 30,
			nullable = false)
	private String name;
	
	@Setter
	@Convert(converter = GenderAttributeConverter.class)
	@Column(name = "member_gender",
			length = 1,
			nullable = false)
	private Gender gender;
	
	@Setter
	@Column(name = "member_email",
			length = 60,
			nullable = false)
	private String email;
	
	@Convert(converter = GroupAttributeConverter.class)
	@Column(name = "member_group",
			length = 1,
			nullable = false)
	private Group group;
	
	@Converter
	public static class GenderAttributeConverter extends DBCodeAttributeConverter<Gender, Character> {

		public GenderAttributeConverter() {
			super(Gender.class, false);
		}
	}
	
	@Converter
	public static class GroupAttributeConverter extends DBCodeAttributeConverter<Group, Integer> {

		public GroupAttributeConverter() {
			super(Group.class, false);
		}
	}
	
}