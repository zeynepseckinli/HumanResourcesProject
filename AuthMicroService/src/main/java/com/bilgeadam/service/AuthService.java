package com.bilgeadam.service;

import com.bilgeadam.dto.request.*;
import com.bilgeadam.dto.response.LoginResponseDto;
import com.bilgeadam.exception.AuthException;
import com.bilgeadam.exception.ErrorType;
import com.bilgeadam.manager.UserManager;
import com.bilgeadam.mapper.AuthMapper;
import com.bilgeadam.repository.AuthRepository;
import com.bilgeadam.repository.entity.Auth;
import com.bilgeadam.utility.JwtTokenManager;
import com.bilgeadam.utility.enums.EState;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
//        if(!authOptional.get().getState().equals(EState.ACTIVE)){
//            throw new AuthException(ErrorType.ACCOUNT_NOT_ACTIVE);
//            //TODO: sifre guncellemeye yonlendirip state.ACTIVE olarak duzenlenecek
//            //changePassword metotu kullanılacak
//            //ya da giriş yapılacak ancak pending stateindekiler sadece password değişikliği yaparak active olabilecek
//        }
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
//        authRepository.findOptionalByEmail(dto.getEmail())
//          .ifPresent(auth->{
//            throw new AuthException(ErrorType.USERNAME_DUPLICATE);
//        });

        Auth auth = AuthMapper.INSTANCE.fromDto(dto);
        auth.setCreateDate(LocalDate.now());
        auth.setUpdateDate(LocalDate.now());
        auth.setState(EState.PENDING);
        return authRepository.save(auth);
    }



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
        userManager.updateUserStateForPassword(id.get());
        return true;

    }

//    public Boolean forgotPassword(ForgotPasswordRequestDto dto) {
//        Optional<Long> id= jwtTokenManager.getIdByToken(dto.getJwtToken());
//        if (id.isEmpty()) {throw new AuthException(ErrorType.INVALID_TOKEN);}
//        Optional<Auth> auth= authRepository.findOptionalById(id.get());
//        if (auth.isEmpty() ) { throw new AuthException(ErrorType.USER_NOT_FOUND);}
//        auth.get().setState(EState.PENDING);
//
//
//
//
//    }
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
            System.out.println(e);
            return false; // Hata durumu
        }
    }

    public Boolean updateRole(AuthRoleUpdateRequestDto dto){
        try {
            Optional<Auth> auth = authRepository.findById(dto.getAuthId());
            if (auth.isPresent()) {
                auth.get().setRole(dto.getSelectedRole());
                authRepository.save(auth.get());
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    public Boolean updateAuthState(AuthStateUpdateRequestDto dto){
        Optional<Auth> auth = authRepository.findById(dto.getAuthId());
        if (auth.isEmpty()){
            throw new AuthException(ErrorType.USER_NOT_FOUND);
        }
        auth.get().setState(dto.getSelectedState());
        auth.get().setUpdateDate(LocalDate.now());
        authRepository.save(auth.get());
        userManager.updateUserStateForPassword(dto.getAuthId());
        return true;
    }


}
