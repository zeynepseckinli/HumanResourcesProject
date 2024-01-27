//package com.bilgeadam.config.security;
//
//import com.bilgeadam.exception.ErrorType;
//import com.bilgeadam.exception.UserException;
//import com.bilgeadam.repository.entity.UserProfile;
//import com.bilgeadam.service.UserService;
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
//    private final UserService userService;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
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
//
//    public UserDetails findByAuthId(Long authId) {
//        /**
//         * 1- gelen authId'nin hangi userProfile'a ait olduğunu bulmalıyız
//         * 2- bulduğumuz userProfile hala aktif mi değil mi onu kontrol etmeliyiz
//         */
//        Optional<UserProfile> userProfile = userService.findByAuthId(authId);
//        if (userProfile.isEmpty()) {
//            throw new UserException(ErrorType.USER_NOT_FOUND);
//        }
//        List<GrantedAuthority> authorityList = new ArrayList<>();
//        authorityList.add(new SimpleGrantedAuthority(userProfile.get().getRole().name()));
//        return User.builder()
//                .username(userProfile.get().getPersonalEmail())
//                .password("")
//                .accountExpired(false)
//                .accountLocked(false)
//                .authorities(authorityList)
//                .build();
//    }
//}
