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

    private MessagePresenter presenter;

    private CatapushRecyclerViewAdapter adapter;

    private TitleChange titleChanger;

    private CatapushRecyclerViewAdapter.ActionListener mActionListener = new CatapushRecyclerViewAdapter.ActionListener() {
        @Override
        public void onImageClick(CatapushMessage message) {
            //TODO: implement
        }

        @Override
        public void onPdfClick(CatapushMessage message) {
            //TODO: implement
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.messages_recyclerview);
        recyclerView.setHasFixedSize(true);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        adapter = new CatapushRecyclerViewAdapter(mActionListener);
        adapter.set(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        presenter.getMessages();
        if (titleChanger != null) {
            titleChanger.set(getString(R.string.messages_fragment_title));
        }
        return rootView;
    }

    @Override
    public void setMessages(List<CatapushMessage> messages) {
        adapter.set(messages);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        getActivity().unregisterReceiver(presenter.getReceiver());
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        presenter = new MessagePresenter(context, this);
    }

    public void setTitleChanger(@NonNull TitleChange change) {
        titleChanger = change;
    }
}
