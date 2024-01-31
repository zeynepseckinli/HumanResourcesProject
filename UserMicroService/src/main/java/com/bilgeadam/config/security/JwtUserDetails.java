//package com.bilgeadam.config.security;
//
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
//
//@Service
//@RequiredArgsConstructor
//public class JwtUserDetails implements UserDet {
//
//    private final UserService userService;
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        return null;
//    }
//
//
//    public UserDetails loadUserByRole (String role) throws UsernameNotFoundException {
//        List<GrantedAuthority> authorityList = new ArrayList<>();
//        authorityList.add(new SimpleGrantedAuthority(role));
//
//        return User.builder()
//                .username(role)//id'den role'e erişemeyiz çünkü user entity ile çalışıyoruz
//                .password("")
//                .accountExpired(false)
//                .accountLocked(false)
//                .authorities(authorityList)
//                .build();
//
//    }
//
////    /**
////     * 1- Sistemde kayıtlı kullanıcıların listesinden kullanıcının bilgileri doğrulanır
////     * 2- Bu kullanıcıya ait yetkiler var ise bu yetkiler çekilir
////     * 3- Spring için UserDetails nesnesi oluşturulur
////     *
////     * @param authId
////     * @return bu kullanıcının authId sinden var olup olmadığını kontrol ederiz
////     * bu kullanıcının authId siyle UserProfile'ına erişebiliyor muyuz hesabı aktif mi kilitli mi kontrol ederiz
////     * bu userProfile ın yetkileri nelerdir onu kontrol ederiz
////     */
////
////    public UserDetails findByAuthId(Long authId) {
////        /**
////         * 1- gelen authId'nin hangi userProfile'a ait olduğunu bulmalıyız
////         * 2- bulduğumuz userProfile hala aktif mi değil mi onu kontrol etmeliyiz
////         */
////        Optional<UserProfile> userProfile = userService.findByAuthId(authId);
////        if (userProfile.isEmpty()) {
////            throw new UserException(ErrorType.USER_NOT_FOUND);
////        }
////        List<GrantedAuthority> authorityList = new ArrayList<>();
////        authorityList.add(new SimpleGrantedAuthority(userProfile.get().getRole().name()));
////        return User.builder()
////                .username(userProfile.get().getPersonalEmail())
////                .password("")
////                .accountExpired(false)
////                .accountLocked(false)
////                .authorities(authorityList)
////                .build();
////    }
//}
