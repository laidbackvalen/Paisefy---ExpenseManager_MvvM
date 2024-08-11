package com.valenpatel.paisefy.views.fragments

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.valenpatel.paisefy.databinding.FragmentMoreBinding
import com.valenpatel.paisefy.views.activities.MainActivity


class MoreFragment : Fragment() {
    lateinit var binding: FragmentMoreBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentMoreBinding.inflate(layoutInflater)
        binding.notificationsView.setOnClickListener {
            (activity as MainActivity).getFragment(NotificationFragment())
        }
        binding.helpNSupport.setOnClickListener {
            sendEmail()
        }
        binding.recentlyDeletedView.setOnClickListener {
            (activity as MainActivity).getFragment(RecentlyDeletedTransaction_Fragment())
        }
        binding.archive.setOnClickListener {
            (activity as MainActivity).getFragment(ArchiveTransaction_Fragment())
        }
        binding.feedbackView.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://play.google.com/store/apps/details?id=com.valenpatel.paisefy&pcampaignid=web_share")
            }

// Check if there's an app to handle this intent
            if (intent.resolveActivity(requireActivity().packageManager) != null) {
                startActivity(intent)
            } else {
                // Handle the case where no app can handle the intent
                Toast.makeText(requireContext(), "No app found to open this link", Toast.LENGTH_SHORT).show()
            }
        }

        binding.privacyView.setOnClickListener {
            val uri = "https://laidbackvalen.github.io/vrpexpenseprivacy/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(uri))
            startActivity(intent)
        }
        binding.termsView.setOnClickListener {
            val uri = "https://laidbackvalen.github.io/valexpenpaistermsnconditn/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setData(Uri.parse(uri))
            startActivity(intent)
        }

        try {
           var versionName = requireActivity().getApplicationContext().getPackageManager()
                .getPackageInfo(requireContext().getApplicationContext().getPackageName(), 0).versionName
            binding.version.text = "APP VERSION "+versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }


        return binding.root
    }

    private fun sendEmail() {
        val emailIntent = Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "valen.patel1@gmail.com", null))
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "This is my subject text")
        requireContext().startActivity(Intent.createChooser(emailIntent, null))
    }
}