package com.example.practice

import android.os.Bundle
import android.text.InputFilter
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.practice.databinding.ActivityMainBinding
import java.util.Calendar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel
    private lateinit var yearLimit: String
    private lateinit var panNumber: String

    companion object {
        const val validPanSize: Int = 10
        const val dayLimit = 31
        const val monthLimit = 12
        val month_31_days = mutableSetOf(1, 3, 7, 8, 10, 12)
    }

    private var isPanValid: Boolean = false
    private var isValidDay: Boolean = false
    private var isValidMonth: Boolean = false
    private var isValidYear: Boolean = false
    private lateinit var day: String
    private lateinit var month: String
    private lateinit var year: String
    private lateinit var convertedDateFormat: String
    private lateinit var currentDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        viewModel = ViewModelProvider(this).get(MainViewModel::class.java)
        initListener()
        initObserver()
        getCurrentYearAndDate()
        setHyperLinkColor()
    }

    private fun setHyperLinkColor() {
        binding.tvInfo.setLinkTextColor(ContextCompat.getColor(this, R.color.accent_color))
    }


    private fun getCurrentYearAndDate() {
        val calendar = Calendar.getInstance()
        yearLimit = calendar.get(Calendar.YEAR).toString()
        currentDate = calendar.get(Calendar.DATE).toString()
    }

    private fun getPanNumber() = binding.etPanNumber.text.toString()
    private fun getDay() = binding.etDay.text.toString()
    private fun getMonth() = binding.etMonth.text.toString()
    private fun getYear() = binding.etYear.text.toString()


    private fun initObserver() {
        viewModel.validPan.observe(this, Observer {
            isPanValid = if (it == true) {
                binding.panInputLayout.setBackgroundResource(R.drawable.text_input_outline)
                true
            } else {
                binding.panInputLayout.setBackgroundResource(R.drawable.ic_error)
                false
            }
        })
        viewModel.validDob.observe(this, Observer {
            if (it == true && isPanValid) {
                binding.btnNext.let { button ->
                    button.setBackgroundColor(ContextCompat.getColor(this, R.color.accent_color))
                    button.isEnabled = true
                    button.isClickable = true
                    button.setTextColor(ContextCompat.getColor(this, R.color.white))
                }
            }
        })

    }

    private fun initListener() {
        binding.etPanNumber.filters = arrayOf(
            InputFilter.AllCaps(),
            InputFilter.LengthFilter(validPanSize)
        )
        binding.etPanNumber.setOnFocusChangeListener { _, _ ->
            panNumber = getPanNumber()
            if (panNumber.isNotEmpty()) {
                viewModel.checkValidPan(panNumber)
            }
        }
        binding.etDay.setOnFocusChangeListener { _, _ ->
            day = getDay()
            binding.etMonth.isEnabled = true
            checkValidDay(day)
        }
        binding.etMonth.setOnFocusChangeListener { _, _ ->
            month = getMonth()
            binding.etYear.isEnabled = true
            checkValidMonth(month)
        }
        binding.etYear.setOnFocusChangeListener { _, _ ->
            year = getYear()
            checkValidYear(year)
        }
        binding.etYear.setOnEditorActionListener { _, actionId, event ->
            if (event != null && event.keyCode == KeyEvent.KEYCODE_ENTER || actionId == EditorInfo.IME_ACTION_DONE) {
                year = getYear()
                checkValidYear(year)
            }
            false
        }
        binding.btnNext.setOnClickListener {
            Toast.makeText(this, R.string.str_details_toast, Toast.LENGTH_SHORT).show()
            lifecycleScope.launch {
                delay(2000)
                finish()
            }
        }
        binding.tvNoPan.setOnClickListener {
            finish()
        }

    }

    private fun checkValidYear(year: String) {
        if (year.isNotEmpty()) {
            if (month.toInt() == 2 && year.toInt() % 4 != 0) {
                binding.etYear.setBackgroundResource(R.drawable.ic_error)
            } else if (year.toInt() > yearLimit.toInt()) {
                binding.etYear.setBackgroundResource(R.drawable.ic_error)
            } else {
                isValidYear = true
                binding.etYear.setBackgroundResource(R.drawable.text_input_outline)
                if (isValidDay && isValidMonth) {
                    checkFinalValidation()
                }
            }
        }
    }

    private fun checkValidDay(day: String) {
        if (day.isNotEmpty()) {
            if (day.toInt() > dayLimit) {
                binding.etDay.setBackgroundResource(R.drawable.ic_error)
            } else {
                binding.etDay.setBackgroundResource(R.drawable.text_input_outline)
                isValidDay = true
                if (isValidDay && isValidMonth && isValidYear) {
                    binding.btnNext.let {
                        it.setBackgroundColor(ContextCompat.getColor(this, R.color.accent_color))
                        it.isEnabled = true
                        it.isClickable = true
                        it.setTextColor(ContextCompat.getColor(this, R.color.white))
                    }
                }
            }
        }
    }

    private fun checkValidMonth(month: String) {
        if (month.isNotEmpty()) {
            when {
                month.toInt() == 2 && day.toInt() > 28 -> {
                    isValidDay = false
                    binding.etDay.setBackgroundResource(R.drawable.ic_error)
                }
                month.toInt() > monthLimit -> {
                    binding.etMonth.setBackgroundResource(R.drawable.ic_error)
                }
                month.toInt() !in month_31_days && day.toInt() == 31 -> {
                    isValidDay = false
                    binding.etDay.setBackgroundResource(R.drawable.ic_error)
                }
                else -> {
                    isValidMonth = true
                    binding.etMonth.setBackgroundResource(R.drawable.text_input_outline)
                    if (isValidDay && isValidMonth && isValidYear) {
                        binding.btnNext.let {
                            it.setBackgroundColor(
                                ContextCompat.getColor(
                                    this,
                                    R.color.accent_color
                                )
                            )
                            it.isEnabled = true
                            it.isClickable = true
                            it.setTextColor(ContextCompat.getColor(this, R.color.white))
                        }
                    }
                }
            }
        }
    }

    private fun checkFinalValidation() {
        if (day.isNotEmpty() && month.isNotEmpty() && year.isNotEmpty()) {
            convertedDateFormat = "$day/$month/$year"
            if (convertedDateFormat <= currentDate) {
                viewModel.checkValidDob(convertedDateFormat)
            }
        }

    }
}
