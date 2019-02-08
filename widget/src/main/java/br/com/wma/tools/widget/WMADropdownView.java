package br.com.wma.tools.widget;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import java.util.List;

import br.com.wma.tools.widget.adapter.DropdownAdapter;
import br.com.wma.tools.widget.entity.DropdownEntity;
import br.com.wma.tools.widget.interfaces.OnDropdownClick;

public class WMADropdownView extends LinearLayout {

    private PopupWindow popupMenu;
    private TextView tvDropdown;
    private DropdownAdapter adapter;
    private List<DropdownEntity> dropdownEntities;
    private ListView lvDropdown;
    private LinearLayout llDropdownLayout, llContainerListview;
    private Activity activity;
    private OnDropdownClick onDropdownClick;

    public WMADropdownView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initComponents(context, attrs);
    }

    private void initComponents(Context context, AttributeSet attrs) {
        inflate(context, R.layout.layout_dropdown, this);

        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.Dropdown, 0, 0);

        a.recycle();

        llDropdownLayout = findViewById(R.id.llDropdownLayout);
        tvDropdown = findViewById(R.id.tvDropdown);
    }

    public void prepareDropdownComponent(Activity activity, List<DropdownEntity> dropdownEntities, OnDropdownClick onDropdownClick){
        this.activity = activity;
        this.dropdownEntities = dropdownEntities;
        this.adapter = new DropdownAdapter(activity, dropdownEntities);
        this.onDropdownClick = onDropdownClick;

        loadPopupMenu(this.activity);

        llDropdownLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu.showAsDropDown(llDropdownLayout, 0, -40);
            }
        });
    }

    private void loadPopupMenu(Activity act){
        View view = act.getLayoutInflater().inflate(R.layout.layout_list_dropdown, null);
        lvDropdown = view.findViewById(R.id.lvDropdown);
        llContainerListview = view.findViewById(R.id.llContainerListview);
        lvDropdown.setAdapter(adapter);

        if(!dropdownEntities.isEmpty()){
            dropdownEntities.get(0).setSelected(true);
            tvDropdown.setText(dropdownEntities.get(0).getDescription());
        }

        popupMenu = new PopupWindow(llDropdownLayout.getContext());
        popupMenu.setContentView(view);
        popupMenu.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        popupMenu.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        popupMenu.setOutsideTouchable(true);
        popupMenu.setFocusable(true);
        popupMenu.setBackgroundDrawable(getResources().getDrawable(android.R.color.transparent));
        popupMenu.setAnimationStyle(R.style.AnimationDropdown);

        lvDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                unsetSelected();

                if(onDropdownClick != null)
                    onDropdownClick.onClick(dropdownEntities.get(position), position);

                dropdownEntities.get(position).setSelected(true);
                tvDropdown.setText(dropdownEntities.get(position).getDescription());

                popupMenu.dismiss();
            }
        });
    }

    public DropdownEntity getSelectedItem(){
        for(DropdownEntity entity: dropdownEntities){
            if(entity.isSelected())
                return entity;
        }

        return null;
    }

    private void unsetSelected(){
        for(DropdownEntity entity: dropdownEntities)
            entity.setSelected(false);
    }
}
