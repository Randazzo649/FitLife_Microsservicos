package com.fitlife.usuario.infrastructure.adapter.out.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fitlife.usuario.application.port.out.JwtTokenPort;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyFactory;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.security.Signature;
import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtKeyService implements JwtTokenPort {
    private final KeyPair pair;
    private final ObjectMapper mapper;
    private final String issuer;
    private final long duration;

    public JwtKeyService(ObjectMapper mapper,
                         @Value("${fitlife.jwt.issuer}") String issuer,
                         @Value("${fitlife.jwt.duration-seconds:28800}") long duration,
                         @Value("${fitlife.jwt.private-key-base64:}") String privateKeyBase64,
                         @Value("${fitlife.jwt.public-key-base64:}") String publicKeyBase64) {
        this.mapper=mapper; this.issuer=issuer; this.duration=duration;
        try {
            boolean temPrivada = privateKeyBase64 != null && !privateKeyBase64.isBlank();
            boolean temPublica = publicKeyBase64 != null && !publicKeyBase64.isBlank();
            if (temPrivada != temPublica) {
                throw new IllegalStateException("Configure as duas variáveis JWT_PRIVATE_KEY_BASE64 e JWT_PUBLIC_KEY_BASE64.");
            }
            if (temPrivada) {
                KeyFactory factory = KeyFactory.getInstance("RSA");
                var privada = factory.generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKeyBase64)));
                var publica = factory.generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKeyBase64)));
                pair = new KeyPair(publica, privada);
            } else {
                KeyPairGenerator generator=KeyPairGenerator.getInstance("RSA");
                generator.initialize(2048);
                pair=generator.generateKeyPair();
            }
        } catch (GeneralSecurityException | IllegalArgumentException ex) {
            throw new IllegalStateException("Não foi possível inicializar as chaves JWT. Verifique as chaves RSA em Base64.", ex);
        }
    }

    @Override
    public String emitir(long usuarioId, String email) {
        try {
            Instant agora=Instant.now();
            Map<String,Object> header=Map.of("alg","RS256","typ","JWT","kid","fitlife-runtime-key");
            Map<String,Object> claims=new LinkedHashMap<>();
            claims.put("iss",issuer); claims.put("sub",Long.toString(usuarioId));
            claims.put("usuarioId",Long.toString(usuarioId)); claims.put("email",email);
            claims.put("iat",agora.getEpochSecond()); claims.put("exp",agora.plusSeconds(duration).getEpochSecond());
            claims.put("scope","fitlife.user");
            String input=encode(mapper.writeValueAsBytes(header))+"."+encode(mapper.writeValueAsBytes(claims));
            Signature signature=Signature.getInstance("SHA256withRSA");
            signature.initSign(pair.getPrivate()); signature.update(input.getBytes(StandardCharsets.US_ASCII));
            return input+"."+encode(signature.sign());
        } catch (Exception ex) { throw new IllegalStateException("Não foi possível emitir o token JWT.", ex); }
    }

    @Override
    public Map<String,Object> jwks() {
        RSAPublicKey key=(RSAPublicKey)pair.getPublic();
        Map<String,Object> jwk=new LinkedHashMap<>();
        jwk.put("kty","RSA"); jwk.put("use","sig"); jwk.put("alg","RS256");
        jwk.put("kid","fitlife-runtime-key"); jwk.put("n",encode(unsigned(key.getModulus())));
        jwk.put("e",encode(unsigned(key.getPublicExponent())));
        return Map.of("keys", List.of(jwk));
    }
    private byte[] unsigned(BigInteger value) { byte[] bytes=value.toByteArray(); return bytes.length>1&&bytes[0]==0?Arrays.copyOfRange(bytes,1,bytes.length):bytes; }
    private String encode(byte[] value) { return Base64.getUrlEncoder().withoutPadding().encodeToString(value); }
}
