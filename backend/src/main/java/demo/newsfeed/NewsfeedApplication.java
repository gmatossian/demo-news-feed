package demo.newsfeed;

import java.time.Clock;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/** Demo/learning news feed backend. Not intended for production use. */
@SpringBootApplication
public class NewsfeedApplication {

    public static void main(String[] args) {
        SpringApplication.run(NewsfeedApplication.class, args);
    }

    /** Single time source so timestamps are consistent and replaceable in tests. */
    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }
}
