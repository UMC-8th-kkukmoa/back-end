package kkukmoa.kkukmoa.user.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserType {

  USER("ROLE_USER"),
  OWNER("ROLE_OWNER"),
  ADMIN("ROLE_ADMIN");
  ;

  private final String roleName;


}
