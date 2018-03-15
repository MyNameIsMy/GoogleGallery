package projects.suchushin.org.googlegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.Collections;
import java.util.List;

public class MasonryRecycleViewAdapter extends RecyclerView.Adapter<MasonryRecycleViewAdapter.ViewHolder>{
    private List<Bitmap> imageList;
    private Context context;

    MasonryRecycleViewAdapter(Context context, List<Bitmap> imageList) {
        this.context = context;
        this.imageList = imageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ImageView imageView = new ImageView(context);
        return new ViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.imageView.setImageBitmap(imageList.get(position));
        holder.imageView.setAdjustViewBounds(true);
        holder.imageView.setPadding(4,4,4,4);
    }

    @Override
    public int getItemCount() {
        return imageList.size();
    }

    void addItems(List<Bitmap> imageList){
        this.imageList.addAll(imageList);
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        ImageView imageView;

        ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView;
        }
    }
}
