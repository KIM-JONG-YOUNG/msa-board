package com.jong.msa.board.microservice.search.response;

import java.util.List;

public interface SearchResponse<Item> {

    long totalCount();

    List<Item> list();

}
