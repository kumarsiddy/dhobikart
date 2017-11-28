package freakydevelopers.dhobikart.fragment;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import freakydevelopers.dhobikart.R;
import freakydevelopers.dhobikart.adapter.ClothAdapter;
import freakydevelopers.dhobikart.pojo.Cloth;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static freakydevelopers.dhobikart.Resources.MyURL.CLOTHLISTWOMEN;

/**
 * Created by PURUSHOTAM on 9/14/2016.
 */
public class WomenClothList extends Fragment {

    OkHttpClient client = new OkHttpClient();
    private List<Cloth> clothList = new ArrayList<>();
    private RecyclerView recyclerView;
    private ClothAdapter clothAdapter;
    Context context;
    TextView cartnotice;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.womenclothlist, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        cartnotice = (TextView) getActivity().findViewById(R.id.cartnotice);
        clothAdapter = new ClothAdapter(context, clothList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(clothAdapter);
        new GetResponse().execute(CLOTHLISTWOMEN);
        return view;

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class GetResponse extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            String string = null;
            Request request = new Request.Builder()
                    .url(url[0])
                    .build();
            Response response = null;
            try {
                response = client.newCall(request).execute();
                string = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

            return string;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            JSONArray jsonArray;

            try {
                jsonArray = new JSONArray(string);
                for (int i = 0; i < jsonArray.length(); i++) {
                    Cloth cloth = new Cloth();
                    String link, name, price;
                    JSONObject jsonObject = null;

                    jsonObject = jsonArray.getJSONObject(i);
                    cloth.setImg(link = jsonObject.getString("link"));
                    cloth.setName(name = jsonObject.getString("name"));
                    cloth.setPrice(price = jsonObject.getString("price"));
                    Log.d("GOT", link + name + price);


                    clothList.add(cloth);
                    clothAdapter.notifyDataSetChanged();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
