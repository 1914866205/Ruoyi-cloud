package com.ruoyi.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import com.ruoyi.gateway.handler.ValidateCodeHandler;

/**
 * 路由配置信息
 *
 * @author ruoyi
 */
@Configuration
public class RouterFunctionConfiguration {
    @Autowired  // 注入一个 ValidateCodeHandler的对象，相当于Collectors中注入的service，是用来封装逻辑的
    private ValidateCodeHandler validateCodeHandler;

    @SuppressWarnings("rawtypes")
    @Bean
    public RouterFunction routerFunction() {
        /**  参考 https://www.cnblogs.com/somefuture/p/15433565.html
         * Spring框架给我们提供了两种http端点暴露方式来隐藏servlet原理:
         *  一种是基于注解的形式@Controller或@RestController以及其他的注解如@RequestMapping、@GetMapping等等。
         *  另外一种是基于路由配置RouterFunction和HandlerFunction的，称为“函数式WEB”。
         */
        return RouterFunctions.route(
                // 接收 /code 请求
                /**  Spring WebFlux 是一个异步非阻塞式的 Web 框架，它能够充分利用多核 CPU 的硬件资源去处理大量的并发请求。
                 * 　　Spring Cloud Gateway将路由作为Spring WebFlux HandlerMapping基础架构的一部分进行匹配。
                 *    Spring Cloud Gateway包括许多内置的路由断言工厂。所有这些断言都与HTTP请求的不同属性匹配。可以将多个路由断言工厂与逻辑and语句结合使用。
                 *    参考 https://www.cnblogs.com/h--d/p/12741901.html
                 */
                // 显然这行代码 会把code转发到 validateCodeHandler 中 去处理
                RequestPredicates.GET("/code").and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), //TEXT_PLAIN = new MediaType("text", "plain");
                // text/html的意思是将文件的content-type设置为text/html的形式，浏览器在获取到这种文件时会自动调用html的解析器对文件进行相应的处理。
                //text/plain的意思是将文件设置为纯文本的形式，浏览器在获取到这种文件时并不会对其进行处理。
                validateCodeHandler);
    }
}
