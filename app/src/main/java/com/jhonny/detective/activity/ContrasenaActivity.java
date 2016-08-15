package com.jhonny.detective.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.jhonny.detective.util.FileUtil;
import com.jhonny.detective.R;
import com.jhonny.detective.Constantes;
import android.support.v7.app.AppCompatActivity;


public class ContrasenaActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	
	private int contSalida = 0;
	private View view;
	private Context context;
	
	//Constants for tablet sized ads (728x90)
	private static final int IAB_LEADERBOARD_WIDTH = 728;
	private static final int MED_BANNER_WIDTH = 480;
	//Constants for phone sized ads (320x50)
	private static final int BANNER_AD_WIDTH = 320;
	private static final int BANNER_AD_HEIGHT = 50;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contrasena);
		contSalida = 0;
		
		try{
			this.context = this;
			this.view = getWindow().getDecorView();

			// barra de herramientas
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);

			DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
			ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
			drawer.setDrawerListener(toggle);
			toggle.syncState();

			NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
			navigationView.setNavigationItemSelectedListener(this);
			
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.menu.menu_contrasena, menu);
		return true;
	}
	
	@Override
    protected void onResume(){
		super.onResume();
		contSalida = 0;
//		reiniciarFondoOpciones();
		cargaConfiguracionGlobal();
		cargaPublicidad();
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if(keyCode == KeyEvent.KEYCODE_BACK) {
    		if(contSalida == 0){
    			contSalida++;
    			Toast.makeText(this, getResources().getString(R.string.txt_salir_1_aviso), Toast.LENGTH_SHORT).show();
    			return true;
    		}else{
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
	
	public void cambiarContrasena(View view){
		String passAlmacenado;
		
		try{
			EditText passViejo = (EditText) findViewById(R.id.editText1);
			EditText passNuevo1 = (EditText) findViewById(R.id.editText2);
			EditText passNuevo2 = (EditText) findViewById(R.id.editText3);
			
			Properties prop = FileUtil.getFicheroAssetConfiguracion(this);
			
			if(passViejo == null || passViejo.getText().toString().equals("")){
				Toast.makeText(this, getResources().getString(R.string.txt_passViejo_incorrecto), Toast.LENGTH_LONG).show();
				return;
			}
			if(passNuevo1 == null || passNuevo1.getText().toString().equals("")){
				Toast.makeText(this, getResources().getString(R.string.txt_passNuevo_incorrecto), Toast.LENGTH_LONG).show();
				return;
			}
			if(passNuevo2 == null || passNuevo2.getText().toString().equals("")){
				Toast.makeText(this, getResources().getString(R.string.txt_passNuevo_incorrecto), Toast.LENGTH_LONG).show();
				return;
			}
			
			if(prop != null){
				passAlmacenado = (String)prop.get(Constantes.PROP_PASSWORD);
				
				if(passAlmacenado.toString().equals(passViejo.getText().toString())){
					if(passNuevo1.getText().toString().equals(passNuevo2.getText().toString())){
						Map<String, String> valores = new HashMap<String, String>();
						
						valores.put(Constantes.PROP_PASSWORD, passNuevo1.getText().toString());
						valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, (String)prop.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES));
						valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, (String)prop.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES));
						valores.put(Constantes.PROP_TIPO_CUENTA, (String)prop.get(Constantes.PROP_TIPO_CUENTA));
						valores.put(Constantes.PROP_FONDO_PANTALLA, (String)prop.get(Constantes.PROP_FONDO_PANTALLA));
						valores.put(Constantes.PROP_EMAIL, (String)prop.get(Constantes.PROP_EMAIL));
						valores.put(Constantes.PROP_EMAIL_ENVIO, (String)prop.get(Constantes.PROP_EMAIL_ENVIO));
						valores.put(Constantes.PROP_EMAIL_CHECK, (String)prop.get(Constantes.PROP_EMAIL_CHECK));
						
						FileUtil.guardaDatosConfiguracion(valores, this);
						
						Toast.makeText(this, getResources().getString(R.string.txt_cambio_pass_ok), Toast.LENGTH_LONG).show();
						
						passViejo.setText("");
						passNuevo1.setText("");
						passNuevo2.setText("");
					}else{
						Toast.makeText(this, getResources().getString(R.string.txt_nuevos_pass_incorrectos), Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(this, getResources().getString(R.string.txt_datos_incorrectos), Toast.LENGTH_LONG).show();
				}
			}
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
					ImageView imageView = (ImageView)findViewById(R.id.fondo_contrasena);
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
		
//		MMAdView adView1 = new MMAdView(this);
//		adView1.setApid("148574");
//		MMRequest request1 = new MMRequest();
//		adView1.setMMRequest(request1);
//		adView1.setId(MMSDK.getDefaultAdId());
//		adView1.setWidth(placementWidth);
//		adView1.setHeight(BANNER_AD_HEIGHT);

		LinearLayout layout2 = (LinearLayout)findViewById(R.id.linearLayout2);
		layout2.removeAllViews();
//		layout2.addView(adView1);
//		adView1.getAd();
		
//		MMAdView adView2 = new MMAdView(this);
//		adView2.setApid("148574");
//		MMRequest request = new MMRequest();
//		adView2.setMMRequest(request);
//		adView2.setId(MMSDK.getDefaultAdId());
//		adView2.setWidth(placementWidth);
//		adView2.setHeight(BANNER_AD_HEIGHT);

		LinearLayout layout3 = (LinearLayout)findViewById(R.id.linearLayout3);
		layout3.removeAllViews();
//		layout3.addView(adView2);
//		adView2.getAd();
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
