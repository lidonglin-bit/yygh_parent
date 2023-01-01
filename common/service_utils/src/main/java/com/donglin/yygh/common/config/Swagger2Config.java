package com.donglin.yygh.common.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {
    @Bean
    public Docket webApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("admin")
                .apiInfo(getAdminApiInfo())
                .select()
                //只显示api路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }

    private ApiInfo getAdminApiInfo(){
        return new ApiInfoBuilder()
                .title("管理员系统")
                .description("本文档描述了网站微服务接口定义")
                .version("1.0")
                .contact(new Contact("donglin", "http://donglin.com", "1909529369@qq.com"))
                .build();
    }

    @Bean
    public Docket userApiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("user")
                .apiInfo(getUserApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/user/.*")))
                .build();
    }

    private ApiInfo getUserApiInfo(){
        return new ApiInfoBuilder()
                .title("用户系统")
                .description("本文档描述了网站微服务接口定义")
                .version("1.0")
                .contact(new Contact("donglin", "http://donglin.com", "1909529369@qq.com"))
                .build();
    }

    @Bean
    public Docket apiConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("api")
                .apiInfo(getApiInfo())
                .select()
                //只显示admin路径下的页面
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }


    private ApiInfo getApiInfo(){
        return new ApiInfoBuilder()
                .title("Api系统")
                .description("本文档描述了网站微服务接口定义")
                .version("1.0")
                .contact(new Contact("donglin", "http://donglin.com", "1909529369@qq.com"))
                .build();
    }



}