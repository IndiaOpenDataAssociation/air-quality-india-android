package com.idn0phl3108ed43d22s30.adapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.idn0phl3108ed43d22s30.R;
import com.idn0phl3108ed43d22s30.etc.Constants;

/**
 * Created by Rutul on 30-06-2016.
 */
public class CommunityDevicesAdapter extends ArrayAdapter<String> {


    private final Activity context;
    private final String[] device;
    private final Integer[] imageId;
    private final String[] description;

    public CommunityDevicesAdapter(Activity context,
                                   String[] device, Integer[] imageId, String[] description) {
        super(context, R.layout.fragment_device_list_row, device);
        this.context = context;
        this.device = device;
        this.description = description;
        this.imageId = imageId;

    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_device_list_row, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txtDeviceName);
        TextView txtDesc = (TextView) rowView.findViewById(R.id.txtDeviceDescription);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.imgDeviceImg);
        txtTitle.setText(device[position]);
        txtDesc.setText(description[position]);
        imageView.setImageResource(imageId[position]);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (position){
                    case 0:
                        openAirowlWebPage();
                        break;
                    default:
                        break;
                }
            }
        });
        return rowView;
    }

    private void openAirowlWebPage(){
        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.AIROWL_WEB_URL));
        context.startActivity(browserIntent);
    }

}
