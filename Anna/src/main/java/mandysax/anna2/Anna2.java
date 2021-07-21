package mandysax.anna2;

import java.lang.reflect.Proxy;

/**
 * @author liuxiaoliu66
 */
public class Anna2 {

    public final static int NO_INTERNET = 1;

    private String mBaseUrl;

    Anna2() {
    }

    public static Anna2 build() {
        return new Anna2();
    }

    public Anna2 baseUrl(String baseUrl) {
        mBaseUrl = baseUrl;
        return this;
    }

    public String getBaseUrl() {
        return mBaseUrl;
    }

    @SuppressWarnings("All")
    public final <T> T newProxy(Class<T> clazz)//构建代理类
    {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new AnnotationHandler(this));
    }

}
