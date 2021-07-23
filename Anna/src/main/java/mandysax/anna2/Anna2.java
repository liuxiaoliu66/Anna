package mandysax.anna2;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Proxy;

import mandysax.anna2.factory.DefaultConverterFactory;
import mandysax.anna2.factory.ConverterFactory;

/**
 * @author liuxiaoliu66
 */
public class Anna2 {

    public final static int UNKNOWN = -1;

    private String mBaseUrl;

    private ConverterFactory.Factory mFactory;

    Anna2() {
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
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

    public Anna2 addConverterFactory(ConverterFactory.Factory factory) {
        if (mFactory != null) throw new IllegalStateException("ConverterFactory has been added");
        mFactory = factory;
        return this;
    }

    public ConverterFactory.Factory getConverterFactory() {
        return mFactory;
    }

    @NotNull
    @SuppressWarnings("All")
    public final <T> T newProxy(@NotNull Class<T> clazz)//构建代理类
    {
        if (mFactory == null) {
            synchronized (Anna2.class) {
                if (mFactory == null)
                    addConverterFactory(DefaultConverterFactory.create());
            }
        }
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, new AnnotationHandler(this));
    }

}
