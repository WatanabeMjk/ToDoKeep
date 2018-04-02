package com.watanabemjk.todokeep;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.github.brnunes.swipeablerecyclerview.SwipeableRecyclerViewTouchListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PlanListFragment extends Fragment {
    private static int m_year;
    private static int m_month;
    private static int m_day;
    private static int m_hour;
    private static int m_minute;
    static final String DATABASE_PLAN_NAME = "PlanListDatabase";
    static final String DATABASE_ARCHIVE_NAME = "ArchiveListDatabase";
    static final String DATABASE_TRASH_NAME = "TrashListDatabase";
    static DatabaseAdapter PlanDatabaseAdapter;
    static DatabaseAdapter ArchiveDatabaseAdapter;
    static DatabaseAdapter TrashDatabaseAdapter;
    static List<PlanData> noteList = new ArrayList<>();
    static RecyclerView.Adapter mAdapter;
    View root;

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PlanDatabaseAdapter = new DatabaseAdapter(getActivity(), DATABASE_PLAN_NAME);
        ArchiveDatabaseAdapter = new DatabaseAdapter(getActivity(),DATABASE_ARCHIVE_NAME);
        TrashDatabaseAdapter = new DatabaseAdapter(getActivity(),DATABASE_TRASH_NAME);
        mAdapter = new RecyclerViewAdapter(getActivity(),noteList,"PlanListFragment");

        root = inflater.inflate(R.layout.plan_list_fragment, container, false);

        FloatingActionButton plusBtn = root.findViewById(R.id.FAB);
        plusBtn.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff3838")));
        plusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DateDialog dateDialog = new DateDialog();
                dateDialog.setTargetFragment(PlanListFragment.this,1);
                dateDialog.show(getFragmentManager(), "test");
            }
        });
        setHasOptionsMenu(true);
        loadNote();
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
        RecyclerView mRecyclerView = root.findViewById(R.id.my_recycler_view);
        mRecyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        SwipeableRecyclerViewTouchListener swipeTouchListener = new SwipeableRecyclerViewTouchListener(mRecyclerView, new SwipeableRecyclerViewTouchListener.SwipeListener() {
            @Override
            public boolean canSwipe(int position) {
                return true;
            }

            @Override
            public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                PlanDatabaseAdapter.open();
                ArchiveDatabaseAdapter.open();
                for (int position : reverseSortedPositions) {
                    PlanData removeNote = noteList.get(position);
                    ArchiveDatabaseAdapter.saveNote(removeNote.getYear(), removeNote.getMonth(), removeNote.getDay(), removeNote.getHour(), removeNote.getMinute(), removeNote.getDaysOfWeek(), removeNote.getTitle(), removeNote.getDetail(), removeNote.getBackColor());
                    final int noteId = removeNote.getId();
                    PlanDatabaseAdapter.deleteNote(noteId);
                }
                PlanDatabaseAdapter.close();
                ArchiveDatabaseAdapter.close();
                mAdapter.notifyDataSetChanged();
                loadNote();
            }

            @Override
            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                PlanDatabaseAdapter.open();
                ArchiveDatabaseAdapter.open();
                for (int position : reverseSortedPositions) {
                    PlanData removeNote = noteList.get(position);
                    ArchiveDatabaseAdapter.saveNote(removeNote.getYear(), removeNote.getMonth(), removeNote.getDay(), removeNote.getHour(), removeNote.getMinute(), removeNote.getDaysOfWeek(), removeNote.getTitle(), removeNote.getDetail(), removeNote.getBackColor());
                    final int noteId = removeNote.getId();
                    PlanDatabaseAdapter.deleteNote(noteId);
                }
                PlanDatabaseAdapter.close();
                ArchiveDatabaseAdapter.close();
                mAdapter.notifyDataSetChanged();
                loadNote();
            }
        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
        loadNote();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode != Activity.RESULT_OK){return;}
        }
        String title = data.getStringExtra("title");
        String detail = data.getStringExtra("detail");
        int backColorId = data.getIntExtra("backColor",0);

        Calendar cal = Calendar.getInstance();
        cal.set(m_year, (m_month - 1 ), m_day);
        String dayOfWeek;
        switch (cal.get(Calendar.DAY_OF_WEEK)) {
            case Calendar.SUNDAY:
                dayOfWeek = getString(R.string.sunday);
                break;
            case Calendar.MONDAY:
                dayOfWeek = getString(R.string.monday);
                break;
            case Calendar.TUESDAY:
                dayOfWeek = getString(R.string.thursday);
                break;
            case Calendar.WEDNESDAY:
                dayOfWeek = getString(R.string.wednesday);
                break;
            case Calendar.THURSDAY:
                dayOfWeek = getString(R.string.thursday);
                break;
            case Calendar.FRIDAY:
                dayOfWeek = getString(R.string.friday);
                break;
            case Calendar.SATURDAY:
                dayOfWeek = getString(R.string.saturday);
                break;
            default:
                Log.d("デフォルト","");
                dayOfWeek = "入力なし";
                break;
        }

        Log.d("PlanListFragmentData1","/" + m_year + m_month + m_day + m_hour + m_minute + dayOfWeek);
        Log.d("PlanListFragmentData2", " / title : " + title + " / detail : " + detail + " / backColorId : " + backColorId );
        PlanDatabaseAdapter.open();
        PlanDatabaseAdapter.saveNote(m_year, m_month, m_day, m_hour, m_minute, dayOfWeek, title, detail, backColorId);
        PlanDatabaseAdapter.close();
        loadNote();
    }

    protected static void loadNote(){
        noteList.clear();
        PlanDatabaseAdapter.open();
        Cursor c = PlanDatabaseAdapter.getAllNotes();
        if(c.moveToFirst()){
            do{
                PlanData note = new PlanData(
                        c.getInt(c.getColumnIndex(DatabaseAdapter.COL_ID)),
                        c.getInt(c.getColumnIndex(DatabaseAdapter.COL_YEAR)),
                        c.getInt(c.getColumnIndex(DatabaseAdapter.COL_MONTH)),
                        c.getInt(c.getColumnIndex(DatabaseAdapter.COL_DAY)),
                        c.getInt(c.getColumnIndex(DatabaseAdapter.COL_HOUR)),
                        c.getInt(c.getColumnIndex(DatabaseAdapter.COL_MINUTE)),
                        c.getString(c.getColumnIndex(DatabaseAdapter.COL_DAYOFWEEK)),
                        c.getString(c.getColumnIndex(DatabaseAdapter.COL_TITLE)),
                        c.getString(c.getColumnIndex(DatabaseAdapter.COL_DETAIL)),
                        c.getInt(c.getColumnIndex(DatabaseAdapter.COL_BACKCOLOR))
                );
                noteList.add(note);
            }while(c.moveToNext());
        }
        Collections.sort(noteList, new Comparator<PlanData>() {
            @Override
            public int compare(PlanData lhs, PlanData rhs) {
                int leftYear = lhs.getYear();
                int rightYear = rhs.getYear();
                int leftMonth = lhs.getMonth();
                int rightMonth = rhs.getMonth();
                int leftDay = lhs.getDay();
                int rightDay = rhs.getDay();

                if (leftYear > rightYear) {
                    return 1;
                } else if (leftYear == rightYear) {
                    if (leftMonth > rightMonth) {
                        return 1;
                    } else {
                        if (leftMonth == rightMonth) {
                            if (leftDay > rightDay) {
                                return 1;
                            } else {
                                if (leftDay == rightDay) {
                                    if (lhs.getHour() > rhs.getHour()) {
                                        return 1;
                                    } else {
                                        if (lhs.getHour() == rhs.getHour()) {
                                            if (lhs.getMinute() > rhs.getMinute()) {
                                                return 1;
                                            } else {
                                                if (lhs.getMinute() == rhs.getMinute()) {
                                                    return 0;
                                                } else {
                                                    return -1;
                                                }
                                            }
                                        } else {
                                            return -1;
                                        }
                                    }
                                } else {
                                    return -1;
                                }
                            }
                        } else {
                            return -1;
                        }
                    }
                } else {
                    return -1;
                }
            }
        });
        PlanDatabaseAdapter.close();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public  void onCreateOptionsMenu(Menu menu,MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                Log.d("PlanListFragment"," / onOptionItemSelected" + 123);
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.confirmation)
                        .setMessage(R.string.really_delete)
                        .setPositiveButton(
                                R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        PlanDatabaseAdapter.open();
                                        PlanDatabaseAdapter.deleteAllNotes();
                                        loadNote();
                                        PlanDatabaseAdapter.close();
                                        PlanListFragment.loadNote();
                                    }
                                }
                        )
                        .setNegativeButton(R.string.cancel, null)
                        .show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void newDateInstance(int year,int month,int day){
        m_year = year;
        m_month = month;
        m_day = day;
    }

    public void newTimeInstance(int hour,int minute){
        m_hour = hour;
        m_minute = minute;
    }
}
