import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.nek12.androidutils.databinding.DataBindingFragment

abstract class RVDataBindingFragment<T : ViewDataBinding> : DataBindingFragment<T>() {

    abstract val recyclers: Set<RecyclerView>

    override fun onDestroyView() {
        recyclers.forEach {
            it.adapter = null
        }
        super.onDestroyView()
    }
}
