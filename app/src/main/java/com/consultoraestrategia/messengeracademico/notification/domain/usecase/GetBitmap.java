package com.consultoraestrategia.messengeracademico.notification.domain.usecase;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.consultoraestrategia.messengeracademico.UseCase;

/**
 * Created by @stevecampos on 3/07/2017.
 */

public class GetBitmap extends UseCase<GetBitmap.RequestValues, GetBitmap.ResponseValue> {

    private static final String TAG = GetBitmap.class.getSimpleName();

    @Override
    protected void executeUseCase(RequestValues requestValues) {

        Context context = requestValues.getContext();
        String uri = requestValues.getUri();
        if (context != null && uri != null) {

            try {
                Resources res = context.getResources();
                int height = (int) res.getDimension(android.R.dimen.notification_large_icon_height);
                int width = (int) res.getDimension(android.R.dimen.notification_large_icon_width);
                Bitmap bitmap = Glide.with(context)
                        .load(uri)
                        .asBitmap()
                        .centerCrop()
                        .into(height, width)
                        .get();
                onSucess(bitmap);
            } catch (Exception e) {
                onError(e);
            }
        } else {
//            onError(new Exception("context can't be null!!!"));
            onSucess(null);
        }
    }

    private void onSucess(Bitmap bitmap) {
        getUseCaseCallback().onSuccess(new ResponseValue(bitmap));
    }

    private void onError(Exception e) {
        Log.d(TAG, "onError: " + e.getMessage());
    }

    public static final class RequestValues implements UseCase.RequestValues {
        private String uri;
        private Context context;

        public RequestValues(String uri, Context context) {
            this.uri = uri;
            this.context = context;
        }

        public String getUri() {
            return uri;
        }

        public Context getContext() {
            return context;
        }
    }

    public static final class ResponseValue implements UseCase.ResponseValue {
        private Bitmap bitmap;

        public ResponseValue(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        public Bitmap getBitmap() {
            return bitmap;
        }
    }
}
