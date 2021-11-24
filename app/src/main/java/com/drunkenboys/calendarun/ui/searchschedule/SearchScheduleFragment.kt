package com.drunkenboys.calendarun.ui.searchschedule

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.ui.setupWithNavController
import androidx.recyclerview.widget.RecyclerView
import com.drunkenboys.calendarun.R
import com.drunkenboys.calendarun.databinding.FragmentSearchScheduleBinding
import com.drunkenboys.calendarun.ui.base.BaseFragment
import com.drunkenboys.calendarun.util.extensions.launchAndRepeatWithViewLifecycle
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SearchScheduleFragment : BaseFragment<FragmentSearchScheduleBinding>(R.layout.fragment_search_schedule) {

    private val searchScheduleViewModel: SearchScheduleViewModel by viewModels()

    private val searchScheduleAdapter = SearchScheduleAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = searchScheduleViewModel

        setupToolbar()
        setupRecyclerView()

        launchAndRepeatWithViewLifecycle {
            launch { collectListItem() }
            launch { collectScheduleClickEvent() }
        }
    }

    private fun setupToolbar() {
        binding.toolbarSearchSchedule.setupWithNavController(navController)
    }

    private fun setupRecyclerView() {
        binding.rvSearchSchedule.adapter = searchScheduleAdapter
        binding.rvSearchSchedule.addItemDecoration(SearchScheduleDivider(requireContext()))
        binding.rvSearchSchedule.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                if (!recyclerView.canScrollVertically(-1)) {
                    searchScheduleViewModel.tryFetchPrev()
                }
                if (!recyclerView.canScrollVertically(1)) {
                    searchScheduleViewModel.tryFetchNext()
                }
            }
        })
    }

    private suspend fun collectListItem() {
        searchScheduleViewModel.listItem.collect { listItem ->
            searchScheduleAdapter.submitList(listItem)
        }
    }

    private suspend fun collectScheduleClickEvent() {
        searchScheduleViewModel.scheduleClickEvent.collect { schedule ->
            val action = SearchScheduleFragmentDirections.toSaveSchedule(schedule.calendarId, schedule.id)
            navController.navigate(action)
        }
    }
}
