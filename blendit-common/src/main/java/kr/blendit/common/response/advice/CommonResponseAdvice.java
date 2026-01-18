package kr.blendit.common.response.advice;

import kr.blendit.common.annotation.RawResponse;
import kr.blendit.common.response.CommonResponse;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@RestControllerAdvice(basePackages = "kr.blendit")
public class CommonResponseAdvice implements ResponseBodyAdvice<Object> {

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        // 이미 CommonResponse인 경우 제외
        if (returnType.getParameterType().equals(CommonResponse.class)) {
            return false;
        }

        // @RawResponse 어노테이션이 있는 경우 제외
        if (returnType.hasMethodAnnotation(RawResponse.class)) {
            return false;
        }

        // 클래스에 @RawResponse가 있는 경우 제외
        if (returnType.getContainingClass().isAnnotationPresent(RawResponse.class)) {
            return false;
        }

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body,
                                  MethodParameter returnType,
                                  MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest request,
                                  ServerHttpResponse response) {

        String path = request.getURI().getPath();

        if (!path.startsWith("/api/") || body instanceof CommonResponse) {
            return body;
        }

        // null인 경우 빈 성공 응답
        if (body == null) {
            return CommonResponse.success();
        }

        // 데이터를 CommonResponse로 래핑
        return CommonResponse.success(body);
    }
}
