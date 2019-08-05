package com.vakoms.oleksandr.havruliyk.lvivopendata.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.vakoms.oleksandr.havruliyk.lvivopendata.R
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.fitness.FitnessRecord
import com.vakoms.oleksandr.havruliyk.lvivopendata.ui.adapter.FitnessAdapter
import com.vakoms.oleksandr.havruliyk.lvivopendata.ui.adapter.OnItemClickListener
import com.vakoms.oleksandr.havruliyk.lvivopendata.ui.vm.FitnessViewModel
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.back_button.*
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.label_layout.*
import javax.inject.Inject

class FitnessActivity : AppCompatActivity(), OnItemClickListener {

    companion object {
        const val DATA_ID = "DATA_ID"
    }

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    private val records = mutableListOf<FitnessRecord>()
    private lateinit var recordsAdapter: FitnessAdapter
    private lateinit var viewModel: FitnessViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        AndroidInjection.inject(this)

        initAdapter()
        initView()
        initRecyclerView()
        initViewModel()
        initObserver()
    }

    private fun initView() {
        label.text = resources.getString(R.string.fitness_label)

        back_button.setOnClickListener { finish() }
    }

    private fun initAdapter() {
        recordsAdapter = FitnessAdapter(this)
    }

    private fun initRecyclerView() {
        with(recycler_view) {
            layoutManager = LinearLayoutManager(context?.applicationContext)
            adapter = recordsAdapter
            itemAnimator = DefaultItemAnimator()
            isNestedScrollingEnabled = true
        }
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, viewModelFactory)
            .get(FitnessViewModel::class.java)
    }

    private fun initObserver() {
        viewModel.getFitnessData()?.observe(
            this,
            androidx.lifecycle.Observer
            {
                if (it != null) {
                    refreshRecordsAndView(it)
                } else {
                    setViewToEmpty()
                }
            })
    }

    private fun refreshRecordsAndView(newRecords: List<FitnessRecord>) {
        records.addAll(newRecords)
        recordsAdapter.data = records
    }

    private fun setViewToEmpty() {
        recycler_view.visibility = View.GONE
    }

    override fun onItemClick(view: View, position: Int) {
        startDataActivityWith(records[position])
    }

    private fun startDataActivityWith(data: FitnessRecord) {
        val intent = Intent(this, FitnessDataActivity::class.java)
        intent.putExtra(DATA_ID, data.id)
        startActivity(intent)
    }
}