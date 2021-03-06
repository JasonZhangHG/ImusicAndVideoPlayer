package com.android.imusic.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import com.android.imusic.R;
import com.android.imusic.music.adapter.holder.MusicListViewHolder;
import com.android.imusic.music.utils.MediaUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.music.player.lib.adapter.base.BaseAdapter;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.listener.MusicOnItemClickListener;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.MusicAlbumCoverTask;
import com.music.player.lib.util.MusicImageCache;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 409
 * 2019/3/22
 * Commend Music List Adapter
 */

public class MusicCollectListAdapter extends BaseAdapter<BaseAudioInfo,MusicListViewHolder> {

    //是否来自专辑界面持有适配器
    private final boolean mIsAlbum;
    private ExecutorService mExecutorService;
    private MusicOnItemClickListener mListener;
    private int mCurrentPosition;

    public MusicCollectListAdapter(Context context, List<BaseAudioInfo> data,
                                   MusicOnItemClickListener listener) {
        this(context,data,listener,false);
    }

    public MusicCollectListAdapter(Context context, List<BaseAudioInfo> data,
                                   MusicOnItemClickListener listener, boolean isAlbum) {
        super(context,data);
        this.mListener=listener;
        int processors = Runtime.getRuntime().availableProcessors();
        mExecutorService = (ExecutorService) Executors.newFixedThreadPool(processors);
        this.mIsAlbum=isAlbum;

    }

    @Override
    public MusicListViewHolder inCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View inflate = mInflater.inflate(R.layout.music_item_music_list, null);
        return new MusicListViewHolder(inflate);
    }

    /**
     * 全量更新
     * @param viewHolder
     * @param position
     */
    @Override
    public void inBindViewHolder(MusicListViewHolder viewHolder, final int position) {
        BaseAudioInfo itemData = getItemData(position);
        if(null!=itemData){
            viewHolder.textTitle.setText(itemData.getAudioName());
            if(!TextUtils.isEmpty(itemData.getAudioAlbumName())){
                viewHolder.textAnchor.setText(itemData.getNickname()+"-"+itemData.getAudioAlbumName());
            }else{
                viewHolder.textAnchor.setText(itemData.getNickname());
            }
            boolean isPlaying=false;
            BaseAudioInfo currentPlayerMusic = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
            if(null!=currentPlayerMusic&&currentPlayerMusic.getAudioId()==itemData.getAudioId()){
                isPlaying=true;
                mCurrentPosition =position;
            }
            itemData.setSelected(isPlaying);
            viewHolder.itemPlayingStatus.setVisibility(itemData.isSelected()?View.VISIBLE:View.GONE);
            viewHolder.itemLine.setVisibility(position==(getData().size()-1)?View.INVISIBLE:View.VISIBLE);
            //封面绑定
            if(itemData.getAudioPath().startsWith("http:")|| itemData.getAudioPath().startsWith("https:")){
                String cover= TextUtils.isEmpty(itemData.getAudioCover())?itemData.getAvatar():itemData.getAudioCover();
                if(!TextUtils.isEmpty(cover)){
                    Glide.with(getContext())
                            .load(cover)
                            .asBitmap()
                            .error(R.drawable.ic_music_default_cover)
                            .diskCacheStrategy(DiskCacheStrategy.RESULT)
                            .centerCrop()
                            .into(new BitmapImageViewTarget(viewHolder.imageCover) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                }
            }else{
                //用户指定了开关，才加载本地音乐封面
                if(MediaUtils.getInstance().isLocalImageEnable()){
                    viewHolder.imageCover.setImageResource(R.drawable.ic_music_default_cover);
                    Bitmap bitmap = MusicImageCache.getInstance().getBitmap(itemData.getAudioPath());
                    if(null!=bitmap){
                        viewHolder.imageCover.setImageBitmap(bitmap);
                    }else{
                        if(null!=mExecutorService){
                            new MusicAlbumCoverTask(viewHolder.imageCover,itemData.getAudioPath()).
                                    executeOnExecutor(mExecutorService);
                        }
                    }
                }else{
                    viewHolder.imageCover.setImageResource(R.drawable.ic_music_default_cover);
                }
            }
            viewHolder.itemRootView.setTag(itemData);
            viewHolder.itemMenu.setTag(itemData);
            //条目点击事件
            viewHolder.itemRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mListener){
                        mListener.onItemClick(v,position,1);
                    }
                }
            });
            //菜单点击事件
            viewHolder.itemMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(null!=mListener){
                        mListener.onItemClick(v,position,0);
                    }
                }
            });
        }
    }

    /**
     * 局部刷新
     * @param viewHolder
     * @param position
     * @param payloads
     */
    @Override
    protected void inBindViewHolder(MusicListViewHolder viewHolder, int position, List<Object> payloads) {
        super.inBindViewHolder(viewHolder, position, payloads);
        BaseAudioInfo itemData = getItemData(position);
        if(null!=itemData){
            viewHolder.itemPlayingStatus.setVisibility(itemData.isSelected()?View.VISIBLE:View.GONE);
            viewHolder.itemRootView.setTag(itemData);
        }
    }

    public int getCurrentPosition() {
        return mCurrentPosition;
    }

    public void setCurrentPosition(int currentPosition) {
        this.mCurrentPosition = currentPosition;
    }

    /**
     * 刷新单个条目
     * @param position
     */
    public void notifyDataSetChanged(int position){
        BaseAudioInfo itemData = getItemData(position);
        BaseAudioInfo currentItemData = getItemData(mCurrentPosition);
        currentItemData.setSelected(false);
        itemData.setSelected(true);
        notifyItemChanged(mCurrentPosition,"NITIFY_DATA");
        notifyItemChanged(position,"NITIFY_ITEM");
        mCurrentPosition=position;
    }

    /**
     * 删除某个条目
     * @param posotion
     */
    public void removeItem(int posotion) {
        if(null!=getData()&&getData().size()>posotion){
            getData().remove(posotion);
            notifyDataSetChanged();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mListener=null;
        if(null!=mExecutorService){
            mExecutorService.shutdown();
            mExecutorService=null;
        }
        MusicImageCache.getInstance().onDestroy();
    }
}