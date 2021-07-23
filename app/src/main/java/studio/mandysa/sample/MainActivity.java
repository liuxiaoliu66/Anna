package studio.mandysa.sample;


import android.app.Activity;
import android.os.Bundle;

import mandysax.anna2.callback.Callback;
import studio.mandysa.sample.logic.model.Api;
import studio.mandysa.sample.logic.model.LoginModel;
import studio.mandysa.sample.logic.network.ServiceCreator;

/**
 * @author liuxiaoliu66
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServiceCreator.create(Api.class).login("mobile", "password").set(new Callback<LoginModel>() {
            @Override
            public void onResponse(boolean loaded, LoginModel loginModel) {
                System.out.println(loginModel.token);
            }

            @Override
            public void onFailure(int code) {
                System.out.println("error code:" + code);
            }
        });
    }
}