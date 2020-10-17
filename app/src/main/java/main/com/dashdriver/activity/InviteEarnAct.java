package main.com.dashdriver.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import main.com.dashdriver.MainActivity;
import main.com.dashdriver.R;
import main.com.dashdriver.constant.MyLanguageSession;
import main.com.dashdriver.constant.MySession;


public class InviteEarnAct extends AppCompatActivity {

    private RelativeLayout exit_app_but;
    private TextView invite_code_tv,invitetv,message_tv;

    private String language ="",currency_str="$";
    private MySession mySession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyLanguageSession.get(this).setLangRecreate();
        setContentView(R.layout.activity_invite_earn);
        mySession = new MySession(this);

        idinit();
        clickevent();
    }
    private void clickevent() {
        exit_app_but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        invitetv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT,
                        getResources().getString(R.string.sharetxt)+" "+ MainActivity.promo_code);
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    private void idinit() {
        exit_app_but = findViewById(R.id.exit_app_but);
        invite_code_tv = findViewById(R.id.invite_code_tv);
        invitetv = findViewById(R.id.invitetv);
        message_tv = findViewById(R.id.message_tv);
        invite_code_tv.setText(""+ MainActivity.promo_code);
    }

}
