package com.open.ai.eros.ai.config;

import com.open.ai.eros.common.constants.CommonConstant;
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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @类名：Swagger2Config
 * @项目名：web-eros-ai
 * @description：
 * @创建人：Administrator
 * @创建时间：2024/8/4 22:24
 */
@Configuration
@EnableSwagger2WebMvc
public class ErosAISwagger2Config {


    private ApiInfo erosAi() {
        return new ApiInfoBuilder()
                .title("AI模块")
                .description("ErosAI项目调用各个AI模型")
                .version("1.0")
                .contact(new Contact("Eros-AI", "http://www.erosai.com", "erosai@gmail.com"))
                .build();
    }

    /**
     * 第一组：api
     * @return
     */
    @Bean("erosAiApiConfig")
    public Docket erosAiApiConfig() {

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
                .groupName("AI接口")
                .apiInfo(erosAi())
                .select()
                //只显示api路径下的页面
                .apis(RequestHandlerSelectors.basePackage("com.open.ai.eros.ai"))
                .paths(PathSelectors.regex("/eros/ai/ai/.*"))
                .build()
                .globalOperationParameters(pars)
                ;
        return webApi;
    }




}
