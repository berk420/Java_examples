package com.example.demo.lifecycle;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import com.example.demo.model.Car;

@Component
public class CarLifecycleLogger implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Car) {
            System.out.println("Before Initialization: " + beanName + " -> " + bean.toString());
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof Car) {
            System.out.println("After Initialization: " + beanName + " -> " + bean.toString());
        }
        return bean;
    }
}
