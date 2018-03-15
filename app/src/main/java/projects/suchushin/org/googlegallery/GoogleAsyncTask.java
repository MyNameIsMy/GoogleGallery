package projects.suchushin.org.googlegallery;

import android.graphics.Bitmap;
import android.os.AsyncTask;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

public class GoogleAsyncTask extends AsyncTask<String, Void, List<Bitmap>> {
    private final static String API_KEY = "AIzaSyBJOeuh-fFR5OpNuPdWCxo8WQZTE1wT4jw";
    private final static String CX = "004619996681643827902:o4zpeun436o";
    private final static String SEARCH_LINK = "https://www.googleapis.com/customsearch/v1";
    private TaskWork taskWork;
    private int start;

    GoogleAsyncTask(TaskWork taskWork, int start){
        this.taskWork = taskWork;
        this.start = start;
    }

    @Override
    protected List<Bitmap> doInBackground(String... strings) {
        String query = strings[0];
        try{
            URL url = fullSearchURL(query);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            String line;
            StringBuffer buffer = new StringBuffer();

            while ((line = reader.readLine()) != null){
                buffer.append(line);
            }

            return taskWork.onTaskRunning(buffer.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<Bitmap> imageList) {
        super.onPostExecute(imageList);
        if (imageList != null)
            taskWork.onCompleted(imageList, start);
    }

    private URL fullSearchURL(String query){
        String stringURL = SEARCH_LINK;

        stringURL += "?key=" + API_KEY;

        stringURL += "&cx=" + CX;

        stringURL += "&alt=json";

        stringURL += "&searchType=image";

        stringURL += "&start=" + start;

        stringURL += "&q=" + query;

        URL url = null;

        try {
            url = new URL(stringURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return url;
    }
}
