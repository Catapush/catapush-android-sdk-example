package com.catapush.example.app.messages;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.PagedList;
import androidx.recyclerview.widget.RecyclerView;

import com.catapush.example.app.MainActivity;
import com.catapush.example.app.MessageContract;
import com.catapush.example.app.MessagePresenter;
import com.catapush.example.app.R;
import com.catapush.example.app.SampleViewModelFactory;
import com.catapush.library.Catapush;
import com.catapush.library.interfaces.Callback;
import com.catapush.library.messages.CatapushMessage;
import com.catapush.library.ui.recyclerview.CatapushMessagesAdapter;
import com.catapush.library.ui.recyclerview.CatapushMessagesLayoutManager;
import com.catapush.library.ui.widget.CatapushMessageTouchHelper;
import com.catapush.library.ui.widget.SendFieldView;
import com.google.android.material.snackbar.Snackbar;


public class MessageFragment
        extends Fragment
        implements MessageContract.MessageView, Observer<PagedList<CatapushMessage>> {

    private final MessagePresenter presenter = new MessagePresenter(this);
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private SendFieldView sendFieldView;
    private CatapushMessagesAdapter adapter;

    private final CatapushMessagesAdapter.ActionListener actionListener = new CatapushMessagesAdapter.ActionListener() {
        @Override
        public void onMessageLongClick(@NonNull View view, @NonNull CatapushMessage message, int position) {
            //TODO: implement
        }

        @Override
        public void onImageClick(@NonNull View view, @NonNull CatapushMessage message, int position) {
            //TODO: implement
        }

        @Override
        public void onPdfClick(@NonNull View view, @NonNull CatapushMessage message, int position) {
            //TODO: implement
        }

        @Override
        public void onTxtClick(@NonNull View view, @NonNull CatapushMessage message, int position) {
            //TODO: implement
        }
    };

    private final CatapushMessagesAdapter.SendFieldViewProvider sendProvider = () -> sendFieldView;

    private final Callback<CatapushMessage> deleteMessageCallback = new Callback<CatapushMessage>() {
        @Override
        public void success(CatapushMessage message) {
            presenter.onMessageDeleted(message);
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

        sendFieldView.setAttachButtonClickListener(v -> {
            presenter.onPickAttachmentRequest("image/*");
            // Other examples:
            // - openFilePicker("text/plain");
            // - openFilePicker("application/pdf");
        });

        RecyclerView recyclerView = rootView.findViewById(R.id.messages_recyclerview);
        recyclerView.setLayoutManager(new CatapushMessagesLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);
        recyclerView.setAdapter(adapter);
        // See also CatapushMessageTouchHelper.ReplyOnSwipeBehavior
        CatapushMessageTouchHelper.RemoveOnSwipeBehavior deleteBehavior
                = new CatapushMessageTouchHelper.RemoveOnSwipeBehavior(adapter, deleteMessageCallback);
        CatapushMessageTouchHelper.attachToRecyclerView(recyclerView, deleteBehavior);


        presenter.onViewTitleChanged(getString(R.string.messages_fragment_title));

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
        if (context instanceof MainActivity) {
            ((MainActivity) context).setPresenter(presenter);
        }
        if (context instanceof MessageContract.MainView) {
            presenter.setMainView((MessageContract.MainView) context);
        }
    }

    @Override
    public void onDetach() {
        final Context context = getContext();
        if (context instanceof MainActivity) {
            ((MainActivity) context).setPresenter(presenter);
        }
        presenter.setMainView(null);
        super.onDetach();
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

    @Override
    public void attachFile(@NonNull Uri attachmentUri) {
        String originalMessageId = sendFieldView.getOriginalMessageId();
        Catapush.getInstance().sendFile(attachmentUri, "", null, originalMessageId, new Callback<Boolean>() {
            public void success(Boolean response) {
                Toast.makeText(getContext(), "File sent", Toast.LENGTH_LONG).show();
            }

            public void failure(@NonNull Throwable irrecoverableError) {
                Toast.makeText(getContext(), "Can't send file", Toast.LENGTH_LONG).show();
            }
        });
    }

}
