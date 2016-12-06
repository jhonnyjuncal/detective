package com.jhonny.detective.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.jhonny.detective.Constantes;
import com.jhonny.detective.R;
import com.jhonny.detective.activity.custom.DrawerNavigationControl;
import com.jhonny.detective.service.LocalizadorListener;
import com.jhonny.detective.util.FileUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ConfiguracionActivity extends DrawerNavigationControl implements OnItemSelectedListener {

	private Spinner spDistancia;
	private Spinner spTiempo;

	private View view;
	private Context context;
	private int contSalida = 0;

	private static String PASS;
	private static float DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	private static long TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	private static String TIPO_CUENTA;
	private static String FONDO_PANTALLA;
	private static String EMAIL;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);

		contSalida = 0;
		int pos1 = 0;
		int pos2 = 0;

		try {
			this.context = this;
			this.view = getWindow().getDecorView();

			// barra de herramientas
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);

			ArrayAdapter adapterDistancia = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item,
					getResources().getStringArray(R.array.lista_distancias));
			spDistancia = (Spinner) findViewById(R.id.spinner1);
			spDistancia.setAdapter(adapterDistancia);

			ArrayAdapter adapterTiempo = new ArrayAdapter<String>(this,
					android.R.layout.simple_spinner_dropdown_item,
					getResources().getStringArray(R.array.lista_tiempos));
			spTiempo = (Spinner) findViewById(R.id.spinner2);
			spTiempo.setAdapter(adapterTiempo);

			pos1 = FileUtil.getPosicionSpinnerSeleccionada(1, this);
			pos2 = FileUtil.getPosicionSpinnerSeleccionada(2, this);

			spDistancia.setSelection(pos1);
			spTiempo.setSelection(pos2);

			spDistancia.setOnItemSelectedListener(this);
			spTiempo.setOnItemSelectedListener(this);

			DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
			ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
			drawer.setDrawerListener(toggle);
			toggle.syncState();

			NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
			navigationView.setNavigationItemSelectedListener(this);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.menu.menu_configuracion, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		contSalida = 0;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (contSalida == 0) {
				contSalida++;
				Toast.makeText(this, getResources().getString(R.string.txt_salir_1_aviso), Toast.LENGTH_SHORT).show();
				return true;
			} else {
				contSalida = 0;
				Intent intent = new Intent();
				intent.setAction(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				startActivity(intent);
			}
		}
		//para las demas cosas, se reenvia el evento al listener habitual
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		try {
			Properties prop = FileUtil.getFicheroAssetConfiguracion(this);

			if (prop != null) {
				PASS = (String) prop.get(Constantes.PROP_PASSWORD);
				TIPO_CUENTA = (String) prop.get(Constantes.PROP_TIPO_CUENTA);
				EMAIL = (String) prop.get(Constantes.PROP_EMAIL);

				switch ((int) spDistancia.getSelectedItemId()) {
					case 0:
						// 5000 metros
						DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = 5000;
						break;
					case 1:
						// 10000 metros
						DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = 10000;
						break;
					case 2:
						// 15000 metros
						DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = 15000;
						break;
					case 3:
						// 20000 metros
						DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = 20000;
						break;
				}

				switch ((int) spTiempo.getSelectedItemId()) {
					case 0:
						// 900000 milisegundos
						TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = 900000;
						break;
					case 1:
						// 1800000 milisegundos
						TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = 1800000;
						break;
					case 2:
						// 2700000 milisegundos
						TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = 2700000;
						break;
					case 3:
						// 3600000 milisegundos
						TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = 3600000;
						break;
				}

				Map<String, String> valores = new HashMap<String, String>();
				valores.put(Constantes.PROP_PASSWORD, PASS);
				valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, String.valueOf(DISTANCIA_MINIMA_PARA_ACTUALIZACIONES));
				valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, String.valueOf(TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES));
				valores.put(Constantes.PROP_TIPO_CUENTA, TIPO_CUENTA);
				valores.put(Constantes.PROP_FONDO_PANTALLA, FONDO_PANTALLA);
				valores.put(Constantes.PROP_EMAIL, EMAIL);
				valores.put(Constantes.PROP_EMAIL_ENVIO, (String) prop.get(Constantes.PROP_EMAIL_ENVIO));
				valores.put(Constantes.PROP_EMAIL_CHECK, (String) prop.get(Constantes.PROP_EMAIL_CHECK));

				LocationManager locationManagerGps = FileUtil.getLocationManagerGps();
				LocationManager locationManagerInternet = FileUtil.getLocationManagerInternet();
				LocalizadorListener localizador = FileUtil.getLocalizador();

				if (locationManagerGps != null && locationManagerGps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
							&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						return;
					}
					locationManagerGps.removeUpdates(localizador);

				} else if(locationManagerInternet != null && locationManagerInternet.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					locationManagerInternet.removeUpdates(localizador);
				}
				
				if(locationManagerGps != null && locationManagerGps.isProviderEnabled(LocationManager.GPS_PROVIDER))
					FileUtil.getLocationManagerGps().requestLocationUpdates(LocationManager.GPS_PROVIDER,
							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
							localizador);
				else if(locationManagerInternet != null && locationManagerInternet.isProviderEnabled(LocationManager.NETWORK_PROVIDER))
					FileUtil.getLocationManagerInternet().requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES, 
							localizador);
				
				
				FileUtil.guardaDatosConfiguracion(valores, this);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();

		//noinspection SimplifiableIfStatement
//		if (id == R.id.action_settings) {
//			return true;
//		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}
}
