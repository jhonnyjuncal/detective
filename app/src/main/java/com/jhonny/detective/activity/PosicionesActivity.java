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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.jhonny.detective.Constantes;
import com.jhonny.detective.R;
import com.jhonny.detective.activity.custom.CustomAdapterListView;
import com.jhonny.detective.activity.custom.DrawerNavigationControl;
import com.jhonny.detective.model.ObjetoPosicion;
import com.jhonny.detective.util.FileUtil;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;

public class PosicionesActivity extends DrawerNavigationControl implements OnItemClickListener {

	private List<ObjetoPosicion> listaPosiciones = null;
	private ListView listView = null;
	private int contSalida = 0;
	private View view;
	private Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_posiciones);
		contSalida = 0;
		
		try{
			this.context = this;
			this.view = getWindow().getDecorView();

			// barra de herramientas
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);
			
			cargaPosicionesAlmacenadas();
			
			listView = (ListView)findViewById(R.id.listView1);
			
			if(listaPosiciones != null && listaPosiciones.size() > 0){
				List<Object[]> lista = new ArrayList<>();
				
				for(ObjetoPosicion op : listaPosiciones){
					Object[] objeto = new Object[4];
					DateFormat dateFormatter = DateFormat.getDateInstance(DateFormat.SHORT, getResources().getConfiguration().locale);
					DateFormat timeFormatter = DateFormat.getTimeInstance(DateFormat.SHORT, getResources().getConfiguration().locale);
					objeto[0] = timeFormatter.format(op.getFecha());
					objeto[1] = dateFormatter.format(op.getFecha());
					objeto[2] = op.getLatitud();
					objeto[3] = op.getLongitud();
					lista.add(objeto);
				}
				
				CustomAdapterListView adapter = new CustomAdapterListView(this.context, lista);
				listView.setAdapter(adapter);
				listView.setOnItemClickListener(this);
			}

			DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
			ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
					R.string.navigation_drawer_open,
					R.string.navigation_drawer_close);
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
//		inflater.inflate(R.menu.menu_posiciones, menu);
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
	
	public void cargaPosicionesAlmacenadas(){
    	try{
    		// lectura del fichero de configuracion
    		Context ctx = this;
    		Properties prefs = FileUtil.getFicheroAssetConfiguracion(ctx);
    		String tipoCuenta = (String)prefs.get(Constantes.PROP_TIPO_CUENTA);
			listaPosiciones = FileUtil.getListaAssetPosiciones(ctx, Integer.parseInt(tipoCuenta));
		}catch(IOException e){
			e.printStackTrace();
		}catch(Exception ex){
			ex.printStackTrace();
		}
    }
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
		try{
			ObjetoPosicion op = listaPosiciones.get(pos);
			Locale locale = getResources().getConfiguration().locale;

			TextView textLatitud = (TextView)findViewById(R.id.textView3);
			TextView textLongitud = (TextView)findViewById(R.id.textView4);
			TextView textFecha = (TextView)findViewById(R.id.textView5);
			TextView textHora = (TextView)findViewById(R.id.textView6);
			
			textLatitud.setText(String.valueOf(op.getLatitud()));
			textLongitud.setText(String.valueOf(op.getLongitud()));
			textFecha.setText(FileUtil.getFechaFormateada(op.getFecha(), locale));
			textHora.setText(FileUtil.getHoraFormateada(op.getFecha(), locale));
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

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle icicle) {
		super.onSaveInstanceState(icicle);
	}
}
