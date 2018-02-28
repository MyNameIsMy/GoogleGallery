package projects.suchushin.org.googlegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

public class MasonryRecycleViewAdapter extends RecyclerView.Adapter<MasonryRecycleViewAdapter.ViewHolder>{
    private List<String> listOfImageURLs;
    private List<Bitmap> listOfDownloadedImage;
    private Context context;

    MasonryRecycleViewAdapter(Context context, List<String> listOfImageURLs, List<Bitmap> listOfDownloadedImage) {
        this.context = context;
        this.listOfImageURLs = listOfImageURLs;
        this.listOfDownloadedImage = listOfDownloadedImage;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (listOfImageURLs != null){
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            String path = listOfImageURLs.get(position);
            Glide.with(context).load(path).override(metrics.widthPixels/4, 200).into(holder.imageView);
        } if (listOfDownloadedImage != null){
            holder.imageView.setImageBitmap(listOfDownloadedImage.get(position));
        }
    }

    @Override
    public int getItemCount() {
        if (listOfImageURLs != null)
            return listOfImageURLs.size();
        else
            return listOfDownloadedImage.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView;
        }
    }
}
