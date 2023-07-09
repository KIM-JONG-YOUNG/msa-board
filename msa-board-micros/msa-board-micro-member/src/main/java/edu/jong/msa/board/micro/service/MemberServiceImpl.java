package edu.jong.msa.board.micro.service;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import edu.jong.msa.board.client.request.MemberParam;
import edu.jong.msa.board.client.response.MemberDetails;
import edu.jong.msa.board.common.BoardConstants.CachingKeys;
import edu.jong.msa.board.common.utils.ObjectMapperUtils;
import edu.jong.msa.board.domain.entity.MemberEntity;
import edu.jong.msa.board.domain.repository.MemberEntityRepository;
import edu.jong.msa.board.micro.mapper.MemberEntityMapper;
import edu.jong.msa.board.redis.service.RedissonLockService;
import edu.jong.msa.board.web.exception.ParamValidException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl implements MemberService {

	private final PasswordEncoder encoder;
	
	private final MemberEntityMapper mapper;

	private final MemberEntityRepository repository;
	
	private final RedissonLockService lockService;
	
	private final RedisTemplate<String, String> redisTemplate;
	
	private MemberDetails caching(MemberDetails details) {
		
		String cachingKey = CachingKeys.MEMBER_KEY + details.getId();
		String cachingVal = ObjectMapperUtils.toJson(details);
		
		redisTemplate.opsForValue().set(cachingKey, cachingVal, 60, TimeUnit.SECONDS);

		return details;
	}
	
	@Transactional
	@Override
	public UUID createMember(MemberParam param) {
		
		if (repository.existsByUsername(param.getUsername())) {

			String lockKey = CachingKeys.MEMBER_USERNAME_LOCK_KEY + param.getUsername();
			
			return lockService.excute(lockKey, 5, 3, () -> {
				
				MemberEntity entity = repository.save(
						mapper.encodeEntity(encoder, mapper.toEntity(param)));

				return caching(mapper.toDetails(entity)).getId();
			});
		} else {
			throw new EntityExistsException("이미 계정이 존재합니다.");
		}
	}

	@Transactional
	@Override
	public UUID modifyMember(UUID id, MemberParam param) {
		
		MemberEntity entity = repository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

		String lockKey = CachingKeys.MEMBER_LOCK_KEY + id;

		return lockService.excute(lockKey, 5, 3, () -> {
			
			MemberEntity updatedEntity = null;
			
			if (StringUtils.isBlank(param.getPassword())) {
				updatedEntity = repository.save(
						mapper.updateEntity(param, entity));
			} else {
				updatedEntity = repository.save(
						mapper.encodeEntity(encoder, mapper.updateEntity(param, entity)));
			}
			
			return caching(mapper.toDetails(updatedEntity)).getId();
		});
	}

	@Transactional(readOnly = true)
	@Override
	public MemberDetails getMember(UUID id) {
		try {
			
			String cachingKey = CachingKeys.MEMBER_KEY + id;
			String cachingVal = redisTemplate.opsForValue().get(cachingKey);
			
			return ObjectMapperUtils.toObject(cachingVal, MemberDetails.class);
		} catch (Exception e) {
			
			MemberEntity entity = repository.findById(id)
					.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));

			return caching(mapper.toDetails(entity));
		}
	}

	@Transactional(readOnly = true)
	@Override
	public MemberDetails loginMember(MemberParam param) {
		
		MemberEntity entity = repository.findByUsername(param.getUsername())
				.orElseThrow(() -> new EntityNotFoundException("존재하지 않는 회원입니다."));
		
		if (encoder.matches(param.getPassword(), entity.getPassword())) {
			return mapper.toDetails(entity);
		} else {
			throw new ParamValidException("비밀번호가 일치하지 않습니다.");
		}
	}

}
