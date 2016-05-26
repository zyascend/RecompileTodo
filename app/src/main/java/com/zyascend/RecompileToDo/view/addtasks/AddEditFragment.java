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

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import static com.google.common.base.Preconditions.checkNotNull;


import com.zyascend.RecompileToDo.R;
import com.zyascend.RecompileToDo.presenter.addtasks.AddEditPresenterListener;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 *
 * Created by zyascend on 2016/5/20.
 */
public class AddEditFragment extends Fragment implements AddEditViewListener {


    public static final String ARGUMENT_EDIT_TASK_ID = "EDIT_TASK_ID";
    private static final String TAG = "TAG_AddEditFragment";

    private String mEditedTaskId;
    private AddEditPresenterListener mPresenter;

    @Bind(R.id.add_task_title)
    TextView mTitle;
    @Bind(R.id.add_task_description)
    TextView mDescription;

    public static AddEditFragment newInstance() {
        return new AddEditFragment();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setTaskIdIfAny();

        FloatingActionButton fab = (FloatingActionButton) getActivity().findViewById(R.id.fab_edit_task_done);
        fab.setImageResource(R.drawable.ic_done);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNewTask()){
                    mPresenter.createTask(mTitle.getText().toString()
                            ,mDescription.getText().toString());
                }else {
                    mPresenter.updateTask(mTitle.getText().toString()
                            ,mDescription.getText().toString());
                }
            }
        });

    }

    private void setTaskIdIfAny() {
        if (getArguments() != null && getArguments().containsKey(ARGUMENT_EDIT_TASK_ID)) {
            mEditedTaskId = getArguments().getString(ARGUMENT_EDIT_TASK_ID);
            Log.d(TAG, "setTaskIdIfAny: get id = "+mEditedTaskId);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.start();
    }

    @Override
    public void setPresenter(AddEditPresenterListener presenter) {
        mPresenter = checkNotNull(presenter);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.addtask_frag, container, false);
        ButterKnife.bind(this, root);

        setHasOptionsMenu(true);
        setRetainInstance(true);
        return root;
    }

    @Override
    public void showEmptyTaskError() {
        Snackbar.make(mTitle, getString(R.string.empty_task_message), Snackbar.LENGTH_LONG).show();

    }

    @Override
    public void showTasksList() {
        getActivity().setResult(Activity.RESULT_OK);
        Log.d(TAG, "showTasksList: ");
        getActivity().finish();
    }

    @Override
    public void setTitle(String title) {
        mTitle.setText(title);
    }

    @Override
    public void setDescription(String description) {
        mDescription.setText(description);
    }

    @Override
    public boolean isActive() {
        return isAdded();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    private boolean isNewTask() {
        return mEditedTaskId == null;
    }
}
