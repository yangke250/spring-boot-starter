package cn.linkedcare.springboot.cachecenter.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * cache会先从缓里取，如果取不到走方法，再放进缓存
 * @author wl
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Cache {
	/**
	 * 当keymethod值为空的时候，会以appname+uri的值为key
	 * @return
	 */
	public String keyMethod();
	
	/**
	 * 缓存失效的时间,默认一分钟
	 * @return
	 */
	public int timeout() default 60;

	/**
	 * 缓存的类
	 * @return
	 */
	public Class<?> cacheClass();
}
