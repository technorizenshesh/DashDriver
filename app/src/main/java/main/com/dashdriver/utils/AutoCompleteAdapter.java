package main.com.dashdriver.utils;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import main.com.dashdriver.Interfaces.onAutocompleteAddressListener;
import main.com.dashdriver.R;
import www.develpoeramit.mapicall.ApiCallBuilder;

public class AutoCompleteAdapter extends BaseAdapter implements Filterable {
    private Location location;
    Context mContext;
    String query;
    ArrayList<ModelAutoAddress>list=new ArrayList<>();
    private onAutocompleteAddressListener listener;
    private int type;

    public AutoCompleteAdapter(Context mContext, Location location) {
        this.mContext = mContext;
        this.location=location;
    }
    public AutoCompleteAdapter setCallback(int type, onAutocompleteAddressListener listener){
        this.listener=listener;
        this.type=type;
        return this;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position).getDescription();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view= LayoutInflater.from(mContext).inflate(R.layout.autocomplete_layout,parent,false);
        TextView tv_title=view.findViewById(R.id.tv_title);
        TextView tv_description=view.findViewById(R.id.tv_description);
        tv_title.setText(list.get(position).getTitle());
        tv_description.setText(list.get(position).getDescription());
        view.setOnClickListener(v->listener.onSelectAddress(type,list.get(position)));
        return view;
    }

    @Override
    public Filter getFilter() {
       Filter filter= new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    String URL = "https://maps.googleapis.com/maps/api/place/autocomplete/json?key=AIzaSyBXvrm0wKFaamcHvScRaQ2_Oi9lZw8if6k&input=" + constraint + "&location=" + location.getLatitude() + "," + location.getLongitude() + "+&radius=1000&types=geocode&sensor=true";
                    ApiCallBuilder.build(mContext).setUrl(URL).execute(new ApiCallBuilder.onResponse() {
                        @Override
                        public void Success(String response) {
                            Log.e("ArrayData","=====>"+response);
                            try {
                                list = new ArrayList<>();
                                JSONObject jk = new JSONObject(response);
                                JSONArray predictions = jk.getJSONArray("predictions");
                                for (int i = 0; i < predictions.length(); i++) {
                                    JSONObject js = predictions.getJSONObject(i);
                                    ModelAutoAddress address=new ModelAutoAddress();
                                    address.setDescription(js.getString("description"));
                                    address.setTitle(js.getJSONObject("structured_formatting").getString("main_text"));
                                    list.add(address);
                                }
                                filterResults.values=list;
                                filterResults.count=list.size();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void Failed(String error) {

                        }
                    });
                }
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (constraint != null && !constraint.equals(query)) {
                    query=constraint.toString();
                    notifyDataSetChanged();
                }
            }
        };
        return filter;
    }

}
