package gestor.feedlotapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class FeedlotAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(FeedlotAppApplication.class, args);
    }

}
