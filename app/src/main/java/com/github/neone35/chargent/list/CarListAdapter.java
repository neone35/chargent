package com.github.neone35.chargent.list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.github.neone35.chargent.R;
import com.github.neone35.chargent.model.Car;
import com.orhanobut.logger.Logger;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import butterknife.BindView;
import butterknife.ButterKnife;

public class CarListAdapter extends RecyclerView.Adapter<CarListAdapter.ViewHolder> {

    private final List<Car> mCars;
    private Context mCtx;

    CarListAdapter(List<Car> cars, Context ctx) {
        mCars = cars;
        mCtx = ctx;
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
        Car car = holder.mCar = mCars.get(position);

        // load car image
        String imgUrl = car.getModel().getPhotoUrl();
        try {
            Glide.with(holder.ivCarPhoto)
                    .load(imgUrl)
                    .into(holder.ivCarPhoto);
        } catch (Exception e) {
            Logger.e(e.getMessage());
        }
        // load other view data
        holder.tvCarTitle.setText(car.getModel().getTitle());
        holder.tvCarPlate.setText(car.getPlateNumber());
        holder.tvCarAddress.setText(car.getLocation().getAddress());
        holder.tvBatteryCharge.setText(String.format("%s%%", String.valueOf(car.getBatteryPercentage())));
        holder.tvBatteryDistance.setText(String.format("%skm", String.valueOf(car.getBatteryEstimatedDistance())));
        if (car.isIsCharging())
            holder.tvCharging.setText(mCtx.getResources().getString(R.string.yes));
        else
            holder.tvCharging.setText(mCtx.getResources().getString(R.string.no));

    }

    @Override
    public int getItemCount() {
        return mCars.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        @BindView(R.id.iv_car_photo)
        ImageView ivCarPhoto;
        @BindView(R.id.tv_car_title)
        TextView tvCarTitle;
        @BindView(R.id.tv_car_plate)
        TextView tvCarPlate;
        @BindView(R.id.tv_car_address)
        TextView tvCarAddress;
        @BindView(R.id.tv_battery_charge)
        TextView tvBatteryCharge;
        @BindView(R.id.tv_battery_distance)
        TextView tvBatteryDistance;
        @BindView(R.id.tv_charging)
        TextView tvCharging;

        Car mCar;

        ViewHolder(View view) {
            super(view);
            mView = view;
            ButterKnife.bind(this, view);
        }
    }
}
