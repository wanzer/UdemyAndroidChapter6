package ricoapp.memorableplaces;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    static ArrayList<String> place = new ArrayList<>();;
    static ArrayList<LatLng> location = new ArrayList<>();
    static ArrayAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView)findViewById(R.id.list);
        ArrayList<String> latitude = new ArrayList<>();
        ArrayList<String> longtitude = new ArrayList<>();
        place.clear();
        latitude.clear();
        longtitude.clear();
        location.clear();
        SharedPreferences sharedPreferences = this.getSharedPreferences("ricoapp.memorableplaces", Context.MODE_PRIVATE);
        try {
            place=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("place",ObjectSerializer.serialize(new ArrayList<String>())));
            latitude=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("latitude",ObjectSerializer.serialize(new ArrayList<String>())));
            longtitude=(ArrayList<String>)ObjectSerializer.deserialize(sharedPreferences.getString("longtitude",ObjectSerializer.serialize(new ArrayList<String>())));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(place.size()>0 && latitude.size()>0 && longtitude.size()>0){
            for (int i=0;i<latitude.size();i++)
                location.add(new LatLng(Double.parseDouble(latitude.get(i)),Double.parseDouble(longtitude.get(i))));

        }

        place.add("Add memorable place");
        location.add(new LatLng(0,0));
        adapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,place);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
                intent.putExtra("Position",position);
                startActivity(intent);
            }
        });
    }
}
