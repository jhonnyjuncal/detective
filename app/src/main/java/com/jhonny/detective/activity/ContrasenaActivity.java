package com.jhonny.detective.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.jhonny.detective.Constantes;
import com.jhonny.detective.R;
import com.jhonny.detective.activity.custom.DrawerNavigationControl;
import com.jhonny.detective.util.FileUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class ContrasenaActivity extends DrawerNavigationControl {

	private int contSalida = 0;
	private View view;
	private Context context;

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
