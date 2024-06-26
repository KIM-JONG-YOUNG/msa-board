package com.jong.msa.board.client.member.request;

import javax.validation.constraints.NotNull;

import com.jong.msa.board.common.constants.Patterns;
import com.jong.msa.board.common.enums.CodeEnum.Gender;
import com.jong.msa.board.common.enums.CodeEnum.Group;
import com.jong.msa.board.core.validation.annotation.StringValidate;
import com.jong.msa.board.core.validation.annotation.StringValidate.BlankCheck;
import com.jong.msa.board.core.validation.annotation.StringValidate.PatternCheck;
import com.jong.msa.board.core.validation.annotation.StringValidate.SizeCheck;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PROTECTED)
public class CreateMemberRequest {

	@Schema(description = "계정", example = "username")
	@StringValidate(
			blankCheck = @BlankCheck(message = "계정은 비어있을 수 없습니다."),
			sizeCheck = @SizeCheck(max = 30, message = "계정은 30자를 초과할 수 없습니다."),
			patternCheck = @PatternCheck(regexp = Patterns.USERNAME_PATTERN, message = "계정이 형식에 맞지 않습니다."))
	private String username;
	
	@Schema(description = "비밀번호" , example = "password")
	@StringValidate(
			blankCheck = @BlankCheck(message = "비밀번호는 비어있을 수 없습니다."))
	private String password;
	
	@Schema(description = "이름", example = "name")
	@StringValidate(
			blankCheck = @BlankCheck(message = "이름은 비어있을 수 없습니다."),
			sizeCheck = @SizeCheck(max = 30, message = "이름은 30자를 초과할 수 없습니다."))
	private String name;
	
	@Schema(description = "성별", example = "MAIL")
	@NotNull(message = "성별은 비어있을 수 없습니다.")
	private Gender gender;
	
	@Schema(description = "이메일", example = "test@example.com")
	@StringValidate(
			blankCheck = @BlankCheck(message = "이메일은 비어있을 수 없습니다."),
			sizeCheck = @SizeCheck(max = 60, message = "이메일은 60자를 초과할 수 없습니다."),
			patternCheck = @PatternCheck(regexp = Patterns.EMAIL_PATTERN, message = "이메일이 형식에 맞지 않습니다."))
	private String email;
	
	@Schema(description = "그룹", example = "ADMIN")
	@NotNull(message = "그룹은 비어있을 수 없습니다.")
	private Group group;
	
}
