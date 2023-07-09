package edu.jong.msa.board.micro.service;

import java.util.UUID;

import edu.jong.msa.board.client.request.MemberParam;
import edu.jong.msa.board.client.response.MemberDetails;

public interface MemberService {

	UUID createMember(MemberParam param);

	UUID modifyMember(UUID id, MemberParam param);

	MemberDetails getMember(UUID id);

	MemberDetails loginMember(MemberParam param);

}
