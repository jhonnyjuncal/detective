package com.jhonny.detective.activity;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.jhonny.detective.R;
import com.jhonny.detective.model.ObjetoPosicion;
import com.jhonny.detective.Constantes;
import com.jhonny.detective.model.Email;
import com.jhonny.detective.util.FileUtil;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
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
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;


public class PrincipalActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
		GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	protected static String PASS;
	protected static float DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	protected static long TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	protected static Integer TIPO_CUENTA;
	protected static final String FICHERO_CONFIGURACION = "config.properties";

	public static List<ObjetoPosicion> listaPosiciones = null;
	public static View viewPrincipal = null;
	public static Resources resourcesPrincipal = null;
	private int contSalida = 0;
	private View view;
	private Context context;
	private Dialog dialogo;
	private EditText email;
	private CheckBox check;
	private Properties prop;

	private static final int IAB_LEADERBOARD_WIDTH = 728;
	private static final int MED_BANNER_WIDTH = 480;
	private static final int BANNER_AD_WIDTH = 320;
	private static final int BANNER_AD_HEIGHT = 50;

	private GoogleApiClient mGoogleApiClient;
	private Location mLastLocation;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_principal);
		contSalida = 0;

		try {
			//MMSDK.setLogLevel(MMSDK.LOG_LEVEL_DEBUG);
			this.context = this;
			this.view = getWindow().getDecorView();

			// barra de herramientas
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);

			// Create an instance of GoogleAPIClient.
			if (mGoogleApiClient == null) {
				mGoogleApiClient = new GoogleApiClient.Builder(this)
						.addConnectionCallbacks(this)
						.addOnConnectionFailedListener(this)
						.addApi(LocationServices.API)
						.build();
			}

			// se inicia el servicio de actualizacion de coordenadas
			iniciaServicio();

			FileUtil.setWifiManager((WifiManager) getSystemService(Context.WIFI_SERVICE));

			// carga los datos de la configuracion
			cargaDatosConfiguracion();

			// estado actual de la wifi
			informaEstadoActualGPS();

			// posiciones almacenadas
			FileUtil.cargaPosicionesAlmacenadas((Context) this, getWindow().getDecorView());

			// informa estado de la wifi/3G
			informaEstadoActualInternet();

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
//    	MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.menu.menu_principal, menu);
		return true;
	}


	@Override
	public void onDestroy() {
		try {
			contSalida = 0;
			super.onDestroy();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	@Override
	public void onResume() {
		super.onResume();
		contSalida = 0;

		try {
//	    	reiniciarFondoOpciones();
			cargaConfiguracionGlobal();
			FileUtil.cargaPosicionesAlmacenadas((Context) this, getWindow().getDecorView());
			cargaPublicidad();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public void muestraMapa(View view) {
		Intent intent = new Intent(this, MapaActivity.class);
		startActivity(intent);
	}


	public void muestraPosiciones(View view) {
		Intent intent = new Intent(this, PosicionesActivity.class);
		startActivity(intent);
	}


	public void enviarPosicionesPorMail(View view) {
		try {
			dialogo = new Dialog(this, R.style.Theme_Dialog_Translucent);
			dialogo.setTitle(R.string.titulo_envio_email);
			dialogo.setContentView(R.layout.alert_email);

			Button boton_enviar = (Button) dialogo.findViewById(R.id.btnEnviar);
			Button boton_cancelar = (Button) dialogo.findViewById(R.id.btnCancelar);
			email = (EditText) dialogo.findViewById(R.id.alert_editText1);
			check = (CheckBox) dialogo.findViewById(R.id.alert_checkBox1);

			try {
				prop = FileUtil.getFicheroAssetConfiguracion(context);
			} catch (IOException e) {
				e.printStackTrace();
			}

			if (prop != null) {
				String valor_check = (String) prop.get(Constantes.PROP_EMAIL_CHECK);
				if (valor_check != null && valor_check.equals("true")) {
					this.check.setChecked(true);
					String email_envio = (String) prop.get(Constantes.PROP_EMAIL_ENVIO);
					if (email_envio != null)
						this.email.setText(email_envio);
				} else
					this.check.setChecked(false);
			}

			boton_enviar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String direccion = email.getText().toString();

					Map<String, String> valores = new HashMap<String, String>();

					valores.put(Constantes.PROP_PASSWORD, (String) prop.get(Constantes.PROP_PASSWORD));
					valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, (String) prop.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES));
					valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, (String) prop.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES));
					valores.put(Constantes.PROP_TIPO_CUENTA, (String) prop.get(Constantes.PROP_TIPO_CUENTA));
					valores.put(Constantes.PROP_FONDO_PANTALLA, (String) prop.get(Constantes.PROP_FONDO_PANTALLA));
					valores.put(Constantes.PROP_EMAIL, (String) prop.get(Constantes.PROP_EMAIL));

					if (check.isChecked()) {
						// guardar la direccion de email en el fichero de propiedades
						valores.put(Constantes.PROP_EMAIL_ENVIO, direccion);
						valores.put(Constantes.PROP_EMAIL_CHECK, "true");
					} else {
						// guardar la direccion de email en el fichero de propiedades
						valores.put(Constantes.PROP_EMAIL_ENVIO, "");
						valores.put(Constantes.PROP_EMAIL_CHECK, "false");
					}

					FileUtil.guardaDatosConfiguracion(valores, context);

					Email.enviarPosicionesPorMail(PrincipalActivity.this, direccion, context);
					dialogo.dismiss();
				}
			});

			boton_cancelar.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogo.cancel();
				}
			});

			dialogo.show();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	/**
	 * muestra el estado actual del GPS
	 */
	private void informaEstadoActualGPS() {
		try {
			seleccionarOrigenDeLocalizacion();
			boolean enabled = false;

			LocationManager loc = FileUtil.getLocationManagerGps();
			if (loc != null)
				enabled = loc.isProviderEnabled(LocationManager.GPS_PROVIDER);

			TextView textoStatus = (TextView) findViewById(R.id.textView2);
			if (enabled == false)
				textoStatus.setText(getResources().getString(R.string.txt_apagada));
			else
				textoStatus.setText(getResources().getString(R.string.txt_encendida));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private void seleccionarOrigenDeLocalizacion() {
//		Localizador localizador = null;
//
//		try {
			// listener del GPS
//			LocationManager locationGps = FileUtil.getLocationManagerGps();
//			LocationManager locationInternet = FileUtil.getLocationManagerInternet();
//
//			if (locationGps == null) {
//				localizador = FileUtil.getLocalizador();
//				if (localizador == null) {
//					localizador = new Localizador();
//					localizador.view = getWindow().getDecorView();
//					localizador.contexto = (Context) this;
//					FileUtil.setLocalizador(localizador);
//				}
//				locationGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			}
//			if (locationInternet == null) {
//				localizador = FileUtil.getLocalizador();
//				if (localizador == null) {
//					localizador = new Localizador();
//					localizador.view = getWindow().getDecorView();
//					localizador.contexto = (Context) this;
//					FileUtil.setLocalizador(localizador);
//				}
//				locationInternet = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//			}
//
//			if (locationGps != null) {
//				if (locationGps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//					locationGps.requestLocationUpdates(LocationManager.GPS_PROVIDER,
//							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES,
//							localizador);
//				}
//				FileUtil.setLocationManagerGps(locationGps);
//			}
//
//			if (locationInternet != null) {
//				if (locationInternet.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
//					locationInternet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
//							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES,
//							localizador);
//				}
//				FileUtil.setLocationManagerGps(locationGps);
//			}

			System.out.println(" Altitud: " + mLastLocation.getAltitude());
			System.out.println(" Latitud: " + mLastLocation.getLatitude());
			System.out.println("Longitud: " + mLastLocation.getLongitude());

//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
	}


	/**
	 * Carga el fichero de configuracion "config.properties"
	 */
	private void cargaDatosConfiguracion() {
		try {
			// lectura del fichero de configuracion
			Properties prefs = new Properties();
			Context ctx = this;

			try {
				prefs = FileUtil.getFicheroAssetConfiguracion(ctx);
			} catch (IOException io) {
				String mensaje = io.getMessage();
				io.printStackTrace();
				Toast.makeText(PrincipalActivity.this, mensaje, Toast.LENGTH_LONG).show();
			}

			if (prefs != null) {
    			/* TIEMPO PARA ACTUALIZACIONES */
				String tiempo = (String) prefs.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES);
				if (tiempo != null && tiempo.length() > 0)
					TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES = Long.parseLong(tiempo);
    			
    			/* DISTANCIA PARA ACTUALIZACIONES */
				String distancia = (String) prefs.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES);
				if (distancia != null && distancia.length() > 0)
					DISTANCIA_MINIMA_PARA_ACTUALIZACIONES = Float.parseFloat(distancia);
    			
    			/* TIPO DE CUENTA */
				String tipoCuenta = (String) prefs.get(Constantes.PROP_TIPO_CUENTA);
				if (tipoCuenta != null && tipoCuenta.length() > 0) {
					TIPO_CUENTA = Integer.parseInt(tipoCuenta);
					//TextView textoCuenta = (TextView) findViewById(R.id.textView8);
					//textoCuenta.setText(tipoCuenta);
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	protected void informaEstadoActualInternet() {
		try {
			PrincipalActivity.viewPrincipal = getWindow().getDecorView();
			PrincipalActivity.resourcesPrincipal = getResources();
			TextView textoWifi = (TextView) findViewById(R.id.textView6);

			if (FileUtil.getWifiManager().isWifiEnabled())
				textoWifi.setText(getResources().getString(R.string.txt_encendida));
			else
				textoWifi.setText(getResources().getString(R.string.txt_apagada));
		} catch (Exception ex) {
			ex.printStackTrace();
		}
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


	private void iniciaServicio() {
		try {
//    		ServicioActualizacion.establecerActividadPrincipal(this);
//    		Intent servicio = new Intent(this, ServicioActualizacion.class);
//    		
//    		// se ejecuta el servicio
//    		startService(servicio);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void cargaConfiguracionGlobal() {
		try {
			if (this.view != null) {
				String fondo = FileUtil.getFondoPantallaAlmacenado(this.context);
				if (fondo != null) {
					String imagen = Constantes.mapaFondo.get(Integer.parseInt(fondo));
					int imageResource1 = this.view.getContext().getApplicationContext().getResources().getIdentifier(
							imagen, "mipmap", this.view.getContext().getApplicationContext().getPackageName());
					Drawable image = this.view.getContext().getResources().getDrawable(imageResource1);
					ImageView imageView = (ImageView) findViewById(R.id.fondo_principal);
					imageView.setImageDrawable(image);
				}
			}
		} catch (Exception ex) {
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
		int adWidthPx = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, adWidth, getResources().getDisplayMetrics());
		DisplayMetrics metrics = this.getResources().getDisplayMetrics();
		return metrics.widthPixels >= adWidthPx;
	}

	private void cargaPublicidad() {
		int placementWidth = BANNER_AD_WIDTH;

		//Finds an ad that best fits a users device.
		if (canFit(IAB_LEADERBOARD_WIDTH)) {
			placementWidth = IAB_LEADERBOARD_WIDTH;
		} else if (canFit(MED_BANNER_WIDTH)) {
			placementWidth = MED_BANNER_WIDTH;
		}

//		MMAdView adView = new MMAdView(this);
//		adView.setApid("148574");
//		MMRequest request = new MMRequest();
//		adView.setMMRequest(request);
//		adView.setId(MMSDK.getDefaultAdId());
//		adView.setWidth(placementWidth);
//		adView.setHeight(BANNER_AD_HEIGHT);

		LinearLayout layout = (LinearLayout) findViewById(R.id.linearLayout2);
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
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
	}

	protected void onStart() {
		mGoogleApiClient.connect();
		super.onStart();
	}

	protected void onStop() {
		mGoogleApiClient.disconnect();
		super.onStop();
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
			// TODO: Consider calling
			//    ActivityCompat#requestPermissions
			// here to request the missing permissions, and then overriding
			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
			//                                          int[] grantResults)
			// to handle the case where the user grants the permission. See the documentation
			// for ActivityCompat#requestPermissions for more details.
			return;
		}

		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//		if (mLastLocation != null) {
//			mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//			mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//		}
	}

	@Override
	public void onConnectionSuspended(int i) {
		mGoogleApiClient.disconnect();
	}

	@Override
	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
		mGoogleApiClient.disconnect();
	}
}



//class DownloadTask2 extends AsyncTask<String, Void, Object> implements Serializable{
//	
//	private static final long serialVersionUID = -2537374909989113250L;
//	String res = null;
//	private Context contexto;
//	
//	protected void onPostExecute(Object result){
//		PrincipalActivity.pd.dismiss();
//		Toast.makeText(contexto, "Clima: " + res, Toast.LENGTH_LONG).show();
//		super.onPostExecute(result);
//	}
//
//	@Override
//	protected Object doInBackground(String... params) {
//		res = WebServiceUtil.enviaDatosAlServidor();
//		return 1;
//	}
//	
//	public void setContexto(Context contexto){
//		this.contexto = contexto;
//	}
//	
//	public Context getContexto(){
//		return contexto;
//	}
//}
