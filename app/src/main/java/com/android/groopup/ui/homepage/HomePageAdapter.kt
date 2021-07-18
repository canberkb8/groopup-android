package com.android.groopup.ui.homepage

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.android.groopup.R
import com.android.groopup.data.remote.model.UserGroupModel
import com.android.groopup.databinding.CardHomepageGroupItemBinding
import com.android.groopup.utils.extensions.withGlideOrEmpty

class HomePageAdapter : RecyclerView.Adapter<HomePageAdapter.HomePageViewHolder>() {

    private lateinit var cardHomepageGroupItemBinding: CardHomepageGroupItemBinding

    private var onItemClickListener: ((String) -> Unit)? = null

    var userGroupList: MutableList<UserGroupModel> = mutableListOf()
        set(value) {
            field = value
            this.notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomePageViewHolder {
        cardHomepageGroupItemBinding = CardHomepageGroupItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HomePageViewHolder(cardHomepageGroupItemBinding)
    }

    override fun getItemCount(): Int = userGroupList.size

    override fun onBindViewHolder(holder: HomePageViewHolder, position: Int) {
        val userGroupItem = userGroupList[position]
        userGroupItem.let {
            holder.bind(it, onItemClickListener)
        }
    }

    class HomePageViewHolder(private val cardHomepageGroupItemBinding: CardHomepageGroupItemBinding) :
        RecyclerView.ViewHolder(cardHomepageGroupItemBinding.root) {
        fun bind(userGroupModel: UserGroupModel, onItemClickListener: ((groupID: String) -> Unit)?) {
            withGlideOrEmpty(cardHomepageGroupItemBinding.imgGroupImage,userGroupModel.groupImg!!)
            cardHomepageGroupItemBinding.txtGroupName.text = userGroupModel.groupTitle
            cardHomepageGroupItemBinding.txtGroupSubtitle.text = userGroupModel.groupSubTitle
            if (userGroupModel.groupFavorite!!){
                cardHomepageGroupItemBinding.imgNonFavorite.visibility =  View.GONE
                cardHomepageGroupItemBinding.imgFavorite.visibility = View.VISIBLE
            }else{
                cardHomepageGroupItemBinding.imgNonFavorite.visibility =  View.VISIBLE
                cardHomepageGroupItemBinding.imgFavorite.visibility = View.GONE
            }
            cardHomepageGroupItemBinding.cardHomepageItem.setOnClickListener { onItemClickListener?.invoke(userGroupModel.groupID!!) }
        }

    }

    fun setOnItemClickListener(onItemClickListener: ((String) -> Unit)?) {
        this.onItemClickListener = onItemClickListener
    }

}