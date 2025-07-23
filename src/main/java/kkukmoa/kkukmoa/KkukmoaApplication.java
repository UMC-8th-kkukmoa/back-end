package kkukmoa.kkukmoa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class KkukmoaApplication {

    public static void main(String[] args) {
        SpringApplication.run(KkukmoaApplication.class, args);
    }
}
