package kkukmoa.kkukmoa.user.domain;

import jakarta.persistence.*;
import kkukmoa.kkukmoa.common.BaseEntity;
import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.usertype.UserType;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

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

    @Column(nullable = true, length = 255)
    private String profile_image;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }


    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        return String.valueOf(this.email);
    }
    public Long getUserId() {
        return id;
    }
}

