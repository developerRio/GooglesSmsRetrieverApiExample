package com.example.googlessmsretrieverapi;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.api.Status;

import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.ListIterator;

import kotlin.TypeCastException;
import kotlin.collections.CollectionsKt;
import kotlin.jvm.internal.Intrinsics;
import kotlin.text.Regex;
import kotlin.text.StringsKt;

public class SmsBroadcastReceiver extends BroadcastReceiver {
    private SmsBroadcastReceiver.OTPReceiveListener otpReceiver;

    public final void initOTPListener(@NotNull SmsBroadcastReceiver.OTPReceiveListener receiver) {
        Intrinsics.checkParameterIsNotNull(receiver, "receiver");
        this.otpReceiver = receiver;
    }

    public void onReceive(@NotNull Context context, @NotNull Intent intent) {
        Intrinsics.checkParameterIsNotNull(context, "context");
        Intrinsics.checkParameterIsNotNull(intent, "intent");
        if (Intrinsics.areEqual("com.google.android.gms.auth.api.phone.SMS_RETRIEVED", intent.getAction())) {
            Bundle extras = intent.getExtras();
            if (extras == null) {
                Intrinsics.throwNpe();
            }

            Object var10000 = extras.get("com.google.android.gms.auth.api.phone.EXTRA_STATUS");
            if (var10000 == null) {
                throw new TypeCastException("null cannot be cast to non-null type com.google.android.gms.common.api.Status");
            }

            Status status = (Status) var10000;
            SmsBroadcastReceiver.OTPReceiveListener var17;
            switch (status.getStatusCode()) {
                case 0:
                    var10000 = extras.get("com.google.android.gms.auth.api.phone.EXTRA_SMS_MESSAGE");
                    if (var10000 == null) {
                        throw new TypeCastException("null cannot be cast to non-null type kotlin.String");
                    }

                    String otp = (String) var10000;
                    if (this.otpReceiver != null) {
                        List var18;
                        label54:
                        {
                            CharSequence var6 = (CharSequence) StringsKt.replace(otp, "<#> Your Otp is: ", "", false);
                            String var7 = "\n";
                            Regex var14 = new Regex(var7);
                            byte var8 = 0;
                            List $receiver$iv = var14.split(var6, var8);
                            if (!$receiver$iv.isEmpty()) {
                                ListIterator iterator$iv = $receiver$iv.listIterator($receiver$iv.size());

                                while (iterator$iv.hasPrevious()) {
                                    String it = (String) iterator$iv.previous();
                                    CharSequence var9 = (CharSequence) it;
                                    if (var9.length() != 0) {
                                        var18 = CollectionsKt.take((Iterable) $receiver$iv, iterator$iv.nextIndex() + 1);
                                        break label54;
                                    }
                                }
                            }

                            var18 = CollectionsKt.emptyList();
                        }

                        Collection $receiver$iv = (Collection) var18;
                        if ($receiver$iv == null) {
                            throw new TypeCastException("null cannot be cast to non-null type java.util.Collection<T>");
                        }

                        Object[] var19 = $receiver$iv.toArray(new String[$receiver$iv.size()]);
                        if (var19 == null) {
                            throw new TypeCastException("null cannot be cast to non-null type kotlin.Array<T>");
                        }

                        otp = ((String[]) var19)[0];
                        var17 = this.otpReceiver;
                        if (this.otpReceiver == null) {
                            Intrinsics.throwNpe();
                        }

                        var17.onOTPReceived(otp);
                    }
                    break;
                case 15:
                    var17 = this.otpReceiver;
                    if (this.otpReceiver == null) {
                        Intrinsics.throwNpe();
                    }

                    var17.onOTPTimeOut();
            }
        }

    }

    public interface OTPReceiveListener {
        void onOTPReceived(@NotNull String var1);

        void onOTPTimeOut();
    }
}
