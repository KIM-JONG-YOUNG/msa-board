package edu.jong.msa.board.micro.validator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Component;

import edu.jong.msa.board.client.request.MemberParam;
import edu.jong.msa.board.common.BoardConstants.Patterns;
import edu.jong.msa.board.web.exception.ParamValidException;

@Component
public class MemberParamValidator implements ParamValidator<MemberParam> {

	public interface CreateValidGroup extends ParamValidGroup {}
	public interface ModifyValidGroup extends ParamValidGroup {}
	public interface LoginValidGroup extends ParamValidGroup {}
	
	@Override
	public <T extends ParamValidGroup> void validate(MemberParam param, Class<T> validGroup) {

		List<String> validMessageList = null;

		if (validGroup.isAssignableFrom(CreateValidGroup.class)) {
			validMessageList = validateCreateParam(param);
		} else if (validGroup.isAssignableFrom(ModifyValidGroup.class)) {
			validMessageList = validateModifyParam(param);
		} else if (validGroup.isAssignableFrom(LoginValidGroup.class)) {
			validMessageList = validateLoginParam(param);
		} else {
			throw new ParamValidException("Not exists valid group.");
		}
		
		if (validMessageList.size() > 0) {
			throw new ParamValidException(validMessageList);
		}
	}

	private List<String> validateCreateParam(MemberParam param) {

		List<String> validMessageList = new ArrayList<>();
		
		if (isBlank(param.getUsername())) {
			validMessageList.add("계정은 비어있을 수 없습니다.");
		} else {
			if (isOver(param.getUsername(), 30)) {
				validMessageList.add("계정은 30자를 넘을 수 없습니다.");
			}
		}
		
		if (isBlank(param.getPassword())) {
			validMessageList.add("비밀번호는 비어있을 수 없습니다.");
		}
		
		if (isBlank(param.getName())) {
			validMessageList.add("이름은 비어있을 수 없습니다.");
		} else {
			if (isOver(param.getName(), 30)) {
				validMessageList.add("이름은 30자를 넘을 수 없습니다.");
			}
		}
		
		if (null == param.getGender()) {
			validMessageList.add("성별은 비어있을 수 없습니다.");
		}

		if (isBlank(param.getEmail())) {
			validMessageList.add("이메일은 비어있을 수 없습니다.");
		} else {
			if (isOver(param.getEmail(), 60)) {
				validMessageList.add("이메일은 60자를 넘을 수 없습니다.");
			} else {
				if (isNotMatch(param.getEmail(), Patterns.EMAIL_PATTERN)) {
					validMessageList.add("이메일 형식이 맞지 않습니다.");
				}
			}
		}
		
		if (null == param.getGroup()) {
			validMessageList.add("그룹은 비어있을 수 없습니다.");
		}

		if (null == param.getState()) {
			validMessageList.add("상태는 비어있을 수 없습니다.");
		}

		return validMessageList;
	}

	private List<String> validateModifyParam(MemberParam param) {
		
		List<String> validMessageList = new ArrayList<>();
		
		if (!isBlank(param.getName())) {
			if (isOver(param.getName(), 30)) {
				validMessageList.add("이름은 30자를 넘을 수 없습니다.");
			}
		}
		
		if (!isBlank(param.getEmail())) {
			if (isOver(param.getEmail(), 60)) {
				validMessageList.add("이메일은 60자를 넘을 수 없습니다.");
			} else {
				if (isNotMatch(param.getEmail(), Patterns.EMAIL_PATTERN)) {
					validMessageList.add("이메일 형식이 맞지 않습니다.");
				}
			}
		}
		
		return validMessageList;
	}

	private List<String> validateLoginParam(MemberParam param) {
		
		List<String> validMessageList = new ArrayList<>();

		if (isBlank(param.getUsername())) {
			validMessageList.add("계정은 비어있을 수 없습니다.");
		} else {
			if (isOver(param.getUsername(), 30)) {
				validMessageList.add("계정은 30자를 넘을 수 없습니다.");
			}
		}
		
		if (isBlank(param.getPassword())) {
			validMessageList.add("비밀번호는 비어있을 수 없습니다.");
		}
		
		return validMessageList;
	}

}
