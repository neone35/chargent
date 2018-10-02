package com.github.neone35.chargent;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.neone35.chargent.model.Car;
import com.orhanobut.logger.Logger;

import org.parceler.Parcels;

import java.util.ArrayList;

public class CarListFragment extends Fragment {

    private static final String ARG_COLUMN_COUNT = "column-count";
    private static final String ARG_CAR_PARCELABLE_LIST = "car-parcelable-list";
    private int mColumnCount = 1;
    private ArrayList<Parcelable> carListParcelableList;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public CarListFragment() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    static CarListFragment newInstance(int columnCount, ArrayList<Car> carList) {
        CarListFragment fragment = new CarListFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        ArrayList<Parcelable> carParcelableList = new ArrayList<>();
        for (int i = 0; i < carList.size(); i++) {
            Parcelable carParcelable = Parcels.wrap(carList.get(i));
            carParcelableList.add(carParcelable);
        }
        args.putParcelableArrayList(ARG_CAR_PARCELABLE_LIST, carParcelableList);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
            carListParcelableList = getArguments().getParcelableArrayList(ARG_CAR_PARCELABLE_LIST);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_car_list, container, false);

        ArrayList<Car> carList = new ArrayList<>();
        for (int i = 0; i < carListParcelableList.size(); i++) {
            Car car = Parcels.unwrap(carListParcelableList.get(i));
            carList.add(car);
        }

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new CarListAdapter(carList, this.getActivity()));
        }
        return view;
    }
}
