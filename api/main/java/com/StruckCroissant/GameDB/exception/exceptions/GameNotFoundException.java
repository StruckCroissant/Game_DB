package com.StruckCroissant.GameDB.exception.exceptions;

import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Requested game not found")
public class GameNotFoundException extends ResourceNotFoundException {
  public GameNotFoundException(String message) {
    super(message);
  }

  public GameNotFoundException(int id) {
    super(String.format("Game id {%d} not found", id));
  }
}
