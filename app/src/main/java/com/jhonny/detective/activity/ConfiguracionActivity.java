package com.jhonny.detective.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import com.jhonny.detective.Constantes;
import com.jhonny.detective.util.FileUtil;
import com.jhonny.detective.location.Localizador;
import com.jhonny.detective.R;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;


public class ConfiguracionActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, OnItemSelectedListener {

	private Spinner spDistancia;
	private Spinner spTiempo;
	private Spinner spFondo;
	private View view;
	private Context context;

	private static String PASS;
	private static float DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	private static long TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	private static String TIPO_CUENTA;
	private static String FONDO_PANTALLA;
	private static String EMAIL;
	private int contSalida = 0;

	//Constants for tablet sized ads (728x90)
	private static final int IAB_LEADERBOARD_WIDTH = 728;
	private static final int MED_BANNER_WIDTH = 480;
	//Constants for phone sized ads (320x50)
	private static final int BANNER_AD_WIDTH = 320;
	private static final int BANNER_AD_HEIGHT = 50;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_configuracion);

		contSalida = 0;
		int pos1 = 0;
		int pos2 = 0;
		int pos3 = 0;

		try {
			this.context = this;
			this.view = getWindow().getDecorView();

			// barra de herramientas
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);

			// se cargan los datos de la configuracion almacenada
			spDistancia = (Spinner) findViewById(R.id.spinner1);
			spTiempo = (Spinner) findViewById(R.id.spinner2);
			spFondo = (Spinner) findViewById(R.id.spinner3);

			cargarSpinnerFondoPantallas();

			pos1 = FileUtil.getPosicionSpinnerSeleccionada(1, this);
			pos2 = FileUtil.getPosicionSpinnerSeleccionada(2, this);
			pos3 = FileUtil.getPosicionSpinnerSeleccionada(3, this);

			spDistancia.setSelection(pos1);
			spTiempo.setSelection(pos2);
			spFondo.setSelection(pos3);

			spDistancia.setOnItemSelectedListener(this);
			spTiempo.setOnItemSelectedListener(this);
			spFondo.setOnItemSelectedListener(this);

			int placementWidth = BANNER_AD_WIDTH;

			//Finds an ad that best fits a users device.
			if (canFit(IAB_LEADERBOARD_WIDTH)) {
				placementWidth = IAB_LEADERBOARD_WIDTH;
			} else if (canFit(MED_BANNER_WIDTH)) {
				placementWidth = MED_BANNER_WIDTH;
			}

//			MMAdView adView = new MMAdView(this);
//			adView.setApid("148574");
//			MMRequest request = new MMRequest();
//			adView.setMMRequest(request);
//			adView.setId(MMSDK.getDefaultAdId());
//			adView.setWidth(placementWidth);
//			adView.setHeight(BANNER_AD_HEIGHT);

			LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout2);
			//Add the adView to the layout. The layout is assumed to be a RelativeLayout.
//			layout.addView(adView);
//			adView.getAd();

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
//		reiniciarFondoOpciones();
		cargaConfiguracionGlobal();
		cargaPublicidad();
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

				switch ((int) spFondo.getSelectedItemId()) {
					case 0:
						FONDO_PANTALLA = "1";
						break;
					case 1:
						FONDO_PANTALLA = "2";
						break;
					case 2:
						FONDO_PANTALLA = "3";
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
				Localizador localizador = FileUtil.getLocalizador();

				if (locationManagerGps != null && locationManagerGps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
							&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						// TODO: Consider calling
						//    ActivityCompat#requestPermissions
						// here to request the missing permissions, and then overriding
						//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
						//                                          int[] grantResults)
						// to handle the case where the user grants the permission. See the documentation
						// for ActivityCompat#requestPermissions for more details.
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
				cargaConfiguracionGlobal();
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
	
	private void cargarSpinnerFondoPantallas(){
		try{
			List<String> list = new ArrayList<String>();
			list.add("Fondo 1");
			list.add("Fondo 2");
			list.add("Fondo 3");
			
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, list);
			dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			
			spFondo.setAdapter(dataAdapter);
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private void cargaConfiguracionGlobal(){
		try{
			if(this.view != null){
				String fondo = FileUtil.getFondoPantallaAlmacenado(this.context);
				if(fondo != null){
					String imagen = Constantes.mapaFondo.get(Integer.parseInt(fondo));
					int imageResource1 = this.view.getContext().getApplicationContext().getResources().getIdentifier(
							imagen, "mipmap", this.view.getContext().getApplicationContext().getPackageName());
					Drawable image = this.view.getContext().getResources().getDrawable(imageResource1);
					ImageView imageView = (ImageView)findViewById(R.id.fondo_configuracion);
					imageView.setImageDrawable(image);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
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
	
	protected boolean canFit(int adWidth) {
		int adWidthPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, adWidth, getResources().getDisplayMetrics());
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		return metrics.widthPixels >= adWidthPx;
	}
	
	private void cargaPublicidad(){
		int placementWidth = BANNER_AD_WIDTH;
		
		//Finds an ad that best fits a users device.
		if(canFit(IAB_LEADERBOARD_WIDTH)) {
		    placementWidth = IAB_LEADERBOARD_WIDTH;
		}else if(canFit(MED_BANNER_WIDTH)) {
		    placementWidth = MED_BANNER_WIDTH;
		}
		
//		MMAdView adView = new MMAdView(this);
//		adView.setApid("148574");
//		MMRequest request = new MMRequest();
//		adView.setMMRequest(request);
//		adView.setId(MMSDK.getDefaultAdId());
//		adView.setWidth(placementWidth);
//		adView.setHeight(BANNER_AD_HEIGHT);
		
		LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout2);
		layout.removeAllViews();
//		layout.addView(adView);
//		adView.getAd();
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

	@SuppressWarnings("StatementWithEmptyBody")
	@Override
	public boolean onNavigationItemSelected(MenuItem item) {
		// Handle navigation view item clicks here.
		int id = item.getItemId();
		Intent intent = null;

		if (id == R.id.nav_principal) {
			intent = new Intent(this, InicioActivity.class);
		} else if (id == R.id.nav_mapa) {
			intent = new Intent(this, MapaActivity.class);
		} else if (id == R.id.nav_posiciones) {
			intent = new Intent(this, PosicionesActivity.class);
//		} else if (id == R.id.nav_compartir) {
//			intent = new Intent(this, EnConstruccion.class);
//		} else if (id == R.id.nav_send) {
//			intent = new Intent(this, EnConstruccion.class);
		} else if (id == R.id.nav_settings) {
			intent = new Intent(this, ConfiguracionActivity.class);
		} else if (id == R.id.nav_password) {
			intent = new Intent(this, ContrasenaActivity.class);
		} else if (id == R.id.nav_borrar_coordenadas) {
			intent = new Intent(this, BorrarPosicionesActivity.class);
//		} else if (id == R.id.nav_desarrollador) {
//			intent = new Intent(this, EnConstruccion.class);
		} else if (id == R.id.nav_acerca) {
			intent = new Intent(this, AcercaActivity.class);
		}
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		drawer.closeDrawer(GravityCompat.START);
		startActivity(intent);
		return true;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
	}
}
