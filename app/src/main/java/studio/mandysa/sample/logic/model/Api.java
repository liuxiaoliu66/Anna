package studio.mandysa.sample.logic.model;

import mandysax.anna2.annotation.Body;
import mandysax.anna2.annotation.Post;
import mandysax.anna2.observable.ObservableImpl;

public interface Api {
    @Post("api/login")
    ObservableImpl<LoginModel> login(@Body("mobile") String mobile, @Body("password") String password);
}
