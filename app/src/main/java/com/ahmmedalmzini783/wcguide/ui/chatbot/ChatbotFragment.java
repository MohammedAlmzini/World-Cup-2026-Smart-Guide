package com.ahmmedalmzini783.wcguide.ui.chatbot;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.ahmmedalmzini783.wcguide.R;
import com.ahmmedalmzini783.wcguide.databinding.FragmentChatbotBinding;
import com.ahmmedalmzini783.wcguide.util.Resource;

import java.util.Locale;

public class ChatbotFragment extends Fragment implements TextToSpeech.OnInitListener {

    private FragmentChatbotBinding binding;
    private ChatbotViewModel viewModel;
    private ChatAdapter chatAdapter;

    // Speech components
    private TextToSpeech textToSpeech;
    private SpeechRecognizer speechRecognizer;
    private boolean isListening = false;

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentChatbotBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ChatbotViewModel.class);

        setupRecyclerView();
        setupInputHandling();
        setupSpeechComponents();
        observeViewModel();
    }

    private void setupRecyclerView() {
        chatAdapter = new ChatAdapter();
        binding.chatRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.chatRecycler.setAdapter(chatAdapter);
    }

    private void setupInputHandling() {
        // Send button click
        binding.sendButton.setOnClickListener(v -> sendMessage());

        // Enter key in input field
        binding.messageInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND) {
                sendMessage();
                return true;
            }
            return false;
        });

        // Voice input button
        binding.voiceInputButton.setOnClickListener(v -> toggleVoiceInput());

        // Quick action chips
        binding.chipDailyPlan.setOnClickListener(v -> showDailyPlanDialog());
        binding.chipTranslate.setOnClickListener(v -> showTranslateDialog());
    }

    private void setupSpeechComponents() {
        // Initialize Text-to-Speech
        textToSpeech = new TextToSpeech(getContext(), this);

        // Initialize Speech Recognition
        if (SpeechRecognizer.isRecognitionAvailable(getContext())) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(getContext());
            speechRecognizer.setRecognitionListener(new RecognitionListener() {
                @Override
                public void onReadyForSpeech(Bundle params) {
                    binding.loadingText.setText(R.string.chatbot_listening);
                    binding.loadingIndicator.setVisibility(View.VISIBLE);
                }

                @Override
                public void onBeginningOfSpeech() {}

                @Override
                public void onRmsChanged(float rmsdB) {}

                @Override
                public void onBufferReceived(byte[] buffer) {}

                @Override
                public void onEndOfSpeech() {
                    binding.loadingIndicator.setVisibility(View.GONE);
                    isListening = false;
                    updateVoiceButton();
                }

                @Override
                public void onError(int error) {
                    binding.loadingIndicator.setVisibility(View.GONE);
                    isListening = false;
                    updateVoiceButton();
                    // Handle speech recognition error
                }

                @Override
                public void onResults(Bundle results) {
                    if (results != null && results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION) != null) {
                        String spokenText = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION).get(0);
                        binding.messageInput.setText(spokenText);
                        sendMessage();
                    }
                }

                @Override
                public void onPartialResults(Bundle partialResults) {}

                @Override
                public void onEvent(int eventType, Bundle params) {}
            });
        }
    }

    private void observeViewModel() {
        viewModel.getChatMessages().observe(getViewLifecycleOwner(), messages -> {
            if (messages != null) {
                chatAdapter.submitList(messages);
                // Scroll to bottom
                binding.chatRecycler.scrollToPosition(messages.size() - 1);
            }
        });

        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading != null) {
                binding.loadingIndicator.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                binding.sendButton.setEnabled(!isLoading);
            }
        });

        viewModel.getLastResponse().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.getStatus()) {
                    case SUCCESS:
                        // Speak the response if TTS is available
                        if (resource.getData() != null && textToSpeech != null) {
                            speakText(resource.getData());
                        }
                        break;
                    case ERROR:
                        // Show error message
                        break;
                }
            }
        });
    }

    private void sendMessage() {
        String message = binding.messageInput.getText().toString().trim();
        if (!TextUtils.isEmpty(message)) {
            viewModel.sendMessage(message);
            binding.messageInput.setText("");
        }
    }

    private void toggleVoiceInput() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                    REQUEST_RECORD_AUDIO_PERMISSION);
            return;
        }

        if (isListening) {
            stopListening();
        } else {
            startListening();
        }
    }

    private void startListening() {
        if (speechRecognizer != null) {
            isListening = true;
            updateVoiceButton();
            viewModel.startVoiceInput();
        }
    }

    private void stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer.stopListening();
            isListening = false;
            updateVoiceButton();
        }
    }

    private void updateVoiceButton() {
        // Update voice button appearance based on listening state
        if (isListening) {
            binding.voiceInputButton.setIconResource(R.drawable.ic_mic);
            binding.voiceInputButton.setIconTintResource(R.color.status_live);
        } else {
            binding.voiceInputButton.setIconResource(R.drawable.ic_mic);
            binding.voiceInputButton.setIconTintResource(android.R.color.darker_gray);
        }
    }

    private void speakText(String text) {
        if (textToSpeech != null) {
            binding.loadingText.setText(R.string.chatbot_speaking);
            binding.loadingIndicator.setVisibility(View.VISIBLE);

            textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null, "ChatbotResponse");
        }
    }

    private void showDailyPlanDialog() {
        // TODO: Show daily plan generation dialog
        viewModel.generateDailyPlan("Los Angeles", 8, "attractions, food, culture");
    }

    private void showTranslateDialog() {
        // TODO: Show translation dialog
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Set language for TTS
            Locale locale = getResources().getConfiguration().getLocales().get(0);
            int result = textToSpeech.setLanguage(locale);

            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Fallback to English
                textToSpeech.setLanguage(Locale.ENGLISH);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startListening();
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }

        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }

        binding = null;
    }
}