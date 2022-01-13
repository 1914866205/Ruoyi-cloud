package com.ruoyi.gateway.handler;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.ruoyi.common.core.exception.CaptchaException;
import com.ruoyi.common.core.web.domain.AjaxResult;
import com.ruoyi.gateway.service.ValidateCodeService;
import reactor.core.publisher.Mono;

/**
 * 验证码获取
 *
 * @author ruoyi
 */
@Component
public class ValidateCodeHandler implements HandlerFunction<ServerResponse> {
    @Autowired
    private ValidateCodeService validateCodeService;  //验证码服务对象

    @Override
    public Mono<ServerResponse> handle(ServerRequest serverRequest) {
        //  响应式编程之Reactor的关于Flux和Mono概念
        /**
         * Flux类似RxJava的Observable，它可以触发零到多个事件，并根据实际情况结束处理或触发错误。
         * Mono最多只触发一个事件
         */
        AjaxResult ajax;  //操作消息提醒，就是一个 统一标准响应体
        try {
            ajax = validateCodeService.createCapcha(); //创建一个验证码
        } catch (CaptchaException | IOException e) {
            return Mono.error(e);
        }
        //
        return ServerResponse.status(HttpStatus.OK).body(BodyInserters.fromValue(ajax));
    }
}