package com.consultoraestrategia.messengeracademico.chat.ui;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Px;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenter;
import com.consultoraestrategia.messengeracademico.chat.adapters.ChatMessageAdapter;
import com.consultoraestrategia.messengeracademico.chat.di.ChatComponent;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.dialogProfile.DialogProfile;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.fullScreen.FullscreenActivity;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.profile.ui.ProfileActivity;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vanniktech.emoji.EmojiEditText;
import com.vanniktech.emoji.EmojiImageView;
import com.vanniktech.emoji.EmojiPopup;
import com.vanniktech.emoji.emoji.Emoji;
import com.vanniktech.emoji.listeners.OnEmojiBackspaceClickListener;
import com.vanniktech.emoji.listeners.OnEmojiClickListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupDismissListener;
import com.vanniktech.emoji.listeners.OnEmojiPopupShownListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardCloseListener;
import com.vanniktech.emoji.listeners.OnSoftKeyboardOpenListener;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.codetail.animation.ViewAnimationUtils;


/**
 * Created by @stevecampos on 9/03/2017.
 */


public class ChatActivity extends AppCompatActivity implements ChatView, ChatMessageListener {

    private static final String TAG = ChatActivity.class.getSimpleName();
    public static final String EXTRA_RECEPTOR_PHONENUMBER = "EXTRA_RECEPTOR_PHONENUMBER";

    @BindView(R.id.img_profile)
    ImageView imgProfile;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_connection)
    TextView txtConnection;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.input_layout_message)
    TextInputLayout inputLayoutMessage;
    @BindView(R.id.layout_bottom)
    RelativeLayout layoutBottom;
    @BindView(R.id.btn_send)
    FloatingActionButton fab;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    @Inject
    ChatMessageAdapter adapter;
    @Inject
    ChatPresenter presenter;

    Contact contact;


    private boolean hidden = true;
    @BindView(R.id.gallery_img_btn)
    ImageButton galleryImgBtn;
    @BindView(R.id.photo_img_btn)
    ImageButton photoImgBtn;
    @BindView(R.id.video_img_btn)
    ImageButton videoImgBtn;
    @BindView(R.id.audio_img_btn)
    ImageButton audioImgBtn;
    @BindView(R.id.location_img_btn)
    ImageButton locationImgBtn;
    @BindView(R.id.contact_img_btn)
    ImageButton contactImgBtn;
    @BindView(R.id.reveal_items)
    CardView mRevealView;


    EmojiPopup emojiPopup;

    @BindView(R.id.coordinatorLayout)
    CoordinatorLayout rootView;

    @BindView(R.id.btn_emoji)
    ImageView emojiButton;
    @BindView(R.id.edt_message)
    EmojiEditText edtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setupViews();
        setupInjection();
        setupRecycler();
        initPresenter();
    }

    private void setupViews() {
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_24dp);
        }
        edtMessage.addTextChangedListener(watcher);
        emojiButton.setColorFilter(ContextCompat.getColor(this, R.color.emoji_icons), PorterDuff.Mode.SRC_IN);
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                emojiPopup.toggle();
            }
        });
        setUpEmojiPopup();
    }

    private void initPresenter() {
        Intent intent = getIntent();
        if (intent.hasExtra(EXTRA_RECEPTOR_PHONENUMBER)) {
            String receptorPhoneNumber = intent.getStringExtra(EXTRA_RECEPTOR_PHONENUMBER);
            presenter.onCreate();
            presenter.loadReceptor(receptorPhoneNumber);
        } else {
            finish();
        }
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            presenter.onBackPressed();
        }
        super.onBackPressed();
    }


    @Override
    protected void onStart() {
        Log.d(TAG, "onStart");
        presenter.onStart();
        super.onStart();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_chat, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_send_image) {
            presenter.pickImage();
            return true;
        }
        if (id == R.id.action_clip) {
            int cx = mRevealView.getRight();
            int cy = mRevealView.getTop();
            makeEffect(mRevealView, cx, cy);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume");
        presenter.onResume();
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause");
        presenter.onPause();
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop");
        if (emojiPopup != null) {
            emojiPopup.dismiss();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        presenter.onDestroy();
        super.onDestroy();
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return presenter;
    }

    private void setupInjection() {
        Log.d(TAG, "setupInjection");
        presenter = (ChatPresenter) getLastCustomNonConfigurationInstance();

        if (presenter == null) {
            Log.d(TAG, "presenter != null");
            MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
            ChatComponent chatComponent = app.getChatComponent(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()), this);
            presenter = chatComponent.getPresenter();
            adapter = chatComponent.getAdapter();
        }
        if (adapter == null) {
            MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
            ChatComponent chatComponent = app.getChatComponent(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()), this);
            adapter = chatComponent.getAdapter();
        }

        presenter.attachView(this);
    }

    private void setupRecycler() {
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recycler.setAdapter(adapter);
        adapter.setRecyclerView(recycler);
    }

    private AppCompatActivity getActivity() {
        return this;
    }

    @Override
    public void setPresenter(ChatPresenter presenter) {
        this.presenter = presenter;
    }

    @Override
    public void showProgress() {
        progressBar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgress() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public void showReceptor(Contact receptor) {
        txtName.setText(receptor.getName());
        Glide
                .with(this)
                .load(receptor.getPhotoUri())
                .error(R.drawable.ic_users)
                .into(imgProfile);
        contact = receptor;
    }

    @Override
    public void addMessages(List<ChatMessage> messages) {
        Log.d(TAG, "addMessages");
        if (messages != null) {
            adapter.addMessageList(messages);
        }
    }

    @Override
    public void addMessage(ChatMessage message) {
        Log.d(TAG, "addMessage");
        if (message != null) {
            adapter.onMessageAdded(message);
        }
    }

    @Override
    public void onMessageReaded(ChatMessage message) {
        Log.d(TAG, "onMessageReaded");
        presenter.readMessage(message);
    }

    @Override
    public void onImageClick(ChatMessage message, View view) {
        Log.d(TAG, "onImageClick uiri: " + message.getMessageUri());
        startFullImageActivity(message, view);
    }


    private void startFullImageActivity(ChatMessage message, View imageView) {
        Intent intent = new Intent(ChatActivity.this, FullscreenActivity.class);
        intent.setData(Uri.parse(message.getMessageUri()));
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, imageView, ViewCompat.getTransitionName(imageView));
        startActivity(intent, options.toBundle());
    }


    @OnClick(R.id.btn_send)
    @Override
    public void sendMessageText() {
        String text = edtMessage.getText().toString();
        if (text.isEmpty()) {
            return;
        }

        edtMessage.setText("");
        presenter.sendMessageText(text);
    }

    public static final SimpleDateFormat SIMPLE_DATE_FORMAT = new SimpleDateFormat("h:mm a", Locale.getDefault());

    @Override
    public void showConnection(Connection connection) {
        Log.d(TAG, "showConnection: " + connection);
        if (connection == null) {
            txtConnection.setVisibility(View.GONE);
            return;
        }

        boolean online = connection.isOnline();
        long lastConnection = connection.getTimestamp();

        String connectionText;
        if (online) {
            connectionText = getString(R.string.global_notice_online);
        } else {
            connectionText = String.format(getString(R.string.chat_info_connection_lastseen), SIMPLE_DATE_FORMAT.format(lastConnection));
        }
        txtConnection.setVisibility(View.VISIBLE);
        txtConnection.setText(connectionText);
    }

    private boolean isWriting;
    private final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            Log.d(TAG, "afterTextChanged editable.length(): " + editable.length());
            presenter.changeWritingState(editable.toString());
        }
    };

    @Override
    public void showReceptorAction(Contact contact, String action) {
        Log.d(TAG, "showReceptorAction");
        String actionFormatted = getString(R.string.global_action_writing);
        txtConnection.setVisibility(View.VISIBLE);
        txtConnection.setText(actionFormatted);
    }

    @Override
    public void startPickImageActivity() {
        Log.d(TAG, "startPickImageActivity: ");
        CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);
    }

    @Override
    public void showError(String error) {
        Snackbar.make(appbar, error, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void showVoiceIcon() {
        fab.setImageResource(R.drawable.ic_keyboard_voice_white_24dp);
    }

    @Override
    public void showSendIcon() {
        fab.setImageResource(R.drawable.ic_send_24dp);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    /*Editado OnbackPressed y entrar Profile*/
    @OnClick(R.id.linear_StartProfile)
    public void onContactReceptorSelected() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ImportDataActivity.EXTRA_PHOTO_URI, contact.getPhotoUri());
        intent.putExtra(ImportDataActivity.EXTRA_NAME, contact.getName());
        intent.putExtra(ImportDataActivity.EXTRA_PHONENUMBER, contact.getPhoneNumber());
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @OnClick({R.id.gallery_img_btn, R.id.photo_img_btn, R.id.video_img_btn, R.id.audio_img_btn, R.id.location_img_btn, R.id.contact_img_btn, R.id.img_camera})
    public void onViewClicked(View view) {
        hideRevealView();
        switch (view.getId()) {
            case R.id.gallery_img_btn:
                presenter.pickImage();
                break;
            case R.id.photo_img_btn:
                showNotImplementedView();
                break;
            case R.id.video_img_btn:
                showNotImplementedView();
                break;
            case R.id.audio_img_btn:
                showNotImplementedView();
                break;
            case R.id.location_img_btn:
                showNotImplementedView();
                break;
            case R.id.contact_img_btn:
                showNotImplementedView();
                break;
            case R.id.img_camera:
                showNotImplementedView();
                break;
        }
    }

    private void showNotImplementedView() {
        showSnackbar("No implementado aún");
    }

    private void showSnackbar(String message) {
        Snackbar.make(toolbar, message, Snackbar.LENGTH_LONG).show();
    }


    private void hideRevealView() {
        if (mRevealView.getVisibility() == View.VISIBLE) {
            mRevealView.setVisibility(View.GONE);
            hidden = true;
        }
    }

    private void makeEffect(final View layout, int cx, int cy) {
        Log.d(TAG, "makeEffect");
        int radius = Math.max(layout.getWidth(), layout.getHeight());

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {

            Animator animator =
                    ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
            animator.setInterpolator(new AccelerateDecelerateInterpolator());
            animator.setDuration(700);

            if (hidden) {
                layout.setVisibility(View.VISIBLE);
                animator.start();
                hidden = false;
            } else {

                Animator anim = ViewAnimationUtils.createCircularReveal(layout, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(View.INVISIBLE);
                        hidden = true;
                    }
                });
                anim.start();

            }
        } else {
            if (hidden) {
                Log.d(TAG, "anim.start()...");
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, 0, radius);
                layout.setVisibility(View.VISIBLE);
                anim.start();
                hidden = false;
            } else {
                Log.d(TAG, "anim.start()...");
                Animator anim = android.view.ViewAnimationUtils.createCircularReveal(layout, cx, cy, radius, 0);
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        layout.setVisibility(View.INVISIBLE);
                        hidden = true;
                    }
                });
                anim.start();

            }
        }
    }


    private void setUpEmojiPopup() {
        emojiPopup = EmojiPopup.Builder.fromRootView(rootView)
                .setOnEmojiBackspaceClickListener(new OnEmojiBackspaceClickListener() {
                    @Override
                    public void onEmojiBackspaceClick(final View v) {
                        Log.d(TAG, "Clicked on Backspace");
                    }
                })
                .setOnEmojiClickListener(new OnEmojiClickListener() {
                    @Override
                    public void onEmojiClick(@NonNull final EmojiImageView imageView, @NonNull final Emoji emoji) {
                        Log.d(TAG, "Clicked on emoji");
                    }
                })
                .setOnEmojiPopupShownListener(new OnEmojiPopupShownListener() {
                    @Override
                    public void onEmojiPopupShown() {
                        emojiButton.setImageResource(R.drawable.ic_keyboard);
                    }
                })
                .setOnSoftKeyboardOpenListener(new OnSoftKeyboardOpenListener() {
                    @Override
                    public void onKeyboardOpen(@Px final int keyBoardHeight) {
                        Log.d(TAG, "Opened soft keyboard");
                    }
                })
                .setOnEmojiPopupDismissListener(new OnEmojiPopupDismissListener() {
                    @Override
                    public void onEmojiPopupDismiss() {
                        emojiButton.setImageResource(R.drawable.emoji_one_category_people);
                    }
                })
                .setOnSoftKeyboardCloseListener(new OnSoftKeyboardCloseListener() {
                    @Override
                    public void onKeyboardClose() {
                        Log.d(TAG, "Closed soft keyboard");
                    }
                })
                .build(edtMessage);
    }
}
