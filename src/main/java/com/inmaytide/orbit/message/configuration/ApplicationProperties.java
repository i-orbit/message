package com.inmaytide.orbit.message.configuration;

import com.inmaytide.orbit.commons.configuration.GlobalProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author inmaytide
 * @since 2023/4/12
 */
@Component
@ConfigurationProperties(prefix = "application")
public class ApplicationProperties extends GlobalProperties {

    private Integer maximumNumberOfExternalMessageResends = 10;

    public Integer getMaximumNumberOfExternalMessageResends() {
        return maximumNumberOfExternalMessageResends;
    }

    public void setMaximumNumberOfExternalMessageResends(Integer maximumNumberOfExternalMessageResends) {
        this.maximumNumberOfExternalMessageResends = maximumNumberOfExternalMessageResends;
    }

}
