package br.com.wma.tools.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.ConnectivityManager;
import android.os.Build;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Locale;

import br.com.wma.tools.exception.WMAException;
import br.com.wma.tools.interfaces.WMADownloadListener;

public class WMAUtilities {
    static NumberFormat nf = new DecimalFormat("#,##0.00", new DecimalFormatSymbols(new Locale("pt", "BR")));

    public static String getMonetaryStringValue(double value){
        return nf.format(value);
    }

    public static double getMonetaryDoubleValue(String value) throws ParseException {
        return nf.parse(value).doubleValue();
    }

    public static boolean isStoragePermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED &&
                    context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSION", "Permission is granted");

                return true;
            } else {
                Log.v("PERMISSION", "Permission is revoked");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PERMISSION", "Permission is granted");
            return true;
        }
    }

    public static boolean isRecordAudioPermissionGranted(Context context) {
        if (Build.VERSION.SDK_INT >= 23) {
            if (context.checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                Log.v("PERMISSION", "Permission is granted");

                return true;
            } else {
                Log.v("PERMISSION", "Permission is revoked");
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            Log.v("PERMISSION", "Permission is granted");
            return true;
        }
    }

    public static void createDirectory(String directoryPath, Context context) throws WMAException {
        boolean checkPermission = isStoragePermissionGranted(context);
        if(!checkPermission)
            throw new WMAException("Você não possui permissão para criar/ler arquivos!");

        File directory = new File(directoryPath);

        if(!directory.isDirectory())
            throw new WMAException("O Caminho informado não é um diretório!");

        if(!directory.exists()) {
            boolean resultDir = directory.mkdir();
            if(!resultDir)
                throw new WMAException("Não foi possível criar o diretório: " + "\'" + directoryPath + "\'");
        }
    }

    public static void createDirectory(File directory, Context context) throws WMAException {
        boolean checkPermission = isStoragePermissionGranted(context);
        if(!checkPermission)
            throw new WMAException("Você não possui permissão para criar/ler arquivos!");

        if(directory == null)
            throw new WMAException("File informado não foi instanciado ainda!");

        if(directory.exists()){
            if(!directory.isDirectory())
                throw new WMAException("O Caminho informado não é um diretório!");
        }

        if(!directory.exists()) {
            boolean resultDir = directory.mkdirs();
            if(!resultDir)
                throw new WMAException("Não foi possível criar o(s) diretório(s)!");
        }
    }

    public static File checkIfStreamFileExistOnDisk(String urlStream, File dir){
        String filename = urlStream.substring(urlStream.lastIndexOf("/") + 1, urlStream.length());

        File aux;

        if(dir.exists() && dir.isDirectory())
            aux = new File(dir.getAbsolutePath() + "/" + filename);
        else if(dir.exists() && dir.isFile())
            aux = new File(dir.getParent() + "/" + filename);
        else
            return null;

        if(aux.exists())
            return aux;
        else
            return null;
    }

    public static Bitmap base64ToBitmap(String base64Str) throws IllegalArgumentException {
        byte[] decodedBytes = Base64.decode(
                base64Str.substring(base64Str.indexOf(",")  + 1),
                Base64.DEFAULT
        );

        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String bitmapToBase64(Bitmap bitmap) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);

        return Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
    }

    /**
     * Verifica se o dispositivo está com uma conexão ativa com à internet.
     * @param context necessário para recuperar o serviço que controla a conectividade do dispositivo.
     * @return true se existir conexão, false caso não exista conexão.
     */
    public static boolean verifyConnection(Context context) {
        boolean connected;
        ConnectivityManager conectivtyManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conectivtyManager.getActiveNetworkInfo() != null
                && conectivtyManager.getActiveNetworkInfo().isAvailable()
                && conectivtyManager.getActiveNetworkInfo().isConnected()) {
            connected = true;
        } else {
            connected = false;
        }

        return connected;
    }

    /**
     * Faz o download de um arquivo à partir de uma URL válida e salva no local de destino informado,
     * o local poderá ser o diretório onde o arquivo será salvo, ou um arquivo com o nome préviamente informado.
     * Durante o download vários eventos são lançados e capturados pelo listener
     * @param urlFile Link do arquivo para fazer o download
     * @param fileOutput Local onde será salvo o arquivo baixado
     * @param smnDownloadListener Listener que lança eventos durante o download do arquivo
     */
    public static void downloadFile(String urlFile, File fileOutput, WMADownloadListener smnDownloadListener){
        try {
            smnDownloadListener.onStart();

            URL url = new URL(urlFile);
            HttpURLConnection urlCon = (HttpURLConnection) url.openConnection();

            urlCon.setReadTimeout(30000);
            urlCon.setConnectTimeout(30000);
            urlCon.setDoInput(true);
            urlCon.setRequestMethod("GET");
            urlCon.connect();

            InputStream in = urlCon.getInputStream();

            FileOutputStream fos;

            String filename = urlFile.substring(urlFile.lastIndexOf("/") + 1, urlFile.length());

            if(fileOutput.isDirectory())
                fos = new FileOutputStream(fileOutput.getAbsolutePath() + "/" + filename);
            else
                fos = new FileOutputStream(fileOutput);

            byte buff[] = new byte[1024];
            int bytesRead;
            long totalBytesRead = 0;
            int percentCompleted = 0;
            long fileSize = urlCon.getContentLength();

            while ((bytesRead = in.read(buff)) != -1) {
                fos.write(buff, 0, bytesRead);
                fos.flush();

                totalBytesRead += bytesRead;
                int newPercent = (int) (totalBytesRead * 100 / fileSize);

                if(percentCompleted != newPercent){
                    smnDownloadListener.onProgress(percentCompleted);
                    percentCompleted = newPercent;
                }
            }

            fos.close();

            smnDownloadListener.onComplete();
        } catch (IOException e) {
            e.printStackTrace();
            smnDownloadListener.onFail(e);
        }
    }

    /**
     * Recupera um bitmap para ser usado como thumb de um vídeo informado
     * @param videoPath URL do vídeo strem
     * @param timeAt tempo do vídeo em que seja necessário retirar a imagem
     * @return retorna o bitmap com a imagem no tempo de vídeo em que foi passado
     * @throws Throwable caso algum erro aconteça.
     */
    public static Bitmap retriveVideoFrameFromVideo(String videoPath, long timeAt)
            throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            if (Build.VERSION.SDK_INT >= 14)
                mediaMetadataRetriever.setDataSource(videoPath, new HashMap<String, String>());
            else
                mediaMetadataRetriever.setDataSource(videoPath);

            bitmap = mediaMetadataRetriever.getFrameAtTime(timeAt, MediaMetadataRetriever.OPTION_CLOSEST);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable(
                    "Exception in retriveVideoFrameFromVideo(String videoPath)"
                            + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }
}
