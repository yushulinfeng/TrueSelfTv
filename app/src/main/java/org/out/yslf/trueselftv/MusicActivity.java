package org.out.yslf.trueselftv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.out.yslf.trueselftv.utils.MediaTool;
import org.out.yslf.trueselftv.utils.ToastTool;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 音乐播放界面
 * 播放，并显示手动滚动的歌词
 *
 * @author SunYuLin
 * @since 2019/2/16
 */
public class MusicActivity extends Activity {
    private static final int SEEK_INTERVAL = 10;
    private static final String KEY_MUSIC_PATH = "music_path";
    private TextView tv_text, tv_pro, tv_pro_all, tv_title;
    private SeekBar pro_text;
    private int old_progress;
    private MediaPlayer player;
    private Timer timer = null;
    private int end_time = 0;
    private String music_path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);
        music_path = getIntent().getStringExtra(KEY_MUSIC_PATH);

        initView();
        initMusicTitle();
        initMusicAndPlay();
    }

    private void initView() {
        ScrollView scroll = findViewById(R.id.main_scroll_text);
        tv_pro_all = findViewById(R.id.main_tv_pro_all);
        tv_text = findViewById(R.id.main_tv_text);
        tv_pro = findViewById(R.id.main_tv_pro);
        tv_title = findViewById(R.id.main_tv_title);
        pro_text = findViewById(R.id.main_pro_text);

        scroll.setOnClickListener(v -> playOrPause());
        tv_text.setOnClickListener(v -> playOrPause());

        // 适应电视，便于测试，改为模拟左右键
        pro_text.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    if (progress < old_progress) onLeftKeyPress();
                    else if (progress > old_progress) onRightKeyPress();
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                old_progress = seekBar.getProgress();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    private void initMusicAndPlay() {
        if (TextUtils.isEmpty(music_path)) {
            ToastTool.showToast(this, "播放路径为空");
            return;
        }
        initMusicLyric();
        releasePlayer();
        player = new MediaPlayer();
        try {
            player = new MediaPlayer();
            player.setDataSource(music_path);
            player.prepare();
            player.start();// 这句的警告不用管
            end_time = player.getDuration() / 1000;
        } catch (Exception e) {
            releasePlayer();
            ToastTool.showToast(this, "播放失败");
            return;
        }
        if (player != null) {
            player.setOnCompletionListener(mp -> finish()); // 播放结束，退出
            tv_pro_all.setText(getAllTime());
            initMusicProgressTask();
        }
    }

    private String getCurrentTime() {
        long now_time = player == null ? 0 : player.getCurrentPosition() / 1000;
        return dealTimeText(now_time / 60) + ":" + dealTimeText(now_time % 60);
    }

    private String getAllTime() {
        return dealTimeText(end_time / 60) + ":" + dealTimeText(end_time % 60);
    }

    private String dealTimeText(long time) {
        return time < 10 ? ("0" + time) : ("" + time);
    }

    private int getTimeProgress() {
        return (player == null) ? 0 : player.getCurrentPosition() / end_time / 10;
    }

    private void initMusicTitle() {
        String title = TextUtils.isEmpty(music_path)
                ? "音乐"
                : music_path.substring(music_path.lastIndexOf("/") + 1);
        int point_index = title.lastIndexOf(".");
        if (point_index != -1) title = title.substring(0, point_index);
        tv_title.setText(title);
    }

    private void initMusicLyric() {
        // 读取歌词
        String lyric_path = null;
        int point_index = music_path.lastIndexOf(".");
        boolean lyric_exist = false;
        if (point_index != -1) {
            lyric_path = music_path.substring(0, point_index) + ".lrc";
            lyric_exist = new File(lyric_path).exists();
        }
        String all_lrc = lyric_exist ? MediaTool.readLrcFromFile(lyric_path) : "歌词文件不存在";
        // 临时使用
        String[] lrc_temp = all_lrc.split("\\[.+?]");
        StringBuilder show_text = new StringBuilder();
        for (String tmp : lrc_temp) {
            show_text.append(tmp).append("\n");// 美观
        }
        all_lrc = show_text.toString().trim();
        tv_text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        tv_text.setText(all_lrc);
    }

    private void initMusicProgressTask() {
        timer = new Timer(true);
        // 这里可能存在内存泄露，暂时不进行优化了
        @SuppressLint("HandlerLeak") final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (player == null || !player.isPlaying()) {
                    return;
                }
                if (pro_text == null || tv_pro == null) {
                    return;
                }
                pro_text.setProgress(getTimeProgress());
                tv_pro.setText(getCurrentTime());
            }
        };
        TimerTask task = new TimerTask() {
            public void run() {
                handler.sendEmptyMessage(0);
            }
        };
        timer.schedule(task, 100, 95);// 开启时钟
    }

    private void releasePlayer() {
        if (player != null) {
            if (player.isPlaying())
                player.stop();
            player.release();
            player = null;
        }
    }

    private void playOrPause() {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.start();
            }
        }
    }

    private void seekSomeTime(boolean trueLeftFalseRight) {
        if (player == null || end_time == 0) {
            return;
        }
        int nextTime = (player.getCurrentPosition() / 1000)
                + (trueLeftFalseRight ? -SEEK_INTERVAL : SEEK_INTERVAL);
        if (nextTime < 0) nextTime = 0;
        if (nextTime > end_time) nextTime = end_time;
        player.seekTo(nextTime * 1000);
        ToastTool.showToast(this, (trueLeftFalseRight ? "后退" : "前进")
                + SEEK_INTERVAL + "秒");
    }

    private boolean onLeftKeyPress() {
        seekSomeTime(true);
        return true;
    }

    private boolean onRightKeyPress() {
        seekSomeTime(false);
        return true;
    }

    @Override
    protected void onDestroy() {
        releasePlayer();
        if (timer != null) {
            timer.cancel();
            // 避免时钟错误
            pro_text = null;
            tv_pro = null;
        }
        super.onDestroy();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            return onLeftKeyPress();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return onRightKeyPress();
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            ToastTool.showToast(this, "上下键滚动歌词\n左右键" + SEEK_INTERVAL + "秒进退");
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * 播放音乐
     */
    public static void playMusic(Context context, String path) {
        Intent intent = new Intent(context, MusicActivity.class);
        intent.putExtra(KEY_MUSIC_PATH, path);
        context.startActivity(intent);
    }
}
