package projects.suchushin.org.googlegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements OnTaskCompleted{
    private static final int NUMBER_OF_IMAGES_COLUMNS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null){
            List<Bitmap> savedImages = new ArrayList<>();
            extractImagesFromCache(this, savedImages);
            setGridOfImage(null, savedImages);
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
    public void onTaskCompleted(String result) {
        try {
            List<String> resultList = new ArrayList<>();
            JSONObject object = new JSONObject(result);
            JSONArray array = object.getJSONArray("items");
            for (int i = 0; i < array.length(); i++){
                JSONObject o = array.getJSONObject(i);
                resultList.add(o.getString("link"));
            }
            setGridOfImage(resultList, null);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setGridOfImage(List<String> imageURLList, List<Bitmap> savedImages){
        RecyclerView recyclerView = findViewById(R.id.grid_of_images);
        recyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_OF_IMAGES_COLUMNS));
        recyclerView.setAdapter(new MasonryRecycleViewAdapter(this, imageURLList, savedImages));
    }

    void cleanCache(Context context){
        File cacheDir =  Glide.getPhotoCacheDir(context);
        for (File imgF : cacheDir.listFiles()){
            imgF.delete();
        }
    }

    void extractImagesFromCache(Context context, List<Bitmap> savedImages){
        File cacheDir =  Glide.getPhotoCacheDir(context);
        for (File imgF : cacheDir.listFiles()){
            if (!imgF.getName().equals("journal")){
                Bitmap imgB = BitmapFactory.decodeFile(imgF.getAbsolutePath());
                savedImages.add(imgB);
            }
        }
    }
}
