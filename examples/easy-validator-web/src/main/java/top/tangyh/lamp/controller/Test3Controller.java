package top.tangyh.lamp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.tangyh.lamp.vo.User;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/11/29 8:57 PM
 * @create [2022/11/29 8:57 PM ] [tangyh] [初始创建]
 */
@RestController
@Slf4j
@RequestMapping("/test3")
@Validated
public class Test3Controller {
    @PostMapping("saveByBody")
    public Object saveByBody(@Validated @RequestBody User user) {
        log.info("user={}", user);
        return user;
    }

    @PostMapping("save")
    public Object save(@Validated User user) {
        log.info("user={}", user);
        return user;
    }

    @PostMapping("updateByBody")
    public Object updateByBody(@Validated @RequestBody User user) {
        log.info("user={}", user);
        return user;
    }

    @PostMapping("update")
    public Object update(@Validated User user) {
        log.info("user={}", user);
        return user;
    }
}
