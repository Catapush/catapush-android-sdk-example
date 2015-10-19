package com.catapush.demo.catapush36integrationtest.app.messages;

import com.catapush.demo.catapush36integrationtest.app.R;
import com.catapush.library.storage.models.IPMessage;

import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class MessageFragment extends Fragment implements MessageView {

    private MessagePresenter mMessagePresenter;

    private TextView mLastMessage;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        mLastMessage = (TextView) rootView.findViewById(R.id.last_message);
        return rootView;
    }

    @Override
    public void setMessage(IPMessage message) {
        mLastMessage.setText(message.getContentAsString());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mMessagePresenter = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mMessagePresenter = new MessagePresenter(context, this);

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        mMessagePresenter.startCatapush(sound);
    }
}