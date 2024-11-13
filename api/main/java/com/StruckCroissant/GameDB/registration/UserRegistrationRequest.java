package com.StruckCroissant.GameDB.registration;

import javax.validation.constraints.NotBlank;
import java.util.Objects;

public class UserRegistrationRequest {

  private final String username;
  private final String password;

  public UserRegistrationRequest(@NotBlank String username, @NotBlank String password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  @Override
  public String toString() {
    return "UserRegistrationRequest{"
        + "username='"
        + username
        + '\''
        + ", password='"
        + password
        + '\''
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    UserRegistrationRequest that = (UserRegistrationRequest) o;
    return username.equals(that.username) && password.equals(that.password);
  }

  @Override
  public int hashCode() {
    return Objects.hash(username, password);
  }
}
