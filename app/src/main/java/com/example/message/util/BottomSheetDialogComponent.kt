import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CustomBottomSheetDialogFragment : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_LAYOUT_RES_ID = "layoutResId"

        fun newInstance(@LayoutRes layoutResId: Int): CustomBottomSheetDialogFragment {
            val fragment = CustomBottomSheetDialogFragment()
            val args = Bundle()
            args.putInt(ARG_LAYOUT_RES_ID, layoutResId)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val layoutResId = requireArguments().getInt(ARG_LAYOUT_RES_ID)
        return inflater.inflate(layoutResId, container, false)
    }
}
