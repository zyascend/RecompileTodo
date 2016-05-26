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
package com.zyascend.RecompileToDo.view.taskdetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.zyascend.RecompileToDo.R;
import com.zyascend.RecompileToDo.model.data.TaskDataAchiever;
import com.zyascend.RecompileToDo.model.data.TasksRepository;
import com.zyascend.RecompileToDo.presenter.addtasks.AddEditPresenterAchiever;
import com.zyascend.RecompileToDo.presenter.taskdetail.DetailPresenterAchiever;
import com.zyascend.RecompileToDo.utils.ActivityUtils;

/**
 * 
 * Created by zyascend on 2016/5/21.
 */
public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_TASK_ID = "TASK_ID";
    private static final String TAG = "TAG_DetailActivity";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taskdetail_act);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setDisplayShowHomeEnabled(true);

        String taskId = getIntent().getStringExtra(EXTRA_TASK_ID);
        Log.d(TAG, "onCreate: id = "+taskId);
        DetailFragment taskDetailFragment = (DetailFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (taskDetailFragment == null) {
            taskDetailFragment = DetailFragment.newInstance(taskId);

            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    taskDetailFragment, R.id.contentFrame);
        }
        //创建presenter对象
        TasksRepository mRepository = TasksRepository.getInstance(TaskDataAchiever.getInstance(this));
        new DetailPresenterAchiever(taskId,mRepository,taskDetailFragment);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

//    @VisibleForTesting
//    public IdlingResource getCountingIdlingResource() {
//        return EspressoIdlingResource.getIdlingResource();
//    }
}
