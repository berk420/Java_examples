package com.draft.e_commerce.bean_lifecycle;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component
public class BeanLifecycleManager implements InitializingBean, DisposableBean {

    private static final Logger logger = LoggerFactory.getLogger(BeanLifecycleManager.class);

    @Override
    public void afterPropertiesSet() throws Exception {
        logger.info("BeanLifecycleManager afterPropertiesSet method called");
        // Bean'lerin başlatılmasıyla ilgili işlemleri burada tanımlayabilirsiniz
    }

    @Override
    public void destroy() throws Exception {
        logger.info("BeanLifecycleManager destroy method called");
        // Bean'lerin yok edilmesiyle ilgili işlemleri burada tanımlayabilirsiniz
    }
}
