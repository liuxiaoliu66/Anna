package mandysax.anna2;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import mandysax.anna2.annotation.Body;
import mandysax.anna2.annotation.Delete;
import mandysax.anna2.annotation.Get;
import mandysax.anna2.annotation.Header;
import mandysax.anna2.annotation.Path;
import mandysax.anna2.annotation.Post;
import mandysax.anna2.annotation.Put;
import mandysax.anna2.annotation.Query;
import mandysax.anna2.observable.Observable;
import mandysax.anna2.utils.GenericUtils;

@SuppressWarnings("ALL")
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
     * 数据加载完成后的解析路径
     */
    private String mPath;

    /**
     * 需要返回的泛型类型
     */
    private Class mReturnGenericClazz;

    AnnotationHandler(Anna2 anna) {
        mAnna = anna;
    }

    @Override
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
        throw new NullPointerException("No annotation request type");
    }

    /**
     * @param method 方法
     * @param params 对应参数列表
     */
    private void handlerAnnotation(Method method, Object[] params) {
        if (mUrl == null || mReturnGenericClazz == null) throw new RuntimeException();
        handlerParams(params);
        Annotation[] annotation = method.getAnnotations();
        for (Annotation annotation1 : annotation) {
            if (annotation1 instanceof Path) {
                if (mPath != null) throw new RuntimeException("Only one @Path is allowed");
                mPath = ((Path) annotation1).value();
            }
        }
        Annotation[][] annotations2 = method.getParameterAnnotations();
        int m = 0;
        for (int i = 0; i < annotations2.length; i++) {
            if (annotations2[i][0] instanceof Query) {
                Query query = (Query) annotations2[i][0];
                mUrl += (m == 0 ? "?" : "&");
                mUrl += query.value() + "=" + params[i];
                m++;
            } else if (annotations2[i][0] instanceof Body) {
                if (mBody == null) mBody = new HashMap<>();
                Body body = (Body) annotations2[i][0];
                mBody.put(body.value(), params[i] == null ? "" : params[i].toString());
            } else if (annotations2[i][0] instanceof Header) {
                if (mHeader == null) mHeader = new HashMap<>();
                Header header = (Header) annotations2[i][0];
                mHeader.put(header.value(), params[i] == null ? "" : params[i].toString());
            }
        }

    }

    /**
     * @param params 去除参数列表上的“[ ]”
     */
    private void handlerParams(Object[] params) {
        if (params != null)
            for (int i = 0; i < params.length; i++) {
                if (params[i] instanceof ArrayList) {
                    String str = params[i].toString();
                    if (str.length() == 0) continue;
                    if (str.charAt(0) == '[' && str.substring(str.length() - 1).equals("]")) {
                        str = str.substring(1, str.length() - 1);
                        params[i] = str;
                    }
                }
            }
    }

    private Observable request(TYPE type) {
        if (mReturnGenericClazz != null)
            return Observable.create(type, mUrl, mPath, mBody, mHeader, mReturnGenericClazz);
        throw new IllegalStateException("Could not find the generic type that needs to be returned");
    }

    /**
     * @param method 方法
     * @param get    get注解对象
     * @param params 对应参数列表
     * @return Observable实例
     */
    private Observable handlerGet(Method method, Get get, Object[] params) {
        mUrl = mAnna.getBaseUrl() + get.value();
        handlerAnnotation(method, params);
        return request(TYPE.GET);
    }

    /**
     * @param method 方法
     * @param post   post注解对象
     * @param params 对应参数列表
     * @return Observable实例
     */
    private Observable handlerPost(Method method, Post post, Object[] params) {
        mUrl = mAnna.getBaseUrl() + post.value();
        handlerAnnotation(method, params);
        return request(TYPE.POST);
    }

    /**
     * @param method 方法
     * @param get    get注解对象
     * @param params 对应参数列表
     * @return Observable实例
     */
    private Observable handleDelete(Method method, Delete delete, Object[] params) {
        mUrl = mAnna.getBaseUrl() + delete.value();
        handlerAnnotation(method, params);
        return request(TYPE.DELETE);
    }

    /**
     * @param method 方法
     * @param get    get注解对象
     * @param params 对应参数列表
     * @return Observable实例
     */
    private Observable handlePut(Method method, Put put, Object[] params) {
        mUrl = mAnna.getBaseUrl() + put.value();
        handlerAnnotation(method, params);
        return request(TYPE.PUT);
    }

}
