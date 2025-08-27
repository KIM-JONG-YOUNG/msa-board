package com.jong.msa.board.platform.gateway.exception;

import io.jsonwebtoken.JwtException;

public class RevokedJwtException extends JwtException {

    public RevokedJwtException(String message) {
        super(message);
    }

}
