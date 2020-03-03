package com.Everis.calculadora.config;
import java.awt.print.Pageable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;

import com.google.common.collect.Lists;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Contact;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
@Import(springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration.class)
public class SwaggerConfig
{
	public static final String AUTHORIZATION_HEADER = "Authorization";
	public static final String DEFAULT_INCLUDE_PATTERN = "/.*";
	@Value("${app.version:sin version}")
	private String appVersion;
	@Bean
	public Docket swaggerSpringfoxDocket() {
		ApiInfo apiInfo = new ApiInfo(
				"Servicio Maestro de RCE",
				"Servicios RCE",
				appVersion,
				"Terms of service",
				new Contact("Goggle", "google.com", ""),
				"Apache License Version 2.0",
				"https://www.apache.org/licenses/LICENSE-2.0", Collections.emptyList());

		Docket docket = new Docket(DocumentationType.SWAGGER_2)
				.apiInfo(apiInfo)
				//.apiInfo(ApiInfo.DEFAULT)
				.forCodeGeneration(true)
				.genericModelSubstitutes(ResponseEntity.class)
				.ignoredParameterTypes(Pageable.class)
				.ignoredParameterTypes(java.sql.Date.class)
				.directModelSubstitute(java.time.LocalDate.class, java.sql.Date.class)
				.directModelSubstitute(java.time.ZonedDateTime.class, Date.class)
				.directModelSubstitute(java.time.LocalDateTime.class, Date.class)
				.securityContexts(Lists.newArrayList(Collections.singleton(securityContext())))
				.securitySchemes(Lists.newArrayList(apiKey()))
				.useDefaultResponseMessages(false);

		docket = docket.select()
				.apis(RequestHandlerSelectors.basePackage("com.rce.rce2.controller"))
				.paths(PathSelectors.any())
				.build();
		return docket;
	}
	
	private ApiKey apiKey() {
		return new ApiKey("JWT", AUTHORIZATION_HEADER, "header");
	}
	
	private SecurityContext securityContext() {
		return SecurityContext.builder()
				.securityReferences(defaultAuth())
				.forPaths(PathSelectors.regex(DEFAULT_INCLUDE_PATTERN))
				.build();
	}
	
	List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope
				= new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Lists.newArrayList(
				new SecurityReference("JWT", authorizationScopes));
	}
//	  @Bean
//	    public Docket api() {
//	        return new Docket(DocumentationType.SWAGGER_2).select()
//	            .apis(RequestHandlerSelectors
//	                .basePackage("com.rce.controller"))
//	            .paths(PathSelectors.regex("/.*"))
//	            .build().apiInfo(apiEndPointsInfo());
//	    }
//	    private ApiInfo apiEndPointsInfo() {
//	        return new ApiInfoBuilder().title("Spring Boot REST API")
//	            .description("Employee Management REST API")
//	            .contact(new Contact("Usuario Generico", "www.javaguides.net", null))
//	            .license("Apache 2.0")
//	            .licenseUrl("http://www.apache.org/licenses/LICENSE-2.0.html")
//	            .version("1.0.0")
//	            .build();
//	    }
}