package com.open.hr.ai.config;

import com.open.ai.eros.common.constants.CommonConstant;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2WebMvc;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：knife4jConfig
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2021/8/4 23:31
 */
@Configuration
@EnableSwagger2WebMvc
public class HrAIKnife4jConfig {


    private ApiInfo erosAi() {
        return new ApiInfoBuilder()
                .title("招聘模块")
                .description("招聘AI")
                .version("1.0")
                .contact(new Contact("HR-AI", "http://www.erosai.com", "erosai@gmail.com"))
                .build();
    }

    /**
     * 第一组：api
     * @return
     */
    @Bean("hrAiApiConfig")
    public Docket hrAiApiConfig() {

        List<Parameter> pars = new ArrayList<>();
        ParameterBuilder tokenPar = new ParameterBuilder();
        tokenPar.name(CommonConstant.USER_LOGIN_TOKEN)
                .description("用户token")
                .defaultValue("test-user@gamil.com")
                .modelRef(new ModelRef("string"))
                .parameterType("header")
                .required(false)
                .build();
        pars.add(tokenPar.build());


        Docket webApi = new Docket(DocumentationType.SWAGGER_2)
                .groupName("AI招聘")
                .apiInfo(erosAi())
                .select()
                //只显示api路径下的页面
                .apis(RequestHandlerSelectors.basePackage("com.open.hr.ai"))
                .paths(PathSelectors.regex("/hr/ai/.*"))
                .build()
                .globalOperationParameters(pars);
        return webApi;
    }
}
