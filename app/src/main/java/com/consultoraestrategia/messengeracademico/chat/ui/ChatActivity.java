package com.consultoraestrategia.messengeracademico.chat.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
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
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.MessengerAcademicoApp;
import com.consultoraestrategia.messengeracademico.R;
import com.consultoraestrategia.messengeracademico.base.actionMode.BaseActivityActionMode;
import com.consultoraestrategia.messengeracademico.chat.ChatPresenter;
import com.consultoraestrategia.messengeracademico.chat.adapters.ChatMessageAdapter;
import com.consultoraestrategia.messengeracademico.chat.di.ChatComponent;
import com.consultoraestrategia.messengeracademico.chat.listener.ChatMessageListener;
import com.consultoraestrategia.messengeracademico.entities.ChatMessage;
import com.consultoraestrategia.messengeracademico.entities.Connection;
import com.consultoraestrategia.messengeracademico.entities.Contact;
import com.consultoraestrategia.messengeracademico.fullScreen.FullscreenActivity;
import com.consultoraestrategia.messengeracademico.importData.ui.ImportDataActivity;
import com.consultoraestrategia.messengeracademico.importGroups.entities.ui.CrmeUser;
import com.consultoraestrategia.messengeracademico.profile.ui.ProfileActivity;
import com.consultoraestrategia.messengeracademico.utils.TimeUtils;
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
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTouch;
import io.codetail.animation.ViewAnimationUtils;
import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;


/**
 * Created by @stevecampos on 9/03/2017.
 */

@RuntimePermissions
public class ChatActivity extends BaseActivityActionMode<ChatMessage, ChatView, ChatPresenter> implements ChatView, ChatMessageListener, ChatMessageAdapter.OnBottomReachedListener {

    private static final String TAG = ChatActivity.class.getSimpleName();
    public static final String EXTRA_RECEPTOR_PHONENUMBER = "EXTRA_RECEPTOR_PHONENUMBER";
    public static final int REQUEST_CODE_CHOOSE_IMAGE = 100;

    @BindView(R.id.img_profile)
    ImageView imgProfile;
    @BindView(R.id.txt_name)
    TextView txtName;
    @BindView(R.id.txt_rol)
    TextView txtRol;
    @BindView(R.id.txt_connection)
    TextView txtConnection;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.appbar)
    AppBarLayout appbar;
    @BindView(R.id.recycler)
    RecyclerView recycler;
    @BindView(R.id.layout_bottom)
    CardView layoutBottom;

    @BindView(R.id.btn_send)
    FloatingActionButton fab;
    @BindView(R.id.progressBar)
    ProgressBar progressBar;


    @Inject
    ChatMessageAdapter adapter;
    /*@Inject
    ChatPresenter presenter;*/

    Contact contact;

    @BindView(R.id.btn_scroll)
    ImageButton btnScroll;
    @BindView(R.id.txtCounter)
    TextView txtCounter;
    @BindView(R.id.layout_croll)
    RelativeLayout layoutCroll;


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


    @BindView(R.id.txt_academic_information)
    TextView txtAcademicInformation;
    @BindView(R.id.layout_academic_information)
    RelativeLayout layoutAcademicInformation;


    // slide-up animation
    Animation slideUp;
    Animation slideDown;

    public static void startChatActivity(Context context, String phonenumberReceptor) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(ChatActivity.EXTRA_RECEPTOR_PHONENUMBER, phonenumberReceptor);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected String getTag() {
        return ChatActivity.class.getSimpleName();
    }

    @Override
    public AppCompatActivity getActivity() {
        return this;
    }

    @Override
    protected ChatPresenter getPresenter() {
        Log.d(TAG, "presenter != null");
        MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
        ChatComponent chatComponent = app.getChatComponent(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()), this, this);
        if (adapter == null) {
            adapter = chatComponent.getAdapter();
        }
        return chatComponent.getPresenter();
    }

    @Override
    protected ChatView getBaseView() {
        return this;
    }

    @Override
    protected Bundle getExtrasInf() {
        return getIntent().getExtras();
    }

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
    }

    @Override
    protected ViewGroup getRootLayout() {
        return toolbar;
    }

    @Override
    protected ProgressBar getProgressBar() {
        return progressBar;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setupViews();
        setupRecycler();
        setupAnimations();
    }

    private void setupAnimations() {
        slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up);
        slideDown = AnimationUtils.loadAnimation(this, R.anim.slide_down);
    }

    private void setupViews() {
        edtMessage.addTextChangedListener(watcher);
        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                emojiPopup.toggle();
            }
        });
        setUpEmojiPopup();
        edtMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.d(TAG, "hasFocus: " + hasFocus);
                if (hasFocus) {
                    hideRevealView();
                }
            }
        });
    }


    @OnTouch(R.id.recycler)
    public boolean onRecyclerClick(View view, MotionEvent event) {
        Log.d(TAG, "onRecyclerClick: ");
        hideRevealView();
        return false;
    }

    @Override
    public void dismissNotification(int id) {
        Log.d(TAG, "id: " + id);
        NotificationManagerCompat.from(this).cancel(id);
    }

    @Override
    public void showButtomToScroll(int count) {
        Log.d(TAG, "showButtonToScroll");
        //layoutCroll.setVisibility(View.VISIBLE);
    }

    @Override
    public void addMoreMessages(List<ChatMessage> messages) {
        Log.d(TAG, "addMoreMessages");
        if (adapter == null) adapter = getAdapter();
        adapter.addMessages(messages);
    }

    @OnClick(R.id.btn_scroll)
    public void buttonScrollClicked() {
        if (adapter == null) adapter = getAdapter();
        adapter.scrollToLastItem();
    }


    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed");
        if (emojiPopup != null && emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        } else {
            super.onBackPressed();
        }
    }

    @OnClick(R.id.layout_profile)
    public void onClickBackPressed() {
        super.onBackPressed();
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

        switch (id) {
            case R.id.action_view_contact:
                presenter.onActionViewContactSelected();
                break;
            case R.id.action_atttach_file:
                toggleMenuWithEffect();
                break;
            case android.R.id.home:
                super.onBackPressed();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showContactInPhone(String phoneNumber) {
        if (phoneNumber == null) return;
        Intent callIntent = new Intent(Intent.ACTION_DIAL);
        callIntent.setData(Uri.parse("tel:" + phoneNumber));
        if (callIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callIntent);
        }
    }

    @Override
    public void copyText(String textToCopy) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        if (clipboard != null) {
            ClipData clip = ClipData.newPlainText("label", textToCopy);
            clipboard.setPrimaryClip(clip);
        }
    }

    @Override
    public void removeMessage(ChatMessage message) {
        if (adapter == null) adapter = getAdapter();
        adapter.removeMessage(message);
    }

    @Override
    public void showFullScreenImg(String uri) {
        startFullImageActivity(uri);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // NOTE: delegate the permission handling to generated method
        ChatActivityPermissionsDispatcher.onRequestPermissionsResult(this, requestCode, grantResults);
    }

    private ChatMessageAdapter getAdapter() {
        MessengerAcademicoApp app = (MessengerAcademicoApp) getApplication();
        ChatComponent chatComponent = app.getChatComponent(getActivity(), PreferenceManager.getDefaultSharedPreferences(getActivity()), this, this);
        return chatComponent.getAdapter();
    }

    private void setupRecycler() {
        if (adapter == null) adapter = getAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recycler.setLayoutManager(layoutManager);
        recycler.setAdapter(adapter);
        adapter.setmLinearLayoutManager(layoutManager);
        adapter.setRecyclerView(recycler);
    }

    @Override
    public void showReceptor(Contact receptor) {
        String name = receptor.getDisplayName();

        CrmeUser crmeUser = CrmeUser.getCrmeUser(receptor.getPhoneNumber());
        if (crmeUser != null) {
            txtName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_verify_white,
                    0,
                    0,
                    0
            );

            name = crmeUser.getName();
            String rol = crmeUser.getDisplayName();
            txtRol.setVisibility(View.VISIBLE);
            if (!TextUtils.isEmpty(rol))
                txtRol.setText(rol);
        } else {
            txtRol.setVisibility(View.GONE);
        }

        String verified = receptor.getInfoVerified();
        if (!TextUtils.isEmpty(verified)) {
            txtName.setCompoundDrawablesRelativeWithIntrinsicBounds(
                    R.drawable.ic_verify_white, 0, 0, 0
            );
        }
        if (!TextUtils.isEmpty(name))
            txtName.setText(name);

        Glide
                .with(this)
                .load(receptor.getPhotoUrl())
                .error(R.drawable.ic_users)
                .into(imgProfile);
        contact = receptor;
    }

    @Override
    public void addMessages(List<ChatMessage> messages) {
        Log.d(TAG, "addMessages");
        if (messages != null) {
            if (adapter == null) adapter = getAdapter();
            adapter.addMessageList(messages);
        }
    }

    @Override
    public void addMessage(ChatMessage message) {
        Log.d(TAG, "addMessage");
        if (message != null) {
            //adapter.onMessageAdded(message);
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
        presenter.onImageClick(message);
        //startFullImageActivity(message, view);
    }

    @Override
    public void onOfficialMesageListener(ChatMessage message, View view) {
        Log.d(TAG, "onOfficialMesageListener: " + message.toMap());
        presenter.onOfficialMessageActionClicked(message, view);
    }

    @Override
    public void onMessageNotReaded(ChatMessage message) {
        Log.d(TAG, "message: " + message.toString());
        presenter.onMessageNotReaded(message);
    }

    @Override
    public void onMessageNotSended(ChatMessage message) {
        presenter.onMessageNotSended(message);
    }

    int counter;

    @Override
    public void onNewMessageAddedToTheBottom() {
        showButtonToScroll();
        txtCounter.setVisibility(View.VISIBLE);
        txtCounter.setText("" + (++counter));
    }

    @Override
    public void onLoadMore(ChatMessage olderMessage) {
        Log.d(TAG, "onLoadMore");
        presenter.loadMoreMessages(olderMessage);
    }


    @Override
    public void showDialogToConfirmOfficialMessage(final ChatMessage message) {
        Log.d(TAG, "showDialogToConfirmOfficialMessage");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Confirmación")
                .setMessage("¿Está seguro de confirmar la solicitud?\nSe enviará un mensaje de respuesta.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onConfirmOfficialMessage(message);
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

    @Override
    public void showDialogToEnsureDenyOfficialMessage(final ChatMessage message) {
        Log.d(TAG, "showDialogToEnsureDenyOfficialMessage");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Denegación")
                .setMessage("¿Está seguro de denegar la solicitud?\nSe enviará un mensaje de respuesta.")
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                onDenyOfficialMessage(message);
                            }
                        })
                .setNegativeButton("CANCELAR",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
        builder.create().show();
    }

    @Override
    public void updateMessage(ChatMessage message) {
        if (adapter == null) adapter = getAdapter();
        adapter.onMessagedChanged(message);
    }

    public void onConfirmOfficialMessage(ChatMessage message) {
        presenter.officialMessageConfirmed(message);
    }

    public void onDenyOfficialMessage(ChatMessage message) {
        presenter.officialMessageDenied(message);
    }


    private void startFullImageActivity(String uri) {
        Intent intent = new Intent(ChatActivity.this, FullscreenActivity.class);
        intent.setData(Uri.parse(uri));
        startActivity(intent);
    }


    @OnClick(R.id.btn_send)
    @Override
    public void sendMessageText() {
        String text = edtMessage.getText().toString();
        if (text.isEmpty()) {
            return;
        }
        if (emojiPopup.isShowing()) {
            emojiPopup.dismiss();
        }

        edtMessage.requestFocus();
        edtMessage.setText("");
        presenter.sendMessageText(text);
        adapter.scrollToLastItem();
    }

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
            //connectionText = String.format(getString(R.string.chat_info_connection_lastseen), SIMPLE_DATE_FORMAT.format(lastConnection));
            connectionText = TimeUtils.calculateLastConnection(lastConnection, new Date().getTime(), getResources());
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

    private void checkPickImagePermissions() {
        ChatActivityPermissionsDispatcher.pickImageWithPermissionCheck(this);
    }

    @NeedsPermission({Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE})
    public void pickImage() {
        presenter.pickImage();
    }

    @Override
    public void startPickImageActivity() {
        Log.d(TAG, "startPickImageActivity: ");
        /*CropImage.activity(null)
                .setGuidelines(CropImageView.Guidelines.ON)
                .setMultiTouchEnabled(true)
                .start(this);*/
        Matisse.from(ChatActivity.this)
                .choose(MimeType.ofImage())
                .countable(true)
                .capture(true)
                .captureStrategy(
                        new CaptureStrategy(true, "com.consultoraestrategia.messengeracademico.fileprovider"))
                .maxSelectable(1)
                .gridExpectedSize(
                        getResources().getDimensionPixelSize(R.dimen.grid_expected_size))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
                .thumbnailScale(0.85f)
                .imageEngine(new GlideEngine())
                .forResult(REQUEST_CODE_CHOOSE_IMAGE);
    }

    @Override
    public void showError(String error) {
        if (TextUtils.isEmpty(error)) return;
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
    public void hideKeboard() {
        // Check if no view has focus:
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void showKeyboard() {

    }

    @Override
    public void toggleMenuWithEffect() {
        Log.d(TAG, "toggleMenuWithEffect: ");
        int cx = mRevealView.getRight();
        int cy = mRevealView.getTop();
        hideKeboard();
        makeEffect(mRevealView, cx, cy);
    }

    @Override
    public void finishActivity() {
        Log.d(TAG, "finishActivity");
        finish();
    }

    @Override
    public void showFatalError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        super.onBackPressed();
    }

    @Override
    public void showAcademicInformation(String data) {
        layoutAcademicInformation.setVisibility(View.VISIBLE);
        txtAcademicInformation.setText(data);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "requestCode: " + requestCode + ", resultCode: " + resultCode);
        presenter.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.linear_StartProfile)
    public void onContactReceptorSelected() {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ImportDataActivity.EXTRA_PHOTO_URI, contact.getPhotoUrl());
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
                checkPickImagePermissions();
                break;
            case R.id.photo_img_btn:
                checkPickImagePermissions();
                break;
            case R.id.video_img_btn:
                presenter.attachVideoClicked();
                break;
            case R.id.audio_img_btn:
                presenter.attachAudioClicked();
                break;
            case R.id.location_img_btn:
                presenter.attachLocationClicked();
                break;
            case R.id.contact_img_btn:
                presenter.attachContactClicked();
                break;
            case R.id.img_camera:
                checkPickImagePermissions();
                break;
        }
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
                edtMessage.clearFocus();
                mRevealView.requestFocus();
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

                edtMessage.clearFocus();
                mRevealView.requestFocus();
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
                        //emojiButton.setImageResource(R.drawable.emoji_one_category_people);
                        emojiButton.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_happy));
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

    @Override
    public void onBottomReached() {
        Log.d(TAG, "onBottomReached");
        counter = 0;
        txtCounter.setVisibility(View.GONE);
        hideButtonToScroll();
    }

    private void hideButtonToScroll() {
        if (layoutCroll.getVisibility() == View.VISIBLE) {
            layoutCroll.setVisibility(View.INVISIBLE);
            layoutCroll.startAnimation(slideDown);
        }
    }

    private void showButtonToScroll() {
        if (layoutCroll.getVisibility() == View.INVISIBLE) {
            layoutCroll.setVisibility(View.VISIBLE);
            layoutCroll.startAnimation(slideUp);
        }
    }

    @Override
    public void onNotBottom() {
        Log.d(TAG, "onNotBottom");
        showButtonToScroll();
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                presenter.actionDelete();
                break;
            default:
                presenter.actionCopy();
                break;
        }
        return true;
    }

}
