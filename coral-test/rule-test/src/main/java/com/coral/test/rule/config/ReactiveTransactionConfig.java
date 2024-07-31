package com.coral.test.rule.config;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.aop.Advisor;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.r2dbc.connection.R2dbcTransactionManager;
import org.springframework.transaction.ReactiveTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.ProxyTransactionManagementConfiguration;
import org.springframework.transaction.interceptor.*;

import java.util.Collections;

/**
 * 响应式事务
 *
 * @author huss
 * @date 2024/4/1 14:16
 * @packageName com.coral.test.spring.natives.config
 * @className ReactiveTransactionConfig
 * @see ProxyTransactionManagementConfiguration
 */
@Configuration
@EnableTransactionManagement
public class ReactiveTransactionConfig {
    private static final String AOP_POINTCUT_EXPRESSION = "execution(* com.coral..*.*Service.*(..))";

    private static final int TX_METHOD_TIMEOUT = 2000;

    @Bean
    public ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
        return new R2dbcTransactionManager(connectionFactory);
    }



    @Bean(name = "myTransactionInterceptor")
    public TransactionInterceptor myTransactionInterceptor(ReactiveTransactionManager transactionManager) {
        //写事务控制
        RuleBasedTransactionAttribute required =
                new RuleBasedTransactionAttribute(TransactionDefinition.PROPAGATION_REQUIRED,
                        Collections.singletonList(new RollbackRuleAttribute(Exception.class)));
        required.setTimeout(TX_METHOD_TIMEOUT);

        //读事务控制
        DefaultTransactionAttribute readOnly = new DefaultTransactionAttribute();
        readOnly.setPropagationBehavior(TransactionDefinition.PROPAGATION_NOT_SUPPORTED);
        readOnly.setReadOnly(true);

        NameMatchTransactionAttributeSource source = new NameMatchTransactionAttributeSource();
        source.addTransactionalMethod("save*", required);
        source.addTransactionalMethod("add*", required);
        source.addTransactionalMethod("insert*", required);
        source.addTransactionalMethod("delete*", required);
        source.addTransactionalMethod("remove*", required);
        source.addTransactionalMethod("update*", required);
        source.addTransactionalMethod("modify*", required);
        source.addTransactionalMethod("edit*", required);
        source.addTransactionalMethod("exec*", required);
        source.addTransactionalMethod("set*", required);
        source.addTransactionalMethod("copy*", required);
        source.addTransactionalMethod("sort*", required);
        source.addTransactionalMethod("submit*", required);
        source.addTransactionalMethod("send*", required);
        source.addTransactionalMethod("import*", required);

        source.addTransactionalMethod("*Save", required);
        source.addTransactionalMethod("*Add", required);
        source.addTransactionalMethod("*Insert", required);
        source.addTransactionalMethod("*Delete", required);
        source.addTransactionalMethod("*Deleted", required);
        source.addTransactionalMethod("*Enabled", required);
        source.addTransactionalMethod("*Disabled", required);
        source.addTransactionalMethod("*Remove", required);
        source.addTransactionalMethod("*Update", required);
        source.addTransactionalMethod("*Modified", required);
        source.addTransactionalMethod("*Modify", required);
        source.addTransactionalMethod("*Edit", required);
        source.addTransactionalMethod("*Exec", required);
        source.addTransactionalMethod("*Set", required);
        source.addTransactionalMethod("*Copy", required);
        source.addTransactionalMethod("*Sort", required);
        source.addTransactionalMethod("*Submit", required);


        source.addTransactionalMethod("get*", readOnly);
        source.addTransactionalMethod("query*", readOnly);
        source.addTransactionalMethod("find*", readOnly);
        source.addTransactionalMethod("list*", readOnly);
        source.addTransactionalMethod("count*", readOnly);
        source.addTransactionalMethod("is*", readOnly);
        source.addTransactionalMethod("exist*", readOnly);
        source.addTransactionalMethod("page*", readOnly);
        source.addTransactionalMethod("check*", readOnly);

        return new TransactionInterceptor(transactionManager, source);
    }

    /**
     * 切点
     *
     * @return
     */
    @Bean
    public Advisor advisor(@Qualifier("myTransactionInterceptor") TransactionInterceptor transactionInterceptor) {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression(AOP_POINTCUT_EXPRESSION);
        return new DefaultPointcutAdvisor(pointcut, transactionInterceptor);
    }

}
