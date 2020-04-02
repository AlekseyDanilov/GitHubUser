package ru.alekseydanilov.githubuser;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import androidx.appcompat.app.AppCompatActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.alekseydanilov.githubuser.retrofit.App;
import ru.alekseydanilov.githubuser.retrofit.model.UserModel;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.username_edit)
    EditText usernameEdit;

    @BindView(R.id.ok_btn)
    Button okBtn;

    @BindView(R.id.user_info_scroll)
    ScrollView userInfoScroll;

    @BindView(R.id.photo)
    ImageView photo;

    @BindView(R.id.name)
    TextView name;

    @BindView(R.id.bio)
    TextView bio;

    @BindView(R.id.website)
    TextView website;

    @BindView(R.id.company)
    TextView company;

    @BindView(R.id.email)
    TextView email;

    @BindView(R.id.followers_info)
    TextView followersInfo;

    @BindView(R.id.location)
    TextView location;

    @BindView(R.id.public_repos)
    TextView publicRepos;

    @BindView(R.id.html_url)
    TextView htmlUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.ok_btn)
    void clickOkBtn() {
        userInfoScroll.setVisibility(View.GONE);

        try {
            InputMethodManager inputManager = (InputMethodManager) MainActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(MainActivity.this.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (NullPointerException ignored) {
        }

        if (usernameEdit.getText().toString().equals("")) {
            Toast.makeText(this, "Введите username", Toast.LENGTH_SHORT).show();
        } else {
            // Использую Single вместо Observable, так как оожидаем только один элемент в onNext,
            // либо ошибку в onError, после чего передача данных будет считаться завершенной.
            Single<UserModel> single = App.getUserController().getUser(usernameEdit.getText().toString());

            single.subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new SingleObserver<UserModel>() {
                        @Override
                        public void onError(Throwable e) {
                            if (e.getMessage().equals("HTTP 404 Not Found")) {
                                Toast.makeText(MainActivity.this,
                                        "Данный пользователь не наден",
                                        Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this,
                                        "Не удалось получить данные",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onSubscribe(Disposable d) {
                            Log.d("Subscribe", "Подписка на событие запущена");
                        }

                        @Override
                        public void onSuccess(UserModel userModel) {
                            initUserInfo(userModel);
                        }
                    });
        }
    }

    private void initUserInfo(UserModel userModel) {
        userInfoScroll.setVisibility(View.VISIBLE);

        if (userModel.getAvatarUrl() != null) {
            photo.setVisibility(View.VISIBLE);
            Uri uri = Uri.parse(userModel.getAvatarUrl());
            Glide.with(this)
                    .load(uri)
                    .centerCrop()
                    .into(photo);
        } else {
            photo.setVisibility(View.GONE);
        }

        if (userModel.getName() != null) {
            name.setText("Имя пользователя: ".concat(userModel.getName()));
        } else {
            name.setText("Имя пользователя не указано");
        }

        if (userModel.getBio() != null) {
            bio.setText("О пользователе: ".concat(userModel.getBio()));
        } else {
            bio.setText("О пользователе не указано");
        }

        if (userModel.getBlog() != null) {
            website.setText("Website: ".concat(userModel.getBlog()));
        } else {
            website.setText("Website не указан");
        }

        if (userModel.getCompany() != null) {
            company.setText("Компания: ".concat(userModel.getCompany()));
        } else {
            company.setText("Компания не указана");
        }

        if (userModel.getEmail() != null) {
            email.setText("E-mail: ".concat(userModel.getEmail()));
        } else {
            email.setText("E-mail не указан");
        }

        if (userModel.getFollowers() != null) {
            followersInfo.setText("Количество фолловеров: ".concat(userModel.getFollowers().toString()));
        }

        if (userModel.getLocation() != null) {
            location.setText("Страна/Город: ".concat(userModel.getLocation()));
        } else {
            location.setText("Страна/Город не указаны");
        }

        if (userModel.getPublicRepos() != null) {
            publicRepos.setText("Открытые репозитории: ".concat(userModel.getPublicRepos().toString()));
        }

        if (userModel.getHtmlUrl() != null) {
            htmlUrl.setText(userModel.getHtmlUrl());
        }
    }
}
