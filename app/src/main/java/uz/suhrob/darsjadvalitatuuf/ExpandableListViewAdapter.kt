package uz.suhrob.darsjadvalitatuuf

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import kotlinx.android.synthetic.main.expandable_list_header.view.*
import kotlinx.android.synthetic.main.expandable_list_item.view.*

class ExpandableListViewAdapter(private val context: Context, val darkThemeEnabled: Boolean, private val headerTitles: List<String>, private val childTitles: HashMap<String, List<String>>)
                :BaseExpandableListAdapter() {

    override fun getGroup(groupPosition: Int): Any {
        return this.headerTitles[groupPosition]
    }

    override fun getGroupCount(): Int {
        return this.headerTitles.size
    }

    override fun getGroupId(groupPosition: Int): Long = groupPosition.toLong()

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val listTitle = getGroup(groupPosition) as String
        val convertView1 = if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            layoutInflater.inflate(if (darkThemeEnabled) R.layout.expandable_list_header_dark else R.layout.expandable_list_header, null)
        } else {
            convertView
        }
        val listTitleTextView = convertView1.title
        listTitleTextView.text = listTitle
        return convertView1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return this.childTitles[this.headerTitles[groupPosition]]?.get(childPosition) ?: ""
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return this.childTitles[this.headerTitles[groupPosition]]?.size ?: 0
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long = childPosition.toLong()

    override fun getChildView(groupPosition: Int, childPosition: Int, isLastChild: Boolean, convertView: View?, parent: ViewGroup?): View {
        val childTitle = getChild(groupPosition, childPosition) as String
        val convertView1 = if (convertView == null) {
            val layoutInflater = this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            layoutInflater.inflate(if (darkThemeEnabled) R.layout.expandable_list_item_dark else R.layout.expandable_list_item, null)
        } else {
            convertView
        }
        val listTitleTextView = convertView1.child_item
        listTitleTextView.text = childTitle
        return convertView1
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int) = true

    override fun hasStableIds() = false
}