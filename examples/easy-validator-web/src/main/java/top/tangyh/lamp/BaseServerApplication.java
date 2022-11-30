package top.tangyh.lamp;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import top.tangyh.basic.validator.annotation.EnableFormValidator;

import java.net.InetAddress;
import java.net.UnknownHostException;


/**
 * 基础服务启动类
 *
 * @author zuihou
 * @date 2021-10-08
 */
@SpringBootApplication
@Configuration
@Slf4j
@EnableFormValidator
public class BaseServerApplication {
    public static void main(String[] args) throws UnknownHostException {
        ConfigurableApplicationContext application = SpringApplication.run(BaseServerApplication.class, args);
        Environment env = application.getEnvironment();
        log.info("\n----------------------------------------------------------\n\t" +
                        "应用 '{}' 启动成功! 访问连接:\n\t" +
                        "Swagger文档: \t\thttp://{}:{}/doc.html\n\t" +
                        "----------------------------------------------------------",
                env.getProperty("spring.application.name"),
                InetAddress.getLocalHost().getHostAddress(),
                env.getProperty("server.port", "8080")
        );
    }
}
