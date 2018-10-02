package com.github.neone35.chargent;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.neone35.chargent.model.Car;

import java.util.List;

public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.ViewHolder> {

    private final List<Car> mCars;

    CarListAdapter(List<Car> cars) {
        mCars = cars;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_car_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        holder.mCar = mCars.get(position);
        holder.mIdView.setText(String.valueOf(mCars.get(position).getId()));
        holder.mContentView.setText(mCars.get(position).getModel().getTitle());
    }

    @Override
    public int getItemCount() {
        return mCars.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        @BindView(R.id.item_number)
        TextView mIdView;
        @BindView(R.id.content)
        TextView mContentView;
        Car mCar;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + mContentView.getText() + "'";
        }
    }
}
