package de.htwberlin.learningcompanion.userinterface.fragment.survey

import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import com.android.volley.Response
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import de.htwberlin.learningcompanion.R
import de.htwberlin.learningcompanion.userinterface.fragment.mainscreen.MainScreenFragmentDirections
import kotlinx.android.synthetic.main.fragment_survey_evaluate1.*
import java.util.*
import kotlin.collections.HashMap


class Survey3EvaluateFragment : Fragment() {
    /*DATABASE*/
    val addUrl : String = "http://192.168.178.32/charlie/addSurvey.php"
    //val addUrl : String = "http://companion.hu-informatik.berlin.de/addRunGoal.php"
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_survey_evaluate3, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

          activity?.findViewById<TextView>(R.id.button_next)?.apply {
            text = getString(R.string.next)
          button_next.setOnClickListener{
              /*DATABASE*/
              addData()
                findNavController().navigate(
                        MainScreenFragmentDirections.actionGlobalSessionOverviewFragment()
                )
            }
        }
    }
    /*DATABASE*/
    fun addData() {
        val getID = Settings.Secure.getString(context?.getContentResolver(), Settings.Secure.ANDROID_ID)
        val getTime = Calendar.getInstance().time.toString()
        val getQuestion = question?.text.toString()
        val getAnswer1 = answerButton1.isChecked.toString()
        val getAnswer2 = answerButton2.isChecked.toString()
        val getAnswer3 = answerButton3.isChecked.toString()
        val getAnswer4 = answerButton4.isChecked.toString()
        val getComment = comment?.text.toString()
        //funktioniert nur in der Simulation bei android 7 stürzt es in echt ab
        // val getTime = LocalDateTime.now().toString()

        val postRequest: StringRequest = object : StringRequest(Method.POST, addUrl,
                Response.Listener { response -> // response
                    Log.d("Response", response)
                },
                Response.ErrorListener { response ->// error
                    Log.d("Error.Response", response.toString())
                }
        ) {
            override fun getParams(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params.put("id", getID)
                params.put("time", getTime)
                params.put("question", getQuestion)
                params.put("answer1", getAnswer1)
                params.put("answer2", getAnswer2)
                params.put("answer3", getAnswer3)
                params.put("answer4", getAnswer4)
                params.put("comment", getComment)
                return params
            }
        }
        val queue = Volley.newRequestQueue(context!!)
        queue.add(postRequest)
    }
}
