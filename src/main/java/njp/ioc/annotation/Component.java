package njp.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Bean(scope = BeanScope.PROTOTYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Component {

    BeanScope scope() default BeanScope.PROTOTYPE;

}
