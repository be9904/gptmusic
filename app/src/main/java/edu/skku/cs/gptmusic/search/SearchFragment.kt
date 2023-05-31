package edu.skku.cs.gptmusic.search

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.RadioButton
import android.widget.SearchView
import androidx.fragment.app.Fragment
import edu.skku.cs.gptmusic.HomeActivity
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.Track

class SearchFragment : Fragment(R.layout.fragment_search){
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_search, null)

        val searchView = view.findViewById<SearchView>(R.id.searchView)
        val searchList = view.findViewById<ListView>(R.id.searchList)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                searchView.clearFocus()
                if(p0!!.isNotEmpty())
                {
                    HomeActivity.apiHandler.trackSearch(
                        requireContext(),
                        searchList,
                        1,
                        30,
                        p0
                    )
                    return true
                }
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                if(p0!!.isNotEmpty()){
                    HomeActivity.apiHandler.trackSearch(
                        requireContext(),
                        searchList,
                        1,
                        5,
                        p0
                    )
                    return true
                }
                else{
                    searchList.adapter = SearchAdapter(requireContext(), listOf<Track>())
                    return true
                }
                return false
            }

        })

        return view
    }
}