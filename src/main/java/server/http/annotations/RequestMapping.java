package server.http.annotations;

import server.http.HttpMethod;
import server.http.MediaType;

import java.lang.annotation.*;

@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface RequestMapping {

	String[] path() default {};

	String[] queries() default {};

	HttpMethod method() default HttpMethod.GET;

	MediaType[] consumes() default {MediaType.TEXT_PLAIN};

	MediaType[] produces() default {MediaType.TEXT_PLAIN};

}
