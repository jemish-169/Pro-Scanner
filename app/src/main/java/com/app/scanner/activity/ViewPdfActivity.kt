package com.app.scanner.activity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.app.scanner.R
import com.app.scanner.databinding.ActivityViewPdfBinding
import com.app.scanner.util.Constants.Companion.SELECTED_FILES
import com.app.scanner.util.Constants.Companion.SELECTED_FILE_NAME
import com.app.scanner.util.shareSelectedFiles

class ViewPdfActivity : AppCompatActivity() {
    private lateinit var selectedFile: String
    private lateinit var selectedFileName: String
    private lateinit var binding: ActivityViewPdfBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)
        if (intent.hasExtra(SELECTED_FILES)) {
            selectedFile = intent.getStringExtra(SELECTED_FILES).toString()
        }
        if (intent.hasExtra(SELECTED_FILE_NAME)) {
            selectedFileName = intent.getStringExtra(SELECTED_FILE_NAME).toString()
        }

        binding.pdfView.initWithUrl(
            url = selectedFile,
            lifecycleCoroutineScope = lifecycleScope,
            lifecycle = lifecycle
        )
        binding.fileName.text = selectedFileName
        binding.backIcon.setOnClickListener {
            finish()
        }
        binding.shareIcon.setOnClickListener {
            shareSelectedFiles(
                this@ViewPdfActivity,
                listOf(Pair(selectedFile.toUri(), getString(R.string.app_name)))
            )
        }
    }
}