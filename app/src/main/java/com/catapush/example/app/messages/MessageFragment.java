package com.catapush.example.app.messages;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.catapush.example.app.R;
import com.catapush.example.app.SampleViewModelFactory;
import com.catapush.example.app.TitleChange;
import com.catapush.library.interfaces.Callback;
import com.catapush.library.messages.CatapushMessage;
import com.catapush.library.ui.recyclerview.CatapushMessagesAdapter;
import com.catapush.library.ui.recyclerview.CatapushMessagesLayoutManager;
import com.catapush.library.ui.widget.CatapushMessageTouchHelper;
import com.catapush.library.ui.widget.SendFieldView;
import com.google.android.material.snackbar.Snackbar;


public class MessageFragment
        extends Fragment
        implements MessageView, Observer<PagedList<CatapushMessage>> {

    private MessagePresenter presenter;
    private TitleChange titleChanger;
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private SendFieldView sendFieldView;
    private CatapushMessagesAdapter adapter;

    private CatapushMessagesAdapter.ActionListener actionListener = new CatapushMessagesAdapter.ActionListener() {
        @Override
        public void onImageClick(@NonNull CatapushMessage message) {
            //TODO: implement
        }

        @Override
        public void onPdfClick(@NonNull CatapushMessage message) {
            //TODO: implement
        }
    };

    private CatapushMessagesAdapter.SendFieldViewProvider sendProvider = () -> sendFieldView;

    private Callback<CatapushMessage> deleteMessageCallback = new Callback<CatapushMessage>() {
        @Override
        public void success(CatapushMessage message) {
            if (presenter != null) {
                presenter.onMessageDeleted(message);
            }
        }
        @Override
        public void failure(@NonNull Throwable t) {
            Log.e(MessageFragment.class.getSimpleName(), t.getMessage());
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        adapter = new CatapushMessagesAdapter(actionListener, sendProvider);
        MessagingViewModel viewModel = new ViewModelProvider(this.getViewModelStore(), new SampleViewModelFactory())
                .get(MessagingViewModel.class)
                .init();
        if (viewModel.messageList != null) {
            viewModel.messageList.observe(this, this);
        }
    }

    @Override
    public void onChanged(PagedList<CatapushMessage> catapushMessages) {
        int prevCount = adapter.getItemCount();
        adapter.submitList(catapushMessages, () -> {
            if (recyclerView != null) {
                if (prevCount == 0) {
                    // First change, no messages already displayed: go to the bottom
                    recyclerView.scrollToPosition(0);
                } else if (prevCount < adapter.getItemCount()) {
                    // Message received with other messages already loaded, scroll to bottom
                    recyclerView.smoothScrollToPosition(0);
                }
            }
        });
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        coordinatorLayout = rootView.findViewById(R.id.coordinator);
        recyclerView = rootView.findViewById(R.id.messages_recyclerview);
        sendFieldView = rootView.findViewById(R.id.send_container);

        RecyclerView recyclerView = rootView.findViewById(R.id.messages_recyclerview);
        recyclerView.setLayoutManager(new CatapushMessagesLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        CatapushMessageTouchHelper.attachToRecyclerView(recyclerView, deleteMessageCallback);

        if (titleChanger != null) {
            titleChanger.set(getString(R.string.messages_fragment_title));
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sendFieldView = null;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        presenter = new MessagePresenter(this);
    }

    public void setTitleChanger(@NonNull TitleChange change) {
        titleChanger = change;
    }

    @Override
    public void showUndeleteSnackBar(String messagePreview) {
        if (coordinatorLayout != null) {
            String content = getString(R.string.message_delete, messagePreview);
            Snackbar
                    .make(coordinatorLayout, content, Snackbar.LENGTH_LONG)
                    .setAction(R.string.undo, view -> {
                        if (adapter != null) {
                            adapter.undo();
                        }
                    })
                    .show();
        }
    }

}
