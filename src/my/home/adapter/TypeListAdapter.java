package my.home.adapter;

import java.util.List;

import com.example.musictrainning.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class TypeListAdapter extends ArrayAdapter<String> {
	
	private int selection;

	public TypeListAdapter(Context context, int resource) {
		super(context, resource);
	}

	@Override
	public void add(String object) {
		super.add(object);
	}

	public void setData(List<String> items) {
	    clear();
	    setNotifyOnChange(false);
	    if (items != null) {
	        for (String item : items)
	            add(item);
	    }
	    setNotifyOnChange(true);
	    notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
		}

		String item = getItem(position);
		TextView itemView = (TextView) row;
		itemView.setText(item);
		if(position == selection){
			itemView.setBackgroundColor(Color.GRAY);
			itemView.setTextColor(Color.WHITE);
        }else{
        	itemView.setBackgroundColor(Color.WHITE);
        	itemView.setTextColor(Color.DKGRAY);
        }

		return row;
	}

	public int getSelection() {
		return selection;
	}

	public void setSelection(int selection) {
		this.selection = selection;
	}
}
