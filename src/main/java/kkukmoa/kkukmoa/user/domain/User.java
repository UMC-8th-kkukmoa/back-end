package kkukmoa.kkukmoa.user.domain;

import jakarta.persistence.*;

import kkukmoa.kkukmoa.common.BaseEntity;
import kkukmoa.kkukmoa.user.enums.SocialType;
import kkukmoa.kkukmoa.user.enums.UserType;

import lombok.*;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class User extends BaseEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    @Column(nullable = true)
    private String email; // 이메일

    @Column(nullable = true, length = 255)
    private String nickname; // 닉네임

    @Column(nullable = true)
    private LocalDate birthday; // 생년월일

//    @Column(nullable = true, length = 255)
//    private String profile_image;

    @Column(nullable = true)
    private String uuid; // uuid

    @Column(nullable = true, length = 20)
    private String phoneNumber; // 로컬 로그인용

    @Column
    private String password; // 로컬 로그인용 (소셜은 null 가능)

    @Column(nullable = false)
    @Builder.Default
    private boolean agreeTerms = false; // 서비스 이용약관 동의 여부

    @Column(nullable = false)
    @Builder.Default
    private boolean agreePrivacy = false; // 개인정보 처리방침 동의 여부

    @Column(nullable = false)
    @Builder.Default
    private boolean emailVerified = false;

    @Enumerated(EnumType.STRING)
    private SocialType socialType;

    /*    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }*/

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getRoleName()))
                .collect(Collectors.toSet());
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role", length = 30)
    @Builder.Default
    private Set<UserType> roles = new HashSet<>();

    public void addRole(UserType role) {
        if (this.roles == null) this.roles = new HashSet<>();
        this.roles.add(role);
    }

    @PrePersist
    private void prePersist() {
        if (this.roles == null) this.roles = new HashSet<>();
    }

    @Override
    public String getPassword() {
        return String.valueOf(this.password);
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    public Long getUserId() {
        return id;
    }
}
