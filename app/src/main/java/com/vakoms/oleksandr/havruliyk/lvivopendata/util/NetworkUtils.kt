package com.vakoms.oleksandr.havruliyk.lvivopendata.util

import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.view.View
import androidx.paging.PagedList
import java.lang.StringBuilder

const val BASE_URL = "https://opendata.city-adm.lviv.ua/api/3/action/"

const val MARKET_ID = "2fc42d58-a332-4234-bc19-152d51f816a1"
const val FITNESS_ID = "29782a2a-bf39-4d3b-9a6d-ac1880f2d498"
const val CATERING_ID = "a656bf70-fde7-404c-9528-7100401040b2"
const val BARBER_ID = "634c8a6d-c272-4375-bd29-92526722b7ac"
const val ATM_ID = "64e9be16-bd89-4b3b-97aa-086b86e681f6"
const val COORDINATES_ID = "8d10826b-c00d-4fbd-b196-e8a231b0f4c0"

const val SEARCH = "datastore_search"
const val SQL = "sql"
const val SEARCH_SQL = "${SEARCH}_$SQL"

const val FIRST_ITEM = 0
const val PAGE_SIZE = 100

fun sqlMarkets(offset: Int): String =
    "SELECT*from\"$MARKET_ID\"" +
            "WHERE _id>$offset " +
            "AND _id<=${offset + PAGE_SIZE} " +
            "ORDER BY _id"

fun sqlMarketsSearchByName(name: String, offset: Int): String =
    "SELECT * from \"$MARKET_ID\"" +
        " WHERE (name LIKE '%$name%' " +
            "OR name LIKE '%$name') " +
            "AND _id > $offset " +
            "ORDER BY _id "

fun sqlFitness(offset: Int): String =
    "SELECT*from\"$FITNESS_ID\"" +
        " WHERE _id>$offset " +
            "AND _id<=${offset + PAGE_SIZE} " +
            "ORDER BY _id"

fun sqlFitnessSearchByName(name: String, offset: Int): String =
    "SELECT * from \"$FITNESS_ID\"" +
        " WHERE (name LIKE '%$name%' " +
            "OR name LIKE '%$name') " +
            "AND _id > $offset ORDER BY _id "

fun sqlCatering(offset: Int): String =
    "SELECT*from\"$CATERING_ID\"" +
        " WHERE _id>$offset " +
            "AND _id<=${offset + PAGE_SIZE} " +
            "ORDER BY _id"

fun sqlCateringSearchByName(name: String, offset: Int): String =
    "SELECT * from \"$CATERING_ID\"" +
        " WHERE (name LIKE '%$name%' " +
            "OR name LIKE '%$name') " +
            "AND _id > $offset " +
            "ORDER BY _id "

fun sqlBarber(offset: Int): String =
    "SELECT*from\"$BARBER_ID\"" +
        " WHERE _id>$offset " +
            "AND _id<=${offset + PAGE_SIZE} " +
            "ORDER BY _id"

fun sqlBarberSearchByName(name: String, offset: Int): String =
    "SELECT * from \"$BARBER_ID\"" +
        " WHERE (name LIKE '%$name%'" +
            " OR name LIKE '%$name')" +
            " AND _id > $offset " +
            "ORDER BY _id "

fun sqlATM(offset: Int): String =
    "SELECT*from\"$ATM_ID\"" +
        " WHERE _id>$offset " +
            "AND _id<=${offset + PAGE_SIZE}" +
            " ORDER BY _id"

fun sqlATMSearchByName(name: String, offset: Int): String =
    "SELECT * from \"$ATM_ID\"" +
        " WHERE (Банкомат LIKE '%$name%' " +
            "OR Банкомат LIKE '%$name') " +
            "AND _id > $offset" +
            " ORDER BY _id "

fun coordinatesSql(streetName: String, houseNumber: String) =
    "SELECT * from \"$COORDINATES_ID\"" +
        " WHERE (((street_s_name LIKE '%$streetName') " +
            "OR (street_d_name LIKE '%$streetName')) " +
            "AND housenumber LIKE '$houseNumber')"

fun isConnected(networkCapabilities: NetworkCapabilities?): Boolean {
    return when (networkCapabilities) {
        null -> false
        else -> with(networkCapabilities) {
            hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) || hasTransport(
                NetworkCapabilities.TRANSPORT_WIFI
            )
        }
    }
}

fun ConnectivityManager.isConnected() =
    isConnected(getNetworkCapabilities(activeNetwork))

fun pagedListConfig(): PagedList.Config {
    return PagedList.Config.Builder()
        .setEnablePlaceholders(false)
        .setPageSize(PAGE_SIZE)
        .build()
}

fun toVisibility(constraint: Boolean): Int {
    return if (constraint) {
        View.VISIBLE
    } else {
        View.GONE
    }
}