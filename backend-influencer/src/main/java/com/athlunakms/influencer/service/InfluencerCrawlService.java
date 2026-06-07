package com.athlunakms.influencer.service;

import com.athlunakms.influencer.entity.Influencer;
import com.athlunakms.influencer.entity.InfluencerCrawlTask;
import com.athlunakms.influencer.entity.SocialMedia;
import com.athlunakms.influencer.repository.InfluencerCrawlTaskRepository;
import com.athlunakms.influencer.repository.InfluencerRepository;
import com.athlunakms.influencer.repository.SocialMediaRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InfluencerCrawlService {
    private static final Logger log = LoggerFactory.getLogger(InfluencerCrawlService.class);

    @Autowired
    private InfluencerCrawlTaskRepository crawlTaskRepository;

    @Autowired
    private InfluencerRepository influencerRepository;

    @Autowired
    private SocialMediaRepository socialMediaRepository;

    @Autowired
    private InfluencerLogService logService;

    public List<InfluencerCrawlTask> getAllTasks() {
        return crawlTaskRepository.findAll();
    }

    public Optional<InfluencerCrawlTask> getTaskById(Long id) {
        return crawlTaskRepository.findById(id);
    }

    public InfluencerCrawlTask createTask(InfluencerCrawlTask task) {
        task.setStatus("PENDING");
        task.setScrapedCount(0);
        return crawlTaskRepository.save(task);
    }

    public void deleteTask(Long id) {
        crawlTaskRepository.deleteById(id);
    }

    @Async
    @Transactional
    public void startCrawlTask(Long taskId) {
        Optional<InfluencerCrawlTask> taskOpt = crawlTaskRepository.findById(taskId);
        if (!taskOpt.isPresent()) {
            log.error("Crawl task not found: {}", taskId);
            return;
        }

        InfluencerCrawlTask task = taskOpt.get();
        task.setStatus("RUNNING");
        crawlTaskRepository.save(task);

        try {
            // Simulate crawling latency
            Thread.sleep(5000);

            // Generate simulated crawled influencers based on task settings
            List<SimulatedInfluencer> scrapedData = generateSimulatedData(task);

            int successCount = 0;
            for (SimulatedInfluencer item : scrapedData) {
                // Upsert logic
                Optional<Influencer> existingOpt = influencerRepository.findFirstByDefaultPlatformAndDefaultHandle("instagram", item.username);
                if (existingOpt.isPresent()) {
                    // Update existing
                    Influencer existing = existingOpt.get();
                    existing.setTotalFans(item.followers);
                    existing.setUpdatedAt(LocalDateTime.now());
                    
                    // Update brand if autoBrandMentions is true and a brand is found
                    if (Boolean.TRUE.equals(task.getAutoBrandMentions()) && item.detectedBrand != null) {
                        existing.setBrand(item.detectedBrand);
                    }
                    influencerRepository.save(existing);

                    // Update corresponding social media
                    Optional<SocialMedia> smOpt = socialMediaRepository.findByPlatformAndHandle("instagram", item.username);
                    if (smOpt.isPresent()) {
                        SocialMedia sm = smOpt.get();
                        sm.setFollowerCount(item.followers);
                        socialMediaRepository.save(sm);
                    }

                    logService.logChange(existing.getId(), "爬取更新", "粉丝数: " + existing.getTotalFans(), "粉丝数: " + item.followers, "SYSTEM", "爬虫任务增量更新");
                } else {
                    // Create new
                    Influencer newInfluencer = new Influencer();
                    newInfluencer.setRealName(item.displayName);
                    newInfluencer.setNickName(item.username);
                    newInfluencer.setPlatform("instagram");
                    newInfluencer.setDefaultPlatform("instagram");
                    newInfluencer.setDefaultHandle(item.username);
                    newInfluencer.setDefaultUrl("https://www.instagram.com/" + item.username + "/");
                    newInfluencer.setTotalFans(item.followers);
                    newInfluencer.setSource("CRAWLER");
                    newInfluencer.setSourceValue(task.getTaskName());
                    newInfluencer.setOrigin(Influencer.Origin.MANUAL);
                    newInfluencer.setStage(Influencer.Stage.POOL);
                    newInfluencer.setStatus(Influencer.Status.PENDING);
                    newInfluencer.setDescription(item.bio);

                    // Set brand: prioritize detected brand, fallback to task target brand
                    if (Boolean.TRUE.equals(task.getAutoBrandMentions()) && item.detectedBrand != null) {
                        newInfluencer.setBrand(item.detectedBrand);
                    } else if (task.getTargetBrand() != null && !task.getTargetBrand().isEmpty()) {
                        newInfluencer.setBrand(task.getTargetBrand());
                    }

                    Influencer saved = influencerRepository.save(newInfluencer);

                    // Save Social Media
                    SocialMedia sm = new SocialMedia();
                    sm.setInfluencerId(saved.getId());
                    sm.setPlatform("instagram");
                    sm.setHandle(item.username);
                    sm.setUrl("https://www.instagram.com/" + item.username + "/");
                    sm.setFollowerCount(item.followers);
                    sm.setIsDefault(true);
                    SocialMedia savedSm = socialMediaRepository.save(sm);

                    // Update default social ID
                    saved.setDefaultSocialId(savedSm.getId());
                    influencerRepository.save(saved);

                    logService.logChange(saved.getId(), "爬取入库", "", "新红人入库", "SYSTEM", "通过爬虫任务新增入库");
                }
                successCount++;
            }

            task.setStatus("COMPLETED");
            task.setScrapedCount(successCount);
            crawlTaskRepository.save(task);

        } catch (Exception e) {
            log.error("Failed to run crawl task: {}", taskId, e);
            task.setStatus("FAILED");
            task.setErrorMessage(e.getMessage());
            crawlTaskRepository.save(task);
        }
    }

    private List<SimulatedInfluencer> generateSimulatedData(InfluencerCrawlTask task) {
        List<SimulatedInfluencer> result = new ArrayList<>();
        Random random = new Random();
        
        long min = task.getMinFollowers() != null ? task.getMinFollowers() : 5000L;
        long max = task.getMaxFollowers() != null ? task.getMaxFollowers() : 1000000L;
        if (min >= max) {
            max = min + 500000L;
        }

        // Generate names based on query
        String cleanQuery = task.getSearchQuery().replace("#", "").replace("@", "").toLowerCase();
        
        String[] prefixes = {"beauty_", "fit_", "glam_", "makeup_", "skin_", "daily_", "style_", "fashion_"};
        String[] suffixes = {"_blogger", "_lifestyle", "_official", "_pro", "_review", "_vibe", "_queen", "_guru"};
        String[] brands = {"EsteeLauder", "Sephora", "Loreal", "Clinique", "Lancome", "DiorBeauty", "MacCosmetics"};

        int count = 5 + random.nextInt(6); // 5 to 10 influencers
        for (int i = 0; i < count; i++) {
            String prefix = prefixes[random.nextInt(prefixes.length)];
            String suffix = suffixes[random.nextInt(suffixes.length)];
            String username = prefix + cleanQuery + suffix + "_" + (random.nextInt(90) + 10);
            String displayName = username.replace("_", " ");
            
            long followers = min + (Math.abs(random.nextLong()) % (max - min));
            
            // Randomly tag a brand in 70% of the cases
            String detectedBrand = null;
            if (random.nextDouble() < 0.7) {
                detectedBrand = brands[random.nextInt(brands.length)];
            }

            String bio = "Beauty & Lifestyle Creator | Collaboration DM open | Loves " + 
                         (detectedBrand != null ? "@" + detectedBrand : "skincare hacks");

            result.add(new SimulatedInfluencer(username, displayName, followers, bio, detectedBrand));
        }

        return result;
    }

    private static class SimulatedInfluencer {
        String username;
        String displayName;
        long followers;
        String bio;
        String detectedBrand;

        public SimulatedInfluencer(String username, String displayName, long followers, String bio, String detectedBrand) {
            this.username = username;
            this.displayName = displayName;
            this.followers = followers;
            this.bio = bio;
            this.detectedBrand = detectedBrand;
        }
    }
}
