package com.idn0phl3108ed43d22s30.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.idn0phl3108ed43d22s30.R;
import com.github.johnkil.print.PrintView;
import com.unnamed.b.atv.model.TreeNode;

/**
 * Created by Bogdan Melnychuk on 2/12/15.
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {
    private TextView tvValue;
    private PrintView arrowView;

    public IconTreeItemHolder(Context context) {
        super(context);
    }

    @SuppressLint("NewApi")
    @Override
    public View createNodeView(final TreeNode node, IconTreeItem value) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.layout_icon_node, null, false);
        tvValue = (TextView) view.findViewById(R.id.node_value);
        tvValue.setText(value.text);

        final ImageView iconView = (ImageView) view.findViewById(R.id.icon);
        if(value.icon!=R.drawable.blank){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                iconView.setImageResource(value.icon);
            }
        }

        arrowView = (PrintView) view.findViewById(R.id.arrow_icon);
        if(value.drawable == R.drawable.blank){

        } else {
            //tvValue.setCompoundDrawablesWithIntrinsicBounds(value.drawable, 0,0,0);
        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem {
        public int icon;
        public String text;
        public int drawable;

        public IconTreeItem(int icon, String text, int drawable) {
            this.icon = icon;
            this.text = text;
            this.drawable = drawable;
        }

        public IconTreeItem(int icon, String text){
            this.icon = icon;
            this.text = text;
            this.drawable = R.drawable.blank;
        }
    }
}
