package com.ahmmedalmzini783.wcguide.ui.home;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.ahmmedalmzini783.wcguide.data.model.QuickInfo;
import com.ahmmedalmzini783.wcguide.databinding.ItemQuickInfoBinding;

import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class QuickInfoAdapter extends ListAdapter<QuickInfo, QuickInfoAdapter.QuickInfoViewHolder> {

    private static final DiffUtil.ItemCallback<QuickInfo> DIFF_CALLBACK = new DiffUtil.ItemCallback<QuickInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull QuickInfo oldItem, @NonNull QuickInfo newItem) {
            return oldItem.getCountryCode() != null && oldItem.getCountryCode().equals(newItem.getCountryCode());
        }

        @Override
        public boolean areContentsTheSame(@NonNull QuickInfo oldItem, @NonNull QuickInfo newItem) {
            return oldItem.equals(newItem);
        }
    };

    public QuickInfoAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public QuickInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemQuickInfoBinding binding = ItemQuickInfoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new QuickInfoViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull QuickInfoViewHolder holder, int position) {
        holder.bind(getItem(position));
    }

    static class QuickInfoViewHolder extends RecyclerView.ViewHolder {
        private final ItemQuickInfoBinding binding;

        QuickInfoViewHolder(ItemQuickInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(QuickInfo quickInfo) {
            String countryName = countryCodeToName(quickInfo.getCountryCode());
            binding.countryName.setText(countryName);

            binding.currencyInfo.setText("Currency: " + safeString(quickInfo.getCurrency()));

            List<String> languages = quickInfo.getLanguages();
            String languagesText;
            if (languages != null && !languages.isEmpty()) {
                languagesText = languagesToString(languages);
            } else {
                languagesText = "-";
            }
            binding.languagesInfo.setText("Languages: " + languagesText);

            binding.transportInfo.setText("Transport: " + safeString(quickInfo.getTransportTips()));
            binding.weatherInfo.setText("Weather: " + safeString(quickInfo.getWeatherTip()));
        }

        private String safeString(String value) {
            return value == null ? "-" : value;
        }

        private String languagesToString(List<String> languages) {
            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < languages.size(); i++) {
                if (i > 0) builder.append(", ");
                builder.append(languages.get(i));
            }
            return builder.toString();
        }

        private String countryCodeToName(String countryCode) {
            if (countryCode == null || countryCode.length() != 2) {
                return countryCode != null ? countryCode : "";
            }
            Locale locale = new Locale("", countryCode);
            String display = locale.getDisplayCountry();
            return display != null && !display.isEmpty() ? display : countryCode;
        }
    }
}

