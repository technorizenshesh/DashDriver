package main.com.dashdriver.Fragments;

import android.app.Dialog;
import androidx.databinding.DataBindingUtil;

import android.os.Bundle;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import main.com.dashdriver.Interfaces.onAddPayoutListener;
import main.com.dashdriver.R;
import main.com.dashdriver.constant.BaseUrl;
import main.com.dashdriver.constant.MySession;
import main.com.dashdriver.databinding.FragmentAddPayoutBinding;
import main.com.dashdriver.utils.Tools;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class FragmentAddPayout extends BottomSheetDialogFragment {
    private FragmentAddPayoutBinding binding;
    private MySession mySession;
    private String user_log_data;
    private String user_id;
    private onAddPayoutListener listener;

    public FragmentAddPayout CallBack(onAddPayoutListener listener){
        this.listener=listener;
        return this;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog=(BottomSheetDialog)super.onCreateDialog(savedInstanceState);
        binding= DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.fragment_add_payout,null,false);
        dialog.setContentView(binding.getRoot());
        BindView();
        return dialog;
    }

    private void BindView() {
        mySession = new MySession(getContext());
        user_log_data = mySession.getKeyAlldata();
        if (user_log_data != null) {
            try {
                JSONObject jsonObject = new JSONObject(user_log_data);
                String message = jsonObject.getString("status");
                if (message.equalsIgnoreCase("1")) {
                    JSONObject jsonObject1 = jsonObject.getJSONObject("result");
                    user_id = jsonObject1.getString("id");
                    Log.e("USER NAME"," >>"+jsonObject1.getString("first_name"));

                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        binding.tvDate.setOnClickListener(v->{
            Tools.get().DatePicker(getContext(),binding.tvDate::setText);
        });
        binding.imgBack.setOnClickListener(v->dismiss());
        binding.btnAdd.setOnClickListener(v->{
            if (validation()){
                ApiCallBuilder.build(getActivity()).isShowProgressBar(true)
                        .setUrl(BaseUrl.get().addExpanse()).setParam(getParam())
                        .execute(new ApiCallBuilder.onResponse() {
                            @Override
                            public void Success(String response) {
                                listener.success();
                                dismiss();
                            }

                            @Override
                            public void Failed(String error) {
                                Toast.makeText(getActivity(), ""+error, Toast.LENGTH_SHORT).show();
                            }
                        });
            }
        });

    }
    private HashMap<String,String>getParam(){
        HashMap<String,String>param=new HashMap<>();
        param.put("user_id",user_id);
        param.put("title",binding.etTitle.getText().toString());
        param.put("description",binding.etDescription.getText().toString());
        param.put("amount",binding.etAmount.getText().toString());
        param.put("date",binding.tvDate.getText().toString());
        param.put("time","");
        return param;
    }
    private boolean validation(){
        if (binding.tvDate.getText().toString().isEmpty()){
            Toast.makeText(getContext(), "Select date", Toast.LENGTH_SHORT).show();
            return false;
        }if (binding.etTitle.getText().toString().isEmpty()){
            binding.etTitle.setError(getString(R.string.required));
            binding.etTitle.requestFocus();
            return false;
        }if (binding.etDescription.getText().toString().isEmpty()){
            binding.etDescription.setError(getString(R.string.required));
            binding.etDescription.requestFocus();
            return false;
        }if (binding.etAmount.getText().toString().isEmpty()){
            binding.etAmount.setError(getString(R.string.required));
            binding.etAmount.requestFocus();
            return false;
        }
        return true;
    }
}
