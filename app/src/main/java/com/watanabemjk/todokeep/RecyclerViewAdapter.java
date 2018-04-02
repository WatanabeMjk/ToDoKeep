package com.watanabemjk.todokeep;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;


public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private static List<PlanData> noteList ;
    private static final String DATABASE_PLAN_NAME = "PlanListDatabase";
    private Context contextData;
    private String mDivideClickReaction;

    RecyclerViewAdapter(Context context, List<PlanData> planDataList, String divideClickReaction){
        super();
        mLayoutInflater = LayoutInflater.from(context);
        contextData = context;
        noteList = planDataList;
        mDivideClickReaction = divideClickReaction;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.plan_list_item, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        final PlanData note = noteList.get(position);
        String dayOfWeek = note.getDaysOfWeek();
        int backColor = note.getBackColor();
        String date = String.valueOf(note.getYear()) + "/" + String.valueOf(padSpace(note.getMonth())) + "/" + String.valueOf(padSpace(note.getDay()));
        String time = String.valueOf(padSpace(note.getHour())) + ":" + String.valueOf(pad0(note.getMinute()));
        String title = note.getTitle();
        String detail = note.getDetail();

        holder.dayOfWeekTextView.setText(dayOfWeek);
        holder.dateTextView.setText(date);
        holder.timeTextView.setText(time);
        holder.titleTextView.setText(title);
        holder.detailTextView.setText(detail);

        if(title.equals("") ){ holder.titleTextView.setTextSize(0); }
        if(detail.equals("") ){ holder.detailTextView.setTextSize(0); }

        if(!Objects.equals(title, "")){ holder.titleTextView.setTextSize(22); }
        if(!Objects.equals(detail, "")){ holder.detailTextView.setTextSize(22); }

        Log.d("Recycler date : ", date + " / time : " + time + " / title : " + title + " / detail : " + detail + " / backColor : " + backColor + " / dayOfWeek : " + dayOfWeek);
        Log.d("RecyclerCardPosition", position + "");

        holder.cardView.setCardBackgroundColor(ContextCompat.getColor(contextData,backColor));
        if(Objects.equals(mDivideClickReaction, "PlanListFragment")) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    final DatabaseAdapter databaseAdapter = new DatabaseAdapter(contextData, DATABASE_PLAN_NAME);
                    final PlanData note = noteList.get(position);
                    final int noteId = note.getId();
                    final LayoutInflater inflater = LayoutInflater.from(contextData);
                    @SuppressLint("InflateParams") View dateDialogView = inflater.inflate(R.layout.date_dialog,null);
                    final DatePicker datePicker = dateDialogView.findViewById(R.id.datePicker);
                    datePicker.updateDate(note.year,(note.month - 1 ),note.day);

                    new AlertDialog.Builder(contextData)
                            .setView(dateDialogView)
                            .setPositiveButton(
                                    R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            int year = datePicker.getYear();
                                            int month = (datePicker.getMonth() + 1);
                                            int day = datePicker.getDayOfMonth();
                                            Calendar cal = Calendar.getInstance();
                                            cal.set(year, (month - 1 ) , day);
                                            String dayOfWeek;
                                            switch (cal.get(Calendar.DAY_OF_WEEK)) {
                                                case Calendar.SUNDAY:
                                                    dayOfWeek = contextData.getString(R.string.sunday);
                                                    break;
                                                case Calendar.MONDAY:
                                                    dayOfWeek = contextData.getString(R.string.monday);
                                                    break;
                                                case Calendar.TUESDAY:
                                                    dayOfWeek = contextData.getString(R.string.tuesday);
                                                    break;
                                                case Calendar.WEDNESDAY:
                                                    dayOfWeek = contextData.getString(R.string.wednesday);
                                                    break;
                                                case Calendar.THURSDAY:
                                                    dayOfWeek = contextData.getString(R.string.thursday);
                                                    break;
                                                case Calendar.FRIDAY:
                                                    dayOfWeek = contextData.getString(R.string.friday);
                                                    break;
                                                case Calendar.SATURDAY:
                                                    dayOfWeek = contextData.getString(R.string.saturday);
                                                    break;
                                                default:
                                                    Log.d("デフォルト","");
                                                    dayOfWeek = "入力なし";
                                                    break;
                                            }
                                            databaseAdapter.open();
                                            databaseAdapter.editDate(noteId,year,month,day,dayOfWeek);
                                            databaseAdapter.close();
                                            PlanListFragment.loadNote();

                                            @SuppressLint("InflateParams") final View timeDialogView = inflater.inflate(R.layout.time_dialog,null);
                                            final TimePicker timePicker = timeDialogView.findViewById(R.id.timePicker);
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                timePicker.setHour(note.hour);
                                                timePicker.setMinute(note.minute);
                                            }else{
                                                timePicker.setCurrentHour(note.hour);
                                                timePicker.setCurrentMinute(note.minute);
                                            }

                                            new AlertDialog.Builder(contextData)
                                                    .setView(timeDialogView)
                                                    .setPositiveButton(
                                                            R.string.ok,
                                                            new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    int hour = timePicker.getCurrentHour();
                                                                    int minute = timePicker.getCurrentMinute();

                                                                    databaseAdapter.open();
                                                                    databaseAdapter.editTime(noteId, hour,minute);
                                                                    databaseAdapter.close();
                                                                    PlanListFragment.loadNote();

                                                                    @SuppressLint("InflateParams") final View editDialogView = inflater.inflate(R.layout.edit_dialog, null);
                                                                    final EditText titleEdit = editDialogView.findViewById(R.id.titleText);
                                                                    final EditText detailEdit = editDialogView.findViewById(R.id.detailText);
                                                                    final RadioGroup colorRadio = editDialogView.findViewById(R.id.radioGroup);

                                                                    titleEdit.setText(note.getTitle());
                                                                    detailEdit.setText(note.getDetail());

                                                                    int radioId;
                                                                    switch (note.getBackColor()){
                                                                        case R.color.listview_back_white:
                                                                            radioId = R.id.first;
                                                                            break;
                                                                        case R.color.listview_back_red:
                                                                            radioId = R.id.second;
                                                                            break;
                                                                        case R.color.listview_back_blue:
                                                                            radioId = R.id.third;
                                                                            break;
                                                                        case R.color.listview_back_orange:
                                                                            radioId = R.id.fourth;
                                                                            break;
                                                                        case R.color.listview_back_green:
                                                                            radioId = R.id.fifth;
                                                                            break;
                                                                        case R.color.listview_back_pink:
                                                                            radioId = R.id.sixth;
                                                                            break;
                                                                        case R.color.listview_back_yellow:
                                                                            radioId = R.id.seventh;
                                                                            break;
                                                                        default:
                                                                            Log.d("デフォルト","");
                                                                            radioId = R.id.seventh;
                                                                            break;
                                                                    }
                                                                    final RadioButton radioButton = editDialogView.findViewById(radioId);
                                                                    radioButton.setChecked(true);

                                                                    new AlertDialog.Builder(contextData)
                                                                            .setView(editDialogView)
                                                                            .setMessage(R.string.make_plan)
                                                                            .setPositiveButton(
                                                                                    R.string.ok,
                                                                                    new DialogInterface.OnClickListener() {
                                                                                        @Override
                                                                                        public void onClick(DialogInterface dialog, int which) {
                                                                                            String title = titleEdit.getText().toString();
                                                                                            String detail = detailEdit.getText().toString();
                                                                                            int radioId = colorRadio.getCheckedRadioButtonId();
                                                                                            databaseAdapter.open();
                                                                                            databaseAdapter.editPlan(noteId, title, detail, radioId);
                                                                                            databaseAdapter.close();
                                                                                            PlanListFragment.loadNote();
                                                                                        }
                                                                                    }
                                                                            )
                                                                            .setNegativeButton(R.string.cancel, null)
                                                                            .show();
                                                                }
                                                            }
                                                    )
                                                    .setNegativeButton(R.string.cancel, null)
                                                    .show();
                                        }
                                    }
                            )
                            .setNegativeButton(R.string.cancel, null)
                            .show();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView dayOfWeekTextView;
        TextView dateTextView;
        TextView timeTextView;
        TextView titleTextView;
        TextView detailTextView;
        CardView cardView;

        ViewHolder(View view){
            super(view);
            dayOfWeekTextView = view.findViewById(R.id.dayOfWeekText);
            dateTextView = view.findViewById(R.id.dateText);
            timeTextView = view.findViewById(R.id.timeText);
            titleTextView = view.findViewById(R.id.titleText);
            detailTextView = view.findViewById(R.id.detailText);
            cardView = view.findViewById(R.id.schedule_data);
        }
    }

    private String pad0(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "0" + String.valueOf(c);
    }

    private String padSpace(int c) {
        if (c >= 10)
            return String.valueOf(c);
        else
            return "  " + String.valueOf(c);
    }
}
