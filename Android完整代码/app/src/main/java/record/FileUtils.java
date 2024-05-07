package record;

import static android.os.Environment.getExternalStorageState;

import android.content.pm.PackageManager;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by HXL on 16/8/11.
 * 管理录音文件的类
 */
public class FileUtils {


    private  static String rootPath="pauseRecordDemo";
    //原始文件(不能播放)
    private final static String AUDIO_PCM_BASEPATH = "/"+rootPath+"/pcm/";
    //可播放的高质量音频文件
    private final static String AUDIO_WAV_BASEPATH = "/"+rootPath+"/wav/";

    private static void setRootPath(String rootPath){
        FileUtils.rootPath=rootPath;
    }

    public static String getPcmFileAbsolutePath(String fileName){
        if(TextUtils.isEmpty(fileName)){
            throw new NullPointerException("fileName isEmpty");
        }
        if(!isSdcardExit()){
            throw new IllegalStateException("sd card no found");
        }
        String mAudioRawPath = "";
        if (isSdcardExit()) {
            if (!fileName.endsWith(".pcm")) {
                fileName = fileName + ".pcm";
            }

            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath();
            File directory = new File(fileBasePath);

            if(directory.exists()){
                fileBasePath = fileBasePath + AUDIO_PCM_BASEPATH;
            }

            File dir = new File(fileBasePath);
            //创建目录
            if (!dir.exists()) {
                try{
                    if(!dir.mkdirs()){
                        // 目录创建失败
                        Log.e("FileUtils_getPcmFileAbsolutePath", "Failed to create directory: " + dir.getAbsolutePath());
                    }
                }catch (Exception e){
                    Log.e("FileUtils_getPcmFileAbsolutePath", "Error creating directory", e);
                    return ""; // 返回空字符串表示创建目录失败
                }

            }
            if(dir.exists()){
                Log.d("FileUtils_getPcmFileAbsolutePath", "Successed to create directory: " + dir.getAbsolutePath());

            }

            mAudioRawPath = fileBasePath + fileName;
        }

        return mAudioRawPath;
    }

    public static String getWavFileAbsolutePath(String fileName) {
        if (fileName == null) {
            throw new NullPointerException("fileName can't be null");
        }
        if (!isSdcardExit()) {
            throw new IllegalStateException("sd card no found");
        }

        String mAudioWavPath = "";
        if (isSdcardExit()) {
            if (!fileName.endsWith(".wav")) {
                fileName = fileName + ".wav";
            }
            String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;
            File file = new File(fileBasePath);
            //创建目录
            if (!file.exists()) {
                try {
                    if (!file.mkdirs()) {
                        // 目录创建失败
                        Log.e("FileUtils_getWavFileAbsolutePath", "Failed to create directory: " + file.getAbsolutePath());
                    }
                } catch (Exception e) {
                    Log.e("FileUtils_getWavFileAbsolutePath", "Error creating directory", e);
                    return ""; // 返回空字符串表示创建目录失败
                }
            }
            mAudioWavPath = fileBasePath + fileName;
        }
        return mAudioWavPath;
    }

    /**
     * 判断是否有外部存储设备sdcard
     *
     * @return true | false
     */
    public static boolean isSdcardExit() {
        if (getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 获取全部pcm文件列表
     *
     * @return
     */
    public static List<File> getPcmFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_PCM_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {

            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        return list;

    }

    /**
     * 获取全部wav文件列表
     *
     * @return
     */
    public static List<File> getWavFiles() {
        List<File> list = new ArrayList<>();
        String fileBasePath = Environment.getExternalStorageDirectory().getAbsolutePath() + AUDIO_WAV_BASEPATH;

        File rootFile = new File(fileBasePath);
        if (!rootFile.exists()) {
        } else {
            File[] files = rootFile.listFiles();
            for (File file : files) {
                list.add(file);
            }

        }
        return list;
    }
}
