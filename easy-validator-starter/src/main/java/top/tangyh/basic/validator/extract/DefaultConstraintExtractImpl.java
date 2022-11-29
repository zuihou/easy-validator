package top.tangyh.basic.validator.extract;

import cn.hutool.core.util.CharUtil;
import cn.hutool.core.util.StrUtil;
import org.hibernate.validator.internal.engine.ValidatorImpl;
import org.hibernate.validator.internal.metadata.BeanMetaDataManager;
import org.hibernate.validator.internal.metadata.aggregated.BeanMetaData;
import org.hibernate.validator.internal.metadata.core.MetaConstraint;
import org.hibernate.validator.internal.metadata.location.ConstraintLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.tangyh.basic.validator.mateconstraint.IConstraintConverter;
import top.tangyh.basic.validator.mateconstraint.impl.MaxMinConstraintConverter;
import top.tangyh.basic.validator.mateconstraint.impl.NotNullConstraintConverter;
import top.tangyh.basic.validator.mateconstraint.impl.OtherConstraintConverter;
import top.tangyh.basic.validator.mateconstraint.impl.RangeConstraintConverter;
import top.tangyh.basic.validator.mateconstraint.impl.RegExConstraintConverter;
import top.tangyh.basic.validator.model.ConstraintInfo;
import top.tangyh.basic.validator.model.FieldValidatorDesc;
import top.tangyh.basic.validator.model.ValidConstraint;

import javax.validation.Validator;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static top.tangyh.basic.validator.utils.ValidatorConstants.ARRAY;
import static top.tangyh.basic.validator.utils.ValidatorConstants.BOOLEAN;
import static top.tangyh.basic.validator.utils.ValidatorConstants.BOOLEAN_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.COLLECTION_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.DATE;
import static top.tangyh.basic.validator.utils.ValidatorConstants.DATETIME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.DATE_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.DOUBLE_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.FLOAT;
import static top.tangyh.basic.validator.utils.ValidatorConstants.FLOAT_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.INTEGER;
import static top.tangyh.basic.validator.utils.ValidatorConstants.INTEGER_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.LIST_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.LOCAL_DATE_TIME_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.LOCAL_DATE_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.LOCAL_TIME_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.LONG_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.MESSAGE;
import static top.tangyh.basic.validator.utils.ValidatorConstants.NOT_NULL;
import static top.tangyh.basic.validator.utils.ValidatorConstants.PATTERN;
import static top.tangyh.basic.validator.utils.ValidatorConstants.SET_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.SHORT_TYPE_NAME;
import static top.tangyh.basic.validator.utils.ValidatorConstants.TIME;

/**
 * 缺省的约束提取器
 *
 * @author zuihou
 * @date 2019-07-14 12:12
 */
public class DefaultConstraintExtractImpl implements IConstraintExtract {
    private static final Logger log = LoggerFactory.getLogger(DefaultConstraintExtractImpl.class);
    private final Map<String, Map<String, FieldValidatorDesc>> CACHE = new HashMap<>();

    private final Validator validator;
    private BeanMetaDataManager beanMetaDataManager;
    private List<IConstraintConverter> constraintConverters;

    public DefaultConstraintExtractImpl(final Validator validator) {
        this.validator = validator;
        init();
    }

    public final void init() {
        try {
            Field beanMetaDataManagerField = ValidatorImpl.class.getDeclaredField("beanMetaDataManager");
            beanMetaDataManagerField.setAccessible(true);
            beanMetaDataManager = (BeanMetaDataManager) beanMetaDataManagerField.get(validator);
            constraintConverters = new ArrayList<>(10);
            constraintConverters.add(new MaxMinConstraintConverter());
            constraintConverters.add(new NotNullConstraintConverter());
            constraintConverters.add(new RangeConstraintConverter());
            constraintConverters.add(new RegExConstraintConverter());
            constraintConverters.add(new OtherConstraintConverter());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            log.error("初始化验证器失败", e);
        }
    }

    @Override
    public Collection<FieldValidatorDesc> extract(List<ValidConstraint> constraints) throws Exception {
        if (constraints == null || constraints.isEmpty()) {
            return Collections.emptyList();
        }
        Map<String, FieldValidatorDesc> fieldValidatorDesc = new HashMap((int) (constraints.size() / 0.75 + 1));
        for (ValidConstraint constraint : constraints) {
            doExtract(constraint, fieldValidatorDesc);
        }

        return fieldValidatorDesc.values();
    }


    private void doExtract(ValidConstraint constraint, Map<String, FieldValidatorDesc> fieldValidatorDesc) throws Exception {
        Class<?> targetClazz = constraint.getTarget();
        Class<?>[] groups = constraint.getGroups();

        String key = targetClazz.getName() + StrUtil.COLON +
                Arrays.stream(groups).map(Class::getName).collect(Collectors.joining(StrUtil.COLON));
        if (CACHE.containsKey(key)) {
            fieldValidatorDesc.putAll(CACHE.get(key));
            return;
        }

        //测试一下这个方法
        //validator.getConstraintsForClass(targetClazz).getConstrainedProperties()

        BeanMetaData<?> res = beanMetaDataManager.getBeanMetaData(targetClazz);
        Set<MetaConstraint<?>> r = res.getMetaConstraints();
        for (MetaConstraint<?> metaConstraint : r) {
            builderFieldValidatorDesc(metaConstraint, groups, fieldValidatorDesc);
        }

        CACHE.put(key, fieldValidatorDesc);
    }


    private void builderFieldValidatorDesc(MetaConstraint<?> metaConstraint, Class<?>[] groups,
                                           Map<String, FieldValidatorDesc> fieldValidatorDesc) throws Exception {
        //字段上的组
        Set<Class<?>> groupsMeta = metaConstraint.getGroupList();
        boolean isContainsGroup = false;

        //需要验证的组
        for (Class<?> group : groups) {
            if (groupsMeta.contains(group)) {
                isContainsGroup = true;
                break;
            }
            for (Class<?> g : groupsMeta) {
                if (g.isAssignableFrom(group)) {
                    isContainsGroup = true;
                    break;
                }
            }
        }
        if (!isContainsGroup) {
            return;
        }

        ConstraintLocation con = metaConstraint.getLocation();
        String domainName = con.getDeclaringClass().getSimpleName();
        String fieldName = con.getConstrainable().getName();
        String key = domainName + fieldName;

        FieldValidatorDesc desc = fieldValidatorDesc.get(key);
        if (desc == null) {
            desc = new FieldValidatorDesc();
            desc.setField(fieldName);
            desc.setFieldType(getType(con.getConstrainable().getType().getTypeName()));
            desc.setConstraints(new ArrayList<>());
            fieldValidatorDesc.put(key, desc);
        }
        ConstraintInfo constraint = builderConstraint(metaConstraint.getDescriptor().getAnnotation());
        desc.getConstraints().add(constraint);

        if (PATTERN.equals(metaConstraint.getDescriptor().getAnnotationType().getSimpleName())) {
            ConstraintInfo notNull = new ConstraintInfo();
            notNull.setType(NOT_NULL);
            Map<String, Object> attrs = new HashMap<>();
            attrs.put(MESSAGE, "不能为空");
            notNull.setAttrs(attrs);
            desc.getConstraints().add(notNull);
        }
    }


    private String getType(String typeName) {
        if (StrUtil.startWithAny(typeName, SET_TYPE_NAME, LIST_TYPE_NAME, COLLECTION_TYPE_NAME)) {
            return ARRAY;
        } else if (StrUtil.equalsAny(typeName, LONG_TYPE_NAME, INTEGER_TYPE_NAME, SHORT_TYPE_NAME)) {
            return INTEGER;
        } else if (StrUtil.equalsAny(typeName, DOUBLE_TYPE_NAME, FLOAT_TYPE_NAME)) {
            return FLOAT;
        } else if (StrUtil.equalsAny(typeName, LOCAL_DATE_TIME_TYPE_NAME, DATE_TYPE_NAME)) {
            return DATETIME;
        } else if (StrUtil.equalsAny(typeName, LOCAL_DATE_TYPE_NAME)) {
            return DATE;
        } else if (StrUtil.equalsAny(typeName, LOCAL_TIME_TYPE_NAME)) {
            return TIME;
        } else if (StrUtil.equalsAny(typeName, BOOLEAN_TYPE_NAME)) {
            return BOOLEAN;
        }
        return StrUtil.subAfter(typeName, CharUtil.DOT, true);
    }

    private ConstraintInfo builderConstraint(Annotation annotation) throws Exception {
        for (IConstraintConverter constraintConverter : constraintConverters) {
            if (constraintConverter.support(annotation.annotationType())) {
                return constraintConverter.converter(annotation);
            }
        }
        return null;
    }
}
