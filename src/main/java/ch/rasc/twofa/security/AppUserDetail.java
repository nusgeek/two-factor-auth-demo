package ch.rasc.twofa.security;

import java.util.Objects;
import java.util.Set;

import ch.rasc.twofa.entity.User;
import org.springframework.security.core.GrantedAuthority;

import ch.rasc.twofa.db.tables.records.AppUserRecord;

public class AppUserDetail {

  private final Integer id;

  private final String username;

  private final boolean enabled;

  private final String secret;

  private final String roleName;

  private final String detail;

  private final Set<GrantedAuthority> authorities;

  public AppUserDetail(User user) {
    this.id = user.getId();
    this.username = user.getUsername();
    this.authorities = Set.of();
    this.secret = user.getSecret();
    this.roleName = user.getRoleName();
    this.detail = user.getDetail();
    this.enabled = Objects.requireNonNullElse(user.isEnabled(), false);
  }

  public Integer getAppUserId() {
    return this.id;
  }

  public String getUsername() {
    return this.username;
  }

  public Set<GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  public boolean isEnabled() {
    return this.enabled;
  }

  public String getSecret() {
    return this.secret;
  }

  public String getRoleName() {
    return roleName;
  }

  public String getDetail() {
    return detail;
  }

}
