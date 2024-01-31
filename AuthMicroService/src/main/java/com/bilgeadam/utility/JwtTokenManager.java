package com.bilgeadam.utility;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.bilgeadam.utility.enums.ERole;
import com.bilgeadam.utility.enums.EState;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class JwtTokenManager {
    /**
     * SecretKey -> Şifreleme için özel bir anahtar.
     * Issuer -> jwt yi oluşturan, sahiplik
     * IssuerAt -> jwt nin oluşturulma zamanı
     * ExpiresAt -> jwt nin geçerlilik son zamanı
     * Sign -> jwt nin imzalanması yani bir şifreleme algoritması ile şifrelenmesi.
     */
    @Value("${my-application.secret-key}")
    private  String SECRETKEY;
    private final String ISSUER = "Java11BoostAuth";
    private final Long EXDATE = 1000L*60*45; // 45 Dakika
    /**
     * Kullanıcının authId si alınarak yeni bir jwt token üretilir.
     *
     * @param authId
     * @return
     */
    public Optional<String> createToken(Long authId, ERole role, EState state){
        String token;
        try{
            token = JWT.create()
                    .withAudience()
                    .withClaim("authId",authId) // DİKKAT!!! buralara(Claim) eklediğiniz datalar şifrelenmez
                    .withClaim("role",role.toString())
                    .withClaim("state",state.toString())
                    .withIssuer(ISSUER) // jwt yi üreten sahiplik
                    .withIssuedAt(new Date()) // jwt üretilme zamanı
                    .withExpiresAt(new Date(System.currentTimeMillis()+EXDATE))// jwt nin sona erme tarihi
                    .sign(Algorithm.HMAC512(SECRETKEY));
            return Optional.of(token);
        }catch (Exception exception){
            return Optional.empty();
        }
    }

    /**
     *
     * @param token
     * @return
     */
    public boolean validateToken(String token){
        try{
            /**
             * Şifrelediğimiz token için şifreyi çözme ve doğrulama işlemi için
             * alogitmayı tanımlıyoruz.
             */
            Algorithm algorithm = Algorithm.HMAC512(SECRETKEY);
            /**
             * token' ı doğrulayabilmek için algoritayı kullanarak token
             * sahipliğini giriyoruz.
             */
            JWTVerifier verifier = JWT.require(algorithm)
                                      .withIssuer(ISSUER).build();
            /**
             * verifier ile token' ı çözüyoruz.
             */
            DecodedJWT decodedJWT = verifier.verify(token);
            /**
             * Eğer ilgili token çözülememiş ise false dönülür.
             * 1-> token yanlış gelmiş olabilir.
             * 2-> token süresi dolmuş olabilir.
             * 3-> farklı bir sahiplik gönderilmiş olabilir.
             */
            if(decodedJWT==null)
                return false;
        }catch (Exception exception){
            return false;
        }
        return  true;
    }

    /**
     * token verip kime ait olduğunu dönuyoruz.
     * @param token
     * @return
     */
    public Optional<Long> getIdByToken(String token){
        try{
            Algorithm algorithm = Algorithm.HMAC512(SECRETKEY);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer(ISSUER).build();
            DecodedJWT decodedJWT = verifier.verify(token);
            if(decodedJWT == null)
                return Optional.empty();
            Long authId = decodedJWT.getClaim("authId").asLong();
            return Optional.of(authId);
        }catch (Exception exception){
            return  Optional.empty();
        }
    }
}
