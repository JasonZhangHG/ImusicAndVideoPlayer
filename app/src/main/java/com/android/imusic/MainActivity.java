package com.android.imusic;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.imusic.base.BaseActivity;
import com.android.imusic.base.BasePresenter;
import com.android.imusic.music.activity.MusicLockActivity;
import com.android.imusic.music.activity.MusicPlayerActivity;
import com.android.imusic.music.adapter.MusicFragmentPagerAdapter;
import com.android.imusic.music.dialog.QuireDialog;
import com.music.player.lib.manager.SqlLiteCacheManager;
import com.android.imusic.music.manager.VersionUpdateManager;
import com.android.imusic.music.ui.fragment.IndexMusicFragment;
import com.android.imusic.music.utils.MediaUtils;
import com.android.imusic.net.OkHttpUtils;
import com.android.imusic.video.activity.VideoPlayerActviity;
import com.android.imusic.video.fragment.IndexVideoFragment;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.listener.MusicInitializeCallBack;
import com.music.player.lib.listener.MusicPlayerInfoListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.model.MusicPlayerConfig;
import com.music.player.lib.util.Logger;
import com.music.player.lib.util.MusicRomUtil;
import com.music.player.lib.util.MusicUtils;
import com.video.player.lib.manager.VideoPlayerManager;
import com.video.player.lib.manager.VideoWindowManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Main
 */

public class MainActivity extends BaseActivity {

    private long currentMillis=0;
    private TextView mBtnMusic,mBtnVideo;
    private ViewPager mViewPager;
    private MusicFragmentPagerAdapter mPagerAdapter;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //?????????????????????????????????
        initConfig();
        mBtnMusic = (TextView) findViewById(R.id.music_btn_music);
        mBtnMusic.setSelected(true);
        mBtnVideo = (TextView) findViewById(R.id.music_btn_video);
        View.OnClickListener onClickListener=new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int id = v.getId();
                switch (id) {
                    case R.id.music_btn_music:
                        mBtnVideo.setSelected(false);
                        mBtnMusic.setSelected(true);
                        mViewPager.setCurrentItem(0);
                        break;
                    case R.id.music_btn_video:
                        mBtnMusic.setSelected(false);
                        mBtnVideo.setSelected(true);
                        mViewPager.setCurrentItem(1);
                        break;
                }
            }
        };
        mBtnMusic.setOnClickListener(onClickListener);
        mBtnVideo.setOnClickListener(onClickListener);
        mViewPager = (ViewPager) findViewById(R.id.view_pager);
        mViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        List<Fragment> fragments=new ArrayList<>();
        fragments.add(new IndexMusicFragment());
        fragments.add(new IndexVideoFragment());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels){

            }

            @Override
            public void onPageSelected(int position) {
                if(0==position){
                    mBtnVideo.setSelected(false);
                    mBtnMusic.setSelected(true);
                }else if(1==position){
                    mBtnMusic.setSelected(false);
                    mBtnVideo.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mPagerAdapter = new MusicFragmentPagerAdapter(getSupportFragmentManager(), fragments);
        mViewPager.setAdapter(mPagerAdapter);
        requstPermissions();

        //???APP??????????????????????????????APP???????????????????????????????????????????????????????????????????????????ID????????????
        long audioID = getIntent().getLongExtra(MusicConstants.KEY_MUSIC_ID, 0);
        if(audioID>0){
            startToMusicPlayer(audioID);
        }
    }

    /**
     * ??????????????????
     */
    private void initConfig() {
        //????????????????????????
        VideoPlayerManager.getInstance()
                //????????????
                .setLoop(true)
                //??????????????????????????????????????????
                .setPlayerActivityClassName(VideoPlayerActviity.class.getCanonicalName());

        //?????????????????????
        MusicPlayerConfig config=MusicPlayerConfig.Build()
                //???????????????????????????????????????????????????????????????
                .setDefaultAlarmModel(MusicConstants.MUSIC_ALARM_MODEL_0)
                //???????????????????????????????????????????????????
                .setDefaultPlayModel(MusicConstants.MUSIC_MODEL_LOOP);

        //????????????????????????
        MusicPlayerManager.getInstance()
                //?????????????????????
                .init(getApplicationContext())
                //?????????????????????
                .setMusicPlayerConfig(config)
                //??????????????????????????????
                .setNotificationEnable(true)
                //?????????????????????????????????
                .setLockForeground(true)
                //?????????????????????????????????????????????,???????????????????????????
                .setPlayerActivityName(MusicPlayerActivity.class.getCanonicalName())
                //??????????????????????????????????????????????????????????????????null
                .setLockActivityName(MusicLockActivity.class.getCanonicalName())
                //??????????????????
                .setPlayInfoListener(new MusicPlayerInfoListener() {
                    //??????????????????????????????
                    @Override
                    public void onPlayMusiconInfo(BaseAudioInfo musicInfo, int position) {
                        SqlLiteCacheManager.getInstance().insertHistroyAudio(musicInfo);
                    }
                })
                //??????????????????????????????????????????,????????????????????????????????????????????????????????????????????????????????????
                .initialize(MainActivity.this, new MusicInitializeCallBack() {

                    @Override
                    public void onSuccess() {
                        //??????????????????????????????
                        if(null!=MusicPlayerManager.getInstance().getCurrentPlayerMusic()){
                            MusicPlayerManager.getInstance().createWindowJukebox();
                        }
                    }
                });

    }

    @Override
    protected BasePresenter createPresenter() {
        return null;
    }

    @Override
    protected void onRequstPermissionResult(int resultCode) {
        super.onRequstPermissionResult(resultCode);
        if(resultCode==PREMISSION_SUCCESS){
            if(null!=mPagerAdapter&&mPagerAdapter.getCount()>0){
                try {
                    if(mPagerAdapter.getItem(0) instanceof IndexMusicFragment){
                        ((IndexMusicFragment) mPagerAdapter.getItem(0)).queryLocationMusic(MainActivity.this);
                    }
                }catch (RuntimeException e){
                    e.printStackTrace();
                }
            }
        }
        //???????????????????????????
        boolean premission = MusicUtils.getInstance().hasNiticePremission(getApplicationContext());
        if(!premission){
            QuireDialog.getInstance(MainActivity.this)
                    .setTitleText(getString(R.string.text_sys_tips))
                    .setContentText(getString(R.string.text_tips_notice))
                    .setSubmitTitleText(getString(R.string.text_start_open))
                    .setCancelTitleText(getString(R.string.music_text_cancel))
                    .setTopImageRes(R.drawable.ic_setting_tips4)
                    .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                        @Override
                        public void onConsent(QuireDialog dialog) {
                            try {
                                Intent intent = new Intent();
                                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                            }catch (RuntimeException e){
                                e.printStackTrace();
                            }
                        }
                    }).show();
        }else{
            if(MusicUtils.getInstance().getInt(MusicConstants.SP_FIRST_START,0)==0){
                //????????????
                QuireDialog.getInstance(MainActivity.this)
                        .setTitleText(getString(R.string.text_action_tips))
                        .setContentText(getString(R.string.text_action_content))
                        .setSubmitTitleText(getString(R.string.text_start_now_open))
                        .setCancelTitleText(getString(R.string.text_yse))
                        .setTopImageRes(R.drawable.ic_setting_tips1)
                        .setBtnClickDismiss(false)
                        .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                            @Override
                            public void onConsent(QuireDialog dialog) {
                                MediaUtils.getInstance().setLocalImageEnable(true);
                                Toast.makeText(MainActivity.this, getString(R.string.text_start_open_success),
                                        Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }

                            @Override
                            public void onRefuse(QuireDialog dialog) {
                                dialog.dismiss();
                            }

                            @Override
                            public void onDissmiss() {
                                Logger.d(TAG,"onDissmiss--->");
                                if(MusicRomUtil.getInstance().isMiui()){
                                    showXiaoMiTips();
                                }else{
                                    //??????????????????
                                    VersionUpdateManager.getInstance().checkAppVersion();
                                }
                            }
                        }).show();
                MusicUtils.getInstance().putInt(MusicConstants.SP_FIRST_START,1);
            }else{
                //??????????????????
                VersionUpdateManager.getInstance().checkAppVersion();
            }
        }
    }

    /**
     * ????????????????????????
     */
    private void showXiaoMiTips() {
        QuireDialog.getInstance(MainActivity.this)
                .setTitleText(getString(R.string.text_xiao_tips_title))
                .setContentText(getString(R.string.text_xiao_tips_content))
                .setSubmitTitleText(getString(R.string.text_xiao_tips_close))
                .setCancelTitleText(getString(R.string.text_yse))
                .setTopImageRes(R.drawable.ic_setting_tips2)
                .setOnQueraConsentListener(new QuireDialog.OnQueraConsentListener() {
                    @Override
                    public void onDissmiss() {
                        VersionUpdateManager.getInstance().checkAppVersion();
                    }
                }).show();
    }

    /**
     * ???????????????????????????
     * @param keyCode
     * @param event
     * @return
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            onBackPressed();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        //??????????????????????????????????????????????????????????????????
        if(VideoPlayerManager.getInstance().isBackPressed(false)){
            long millis = System.currentTimeMillis();
            if(0 == currentMillis | millis-currentMillis > 2000){
                Toast.makeText(MainActivity.this,getString(R.string.text_back_tips)+getResources().getString(R.string.app_name),Toast.LENGTH_SHORT).show();
                currentMillis=millis;
                return;
            }
            currentMillis=millis;
            //???????????????????????????
            if(VideoPlayerManager.getInstance().isBackPressed(true)){
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        VideoPlayerManager.getInstance().onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        VideoPlayerManager.getInstance().onPause();
    }

    /**
     * ???????????????
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        VideoPlayerManager.getInstance().onDestroy();
        VideoWindowManager.getInstance().onDestroy();
        //??????????????????????????????????????????????????????????????????????????????
        MusicPlayerManager.getInstance().unInitialize(MainActivity.this);
        OkHttpUtils.getInstance().onDestroy();
        if(null!=mPagerAdapter){
            mPagerAdapter.onDestroy();
            mPagerAdapter=null;
        }
    }
}