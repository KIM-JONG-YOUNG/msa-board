package edu.jong.msa.board.common;

public final class BoardConstants {

	public static final class Packages {
		public static final String ROOT_PACKAGE 		= "edu.jong.msa.board";
		public static final String ENTITY_PACKAGE 		= "edu.jong.msa.board.entity";
		public static final String REPOSITORY_PACKAGE 	= "edu.jong.msa.board.repository";
		public static final String CLIENT_PACKAGE 		= "edu.jong.msa.board.client";
	}
	
	public static final class Patterns {
		public static final String DATE_FORMAT 		= "yyyy-MM-dd";
		public static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
		public static final String EMAIL_PATTERN 	= "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$";
	}
	
	public static final class URLPaths {
		public static final String MEMBER_URL = "/members";
	}
	
	public static final class CachingKeys {
		public static final String MEMBER_USERNAME_LOCK_KEY = "member-username::";
		public static final String MEMBER_LOCK_KEY 			= "member-lock::";
		public static final String MEMBER_KEY 				= "member::";
	}

	public static final class TopicNames {
		public static final String MEMBER_TOPIC = "member-topic";
	}
	
}
