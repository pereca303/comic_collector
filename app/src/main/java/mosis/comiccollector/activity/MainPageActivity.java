package mosis.comiccollector.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import mosis.comiccollector.MyApplication;
import mosis.comiccollector.R;
import mosis.comiccollector.login.BackPressHandler;
import mosis.comiccollector.login.InvalidUserInfoLoaded;
import mosis.comiccollector.login.LoginDialog;
import mosis.comiccollector.manager.AppManager;
import mosis.comiccollector.manager.handler.JobDoneHandler;

public class MainPageActivity extends AppCompatActivity {

    private Button logout_button;

    private Button read_button;
    private Button collect_button;
    private Button discover_button;
    private Button profile_button;

    private LoginDialog login_dialog;

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        MyApplication.getInstance().registerActivityContext(MainPageActivity.this);

        this.handler = new Handler(Looper.getMainLooper());

        this.initButtons();

        this.resolveUser();
    }

    @Override
    protected void onStart() {
        super.onStart();

        MyApplication.getInstance().registerActivityContext(MainPageActivity.this);

    }

    private void resolveUser() {

        if (AppManager.getInstance().getUsersManager().hasUser()) {

            Log.w("MainActivity", "onCreate: app has user");
            try {

                AppManager.getInstance().showLoadingScreen();

                AppManager.getInstance().getUsersManager().reloadUser(new JobDoneHandler() {
                    @Override
                    public void execute(String message) {

                        Log.w("MainActivity", "execute: reload user done");

                        AppManager.getInstance().hideLoadingScreen();

                    }
                });

            } catch (InvalidUserInfoLoaded invalidUserInfoReload) {

                AppManager.getInstance().makeToast("Local data lost, login again");

                AppManager.getInstance().hideLoadingScreen();

                this.showLoginDialog(true);

            }

        } else {

            AppManager.getInstance().makeToast("App does't have user");

            Log.e("MainActivity", "onCreate: app does not have user");

            this.showLoginDialog(true);

        }

    }

    private void initButtons() {

        this.logout_button = (Button) this.findViewById(R.id.logout_btn);
        this.logout_button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // TODO show dialog with warning message
                // all comic will be deleted after this operation

                AppManager.getInstance().getUsersManager().clearUser();
                // forced login dialog
                showLoginDialog(true);

            }

        });

        this.read_button = (Button) this.findViewById(R.id.read_btn);
        this.read_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent list_intent = new Intent(MainPageActivity.this, ComicListActivity.class);

                list_intent.putExtra("list_context", String.valueOf(ComicListContext.CollectedComics));

                startActivity(list_intent);

            }
        });

        this.collect_button = (Button) this.findViewById(R.id.collect_main_btn);
        this.collect_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent list_indent = new Intent(MainPageActivity.this, ComicListActivity.class);

                list_indent.putExtra("list_context", String.valueOf(ComicListContext.QueuedComics));

                startActivity(list_indent);

            }
        });

        this.discover_button = (Button) this.findViewById(R.id.discover_main_btn);
        this.discover_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent discover_intent = new Intent(MainPageActivity.this, ComicListActivity.class);

                discover_intent.putExtra("list_context", String.valueOf(ComicListContext.DiscoverComics));

                startActivity(discover_intent);

            }
        });


        this.profile_button = (Button) this.findViewById(R.id.profile_main_btn);
        this.profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent profile_intent = new Intent(MainPageActivity.this, ProfileActivity.class);

                startActivity(profile_intent);

            }
        });

    }


    private void showLoginDialog(final boolean forced) {

        this.login_dialog = new LoginDialog(this, forced, new BackPressHandler() {
            @Override
            public void execute() {

                if (forced) {

                    login_dialog.dismiss();
                    finish();

                } else {

                    login_dialog.dismiss();

                }


            }

        });

        this.login_dialog.show();

    }

}
