package cn.ethapp.htclient;

import androidx.appcompat.app.AppCompatActivity;
import cn.ethapp.htclient.base.BaseTitleActivity;
import z.j.d.lib.utils.ToastUtil;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class BackUpPrivateKeyActivity extends BaseTitleActivity {

    private TextView tv_pri, tv_copy;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_back_up_private_key;
    }

    @Override
    protected boolean fitSystemWindow() {
        return true;
    }

    @Override
    public void initView() {
        setTitle(R.string.text_backup_pri);
        setLeftRes(R.drawable.img_back_black);
        tv_pri = (TextView) findViewById(R.id.tv_pri);
        tv_copy = (TextView) findViewById(R.id.tv_copy);
        String pri = getIntent().getStringExtra("pri");
        tv_pri.setText(pri);
        tv_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("ht", pri);
                if (cmb != null){
                    cmb.setPrimaryClip(clip);
                    ToastUtil.showToast(R.string.text_copy_success, 2000);
                }
            }
        });
    }

    @Override
    public void initData() {

    }
}
