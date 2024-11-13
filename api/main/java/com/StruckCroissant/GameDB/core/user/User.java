package com.StruckCroissant.GameDB.core.user;

import com.StruckCroissant.GameDB.core.DBAL.contract.DbModelObj;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import javax.validation.constraints.NotBlank;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class User implements DbModelObj, UserDetails {
  private final Integer uid;
  @NotBlank private final String username;

  @NotBlank
  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  private String password;

  private final UserRoleEnum userRole;

  private final Boolean locked;

  private final Boolean enabled;

  public User(
      @JsonProperty("id") int id,
      @JsonProperty("username") String username,
      @JsonProperty("password") String password,
      @JsonProperty("role") UserRoleEnum role,
      @JsonProperty("locked") Boolean locked,
      @JsonProperty("enabled") Boolean enabled) {
    this.uid = id;
    this.username = username;
    this.password = password;
    this.userRole = role;
    this.locked = locked;
    this.enabled = enabled;
  }

  public User(
      @JsonProperty("username") String username,
      @JsonProperty("password") String password,
      @JsonProperty("role") UserRoleEnum role,
      @JsonProperty("locked") Boolean locked,
      @JsonProperty("enabled") Boolean enabled) {
    this.uid = null;
    this.username = username;
    this.password = password;
    this.userRole = role;
    this.locked = locked;
    this.enabled = enabled;
  }

  public Optional<Integer> getId() {
    return Optional.ofNullable(this.uid);
  }

  public String getUsername() {
    return username;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String encodedPassword) {
    this.password = encodedPassword;
  }

  public UserRoleEnum getRole() {
    return this.userRole;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return !locked;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return false;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    SimpleGrantedAuthority authority = new SimpleGrantedAuthority(userRole.name());
    return Collections.singletonList(authority);
  }

  @Override
  public String toString() {
    return "User{" + "id=" + this.uid + ", username='" + this.username + '\'' + '}';
  }
}
