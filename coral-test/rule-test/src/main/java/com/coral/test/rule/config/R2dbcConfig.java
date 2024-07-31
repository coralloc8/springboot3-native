package com.coral.test.rule.config;

import com.coral.test.rule.core.web.convert.EnumToInteger;
import com.coral.test.rule.core.web.convert.EnumToString;
import com.coral.test.rule.core.web.convert.IntegerToEnumConverterFactory;
import com.coral.test.rule.core.web.convert.StringToEnumConverterFactory;
import io.r2dbc.pool.ConnectionPool;
import io.r2dbc.pool.ConnectionPoolConfiguration;
import io.r2dbc.proxy.ProxyConnectionFactory;
import io.r2dbc.proxy.support.QueryExecutionInfoFormatter;
import io.r2dbc.spi.ConnectionFactories;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.ConnectionFactoryOptions;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcAutoConfiguration;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcConnectionDetails;
import org.springframework.boot.autoconfigure.r2dbc.R2dbcProperties;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.MySqlDialect;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;

import java.util.List;

/**
 * r2dbc
 *
 * @author huss
 * @date 2024/4/3 14:26
 * @packageName com.coral.test.spring.natives.config
 * @className R2dbcConfig
 */
@Slf4j
@Configuration
public class R2dbcConfig extends AbstractR2dbcConfiguration {
    /**
     * 符号引用
     */
    public static String QUOTATION_MARKS;

    @Resource
    private R2dbcProperties properties;

    @Resource
    private R2dbcConnectionDetails connectionDetails;

    /**
     * @return
     * @see R2dbcAutoConfiguration //ConnectionFactoryConfigurations
     */
    @Bean
    @Override
    public ConnectionFactory connectionFactory() {
        log.info(">>>>>自定义connectionFactory start. properties.url:{} connectionDetails:{}", properties.getUrl(), connectionDetails);

        ConnectionFactoryOptions connectionFactoryOptions = null;
        try {
            connectionFactoryOptions = connectionDetails.getConnectionFactoryOptions();
        } catch (Exception e) {
            // aot编译不通过
            connectionFactoryOptions = ConnectionFactoryOptions.builder()
                    .option(ConnectionFactoryOptions.DRIVER, "postgresql")
                    .option(ConnectionFactoryOptions.HOST, "192.168.29.64")
                    .option(ConnectionFactoryOptions.PORT, 3306)
                    .option(ConnectionFactoryOptions.DATABASE, "bigdata")
                    .option(ConnectionFactoryOptions.USER, "app")
                    .option(ConnectionFactoryOptions.PASSWORD, "App.1202p")
                    .build();

        }

        ConnectionFactory connectionFactory = ConnectionFactories.get(connectionFactoryOptions);
        R2dbcProperties.Pool pool = properties.getPool();
        PropertyMapper map = PropertyMapper.get().alwaysApplyingWhenNonNull();
        ConnectionPoolConfiguration.Builder builder = ConnectionPoolConfiguration.builder(connectionFactory);
        map.from(pool.getMaxIdleTime()).to(builder::maxIdleTime);
        map.from(pool.getMaxLifeTime()).to(builder::maxLifeTime);
        map.from(pool.getMaxAcquireTime()).to(builder::maxAcquireTime);
        map.from(pool.getMaxCreateConnectionTime()).to(builder::maxCreateConnectionTime);
        map.from(pool.getInitialSize()).to(builder::initialSize);
        map.from(pool.getMaxSize()).to(builder::maxSize);
        map.from(pool.getValidationQuery()).whenHasText().to(builder::validationQuery);
        map.from(pool.getValidationDepth()).to(builder::validationDepth);
        map.from(pool.getMinIdle()).to(builder::minIdle);
        map.from(pool.getMaxValidationTime()).to(builder::maxValidationTime);
        connectionFactory = new ConnectionPool(builder.build());
        QueryExecutionInfoFormatter queryFormatter = QueryExecutionInfoFormatter.showAll();
        // 设置符号引用
        this.setQuotationMarks(connectionFactory);
        //
        connectionFactory = ProxyConnectionFactory.builder(connectionFactory)
                .onAfterQuery(queryInfo -> log.info("#####[SQL]: {}", queryFormatter.format(queryInfo))).build();
        log.info(">>>>>自定义connectionFactory end.");
        return connectionFactory;
    }


    @Override
    protected List<Object> getCustomConverters() {
        return List.of(new IntegerToEnumConverterFactory(), new StringToEnumConverterFactory(), new EnumToInteger(), new EnumToString());
    }


    /**
     * 获取对应数据的符号引用
     *
     * @param connectionFactory
     * @return
     */
    private void setQuotationMarks(ConnectionFactory connectionFactory) {
        R2dbcDialect dialect = DialectResolver.getDialect(connectionFactory);
        QUOTATION_MARKS = "\"";
        if (dialect instanceof MySqlDialect) {
            QUOTATION_MARKS = "`";
        }
    }

}
