package top.tangyh.lamp.vo;

import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/11/29 8:58 PM
 * @create [2022/11/29 8:58 PM ] [tangyh] [初始创建]
 */
@Data
@ToString(callSuper = true)
public class User {
    @NotEmpty(message = "name不能为空")
    private String name;
    @NotNull(message = "name不能为空")
    private String sex;

}
