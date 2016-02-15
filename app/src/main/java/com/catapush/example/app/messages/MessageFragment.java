package com.catapush.example.app.messages;

import com.catapush.example.app.R;
import com.catapush.example.app.TitleChange;
import com.catapush.library.CatapushRecyclerViewAdapter;
import com.catapush.library.messages.CatapushMessage;

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
        mAdapter.set(new ArrayList<CatapushMessage>());
        recyclerView.setAdapter(mAdapter);

        mPresenter.getMessages();
        if (titleChanger != null) {
            titleChanger.set(getString(R.string.messages_fragment_title));
        }
        return rootView;
    }

    @Override
    public void setMessages(List<CatapushMessage> messages) {
        mAdapter.set(messages);
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
