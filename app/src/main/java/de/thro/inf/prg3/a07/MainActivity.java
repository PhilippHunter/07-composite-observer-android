package de.thro.inf.prg3.a07;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.thro.inf.prg3.a07.api.OpenMensaAPI;
import de.thro.inf.prg3.a07.model.Meal;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

	private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
	private Retrofit retrofit = new Retrofit.Builder()
		.addConverterFactory(GsonConverterFactory.create())
		.baseUrl("http://openmensa.org/api/v2/")
		.build();

	private OpenMensaAPI openMensaAPI = retrofit.create(OpenMensaAPI.class);

	ListView listView = findViewById(R.id.listMeals);
	Button btnRefresh = findViewById(R.id.btnRefresh);
	Switch swVegetarian = findViewById(R.id.swVegetarian);


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// this will inflate the layout from res/layout/activity_main.xml
		setContentView(R.layout.activity_main);

		// add your code here
		listView.setAdapter(new ArrayAdapter<>(
			MainActivity.this,
			R.layout.meal_entry,
			new String[]{"Moinsn", "Ichbins", "derbeder"}
		));

		btnRefresh.setOnClickListener((view) ->
			this.fetchMeals()
		);
	}

	private void fetchMeals() {
		Call<List<Meal>> mealCall = openMensaAPI.getMeals(sdf.format(new Date()));
		mealCall.enqueue(new Callback<List<Meal>>() {
			@Override
			public void onResponse(Call<List<Meal>> call, Response<List<Meal>> response) {
				if (!response.isSuccessful()) return;
				if (response.body() == null) return;
				if (!swVegetarian.isActivated())
					listView.setAdapter(new ArrayAdapter<>(MainActivity.this,
						R.layout.meal_entry,
						response.body())
					);
			}

			@Override
			public void onFailure(Call<List<Meal>> call, Throwable t) {
				listView.setAdapter(new ArrayAdapter<>(MainActivity.this,
					R.layout.meal_entry,
					new String[]{"Fail"})
				);
			}
		});
	}
}
