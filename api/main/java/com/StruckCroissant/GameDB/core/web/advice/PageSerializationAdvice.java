package com.StruckCroissant.GameDB.core.web.advice;

import com.StruckCroissant.GameDB.core.web.DTO.PageCustomFormat;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.MethodParameter;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

@ControllerAdvice
public class PageSerializationAdvice implements ResponseBodyAdvice<Page<?>> {
  @Override
  public boolean supports(
      @NotNull MethodParameter returnType,
      @NotNull Class<? extends HttpMessageConverter<?>> converterType
  ) {
    return converterType.isAssignableFrom(MappingJackson2HttpMessageConverter.class)
        && Page.class.isAssignableFrom(returnType.getParameterType());
  }

  public Page<?> beforeBodyWrite(
      Page<?> body,
      @NotNull MethodParameter returnType,
      @NotNull MediaType selectedContentType,
      @NotNull Class<? extends HttpMessageConverter<?>> selectedConverterType,
      @NotNull ServerHttpRequest request,
      @NotNull ServerHttpResponse response
  ) {
    return new PageCustomFormat<>(body);
  }
}
