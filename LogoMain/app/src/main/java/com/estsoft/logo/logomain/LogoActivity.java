package com.estsoft.logo.logomain;

import android.content.Intent;
import android.media.tv.TvContract;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

public class LogoActivity extends AppCompatActivity {

    Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        /* 2초동안 인트로 로고화면이 뜨게한다. */
        handler = new Handler();
        handler.postDelayed(irun, 3000);
    }

    Runnable irun = new Runnable() {
        @Override
        public void run() {
            Intent intent = new Intent(LogoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();

            /* fade in 으로 시작하여 fade out 으로 로고 화면이 꺼지게 애니메이션 추가 */
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    };

    /* 로고화면 중간에 뒤로가기 버튼을 눌러서 꺼졌을 시 2초 후 메인 페이지가 뜨는 것을 방지 */
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        handler.removeCallbacks(irun);
    }
}
