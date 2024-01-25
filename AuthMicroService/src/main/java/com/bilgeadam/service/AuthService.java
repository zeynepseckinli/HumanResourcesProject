package com.bilgeadam.service;

import com.bilgeadam.dto.request.AuthUpdateRequestDto;
import com.bilgeadam.dto.request.ChangePasswordDto;
import com.bilgeadam.dto.request.LoginRequestDto;

import com.bilgeadam.dto.response.LoginResponseDto;

import com.bilgeadam.dto.request.SaveAuthRequestDto;

import com.bilgeadam.exception.AuthException;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.manager.UserManager;
import com.bilgeadam.mapper.AuthMapper;
import com.bilgeadam.repository.AuthRepository;
import com.bilgeadam.repository.entity.Auth;
import com.bilgeadam.utility.JwtTokenManager;
import com.bilgeadam.utility.enums.EState;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtTokenManager jwtTokenManager;
    private final UserManager userManager;

    public LoginResponseDto login(LoginRequestDto dto) {
        Optional<Auth> authOptional =  authRepository.findOptionalByEmailAndPassword(dto.getEmail(),dto.getPassword());
        if(authOptional.isEmpty()){
            throw new AuthException(ErrorType.LOGIN_ERROR);
        }
        if(!authOptional.get().getState().equals(EState.ACTIVE)){
            throw new AuthException(ErrorType.ACCOUNT_NOT_ACTIVE);
            //TODO: sifre guncellemeye yonlendirip state.ACTIVE olarak duzenlenecek
            //changePassword metotu kullanılacak
        }
        Optional<String> jwtToken = jwtTokenManager.createToken(authOptional.get().getId(), authOptional.get().getRole(), authOptional.get().getState());
        if(jwtToken.isEmpty())
            throw new AuthException(ErrorType.TOKEN_ERROR);

        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .role(authOptional.get().getRole())
                .token(jwtToken.get())
                .authId(authOptional.get().getId())
                .state(authOptional.get().getState())
                .build();
        return loginResponseDto;
    }

    public Auth save(SaveAuthRequestDto dto) {
        authRepository.findOptionalByEmail(dto.getEmail())
          .ifPresent(auth->{
            throw new AuthException(ErrorType.USERNAME_DUPLICATE);
        });

        Auth auth = AuthMapper.INSTANCE.fromDto(dto);
        auth.setCreateDate(System.currentTimeMillis());
        auth.setUpdateDate(System.currentTimeMillis());
        auth.setState(EState.ACTIVE);
        authRepository.save(auth);
        auth.setState(EState.ACTIVE);
        return authRepository.save(auth);

//                userManager.save(UserSaveRequestDto.builder()
//                        .authId(auth.getId())
//                        .email(auth.getEmail())
//                .build());
    }

//    public Long findAuthIdByEmail(String email){
//        return authRepository.findAuthIdByEmail(email);
//    }

    public Boolean changePassword(ChangePasswordDto dto){
        Optional<Long> id= jwtTokenManager.getIdByToken(dto.getJwtToken());
        if (id.isEmpty()) {throw new AuthException(ErrorType.INVALID_TOKEN);}
        Optional<Auth> auth= authRepository.findOptionalById(id.get());
        if (auth.isEmpty() ) { throw new AuthException(ErrorType.USER_NOT_FOUND);}
        if (!(auth.get().getPassword().equals(dto.getOldPassword()))) {throw new AuthException(ErrorType.PASSWORD_NOT_MATCH);}
        if (!(dto.getNewPassword().equals(dto.getConfirmPassword()))) {throw new AuthException(ErrorType.PASSWORD_NOT_MATCH);}
        auth.get().setPassword(dto.getNewPassword());
        auth.get().setState(EState.ACTIVE);
        authRepository.save(auth.get());
        return true;
    }

    public Boolean updateAuth(AuthUpdateRequestDto dto) {
        try {
            Optional<Auth> auth = authRepository.findById(dto.getAuthId());
            if (auth.isPresent()) {
                authRepository.save(AuthMapper.INSTANCE.fromAuthUpdateRequestDto(dto, auth.get()));
                return true; // Başarılı bir güncelleme durumu
            } else {
                return false; // Güncellenecek Auth bulunamadı durumu
            }
        } catch (Exception e) {
            // Detaylı hatayı logla
            System.out.println(e);
            return false; // Hata durumu
        }
    }
}
