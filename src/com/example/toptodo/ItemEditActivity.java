package com.example.toptodo;

import java.util.Calendar;
import java.util.Date;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.content.Context;
import android.content.Intent;
import android.media.ExifInterface;
import android.os.Bundle;
import android.text.InputType;
import android.text.format.DateFormat;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.DateSorter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class ItemEditActivity extends FragmentActivity {

	private Intent incomingIntent;
	EditText dateEdit;
	Date dateSaved = new Date();
	private int editPosition;
	private int datebarrier;
	private int timebarrier;
	private int yearSaved;
	private int monthSaved;
	private int daySaved;
	private int hourSaved;
	private int minuteSaved;
	private TodoItem incomingItem;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edititem);
		
		dateEdit = (EditText)findViewById(R.id.due_date_edit);
		dateEdit.setInputType(InputType.TYPE_NULL);
		dateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            	datebarrier=timebarrier=0;
                pickDate();
            }
        });
		
		incomingIntent = getIntent();
		editPosition = incomingIntent.getIntExtra(getString(R.string.position_key), -1);
		if ( editPosition>=0 && incomingIntent!=null ) {
			incomingItem = (TodoItem)incomingIntent.getSerializableExtra(getString(R.string.attachment_key));
			((EditText)findViewById(R.id.header_edit)).setText(incomingItem.getHeader());
			((EditText)findViewById(R.id.details_edit)).setText(incomingItem.getDetails());
			dateSaved = incomingItem.getDueDate();
			updateDate(dateSaved);
			((SeekBar)findViewById(R.id.urgency_edit)).setProgress(incomingItem.getUrgency());
		}
		else {
			incomingItem = new TodoItem();
		}
	}
	
	private void updateDate(Date d) {
		String dd = java.text.DateFormat.getDateTimeInstance().format(d);
		((EditText)findViewById(R.id.due_date_edit)).setText(dd);
	}

	public void onSaveClicked(final View v) {
		Intent data = new Intent();
		Bundle bun = new Bundle();
		String vHeader =  ((EditText)findViewById(R.id.header_edit)).getText().toString();
		String vDetails = ((EditText)findViewById(R.id.details_edit)).getText().toString();
		incomingItem.setHeader(vHeader);
		incomingItem.setDetails(vDetails);
		incomingItem.setUrgency(((SeekBar)findViewById(R.id.urgency_edit)).getProgress());
		incomingItem.setDueDate(dateSaved);
		bun.putSerializable(getString(R.string.attachment_key), incomingItem);
		data.putExtras( bun );
		finishWithResult(data);
	}

	public void onDiscardClicked( final View v) {
		finishWithResult(null);
	}
	
	private void finishWithResult( Intent data ) {
		if ( data != null ) {
			if (getParent() == null) {
			    setResult(Activity.RESULT_OK, data);
			} else {
			    getParent().setResult(Activity.RESULT_OK, data);
			}
		}
		finish();
	}
	
	public class DatePickerFragment extends DialogFragment implements
	    DatePickerDialog.OnDateSetListener {
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		    // Use the current date as the default date in the picker
		    final Calendar c = Calendar.getInstance();
		    int year = c.get(Calendar.YEAR);
		    int month = c.get(Calendar.MONTH);
		    int day = c.get(Calendar.DAY_OF_MONTH);
		
		    // Create a new instance of DatePickerDialog and return it
		    return new DatePickerDialog(getActivity(), this, year, month, day);
		}
		
		public void onDateSet(DatePicker view, int year, int month, int day) {
			if ( datebarrier++==0 ) {
				yearSaved = year;
				monthSaved = month;
				daySaved = day;
				pickTime();
			}
		}
	}
	
	public class TimePickerFragment extends DialogFragment implements
	    TimePickerDialog.OnTimeSetListener {
	
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar c = Calendar.getInstance();
			int hour = c.get(Calendar.HOUR_OF_DAY);
			int minute = c.get(Calendar.MINUTE);
		
		    return new TimePickerDialog(getActivity(), this, hour, minute,
		            DateFormat.is24HourFormat(getActivity()));
		}
		
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			if ( timebarrier++==0 ) {
				hourSaved = hourOfDay;
				minuteSaved = minute;
				dateSaved = new Date(yearSaved-1900,monthSaved,daySaved,hourSaved,minuteSaved,0);
				updateDate(dateSaved);
			}
		}
	}
	
	public void pickDate() {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }
	
	public void pickTime() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }
}
