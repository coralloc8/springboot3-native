package com.coral.test.spring.natives.core.utils.validator;

import lombok.*;

/**
 * @author huss
 * @version 1.0
 * @className ValidatorErrorMsg
 * @description 检验错误信息
 * @date 2021/3/24 15:26
 */
@Data
public class ValidatorErrorMsg {
    /**
     * 属性
     */
    private String property;

    /**
     * 翻译key
     */
    private String messageKey;

    /**
     * 错误信息
     */
    private String message;

    public ValidatorErrorMsgView create() {
        return new ValidatorErrorMsgView(this.getProperty(), this.getMessage());
    }

    @AllArgsConstructor
    @ToString
    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static class ValidatorErrorMsgView {
        /**
         * 属性
         */
        private String property;

        /**
         * 错误信息
         */
        private String message;
    }
}
