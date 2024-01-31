//package com.bilgeadam.config.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class UserSecurityConfig {
//
//    /**
//     * JwtTokenFilter'dan bir tane nesne yaratmamız lazım bunun içinde jwtTokenFilter methodunu yazıyoruz
//     * Burada yarattığımız jwtToken nesnesini aşağıda "filtre uygulanmadan önce bunu araya ekle" diyip,
//     * addFilterBefore'a veriyoruz.
//     * Özet: jwtToken nesnesini yaratıyoruz ve filtre uygulamadan önce araya filtre olarak koyuyoruz, gelen bütün istekleri yakalıyoruz
//     * yakaladığımız bu istekler içerisinden token'ı çıkartıp Spring'in tanıyacağı bir kullanıcı oluşturtuyoruz.
//     */
//    @Bean
//    JwtTokenFilter getJwtTokenFilter() {
//        return new JwtTokenFilter();
//    }
//
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//
//        httpSecurity.authorizeHttpRequests(req ->//filtreye tabi tuttuğumuz kısımlar:
//                req.requestMatchers(
//                                "/swagger-ui/**",//swagger'ın sadece görsel arayüzünü görir
//                                "/v3/api-docs/**",
//                                "/api/v1/user/**"//bu adresteki bilgiler json datası olarak tutuluyor-swaggerdan sonra bunu eklediğimizde end-pointleri görebiliyoruz
//                        ).permitAll()//yetkisi olanlar belirtilen end-point'lere erişsin, yetkisi olmayanlar erişmesin..
//                        //erişimleri kısıtlama:
//                        //   .requestMatchers(   "/user/**" ).hasAnyRole("ADMIN", "MANAGER") TODO:Bizim Rollerimizin yetkilerine göre düzenlenecek
//                        .anyRequest()
//                        .authenticated()
//        );
//        /**
//         * csrf-> web sayfaların atılan isteklerin cevaplarını karşılıyor
//         */
//        httpSecurity.csrf(AbstractHttpConfigurer::disable);//csrf ->restApi'de csrf yoktur, kullanılırken kapatılır. Bir şifre/güvenlik önlemi türüdür
//        /**
//         *
//         * Roller, Yetkiler gbi kullanıcı bazlı işlemlerin kontrol edilebilmesi için filter içine Spring in yöneteceği
//         * bir user tanımlanması ve bu işlemin türü belirlenmelidir..
//         *
//         * 1- Spring filter devreye girmeden önce gelen isteği analiz edip, içinde token var mı, varsa bu token kime ait,
//         * bu kişinin nelere yetkisi var mı gbi kontrolleri gerçekleştireceğiz
//         * 2- Bu kişi doğrulandıktan sonra, Spring'in bildiği bir authentication user oluşturup,
//         * filterBefore'la yukarıdaki authorize filtreleme işleminin önüne eklemiş olacağız.
//         * 3- securityFilterChain methodu çalışmaya başladığında önce spring'in tanıdığı user bilgileri kontrol edilecek
//         * gereken izinler ya da yetkiler bu user'daki rollerde varsa method işlemeye devam edecek...
//         * */
//        httpSecurity.addFilterBefore(getJwtTokenFilter(), UsernamePasswordAuthenticationFilter.class);
//        return httpSecurity.build();
//
//    }
//}
