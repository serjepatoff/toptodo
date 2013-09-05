package com.example.toptodo;

import java.util.Date;
import java.util.List;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import java.text.DateFormat;

public class TodoListAdapter extends ArrayAdapter<TodoItem> {
  private Context context;
  private List<TodoItem> listOfItems;
  private LayoutInflater inflater;
  private final DateFormat dformat = DateFormat.getDateTimeInstance();

  public TodoListAdapter(Context context, List<TodoItem> listOfItems ) {
	  super( context, R.layout.row_layout, listOfItems );
	  this.context = context;
	  this.listOfItems = listOfItems;
	  inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    View rowView = convertView!=null ? convertView : inflater.inflate(R.layout.row_layout, parent, false);
    
    UrgencyView uv = (UrgencyView) rowView.findViewById(R.id.listIcon);
    TextView header = (TextView) rowView.findViewById(R.id.listHeader);
    TextView detail1 = (TextView) rowView.findViewById(R.id.listDetail1);
    TextView detail2 = (TextView) rowView.findViewById(R.id.listDetail2);
    
    TodoItem item = listOfItems.get(position);
    rowView.findViewById(R.id.itemDeleteIcon).setTag(item);
    header.setText(item.getHeader());
    
    detail1.setText(item.getDetails());
    Date d = item.getDueDate();
    detail2.setText(d==null? "" : dformat.format(item.getDueDate()));  
    uv.setUrgency(item.getUrgency());
    
    if(item.getClosed()){
    	header.setPaintFlags(header.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    	detail1.setPaintFlags(header.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
    	detail2.setPaintFlags(header.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
	}else{
		header.setPaintFlags(header.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		detail1.setPaintFlags(header.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
		detail2.setPaintFlags(header.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
	}

    return rowView;
  }
}