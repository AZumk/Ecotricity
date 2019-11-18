package com.anazunkeller.ecotricity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.anazunkeller.ecotricity.content.Content;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ItemListActivity extends AppCompatActivity {

    View recyclerView;
    SimpleItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Carbon Intensity by Region");
        setSupportActionBar(toolbar);

        recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;

        Content.addItem(new Content.RegionData("", "", "Loading region....", "00", "loading"));

        fetchAPIData();
    }

    private void fetchAPIData() {

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.UK);
        String date = sdf.format(new Date());

        String url = "https://api.carbonintensity.org.uk/regional/intensity/"+
                date
                +"/fw24h";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    Content.ITEMS.clear();

                    JSONObject jsonObject = new JSONObject(response);
                    JSONArray data = jsonObject.getJSONArray("data");
                    JSONArray jsonArray = data.getJSONObject(0).getJSONArray("regions");

                    Log.d("Volley", jsonArray.toString());

                    for(int i = 0; i < jsonArray.length(); i++){

                        JSONObject currentRegion = jsonArray.getJSONObject(i);

                        Content.RegionData regionData = new Content.RegionData(
                                currentRegion.getString("regionid"),
                                currentRegion.getString("dnoregion"),
                                currentRegion.getString("shortname"),
                                currentRegion.getJSONObject("intensity").getString("forecast"),
                                currentRegion.getJSONObject("intensity").getString("index")
                        );

                        Content.addItem(regionData);
                    }

                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("Volley", e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Volley", error.toString());
            }
        });

        setupRecyclerView((RecyclerView) recyclerView);
        requestQueue.add(stringRequest);

    }

    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        adapter = new SimpleItemRecyclerViewAdapter(Content.ITEMS);
        recyclerView.setAdapter(adapter);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<Content.RegionData> mValues;

        SimpleItemRecyclerViewAdapter(List<Content.RegionData> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
            holder.mShortNameView.setText(mValues.get(position).shortName);
            holder.mForecastView.setText(mValues.get(position).intensityForecast);
            holder.mIndexView.setText(mValues.get(position).intensityIndex);

            switch (mValues.get(position).intensityIndex){
                case "very low":
                    holder.mForecastStatus.setColorFilter(getResources().getColor(R.color.veryLow));
                    break;
                case "low":
                    holder.mForecastStatus.setColorFilter(getResources().getColor(R.color.low));
                    break;
                case "moderate":
                    holder.mForecastStatus.setColorFilter(getResources().getColor(R.color.moderate));
                    break;
                case "high":
                    holder.mForecastStatus.setColorFilter(getResources().getColor(R.color.high));
                    break;
                case "very high":
                    holder.mForecastStatus.setColorFilter(getResources().getColor(R.color.veryHigh));
                    break;
                default:
                    holder.mForecastStatus.setColorFilter(getResources().getColor(R.color.lightGray));
                    break;

            }

            holder.itemView.setTag(mValues.get(position));
        }

        @Override
        public int getItemCount() {
            return mValues.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView mShortNameView;
            final TextView mForecastView;
            final TextView mIndexView;
            final ImageView mForecastStatus;

            ViewHolder(View view) {
                super(view);
                mShortNameView = (TextView) view.findViewById(R.id.item_shortName);
                mForecastView = (TextView) view.findViewById(R.id.item_forecast);
                mIndexView = (TextView) view.findViewById(R.id.item_index);
                mForecastStatus = (ImageView) view.findViewById(R.id.forecast_status_bar);
            }
        }
    }
}
