package com.fitlife.usuario.infrastructure.adapter.in.web;

import com.fitlife.usuario.application.port.out.JwtTokenPort;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JwksController {
    private final JwtTokenPort tokens;
    public JwksController(JwtTokenPort tokens) { this.tokens=tokens; }
    @GetMapping("/oauth2/jwks")
    public Map<String,Object> jwks() { return tokens.jwks(); }
}
