package com.rpl.fintrack.ui.adapter

import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rpl.fintrack.database.local.entity.TransactionEntity
import com.rpl.fintrack.databinding.ItemTransactionBinding
import com.rpl.fintrack.ui.detail.DetailActivity
import com.rpl.fintrack.util.currencyUtils.formatRupiah

class TransactionsAdapter: ListAdapter<TransactionEntity, TransactionsAdapter.MyViewHolder>(DIFF_CALLBACK){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionsAdapter.MyViewHolder {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val transaction = getItem(position)
        holder.bind(transaction)
    }

    class MyViewHolder(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind(transaction: TransactionEntity){
            binding.tvCategory.text = transaction.category
            binding.tvName.text = transaction.name
            binding.tvAmount.text = formatRupiah(transaction.amount.toLong())

            if (transaction.type.equals("Income", ignoreCase = true)) {
                binding.tvAmount.setTextColor(Color.parseColor("#28A745"))
            }
            else {
                binding.tvAmount.setTextColor(Color.parseColor("#DC3545"))
            }

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, DetailActivity::class.java)
                intent.putExtra(TID, transaction.tid)
                startActivity(itemView.context, intent, null)
            }
        }
    }

    companion object{
        private const val TID = "transactionId"

        val DIFF_CALLBACK: DiffUtil.ItemCallback<TransactionEntity> =
            object: DiffUtil.ItemCallback<TransactionEntity>(){
                override fun areItemsTheSame(
                    oldItem: TransactionEntity,
                    newItem: TransactionEntity
                ): Boolean {
                    return oldItem.tid == newItem.tid
                }

                override fun areContentsTheSame(
                    oldItem: TransactionEntity,
                    newItem: TransactionEntity
                ): Boolean {
                    return oldItem == newItem
                }
            }
    }
}