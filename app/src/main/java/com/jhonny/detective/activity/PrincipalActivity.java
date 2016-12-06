package com.jhonny.detective.activity;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.jhonny.detective.Constantes;
import com.jhonny.detective.R;
import com.jhonny.detective.activity.custom.DrawerNavigationControl;
import com.jhonny.detective.model.Email;
import com.jhonny.detective.model.ObjetoPosicion;
import com.jhonny.detective.service.LocalizadorListener;
import com.jhonny.detective.service.ServicioActualizacion;
import com.jhonny.detective.util.FileUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PrincipalActivity extends DrawerNavigationControl {

	protected static String PASS;
	protected static float DISTANCIA_MINIMA_PARA_ACTUALIZACIONES;
	protected static long TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES;
	protected static Integer TIPO_CUENTA;
	protected static final String FICHERO_CONFIGURACION = "config.properties";

	public static List<ObjetoPosicion> listaPosiciones = null;
	public static View viewPrincipal = null;
	public static Resources resourcesPrincipal = null;
	public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;

	private int contSalida = 0;
	private View view;
	private Context context;
	private Dialog dialogo;
	private EditText email;
	private CheckBox check;
	private Properties prop;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_principal);
		contSalida = 0;

		try {
			this.context = this;
			this.view = getWindow().getDecorView();

			// barra de herramientas
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);

			// se inicia el servicio de actualizacion de coordenadas
//			iniciaServicio();

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
			FileUtil.cargaPosicionesAlmacenadas((Context) this, getWindow().getDecorView());
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

	private void informaEstadoActualGPS() {
		try {
			seleccionarOrigenDeLocalizacion();
			boolean enabled = false;
			LocationManager loc = FileUtil.getLocationManagerGps();

			if (loc != null)
				enabled = loc.isProviderEnabled(LocationManager.GPS_PROVIDER);

			TextView gpsStatus = (TextView) findViewById(R.id.ppal_textView4);
			if (enabled == false)
				gpsStatus.setText(getResources().getString(R.string.txt_apagada));
			else
				gpsStatus.setText(getResources().getString(R.string.txt_encendida));

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	private void seleccionarOrigenDeLocalizacion() {
		LocalizadorListener localizador = null;

		try {
			// listener del GPS
			LocationManager locationGps = FileUtil.getLocationManagerGps();
			LocationManager locationInternet = FileUtil.getLocationManagerInternet();

			if (locationGps == null) {
				localizador = FileUtil.getLocalizador();
				if (localizador == null) {
					localizador = new LocalizadorListener();
					localizador.view = getWindow().getDecorView();
					localizador.contexto = (Context) this;
					FileUtil.setLocalizador(localizador);
				}
				locationGps = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			}
			if (locationInternet == null) {
				localizador = FileUtil.getLocalizador();
				if (localizador == null) {
					localizador = new LocalizadorListener();
					localizador.view = getWindow().getDecorView();
					localizador.contexto = (Context) this;
					FileUtil.setLocalizador(localizador);
				}
				locationInternet = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
			}

			if (locationGps != null) {
				if (locationGps.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
					int permiso1 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
					int permiso2 = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION);

					if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
						ActivityCompat.requestPermissions(this,
								new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
					} else {
						ActivityCompat.requestPermissions(this,
								new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSIONS_REQUEST_LOCATION);
					}

//					if (permiso1 != PackageManager.PERMISSION_GRANTED && permiso2 != PackageManager.PERMISSION_GRANTED) {
//						return;
//					}

					locationGps.requestLocationUpdates(LocationManager.GPS_PROVIDER,
							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES,
							localizador);
				}
				FileUtil.setLocationManagerGps(locationGps);
			}

			if (locationInternet != null) {
				if (locationInternet.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
					locationInternet.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
							TIEMPO_MINIMO_ENTRE_ACTUALIZACIONES, DISTANCIA_MINIMA_PARA_ACTUALIZACIONES,
							localizador);
				}
				FileUtil.setLocationManagerGps(locationGps);
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

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
			TextView textoWifi = (TextView) findViewById(R.id.ppal_textView6);

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
    		ServicioActualizacion.establecerActividadPrincipal(this);
    		Intent servicio = new Intent(this, ServicioActualizacion.class);
//    		
//    		// se ejecuta el servicio
    		startService(servicio);
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

	@Override
	public void onBackPressed() {
		DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		if (drawer.isDrawerOpen(GravityCompat.START)) {
			drawer.closeDrawer(GravityCompat.START);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
	}

//	protected void onStart() {
//		mGoogleApiClient.connect();
//		super.onStart();
//	}

//	protected void onStop() {
//		mGoogleApiClient.disconnect();
//		super.onStop();
//	}

//	@Override
//	public void onConnected(Bundle connectionHint) {
//		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//			// TODO: Consider calling
//			//    ActivityCompat#requestPermissions
//			// here to request the missing permissions, and then overriding
//			//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//			//                                          int[] grantResults)
//			// to handle the case where the user grants the permission. See the documentation
//			// for ActivityCompat#requestPermissions for more details.
//			return;
//		}
//
//		mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
//		if (mLastLocation != null) {
//			mLatitudeText.setText(String.valueOf(mLastLocation.getLatitude()));
//			mLongitudeText.setText(String.valueOf(mLastLocation.getLongitude()));
//		}
//	}

//	@Override
//	public void onConnectionSuspended(int i) {
//		mGoogleApiClient.disconnect();
//	}

//	@Override
//	public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
//		mGoogleApiClient.disconnect();
//	}
}



//class DownloadTask2 extends AsyncTask<String, Void, Object> implements Serializable{
//	
//	private static final long serialVersionUID = -2537374909989113250L;
//	String res = null;
//	private Context contexto;
//	
//	protected void onPostExecute(Object result){
//		PrincipalControlDrawer.pd.dismiss();
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
