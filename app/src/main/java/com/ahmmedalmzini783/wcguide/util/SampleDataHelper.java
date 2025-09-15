package com.ahmmedalmzini783.wcguide.util;

import com.ahmmedalmzini783.wcguide.data.model.Banner;
import com.ahmmedalmzini783.wcguide.data.remote.FirebaseDataSource;

public class SampleDataHelper {

    public static void addSampleBanners() {
        FirebaseDataSource dataSource = new FirebaseDataSource();

        // Banner 1: World Cup Tickets
        Banner banner1 = new Banner();
        banner1.setId("banner_001");
        banner1.setTitle("احجز تذاكر كأس العالم 2026");
        banner1.setDescription("لا تفوت فرصة حضور أهم حدث رياضي في العالم! احجز تذاكرك الآن لكأس العالم 2026 في الولايات المتحدة وكندا والمكسيك. تذاكر محدودة ومتنوعة لجميع المباريات.");
        banner1.setImageUrl("https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=800&h=400&fit=crop");
        banner1.setDeeplink("app://tickets/world_cup_2026");

        dataSource.addBanner(banner1, new FirebaseDataSource.OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean success, String message) {
                System.out.println("Banner 1: " + message);
            }
        });

        // Banner 2: Fan Zone Events
        Banner banner2 = new Banner();
        banner2.setId("banner_002");
        banner2.setTitle("مناطق المشجعين - أحداث مثيرة");
        banner2.setDescription("استمتع بأجواء كأس العالم في مناطق المشجعين المخصصة! فعاليات ترفيهية، عروض موسيقية، وأنشطة تفاعلية في جميع المدن المضيفة. مجاناً للجميع!");
        banner2.setImageUrl("https://images.unsplash.com/photo-1574869711983-2bb7d1b6659c?w=800&h=400&fit=crop");
        banner2.setDeeplink("app://events/fan_zones");

        dataSource.addBanner(banner2, new FirebaseDataSource.OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean success, String message) {
                System.out.println("Banner 2: " + message);
            }
        });

        // Banner 3: Travel Packages
        Banner banner3 = new Banner();
        banner3.setId("banner_003");
        banner3.setTitle("باقات السفر الحصرية");
        banner3.setDescription("اكتشف أفضل عروض السفر لكأس العالم 2026! باقات شاملة تتضمن الإقامة، التذاكر، والنقل. خصومات خاصة للحجز المبكر والعائلات.");
        banner3.setImageUrl("https://images.unsplash.com/photo-1436491865332-7a61a109cc05?w=800&h=400&fit=crop");
        banner3.setDeeplink("app://travel/packages");

        dataSource.addBanner(banner3, new FirebaseDataSource.OnCompleteListener<Void>() {
            @Override
            public void onComplete(boolean success, String message) {
                System.out.println("Banner 3: " + message);
            }
        });
    }
}
