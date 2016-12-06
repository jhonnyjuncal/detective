package com.jhonny.detective.activity;

import java.util.Locale;
import org.joda.time.DateTime;

import com.jhonny.detective.activity.custom.DrawerNavigationControl;
import com.jhonny.detective.util.FileUtil;
import com.jhonny.detective.R;
import android.net.Uri;
import android.os.Bundle;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AcercaActivity extends DrawerNavigationControl {
	
	private int contSalida = 0;
	private View view;
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_acerca);
		contSalida = 0;
		
		try{
			this.context = this;
			this.view = getWindow().getDecorView();

			// barra de herramientas
			Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
			setSupportActionBar(toolbar);

			// version de la aplicacion
			PackageInfo pInfo = getApplicationContext().getPackageManager().getPackageInfo(getPackageName(), 0);
			TextView textoVersion = (TextView)findViewById(R.id.acer_textView3);
			textoVersion.setText(pInfo.versionName);
			
			// fecha de creacion
			TextView textoFecha = (TextView)findViewById(R.id.acer_textView5);
			DateTime fecha = new DateTime("2013-11-04");
			Locale locale = getResources().getConfiguration().locale;
			textoFecha.setText(FileUtil.getFechaFormateada(fecha.toDate(), locale));
			
			// link de mi perfil en google+
			TextView link = (TextView)findViewById(R.id.acer_textView7);
			link.setMovementMethod(LinkMovementMethod.getInstance());
			
			// link de facebook
			ImageView imgFacebook = (ImageView)findViewById(R.id.acer_imageView2);
			imgFacebook.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
			        intent.setAction(Intent.ACTION_VIEW);
			        intent.addCategory(Intent.CATEGORY_BROWSABLE);
			        intent.setData(Uri.parse("https://www.face book.com/androiddetective"));
			        startActivity(intent);
				}
			});
			
			// link de google plus
			ImageView imgGoogle = (ImageView)findViewById(R.id.acer_imageView3);
			imgGoogle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
			        intent.setAction(Intent.ACTION_VIEW);
			        intent.addCategory(Intent.CATEGORY_BROWSABLE);
			        intent.setData(Uri.parse("https://plus.google.com/103927277529482513436"));
			        startActivity(intent);
				}
			});

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
//		inflater.inflate(R.menu.menu_acerca, menu);
		return true;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		contSalida = 0;
		this.context = this;
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
