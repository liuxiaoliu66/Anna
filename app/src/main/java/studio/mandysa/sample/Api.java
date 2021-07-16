package studio.mandysa.sample;

import mandysax.anna2.annotation.Body;
import mandysax.anna2.annotation.Post;
import mandysax.anna2.observable.Observable;

public interface Api {
    @Post("api/login")
    Observable<LoginModel> login(@Body("mobile") String mobile, @Body("password") String password);
}
