package com.jhonny.detective.activity;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.Projection;
import com.jhonny.detective.model.ObjetoPosicion;
import com.jhonny.detective.R;
import com.jhonny.detective.Constantes;
import com.jhonny.detective.util.FileUtil;


public class MapaActivity extends Activity implements OnMapReadyCallback {
	
	private GoogleMap mapa;
	private Properties propiedades;
	private Context contexto;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mapa);
		contexto = this;

		try{
			MapFragment mapFragment = ((MapFragment) getFragmentManager().findFragmentById(R.id.map));
			mapFragment.getMapAsync(this);

			propiedades = FileUtil.getFicheroAssetConfiguracion(contexto);

		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_mapa, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.menuMapa_legal:
				String LicenseInfo = GooglePlayServicesUtil.getOpenSourceSoftwareLicenseInfo(getApplicationContext());
				AlertDialog.Builder LicenseDialog = new AlertDialog.Builder(MapaActivity.this);
				LicenseDialog.setTitle(getResources().getString(R.string.menu_legalnotices));
				LicenseDialog.setMessage(LicenseInfo);
				LicenseDialog.show();
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onMapReady(GoogleMap googleMap) {
		this.mapa = googleMap;

		try {
			cargaPosicionesAlmacenadasEnElMapa();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void cargaPosicionesAlmacenadasEnElMapa() throws IOException {
		String tipoCuenta = (String) propiedades.get(Constantes.PROP_TIPO_CUENTA);

		PolylineOptions linea = new PolylineOptions();
		linea.color(Color.RED);
		linea.width(1);

		List<ObjetoPosicion> listaPosiciones = FileUtil.getListaAssetPosiciones(contexto, Integer.parseInt(tipoCuenta));
		for(int i=0; i<listaPosiciones.size(); i++){
			ObjetoPosicion obj = listaPosiciones.get(i);
			if(i == 0)
				agregarMarcador(obj.getLatitud(), obj.getLongitud(), obj.getFecha(), true);
			else
				agregarMarcador(obj.getLatitud(), obj.getLongitud(), obj.getFecha(), false);
			linea.add(new LatLng(obj.getLatitud(), obj.getLongitud()));
		}

		if(mapa != null){
			mapa.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			mapa.addPolyline(linea);

			// listener para el evento onclick de los marcadores
			mapa.setOnMarkerClickListener(new OnMarkerClickListener() {
				public boolean onMarkerClick(Marker marker) {
					marker.showInfoWindow();
					return true;
				}
			});

			mapa.setOnMapLongClickListener(new OnMapLongClickListener() {
				public void onMapLongClick(LatLng point) {
					Projection proj = mapa.getProjection();
					Point coord = proj.toScreenLocation(point);

					Toast.makeText(MapaActivity.this,
							getResources().getString(R.string.label_latitud) + " " + point.latitude + "\n" +
									getResources().getString(R.string.label_longitud) + " " + point.longitude + "\n" +
									"X: " + coord.x + "\n" + "Y: " + coord.y, Toast.LENGTH_LONG).show();
				}
			});

			// nivel del zoom
			if(listaPosiciones != null && listaPosiciones.size() > 0){
				MapsInitializer.initialize(this);
				LatLng ubicacion = new LatLng(listaPosiciones.get(0).getLatitud(), listaPosiciones.get(0).getLongitud());

				CameraPosition camPos = new CameraPosition.Builder()
						.target(ubicacion)	//Centramos el mapa en Madrid
						.zoom(19)			//Establecemos el zoom en 19
						.bearing(45)		//Establecemos la orientación con el noreste arriba
						.tilt(70)			//Bajamos el punto de vista de la cámara 70 grados
						.build();

				CameraUpdate cu = CameraUpdateFactory.newCameraPosition(camPos);
				mapa.animateCamera(cu);
			}
		}
	}

	private void agregarMarcador(double latitud, double longitud, Date fecha, boolean primero){
		try{
			if(mapa != null){
				Locale locale = getResources().getConfiguration().locale;
				MarkerOptions marker = new MarkerOptions();
				marker.position(new LatLng(latitud, longitud));
				marker.title(getResources().getString(R.string.label_fecha) + " "
								+ FileUtil.getFechaFormateada(fecha, locale));
				marker.snippet(getResources().getString(R.string.label_hora) + " "
								+ FileUtil.getHoraFormateada(fecha, locale));
				if (primero) {
					marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
				} else {
					marker.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
				}
				mapa.addMarker(marker);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
