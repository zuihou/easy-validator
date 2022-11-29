package top.tangyh.basic.validator.model;

import java.util.List;

/**
 * 字段校验规则信息
 *
 * @author zuihou
 * @date 2019-07-12 14:28
 */
public class FieldValidatorDesc {
    /**
     * 字段名称
     */
    private String field;
    /**
     * 字段的类型
     */
    private String fieldType;
    /**
     * 约束集合
     */
    private List<ConstraintInfo> constraints;

    public String getField() {
        return field;
    }

    public FieldValidatorDesc setField(String field) {
        this.field = field;
        return this;
    }

    public String getFieldType() {
        return fieldType;
    }

    public FieldValidatorDesc setFieldType(String fieldType) {
        this.fieldType = fieldType;
        return this;
    }

    public List<ConstraintInfo> getConstraints() {
        return constraints;
    }

    public FieldValidatorDesc setConstraints(List<ConstraintInfo> constraints) {
        this.constraints = constraints;
        return this;
    }
}
