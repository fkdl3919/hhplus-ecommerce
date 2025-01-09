package kr.hhplus.be.server.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import kr.hhplus.be.server.interfaces.common.ErrorResponse;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
            .title("E-commerce project") // API의 제목
            .version("1.0.0"); // API의 버전
    }

    @Bean
    public OpenApiCustomizer globalResponseStatusOpenApiCustomiser() {
        return openApi -> {
            if (openApi.getPaths() != null) {
                openApi.getPaths().values().forEach(pathItem -> {
                    pathItem.readOperations().forEach(operation -> {
                        // 이미 작성된 400 응답이 없다면 추가
                        ApiResponses responses = operation.getResponses();
                        if (!responses.containsKey("400")) {
                            responses.addApiResponse("400", createBadRequestResponse());
                        }
                    });
                });
            }
        };
    }

    private ApiResponse createBadRequestResponse() {
        return new ApiResponse()
            .description("Bad Request")
            .content(
                new Content()
                    .addMediaType("application/json", new MediaType()
                        .addExamples("실패 응답 예제", new Example().value(new ErrorResponse("잘못된 요청 파라미터입니다."))))
            );

    }
}
