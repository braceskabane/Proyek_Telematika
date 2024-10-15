import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.dicoding.hanebado.R
import com.dicoding.hanebado.databinding.DialogDailyAddWorkoutBinding
import com.dicoding.hanebado.view.dashboard.plan.addworkout.customworkout.RepeatWorkoutDialog
import com.dicoding.hanebado.view.dashboard.plan.addworkout.repeat.CustomWorkoutDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class AddWorkoutDialog : BottomSheetDialogFragment() {

    private lateinit var binding: DialogDailyAddWorkoutBinding

    override fun getTheme(): Int = R.style.CustomBottomSheetDialog

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDailyAddWorkoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setUpListeners()
    }

    private fun setUpListeners() {
        binding.apply {
            btnAddTagSave.setOnClickListener {
                dismiss()
            }
            binding.btnAddTagCancel.setOnClickListener {
                dismiss()
            }
            llCustomWorkout.setOnClickListener{
                showCustomWorkoutDialog()
            }
            llRepeat.setOnClickListener{
                showRepeatWorkoutDialog()
            }
        }
    }

    private fun showCustomWorkoutDialog() {
        val dialog = CustomWorkoutDialog()
        dialog.show(childFragmentManager, CustomWorkoutDialog.TAG)
    }

    private fun showRepeatWorkoutDialog() {
        val dialog = RepeatWorkoutDialog()
        dialog.show(childFragmentManager, RepeatWorkoutDialog.TAG)
    }

    companion object {
        const val TAG = "AddWorkoutDialog"
    }
}