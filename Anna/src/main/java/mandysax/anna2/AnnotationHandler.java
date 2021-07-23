package mandysax.anna2;

import org.jetbrains.annotations.NotNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import mandysax.anna2.annotation.Body;
import mandysax.anna2.annotation.BodyMap;
import mandysax.anna2.annotation.Delete;
import mandysax.anna2.annotation.Get;
import mandysax.anna2.annotation.Header;
import mandysax.anna2.annotation.Path;
import mandysax.anna2.annotation.Post;
import mandysax.anna2.annotation.Put;
import mandysax.anna2.annotation.Query;
import mandysax.anna2.annotation.QueryMap;
import mandysax.anna2.observable.Observable;
import mandysax.anna2.observable.ObservableImpl;
import mandysax.anna2.utils.GenericUtils;
import mandysax.anna2.utils.ThrowUtils;

final class AnnotationHandler implements InvocationHandler {

    private final Anna2 mAnna;

    /**
     * url
     */
    private String mUrl;

    /**
     * 请求体
     */
    private HashMap<String, String> mBody;

    /**
     * 请求头
     */
    private HashMap<String, String> mHeader;

    /**
     * 词穷了。。。
     */
    private HashMap<String, String> mQuery;
    /**
     * 数据加载完成后的解析路径
     */
    private String mPath;

    /**
     * 需要返回的泛型类型
     */
    private Class<?> mReturnGenericClazz;

    AnnotationHandler(Anna2 anna) {
        mAnna = anna;
    }

    @Override
    @SuppressWarnings("ALL")
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        mReturnGenericClazz = GenericUtils.getGenericType(method);
        for (Annotation annotation : method.getAnnotations()) {
            if (annotation instanceof Get)
                return handlerGet(method, (Get) annotation, args);
            if (annotation instanceof Post)
                return handlerPost(method, (Post) annotation, args);
            if (annotation instanceof Delete)
                return handleDelete(method, (Delete) annotation, args);
            if (annotation instanceof Put)
                return handlePut(method, (Put) annotation, args);
        }
        throw ThrowUtils.noAnnotationRequestType();
    }

    /**
     * @param method 方法
     * @param params 对应参数列表
     */
    @SuppressWarnings("ALL")
    private void handlerAnnotation(@NotNull Method method, Object[] params) {
        handlerParams(params);
        for (Annotation annotation : method.getAnnotations())
            if (annotation instanceof Path) {
                if (mPath != null) throw ThrowUtils.pathAlreadyExists();
                mPath = ((Path) annotation).value();
            }
        Annotation[][] annotations = method.getParameterAnnotations();
        for (int i = 0; i < annotations.length; i++) {
            if (annotations[i][0] instanceof Query) {
                Query query = (Query) annotations[i][0];
                (mQuery == null ? mQuery = new HashMap<>() : mQuery).put(query.value(), params[i] == null ? null : params[i].toString());
            } else if (annotations[i][0] instanceof Body) {
                Body body = (Body) annotations[i][0];
                (mBody == null ? mBody = new HashMap<>() : mBody).put(body.value(), params[i] == null ? null : params[i].toString());
            } else if (annotations[i][0] instanceof Header) {
                Header header = (Header) annotations[i][0];
                (mHeader == null ? mHeader = new HashMap<>() : mHeader).put(header.value(), params[i] == null ? null : params[i].toString());
            } else if (annotations[i][0] instanceof BodyMap) {
                if (mHeader != null) throw ThrowUtils.bodyMapAlreadyExists();
                mHeader = (HashMap<String, String>) params[i];
            } else if (annotations[i][0] instanceof QueryMap) {
                if (mQuery != null) throw ThrowUtils.bodyMapAlreadyExists();
                mQuery = (HashMap<String, String>) params[i];
            }
        }
    }

    /**
     * @param params 当参数列表上有List时，需要去除它们身上的“[ ]”
     */
    private void handlerParams(Object[] params) {
        if (params != null)
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof List) {
                    String str = params[i].toString();
                    if (str.length() == 0) continue;
                    if (str.charAt(0) == '[' && str.endsWith("]")) {
                        str = str.substring(1, str.length() - 1);
                        params[i] = str;
                    }
                }
            }
    }

    @NotNull
    private Observable<?> request(TYPE type) {
        if (mReturnGenericClazz != null)
            return ObservableImpl.create(mAnna, mReturnGenericClazz, type, mUrl, mPath, mQuery, mBody, mHeader);
        throw ThrowUtils.returnGenericClazzIsNull();
    }

    /**
     * @param method 方法
     * @param get    get注解对象
     * @param params 对应参数列表
     * @return Observable实例
     */
    @NotNull
    private Observable<?> handlerGet(Method method, @NotNull Get get, Object[] params) {
        mUrl = mAnna.getBaseUrl() + get.value();
        handlerAnnotation(method, params);
        return request(TYPE.GET);
    }

    @NotNull
    private Observable<?> handlerPost(Method method, @NotNull Post post, Object[] params) {
        mUrl = mAnna.getBaseUrl() + post.value();
        handlerAnnotation(method, params);
        return request(TYPE.POST);
    }

    @NotNull
    private Observable<?> handleDelete(Method method, @NotNull Delete delete, Object[] params) {
        mUrl = mAnna.getBaseUrl() + delete.value();
        handlerAnnotation(method, params);
        return request(TYPE.DELETE);
    }

    @NotNull
    private Observable<?> handlePut(Method method, @NotNull Put put, Object[] params) {
        mUrl = mAnna.getBaseUrl() + put.value();
        handlerAnnotation(method, params);
        return request(TYPE.PUT);
    }

}
