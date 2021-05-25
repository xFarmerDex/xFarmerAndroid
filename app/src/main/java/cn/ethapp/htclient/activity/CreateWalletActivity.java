package cn.ethapp.htclient;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import cn.ethapp.htclient.util.Crypto;
import cn.ethapp.htclient.util.EthGlobal;

import z.j.d.lib.utils.ToastUtil;

public class CreateWalletActivity extends BaseInitActivity implements View.OnClickListener{

    private EditText edit_wallet_name, edit_wallet_pwd, edit_wallet_pwd_enter;

    private TextView tv_create_enter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_create_wallet;
    }

    @Override
    protected boolean fitSystemWindow() {
        return true;
    }

    @Override
    public void initView() {
        setLeftRes(R.drawable.img_back_black);
        setTitle(R.string.text_create_wallet_title);
        setTextColor(R.color.color_444444);

        edit_wallet_name = (EditText) findViewById(R.id.edit_wallet_name);
        edit_wallet_pwd = (EditText) findViewById(R.id.edit_wallet_pwd);
        edit_wallet_pwd_enter = (EditText) findViewById(R.id.edit_wallet_pwd_enter);
        tv_create_enter = (TextView) findViewById(R.id.tv_create_enter);
        initListener();
    }

    private String words;

    @Override
    public void initData() {
        words = Crypto.generateMnemonic();
    }

    private void initListener() {
        tv_create_enter.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_create_enter:
                createWalletEnter();
                break;
        }
    }

    private void createWalletEnter() {
        String nickname = edit_wallet_name.getText().toString().trim();
        if (TextUtils.isEmpty(nickname)){
            ToastUtil.showToast(R.string.text_input_wallet_nickname, 2000);
            return;
        }

        String password = edit_wallet_pwd.getText().toString().trim();
        if (TextUtils.isEmpty(password)){
            ToastUtil.showToast(R.string.text_input_wallet_password, 2000);
            return;
        }

        String password2 = edit_wallet_pwd_enter.getText().toString().trim();
        if (TextUtils.isEmpty(password2) || !password.equals(password2)){
            ToastUtil.showToast(R.string.text_wallet_password_not_same, 2000);
            return;
        }

        if (TextUtils.isEmpty(words)){
            words = Crypto.generateMnemonic();
        }

        startActivity(new Intent(CreateWalletActivity.this, BackUpAccountActivity.class).putExtra("words", words).putExtra("nickname", nickname).putExtra("password", password));

//        EthGlobal.getInstance().createPrivateKey(nickname, password, new EthGlobal.CreatePrivateKeyCallback() {
//            @Override
//            public void start() {
//
//            }
//
//            @Override
//            public void complete(boolean success) {
//                if (success){
//                    startActivity(new Intent(CreateWalletActivity.this, BackUpAccountActivity.class));
//                }else {
//                    ToastUtil.showToast("failed", 2000);
//                }
//            }
//        });

    }
}
