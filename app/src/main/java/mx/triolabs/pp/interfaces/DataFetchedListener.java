package mx.triolabs.pp.interfaces;

import mx.triolabs.pp.objects.FetcherResponse;

/**
 * Created by hugomedina on 11/25/16.
 */

/**
 * Provides a method to link a Fetcher response with the View that made the request
 */
public interface DataFetchedListener {

    /**
     * Passes the Fetcher's data to the main View
     * @param data The fetched data
     */
    void onDataFetched(FetcherResponse data);

}
