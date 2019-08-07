package com.vakoms.oleksandr.havruliyk.lvivopendata.ui.activity.group

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.SearchView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import com.vakoms.oleksandr.havruliyk.lvivopendata.R
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.barber.BarberRecord
import com.vakoms.oleksandr.havruliyk.lvivopendata.ui.activity.data.BarberDataActivity
import com.vakoms.oleksandr.havruliyk.lvivopendata.ui.adapter.BarberAdapter
import com.vakoms.oleksandr.havruliyk.lvivopendata.ui.listener.OnItemClickListener
import com.vakoms.oleksandr.havruliyk.lvivopendata.ui.vm.group.BarberViewModel
import com.vakoms.oleksandr.havruliyk.lvivopendata.util.DATA_ID
import com.vakoms.oleksandr.havruliyk.lvivopendata.util.hideKeyboard
import dagger.android.AndroidInjection
import kotlinx.android.synthetic.main.activity_list.*
import kotlinx.android.synthetic.main.back_button.*
import kotlinx.android.synthetic.main.label_layout.label_view
import kotlinx.android.synthetic.main.search_layout.*
import javax.inject.Inject

class BarberActivity : AppCompatActivity(),
    OnItemClickListener {

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory
    private lateinit var viewModel: BarberViewModel

    private var records = listOf<BarberRecord>()
    private var cacheRecords = listOf<BarberRecord>()

    private lateinit var recordsAdapter: BarberAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        AndroidInjection.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        initAdapter()
        initView()
        initRecyclerView()
        initViewModel()
        initObserver()
        initSearchView()
    }

    private fun initSearchView() {
        search_view.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                viewModel.setSearchData(search_view.query.toString())
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                if (newText.isNotEmpty()) {
                    label_view.visibility = View.GONE
                } else {
                    label_view.visibility = View.VISIBLE
                }
                return true
            }
        })

        search_view.setOnCloseListener {
            search_view.setQuery("", false)
            label_view.requestFocus()
            hideKeyboard(this)
            upDateView(cacheRecords)
            true
        }
    }

    private fun initView() {
        label_view.text = resources.getString(R.string.barber_label)

        back_button.setOnClickListener { finish() }
    }

    private fun initAdapter() {
        recordsAdapter = BarberAdapter(this)
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
            .get(BarberViewModel::class.java)
    }

    private fun initObserver() {
        viewModel.data.observe(this, Observer<List<BarberRecord>> { records ->
            upDateView(records)
        })

        viewModel.searchData.observe(this, Observer<List<BarberRecord>> { records ->
            upDateSearchView(records)
        })
    }

    private fun upDateView(records: List<BarberRecord>) {
        if (records.isEmpty()) {
            showEmptyView()
        } else {
            this.records = records
            cacheRecords = records
            recordsAdapter.data = records
            showRecyclerView()
        }
    }

    private fun upDateSearchView(records: List<BarberRecord>) {
        if (records.isEmpty()) {
            showEmptyView()
        } else {
            this.records = records
            recordsAdapter.data = records
            showRecyclerView()
        }
    }

    private fun showEmptyView() {
        recycler_view.visibility = View.GONE
    }

    private fun showRecyclerView() {
        recycler_view.visibility = View.VISIBLE
    }

    override fun onItemClick(view: View, position: Int) {
        startDataActivityWith(records[position])
    }

    private fun startDataActivityWith(data: BarberRecord) {
        val intent = Intent(this, BarberDataActivity::class.java)
        intent.putExtra(DATA_ID, data._id)
        startActivity(intent)
    }
}