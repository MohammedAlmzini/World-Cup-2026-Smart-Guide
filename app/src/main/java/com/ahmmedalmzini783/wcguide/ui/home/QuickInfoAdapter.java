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

public class QuickInfoAdapter extends ListAdapter<QuickInfo, QuickInfoAdapter.QuickInfoViewHolder> {

    public QuickInfoAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<QuickInfo> DIFF_CALLBACK = new DiffUtil.ItemCallback<QuickInfo>() {
        @Override
        public boolean areItemsTheSame(@NonNull QuickInfo oldItem, @NonNull QuickInfo newItem) {
            return oldItem.getCountryCode().equals(newItem.getCountryCode());
        }

        @Override
        public boolean areContentsTheSame(@NonNull QuickInfo oldItem, @NonNull QuickInfo newItem) {
            return oldItem.equals(newItem);
        }
    };

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
            // Set country name from country code
            String countryName = getCountryName(quickInfo.getCountryCode());
            binding.countryName.setText(countryName);

            // Set currency info
            binding.currencyInfo.setText("Currency: " + quickInfo.getCurrency());

            // Set languages info
            List<String> languages = quickInfo.getLanguages();
            String languagesText = "Languages: ";
            if (languages != null && !languages.isEmpty()) {
                languagesText += String.join(", ", languages);
            } else {
                languagesText += "N/A";
            }
            binding.languagesInfo.setText(languagesText);

            // Set transport info
            String transportText = "Transport: " + 
                (quickInfo.getTransportTips() != null ? quickInfo.getTransportTips() : "N/A");
            binding.transportInfo.setText(transportText);

            // Set weather info
            String weatherText = "Weather: " + 
                (quickInfo.getWeatherTip() != null ? quickInfo.getWeatherTip() : "N/A");
            binding.weatherInfo.setText(weatherText);
        }

        private String getCountryName(String countryCode) {
            if (countryCode == null) return "Unknown";
            
            switch (countryCode.toUpperCase()) {
                case "US":
                    return "United States";
                case "CA":
                    return "Canada";
                case "MX":
                    return "Mexico";
                case "QA":
                    return "Qatar";
                case "AE":
                    return "United Arab Emirates";
                case "SA":
                    return "Saudi Arabia";
                case "KW":
                    return "Kuwait";
                case "BH":
                    return "Bahrain";
                case "OM":
                    return "Oman";
                default:
                    // Try to get country name from Locale
                    try {
                        Locale locale = new Locale("", countryCode);
                        return locale.getDisplayCountry();
                    } catch (Exception e) {
                        return countryCode;
                    }
            }
        }
    }
}