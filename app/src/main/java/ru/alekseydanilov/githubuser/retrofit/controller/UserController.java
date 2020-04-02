package ru.alekseydanilov.githubuser.retrofit.controller;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Path;
import ru.alekseydanilov.githubuser.retrofit.model.UserModel;

/**
 * Created by adanilov on 01,April,2020
 */
public interface UserController {

    @GET("users/{username}")
    Single<UserModel> getUser(@Path("username") String username);
}
