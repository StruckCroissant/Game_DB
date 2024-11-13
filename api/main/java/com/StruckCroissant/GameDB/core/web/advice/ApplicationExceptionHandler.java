package com.StruckCroissant.GameDB.core.web.advice;

import javax.servlet.http.HttpServletRequest;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.ThrowableProblem;
import org.zalando.problem.spring.web.advice.ProblemHandling;

@RestControllerAdvice
public class ApplicationExceptionHandler implements ProblemHandling {
  @ExceptionHandler
  @Override
  public ResponseEntity<Problem> handleProblem(
      final @NotNull ThrowableProblem problem, final @NotNull NativeWebRequest request) {
    return ProblemHandling.super.handleProblem(problem, request);
  }

  @ExceptionHandler(NoHandlerFoundException.class)
  @ResponseStatus(HttpStatus.NOT_FOUND)
  @Override
  public ResponseEntity<Problem> handleNoHandlerFound(
      final @NotNull NoHandlerFoundException exception, final @NotNull NativeWebRequest request) {
    HttpServletRequest httpServletRequest = request.getNativeRequest(HttpServletRequest.class);
    assert httpServletRequest != null;
    Problem problem =
        Problem.builder()
            .withTitle("Route Not Found")
            .withDetail(
                String.format(
                    "No route found for %s %s",
                    httpServletRequest.getMethod(), httpServletRequest.getRequestURI()))
            .withStatus(Status.NOT_FOUND)
            .build();
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PROBLEM_JSON);

    return new ResponseEntity<>(problem, headers, HttpStatus.NOT_FOUND);
  }
}
