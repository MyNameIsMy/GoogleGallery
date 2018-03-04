package projects.suchushin.org.googlegallery;


import android.graphics.Bitmap;

import java.util.List;

interface TaskWork {
    List<Bitmap> onTaskRunning(String result);
    void onCompleted(List<Bitmap> imageList);
}
