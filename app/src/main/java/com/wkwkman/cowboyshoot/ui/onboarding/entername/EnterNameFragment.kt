package com.wkwkman.cowboyshoot.ui.onboarding.entername

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.wkwkman.cowboyshoot.databinding.FragmentEnterNameBinding
import com.wkwkman.cowboyshoot.ui.menu.GameModeActivity
import com.wkwkman.cowboyshoot.ui.onboarding.OnFinishNavigateListener

class EnterNameFragment: Fragment(), OnFinishNavigateListener {
    private lateinit var binding: FragmentEnterNameBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentEnterNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onFinishNavigateListener() {
        val name = binding.etName.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(requireContext(), "Please input your name", Toast.LENGTH_SHORT).show()
        } else {
            navigateToMenu(name)
        }
    }
    
    private fun navigateToMenu(name: String) {
        val intent = Intent(activity, GameModeActivity::class.java)
        intent.putExtra("Username", name)
        //intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
    }
}