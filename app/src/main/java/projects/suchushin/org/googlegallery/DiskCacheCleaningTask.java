package projects.suchushin.org.googlegallery;

import android.os.AsyncTask;

import com.bumptech.glide.Glide;


public class DiskCacheCleaningTask extends AsyncTask<Void, Void, Void> {
    private Glide glide;

    DiskCacheCleaningTask(Glide glide){
        this.glide = glide;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        glide.clearDiskCache();
        return null;
    }
}
