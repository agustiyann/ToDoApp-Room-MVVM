package com.masscode.simpletodolist.ui.list

import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.masscode.simpletodolist.R
import com.masscode.simpletodolist.adapter.ListAdapter
import com.masscode.simpletodolist.databinding.FragmentListBinding
import com.masscode.simpletodolist.utils.hideKeyboard
import com.masscode.simpletodolist.utils.shortToast
import com.masscode.simpletodolist.viewmodel.TodoViewModel
import com.masscode.simpletodolist.viewmodel.TodoViewModelFactory
import jp.wasabeef.recyclerview.animators.FadeInUpAnimator

class ListFragment : Fragment() {

    private lateinit var binding: FragmentListBinding
    private lateinit var mLayoutManager: RecyclerView.LayoutManager
    private lateinit var viewModel: TodoViewModel
    private lateinit var adapter: ListAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        // Inflate the layout for this fragment
        binding = FragmentListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        activity?.hideKeyboard()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModelFactory = TodoViewModelFactory.getInstance(requireContext())
        viewModel = ViewModelProvider(this, viewModelFactory)[TodoViewModel::class.java]

        mLayoutManager = StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        adapter = ListAdapter(viewModel)

        binding.apply {
            addTaskBtn.setOnClickListener(
                Navigation.createNavigateOnClickListener(R.id.action_listFragment_to_addFragment)
            )
        }

        // Setup RecyclerView
        setupRecyclerview()

        viewModel.getAllTodos().observe(viewLifecycleOwner, { list ->
            adapter.setData(list)

            if (list.isEmpty()) {
                binding.noDataImage.visibility = View.VISIBLE
                binding.noDataText.visibility = View.VISIBLE
            } else {
                binding.noDataImage.visibility = View.GONE
                binding.noDataText.visibility = View.GONE
            }
        })
    }

    private fun setupRecyclerview() {
        val recyclerView = binding.rvTodo
        recyclerView.adapter = adapter
        recyclerView.layoutManager =
            StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
        recyclerView.itemAnimator = FadeInUpAnimator().apply {
            addDuration = 100
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val delete: String

        when (item.itemId) {
            R.id.delete_selected -> {
                delete = "selected"
                showDialog(delete)
            }

            R.id.delete_all -> {
                delete = "all"
                showDialog(delete)
            }
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showDialog(delete: String) {
        if (delete == "selected") {

            AlertDialog.Builder(requireContext(), R.style.MyDialogTheme).apply {
                setTitle("Delete Checked Lists")
                setMessage("All checked lists will be deleted. Are you sure?")
                setPositiveButton("Yes") { _, _ ->
                    viewModel.deleteSelected()

                    context.shortToast("All checked lists have been deleted.")
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            }.create().show()

        } else if (delete == "all") {

            AlertDialog.Builder(requireContext(), R.style.MyDialogTheme).apply {
                setTitle("Clear Lists")
                setMessage("All lists will be deleted. Are you sure?")
                setPositiveButton("Yes") { _, _ ->
                    viewModel.clearTodos()

                    context.shortToast("All lists have been deleted. Add your To Do!")
                }
                setNegativeButton("No") { dialog, _ ->
                    dialog.cancel()
                }
            }.create().show()
        }
    }
}