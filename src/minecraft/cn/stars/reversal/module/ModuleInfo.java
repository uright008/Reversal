package cn.stars.reversal.module;

import org.lwjgl.input.Keyboard;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ModuleInfo {
    String name();

    String description();

    String localizedName() default "";

    String localizedDescription() default "";

    Category category();

    int defaultKey() default Keyboard.KEY_NONE;
}
