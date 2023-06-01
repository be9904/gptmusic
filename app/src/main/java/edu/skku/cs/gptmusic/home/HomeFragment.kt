package edu.skku.cs.gptmusic.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import androidx.fragment.app.Fragment
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.User

class HomeFragment : Fragment(R.layout.fragment_home) {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)

        val trackGrid = view.findViewById<GridView>(R.id.trackGrid)

        val screenWidthDp = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val numCardsInRow = ((screenWidthDp - 30) / 150).toInt()

        trackGrid.numColumns = numCardsInRow
        trackGrid.adapter =
            HomeAdapter(
                requireContext(),
                parentFragmentManager,
                User.info.savedTracks.toMutableList().apply { reverse() }.toList()
            )

        return view
    }
}