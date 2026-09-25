package demo.newsfeed.demodata;

import java.io.IOException;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import tools.jackson.databind.json.JsonMapper;

@Configuration
class DemoDataConfig {

    private static final Logger log = LoggerFactory.getLogger(DemoDataConfig.class);

    @Bean
    DemoDataset demoDataset(JsonMapper jsonMapper) throws IOException {
        try (InputStream in = new ClassPathResource("seed/demo-data.json").getInputStream()) {
            return jsonMapper.readValue(in, DemoDataset.class);
        }
    }

    /** Seeds a fresh database on startup; existing data is always preserved. */
    @Bean
    ApplicationRunner seedFreshDatabase(DemoDataService demoData) {
        return args -> {
            if (demoData.seedIfFresh()) {
                log.info("Fresh database: loaded the demo dataset.");
            } else {
                log.info("Existing database kept as is ({} posts).", demoData.postCount());
            }
        };
    }
}
