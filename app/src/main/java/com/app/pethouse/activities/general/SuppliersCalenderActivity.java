package com.app.pethouse.activities.general;

import com.app.pethouse.R;
import com.app.pethouse.activities.owener.PayActivity;
import com.app.pethouse.callback.OrderCareCallback;
import com.app.pethouse.controller.OrderCareController;
import com.app.pethouse.model.OrderCareModel;
import com.app.pethouse.model.UserModel;
import com.app.pethouse.utils.LoadingHelper;
import com.app.pethouse.utils.SharedData;
import com.roomorama.caldroid.CaldroidFragment;
import com.roomorama.caldroid.CaldroidListener;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class SuppliersCalenderActivity extends AppCompatActivity {

    private UserModel supplier = new UserModel();
    ArrayList<OrderCareModel> allOrders = new ArrayList<>();

    CalendarView calendar;
    CaldroidFragment caldroidFragment = new CaldroidFragment();
    final SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
    Date redDate, darkBlueDate, yellowDate, greenGrayDate = null;
    ArrayList<Date> bookedDays = new ArrayList<>();
    ArrayList<Date> ChoosedDays = new ArrayList<>();
    ColorDrawable red, darkBlue, yellow, greenGray, white;
    Button book;
    LoadingHelper loadingHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_calender);
        calendar = findViewById(R.id.calendar);
        darkBlue = new ColorDrawable(getResources().getColor(R.color.darkBlue));
        red = new ColorDrawable(getResources().getColor(R.color.red));
        yellow = new ColorDrawable(getResources().getColor(R.color.yellow));
        greenGray = new ColorDrawable(getResources().getColor(R.color.greenGray));
        white = new ColorDrawable(getResources().getColor(R.color.white));
        loadingHelper = new LoadingHelper(this);
        book= findViewById(R.id.book);


        book.setOnClickListener(v -> {
            if(ChoosedDays.size() > 0 ){
                OrderCareModel newOrder = new OrderCareModel();
                newOrder.setSupplier(supplier);
                newOrder.setDays(ChoosedDays);
                newOrder.setCreatedAt(Calendar.getInstance().getTime());
                newOrder.setTotalPrice(supplier.getPrice() * ChoosedDays.size());
                if(SharedData.userType == 2) {
                    newOrder.setState(2);
                    loadingHelper.showLoading("");
                    new OrderCareController().save(newOrder, new OrderCareCallback() {
                        @Override
                        public void onSuccess(ArrayList<OrderCareModel> orderCares) {
                            loadingHelper.dismissLoading();
                            onBackPressed();
                        }

                        @Override
                        public void onFail(String error) {
                            loadingHelper.dismissLoading();
                            Toast.makeText(SuppliersCalenderActivity.this, error, Toast.LENGTH_LONG).show();
                        }
                    });
                } else if(SharedData.userType == 3) {
                    newOrder.setState(0);
                    newOrder.setOwener(SharedData.owner);
                    SharedData.currentOrder = newOrder;
                    ChoosedDays = new ArrayList<>();
                    Intent intent = new Intent(SuppliersCalenderActivity.this, PayActivity.class);
                    startActivity(intent);
                }
            }
        });

        getData();
    }

    @Override
    protected void onResume() {
        getData();
        super.onResume();
    }

    private void getData() {
        supplier = SharedData.supplier;
        new OrderCareController().getOrders(new OrderCareCallback() {
            @Override
            public void onSuccess(ArrayList<OrderCareModel> orderCares) {
                allOrders = new ArrayList<>();
                for(OrderCareModel order : orderCares) {
                    if(order.getSupplier().getKey().equals(supplier.getKey()))
                        allOrders.add(order);
                }
                setDataOnCalendar();
            }
            @Override
            public void onFail(String error) {
                Toast.makeText(SuppliersCalenderActivity.this, error, Toast.LENGTH_LONG).show();
            }
        });
        checkChoosedDays();
    }

    @SuppressLint("DefaultLocale")
    private void setDataOnCalendar() {
        Bundle args = new Bundle();
        Calendar cal = Calendar.getInstance();
        args.putInt(CaldroidFragment.MONTH, cal.get(Calendar.MONTH) + 1);
        args.putInt(CaldroidFragment.YEAR, cal.get(Calendar.YEAR));
        args.putInt(CaldroidFragment.START_DAY_OF_WEEK, CaldroidFragment.SATURDAY); //first day of week SATURDAY
        caldroidFragment.setArguments(args);
        caldroidFragment.setMinDate(cal.getTime());
        FragmentTransaction t = getSupportFragmentManager().beginTransaction();
        t.replace(R.id.calendar, caldroidFragment);
        t.commit();
        colorDate();
    }

    private void colorDate() {
        for (int i = 0; i < allOrders.size(); i++) {
            int state = allOrders.get(i).getState();
            for(Date day : allOrders.get(i).getDays()) {
                long l = day.getTime();
                caldroidFragment.setBackgroundDrawableForDate(white, new Date(l));
                if (state == 1) { // accept
                    redDate = new Date(l);
                    bookedDays.add(redDate);
                    caldroidFragment.setBackgroundDrawableForDate(red, redDate);
                    caldroidFragment.setTextColorForDate(R.color.white, redDate);
                } else if (state == 2) { // Maintenance
                    darkBlueDate = new Date(l);
                    bookedDays.add(darkBlueDate);
                    caldroidFragment.setBackgroundDrawableForDate(darkBlue, darkBlueDate);
                    caldroidFragment.setTextColorForDate(R.color.white, darkBlueDate);
                } else if (state == 0) // Waiting
                {
                    yellowDate = new Date(l);
                    bookedDays.add(yellowDate);
                    caldroidFragment.setBackgroundDrawableForDate(yellow, yellowDate);
                    caldroidFragment.setTextColorForDate(R.color.white, yellowDate);
                }
                for (int n = 0; n < ChoosedDays.size(); n++) {
                    greenGrayDate = ChoosedDays.get(n);
                    caldroidFragment.setBackgroundDrawableForDate(greenGray, greenGrayDate);
                    //caldroidFragment.setTextColorForDate(R.color.white, greenGrayDate);
                }
            }
        }
        caldroidFragment.refreshView();
        caldroidFragment.setCaldroidListener(listener);
    }

    private void ColorChoosed(Date date, int state) {
        if (state == 0) {
            caldroidFragment.setBackgroundDrawableForDate(white, date);
            caldroidFragment.setTextColorForDate(R.color.caldroid_black, date);
        } else {
            for (int n = 0; n < ChoosedDays.size(); n++) {
                greenGrayDate = ChoosedDays.get(n);
                caldroidFragment.setBackgroundDrawableForDate(greenGray, greenGrayDate);
                caldroidFragment.setTextColorForDate(R.color.caldroid_black, greenGrayDate);
            }
        }
        caldroidFragment.refreshView();
    }

    private void checkChoosedDays() {
        if(SharedData.userType== 3 && ChoosedDays.size() > 0){
            book.setVisibility(View.VISIBLE);
        } else if(SharedData.userType== 2 && ChoosedDays.size() > 0){
            book.setVisibility(View.VISIBLE);
            book.setText("Make Days Unavailable");
        } else{
            book.setVisibility(View.GONE);
        }
    }

    final CaldroidListener listener = new CaldroidListener() {
        @Override
        public void onSelectDate(Date date, View view) {
            //Toast.makeText(getApplicationContext(), formatter.format(date), Toast.LENGTH_SHORT).show();
            if(SharedData.userType == 1) {
                return;
            }
            Boolean IsBooked = false, IsChoosed = false;
            for (int i = 0; i < bookedDays.size(); i++) {
                if (formatter.format(bookedDays.get(i)).equals(formatter.format(date))) {
                    IsBooked = true;
                }
            }

            for (int n = 0; n < ChoosedDays.size(); n++) {
                if (formatter.format(ChoosedDays.get(n)).equals(formatter.format(date))) {
                    IsChoosed = true;
                    ColorChoosed(ChoosedDays.get(n), 0);
                    ChoosedDays.remove(n);
                }
            }

            if (!IsBooked) {
                if (!IsChoosed) {
                    ChoosedDays.add(date);
                    ColorChoosed(date, 1);
                    //dialogPay(date);
                }
            }

            checkChoosedDays();
        }
    };

}