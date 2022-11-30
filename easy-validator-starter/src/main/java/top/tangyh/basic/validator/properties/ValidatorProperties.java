package top.tangyh.basic.validator.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/11/28 6:31 PM
 * @create [2022/11/28 6:31 PM ] [tangyh] [初始创建]
 */
@ConfigurationProperties(prefix = ValidatorProperties.PREFIX)
public class ValidatorProperties {
    public static final String PREFIX = "easy-validator";
    private Boolean enabled = true;
    private Boolean failFast = true;


    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public Boolean getFailFast() {
        return failFast;
    }

    public void setFailFast(Boolean failFast) {
        this.failFast = failFast;
    }
}
