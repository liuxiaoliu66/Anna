### Anna

Anna 是基于okhttp二次封装的网络请求库

### 优点

1. 使用非常方便灵活
2. 请求的方法参数注解都可以定制
3. 强大的数据解析功能

### 使用介绍

使用 Anna 的步骤共有7个：

1. 添加Anna库的依赖
2. 创建 接收服务器返回数据 的类
3. 创建 用于描述网络请求 的接口
4. 创建 Anna 实例
5. 发送网络请求 (异步)

### 步骤1

* 在项目根目录下的 `build.gradle` 文件中加入

```groovy
buildscript {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
```

* 在项目 app 模块下的 `build.gradle` 文件中加入

```groovy
android {
    compileOptions {
        targetCompatibility JavaVersion.VERSION_1_8
        sourceCompatibility JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation 'com.gitee.liuxiaoliu66:anna:1.3.0'
}
```

### 步骤2

假设服务端返回的数据如下

```
{
  "data": {
    "Fans": 999,
    "head_img": null,
    "nick_name": "封茗囧菌",
    "status": true
  },
  "msg": "Request successful"
}
```

创建一个类，名称随意(UserModel)

```
public class UserModel {

    @Value("Fans")
    public int fans;

    @Value("head_img")
    public String headImg;

    @Value("nick_name")
    public String name;

    @Value("status")
    public boolean status;
}
```

### 步骤3

创建一个接口，名称随意(Api)，方法名随意(getUserInfo)

```
@Get("api/user")
@Path("data")
Observable<UserModel> getUserInfo(@Query("id") String userId);
```

@Path将在疑难解答中进行说明

`Anna`目前支持以下请求类型
- Get
- Post
- Put
- Delete

支持携带以下数据
- Query
- Body
- Header

 **不明白具体含义请查看文末的疑难解答** 

### 步骤4

`Anna2 anna2 = Anna2.build().baseUrl("xxxx");`

xxxx替换为你的服务器url，所有请求的url将是你的baseUrl+@Get，@Post，@Put，@Delete上的注解内容组成的

### 步骤5


```
anna2.newProxy(Api.class).getUserInfo("199867").set(new Callback<UserModel>() {
            @Override
            public void onResponse(boolean loaded, UserModel userModel) {
                System.out.println(userModel.name);
            }

            @Override
            public void onFailure(int code) {
                System.out.println("error");
            }
        });
```

如果你还没有明白的话，查看文末的疑难解答
**因为服务端是虚构的，所以这段代码是无法正常运行的** 

### 最佳实践

请查看demo

### 进阶用法

若解析的数据类型为JSONArray

```
{
  "data": {
    "allActivity": [
      {
        "activity_name": "test",
        "endTime": 1625065200,
        "id": 2,
        "startTime": 1624982400
      }
    ]
  },
  "msg": "Request successful"
}
```

你的数据类应该是这样的

```
@Array
public class DataModel{

    @Value("activity_name")
    public String name;
    
}
```

然后在描述网络请求的接口上添加如下代码

```
@Path("data/allActivity")
```

添加后是这样的

```
@Post("api/login")
@Path("data/allActivity")
Observable<LoginModel> login(@Body("mobile") String mobile, @Body("password") String password);
```

@Path和@Array注解的作用想必聪明的你已经明白了

`Anna`是支持嵌套解析的，并且还可以自动装载List

```
public class PlaylistInfoModel {
    @Value("name")
    public String name;

    @Value("coverImgUrl")
    public String coverImgUrl;

    @Value("trackIds")
    public List<SongList> songList;

    public static class SongList {

        @Value("id")
        public String id;

    }
}
```


### 疑难解答

Q：请求错误的code=-1是什么情况

A：未知错误，通常是okhttp无法进行网络请求，可能是没有权限，或者请求的配置问题。可以判断code是否等于Anna2.UNKNOWN，然后对用户进行反馈

Q：若不需要关心返回的数据，应该如何进行请求

A：Callback改为ResponseBody

Q：是否支持提交List

A：支持，List将被转换为xxx,xxxx,xxxxx的形式进行提交

注解说明其`作用域`：

| 注解 | 作用 | 作用域 |
|:----:|:----:|:----:|
|   Array   |   告诉Anna，这个数据类解析的对象是JSONArray   |   类   |
|   Body   |   请求体   |   参数   |
|   Header   |   请求头  |   参数   |
|   Query   |   请求参数   |   参数   |
|   Value   |   解析对应key并赋值   |   变量   |
|   Path   |   设置解析路径   |   方法和变量   |
