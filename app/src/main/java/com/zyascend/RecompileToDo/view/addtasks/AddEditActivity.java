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
package com.zyascend.RecompileToDo.view.addtasks;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.FrameLayout;

import com.zyascend.RecompileToDo.R;
import com.zyascend.RecompileToDo.model.data.TaskDataAchiever;
import com.zyascend.RecompileToDo.model.data.TasksRepository;
import com.zyascend.RecompileToDo.presenter.addtasks.AddEditPresenterAchiever;
import com.zyascend.RecompileToDo.utils.ActivityUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by zyascend on 2016/5/20.
 */
public class AddEditActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_TASK = 1;
    private static final String TAG = "TAG_AEActivity";

    @Bind(R.id.contentFrame)
    FrameLayout mContentFrame;
    @Bind(R.id.fab_edit_task_done)
    FloatingActionButton mFabEditTaskDone;
    @Bind(R.id.coordinatorLayout)
    CoordinatorLayout mCoordinatorLayout;
    @Bind(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;
    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addtask_act);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);

            Log.d(TAG, "onCreate: actionbar 存在");

        }

        AddEditFragment addEditFragment = (AddEditFragment) getSupportFragmentManager().findFragmentById(R.id.contentFrame);
        String taskId = null;
        if (addEditFragment == null) {
            addEditFragment = AddEditFragment.newInstance();

            if (getIntent().hasExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID)) {

                taskId = getIntent().getStringExtra(AddEditFragment.ARGUMENT_EDIT_TASK_ID);
                actionBar.setTitle(R.string.edit_task);
                Bundle bundle = new Bundle();
                bundle.putString(AddEditFragment.ARGUMENT_EDIT_TASK_ID, taskId);
                addEditFragment.setArguments(bundle);

                Log.d(TAG, "onCreate: 编辑task Id = "+taskId);
            } else {
                actionBar.setTitle(R.string.add_task);
            }

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    addEditFragment, R.id.contentFrame);
        }

        //创建presenter对象
        TasksRepository mRepository = TasksRepository.getInstance(TaskDataAchiever.getInstance(this));
        new AddEditPresenterAchiever(taskId, mRepository, addEditFragment);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
