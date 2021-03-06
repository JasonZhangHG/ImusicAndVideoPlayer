package com.android.imusic.music.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import com.android.imusic.R;
import com.android.imusic.music.adapter.holder.IndexAlbumViewHolder;
import com.android.imusic.music.adapter.holder.IndexDefaultViewHolder;
import com.android.imusic.music.adapter.holder.IndexMusicViewHolder;
import com.android.imusic.music.adapter.holder.IndexTitleViewHolder;
import com.android.imusic.music.bean.AudioInfo;
import com.music.player.lib.manager.SqlLiteCacheManager;
import com.android.imusic.music.utils.MediaUtils;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.music.player.lib.adapter.base.BaseAdapter;
import com.music.player.lib.bean.BaseAudioInfo;
import com.music.player.lib.constants.MusicConstants;
import com.music.player.lib.manager.MusicPlayerManager;
import com.music.player.lib.util.Logger;
import com.music.player.lib.util.MusicUtils;
import java.util.List;

/**
 * 409
 * 2019/3/24
 * Index List Adapter
 */

public class MusicIndexDataAdapter extends BaseAdapter<AudioInfo,RecyclerView.ViewHolder> {

    private int mItemWidth;

    public MusicIndexDataAdapter(Context context, @Nullable List<AudioInfo> data) {
        super(context,data);
        int screenWidth = MusicUtils.getInstance().getScreenWidth(context);
        //边距16+10+10+16
        mItemWidth = (screenWidth - MusicUtils.getInstance().dpToPxInt(context, 52f)) /3;
    }

    @Override
    public int getItemCount() {
        return null==mData?0:mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        if(null!=getData()){
            return getData().get(position).getItemType();
        }
        return AudioInfo.ITEM_UNKNOWN;
    }

    @Override
    public RecyclerView.ViewHolder inCreateViewHolder(ViewGroup viewGroup, int viewType) {
        if(viewType==AudioInfo.ITEM_DEFAULT){
            View inflate = mInflater.inflate(R.layout.music_index_list_default_item, null);
            return new IndexDefaultViewHolder(inflate);
        }else if(viewType==AudioInfo.ITEM_TITLE){
            View inflate = mInflater.inflate(R.layout.music_index_list_title_item, null);
            return new IndexTitleViewHolder(inflate);
        }else if(viewType==AudioInfo.ITEM_ALBUM){
            View inflate = mInflater.inflate(R.layout.music_index_list_album_item, null);
            return new IndexAlbumViewHolder(inflate);
        }else if(viewType==AudioInfo.ITEM_MUSIC){
            View inflate = mInflater.inflate(R.layout.music_index_list_music_item, null);
            return new IndexMusicViewHolder(inflate);
        }else if(viewType==AudioInfo.ITEM_MORE){
            View inflate = mInflater.inflate(R.layout.music_index_list_more_item, null);
            return new IndexTitleViewHolder(inflate);
        }
        return new UnKnownView(mInflater.inflate(R.layout.music_unknown_layout, null));
    }

    @Override
    public void inBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int itemViewType = getItemViewType(position);
        AudioInfo itemData = getItemData(position);
        viewHolder.itemView.setTag(itemData);
        if(null!=itemData){
            //默认
            if(itemViewType==AudioInfo.ITEM_DEFAULT){
                IndexDefaultViewHolder defaultViewHolder= (IndexDefaultViewHolder) viewHolder;
                defaultViewHolder.textTitle.setText(itemData.getTitle());
                //初始数据
                defaultViewHolder.textDesp.setText("(0)");
                if(!TextUtils.isEmpty(itemData.getDesp())){
                    defaultViewHolder.textDesp.setText(itemData.getDesp());
                }
                //播放状态和历史记录
                boolean isVisible=false;
                if(!TextUtils.isEmpty(itemData.getTag_id())){
                    //本地音乐
                    if(itemData.getTag_id().equals(AudioInfo.TAG_LOCATION)){
                        if(MusicPlayerManager.getInstance().getPlayingChannel()==MusicConstants.CHANNEL_LOCATION){
                            isVisible=true;
                        }
                        //本地音频个数获取
                        List<AudioInfo> locationMusic = MediaUtils.getInstance().getLocationMusic();
                        if(null!=locationMusic&&locationMusic.size()>0){
                            defaultViewHolder.textDesp.setText(String.format("(%s首)",locationMusic.size()));
                        }else{
                            defaultViewHolder.textDesp.setText("(0)");
                        }
                    //最近播放
                    }else if(itemData.getTag_id().equals(AudioInfo.TAG_LAST_PLAYING)){
                        if(MusicPlayerManager.getInstance().getPlayingChannel()==MusicConstants.CHANNEL_HISTROY){
                            isVisible=true;
                        }
                        //优先拿播放器内部的，处理播放过程中切换了对象
                        BaseAudioInfo currentPlayerMusic = MusicPlayerManager.getInstance().getCurrentPlayerMusic();
                        if(null!=currentPlayerMusic){
                            defaultViewHolder.textDesp.setText("("+currentPlayerMusic.getAudioName()+")");
                        }else{
                            //历史记录
                            BaseAudioInfo mediaInfo = SqlLiteCacheManager.getInstance().queryHistroyFirstAudio();
                            if(null!=mediaInfo){
                                defaultViewHolder.textDesp.setText(String.format("(%s)",mediaInfo.getAudioName()));
                            }else{
                                defaultViewHolder.textDesp.setText(String.format("(%s)","暂无播放记录"));
                            }
                        }
                    //我的收藏
                    }else if(itemData.getTag_id().equals(AudioInfo.TAG_COLLECT)){
                        if(MusicPlayerManager.getInstance().getPlayingChannel()==MusicConstants.CHANNEL_COLLECT){
                            isVisible=true;
                        }
                        //查询收藏表总条数
                        long size = SqlLiteCacheManager.getInstance().queryCollectAudiosSize();
                        defaultViewHolder.textDesp.setText(String.format("(%s)",size));
                    }
                }
                defaultViewHolder.playingStatus.setVisibility(isVisible?View.VISIBLE:View.GONE);
                if(itemData.getImage() instanceof Integer){
                    defaultViewHolder.imageCover.setImageResource((Integer) itemData.getImage());
                }else{
                    Glide.with(getContext())
                            .load(itemData.getImage())
                            .asBitmap()
                            .error(R.drawable.ic_music_default_cover)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .fitCenter()
                            .into(defaultViewHolder.imageCover);
                }
                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams)
                        defaultViewHolder.itemLine.getLayoutParams();
                int margin = 0;//MusicUtils.getInstance().dpToPxInt(getContext(), 5f)
                if(itemData.getTag_id().equals(AudioInfo.TAG_COLLECT)){
                    layoutParams.setMargins(margin,0,margin,0);
                }else{
                    layoutParams.setMargins(MusicUtils.getInstance().dpToPxInt(
                            getContext(),39f),0,margin,0);
                }
                defaultViewHolder.itemLine.setLayoutParams(layoutParams);
                //标题
            }else if(itemViewType==AudioInfo.ITEM_TITLE){
                IndexTitleViewHolder titleViewHolder= (IndexTitleViewHolder) viewHolder;
                titleViewHolder.textTitle.setText(itemData.getTitle());
            //更多
            }else if(itemViewType==AudioInfo.ITEM_MORE){
                IndexTitleViewHolder titleViewHolder= (IndexTitleViewHolder) viewHolder;
                titleViewHolder.textTitle.setText(itemData.getTitle());
            //专辑
            }else if(itemViewType==AudioInfo.ITEM_ALBUM){
                IndexAlbumViewHolder musicViewHolder= (IndexAlbumViewHolder) viewHolder;
                musicViewHolder.imageCover.getLayoutParams().height=mItemWidth;
                musicViewHolder.textTitle.setText(itemData.getTitle());
                musicViewHolder.textAnchor.setText(itemData.getDesp());
                Glide.with(getContext())
                        .load(itemData.getImage())
                        .asBitmap()
                        .error(R.drawable.ic_music_default_cover)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .centerCrop()
                        .into(new BitmapImageViewTarget(musicViewHolder.imageCover) {
                            @Override
                            protected void setResource(Bitmap resource) {
                                super.setResource(resource);
                            }
                        });
            //音乐
            }else if(itemViewType==AudioInfo.ITEM_MUSIC){
                IndexMusicViewHolder musicViewHolder= (IndexMusicViewHolder) viewHolder;
                musicViewHolder.imageCover.getLayoutParams().height=mItemWidth;
                musicViewHolder.textTitle.setText(itemData.getAudioName());
                musicViewHolder.textAnchor.setText(itemData.getNickname());
                String cover= TextUtils.isEmpty(itemData.getAudioCover())
                        ?itemData.getAvatar():itemData.getAudioCover();
                if(!TextUtils.isEmpty(cover)){
                    Glide.with(getContext())
                            .load(cover)
                            .asBitmap()
                            .error(R.drawable.ic_music_default_cover)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .centerCrop()
                            .into(new BitmapImageViewTarget(musicViewHolder.imageCover) {
                                @Override
                                protected void setResource(Bitmap resource) {
                                    super.setResource(resource);
                                }
                            });
                }
            }
        }
    }

    @Override
    protected void inBindViewHolder(RecyclerView.ViewHolder viewHolder, int position, List<Object> payloads) {
        super.inBindViewHolder(viewHolder, position, payloads);
        AudioInfo itemData = getItemData(position);
        if(null!=itemData){
            int itemViewType = getItemViewType(position);
            viewHolder.itemView.setTag(itemData);
            //默认
            if(itemViewType== AudioInfo.ITEM_DEFAULT){
                Logger.d(TAG,"inBindViewHolder-->局部刷新DESP");
                IndexDefaultViewHolder defaultViewHolder= (IndexDefaultViewHolder) viewHolder;
                defaultViewHolder.textDesp.setText(itemData.getDesp());
            }
        }
    }

    /**
     * 动态给定权重 这里的SpanSize是两列 2 表示占据一整行
     * @param recyclerView
     */
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        if(null!=layoutManager&&layoutManager instanceof GridLayoutManager){
            GridLayoutManager gridLayoutManager= (GridLayoutManager) layoutManager;
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    int itemViewType = getItemViewType(position);
                    switch (itemViewType) {
                        //默认的
                        case AudioInfo.ITEM_DEFAULT:
                        case AudioInfo.ITEM_TITLE:
                        case AudioInfo.ITEM_MORE:
                        case AudioInfo.ITEM_UNKNOWN:
                            return 3;
                        //音频
                        case AudioInfo.ITEM_MUSIC:
                        //专辑
                        case AudioInfo.ITEM_ALBUM:
                            return 1;
                    }
                    return 3;
                }
            });
        }
    }

    private class UnKnownView extends RecyclerView.ViewHolder{

        public UnKnownView(View itemView) {
            super(itemView);
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        mItemWidth=0;
    }
}