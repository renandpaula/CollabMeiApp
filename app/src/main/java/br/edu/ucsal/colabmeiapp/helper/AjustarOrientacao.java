package br.edu.ucsal.colabmeiapp.helper;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.media.ExifInterface;

public class AjustarOrientacao extends Object{
    public static String encontraCaminho(Context context, Uri localImagem, Bitmap bitmap){
        //Começa...
        String[] filePath = {MediaStore.Images.Media.DATA};
        Cursor c = context.getContentResolver().query(localImagem, filePath, null, null, null);
        c.moveToFirst();
        int columnindex = c.getColumnIndex(filePath[0]);
        String picturePath = c.getString(columnindex);
        c.close();
        //Termina.
        return picturePath;
    }

    public static Bitmap rotation(Bitmap bitmap, String local){
        ExifInterface exif = null;
        try{
            exif = new ExifInterface(local);
        }catch(Exception e){
            e.printStackTrace();
        }
        int orientacao = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
        Matrix matrix = new Matrix();
        switch (orientacao) {
            case ExifInterface.ORIENTATION_NORMAL:
                matrix.setRotate(0);
                break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    matrix.setRotate(90);
                    break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        matrix.setRotate(180);
                        break;
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            matrix.setRotate(270);
                            break;
        }
        return Bitmap.createBitmap(bitmap, 0,0,bitmap.getWidth(), bitmap.getHeight(), matrix, true);
    }
}



