package com.conx2share.conx2share.ui.base;

import android.view.View;
import android.widget.TextView;

import com.conx2share.conx2share.R;
import com.conx2share.conx2share.model.Message;
import com.conx2share.conx2share.model.TimeDividerMessage;

import butterknife.BindView;
import butterknife.ButterKnife;
//import butterknife.InjectView;
import io.techery.celladapter.Cell;
import io.techery.celladapter.Layout;

@Layout(R.layout.item_chat_time_message)
public class MessageTimeCell extends BaseRoboCell<Message, Cell.Listener<Message>> {

    @BindView(R.id.chat_time_item_text)
    TextView timeTv;

    public MessageTimeCell(View view) {
        super(view);
        ButterKnife.bind(this, view);
    }

    @Override
    protected void bindView() {
        Message msg = getItem();
        if (msg instanceof TimeDividerMessage) {
            timeTv.setText(((TimeDividerMessage) msg).getTimeToDisplay());
        }else{
            timeTv.setText(msg.getCreatedAt());
        }
    }
}