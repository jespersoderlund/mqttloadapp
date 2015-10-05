package com.jsoft.iot.mqttloadapp;

import com.amazonaws.auth.AWSCredentialsProviderChain;
import com.amazonaws.auth.ClasspathPropertiesFileCredentialsProvider;
import com.amazonaws.auth.EnvironmentVariableCredentialsProvider;
import com.amazonaws.auth.InstanceProfileCredentialsProvider;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;

public class MyCredentialsProvider extends AWSCredentialsProviderChain {

    MyCredentialsProvider() {
        super(new EnvironmentVariableCredentialsProvider(),
                new SystemPropertiesCredentialsProvider(),
                // Removed due to ElasticBeanstalk environment not being setup with a profile dir
                // new ProfileCredentialsProvider(),
                new InstanceProfileCredentialsProvider(),
                new ClasspathPropertiesFileCredentialsProvider());
    }
}
