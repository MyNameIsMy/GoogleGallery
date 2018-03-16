package projects.suchushin.org.googlegallery;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity implements TaskWork {
    private Glide glide;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private EditText editText;
    private Button button;
    private String lastSearchQuery;
    private static final String LSQ_KEY = "lsq";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        if (savedInstanceState != null){
            lastSearchQuery = savedInstanceState.getString(LSQ_KEY);
            List<Bitmap> savedImages = new ArrayList<>();
            extractImagesFromCache(savedImages);
            setGridOfImages(savedImages);
            getButton().setVisibility(View.INVISIBLE);
            if (savedImages.size() > 0)
                getButton().setVisibility(View.VISIBLE);
        } else {
            cleanCache();
            hideKeyboard();
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(LSQ_KEY, lastSearchQuery);
        super.onSaveInstanceState(outState);
    }

    @OnClick(R.id.search_button)
    public void search(){
        cleanCache();
        hideKeyboard();
        String query = String.valueOf(getEditText().getText());
        lastSearchQuery = query;
        GoogleAsyncTask asyncTask = new GoogleAsyncTask(MainActivity.this, 1);
        asyncTask.execute(query);
        getProgressBar().setVisibility(View.VISIBLE);
    }

    @OnClick(R.id.load_more_button)
    public void searchMore(){
        int start = getRecyclerView().getAdapter().getItemCount() + 1;
        GoogleAsyncTask asyncTask = new GoogleAsyncTask(MainActivity.this, start);
        asyncTask.execute(lastSearchQuery);
        getButton().setVisibility(View.INVISIBLE);
        getProgressBar().setVisibility(View.VISIBLE);
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
    public void onCompleted(List<Bitmap> imageList, int start) {
        if (start < 11) {
            setGridOfImages(imageList);
        } else {
            updateGridOfImages(imageList);
        }
        getButton().setVisibility(View.VISIBLE);
        getProgressBar().setVisibility(View.INVISIBLE);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private Bitmap downloadImage(String link){
        try {
            return Glide.with(this).load(link).asBitmap().into(100, 100).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void setGridOfImages(List<Bitmap> imageList){
        recyclerView = getRecyclerView();
        if (getResources().getConfiguration().orientation == 1)
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
        else
            recyclerView.setLayoutManager(new StaggeredGridLayoutManager(4, OrientationHelper.VERTICAL));
        recyclerView.setAdapter(new MasonryRecycleViewAdapter(this, imageList));
        getButton().setVisibility(View.VISIBLE);
    }

    private void updateGridOfImages(List<Bitmap> imageList){
        MasonryRecycleViewAdapter adapter = (MasonryRecycleViewAdapter) getRecyclerView().getAdapter();
        adapter.addItems(imageList);
        adapter.notifyDataSetChanged();
    }

    void cleanCache(){
        Glide glide = getGlide();
        new DiskCacheCleaningTask(glide).execute();
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

    private Glide getGlide(){
        if (glide == null)
            glide = Glide.get(this);
        return glide;
    }

    private RecyclerView getRecyclerView(){
        if (recyclerView == null)
            recyclerView = findViewById(R.id.grid_of_images);
        return recyclerView;
    }

    private EditText getEditText(){
        if (editText == null)
            editText = findViewById(R.id.search_string);
        return editText;
    }

    private Button getButton(){
        if (button == null)
            button = findViewById(R.id.load_more_button);
        return button;
    }

    private ProgressBar getProgressBar(){
        if (progressBar == null)
            progressBar = findViewById(R.id.progressBar);
        return progressBar;
    }
}
