package com.newpage.hack2;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Scene;
import android.transition.TransitionSet;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class LoginActivity extends AppCompatActivity {

    private boolean firstLogin = false;

    private String session = null;
    private String token = null;

    class ValidateSessionAndToken extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            MediaType type = MediaType.parse("application/x-www-form-urlencoded");
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create("session=" + session + "&token=" + token, type);
            Request request = new Request.Builder().url("https://newpage.ddns.net/tangle/api/validator.php")
                    .post(body)
                    .build();
            try (Response response = client.newCall(request).execute()) {
                String ans = response.body().string();
                if (ans.equals("false")) {
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(() -> makeLoginScreen());
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 1500);
                    return Boolean.valueOf(false);
                }
            } catch (IOException ex) {
                Log.e("ea", "ead");
            }
            return Boolean.valueOf(true);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            LoginActivity.this.startActivity(new Intent(LoginActivity.this, WaysActivity.class));
                        });
                    }
                };
                Timer timer = new Timer();
                timer.schedule(task, 1500);
            }
        }
    }

    class LoginTask extends AsyncTask<String[], Void, Boolean> {

        @Override
        protected Boolean doInBackground(String[]... strings) {
            String log = strings[0][0];
            String pass = strings[0][1];

            MediaType type = MediaType.parse("application/x-www-form-urlencoded");
            OkHttpClient client = new OkHttpClient();

            RequestBody body = RequestBody.create("email=" + log + "&password=" + pass, type);
            Request request = new Request.Builder().url("https://newpage.ddns.net/tangle/api/auth.php")
                    .post(body)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                String ans = response.body().string();

                if (ans.equals("false")) {
                    Log.e("login eror", "le");
                    return Boolean.valueOf(false); // FASDASDASDKEGKAENHJWE
                }
                JSONObject jsonObject = new JSONObject(ans);
                session = jsonObject.getString("session");
                token = jsonObject.getString("token");
                saveSessionAndToken();
            } catch (IOException ex) {
                Log.e("wtf", "idk");
            } catch (JSONException jex) {
                Log.e("d", "server");
            }
            return new Boolean(true);
        }


        @Override
        protected void onPostExecute(Boolean result) {
            if (result) {
                if (firstLogin) {
                    saveSessionAndToken();
                }
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, WaysActivity.class));
            }
        }
    }

    private void saveSessionAndToken() {
        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root, "p.txt");
        StringBuilder text = new StringBuilder();
        text.append(session);
        text.append("\n");
        text.append(token);

        try {
            BufferedWriter wr = new BufferedWriter(new FileWriter(file));
            wr.write(text.toString());
            wr.flush();
            wr.close();
        } catch (IOException ex) {
            Log.e("ex", "file ex");
        }
    }

    private void loginPress() {
        TextView login = findViewById(R.id.login);
        TextView pass = findViewById(R.id.pass);

        String log = login.getText().toString();
        String password = pass.getText().toString();
        String[] string = new String[2];
        string[0] = log;
        string[1] = password;

        LoginTask backTask = new LoginTask();
        backTask.execute(string);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        PermissionsRequestor permissionsRequestor = new PermissionsRequestor(this);

        final boolean[] gg = {true};
        while (gg[0]) {
        permissionsRequestor.request(new PermissionsRequestor.ResultListener() {
            @Override
            public void permissionsGranted() {
                afterPermissions();
                gg[0] = false;
            }

            @Override
            public void permissionsDenied() {
                Log.e("eeeeeee", "EEEEEEEEE");
            }
        });}

    }

    private void afterPermissions() {
        String root = Environment.getExternalStorageDirectory().toString();
        File file = new File(root, "p.txt");
        StringBuilder text = new StringBuilder();

        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            session = null;
            token = null;
            String line;

            while ((line = br.readLine()) != null) {
                if (session == null) {
                    session = line;
                } else {
                    token = line;
                }
                text.append("\n");
            }
            br.close();
            new ValidateSessionAndToken().execute();
        } catch (FileNotFoundException fex) {
            Log.i("n", "not logined yet");
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(() -> makeLoginScreen());
                }
            };
            Timer timer = new Timer();
            timer.schedule(task, 1000);
            firstLogin = true;
        } catch (IOException iex) {
            Log.e("wtf", "fex");
        }
    }

    private void makeLoginScreen() {

        ViewGroup RootLayout = findViewById(R.id.loginLayout);
        final Scene scene2 = Scene.getSceneForLayout(RootLayout, R.layout.login, this);
        TransitionSet set = new TransitionSet();
        set.addTransition(new Fade());
        //set.addTransition(new Slide(Gravity.TOP));
        // выполняться они будут одновременно
        set.setOrdering(TransitionSet.ORDERING_TOGETHER);
        // уставим свою длительность анимации
        set.setDuration(1000);
        // и изменим Interpolator
        set.setInterpolator(new AccelerateInterpolator());
        TransitionManager.go(scene2, set);

        findViewById(R.id.submitlogin).setOnClickListener(v -> loginPress());
    }
}
