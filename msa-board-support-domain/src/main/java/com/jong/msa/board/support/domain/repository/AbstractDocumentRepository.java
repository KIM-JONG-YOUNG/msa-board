package com.jong.msa.board.support.domain.repository;

import java.util.UUID;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractDocumentRepository<T> extends ElasticsearchRepository<T, UUID> {}
