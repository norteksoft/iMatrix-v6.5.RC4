package com.norteksoft.product.web.struts2;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 自定义注解
 * 在swing工具的代码生成和生成元数据时会读取该配置
 * @author ldx
 *
 */
@Target({ElementType.METHOD, ElementType.TYPE,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface MetaData {
    /**
     * 是否受权限控制
     * @return
     */
    boolean isAuth() default true;
    /**
     * 当前资源是否是菜单资源
     * @return
     */
    boolean isMenu() default false;
    /**
     * 描述
     * @return
     */
    String describe() default "";
}
