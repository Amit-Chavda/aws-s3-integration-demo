package com.awss3.demo.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.config.YamlPropertiesFactoryBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.PropertiesPropertySource;
import org.springframework.core.io.ClassPathResource;

import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class AppProperties {

    private final Environment environment;

    // Define properties for all environments
    @PostConstruct
    public void init() {
        String activeProfile = environment.getActiveProfiles()[0];

        //Will fetch only the currently active environment
        String configFileName = "application-" + activeProfile + ".yml";

        YamlPropertiesFactoryBean factory = new YamlPropertiesFactoryBean();
        factory.setResources(new ClassPathResource("application.yml"), new ClassPathResource(configFileName));
        Properties properties = factory.getObject();

        assert properties != null;
        PropertiesPropertySource propertySource = new PropertiesPropertySource(configFileName, properties);
        MutablePropertySources propertySources = ((ConfigurableEnvironment) environment).getPropertySources();
        propertySources.addFirst(propertySource);
    }


    //AWS
    public String awsS3BucketName() {
        return environment.getProperty("aws.s3.bucket");
    }

    public String awsS3BucketRegion() {
        return environment.getProperty("aws.s3.region");
    }

    public String awsS3BucketKeySecret() {
        return environment.getProperty("aws.s3.secretKey");
    }

    public String awsS3BucketAccessKey() {
        return environment.getProperty("aws.s3.accessKey");
    }
}
