package com.bilgeadam.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public enum ErrorType {

    USERNAME_PASSWORD_ERROR(1001, "Kullanıcı adı ya da şifre hatalıdır.", HttpStatus.BAD_REQUEST),
    BAD_REQUEST_ERROR(3001, "Girilen bilgiler Hatalı, kontrol ederek tekar giriniz.", HttpStatus.BAD_REQUEST),
    TOKEN_ERROR(1004, "Token üretilemedi, lütfen tekrar giriş yapmayı deneyiniz.", HttpStatus.INTERNAL_SERVER_ERROR),
    INTERNAL_ERROR(5100, "Sunucu Hatasi...", HttpStatus.INTERNAL_SERVER_ERROR),
    BAD_REQUEST(4100, "Parametre Hatasi...", HttpStatus.BAD_REQUEST),
    LOGIN_ERROR(4110, "Kullanici adi veya sifre hatali...", HttpStatus.BAD_REQUEST),
    USERNAME_DUPLICATE(4111, "Kullanici adi kullanilmaktadir", HttpStatus.BAD_REQUEST),
    USER_NOT_FOUND(4112, "Kulanici bulunamadi...", HttpStatus.BAD_REQUEST),
    ACTIVATION_CODE_ERROR(4113, "Aktivasyon kodu hatalidir...", HttpStatus.BAD_REQUEST),
    INVALID_TOKEN(4114, "Gecersiz token", HttpStatus.BAD_REQUEST),
    TOKEN_NOT_CREATED(4115, "Token olusturulamadi...", HttpStatus.BAD_REQUEST),
    ACCOUNT_NOT_ACTIVE(4116, "Hesabınız aktif edilmemiştir. Lütfen hesabınızı aktif hale getiriniz...", HttpStatus.FORBIDDEN),
    USER_NOT_CREATED(4117, "Kullanici profili olusturulamadi...", HttpStatus.BAD_REQUEST),
    ROLE_NOT_FOUND(4118, "Boyle bir kullanici rolu bulunmamaktadir...", HttpStatus.BAD_REQUEST),
    PASSWORD_UPDATE_REQUIRED(4119, "Sifrenizi guncellemeniz gerekmektedir...", HttpStatus.BAD_REQUEST),
    PASSWORD_NOT_MATCH(4120, "Parolalar uyusmamaktadir...", HttpStatus.BAD_REQUEST),
    ADVANCE_ERROR(4121, "Avans tutarı olması gerekenden yüksek.", HttpStatus.BAD_REQUEST),
    REQUEST_NOT_FOUND(4122,"Personel isteği bulunamadı.",HttpStatus.BAD_REQUEST),
    AUTHORITY_ERROR(4123,"Bu işlem için yetkiniz yoktur.",HttpStatus.BAD_REQUEST),
    COMPANY_NOT_FOUND(4124, "Şirket bulunmamaktadır...",HttpStatus.BAD_REQUEST );

    private int code;
    private String message;
    private HttpStatus httpStatus;

//    ErrorType(int code, String message, HttpStatus httpStatus){
//        this.code = code;
//        this.httpStatus = httpStatus;
//        this.message = message;
//    }
}
