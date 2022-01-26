package com.example.igallery;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.job.JobInfo;
import android.app.job.JobParameters;
import android.app.job.JobScheduler;
import android.app.job.JobService;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class JobSchedulerService extends JobService {
    private Context mContext;
    private static final int ASJOBSERVICE_JOB_ID = 999;

    // JobInfo được tạo sẵn mà chúng ta sử dụng để lên lịch công việc.
    private static JobInfo JOB_INFO = null;

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static int a(Context context) {
        int schedule = ((JobScheduler) context.getSystemService(JobScheduler.class)).schedule(JOB_INFO);
        Log.i("PhotosContentJob", "JOB SCHEDULED!");
        return schedule;
    }

    // Lên lịch công việc này, thay thế bất kỳ công việc hiện có nào.
    @SuppressLint("WrongConstant")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void scheduleJob(Context context) {
        if (JOB_INFO != null) {
            a(context);
        } else {
            JobScheduler js = context.getSystemService(JobScheduler.class);
            JobInfo.Builder builder = new JobInfo.Builder(ASJOBSERVICE_JOB_ID,
                    new ComponentName(BuildConfig.APPLICATION_ID, JobSchedulerService.class.getName()));
            builder.addTriggerContentUri(new JobInfo.TriggerContentUri(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, 1));
            builder.addTriggerContentUri(new JobInfo.TriggerContentUri(MediaStore.Images.Media.INTERNAL_CONTENT_URI, 1));
            builder.addTriggerContentUri(new JobInfo.TriggerContentUri(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, 1));
            builder.addTriggerContentUri(new JobInfo.TriggerContentUri(MediaStore.Video.Media.INTERNAL_CONTENT_URI, 1));
            builder.setTriggerContentMaxDelay(1000);
            JOB_INFO = builder.build();
            js.schedule(JOB_INFO);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onStartJob(final JobParameters params) {
        mContext = this;
        // Chúng ta có kích hoạt do thay đổi nội dung không?
        if (params.getTriggeredContentAuthorities() != null) {
            if (params.getTriggeredContentUris() != null) {
                // Nếu chúng ta có thông tin chi tiết về những URI nào đã thay đổi, hãy lặp lại chúng
                // và thu thập các id đã bị ảnh hưởng hoặc lưu ý rằng một
                // thay đổi đã xảy ra.

//                ArrayList<String> ids = new ArrayList<>();
//                for (Uri uri : params.getTriggeredContentUris()) {
//                    if (uri != null) {
//                        Handler handler = new Handler();
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                //Thực hiện nhiệm vụ của bạn ở đây
//                                SharedPreferences sharedPreferences = getSharedPreferences("RELOAD_ALL_DATA",MODE_PRIVATE);
//                                SharedPreferences.Editor editor = sharedPreferences.edit();
//                                editor.putInt("reload_all_data",1);
//                                editor.commit();
//                            }
//                        });
//                    }
//                }

                Uri uri = params.getTriggeredContentUris()[0];
                if (uri!=null){
                    Handler handler = new Handler();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            SharedPreferences sharedPreferences = getSharedPreferences("RELOAD_ALL_DATA",MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putInt("reload_all_data",1);
                            editor.commit();
                        }
                    });

                }
                jobFinished(params, true); // xem này, chúng ta đang nói rằng chúng ta vừa hoàn thành công việc
                // Chúng ta sẽ thi đua dành chút thời gian để thực hiện công việc này, vì vậy chúng ta có thể thấy việc phân lô xảy ra.
                scheduleJob(this);
            }
        }
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters params) {
        return false;
    }
}
