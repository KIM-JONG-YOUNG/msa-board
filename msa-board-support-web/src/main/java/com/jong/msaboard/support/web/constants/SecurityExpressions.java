package com.jong.msaboard.support.web.constants;

public class SecurityExpressions {

    public static final String IS_ANONYMOUS = "isAnonymous()";
    public static final String IS_ADMIN = "hasRole('ROLE_ADMIN')";
    public static final String IS_ADMIN_OR_USER = "hasAnyRole('ROLE_USER','ROLE_ADMIN')";

}
