package main.com.dashdriver.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import main.com.dashdriver.R;
import main.com.dashdriver.activity.CashOutAct;
import main.com.dashdriver.paymentclasses.MyCardsPayment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentAddAmount extends Fragment {
    private String amount_str="0";
    private View view;

    public FragmentAddAmount() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
         view=inflater.inflate(R.layout.fragment_add_amount, container, false);
        bindView();
        return view;
    }

    private void bindView() {
        EditText amountEt=view.findViewById(R.id.et_amount);
        TextView fiftyBut=view.findViewById(R.id.fifty_but);
        TextView hundredBut=view.findViewById(R.id.hundred_but);
        TextView onefiftyBut=view.findViewById(R.id.onefifty_but);
        TextView addmoney=view.findViewById(R.id.addmoney);
        TextView tvWithdraw=view.findViewById(R.id.tv_withdraw);
        tvWithdraw.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), CashOutAct.class);
                startActivity(i);
            }
        });
        addmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amount_str = amountEt.getText().toString();
                if (amount_str.equalsIgnoreCase("")){
                    Toast.makeText(getActivity(),getResources().getString(R.string.enteramount),Toast.LENGTH_LONG).show();
                }
                else {
                    Intent i = new Intent(getActivity(), MyCardsPayment.class);
                    i.putExtra("amount_str",amount_str);
                    startActivity(i);

                }
            }
        });
        fiftyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEt.setText("50");
                fiftyBut.setBackgroundResource(R.drawable.border_yellowrounddrab);
                hundredBut.setBackgroundResource(R.drawable.border_grey_rec);
                onefiftyBut.setBackgroundResource(R.drawable.border_grey_rec);

            }
        });
        hundredBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEt.setText("100");
                hundredBut.setBackgroundResource(R.drawable.border_yellowrounddrab);
                onefiftyBut.setBackgroundResource(R.drawable.border_grey_rec);
                fiftyBut.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });

        onefiftyBut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                amountEt.setText("150");
                onefiftyBut.setBackgroundResource(R.drawable.border_yellowrounddrab);
                hundredBut.setBackgroundResource(R.drawable.border_grey_rec);
                fiftyBut.setBackgroundResource(R.drawable.border_grey_rec);
            }
        });

    }
}
