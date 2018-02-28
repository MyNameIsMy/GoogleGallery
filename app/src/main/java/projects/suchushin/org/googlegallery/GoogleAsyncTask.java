package projects.suchushin.org.googlegallery;

import android.os.AsyncTask;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class GoogleAsyncTask extends AsyncTask<String, Void, String> {
    private final static String API_KEY = "AIzaSyBJOeuh-fFR5OpNuPdWCxo8WQZTE1wT4jw";
    private final static String CX = "004619996681643827902:o4zpeun436o";
    private final static String SEARCH_LINK = "https://www.googleapis.com/customsearch/v1";
    private OnTaskCompleted onTaskCompleted;

    GoogleAsyncTask(OnTaskCompleted onTaskCompleted){
        this.onTaskCompleted = onTaskCompleted;
    }

    @Override
    protected String doInBackground(String... strings) {
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

            return buffer.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        onTaskCompleted.onTaskCompleted(result);
    }

    private URL fullSearchURL(String query){
        String stringURL = SEARCH_LINK;

        stringURL += "?key=" + API_KEY;

        stringURL += "&cx=" + CX;

        stringURL += "&alt=json";

        stringURL += "&searchType=image";

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