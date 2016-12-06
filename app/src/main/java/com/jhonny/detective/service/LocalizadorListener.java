package com.jhonny.detective.service;

import java.util.Date;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.jhonny.detective.model.ObjetoPosicion;
import com.jhonny.detective.R;
import com.jhonny.detective.util.FileUtil;


public class LocalizadorListener implements LocationListener {
	
	public Context contexto;
	public View view;
	
	
	@Override
	public void onLocationChanged(Location location) {
		/* CUANDO LA POSICION GPS CAMBIA SEGUN EL CONSTRUCTOR DE DISTANCIA Y TIEMPO */
		ObjetoPosicion pos = new ObjetoPosicion();
		pos.setFecha(new Date());
		pos.setLatitud(location.getLatitude());
		pos.setLongitud(location.getLongitude());
		
		FileUtil.almacenaPosicionesAlFinalEnFichero(pos, contexto);
		FileUtil.cargaPosicionesAlmacenadas(contexto, view);
	}

	@Override
	public void onProviderDisabled(String arg0) {
		cambiaEstado(contexto.getResources().getString(R.string.txt_apagada));
	}

	@Override
	public void onProviderEnabled(String arg0) {
		cambiaEstado(contexto.getResources().getString(R.string.txt_encendida));
	}

	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		System.out.println("*** ha cambiado el estado ***");
	}

	private void cambiaEstado(String texto) {
		try {
			TextView tv2 = (TextView) view.findViewById(R.id.ppal_textView4);
			tv2.setText(texto);
			view.buildDrawingCache(true);
		}catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}
