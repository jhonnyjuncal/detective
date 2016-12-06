package com.jhonny.detective.activity;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.jhonny.detective.R;
import com.jhonny.detective.Constantes;
import com.jhonny.detective.activity.custom.DrawerNavigationControl;
import com.jhonny.detective.util.FileUtil;

public class InicioActivity extends DrawerNavigationControl {

	private String PASS;
	private String DIST_MIN_ACTUALIZACIONES;
	private String TMP_MIN_ACTUALIZACIONES;
	private String TIPO_CUENTA;
	private String FONDO_PANTALLA;
	private String EMAIL;
	private String EMAIL_ENVIO;
	private String EMAIL_CHECK;
	private int contSalida = 0;
	private View view;
	private Context context;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_inicio);
    	contSalida = 0;
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
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
		return true;
    }

    @Override
    protected void onResume(){
    	super.onResume();
    	contSalida = 0;
    	
    	try{
        	EditText et = (EditText)findViewById(R.id.editText1);
        	et.setText("");
        	
        }catch(Exception ex){
        	ex.printStackTrace();
        }
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

    public void accesoUsuarioRegistrado(View view){
    	Properties prop = new Properties();
    	Context ctx = this;
    	
    	try{
    		EditText passUsuario = (EditText)findViewById(R.id.editText1);
    		prop = FileUtil.getFicheroAssetConfiguracion(ctx);
    		
    		if(prop != null){
    			// carga los datos de la configuracion guardados
    			if(prop.containsKey(Constantes.PROP_PASSWORD))
    				PASS = (String)prop.get(Constantes.PROP_PASSWORD);
    			if(prop.containsKey(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES))
    				DIST_MIN_ACTUALIZACIONES = (String)prop.get(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES);
    			if(prop.containsKey(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES))
    				TMP_MIN_ACTUALIZACIONES = (String)prop.get(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES);
    			if(prop.containsKey(Constantes.PROP_TIPO_CUENTA))
    				TIPO_CUENTA = (String)prop.get(Constantes.PROP_TIPO_CUENTA);
    			if(prop.containsKey(Constantes.PROP_FONDO_PANTALLA))
    				FONDO_PANTALLA = (String)prop.get(Constantes.PROP_FONDO_PANTALLA);
    			if(prop.containsKey(Constantes.PROP_EMAIL))
    				EMAIL = (String)prop.get(Constantes.PROP_EMAIL);
    			if(prop.containsKey(Constantes.PROP_EMAIL_ENVIO))
    				EMAIL_ENVIO = (String)prop.get(Constantes.PROP_EMAIL_ENVIO);
    			if(prop.containsKey(Constantes.PROP_EMAIL_CHECK))
    				EMAIL_CHECK = (String)prop.get(Constantes.PROP_EMAIL_CHECK);
    		}
    		
    		if(passUsuario == null || passUsuario.length() <= 0){
    			String text = getResources().getString(R.string.txt_debe_introducir);
    			Toast.makeText(InicioActivity.this, text, Toast.LENGTH_LONG).show();
    		}else{
    			Map <String, String> valores = new HashMap<String, String>();
    			
				if(PASS == null || PASS.toString().equals("null")){
					// contraseña sin establecer
					PASS = passUsuario.getText().toString();
					
					valores.put(Constantes.PROP_PASSWORD, PASS);
					valores.put(Constantes.PROP_DISTANCIA_MINIMA_ACTUALIZACIONES, DIST_MIN_ACTUALIZACIONES);
					valores.put(Constantes.PROP_TIEMPO_MINIMO_ACTUALIZACIONES, TMP_MIN_ACTUALIZACIONES);
					valores.put(Constantes.PROP_TIPO_CUENTA, TIPO_CUENTA);
					valores.put(Constantes.PROP_FONDO_PANTALLA, FONDO_PANTALLA);
					valores.put(Constantes.PROP_EMAIL, EMAIL);
					valores.put(Constantes.PROP_EMAIL_ENVIO, EMAIL_ENVIO);
					valores.put(Constantes.PROP_EMAIL_CHECK, EMAIL_CHECK);
					
					FileUtil.guardaDatosConfiguracion(valores, InicioActivity.this);
				}
				
				if(passUsuario.getText().toString().equals(PASS)){
					// contraseña corrrecta
					Intent intent = new Intent(this, PrincipalActivity.class);
					startActivity(intent);
				}else{
					// contraseña incorrecta
					String text = getResources().getString(R.string.txt_datos_incorrectos);
					Toast.makeText(InicioActivity.this, text, Toast.LENGTH_LONG).show();
					passUsuario.setText("");
				}
			}
    	}catch(Exception ex){
    		ex.printStackTrace();
    	}
    }

	public void changeTransformationMethod(View vista) {
		EditText et = (EditText)findViewById(R.id.editText1);
		if(et.getTransformationMethod() == null){
			et.setTransformationMethod(new PasswordTransformationMethod());
		}else {
			et.setTransformationMethod(null);
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
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
	}
}
