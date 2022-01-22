package com.jp_funda.todomind.view.mindmap

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import com.jp_funda.todomind.R
import com.jp_funda.todomind.view.components.MindMapCard

class MindMapFragment : Fragment() {

    companion object {
        fun newInstance() = MindMapFragment()
    }

    private lateinit var viewModel: MindMapViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(MindMapViewModel::class.java)

        return ComposeView(requireContext()).apply {
            setContent {
                MindMapContent()
            }
        }
    }

    @Composable
    fun MindMapContent() {
        val scrollState = rememberScrollState()

        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            // Recent Activity Section
            Text(
                text = "The Mind Map you are working on recently...",
                modifier = Modifier
                    .padding(15.dp)
                    .width(250.dp),
                color = Color.White,
                style = MaterialTheme.typography.h6,
            )
            MindMapCard(modifier = Modifier.padding(bottom = 20.dp))

            // Mind Maps Section
            Text(
                text = "Mind Maps",
                modifier = Modifier.padding(15.dp),
                color = Color.White,
                style = MaterialTheme.typography.h6,
            )
            MindMapsRow()

            // Completed Section
            Text(
                text = "Closed",
                modifier = Modifier.padding(15.dp),
                color = Color.White,
                style = MaterialTheme.typography.h6,
            )
            MindMapsRow()
        }
    }

    // MindMapFragment's components

    @Composable
    fun MindMapsRow() {
        LazyRow(modifier = Modifier.padding(bottom = 20.dp)) {
            // todo fill with data
            items(items = List<String>(5) { "d" }) { str ->
                MindMapCard()
            }
        }
    }
}