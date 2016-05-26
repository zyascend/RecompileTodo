/*
 * Copyright 2016, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.zyascend.RecompileToDo.model.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.zyascend.RecompileToDo.R;
import com.zyascend.RecompileToDo.model.data.Task;
import com.zyascend.RecompileToDo.view.tasks.TasksFragment;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;


/**
 *
 * Created by Administrator on 2016/5/15.
 */

public class TasksAdapter extends BaseAdapter {

    private static final String TAG = "TAG_TasksAdapter";
    private List<Task> mTasks;
    private TasksFragment.TasksItemListener mItemListener;

    public TasksAdapter(List<Task> tasks, TasksFragment.TasksItemListener itemListener){
        mTasks = checkNotNull(tasks);
        mItemListener = itemListener;
    }

    public void replaceData(List<Task> tasks) {
        Log.d(TAG, "replaceData: ");
        setList(tasks);
        notifyDataSetChanged();
    }

    private void setList(List<Task> tasks) {
        mTasks = checkNotNull(tasks);
    }

    @Override
    public int getCount() {
        return mTasks.size();
    }

    @Override
    public Task getItem(int position) {
        return mTasks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view  = convertView;
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.task_item, parent, false);
        }

        final Task task = getItem(position);
        TextView titleTV = (TextView) view.findViewById(R.id.title);
        titleTV.setText(task.getTitleForList());

        Log.d(TAG, "getView: title = "+task.getTitle());

        CheckBox completeCB = (CheckBox) view.findViewById(R.id.complete);
        completeCB.setChecked(task.isCompleted());

        if (task.isCompleted()) {
            view.setBackgroundDrawable(parent.getContext()
                    .getResources().getDrawable(R.drawable.list_completed_touch_feedback));
        } else {
            view.setBackgroundDrawable(parent.getContext()
                    .getResources().getDrawable(R.drawable.touch_feedback));
        }

        completeCB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!task.isCompleted()) {
                    mItemListener.onCompleteTaskClick(task);
                } else {
                    mItemListener.onActivateTaskClick(task);
                }
            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mItemListener.onTaskClick(task);
            }
        });

        return view;
    }
}
