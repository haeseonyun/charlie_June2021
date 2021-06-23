package de.htwberlin.learningcompanion.userinterface.fragment.datasecure

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import de.htwberlin.learningcompanion.R
import kotlinx.android.synthetic.main.fragment_datasecure.*

class DataSecureFragment : Fragment(){

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                                  savedInstanceState: Bundle?): View? {
            // Inflate the layout for this fragment
            return inflater.inflate(R.layout.fragment_datasecure, container, false)
        }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.findViewById<TextView>(R.id.btnSkippTutorial)?.visibility = View.GONE
        activity?.findViewById<TextView>(R.id.btnPrevScreen)?.visibility = View.GONE
        activity?.findViewById<TextView>(R.id.btnNextScreen)?.visibility = View.GONE
            button_yes.setOnClickListener {
                findNavController().navigate(
                        DataSecureFragmentDirections.actionDataSecureFragmentToGreetings1Fragment()
                )
            }
            button_no.setOnClickListener {
                findNavController().navigate(
                        DataSecureFragmentDirections.actionDataSecureFragmentToGreetings1Fragment()
                )
            }
    }
}