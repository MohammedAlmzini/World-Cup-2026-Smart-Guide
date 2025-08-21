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

    class QuickInfoViewHolder extends RecyclerView.ViewHolder {
        private final ItemQuickInfoBinding binding;

        QuickInfoViewHolder(ItemQuickInfoBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bind(QuickInfo quickInfo) {
            // Set country name based on country code
            String countryName = getCountryName(quickInfo.getCountryCode());
            binding.countryName.setText(countryName);
            
            // Set currency info
            binding.currencyInfo.setText("Currency: " + quickInfo.getCurrency());
            
            // Set languages info
            if (quickInfo.getLanguages() != null && !quickInfo.getLanguages().isEmpty()) {
                String languagesText = "Languages: " + String.join(", ", quickInfo.getLanguages());
                binding.languagesInfo.setText(languagesText);
            } else {
                binding.languagesInfo.setText("Languages: Not specified");
            }
            
            // Set transport info
            binding.transportInfo.setText("Transport: " + quickInfo.getTransportTips());
            
            // Set weather info
            binding.weatherInfo.setText("Weather: " + quickInfo.getWeatherTip());
        }

        private String getCountryName(String countryCode) {
            switch (countryCode) {
                case "US":
                    return "United States";
                case "CA":
                    return "Canada";
                case "MX":
                    return "Mexico";
                case "QA":
                    return "Qatar";
                case "SA":
                    return "Saudi Arabia";
                case "AE":
                    return "United Arab Emirates";
                case "EG":
                    return "Egypt";
                case "MA":
                    return "Morocco";
                case "TN":
                    return "Tunisia";
                case "SN":
                    return "Senegal";
                case "GH":
                    return "Ghana";
                case "CM":
                    return "Cameroon";
                case "NG":
                    return "Nigeria";
                case "BR":
                    return "Brazil";
                case "AR":
                    return "Argentina";
                case "UY":
                    return "Uruguay";
                case "EC":
                    return "Ecuador";
                case "PE":
                    return "Peru";
                case "CL":
                    return "Chile";
                case "CO":
                    return "Colombia";
                case "VE":
                    return "Venezuela";
                case "PY":
                    return "Paraguay";
                case "BO":
                    return "Bolivia";
                case "CR":
                    return "Costa Rica";
                case "PA":
                    return "Panama";
                case "HN":
                    return "Honduras";
                case "SV":
                    return "El Salvador";
                case "GT":
                    return "Guatemala";
                case "NI":
                    return "Nicaragua";
                case "BZ":
                    return "Belize";
                case "JM":
                    return "Jamaica";
                case "TT":
                    return "Trinidad and Tobago";
                case "GY":
                    return "Guyana";
                case "SR":
                    return "Suriname";
                case "GF":
                    return "French Guiana";
                case "FK":
                    return "Falkland Islands";
                case "GS":
                    return "South Georgia and the South Sandwich Islands";
                case "BV":
                    return "Bouvet Island";
                case "TF":
                    return "French Southern Territories";
                case "AQ":
                    return "Antarctica";
                case "IO":
                    return "British Indian Ocean Territory";
                case "SH":
                    return "Saint Helena, Ascension and Tristan da Cunha";
                case "AC":
                    return "Ascension Island";
                case "TA":
                    return "Tristan da Cunha";
                case "PN":
                    return "Pitcairn";
                case "TC":
                    return "Turks and Caicos Islands";
                case "VG":
                    return "British Virgin Islands";
                case "AI":
                    return "Anguilla";
                case "MS":
                    return "Montserrat";
                case "BL":
                    return "Saint Barthélemy";
                case "MF":
                    return "Saint Martin";
                case "GP":
                    return "Guadeloupe";
                case "MQ":
                    return "Martinique";
                case "RE":
                    return "Réunion";
                case "YT":
                    return "Mayotte";
                case "NC":
                    return "New Caledonia";
                case "PF":
                    return "French Polynesia";
                case "WF":
                    return "Wallis and Futuna";
                case "PM":
                    return "Saint Pierre and Miquelon";
                case "GL":
                    return "Greenland";
                case "FO":
                    return "Faroe Islands";
                case "AX":
                    return "Åland Islands";
                case "SJ":
                    return "Svalbard and Jan Mayen";
                case "HM":
                    return "Heard Island and McDonald Islands";
                case "CC":
                    return "Cocos (Keeling) Islands";
                case "CX":
                    return "Christmas Island";
                case "NF":
                    return "Norfolk Island";
                case "CK":
                    return "Cook Islands";
                case "NU":
                    return "Niue";
                case "TK":
                    return "Tokelau";
                case "PW":
                    return "Palau";
                case "MH":
                    return "Marshall Islands";
                case "FM":
                    return "Micronesia";
                case "NR":
                    return "Nauru";
                case "TV":
                    return "Tuvalu";
                case "KI":
                    return "Kiribati";
                case "WS":
                    return "Samoa";
                case "TO":
                    return "Tonga";
                case "FJ":
                    return "Fiji";
                case "VU":
                    return "Vanuatu";
                case "SB":
                    return "Solomon Islands";
                case "PG":
                    return "Papua New Guinea";
                case "NC":
                    return "New Caledonia";
                case "PF":
                    return "French Polynesia";
                case "WF":
                    return "Wallis and Futuna";
                case "PM":
                    return "Saint Pierre and Miquelon";
                case "GL":
                    return "Greenland";
                case "FO":
                    return "Faroe Islands";
                case "AX":
                    return "Åland Islands";
                case "SJ":
                    return "Svalbard and Jan Mayen";
                case "HM":
                    return "Heard Island and McDonald Islands";
                case "CC":
                    return "Cocos (Keeling) Islands";
                case "CX":
                    return "Christmas Island";
                case "NF":
                    return "Norfolk Island";
                case "CK":
                    return "Cook Islands";
                case "NU":
                    return "Niue";
                case "TK":
                    return "Tokelau";
                case "PW":
                    return "Palau";
                case "MH":
                    return "Marshall Islands";
                case "FM":
                    return "Micronesia";
                case "NR":
                    return "Nauru";
                case "TV":
                    return "Tuvalu";
                case "KI":
                    return "Kiribati";
                case "WS":
                    return "Samoa";
                case "TO":
                    return "Tonga";
                case "FJ":
                    return "Fiji";
                case "VU":
                    return "Vanuatu";
                case "SB":
                    return "Solomon Islands";
                case "PG":
                    return "Papua New Guinea";
                default:
                    return countryCode;
            }
        }
    }
}