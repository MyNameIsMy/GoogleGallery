package projects.suchushin.org.googlegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.Target;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements TaskWork {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            List<Bitmap> savedImages = new ArrayList<>();
            extractImagesFromCache(savedImages);
            setGridOfImage(savedImages);
        } else {
            cleanCache(this);
        }

        findViewById(R.id.search_button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                EditText editText = findViewById(R.id.search_string);
                String query = String.valueOf(editText.getText());
                GoogleAsyncTask asyncTask = new GoogleAsyncTask(MainActivity.this);
                asyncTask.execute(query);
            }
        });

    }

    @Override
    public List<Bitmap> onTaskRunning(String result) {
        try {
            List<Bitmap> resultList = new ArrayList<>();
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("items");
            for (int i = 0; i < array.length(); i++){
                JSONObject o = array.getJSONObject(i);
                Bitmap image = downloadImage(o.getString("link"));
                resultList.add(image);
            }
            return resultList;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void onCompleted(List<Bitmap> imageList) {
        setGridOfImage(imageList);
    }

    private Bitmap downloadImage(String link){
        try {
            return Glide.with(this).load(link).asBitmap().into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setGridOfImage(List<Bitmap> imageList){
        RecyclerView recyclerView = findViewById(R.id.grid_of_images);
        if (getResources().getConfiguration().orientation == 1)
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
        else
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(new MasonryRecycleViewAdapter(this, imageList));
    }

    void cleanCache(Context context){
        File cacheDir =  Glide.getPhotoCacheDir(context);
        for (File imgF : cacheDir.listFiles()){
            imgF.delete();
        }
    }

    void extractImagesFromCache(List<Bitmap> savedImages){
        File cacheDir =  Glide.getPhotoCacheDir(this);
        for (File imgF : cacheDir.listFiles()){
            if (!imgF.getName().equals("journal")){
                Bitmap imgB = BitmapFactory.decodeFile(imgF.getAbsolutePath());
                savedImages.add(imgB);
            }
        }
    }
}
