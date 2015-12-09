package com.catapush.demo.catapush36integrationtest.app.messages;

import com.catapush.demo.catapush36integrationtest.app.R;
import com.catapush.demo.catapush36integrationtest.app.TitleChange;
import com.catapush.library.storage.models.IPMessage;
import com.catapush.library.ui.CatapushRecyclerViewAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class MessageFragment extends Fragment implements MessageView {

    private MessagePresenter mPresenter;

    private CatapushRecyclerViewAdapter mAdapter;

    private TitleChange titleChanger;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.messages_recyclerview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new CatapushRecyclerViewAdapter();
        mAdapter.setMessages(new ArrayList<IPMessage>());
        recyclerView.setAdapter(mAdapter);

        mPresenter.getMessages();
        if (titleChanger != null) {
            titleChanger.set(getString(R.string.messages_fragment_title));
        }
        return rootView;
    }

    @Override
    public void addMessage(IPMessage message) {
        mAdapter.add(message);
    }

    @Override
    public void setMessages(List<IPMessage> messages) {
        mAdapter.setMessages(messages);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mPresenter = null;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mPresenter = new MessagePresenter(context, this);
    }

    public void setTitleChanger(@NonNull TitleChange change) {
        titleChanger = change;
    }
}