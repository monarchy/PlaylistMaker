package com.example.playlistmaker.ui.playlist_forms.playlist_edit_form

import android.os.Bundle
import android.view.View
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.playlistmaker.R
import com.example.playlistmaker.ui.playlist_forms.playlist_create_form.PlaylistCreateFragment
import com.example.playlistmaker.ui.playlist_forms.states.PlaylistEditState
import com.example.playlistmaker.util.GsonClient
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class PlaylistEditFragment : PlaylistCreateFragment() {


    override val viewModel: PlaylistEditViewModel by lazy {
        getViewModel {
            parametersOf(
                GsonClient.playlistFromJson(
                    arguments?.getString(PLAYLIST_TAG).toString()
                ),
                requireContext()
            )
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.applyButton.text = getString(R.string.change)
        binding.back.title = getString(R.string.editor)
    }

    override fun exitDialog() {
        parentFragment?.findNavController()?.navigateUp()
    }

    override fun stateObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.playlistFormState.collect() { state ->
                when (state) {
                    is PlaylistEditState.OnEditing -> {
                        super.playlistName = state.playlist.playlistName
                        binding.playlistNameEditText.setText(state.playlist.playlistName)

                        playlistNameTextWatcher.onTextChanged(
                            state.playlist.playlistName,
                            0, 0, state.playlist.playlistName?.length ?: 0
                        )

                        super.playlistTitle =
                            when (state.playlist.playlistTitle.equals(getString(R.string.empty_title))) {
                                true -> ""
                                false -> state.playlist.playlistTitle!!
                            }
                        if (state.playlist.playlistTitle != null)
                            binding.playlistDescriptionEditText.setText(
                                when (state.playlist.playlistTitle.equals(getString(R.string.empty_title))) {
                                    true -> ""
                                    false -> state.playlist.playlistTitle!!
                                }
                            )


                        super.imageUri = state.playlist.imagePath?.toUri()
                        if (state.playlist.imagePath != null) {
                            binding.poster.setImageURI(state.playlist.imagePath.toUri())
                        } else {
                            binding.poster.setImageResource(R.drawable.player_placeholder)
                        }
                    }

                    PlaylistEditState.Success -> Unit
                    else -> Unit
                }
            }
        }
    }

    companion object {
        fun createArgs(playlist: String): Bundle = bundleOf(
            PLAYLIST_TAG to playlist,
        )

        private const val PLAYLIST_TAG = "Playlist"
    }
}