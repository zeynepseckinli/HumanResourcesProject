//package com.bilgeadam.config.security;
//
//import io.swagger.v3.oas.models.Components;
//import io.swagger.v3.oas.models.OpenAPI;
//import io.swagger.v3.oas.models.security.SecurityRequirement;
//import io.swagger.v3.oas.models.security.SecurityScheme;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//@Configuration
//public class SwaggerConfig {
//
//
//    /**
//     * Bu method bize özelleştirilmiş bir swagger=OpenApi
//     * Bir security şeması tanımlayacağız -> String securitySchemeName = "bearerAuth";
//     * dönüşü yeni bir OpenApi olacak bunu özelleştirirken
//     * bir securtiy eşyası tanımlıyoruz ve bunu addList diyerek Listeye ekliyoruz
//     * -> .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
//     *
//     * @return
//     */
//    @Bean
//    public OpenAPI customize(){ //swagger=OpenApi özelleştirme
//        String securitySchemeName="bearearAuth";
//        return new OpenAPI()
//                .addSecurityItem(new SecurityRequirement().addList(securitySchemeName))
//                .components(new Components()
//                        .addSecuritySchemes(securitySchemeName,new SecurityScheme()
//                                .name(securitySchemeName)
//                                .type(SecurityScheme.Type.HTTP)
//                                .scheme("Bearer").bearerFormat("JWT")//JWT formatında bir bearer token alacağız
//                        ));
//    }
//}
