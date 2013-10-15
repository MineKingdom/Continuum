package net.minekingdom.continuum.commands.annotated;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface SubCommand {
	public String name();
	public String description() default "";
	public String usage() default "";
	public String permission() default "";
	public int min() default 0;
	public int max() default -1;
	public String[] flags() default {};
}
