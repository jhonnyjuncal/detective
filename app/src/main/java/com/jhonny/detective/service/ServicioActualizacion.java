package com.jhonny.detective.service;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.Manifest;
import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

import com.jhonny.detective.activity.PrincipalActivity;
import com.jhonny.detective.model.ObjetoPosicion;
import com.jhonny.detective.util.FileUtil;


public class ServicioActualizacion extends Service {

	private Timer timer = null;
	public static Activity ACTIVIDAD;
	private boolean primeraEjecucion = true;


	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}


	public static void establecerActividadPrincipal(Activity actividad) {
		ServicioActualizacion.ACTIVIDAD = actividad;
	}


	public void onCreate() {
		super.onCreate();

		// se inicia el servicio
		this.iniciarServicio();
	}


	public void iniciarServicio() {
		try {
			if (this.timer == null) {
				// Creamos el timer
				this.timer = new Timer();

				// Configuramos lo que tiene que hacer
				this.timer.scheduleAtFixedRate(new TimerTask() {
					public void run() {
						ejecutarTarea();
					}
				}, 0, 2 * (60 * 1000)); // minutos
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	public void finalizarServicio() {
		try {
			// Detenemos el timer
			this.timer.cancel();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}


	private void ejecutarTarea() {
		try {
			if (!primeraEjecucion) {
				ServicioActualizacion.ACTIVIDAD.runOnUiThread(new Runnable() {
					public void run() {
						if (!FileUtil.getLocationManagerGps().isProviderEnabled(LocationManager.GPS_PROVIDER)) {
							for (String prov : FileUtil.getLocationManagerGps().getAllProviders()) {
								if (ActivityCompat.checkSelfPermission(ServicioActualizacion.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
										&& ActivityCompat.checkSelfPermission(ServicioActualizacion.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
									// TODO: Consider calling
									//    ActivityCompat#requestPermissions
									// here to request the missing permissions, and then overriding
									//   public void onRequestPermissionsResult(int requestCode, String[] permissions,
									//                                          int[] grantResults)
									// to handle the case where the user grants the permission. See the documentation
									// for ActivityCompat#requestPermissions for more details.
									return;
								}
								Location location = FileUtil.getLocationManagerGps().getLastKnownLocation(prov);
								
								if(location != null){
									ObjetoPosicion pos = new ObjetoPosicion();
									
						    		pos.setFecha(new Date());
									pos.setLatitud(location.getLatitude());
						    		pos.setLongitud(location.getLongitude());
						    		
						    		PrincipalActivity pa = new PrincipalActivity();
						    		FileUtil.almacenaPosicionesAlFinalEnFichero(pos, pa.getApplicationContext());
						    		FileUtil.cargaPosicionesAlmacenadas(FileUtil.getLocalizador().contexto, 
						    				FileUtil.getLocalizador().view);
								}
							}
						}
					}
				});
			}else
				primeraEjecucion = false;
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
