/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.diamond.jogger.base.scan;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.diamond.jogger.base.R;
import com.diamond.jogger.base.scan.camera.CameraManager;
import com.diamond.jogger.base.scan.decode.DecodeThread;
import com.diamond.jogger.base.scan.utils.BeepManager;
import com.diamond.jogger.base.scan.utils.CaptureActivityHandler;
import com.diamond.jogger.base.scan.utils.InactivityTimer;
import com.diamond.jogger.base.utils.QMUIStatusBarHelper;
import com.diamond.jogger.base.utils.QrUtils;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.DecodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Hashtable;

/**
 * 扫描二维码界面
 */
public final class CaptureActivity extends AppCompatActivity implements
        SurfaceHolder.Callback {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private Rect mCropRect = null;
    private boolean isHasSurface = false;
    public static final int RESULT_CODE = 101;
    public static final int PICK_FROM_FILE = 103;
    private String photo_path;
    private boolean isOpenFlashlight = false;
    private SurfaceView mCapturePreview;
    private RelativeLayout mRlCapture;
    private RelativeLayout mCaptureContainer;


    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);
        init(savedInstanceState);
    }

    protected void init(Bundle savedInstanceState) {
        mCapturePreview = findViewById(R.id.capture_preview);
        mRlCapture = findViewById(R.id.rl_capture);
        findViewById(R.id.tv_ablum).setOnClickListener(v -> {
            getAlbum();
        });
        mCaptureContainer = findViewById(R.id.capture_container);
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        View captureScanLine = findViewById(R.id.capture_scan_line);
        captureScanLine.startAnimation(animation);
        QMUIStatusBarHelper.setStatusBarDarkMode(this);
        QMUIStatusBarHelper.setStatusbarColor(this, Color.parseColor("#33333333"));
//        getAlbum();
    }


    private void getAlbum() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(
                Intent.createChooser(intent, "从相册选择"), PICK_FROM_FILE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(this);
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);
        handler = null;
        if (isHasSurface) {
            initCamera(mCapturePreview.getHolder());
        } else {
            mCapturePreview.getHolder().addCallback(this);
        }
        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        if (inactivityTimer != null) {
            inactivityTimer.onPause();
            beepManager.close();
            cameraManager.closeDriver();
            if (!isHasSurface) {
                mCapturePreview.getHolder().removeCallback(this);
            }
        }
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (inactivityTimer != null) {
            inactivityTimer.shutdown();
        }
        super.onDestroy();
    }


    @Override
    public void surfaceCreated(final SurfaceHolder holder) {
        if (!isHasSurface) {
            isHasSurface = true;
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    initCamera(holder);
                }
            });
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }


    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager,
                        DecodeThread.ALL_MODE);
            }
            initCrop();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
            displayFrameworkBugMessageAndExit();
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            displayFrameworkBugMessageAndExit();
        }
    }

    /**
     * 当初始化失败后给的提示
     */
    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.app_name));
        builder.setMessage("相机未授权,不能进行二维码扫描");
        builder.setPositiveButton("OK", (dialog, which) -> finish());
        builder.setOnCancelListener(dialog -> finish());
        builder.show();
    }


    public Rect getCropRect() {
        return mCropRect;
    }


    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;
        //获取布局中扫描框的位置信息
        int[] location = new int[2];
        mRlCapture.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = mRlCapture.getWidth();
        int cropHeight = mRlCapture.getHeight();

        //获取布局容器的宽高
        int containerWidth = mCaptureContainer.getWidth();
        int containerHeight = mCaptureContainer.getHeight();

        //计算最终截取的矩形的左上角顶点x坐标
        int x = cropLeft * cameraWidth / containerWidth;
        //计算最终截取的矩形的左上角顶点y坐标
        int y = cropTop * cameraHeight / containerHeight;

        //计算最终截取的矩形的宽度
        int width = cropWidth * cameraWidth / containerWidth;
        //计算最终截取的矩形的高度
        int height = cropHeight * cameraHeight / containerHeight;

        //生成最终的截取的矩形
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 扫描二维码图片的方法
     */
    public Result scanningImage(String path) {
        if (TextUtils.isEmpty(path)) {
            return null;
        }
        Hashtable<DecodeHintType, String> hints = new Hashtable<>();
        hints.put(DecodeHintType.CHARACTER_SET, "UTF8"); // 设置二维码内容的编码
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 先获取原大小
        Bitmap scanBitmap = BitmapFactory.decodeFile(path, options);
        options.inJustDecodeBounds = false; // 获取新的大小
        int sampleSize = (int) (options.outHeight / (float) 200);
        if (sampleSize <= 0)
            sampleSize = 1;
        options.inSampleSize = sampleSize;
        scanBitmap = BitmapFactory.decodeFile(path, options);
        int width = scanBitmap.getWidth();
        int height = scanBitmap.getHeight();
        int[] pixels = new int[width * height];
        scanBitmap.getPixels(pixels, 0, width, 0, 0, width, height);

        //第三个参数是图片的像素
        RGBLuminanceSource source = new RGBLuminanceSource(width, height,
                pixels);
        BinaryBitmap bitmap1 = new BinaryBitmap(new HybridBinarizer(source));
        QRCodeReader reader = new QRCodeReader();
        try {
            return reader.decode(bitmap1, hints);

        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取二维码信息成功,做发送消息
     *
     * @param rawResult 结果
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        inactivityTimer.onActivity();
        beepManager.playBeepSoundAndVibrate();
        String resultText = rawResult.getText();
        if (TextUtils.isEmpty(resultText)) {
            Toast.makeText(this, "扫描结果有误", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        //不拦截二维码，将二维码返回
        Intent resultIntent = new Intent();
        bundle.putInt("width", mCropRect.width());
        bundle.putInt("height", mCropRect.height());
        bundle.putString("result", rawResult.getText());
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case 0x01:
                    try {
                        // 获取选中图片的路径
                        Cursor cursor = getContentResolver().query(data.getData(),
                                null, null, null, null);
                        if (cursor.moveToFirst()) {
                            photo_path = cursor.getString(cursor
                                    .getColumnIndex(MediaStore.Images.Media.DATA));
                        }
                        cursor.close();
                        new Thread(() -> {
                            Result result = scanningImage(photo_path);
                            if (result != null) {
                                handleDecode(result, new Bundle());
                            }
                        }).start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case PICK_FROM_FILE:
                    doCrop(data.getData());
                    break;
            }
        }
    }

    private void doCrop(Uri uri) {
        try {
            Bitmap bitmap = getBitmapFormUri(this, uri);
            String qrcode = QrUtils.identifyImageQr(bitmap);
            // TODO: 2019/9/17 识别二维码
            Log.e(TAG, "----------qrcode:" + qrcode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过uri获取图片并进行压缩
     */
    public static Bitmap getBitmapFormUri(Activity ac, Uri uri) throws IOException {
        InputStream input = ac.getContentResolver().openInputStream(uri);
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        if (input != null) input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;
        float hh = 800f;//这里设置高度为800f
        float ww = 480f;//这里设置宽度为480f
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;//optional
        bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;//optional
        input = ac.getContentResolver().openInputStream(uri);
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        if (input != null) input.close();
        return bitmap;
    }

}