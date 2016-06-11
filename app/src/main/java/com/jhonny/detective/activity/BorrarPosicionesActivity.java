package com.jhonny.detective.activity;

import com.jhonny.detective.Constantes;
import com.jhonny.detective.util.FileUtil;
import com.jhonny.detective.R;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;


public class BorrarPosicionesActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	
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
		setContentView(R.layout.activity_borrar_posiciones);
		contSalida = 0;
		
		try{
			this.context = this;
			this.view = getWindow().getDecorView();

			// barra de herramientas
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			
	        actualizaCantidadDeCoordenadas();
	        
	        int placementWidth = BANNER_AD_WIDTH;

			//Finds an ad that best fits a users device.
			if(canFit(IAB_LEADERBOARD_WIDTH)) {
			    placementWidth = IAB_LEADERBOARD_WIDTH;
			}else if(canFit(MED_BANNER_WIDTH)) {
			    placementWidth = MED_BANNER_WIDTH;
			}
			
//			MMAdView adView = new MMAdView(this);
//			adView.setApid("148574");
//			MMRequest request = new MMRequest();
//			adView.setMMRequest(request);
//			adView.setId(MMSDK.getDefaultAdId());
//			adView.setWidth(placementWidth);
//			adView.setHeight(BANNER_AD_HEIGHT);

			LinearLayout layout = (LinearLayout)findViewById(R.id.linearLayout2);
			//Add the adView to the layout. The layout is assumed to be a RelativeLayout.
//			layout.addView(adView);
//			adView.getAd();

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
	
	private void actualizaCantidadDeCoordenadas(){
		try{
			TextView tNumero = (TextView)findViewById(R.id.borr_textView3);
	        if(PrincipalActivity.listaPosiciones == null){
	        	tNumero.setText("0");
	        }else{
	        	tNumero.setText(String.valueOf(PrincipalActivity.listaPosiciones.size()));
	        }
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		MenuInflater inflater = getSupportMenuInflater();
//		inflater.inflate(R.menu.menu_borrar_posiciones, menu);
		return true;
	}
	
	@Override
	public void onResume(){
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
	
	public void borraCoordenadasActuales(View view){
		try{
			FileUtil.borraFicheroActualDePosiciones(this);
			FileUtil.cargaPosicionesAlmacenadas((Context)this, getWindow().getDecorView());
			Toast.makeText(this, getResources().getString(R.string.txt_coordenadas_borradas_ok), Toast.LENGTH_LONG).show();
			
			actualizaCantidadDeCoordenadas();
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
							imagen, "drawable", this.view.getContext().getApplicationContext().getPackageName());
					Drawable image = this.view.getContext().getResources().getDrawable(imageResource1);
					ImageView imageView = (ImageView)findViewById(R.id.fondo_borrar);
					imageView.setImageDrawable(image);
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
//		// Handle action bar item clicks here. The action bar will
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
		} else if (id == R.id.nav_cambiar_contrasena) {
			intent = new Intent(this, ContrasenaActivity.class);
		} else if (id == R.id.nav_borrar_coordenadas) {
			intent = new Intent(this, BorrarPosicionesActivity.class);
		} else if (id == R.id.nav_compartir) {
//			intent = new Intent(this, EnConstruccion.class);
		} else if (id == R.id.nav_send) {
//			intent = new Intent(this, EnConstruccion.class);
		} else if (id == R.id.nav_settings) {
			intent = new Intent(this, ConfiguracionActivity.class);
		} else if (id == R.id.nav_desarrollador) {
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
