package com.simplex.whatsup.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.simplex.whatsup.R
import com.simplex.whatsup.models.Report
import com.simplex.whatsup.prettyPrintTime
import org.koin.core.KoinComponent

class ReportAdapter(private val dataSet: List<Report>): RecyclerView.Adapter<ReportAdapter.ReportViewHolder>(), KoinComponent {
    class ReportViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val name: TextView = itemView.findViewById(R.id.report_name)
        private val category: TextView = itemView.findViewById(R.id.report_category)
        private val comments: TextView = itemView.findViewById(R.id.report_comments)
        private val time: TextView = itemView.findViewById(R.id.report_time)
        private val image: ImageView = itemView.findViewById(R.id.report_image)
        private val divider: View = itemView.findViewById(R.id.report_divider)

        fun setupViewHolder(report: Report, isFirst: Boolean) {
            name.text = report.username
            category.text = report.category
            comments.text = report.comments
            time.text = report.time.prettyPrintTime()

            if (isFirst) { divider.visibility = View.GONE }
            if (report.report_image != null && report.image != null) {
                image.setImageBitmap(report.image)
            }
            else {
                image.setImageBitmap(null)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.report_viewholder, parent, false)

        return ReportViewHolder(itemView)
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        holder.setupViewHolder(dataSet[position], isFirst(position))
    }

    private fun isFirst(position: Int) = (position == 0)
}