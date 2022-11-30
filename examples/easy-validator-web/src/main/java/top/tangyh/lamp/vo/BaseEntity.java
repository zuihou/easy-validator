package top.tangyh.lamp.vo;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.groups.Default;
import java.time.LocalDateTime;

/**
 * @author tangyh
 * @version v1.0
 * @date 2022/11/29 8:59 PM
 * @create [2022/11/29 8:59 PM ] [tangyh] [初始创建]
 */
@Data
public class BaseEntity {

    @NotNull(message = "id不能为空", groups = BaseEntity.Update.class)
    private Long id;

    @NotEmpty(message = "idStr不能为空")
    private String idStr;

    private LocalDateTime createdBy;


    /**
     * 保存和缺省验证组
     */
    public interface Save extends Default {

    }

    /**
     * 更新和缺省验证组
     */
    public interface Update extends Default {

    }
}
