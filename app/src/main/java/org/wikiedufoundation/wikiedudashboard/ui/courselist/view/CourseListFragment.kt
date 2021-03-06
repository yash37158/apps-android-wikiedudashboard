package org.wikiedufoundation.wikiedudashboard.ui.courselist.view

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.fragment_explore_course_list.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import org.wikiedufoundation.wikiedudashboard.R
import org.wikiedufoundation.wikiedudashboard.data.preferences.SharedPrefs
import org.wikiedufoundation.wikiedudashboard.ui.adapters.CourseListRecyclerAdapter
import org.wikiedufoundation.wikiedudashboard.ui.coursedetail.common.view.CourseDetailActivity
import org.wikiedufoundation.wikiedudashboard.ui.courselist.data.CourseListData
import org.wikiedufoundation.wikiedudashboard.ui.courselist.viewmodel.CourseListViewModel
import org.wikiedufoundation.wikiedudashboard.util.filterOrEmptyList
import org.wikiedufoundation.wikiedudashboard.util.showSnackbar
import timber.log.Timber
import java.util.Locale
import kotlin.collections.ArrayList

/**
 * A simple [Fragment] subclass.
 * Activities that contain this fragment must implement the
 * Use the [CourseListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class CourseListFragment : Fragment() {

    private val sharedPrefs: SharedPrefs by inject()
    private val courselistViewModel by viewModel<CourseListViewModel> { parametersOf(sharedPrefs.cookies) }
    private var mParam1: String? = null
    private var mParam2: String? = null
    private var coursesList: List<CourseListData> = ArrayList()
    private lateinit var courseListRecyclerAdapter: CourseListRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            mParam1 = arguments?.getString(ARG_PARAM1)
            mParam2 = arguments?.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? = inflater.inflate(R.layout.fragment_explore_course_list, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        courseListRecyclerAdapter = CourseListRecyclerAdapter(R.layout.item_rv_explore_courses) {
            openCourseDetail(it)
        }
        initializeRecyclerView()
        setData()
        initializeProgressBar()
        initializeMessage()
    }

    private fun initializeRecyclerView() {
        recyclerCourseList?.apply {
            layoutManager = LinearLayoutManager(context)
            setHasFixedSize(true)
            adapter = courseListRecyclerAdapter
        }
    }

    private fun setData() {
        courselistViewModel.data.observe(this, Observer {
            Timber.d(it.toString())
            if (it.isNotEmpty()) {
                recyclerCourseList?.visibility = View.VISIBLE
                courseListRecyclerAdapter.setData(it)
                textViewNoCourses?.visibility = View.GONE
            } else {
                recyclerCourseList?.visibility = View.GONE
                textViewNoCourses?.visibility = View.VISIBLE
            }
        })
    }

    private fun initializeProgressBar() {
        courselistViewModel.progressbar.observe(this, androidx.lifecycle.Observer {
            progressBar.visibility = if (it) View.VISIBLE else View.GONE
        })
    }

    private fun initializeMessage() {
        courselistViewModel.showMsg.observe(this, androidx.lifecycle.Observer {
            val message = it?.showMsg
            message?.let { msg -> view?.showSnackbar(msg) }
        })
    }

    private fun openCourseDetail(slug: String) {
        val i = Intent(context, CourseDetailActivity::class.java)
        i.putExtra("url", slug)
        i.putExtra("enrolled", false)
        startActivity(i)
    }

    fun updateSearchQuery(query: String) {
        Timber.d(query)

        val filterCourseQuery = courselistViewModel.data.value.filterOrEmptyList {
            it.title.toLowerCase(Locale.getDefault())
                    .contains(query.toLowerCase(Locale.getDefault()))
        }

        courseListRecyclerAdapter.setData(filterCourseQuery)
    }

    companion object {
        // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
        private const val ARG_PARAM1 = "param1"
        private const val ARG_PARAM2 = "param2"

        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ExploreFragment.
         */
        // TODO: Rename and change types and number of parameters
        fun newInstance(param1: String, param2: String) = CourseListFragment().apply {
            val args = Bundle()
            args.putString(ARG_PARAM1, param1)
            args.putString(ARG_PARAM2, param2)
            this.arguments = args
        }
    }
}