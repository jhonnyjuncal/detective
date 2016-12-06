package com.jhonny.detective.activity;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import com.jhonny.detective.R;
import com.jhonny.detective.activity.custom.DrawerNavigationControl;
import com.jhonny.detective.util.FileUtil;

public class BorrarPosicionesActivity extends DrawerNavigationControl {
	
	private int contSalida = 0;
	private View view;
	private Context context;
	
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
