//package com.bilgeadam.config.security;
//
//
//import com.bilgeadam.repository.entity.Auth;
//import com.bilgeadam.service.AuthService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.security.core.GrantedAuthority;
//import org.springframework.security.core.authority.SimpleGrantedAuthority;
//import org.springframework.security.core.userdetails.User;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.security.core.userdetails.UsernameNotFoundException;
//import org.springframework.stereotype.Service;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@RequiredArgsConstructor
//public class JwtUserDetails implements UserDetailsService {
//
//    private final AuthService authService;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
//
//
//    /**
//     * 1- Sistemde kayıtlı kullanıcıların listesinden kullanıcının bilgileri doğrulanır
//     * 2- Bu kullanıcıya ait yetkiler var ise bu yetkiler çekilir
//     * 3- Spring için UserDetails nesnesi oluşturulur
//     *
//     * @param authId
//     * @return bu kullanıcının authId sinden var olup olmadığını kontrol ederiz
//     * bu kullanıcının authId siyle UserProfile'ına erişebiliyor muyuz hesabı aktif mi kilitli mi kontrol ederiz
//     * bu userProfile ın yetkileri nelerdir onu kontrol ederiz
//     */
//    public UserDetails findByAuthId(Long authId) {
//        Optional<Auth> auth = authService.findByAuthId(authId);
//        if (auth.isPresent()) {
//            List<GrantedAuthority> authorityList = new ArrayList<>();
//            authorityList.add(new SimpleGrantedAuthority(auth.get().getRole().toString()));
//
//            return User.builder()
//                    .username(auth.get().getEmail())
//                    .password("")
//                    .authorities(authorityList)
//                    .accountExpired(false)
//                    .accountLocked(false)
//                    .build();
//        }
//        return null;
//    }
//}
