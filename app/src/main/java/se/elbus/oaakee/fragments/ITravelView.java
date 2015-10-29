package se.elbus.oaakee.fragments;

import android.os.Parcelable;

public interface ITravelView {
    void saveParcelable(String tag, Parcelable parcelable);

    void nextFragment();
}
