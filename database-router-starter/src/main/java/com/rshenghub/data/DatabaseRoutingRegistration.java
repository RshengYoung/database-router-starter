package com.rshenghub.data;

import java.util.Optional;

import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

class DatabaseRoutingRegistration implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importMetadata, BeanDefinitionRegistry registry) {
        var attributes = importMetadata.getAnnotationAttributes(EnableDatasourceRouting.class.getName());
        var tenantDefineDir = Optional.of(attributes.get("tenantDefineDir")).map(String.class::cast).orElseThrow();

        var props = new MutablePropertyValues();
        props.addPropertyValue("tenantDatasourcePath", tenantDefineDir);
        // props.addPropertyValue("datasourceType", datasourceType);

        var beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(MultiTenantDataSource.class);
        beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_NAME);
        beanDefinition.setPrimary(true);
        beanDefinition.setPropertyValues(props);
        beanDefinition.setDestroyMethodName("shutdown");
        // beanDefinition.setScope(SCOPE_SESSION);

        registry.registerBeanDefinition("datasource", beanDefinition);
    }

}
