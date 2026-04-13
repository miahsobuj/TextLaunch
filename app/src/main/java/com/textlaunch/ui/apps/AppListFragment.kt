package com.textlaunch.ui.apps

import android.graphics.Typeface
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.textlaunch.R
import com.textlaunch.data.repository.AppRepositoryImpl
import com.textlaunch.data.repository.PreferencesRepositoryImpl
import com.textlaunch.domain.model.AppInfo
import kotlinx.coroutines.launch

class AppListFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var searchEditText: EditText
    private lateinit var appRepository: AppRepositoryImpl
    private lateinit var preferencesRepository: PreferencesRepositoryImpl

    private var allApps = listOf<AppInfo>()
    private lateinit var adapter: AppListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_app_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appRepository = AppRepositoryImpl(requireContext())
        preferencesRepository = PreferencesRepositoryImpl(requireContext())

        recyclerView = view.findViewById(R.id.apps_recycler_view)
        searchEditText = view.findViewById(R.id.search_edit_text)

        setupRecyclerView()
        setupSearch()
        loadApps()
    }

    private fun setupRecyclerView() {
        adapter = AppListAdapter { app ->
            appRepository.launchApp(app)
        }
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
    }

    private fun setupSearch() {
        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                filterApps(s?.toString() ?: "")
            }
        })
    }

    private fun loadApps() {
        lifecycleScope.launch {
            allApps = appRepository.getInstalledApps()
            adapter.submitList(allApps)
        }
    }

    private fun filterApps(query: String) {
        val filtered = if (query.isEmpty()) {
            allApps
        } else {
            allApps.filter { it.appName.contains(query, ignoreCase = true) }
        }
        adapter.submitList(filtered)
    }

    class AppListAdapter(
        private val onAppClick: (AppInfo) -> Unit
    ) : RecyclerView.Adapter<AppListAdapter.AppViewHolder>() {

        private var apps = listOf<AppInfo>()

        fun submitList(newApps: List<AppInfo>) {
            apps = newApps
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_app_text, parent, false)
            return AppViewHolder(view)
        }

        override fun onBindViewHolder(holder: AppViewHolder, position: Int) {
            holder.bind(apps[position])
        }

        override fun getItemCount() = apps.size

        inner class AppViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val textView: TextView = itemView.findViewById(R.id.app_name_text)

            fun bind(app: AppInfo) {
                textView.text = app.appName
                textView.setTextColor(0xFF00FFFF.toInt())
                textView.textSize = 16f
                textView.typeface = Typeface.MONOSPACE

                itemView.setOnClickListener { onAppClick(app) }
            }
        }
    }
}