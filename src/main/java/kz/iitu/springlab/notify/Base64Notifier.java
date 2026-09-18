package kz.iitu.springlab.notify;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component("base64")
@Order(3)
public class Base64Notifier implements Notifier {

    private static final Logger log =
            LoggerFactory.getLogger(Base64Notifier.class);

    @PostConstruct
    void init() {
        log.info("Base64Notifier initialized");
    }

    @Override
    public String send(String message) {
        String encoded = Base64.getEncoder()
                .encodeToString(message.getBytes(StandardCharsets.UTF_8));

        log.info("BASE64 >> {}", encoded);
        return "base64: " + encoded;
    }

    @Override
    public String channel() {
        return "base64";
    }
}
