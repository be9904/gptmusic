package edu.skku.cs.gptmusic.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.TextView
import androidx.fragment.app.Fragment
import edu.skku.cs.gptmusic.R
import edu.skku.cs.gptmusic.api.User

class HomeFragment : Fragment(R.layout.fragment_home) {
    lateinit var trackGrid: GridView
    lateinit var msg: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_home, null)

        msg = view.findViewById(R.id.emptyMsg)
        trackGrid = view.findViewById(R.id.trackGrid)

        val screenWidthDp = resources.displayMetrics.widthPixels / resources.displayMetrics.density
        val numCardsInRow = ((screenWidthDp - 30) / 150).toInt()

        trackGrid.numColumns = numCardsInRow
        updateUI()

        return view
    }

    fun updateUI(){
        println("Update UI")
        if(User.info.savedTracks.isEmpty())
            msg.visibility = View.VISIBLE
        else
            msg.visibility = View.GONE

        val adapter = HomeAdapter(
            requireContext(),
            parentFragmentManager,
            User.info.savedTracks.toMutableList().apply { reverse() }.toList()
        )
        trackGrid.adapter = adapter
    }
}