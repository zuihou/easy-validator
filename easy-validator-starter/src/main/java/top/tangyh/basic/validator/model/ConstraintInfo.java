package top.tangyh.basic.validator.model;


import java.util.Map;

/**
 * 检验约束信息
 *
 * @author zuihou
 * @date 2019-07-12 14:28
 */
public class ConstraintInfo {
    /**
     * 约束对象的类型
     */
    private String type;
    /**
     * 约束属性
     */
    private Map<String, Object> attrs;

    public String getType() {
        return type;
    }

    public ConstraintInfo setType(String type) {
        this.type = type;
        return this;
    }

    public Map<String, Object> getAttrs() {
        return attrs;
    }

    public ConstraintInfo setAttrs(Map<String, Object> attrs) {
        this.attrs = attrs;
        return this;
    }
}
