package me.mking.doggodex.presentation.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import me.mking.doggodex.R
import me.mking.doggodex.common.ui.BindingRecyclerAdapter
import me.mking.doggodex.common.ui.BindingViewHolder
import me.mking.doggodex.databinding.DogBreedRecyclerItemBinding
import me.mking.doggodex.presentation.viewstate.DogBreedsViewData

typealias OnDogBreedClick = (Int) -> Unit

class DogBreedsRecyclerAdapter(
    override val data: List<DogBreedsViewData>,
    private val onDogBreedClick: OnDogBreedClick? = null
) : BindingRecyclerAdapter<DogBreedsViewData, DogBreedsRecyclerAdapter.DogBreedViewHolder>(data) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DogBreedViewHolder {
        return DogBreedViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dog_breed_recycler_item, parent, false),
            onDogBreedClick
        )
    }

    override fun onBindViewHolder(holder: DogBreedViewHolder, position: Int) =
        holder.bind(data[position])

    override fun getItemCount() = data.size

    class DogBreedViewHolder(itemView: View, private val onDogBreedClick: OnDogBreedClick?) :
        BindingViewHolder<DogBreedRecyclerItemBinding, DogBreedsViewData>(itemView) {
        override val viewBinding: DogBreedRecyclerItemBinding =
            DogBreedRecyclerItemBinding.bind(itemView)

        override fun bind(data: DogBreedsViewData) {
            viewBinding.dogBreedRecyclerItemTitle.text = data.breedName
            viewBinding.dogBreedRecyclerItem.setOnClickListener {
                onDogBreedClick?.invoke(adapterPosition)
            }
        }
    }
}