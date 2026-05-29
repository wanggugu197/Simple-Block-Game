package com.simple_block_game;

import net.neoforged.neoforge.common.ModConfigSpec;

public class SimpleBlockGameConfig {

    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    public static final Game2048RewardConfig GAME_2048_CONFIG;
    public static final MinesweeperRewardConfig MINESWEEPER_CONFIG;
    public static final MemoryKeyRewardConfig MEMORY_KEY_CONFIG;
    public static final TenDropRewardConfig TEN_DROP_CONFIG;

    static {
        GAME_2048_CONFIG = new Game2048RewardConfig();
        MINESWEEPER_CONFIG = new MinesweeperRewardConfig();
        MEMORY_KEY_CONFIG = new MemoryKeyRewardConfig();
        TEN_DROP_CONFIG = new TenDropRewardConfig();
        initConfig();
        SPEC = BUILDER.build();
    }

    public static ModConfigSpec.BooleanValue enable2048Game;
    public static ModConfigSpec.BooleanValue enableMinesweeperGame;
    public static ModConfigSpec.BooleanValue enableMemoryKeyGame;
    public static ModConfigSpec.BooleanValue enableTenDropGame;

    private static void initConfig() {
        BUILDER.push("Simple Block Game Settings");

        enable2048Game = BUILDER.comment("Enable 2048 game")
                .define("enable_2048_game", true);
        enableMinesweeperGame = BUILDER.comment("Enable Minesweeper game")
                .define("enable_minesweeper_game", true);
        enableMemoryKeyGame = BUILDER.comment("Enable Memory Key game")
                .define("enable_memory_key_game", true);
        enableTenDropGame = BUILDER.comment("Enable Ten Drop game")
                .define("enable_ten_drop_game", true);

        BUILDER.pop();

        GAME_2048_CONFIG.init(BUILDER);
        MINESWEEPER_CONFIG.init(BUILDER);
        MEMORY_KEY_CONFIG.init(BUILDER);
        TEN_DROP_CONFIG.init(BUILDER);
    }

    public static class Game2048RewardConfig {

        public ModConfigSpec.IntValue scoreThreshold1;
        public ModConfigSpec.ConfigValue<String> scoreReward1;
        public ModConfigSpec.IntValue scoreThreshold2;
        public ModConfigSpec.ConfigValue<String> scoreReward2;
        public ModConfigSpec.IntValue scoreThreshold3;
        public ModConfigSpec.ConfigValue<String> scoreReward3;
        public ModConfigSpec.IntValue scoreThreshold4;
        public ModConfigSpec.ConfigValue<String> scoreReward4;
        public ModConfigSpec.IntValue scoreThreshold5;
        public ModConfigSpec.ConfigValue<String> scoreReward5;
        public ModConfigSpec.IntValue scoreThreshold6;
        public ModConfigSpec.ConfigValue<String> scoreReward6;
        public ModConfigSpec.IntValue scoreThreshold7;
        public ModConfigSpec.ConfigValue<String> scoreReward7;
        public ModConfigSpec.IntValue scoreThreshold8;
        public ModConfigSpec.ConfigValue<String> scoreReward8;
        public ModConfigSpec.IntValue scoreThreshold9;
        public ModConfigSpec.ConfigValue<String> scoreReward9;
        public ModConfigSpec.IntValue scoreThreshold10;
        public ModConfigSpec.ConfigValue<String> scoreReward10;
        public ModConfigSpec.IntValue scoreThreshold11;
        public ModConfigSpec.ConfigValue<String> scoreReward11;
        public ModConfigSpec.IntValue scoreThreshold12;
        public ModConfigSpec.ConfigValue<String> scoreReward12;
        public ModConfigSpec.IntValue scoreThreshold13;
        public ModConfigSpec.ConfigValue<String> scoreReward13;
        public ModConfigSpec.IntValue scoreThreshold14;
        public ModConfigSpec.ConfigValue<String> scoreReward14;

        public ModConfigSpec.ConfigValue<String> maxReward1024;
        public ModConfigSpec.ConfigValue<String> maxReward2048;
        public ModConfigSpec.ConfigValue<String> maxReward4096;
        public ModConfigSpec.ConfigValue<String> maxReward8192;
        public ModConfigSpec.ConfigValue<String> maxReward16384;
        public ModConfigSpec.ConfigValue<String> maxReward32768;
        public ModConfigSpec.ConfigValue<String> maxReward65536;

        public void init(ModConfigSpec.Builder builder) {
            builder.push("2048 Game Score");

            scoreThreshold1 = builder.comment("Score threshold for reward tier 1")
                    .defineInRange("score_threshold_1", 500, 0, Integer.MAX_VALUE);
            scoreReward1 = builder.comment("Loot table for score reward tier 1")
                    .define("score_reward_1", "minecraft:chests/igloo_chest");

            scoreThreshold2 = builder.comment("Score threshold for reward tier 2")
                    .defineInRange("score_threshold_2", 1000, 0, Integer.MAX_VALUE);
            scoreReward2 = builder.comment("Loot table for score reward tier 2")
                    .define("score_reward_2", "minecraft:chests/shipwreck_treasure");

            scoreThreshold3 = builder.comment("Score threshold for reward tier 3")
                    .defineInRange("score_threshold_3", 1500, 0, Integer.MAX_VALUE);
            scoreReward3 = builder.comment("Loot table for score reward tier 3")
                    .define("score_reward_3", "minecraft:chests/underwater_ruin_big");

            scoreThreshold4 = builder.comment("Score threshold for reward tier 4")
                    .defineInRange("score_threshold_4", 2000, 0, Integer.MAX_VALUE);
            scoreReward4 = builder.comment("Loot table for score reward tier 4")
                    .define("score_reward_4", "minecraft:chests/desert_pyramid");

            scoreThreshold5 = builder.comment("Score threshold for reward tier 5")
                    .defineInRange("score_threshold_5", 3000, 0, Integer.MAX_VALUE);
            scoreReward5 = builder.comment("Loot table for score reward tier 5")
                    .define("score_reward_5", "minecraft:chests/abandoned_mineshaft");

            scoreThreshold6 = builder.comment("Score threshold for reward tier 6")
                    .defineInRange("score_threshold_6", 4000, 0, Integer.MAX_VALUE);
            scoreReward6 = builder.comment("Loot table for score reward tier 6")
                    .define("score_reward_6", "minecraft:chests/jungle_temple");

            scoreThreshold7 = builder.comment("Score threshold for reward tier 7")
                    .defineInRange("score_threshold_7", 5000, 0, Integer.MAX_VALUE);
            scoreReward7 = builder.comment("Loot table for score reward tier 7")
                    .define("score_reward_7", "minecraft:chests/pillager_outpost");

            scoreThreshold8 = builder.comment("Score threshold for reward tier 8")
                    .defineInRange("score_threshold_8", 7500, 0, Integer.MAX_VALUE);
            scoreReward8 = builder.comment("Loot table for score reward tier 8")
                    .define("score_reward_8", "minecraft:chests/stronghold_library");

            scoreThreshold9 = builder.comment("Score threshold for reward tier 9")
                    .defineInRange("score_threshold_9", 10000, 0, Integer.MAX_VALUE);
            scoreReward9 = builder.comment("Loot table for score reward tier 9")
                    .define("score_reward_9", "minecraft:chests/bastion_other");

            scoreThreshold10 = builder.comment("Score threshold for reward tier 10")
                    .defineInRange("score_threshold_10", 15000, 0, Integer.MAX_VALUE);
            scoreReward10 = builder.comment("Loot table for score reward tier 10")
                    .define("score_reward_10", "minecraft:chests/bastion_treasure");

            scoreThreshold11 = builder.comment("Score threshold for reward tier 11")
                    .defineInRange("score_threshold_11", 20000, 0, Integer.MAX_VALUE);
            scoreReward11 = builder.comment("Loot table for score reward tier 11")
                    .define("score_reward_11", "minecraft:chests/woodland_mansion");

            scoreThreshold12 = builder.comment("Score threshold for reward tier 12")
                    .defineInRange("score_threshold_12", 25000, 0, Integer.MAX_VALUE);
            scoreReward12 = builder.comment("Loot table for score reward tier 12")
                    .define("score_reward_12", "minecraft:chests/ancient_city_ice_box");

            scoreThreshold13 = builder.comment("Score threshold for reward tier 13")
                    .defineInRange("score_threshold_13", 30000, 0, Integer.MAX_VALUE);
            scoreReward13 = builder.comment("Loot table for score reward tier 13")
                    .define("score_reward_13", "minecraft:chests/ancient_city");

            scoreThreshold14 = builder.comment("Score threshold for reward tier 14")
                    .defineInRange("score_threshold_14", 50000, 0, Integer.MAX_VALUE);
            scoreReward14 = builder.comment("Loot table for score reward tier 14")
                    .define("score_reward_14", "minecraft:chests/end_city_treasure");

            builder.pop();

            builder.push("2048 Game Max Number Rewards");

            maxReward1024 = builder.comment("Loot table for max number 1024")
                    .define("max_reward_1024", "minecraft:chests/simple_dungeon");
            maxReward2048 = builder.comment("Loot table for max number 2048")
                    .define("max_reward_2048", "minecraft:chests/village/village_weaponsmith");
            maxReward4096 = builder.comment("Loot table for max number 4096")
                    .define("max_reward_4096", "minecraft:chests/woodland_mansion");
            maxReward8192 = builder.comment("Loot table for max number 8192")
                    .define("max_reward_8192", "minecraft:chests/ancient_city");
            maxReward16384 = builder.comment("Loot table for max number 16384")
                    .define("max_reward_16384", "minecraft:chests/bastion_treasure");
            maxReward32768 = builder.comment("Loot table for max number 32768")
                    .define("max_reward_32768", "minecraft:chests/buried_treasure");
            maxReward65536 = builder.comment("Loot table for max number 65536")
                    .define("max_reward_65536", "minecraft:chests/end_city_treasure");

            builder.pop();
        }
    }

    public static class MinesweeperRewardConfig {

        public ModConfigSpec.DoubleValue threshold1;
        public ModConfigSpec.ConfigValue<String> reward1;
        public ModConfigSpec.DoubleValue threshold2;
        public ModConfigSpec.ConfigValue<String> reward2;
        public ModConfigSpec.DoubleValue threshold3;
        public ModConfigSpec.ConfigValue<String> reward3;
        public ModConfigSpec.DoubleValue threshold4;
        public ModConfigSpec.ConfigValue<String> reward4;
        public ModConfigSpec.DoubleValue threshold5;
        public ModConfigSpec.ConfigValue<String> reward5;
        public ModConfigSpec.DoubleValue threshold6;
        public ModConfigSpec.ConfigValue<String> reward6;
        public ModConfigSpec.DoubleValue threshold7;
        public ModConfigSpec.ConfigValue<String> reward7;
        public ModConfigSpec.DoubleValue threshold8;
        public ModConfigSpec.ConfigValue<String> reward8;
        public ModConfigSpec.DoubleValue threshold9;
        public ModConfigSpec.ConfigValue<String> reward9;
        public ModConfigSpec.DoubleValue threshold10;
        public ModConfigSpec.ConfigValue<String> reward10;

        public void init(ModConfigSpec.Builder builder) {
            builder.push("Minesweeper Game");

            threshold1 = builder.comment("Threshold for reward tier 1")
                    .defineInRange("threshold_1", 0.08, 0.0, 1.0);
            reward1 = builder.comment("Loot table for reward tier 1")
                    .define("reward_1", "minecraft:chests/desert_pyramid");

            threshold2 = builder.comment("Threshold for reward tier 2")
                    .defineInRange("threshold_2", 0.15, 0.0, 1.0);
            reward2 = builder.comment("Loot table for reward tier 2")
                    .define("reward_2", "minecraft:chests/abandoned_mineshaft");

            threshold3 = builder.comment("Threshold for reward tier 3")
                    .defineInRange("threshold_3", 0.22, 0.0, 1.0);
            reward3 = builder.comment("Loot table for reward tier 3")
                    .define("reward_3", "minecraft:chests/pillager_outpost");

            threshold4 = builder.comment("Threshold for reward tier 4")
                    .defineInRange("threshold_4", 0.29, 0.0, 1.0);
            reward4 = builder.comment("Loot table for reward tier 4")
                    .define("reward_4", "minecraft:chests/woodland_mansion");

            threshold5 = builder.comment("Threshold for reward tier 5")
                    .defineInRange("threshold_5", 0.35, 0.0, 1.0);
            reward5 = builder.comment("Loot table for reward tier 5")
                    .define("reward_5", "minecraft:chests/bastion_treasure");

            threshold6 = builder.comment("Threshold for reward tier 6")
                    .defineInRange("threshold_6", 0.40, 0.0, 1.0);
            reward6 = builder.comment("Loot table for reward tier 6")
                    .define("reward_6", "minecraft:chests/end_city_treasure");

            threshold7 = builder.comment("Threshold for reward tier 7")
                    .defineInRange("threshold_7", 0.50, 0.0, 1.0);
            reward7 = builder.comment("Loot table for reward tier 7")
                    .define("reward_7", "minecraft:chests/end_city_treasure");

            threshold8 = builder.comment("Threshold for reward tier 8")
                    .defineInRange("threshold_8", 0.60, 0.0, 1.0);
            reward8 = builder.comment("Loot table for reward tier 8")
                    .define("reward_8", "minecraft:chests/end_city_treasure");

            threshold9 = builder.comment("Threshold for reward tier 9")
                    .defineInRange("threshold_9", 0.70, 0.0, 1.0);
            reward9 = builder.comment("Loot table for reward tier 9")
                    .define("reward_9", "minecraft:chests/end_city_treasure");

            threshold10 = builder.comment("Threshold for reward tier 10")
                    .defineInRange("threshold_10", 0.80, 0.0, 1.0);
            reward10 = builder.comment("Loot table for reward tier 10")
                    .define("reward_10", "minecraft:chests/end_city_treasure");

            builder.pop();
        }
    }

    public static class MemoryKeyRewardConfig {

        public ModConfigSpec.ConfigValue<String> level1Reward;
        public ModConfigSpec.ConfigValue<String> level2Reward;
        public ModConfigSpec.ConfigValue<String> level3Reward;
        public ModConfigSpec.ConfigValue<String> level4Reward;
        public ModConfigSpec.ConfigValue<String> level5Reward;
        public ModConfigSpec.ConfigValue<String> level6Reward;
        public ModConfigSpec.ConfigValue<String> allSuccessReward;

        public void init(ModConfigSpec.Builder builder) {
            builder.push("Memory Key Game");

            level1Reward = builder.comment("Loot table for level 1 reward")
                    .define("level_1_reward", "minecraft:chests/simple_dungeon");
            level2Reward = builder.comment("Loot table for level 2 reward")
                    .define("level_2_reward", "minecraft:chests/igloo_chest");
            level3Reward = builder.comment("Loot table for level 3 reward")
                    .define("level_3_reward", "minecraft:chests/shipwreck_supply");
            level4Reward = builder.comment("Loot table for level 4 reward")
                    .define("level_4_reward", "minecraft:chests/abandoned_mineshaft");
            level5Reward = builder.comment("Loot table for level 5 reward")
                    .define("level_5_reward", "minecraft:chests/pillager_outpost");
            level6Reward = builder.comment("Loot table for level 6 reward")
                    .define("level_6_reward", "minecraft:chests/bastion_treasure");
            allSuccessReward = builder.comment("Loot table for all success reward")
                    .define("all_success_reward", "minecraft:chests/end_city_treasure");

            builder.pop();
        }
    }

    public static class TenDropRewardConfig {

        public ModConfigSpec.ConfigValue<String> level1Reward;
        public ModConfigSpec.ConfigValue<String> level2Reward;
        public ModConfigSpec.ConfigValue<String> level3Reward;
        public ModConfigSpec.ConfigValue<String> level4Reward;
        public ModConfigSpec.ConfigValue<String> level5Reward;
        public ModConfigSpec.ConfigValue<String> level6Reward;
        public ModConfigSpec.ConfigValue<String> level7Reward;
        public ModConfigSpec.ConfigValue<String> level8Reward;
        public ModConfigSpec.ConfigValue<String> level9Reward;
        public ModConfigSpec.ConfigValue<String> level10Reward;

        public void init(ModConfigSpec.Builder builder) {
            builder.push("Ten Drop Game");

            level1Reward = builder.comment("Loot table for level 1 reward")
                    .define("ten_drop_level_1_reward", "minecraft:chests/simple_dungeon");
            level2Reward = builder.comment("Loot table for level 2 reward")
                    .define("ten_drop_level_2_reward", "minecraft:chests/abandoned_mineshaft");
            level3Reward = builder.comment("Loot table for level 3 reward")
                    .define("ten_drop_level_3_reward", "minecraft:chests/pillager_outpost");
            level4Reward = builder.comment("Loot table for level 4 reward")
                    .define("ten_drop_level_4_reward", "minecraft:chests/pillager_outpost");
            level5Reward = builder.comment("Loot table for level 5 reward")
                    .define("ten_drop_level_5_reward", "minecraft:chests/woodland_mansion");
            level6Reward = builder.comment("Loot table for level 6 reward")
                    .define("ten_drop_level_6_reward", "minecraft:chests/woodland_mansion");
            level7Reward = builder.comment("Loot table for level 7 reward")
                    .define("ten_drop_level_7_reward", "minecraft:chests/bastion_treasure");
            level8Reward = builder.comment("Loot table for level 8 reward")
                    .define("ten_drop_level_8_reward", "minecraft:chests/bastion_treasure");
            level9Reward = builder.comment("Loot table for level 9 reward")
                    .define("ten_drop_level_9_reward", "minecraft:chests/bastion_treasure");
            level10Reward = builder.comment("Loot table for level 10 reward")
                    .define("ten_drop_level_10_reward", "minecraft:chests/end_city_treasure");

            builder.pop();
        }
    }
}
