package studio.mandysa.sample;


import android.app.Activity;
import android.os.Bundle;

import mandysax.anna2.Anna2;
import mandysax.anna2.callback.Callback;

/**
 * @author liuxiaoliu66
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Anna2 anna2 = Anna2.build().baseUrl("xxxx");
        anna2.newProxy(Api.class).login("xxxx", "xxxx").set(new Callback<LoginModel>() {
            @Override
            public void onResponse(boolean loaded, LoginModel loginModel) {
                System.out.println(loginModel.token);
            }

            @Override
            public void onFailure(int code) {
                System.out.println("error");
            }
        });
    }
}