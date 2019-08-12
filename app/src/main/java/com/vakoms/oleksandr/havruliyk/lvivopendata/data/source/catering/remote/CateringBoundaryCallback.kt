package com.vakoms.oleksandr.havruliyk.lvivopendata.data.source.catering.remote

import androidx.annotation.MainThread
import androidx.paging.PagingRequestHelper
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.api.OpenDataApi
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.catering.CateringRecord
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.model.catering.CateringResponse
import com.vakoms.oleksandr.havruliyk.lvivopendata.data.source.DataBoundaryCallback
import com.vakoms.oleksandr.havruliyk.lvivopendata.util.FIRST_ITEM
import com.vakoms.oleksandr.havruliyk.lvivopendata.util.sqlCatering
import retrofit2.Response
import java.util.concurrent.Executor

class CateringBoundaryCallback(
    private val webservice: OpenDataApi,
    private val handleResponse: (List<CateringRecord>) -> Unit,
    private val ioExecutor: Executor
) : DataBoundaryCallback<CateringRecord>(ioExecutor) {

    @MainThread
    override fun onZeroItemsLoaded() {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.INITIAL) {
            webservice.getCatering(sqlCatering(FIRST_ITEM))
                .enqueue(CateringWebServiceCallback(it) { response, _ ->
                    insertItemsIntoDb(response, it)
                })
        }
    }

    @MainThread
    override fun onItemAtEndLoaded(itemAtEnd: CateringRecord) {
        helper.runIfNotRunning(PagingRequestHelper.RequestType.AFTER) {
            webservice.getCatering(sqlCatering(itemAtEnd._id))
                .enqueue(CateringWebServiceCallback(it) { response, _ ->
                    insertItemsIntoDb(response, it)
                })
        }
    }

    private fun insertItemsIntoDb(
        response: Response<CateringResponse>,
        it: PagingRequestHelper.Request.Callback
    ) {
        ioExecutor.execute {
            handleResponse(response.body().result.records)
            it.recordSuccess()
        }
    }
}