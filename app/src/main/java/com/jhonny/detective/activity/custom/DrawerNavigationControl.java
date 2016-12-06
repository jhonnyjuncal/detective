package com.jhonny.detective.activity.custom;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.jhonny.detective.R;
import com.jhonny.detective.activity.AcercaActivity;
import com.jhonny.detective.activity.BorrarPosicionesActivity;
import com.jhonny.detective.activity.ConfiguracionActivity;
import com.jhonny.detective.activity.ContrasenaActivity;
import com.jhonny.detective.activity.InicioActivity;
import com.jhonny.detective.activity.MapaActivity;
import com.jhonny.detective.activity.PosicionesActivity;
import com.jhonny.detective.activity.PrincipalActivity;

public class DrawerNavigationControl extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawerLayout;

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        Intent intent = null;

        switch(id) {
            case R.id.nav_principal:
                intent = new Intent(this, PrincipalActivity.class);
                break;
            case R.id.nav_mapa:
                intent = new Intent(this, MapaActivity.class);
                break;
            case R.id.nav_posiciones:
                intent = new Intent(this, PosicionesActivity.class);
                break;
            case R.id.nav_settings:
                intent = new Intent(this, ConfiguracionActivity.class);
                break;
            case R.id.nav_password:
                intent = new Intent(this, ContrasenaActivity.class);
                break;
            case R.id.nav_borrar_coordenadas:
                intent = new Intent(this, BorrarPosicionesActivity.class);
                break;
            case R.id.nav_acerca:
                intent = new Intent(this, AcercaActivity.class);
                break;
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerLayout.closeDrawer(GravityCompat.START);

        startActivity(intent);
        return true;
    }
}
