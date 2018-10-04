package com.github.neone35.chargent;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;

import com.appyvet.materialrangebar.RangeBar;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import butterknife.BindView;
import butterknife.ButterKnife;

public class FilterDialogFragment extends DialogFragment {

    private static final String ARG_TITLE_ID = "dialog-message";
    private static final String ARG_ENABLED = "is-enabled";
    private static final String ARG_TICK_START = "tick-start";
    private static final String ARG_TICK_END = "tick-end";
    private int mTitleID;
    private boolean mIsEnabled = false;
    private int mTickStart;
    private int mTickEnd;
    private Context mCtx;
    private FilterDialogListener mListener;
    @BindView(R.id.cb_enabled)
    CheckBox cbEnabled;
    @BindView(R.id.rb_filter)
    RangeBar rbFilter;

    /* The activity that creates an instance of this dialog fragment must
     * implement this interface in order to receive event callbacks.
     * Each method passes the DialogFragment in case the host needs to query it. */
    public interface FilterDialogListener {
        void onDialogPositiveClick(DialogFragment dialog, int titleID, boolean isEnabled);
        void onDialogNegativeClick(DialogFragment dialog);
    }

    static FilterDialogFragment newInstance(int titleID, boolean isEnabled,
                                            int tickStart, int tickEnd) {
        FilterDialogFragment fragment = new FilterDialogFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_TITLE_ID, titleID);
        args.putBoolean(ARG_ENABLED, isEnabled);
        args.putInt(ARG_TICK_START, tickStart);
        args.putInt(ARG_TICK_END, tickEnd);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        mCtx = this.getActivity();
        if (getArguments() != null) {
            mTitleID = getArguments().getInt(ARG_TITLE_ID);
            mIsEnabled = getArguments().getBoolean(ARG_ENABLED);
            mTickStart = getArguments().getInt(ARG_TICK_START);
            mTickEnd = getArguments().getInt(ARG_TICK_END);
        }
        super.onCreate(savedInstanceState);
    }

    @NonNull
    @Override
    @SuppressLint("InflateParams")
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        LayoutInflater inflater = Objects.requireNonNull(getActivity()).getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.fragment_filter_dialog, null);
        ButterKnife.bind(this, dialogView);
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        builder.setView(dialogView)
                .setTitle(getString(mTitleID))
                .setPositiveButton("OK", (dialogInterface, id) ->
                        mListener.onDialogPositiveClick(this, mTitleID, cbEnabled.isChecked()))
                .setNegativeButton("Cancel", (dialogInterface, i) ->
                        mListener.onDialogNegativeClick(this));

        setUpCheckbox();

        return builder.create();
    }

    private void setUpCheckbox() {
        // update checkbox and rangebar state on creation
        if (!mIsEnabled) {
            cbEnabled.setChecked(false);
            rbFilter.setEnabled(false);
        } else {
            cbEnabled.setChecked(true);
            rbFilter.setEnabled(true);
        }
        // listen for checbox click
        cbEnabled.setOnCheckedChangeListener((compoundButton, checked) -> {
            if (checked) rbFilter.setEnabled(true);
            else rbFilter.setEnabled(false);
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (FilterDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(mCtx.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
