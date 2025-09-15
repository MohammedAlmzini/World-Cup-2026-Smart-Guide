package com.ahmmedalmzini783.wcguide.ui.chatbot;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.databinding.ItemChatMessageBinding;
import com.ahmmedalmzini783.wcguide.util.DateTimeUtil;

import java.util.List;
import java.util.Locale;

public class ChatAdapter extends ListAdapter<ChatbotViewModel.ChatMessage, ChatAdapter.ChatViewHolder> {

    private static final DiffUtil.ItemCallback<ChatbotViewModel.ChatMessage> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<ChatbotViewModel.ChatMessage>() {
                @Override
                public boolean areItemsTheSame(@NonNull ChatbotViewModel.ChatMessage oldItem,
                                               @NonNull ChatbotViewModel.ChatMessage newItem) {
                    return oldItem.getTimestamp() == newItem.getTimestamp() &&
                            oldItem.getRole().equals(newItem.getRole());
                }

                @Override
                public boolean areContentsTheSame(@NonNull ChatbotViewModel.ChatMessage oldItem,
                                                  @NonNull ChatbotViewModel.ChatMessage newItem) {
                    return oldItem.getContent().equals(newItem.getContent());
                }
            };

    public ChatAdapter() {
        super(DIFF_CALLBACK);
    }

    public void updateMessages(List<ChatbotViewModel.ChatMessage> messages) {
        submitList(messages);
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChatMessageBinding binding = ItemChatMessageBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ChatViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        private final ItemChatMessageBinding binding;

        ChatViewHolder(ItemChatMessageBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(ChatbotViewModel.ChatMessage message) {
            if (message.isFromUser()) {
                // عرض رسالة المستخدم
                binding.userMessageContainer.setVisibility(View.VISIBLE);
                binding.assistantMessageContainer.setVisibility(View.GONE);
                binding.userMessageText.setText(message.getContent());
                
                // عرض التوقيت للمستخدم
                String timeText = DateTimeUtil.formatTime(message.getTimestamp(), Locale.getDefault());
                binding.userMessageTime.setText(timeText);
                binding.userMessageTime.setVisibility(View.VISIBLE);
                
            } else {
                // عرض رسالة المساعد
                binding.userMessageContainer.setVisibility(View.GONE);
                binding.assistantMessageContainer.setVisibility(View.VISIBLE);
                binding.assistantMessageText.setText(message.getContent());
                
                // عرض التوقيت للمساعد
                String timeText = DateTimeUtil.formatTime(message.getTimestamp(), Locale.getDefault());
                binding.assistantMessageTime.setText(timeText);
                binding.assistantMessageTime.setVisibility(View.VISIBLE);
            }

            // إخفاء التوقيت المشترك (غير مستخدم في التصميم الجديد)
            binding.messageTimestamp.setVisibility(View.GONE);
        }
    }
}