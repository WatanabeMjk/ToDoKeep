package com.watanabemjk.todokeep;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TrashListFragment extends Fragment {
    static List<PlanData> noteList = new ArrayList<>();
    static RecyclerView.Adapter mAdapter;
    static final String DATABASE_PLAN_NAME = "PlanListDatabase";
    static final String DATABASE_ARCHIVE_NAME = "ArchiveListDatabase";
    static final String DATABASE_TRASH_NAME = "TrashListDatabase";
    static DatabaseAdapter PlanDatabaseAdapter;
    static DatabaseAdapter ArchiveDatabaseAdapter;
    static DatabaseAdapter TrashDatabaseAdapter;
    View root;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        PlanDatabaseAdapter = new DatabaseAdapter(getActivity(), DATABASE_PLAN_NAME);
        ArchiveDatabaseAdapter = new DatabaseAdapter(getActivity(),DATABASE_ARCHIVE_NAME);
        TrashDatabaseAdapter = new DatabaseAdapter(getActivity(),DATABASE_TRASH_NAME);
        mAdapter = new RecyclerViewAdapter(getActivity(),noteList,"TrashListFragment");
        root = inflater.inflate(R.layout.archive_trash_list_fragment, container, false);
        setHasOptionsMenu(true);
        loadNote();
        return root;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);

        RecyclerView mRecyclerView = root.findViewById(R.id.my_recycler_view);
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
                TrashDatabaseAdapter.open();
                for (int position : reverseSortedPositions) {
                    PlanData removeNote = noteList.get(position);
                    final int noteId = removeNote.getId();
                    TrashDatabaseAdapter.deleteNote(noteId);
                }
                TrashDatabaseAdapter.close();
                mAdapter.notifyDataSetChanged();
                loadNote();
            }

            @Override

            public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                TrashDatabaseAdapter.open();
                for (int position : reverseSortedPositions) {
                    PlanData removeNote = noteList.get(position);
                    final int noteId = removeNote.getId();
                    TrashDatabaseAdapter.deleteNote(noteId);
                }
                TrashDatabaseAdapter.close();
                mAdapter.notifyDataSetChanged();
                loadNote();
            }
        });
        mRecyclerView.addOnItemTouchListener(swipeTouchListener);
        loadNote();
    }

    protected static void loadNote(){
        noteList.clear();
        TrashDatabaseAdapter.open();
        Cursor c = TrashDatabaseAdapter.getAllNotes();
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
                if (lhs.getYear() > rhs.getYear()) {
                    return 1;
                } else if (lhs.getYear() == rhs.getYear()) {
                    if (lhs.getMonth() > rhs.getMonth()) {
                        return 1;
                    } else {
                        if (lhs.getMonth() == rhs.getMonth()) {
                            if (lhs.getDay() > rhs.getDay()) {
                                return 1;
                            } else {
                                if (lhs.getDay() == rhs.getDay()) {
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
        TrashDatabaseAdapter.close();
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public  void onCreateOptionsMenu(Menu menu, MenuInflater inflater){
        super.onCreateOptionsMenu(menu,inflater);
        inflater.inflate(R.menu.menu_main,menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.action_settings:
                Log.d("TrashListFragment"," / onOptionItemSelected" + 123);
                new AlertDialog.Builder(getActivity())
                        .setTitle(R.string.confirmation)
                        .setMessage(R.string.really_delete)
                        .setPositiveButton(
                                R.string.yes,
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        TrashDatabaseAdapter.open();
                                        TrashDatabaseAdapter.deleteAllNotes();
                                        loadNote();
                                        TrashDatabaseAdapter.close();
                                        TrashListFragment.loadNote();
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
}
