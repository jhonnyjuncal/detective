package com.jhonny.detective.activity.custom;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.jhonny.detective.R;

import java.util.List;

public class CustomAdapterListView extends BaseAdapter {

    private Context context;
    private List<Object[]> items;

    public CustomAdapterListView(Context context, List<Object[]> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return this.items.size();
    }

    @Override
    public Object getItem(int position) {
        return this.items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            rowView = inflater.inflate(R.layout.content_posiciones_item_listview, parent, false);
        }

        TextView textViewHora = (TextView)rowView.findViewById(R.id.item_textHora);
        TextView textViewFecha = (TextView)rowView.findViewById(R.id.item_textFecha);
        TextView textViewLatitud = (TextView)rowView.findViewById(R.id.item_textLatitud);
        TextView textViewLongitud = (TextView)rowView.findViewById(R.id.item_textLongitud);

        Object[] item = this.items.get(position);
        textViewHora.setText(item[0].toString());
        textViewFecha.setText(item[1].toString());
        textViewLatitud.setText(item[2].toString());
        textViewLongitud.setText(item[3].toString());

        return rowView;
    }
}
