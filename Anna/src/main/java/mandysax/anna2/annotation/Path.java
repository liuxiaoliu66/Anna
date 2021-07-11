package mandysax.anna2.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * @author liuxiaoliu66
 */
@Target({ElementType.FIELD,ElementType.LOCAL_VARIABLE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Path
{
	String value();
}

