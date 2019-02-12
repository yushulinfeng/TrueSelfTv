package org.out.yslf.trueselftv;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.out.yslf.trueselftv.utils.EasyListAdapter;
import org.out.yslf.trueselftv.FileShowAdapter.*;

import java.util.List;

/**
 * @author SunYuLin
 * @since 2019/2/12
 */
public class FileShowAdapter extends EasyListAdapter<MediaHolder, MediaItem> {

    public FileShowAdapter(Context context, List<MediaItem> items) {
        super(context, items);
    }

    @Override
    protected MediaHolder getNewHolder(View rootView) {
        return new MediaHolder(rootView);
    }

    @Override
    protected int getLayoutRes() {
        return R.layout.activity_file_show_item;
    }

    @Override
    protected void getView(MediaHolder holder, int position) {
        MediaItem item = items.get(position);
        holder.indexView.setText(String.valueOf(position + 1));
        holder.textView.setText(item.getName());
        holder.infoView.setText(item.getInfo());
        holder.imageView.setImageResource(item.getImageRes());
    }

    public static class MediaHolder {
        public TextView indexView;
        public TextView textView;
        public TextView infoView;
        public ImageView imageView;

        public MediaHolder(View itemView) {
            indexView = itemView.findViewById(R.id.main_item_index);
            textView = itemView.findViewById(R.id.main_item_text);
            infoView = itemView.findViewById(R.id.main_item_info);
            imageView = itemView.findViewById(R.id.main_item_icon);
        }
    }
}
