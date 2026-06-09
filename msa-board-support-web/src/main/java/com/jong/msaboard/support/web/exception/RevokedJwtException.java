package com.jong.msaboard.support.web.exception;

import io.jsonwebtoken.JwtException;

public class RevokedJwtException extends JwtException {

    public RevokedJwtException(String message) {
        super(message);
    }

}
