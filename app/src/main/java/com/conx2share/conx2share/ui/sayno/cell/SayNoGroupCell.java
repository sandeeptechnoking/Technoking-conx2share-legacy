package com.conx2share.conx2share.ui.sayno.cell;

import android.view.View;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Group;
import com.conx2share.conx2share.ui.view.AvatarImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import io.techery.celladapter.Cell;
import io.techery.celladapter.Layout;

@Layout(R.layout.item_say_no_school_item)
public final class SayNoGroupCell extends Cell<Group, Cell.Listener<Group>> {
    @BindView(R.id.say_no_school_logo)
    AvatarImageView schoolLogo;

    @BindView(R.id.say_no_school_name)
    TextView schoolName;

    public SayNoGroupCell(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    protected void bindView() {
        Group group = getItem();
        schoolLogo.initView(group.getGroupAvatarUrl(), group.getName());
        schoolName.setText(group.getName());
    }
}