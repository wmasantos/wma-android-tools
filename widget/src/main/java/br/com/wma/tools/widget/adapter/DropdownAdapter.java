package br.com.wma.tools.widget.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import br.com.wma.tools.widget.R;
import br.com.wma.tools.widget.entity.DropdownEntity;

public class DropdownAdapter extends BaseAdapter {
    private Activity act;
    private List<DropdownEntity> dropdownEntities;

    public DropdownAdapter(Activity act, List<DropdownEntity> dropdownEntities) {
        this.act = act;
        this.dropdownEntities = dropdownEntities;
    }

    @Override
    public int getCount() {
        return dropdownEntities.size();
    }

    @Override
    public Object getItem(int position) {
        return dropdownEntities.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        view = act.getLayoutInflater().inflate(R.layout.layout_line_dropdown, null);

        TextView tvDropdownText = view.findViewById(R.id.tvDropdownText);

        tvDropdownText.setText(dropdownEntities.get(position).getDescription());

        return view;
    }
}
