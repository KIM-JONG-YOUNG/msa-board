package com.jong.msa.board.common.constants;

public final class MicroserviceNames {

    public static final String MEMBER_MICROSERVICE = "microservice-member";
    public static final String POST_MICROSERVICE = "microservice-post";
    public static final String COMMENT_MICROSERVICE = "microservice-comment";
    public static final String SEARCH_MICROSERVICE = "microservice-search";

    public static final String LB_MEMBER_MICROSERVICE = "lb::" + MEMBER_MICROSERVICE;
    public static final String LB_POST_MICROSERVICE = "lb::" + POST_MICROSERVICE;
    public static final String LB_COMMENT_MICROSERVICE = "lb::" + COMMENT_MICROSERVICE;
    public static final String LB_SEARCH_MICROSERVICE = "lb::" + SEARCH_MICROSERVICE;

}
