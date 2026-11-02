package axiom.daos.tourmate.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TourmatePlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(TourmatePlatformApplication.class, args);
	}

}
