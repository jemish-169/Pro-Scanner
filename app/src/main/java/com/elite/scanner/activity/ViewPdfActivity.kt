package com.elite.scanner.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toUri
import androidx.lifecycle.lifecycleScope
import com.elite.scanner.R
import com.elite.scanner.databinding.ActivityViewPdfBinding
import com.elite.scanner.util.Constants.Companion.SELECTED_FILES
import com.elite.scanner.util.Constants.Companion.SELECTED_FILE_NAME
import com.elite.scanner.util.shareSelectedFiles

class ViewPdfActivity : AppCompatActivity() {

    private lateinit var fileUriToSting: String
    private lateinit var fileUri: Uri
    private lateinit var fileName: String
    private lateinit var binding: ActivityViewPdfBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityViewPdfBinding.inflate(layoutInflater)
        setContentView(binding.root)

        handleIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIntent(intent)
    }

    private fun handleIntent(intent: Intent?) {
        if (intent != null) {
            if (intent.hasExtra(SELECTED_FILES) && intent.hasExtra(SELECTED_FILE_NAME)) {
                fileUriToSting = intent.getStringExtra(SELECTED_FILES).toString()
                fileName = intent.getStringExtra(SELECTED_FILE_NAME).toString()
                viewPdfFromUriString()
            } else {
                intent.data?.let {
                    fileUri = it
                    fileName = it.lastPathSegment ?: getString(R.string.app_name)
                    viewPdfFromUri()
                }
            }
        }
    }

    private fun viewPdfFromUri() {
        binding.pdfView.initWithUri(
            uri = fileUri
        )
        binding.fileName.text = fileName
        binding.backIcon.setOnClickListener {
            finish()
        }
        binding.shareIcon.setOnClickListener {
            shareSelectedFiles(
                this@ViewPdfActivity,
                listOf(Pair(fileUri, getString(R.string.app_name)))
            )
        }
    }

    private fun viewPdfFromUriString() {
        binding.pdfView.initWithUrl(
            url = fileUriToSting,
            lifecycleCoroutineScope = lifecycleScope,
            lifecycle = lifecycle
        )
        binding.fileName.text = fileName
        binding.backIcon.setOnClickListener {
            finish()
        }
        binding.shareIcon.setOnClickListener {
            shareSelectedFiles(
                this@ViewPdfActivity,
                listOf(Pair(fileUriToSting.toUri(), getString(R.string.app_name)))
            )
        }
    }
}