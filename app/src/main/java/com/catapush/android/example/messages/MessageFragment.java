package com.catapush.android.example.messages;

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
import androidx.lifecycle.ViewModelProvider;
import androidx.paging.CombinedLoadStates;
import androidx.paging.LoadState;
import androidx.recyclerview.widget.RecyclerView;

import com.catapush.android.example.MainActivity;
import com.catapush.android.example.MessageContract;
import com.catapush.android.example.MessagePresenter;
import com.catapush.android.example.R;
import com.catapush.android.example.SampleViewModelFactory;
import com.catapush.library.Catapush;
import com.catapush.library.interfaces.Callback;
import com.catapush.library.messages.CatapushMessage;
import com.catapush.library.ui.recyclerview.CatapushMessagesAdapter;
import com.catapush.library.ui.recyclerview.CatapushMessagesLayoutManager;
import com.catapush.library.ui.widget.CatapushMessageTouchHelper;
import com.catapush.library.ui.widget.SendFieldView;
import com.google.android.material.snackbar.Snackbar;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;


public class MessageFragment
        extends Fragment
        implements MessageContract.MessageView {

    private final MessagePresenter presenter = new MessagePresenter(this);
    private CoordinatorLayout coordinatorLayout;
    private RecyclerView recyclerView;
    private SendFieldView sendFieldView;
    private CatapushMessagesAdapter adapter;
    private Disposable messageListSubscription;
    private int prevAdapterCount = 0;
    private final Function1<CombinedLoadStates, Unit> loadEndListener = new Function1<CombinedLoadStates, Unit>() {
        boolean wasLoading = false;
        @Override
        public Unit invoke(CombinedLoadStates combinedLoadStates) {
            if (!wasLoading && combinedLoadStates.getRefresh() instanceof LoadState.Loading) {
                wasLoading = true;
            } else if (wasLoading && combinedLoadStates.getRefresh() instanceof LoadState.NotLoading) {
                int newCount = adapter.getItemCount();
                if (recyclerView != null && prevAdapterCount != newCount) {
                    if (prevAdapterCount == 0) {
                        // First change, no messages already displayed: go to the bottom
                        recyclerView.scrollToPosition(0);
                    } else if (prevAdapterCount < newCount) {
                        // Message received with other messages already loaded, scroll to bottom
                        recyclerView.smoothScrollToPosition(0);
                    }
                    prevAdapterCount = newCount;
                }
                wasLoading = false;
            } else {
                wasLoading = false;
            }
            return null;
        }
    };

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

        adapter = new CatapushMessagesAdapter(getLifecycle(), actionListener, sendProvider);
        adapter.addLoadStateListener(loadEndListener);
        MessagingViewModel viewModel = new ViewModelProvider(this.getViewModelStore(), new SampleViewModelFactory())
                .get(MessagingViewModel.class)
                .init();
        if (viewModel.messageList != null) {
            messageListSubscription = viewModel.messageList
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            pagingData -> {
                                prevAdapterCount = adapter.getItemCount();
                                adapter.submitData(getLifecycle(), pagingData);
                            },
                            e -> Log.e("MyApp", "Message list flowable error", e)
                    );
        }
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
        adapter.removeLoadStateListener(loadEndListener);
        messageListSubscription.dispose();
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
