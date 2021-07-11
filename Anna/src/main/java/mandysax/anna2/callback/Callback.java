package mandysax.anna2.callback;

/**
 * @author liuxiaoliu66
 */
public interface Callback<T>
{
   /**
    * @param loaded 是否加载完成
    * @param t 解析好的实体类
    */
   void onResponse(boolean loaded,T t);

   /**
    * @param code 错误code
    */
   void onFailure(int code);
}
