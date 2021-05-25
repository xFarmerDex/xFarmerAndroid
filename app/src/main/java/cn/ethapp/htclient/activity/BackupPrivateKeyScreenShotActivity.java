package cn.ethapp.htclient;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.nio.ByteBuffer;


import cn.ethapp.htclient.base.BaseActivity;
import cn.ethapp.htclient.util.Crypto;
import cn.ethapp.htclient.util.QRHelper;
import z.j.d.lib.log.Log;
import z.j.d.lib.utils.StatusBarUtil;
import z.j.d.lib.utils.ToastUtil;
import zxing.util.BitmapUtil;

@Deprecated
public class BackupPrivateKeyScreenShotActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_name, tv_address, tv_save_words, tv_save;
    private ImageView img_qr, img_back;

    private static final int REQUEST_CODE = 100;
    public MediaProjectionManager mProjectionManager;

    private int IMAGES_PRODUCED;
    private static final String SCREENCAP_NAME = "screencap";

    private MediaProjection mMediaProjection;
    private ImageReader mImageReader;
    private VirtualDisplay mVirtualDisplay;
    private int mDensity;
    private int mWidth;
    private int mHeight;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_backup_private_key_screen_shot);
        initView();
        initData();
    }

    private void initView() {
        StatusBarUtil.setStatusBarDarkTheme(this, false);
        tv_name = (TextView) findViewById(R.id.tv_name);
        tv_address = (TextView) findViewById(R.id.tv_address);
        tv_save = (TextView) findViewById(R.id.tv_save);
        img_qr = (ImageView) findViewById(R.id.img_qr);
        img_back = (ImageView) findViewById(R.id.img_back);
        tv_save_words = (TextView) findViewById(R.id.tv_save_words);
        tv_save_words.setOnClickListener(this);
        tv_save.setOnClickListener(this);
        img_back.setOnClickListener(this);
    }

    private void initData() {
//        String pri_words = getIntent().getStringExtra("pri_words");
//        String nickname = getIntent().getStringExtra("nickname");
//        String pri = Crypto.generatePrivateKeyFromMnemonic(pri_words);
//        String address = Crypto.generateAddressFromPriv(pri);
////        tv_address.setText(MsgUtil.getShortWalletAddress(address, 9, 8));
//        tv_address.setText(address);
//        tv_name.setText(nickname);
//        Bitmap qr = QRHelper.createQRImage(pri_words, 400, 400);
//        if (qr != null){
//            img_qr.setImageBitmap(qr);
//        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_save_words:
                String pri_words = getIntent().getStringExtra("pri_words");
                ClipboardManager cmb = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("eva", pri_words);
                if (cmb != null){
                    cmb.setPrimaryClip(clip);
                    ToastUtil.showToast(R.string.text_copy_success, 2000);
                }
                break;
            case R.id.tv_save:
                getMediaProjectionManger();
                break;
            case R.id.img_back:
                finish();
                break;
        }
    }


    private void getMediaProjectionManger() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mProjectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
            if (mProjectionManager != null) {
                startActivityForResult(mProjectionManager.createScreenCaptureIntent(), REQUEST_CODE);
            }
        }else {
            ToastUtil.showToast("error...", 2000);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                mMediaProjection = mProjectionManager.getMediaProjection(resultCode, data);
            }
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            mDensity = metrics.densityDpi;

            WindowManager wm = (WindowManager) getBaseContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            mWidth = size.x;
            mHeight = size.y;
            Log.d("width:" + mWidth + "   height：" + mHeight);

            // start capture reader
            //mImageReader = ImageReader.newInstance(mWidth, mHeight, PixelFormat.RGB_8888, 2);
            mImageReader = ImageReader.newInstance(mWidth, mHeight, 0x01, 2);
            //mVirtualDisplay = mMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, VIRTUAL_DISPLAY_FLAGS, mImageReader.getSurface(), null, null);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && mMediaProjection != null) {
                mVirtualDisplay = mMediaProjection.createVirtualDisplay(SCREENCAP_NAME, mWidth, mHeight, mDensity, DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, mImageReader.getSurface(), null, null);
            }
            mImageReader.setOnImageAvailableListener(new ImageAvailableListener(), null);

        }
    }

    private class ImageAvailableListener implements ImageReader.OnImageAvailableListener {
        @Override
        public void onImageAvailable(ImageReader reader) {
            try (Image image = reader.acquireLatestImage()) {
                if (image != null) {
                    String name = String.valueOf("eva" + System.currentTimeMillis() + ".jpeg");
                    IMAGES_PRODUCED++;
                    Log.e("captured image: ", String.valueOf(IMAGES_PRODUCED));

//                    if (IMAGES_PRODUCED % 10 == 0){
//                        saveJpeg(image, name);
//                    }
                    if (IMAGES_PRODUCED == 10){
                        saveJpeg(image, name);
                    }
                    image.close();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private void saveJpeg(Image image,String name) {

        Image.Plane[] planes = image.getPlanes();
        ByteBuffer buffer = planes[0].getBuffer();
        int pixelStride = planes[0].getPixelStride();
        int rowStride = planes[0].getRowStride();
        int rowPadding = rowStride - pixelStride * mWidth;

        Bitmap bitmap = Bitmap.createBitmap(mWidth + rowPadding / pixelStride, mHeight, Bitmap.Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
        //bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//        ImageSaveUtil.saveBitmap2file(bitmap,getApplicationContext(),name);
        new Thread(new Runnable() {
            @Override
            public void run() {
                BitmapUtil.saveBitmap2file(bitmap, name, BackupPrivateKeyScreenShotActivity.this);
            }
        }).start();
        mImageReader.setOnImageAvailableListener(null, null);
        IMAGES_PRODUCED = 0;
    }
}
