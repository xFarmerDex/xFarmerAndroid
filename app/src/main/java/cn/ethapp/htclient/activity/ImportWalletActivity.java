package cn.ethapp.htclient;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hjq.permissions.OnPermissionCallback;
import com.hjq.permissions.XXPermissions;

import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;

import java.util.List;

import androidx.annotation.Nullable;

import androidx.core.content.ContextCompat;
import cn.ethapp.htclient.base.BaseTitleActivity;
import cn.ethapp.htclient.base.RootActivity;
import cn.ethapp.htclient.util.Constants;
import cn.ethapp.htclient.util.Crypto;
import cn.ethapp.htclient.util.EthGlobal;
import z.j.d.lib.log.Log;
import z.j.d.lib.utils.SPUtils;
import z.j.d.lib.utils.ToastUtil;
import zxing.app.CaptureActivity;

public class ImportWalletActivity extends BaseTitleActivity implements View.OnClickListener{

    private static final String TAG = ImportWalletActivity.class.getSimpleName();

    private EditText edit_words, edit_wallet_name, edit_wallet_pwd, edit_wallet_pwd_enter;

    private TextView tv_create_enter, tv_word_import, tv_pri_import;

    @Override
    protected boolean isNeedRXPermissions() {
        return true;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_import_wallet;
    }

    @Override
    protected boolean fitSystemWindow() {
        return true;
    }

    @Override
    public void initView() {
        setTitle(R.string.text_import_wallet);
        setLeftRes(R.drawable.img_back_black);
        setRightRes(R.drawable.img_scan_black);
        setTextColor(R.color.color_444444);
        edit_words = (EditText) findViewById(R.id.edit_words);
        edit_wallet_name = (EditText) findViewById(R.id.edit_wallet_name);
        edit_wallet_pwd = (EditText) findViewById(R.id.edit_wallet_pwd);
        edit_wallet_pwd_enter = (EditText) findViewById(R.id.edit_wallet_pwd_enter);
        tv_create_enter = (TextView) findViewById(R.id.tv_create_enter);
        tv_word_import = (TextView) findViewById(R.id.tv_word_import);
        tv_pri_import = (TextView) findViewById(R.id.tv_pri_import);
        initListener();
    }

    @SuppressLint("CheckResult")
    @Override
    protected void onRightClick() {
        super.onRightClick();
        //跳转到扫描二维码页面
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        XXPermissions.with(this).permission(permissions).request(new OnPermissionCallback() {
            @Override
            public void onGranted(List<String> permissions, boolean all) {
                if (all){
                    //跳转到扫描二维码页面
//                    startActivityForResult(new Intent(ImportWalletActivity.this, CaptureActivity.class), 1);
                    goScan(new RootActivity.OnScanResultCallback() {
                        @Override
                        public void onScanQrCodeResult(String result) {
                            edit_words.setText(result);edit_words.setSelection(result.length());//将光标移至文字末尾
                        }
                    });
                }else {
                    ToastUtil.showToast("no permission", 2000);
                }
            }
        });
    }

    @Override
    public void initData() {

    }

    private void initListener() {
        tv_create_enter.setOnClickListener(this);
        tv_word_import.setOnClickListener(this);
        tv_pri_import.setOnClickListener(this);
    }

    private int importType = 0;
    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.tv_create_enter:
                importWalletEnter();
                break;
            case R.id.tv_word_import:
                tv_word_import.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_word_import.setBackground(ContextCompat.getDrawable(this, R.drawable.corners_15dp_fc077b_left_bg));
                tv_pri_import.setTextColor(ContextCompat.getColor(this, R.color.color_999999));
                tv_pri_import.setBackground(ContextCompat.getDrawable(this, R.drawable.corners_15dp_eeeeee_right_bg));
                importType = 0;
                edit_words.setHint(R.string.text_input_words);
                break;
            case R.id.tv_pri_import:
                tv_word_import.setTextColor(ContextCompat.getColor(this, R.color.color_999999));
                tv_word_import.setBackground(ContextCompat.getDrawable(this, R.drawable.corners_15dp_eeeeee_left_bg));
                tv_pri_import.setTextColor(ContextCompat.getColor(this, R.color.white));
                tv_pri_import.setBackground(ContextCompat.getDrawable(this, R.drawable.corners_15dp_fc077b_right_bg));
                importType = 1;
                edit_words.setHint(R.string.text_pri_input);
                break;
        }
    }

    private void importWalletEnter() {

        Log.d(TAG, "importWalletEnter");

        if (TextUtils.isEmpty(edit_words.getText().toString().trim())){
            if (importType == 0){
                ToastUtil.showToast(R.string.text_input_words, 2000);
            }else {
                ToastUtil.showToast(R.string.text_input_pri, 2000);
            }
            return;
        }

        if (importType == 0){
            if (!edit_words.getText().toString().contains(" ")){
                ToastUtil.showToast(R.string.text_words_invalid, 2000);
                return;
            }
            String words = edit_words.getText().toString().trim();
            String[] seeds = words.split(" ");
            if (seeds.length < 12){
                ToastUtil.showToast(R.string.text_words_invalid, 2000);
                return;
            }

        }else {
            if (edit_words.getText().toString().contains(" ")){
                ToastUtil.showToast(R.string.text_input_pri_space, 2000);
                return;
            }
        }

        if (TextUtils.isEmpty(edit_wallet_name.getText().toString().trim())){
            ToastUtil.showToast(R.string.text_input_wallet_nickname, 2000);
            return;
        }

        if (TextUtils.isEmpty(edit_wallet_pwd.getText().toString().trim())){
            ToastUtil.showToast(R.string.text_input_wallet_password, 2000);
            return;
        }

        if (TextUtils.isEmpty(edit_wallet_pwd_enter.getText().toString().trim()) || !edit_wallet_pwd.getText().toString().trim().equals(edit_wallet_pwd_enter.getText().toString().trim())){
            ToastUtil.showToast(R.string.text_wallet_password_not_same, 2000);
            return;
        }

        String word = edit_words.getText().toString();
        if (importType != 0){
            if ((word.startsWith("0x") || word.startsWith("0X"))){
                word = word.substring(2);
            }
            boolean b = Crypto.isValidPrivateKey(word);
            if (!b){
                ToastUtil.showToast(R.string.text_words_invalid, 2000);
                return;
            }
        }else {
            if (!MnemonicUtils.validateMnemonic(word)){
                ToastUtil.showToast(R.string.text_words_invalid, 2000);
                return;
            }
        }
        EthGlobal.getInstance().verifyIsExistWallet(edit_wallet_name.getText().toString().trim(), word,  new EthGlobal.VerifyWalletIsExitCallback() {
            @Override
            public void verifyComplete(boolean isNicknameExist, boolean isWalletExist) {
                if (isNicknameExist){
                    ToastUtil.showToast(R.string.text_wallet_nickname_exist, 2000);
                }else {
                    if (isWalletExist){
                        ToastUtil.showToast(R.string.text_wallet_exist, 2000);
                    }else {
                        importWallet();
                    }
                }
            }
        });

    }

    private void importWallet() {
        Log.d(TAG, "importWallet");
        if (importType == 0){
            EthGlobal.getInstance().importPrivateKey(edit_words.getText().toString().trim(), edit_wallet_name.getText().toString().trim(), edit_wallet_pwd.getText().toString().trim(), new EthGlobal.CreatePrivateKeyCallback() {
                @Override
                public void start() {

                }

                @Override
                public void complete(boolean success) {
                    SPUtils.getInstance().saveBoolean(Constants.INIT_COMPLETE, true);
                    startActivity(new Intent(ImportWalletActivity.this, MainActivity.class));
                }
            });
        }else {
            EthGlobal.getInstance().importPrivateKeyPri(edit_words.getText().toString().trim(), edit_wallet_name.getText().toString().trim(), edit_wallet_pwd.getText().toString().trim(), new EthGlobal.CreatePrivateKeyCallback() {
                @Override
                public void start() {

                }

                @Override
                public void complete(boolean success) {
                    SPUtils.getInstance().saveBoolean(Constants.INIT_COMPLETE, true);
                    startActivity(new Intent(ImportWalletActivity.this, MainActivity.class));
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RESULT_OK:
                if (data == null || TextUtils.isEmpty(data.getStringExtra("SCAN_RESULT"))){
                    return;
                }
                edit_words.setText(data.getStringExtra("SCAN_RESULT"));edit_words.setSelection(data.getStringExtra("SCAN_RESULT").length());//将光标移至文字末尾
                break;
            case 300:
                if (data == null || TextUtils.isEmpty(data.getStringExtra("LOCAL_PHOTO_RESULT"))){
                    return;
                }
                edit_words.setText(data.getStringExtra("LOCAL_PHOTO_RESULT"));edit_words.setSelection(data.getStringExtra("LOCAL_PHOTO_RESULT").length());//将光标移至文字末尾
                break;
        }
    }
}
