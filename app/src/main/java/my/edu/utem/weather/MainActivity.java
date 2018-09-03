package my.edu.utem.weather;

import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    EditText editTextState;
    CustomAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editTextState=findViewById(R.id.editText_state);
        adapter = new CustomAdapter(getApplicationContext());
        RecyclerView recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
    }

    public void getWeather(View view) {
        // Instantiate the RequestQueue.
        String city = editTextState.getText().toString();
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="https://api.openweathermap.org/data/2.5/forecast/daily?q="+city+",My&appid=9fd7a449d055dba26a982a3220f32aa2";

// Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("debug", response);
                        try{
                            JSONObject jsonObject = new JSONObject(response);
                            //JSONObject nameArray = jsonObject.getJSONArray("weather").getJSONObject(0);
                            JSONArray weatherArray = jsonObject.getJSONArray("list");
                            for(int i = 0; i<weatherArray.length();i++){
                                adapter.addWeather(weatherArray.getJSONObject(i));
                            }
                            adapter.notifyDataSetChanged();

                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("debug", error.toString());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder{

        ImageView imageViewWeather;
        TextView textViewWeather, textViewDegree, textViewDate;

        public CustomViewHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.custom_row,parent,false));


            imageViewWeather=itemView.findViewById(R.id.imageView_image);
            textViewWeather=itemView.findViewById(R.id.textView_weather);
            textViewDegree=itemView.findViewById(R.id.textView_degree);
            textViewDate=itemView.findViewById(R.id.textView_date);
        }
    }

    public class CustomAdapter extends RecyclerView.Adapter<CustomViewHolder>{
        List<JSONObject> weatherList = new ArrayList<>();
        Context context;


        public CustomAdapter(Context context) {
            this.context = context;
        }

        public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new CustomViewHolder(LayoutInflater.from(viewGroup.getContext()), viewGroup);
        }


        @Override
        public void onBindViewHolder(@NonNull CustomViewHolder customViewHolder, int i) {
            JSONObject currentWeather = weatherList.get(i);

            try {
                String iconImage = currentWeather.getJSONArray("weather").getJSONObject(0).getString("icon");
                String imageURL = "https://openweathermap.org/img/w/"+iconImage+".png";
                Glide.with(context).load(imageURL).into(customViewHolder.imageViewWeather);

                customViewHolder.textViewWeather.setText(""+currentWeather.getJSONArray("weather").getJSONObject(0).getString("main"));
                customViewHolder.textViewDegree.setText(currentWeather.getJSONObject("temp").getDouble("day")-273+" C");
                customViewHolder.textViewDate.setText(""+currentWeather.getInt("dt"));
            }catch (JSONException e){
                e.printStackTrace();
            }

        }

        @Override
        public int getItemCount() {
            return weatherList.size();
        }

        public void addWeather(JSONObject weather){
            weatherList.add(weather);
        }
    }

}
