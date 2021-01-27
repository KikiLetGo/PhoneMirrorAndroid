package com.elexlab.mydisk.ui.files;

import android.content.Context;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.elexlab.mydisk.R;
import com.elexlab.mydisk.constants.Constants;
import com.elexlab.mydisk.manager.FileSystemManager;
import com.elexlab.mydisk.pojo.FileInfo;
import com.elexlab.mydisk.ui.home.FileListFragment;
import com.elexlab.mydisk.ui.misc.RecyclerItemData;
import com.elexlab.mydisk.utils.DownloadTools;
import com.elexlab.mydisk.utils.FileOpenUtils;
import com.elexlab.mydisk.utils.FragmentUtils;
import com.elexlab.mydisk.utils.HeroLog;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BruceYoung on 10/3/17.
 */
public class FilesBrowserAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    //private DistAlbums distAlbums;
    private Context context;
    private Fragment fragment;

    private Handler handler = new Handler();

    private List<FileInfo> fileInfos;
    private List<RecyclerItemData<FileInfo>> recyclerItems;

    public FilesBrowserAdapter (Fragment fragment) {
        //this.distAlbums = distAlbums;
        this.fragment = fragment;
        this.context = fragment.getContext();
    }
    public FilesBrowserAdapter (Fragment fragment,List<FileInfo> fileInfos) {
        //this.distAlbums = distAlbums;
        this.fragment = fragment;
        this.context = fragment.getContext();
        this.fileInfos = fileInfos;
    }
    public void resetFileList(List<FileInfo> fileInfos){
//        if(this.fileInfos == null){
//            this.fileInfos = fileInfos;
//            notifyDataSetChanged();
//            return;
//        }
//        List<Integer> removedIndices = computeRemovedIndices(fileInfos);
//        List<Integer> addedIndices = computeAddedIndices(fileInfos);
//
//        for(int index:removedIndices){
//            notifyItemRemoved(index);
//        }
//        for(int index:addedIndices){
//            notifyItemInserted(index);
//        }
        this.fileInfos = fileInfos;
        notifyDataSetChanged();
    }


    private int myAlbumFirstIndex = -1;
    private int systemAlbumFirstIndex = -1;

    public int getSystemAlbumFirstIndex() {
        return systemAlbumFirstIndex;
    }

    public int getMyAlbumFirstIndex() {
        return myAlbumFirstIndex;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType){
            case FileInfo.StoreLocation.LOCAL_MIRROR:{
                ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(
                        context).inflate(R.layout.item_file, parent,
                        false));
                return viewHolder;
            }
            case FileInfo.StoreLocation.MIRROR:{
                MirrorViewHolder viewHolder = new MirrorViewHolder(LayoutInflater.from(
                        context).inflate(R.layout.item_file_mirror, parent,
                        false));
                return viewHolder;
            }
            case FileInfo.StoreLocation.LOCAL:{
                LocalViewHolder viewHolder = new LocalViewHolder(LayoutInflater.from(
                        context).inflate(R.layout.item_file_local, parent,
                        false));
                return viewHolder;
            }
            case FileInfo.StoreLocation.LOCAL_MIRROR_RECOVERY:{
                ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(
                        context).inflate(R.layout.item_file_recovery, parent,
                        false));
                return viewHolder;
            }
            default:{
                ViewHolder viewHolder = new ViewHolder(LayoutInflater.from(
                        context).inflate(R.layout.item_file, parent,
                        false));
                return viewHolder;
            }
        }


    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final FileInfo fileInfo = fileInfos.get(position);

        final ViewHolder viewHolder = (ViewHolder) holder;
        viewHolder.ivPlay.setVisibility(View.GONE);
        viewHolder.tvFileName.setText(fileInfo.getName());
        View.OnClickListener onClickListener = null;
        if("dir".equals(fileInfo.getFileType())){
            viewHolder.ivIcon.setImageResource(R.drawable.ic_folder);
        }else {
            viewHolder.ivIcon.setImageResource(R.drawable.ic_file);

            if(FileOpenUtils.isImage(fileInfo.getName())){
                Glide.with(context)
                        .load(fileInfo.getPath())
                        .centerCrop()
                        .into(viewHolder.ivIcon);

            }
            if(FileOpenUtils.isVideo(fileInfo.getName())){
                Glide.with(context)
                        .load(fileInfo.getPath())
                        .centerCrop()
                        .into(viewHolder.ivIcon);
                viewHolder.ivPlay.setVisibility(View.VISIBLE);

            }
        }


        if(getItemViewType(position) == FileInfo.StoreLocation.LOCAL_MIRROR||
                getItemViewType(position) == FileInfo.StoreLocation.LOCAL_MIRROR_RECOVERY){
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileOpenUtils.openFile(context,
                            fileInfo.getPath());
                }
            };
        }else if(getItemViewType(position) == FileInfo.StoreLocation.MIRROR){
            final MirrorViewHolder mirrorViewHolder = (MirrorViewHolder) holder;

            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mirrorViewHolder.pbRecovering.setVisibility(View.VISIBLE);
                    String url = fileInfo.getUrl();//Constants.DOWNLOAD_FILE + fileInfo.getPath()+fileInfo.getName()+"&filename="+fileInfo.getName();
                    final String path = fileInfo.getPath();
                    HeroLog.d("FilesAdapter",path);
                    HeroLog.d("url",url);


                    DownloadTools.DownloadFile(url, path, new DownloadTools.DownLoadCallback() {
                        @Override
                        public void onSuccess() {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    mirrorViewHolder.pbRecovering.setVisibility(View.INVISIBLE);
                                    fileInfo.setStoreLocation(FileInfo.StoreLocation.LOCAL_MIRROR_RECOVERY);
                                    notifyItemChanged(position);
                                    //FileOpenUtils.openFile(context,path);
                                }
                            });
                        }

                        @Override
                        public void onFail(String msg) {

                        }
                    });
                }
            };

        }else if(getItemViewType(position) == FileInfo.StoreLocation.LOCAL){
            final LocalViewHolder localViewHolder = (LocalViewHolder) holder;
            localViewHolder.ivLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(final View v) {
                    final Animation operatingAnim = AnimationUtils.loadAnimation(context, R.anim.rotate_anim);
                    LinearInterpolator lin = new LinearInterpolator();
                    operatingAnim.setInterpolator(lin);
                    localViewHolder.ivLocation.startAnimation(operatingAnim);
                    FileSystemManager.getInstance().uploadFile(fileInfo, new FileSystemManager.FileActionListener() {
                        @Override
                        public void onCompletion(final FileInfo fileInfo,String msg) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    localViewHolder.ivLocation.clearAnimation();
                                    fileInfo.setStoreLocation(FileInfo.StoreLocation.LOCAL_MIRROR);
                                    notifyItemChanged(position);
                                }
                            });

                        }

                        @Override
                        public void onError(FileInfo fileInfo,final String msg) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    localViewHolder.ivLocation.clearAnimation();
                                    Toast.makeText(fragment.getContext(),"同步发生错误:"+msg,Toast.LENGTH_LONG).show();

                                }
                            });
                        }
                    });
                }
            });
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileOpenUtils.openFile(context,
                            fileInfo.getPath());
                }
            };
        }

        if(FileInfo.FileType.DIR.equals(fileInfo.getFileType())){
            onClickListener = new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FileListFragment fileListFragment = new FileListFragment();
                    Bundle args = new Bundle();
                    String dir = fileInfo.getPath();
                    args.putString("dir", dir);
                    fileListFragment.setArguments(args);
                    FragmentUtils.switchFragment(fragment.getActivity(),R.id.flContainer,fragment,fileListFragment,true);
                }
            };

        }
        viewHolder.itemView.setOnClickListener(onClickListener);
        if(getItemViewType(position) == FileInfo.StoreLocation.LOCAL_MIRROR_RECOVERY){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fileInfo.setStoreLocation(FileInfo.StoreLocation.LOCAL_MIRROR);
                    notifyItemChanged(position);
                }
            },1500);
        }
    }


    @Override
    public int getItemViewType(int position) {
        return fileInfos.get(position).getStoreLocation();
    }


    private int myAlbumSize;
    private int sysAlbumSize;
    @Override
    public int getItemCount() {
        return fileInfos == null?0:fileInfos.size();
    }




    private class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView ivIcon;
        public View ivPlay;
        public TextView tvFileName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivIcon = (ImageView) itemView.findViewById(R.id.ivIcon);
            tvFileName = (TextView) itemView.findViewById(R.id.tvFileName);
            ivPlay = itemView.findViewById(R.id.ivPlay);
        }
    }
    private class MirrorViewHolder extends ViewHolder{

        public ProgressBar pbRecovering;

        public MirrorViewHolder(@NonNull View itemView) {
            super(itemView);
            ColorMatrix cm = new ColorMatrix();
            cm.setSaturation(0.1f);
            ivIcon.setColorFilter(new ColorMatrixColorFilter(cm));
            pbRecovering = itemView.findViewById(R.id.pbRecovering);

        }
    }
    private class LocalViewHolder extends ViewHolder{
        public ImageView ivLocation;
        public LocalViewHolder(@NonNull View itemView) {
            super(itemView);
            ivLocation = itemView.findViewById(R.id.ivLocation);
        }
    }

    private OnItemLongClickListener onItemLongClickListener;

    public void setOnItemLongClickListener(OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public interface OnItemLongClickListener{
        void onLongClick(RecyclerView.ViewHolder viewHolder);
    }


    private List<Integer> computeRemovedIndices(List<FileInfo> newFiles){
        List<Integer> indices = new ArrayList();
        for(int i=0;i<fileInfos.size();i++){
            FileInfo fileInfo = fileInfos.get(i);
            if(!newFiles.contains(fileInfo)){
                indices.add(i);

            }
        }
        return indices;

    }

    private List<Integer> computeAddedIndices(List<FileInfo> newFiles){
        List<Integer> indices = new ArrayList();
        for(int i=0;i<newFiles.size();i++){
            FileInfo fileInfo = newFiles.get(i);
            if(!fileInfos.contains(fileInfo)){
                indices.add(fileInfos.indexOf(fileInfo));

            }
        }
        return indices;

    }
}
