# ElasticSearch 공식 이미지 사용
FROM docker.elastic.co/elasticsearch/elasticsearch:8.12.1

# nori plugin 설치
RUN bin/elasticsearch-plugin install analysis-nori

# 실행
CMD ["bin/elasticsearch"]
