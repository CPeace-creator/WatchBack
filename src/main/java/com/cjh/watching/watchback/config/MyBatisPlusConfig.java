package com.cjh.watching.watchback.config;
import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
/**
 * - @author Cjh。
 * - @date 2025/8/27 11:23。
 **/
@Configuration
public class MyBatisPlusConfig {

    /**
     * 注册分页插件
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // 添加分页插件
        PaginationInnerInterceptor paginationInterceptor = new PaginationInnerInterceptor();
        // 设置数据库类型（根据实际使用的数据库调整）
        paginationInterceptor.setDbType(DbType.MYSQL);
        // 溢出总页数后是否进行处理（默认false，即返回最后一页）
        paginationInterceptor.setOverflow(true);
        interceptor.addInnerInterceptor(paginationInterceptor);
        return interceptor;
    }
}
