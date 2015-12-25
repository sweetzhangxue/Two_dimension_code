package com.example.sweet_xue.two_dimension_code;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.ChecksumException;
import com.google.zxing.EncodeHintType;
import com.google.zxing.FormatException;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import java.util.Hashtable;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnLongClickListener {

    private EditText edt_wenan;
    private Button btn_create,btn_saomiao,btn_select;
    private ImageView imageView;

    private String url = "http://www.weibo.com/";
    private int QR_WIDTH = 170;
    private int QR_HEIGHT = 170;
    private Bitmap bitmap;

    private static final  int CHOOCE_PIC = 0;
    private static final  int PHOTO_PIC = 1;
    private String imagePath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {

        edt_wenan = (EditText) findViewById(R.id.edt_wenan);
        btn_create = (Button) findViewById(R.id.btn_create);
        imageView = (ImageView) findViewById(R.id.imageview);
        btn_saomiao = (Button) findViewById(R.id.btn_saomiao);
        btn_select = (Button) findViewById(R.id.btn_select);

        btn_create.setOnClickListener(this);
        btn_saomiao.setOnClickListener(this);
        btn_select.setOnClickListener(this);
        imageView.setOnLongClickListener(this);

        edt_wenan.setText(url);
    }

    /**
     * 生成二维码
     * @param url
     */
    private void createQRImage(String url){
        //判断url的合法性
        if (url == null || "".equals(url) || url.length() < 1){
            return;
        }
        Hashtable<EncodeHintType,String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET, "utf-8");

        try {
            BitMatrix  bitMatrix = new QRCodeWriter().encode(url, BarcodeFormat.QR_CODE,QR_WIDTH,QR_HEIGHT,hints);
            int[] pixels = new int[QR_WIDTH * QR_HEIGHT];

            for (int y = 0;y< QR_HEIGHT;y++){
                for (int x = 0;x<QR_WIDTH;x++){
                    if (bitMatrix.get(x,y)){
                        pixels[y*QR_WIDTH + x] = 0xff000000;
                    }else {
                        pixels[y*QR_WIDTH + x] = 0xffffffff;
                    }
                }
            }

            bitmap = Bitmap.createBitmap(QR_WIDTH, QR_HEIGHT, Bitmap.Config.ARGB_8888);
            bitmap.setPixels(pixels,0,QR_WIDTH,0,0,QR_WIDTH,QR_HEIGHT);
            imageView.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析图片中的二维码
     * @param bitmapPath
     * @return
     */
    private Result paresQRciseBitmap(String bitmapPath){
        Hashtable<EncodeHintType,String> hints = new Hashtable<EncodeHintType, String>();
        hints.put(EncodeHintType.CHARACTER_SET,"utf-8");

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;


        Bitmap bitmap = BitmapFactory.decodeFile(bitmapPath,options);

        options.inSampleSize = options.outHeight / 400;
        if (options.inSampleSize <=0){
            options.inSampleSize = 1;
        }

        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(bitmapPath,options);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try {
            result = reader.decode(binaryBitmap);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }


        return result;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.btn_create:
                createQRImage(url);
                break;
            case R.id.btn_saomiao:
                Intent intent2 = new Intent(MainActivity.this,MipcaActivityCapture.class);
                startActivityForResult(intent2,PHOTO_PIC);
                break;
            case R.id.btn_select:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_PICK);
                intent.setType("image/*");
                Intent intent1 = Intent.createChooser(intent,"选择一张二维码图片");
                startActivityForResult(intent1,CHOOCE_PIC);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imagePath = null;
        if (resultCode == RESULT_OK){
            switch (requestCode){
                case PHOTO_PIC:
                    String result = data.getExtras().getString("result");
                    Intent intent = new Intent(MainActivity.this,MoWebActivity.class);
                    intent.putExtra("url_", result);
                    startActivity(intent);
                    break;

                case CHOOCE_PIC:
                    String[] proj = new String[]{MediaStore.Images.Media.DATA};
                    Cursor cursor = MainActivity.this.getContentResolver().query(data.getData(), proj, null, null, null);
                    if (cursor.moveToFirst()){
                        int columIndex = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
                        imagePath = cursor.getString(columIndex);

                    }
                    cursor.close();

                    Result result1 = paresQRciseBitmap(imagePath);
                    Intent intent1 = new Intent(MainActivity.this,MoWebActivity.class);
                    intent1.putExtra("url_",result1.toString());
                    startActivity(intent1);
                    break;
            }
        }
    }

    @Override
    public boolean onLongClick(View v) {
        //此处可弄一个类似微信的长按二维码的dialog
       Bitmap bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        System.out.println("====bitmap==onLong="+bitmap);
        RGBLuminanceSource rgbLuminanceSource = new RGBLuminanceSource(bitmap);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(rgbLuminanceSource));
        QRCodeReader reader = new QRCodeReader();
        Result result = null;
        try {
            result = reader.decode(binaryBitmap);
            Intent intent2 = new Intent(MainActivity.this,MoWebActivity.class);
            intent2.putExtra("url_",result.toString());
            startActivity(intent2);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (ChecksumException e) {
            e.printStackTrace();
        } catch (FormatException e) {
            e.printStackTrace();
        }
        return true;
    }
}
