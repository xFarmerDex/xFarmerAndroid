package cn.ethapp.htclient;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import cn.ethapp.htclient.util.Crypto;
import cn.ethapp.htclient.util.DESUtil;
import cn.ethapp.htclient.util.EthGlobal;

public class BackUpAccountActivity extends BaseInitActivity implements View.OnClickListener{

    private TextView tv_backup_at_once;

    @Override
    protected boolean fitSystemWindow() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_back_up_account;
    }

    @Override
    public void initView() {
        setLeftRes(R.drawable.img_back_black);
        setTextColor(R.color.color_444444);
        setTitle(R.string.text_backup_title);
        tv_backup_at_once = (TextView) findViewById(R.id.tv_backup_at_once);
        tv_backup_at_once.setOnClickListener(this);
    }

    private String words;
    private String nickname, password;

    @Override
    public void initData() {
        words = getIntent().getStringExtra("words");
        nickname = getIntent().getStringExtra("nickname");
        password = getIntent().getStringExtra("password");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_backup_at_once:
                if (TextUtils.isEmpty(words)){
                    words = Crypto.generateMnemonic();
                }
                startActivity(new Intent(this, BackUpWordsActivity.class).putExtra("isNeedVerifyBackup", true).putExtra("words", words).putExtra("nickname", nickname).putExtra("password", password));
                break;
        }
    }
}
