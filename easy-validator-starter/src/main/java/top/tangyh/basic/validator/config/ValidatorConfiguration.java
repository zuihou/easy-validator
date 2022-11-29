package top.tangyh.basic.validator.config;

import org.hibernate.validator.HibernateValidator;
import org.hibernate.validator.HibernateValidatorConfiguration;
import org.hibernate.validator.cfg.ConstraintMapping;
import org.hibernate.validator.cfg.context.ConstraintDefinitionContext;
import org.hibernate.validator.internal.cfg.context.DefaultConstraintMapping;
import org.hibernate.validator.internal.engine.DefaultPropertyNodeNameProvider;
import org.hibernate.validator.internal.properties.DefaultGetterPropertySelectionStrategy;
import org.hibernate.validator.internal.properties.javabean.JavaBeanHelper;
import org.hibernate.validator.spi.nodenameprovider.PropertyNodeNameProvider;
import org.hibernate.validator.spi.properties.GetterPropertySelectionStrategy;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.validation.beanvalidation.MethodValidationPostProcessor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import top.tangyh.basic.validator.annotation.NotEmptyPattern;
import top.tangyh.basic.validator.constraintvalidators.NotEmptyPatternConstraintValidator;
import top.tangyh.basic.validator.controller.FormValidatorController;
import top.tangyh.basic.validator.extract.DefaultConstraintExtractImpl;
import top.tangyh.basic.validator.extract.IConstraintExtract;
import top.tangyh.basic.validator.properties.ValidatorProperties;

import javax.validation.Configuration;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

/**
 * 验证器配置
 *
 * @author zuihou
 * @date 2019/07/14
 */
@ConditionalOnProperty(prefix = ValidatorProperties.PREFIX, name = "enabled", havingValue = "true", matchIfMissing = true)
@EnableConfigurationProperties(ValidatorProperties.class)
public class ValidatorConfiguration {
    private final ValidatorProperties validatorProperties;

    public ValidatorConfiguration(ValidatorProperties validatorProperties) {
        this.validatorProperties = validatorProperties;
    }

    @Bean
    @ConditionalOnMissingBean
    public Validator validator() {
        HibernateValidatorConfiguration hibernateValidatorConfiguration = Validation.byProvider(HibernateValidator.class).configure()
                //快速失败返回模式
                .addProperty("hibernate.validator.fail_fast", validatorProperties.getFailFast() ? "true" : "false");
        ValidatorFactory validatorFactory = warp(hibernateValidatorConfiguration).buildValidatorFactory();
        return validatorFactory.getValidator();
    }

    private Configuration<HibernateValidatorConfiguration> warp(HibernateValidatorConfiguration configuration) {
        // 增加一个我们自定义的校验处理器与length的映射
        GetterPropertySelectionStrategy getterPropertySelectionStrategyToUse = new DefaultGetterPropertySelectionStrategy();
        PropertyNodeNameProvider defaultPropertyNodeNameProvider = new DefaultPropertyNodeNameProvider();
        ConstraintMapping mapping = new DefaultConstraintMapping(new JavaBeanHelper(getterPropertySelectionStrategyToUse, defaultPropertyNodeNameProvider));

        ConstraintDefinitionContext<NotEmptyPattern> notEmptyPattern = mapping.constraintDefinition(NotEmptyPattern.class);
        notEmptyPattern.includeExistingValidators(true);
        notEmptyPattern.validatedBy(NotEmptyPatternConstraintValidator.class);

        configuration.addMapping(mapping);
        return configuration;
    }

    /**
     * 开启快速返回
     * <p>
     * 如果参数校验有异常，直接抛异常，不会进入到 controller，使用全局异常拦截进行拦截
     *
     * @param validator 验证器
     */
    @Bean
    @ConditionalOnMissingBean
    public MethodValidationPostProcessor methodValidationPostProcessor(Validator validator) {
        MethodValidationPostProcessor postProcessor = new MethodValidationPostProcessor();
        postProcessor.setValidator(validator);
        return postProcessor;
    }

    @Bean
    @ConditionalOnMissingBean
    public IConstraintExtract constraintExtract(Validator validator) {
        return new DefaultConstraintExtractImpl(validator);
    }

    @Bean
    @ConditionalOnMissingBean
    public FormValidatorController getFormValidatorController(IConstraintExtract constraintExtract, RequestMappingHandlerMapping requestMappingHandlerMapping) {
        return new FormValidatorController(constraintExtract, requestMappingHandlerMapping);
    }

}
