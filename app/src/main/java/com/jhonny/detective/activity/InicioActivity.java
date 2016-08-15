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
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.TransformationMethod;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.support.v7.app.AppCompatActivity;

import com.jhonny.detective.R;
import com.jhonny.detective.Constantes;
import com.jhonny.detective.util.FileUtil;


public class InicioActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
	
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
        	
        	cargaConfiguracionGlobal();
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
    
	private void cargaConfiguracionGlobal(){
		try{
			if(this.view != null){
				String fondo = FileUtil.getFondoPantallaAlmacenado(this.context);
				if(fondo != null){
					String imagen = Constantes.mapaFondo.get(Integer.parseInt(fondo));
					int imageResource1 = this.view.getContext().getApplicationContext().getResources().getIdentifier(
							imagen, "mipmap", this.view.getContext().getApplicationContext().getPackageName());
					Drawable image = this.view.getContext().getResources().getDrawable(imageResource1);
					ImageView imageView = (ImageView)findViewById(R.id.fondo_inicio);
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
