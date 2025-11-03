package com.rpl.fintrack.ui.write

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.rpl.fintrack.ui.write.expenses.ExpensesFragment
import com.rpl.fintrack.ui.write.income.IncomeFragment

class WritePagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        var fragment: Fragment? = null
        when(position){
            0 -> fragment = IncomeFragment()
            1 -> fragment = ExpensesFragment()
        }
        return fragment as Fragment
    }
}